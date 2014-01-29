package com.lvl6.mobsters.services.taskstageforuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskStageForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.QuestMonsterItemRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.TaskStageMonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageHistory;
import com.lvl6.mobsters.po.staticdata.QuestMonsterItem;
import com.lvl6.mobsters.po.staticdata.TaskStage;
import com.lvl6.mobsters.po.staticdata.TaskStageMonster;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.services.taskstagehistory.TaskStageHistoryService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class TaskStageForUserServiceImpl implements TaskStageForUserService {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_TASK_STAGE_FOR_USER;
	
	
	@Autowired
	protected TaskStageForUserEntityManager taskStageForUserEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected TaskStageHistoryService taskStageHistoryService;
	
	@Autowired
	protected TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtils;
	
	@Autowired
	protected QuestMonsterItemRetrieveUtils questMonsterItemRetrieveUtils;
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	//returns map(stageNum ---> List<TaskStageForUser>). In English, the map will  
	//contain a stageNum tied to a list of monsters the user faces in this stage;
	//also returns
	//expGained (contains the sum of the exp across all stages),
	//and same with cashGained,
	//(given that these taskStages are for one task, stageNum and stageId are one-to-one)
	@Override
	public Map<Integer, List<TaskStageForUser>> generateUserTaskStagesFromTaskStages(
			UUID userTaskId, Map<Integer, TaskStage> tsIdToTs, List<Integer> expGained,
			List<Integer> cashGained) {
		//return value
		int expSum = 0;
		int cashSum = 0;
		Map<Integer, List<TaskStageForUser>> stageNumToUserTaskStages = 
				new HashMap<Integer, List<TaskStageForUser>>();
		
		//for each stage, calculate the monster(s) the user will face and
		//reward(s) that might be given if the user wins
		for (int tsId : tsIdToTs.keySet()) {
			TaskStage ts = tsIdToTs.get(tsId);
			int stageNum = ts.getStageNum();
			
			//calculate the monster(s) the user will face for this stage
			//at the moment only one monster will be generated
			List<TaskStageMonster> spawnedTaskStageMonsters = generateSpawnedMonsters(
					tsId, 1);
			
			//create the TaskStageForUsers from the monsters.
			List<Integer> stageExpReward = new ArrayList<Integer>();
			List<Integer> stageCashReward = new ArrayList<Integer>();
			List<TaskStageForUser> curTaskStages = createTaskStagesFromMonsters(
					userTaskId, stageNum, spawnedTaskStageMonsters, stageExpReward,
					stageCashReward);
			
			expSum += stageExpReward.get(0);
			cashSum += stageCashReward.get(0);
			stageNumToUserTaskStages.put(stageNum, curTaskStages);
		}
		expGained.add(expSum);
		cashGained.add(cashSum);
		return stageNumToUserTaskStages;
	}
	
	@Override
	public List<TaskStageMonster> generateSpawnedMonsters(int taskStageId, int quantity) {
		//return value
		List<TaskStageMonster> selectedTsm = new ArrayList<TaskStageMonster>();

		Random rand = new Random();
		List<TaskStageMonster> possibleMonsters = getTaskStageMonsterRetrieveUtils()
				.getTaskStagesForTaskStageId(taskStageId);
		int size = possibleMonsters.size();
		int quantityWanted = quantity;
		//sum up chance to appear, and need to normalize all the probabilities
		float sumOfProbabilities = sumProbabilities(possibleMonsters);

		for (int i = 0; i < size; i++) {
			if (0 == quantityWanted) {
				//since we selected all the monsters we wanted, exit
				break;
			}
			if (quantityWanted < 0) {
				log.error("selecting some amount of monsters out of n possible monsters failed.");
				break;
			}
			//since we want k more monsters and we have k left, take them all
			int numLeft = size - i;
			if (quantityWanted == numLeft) {
				List<TaskStageMonster> leftOvers = possibleMonsters.subList(i, size);
				selectedTsm.addAll(leftOvers);
				break;
			}

			//seeing if current monster is selected
			TaskStageMonster tsmSoFar = possibleMonsters.get(i);

			if (monsterSelected(rand, tsmSoFar, sumOfProbabilities)) {
				selectedTsm.add(tsmSoFar);
				quantityWanted -= 1;
			}
			//selecting without replacement so this guy's probability needs to go
			sumOfProbabilities -= tsmSoFar.getChanceToAppear();
		}
		return selectedTsm;
	}

	@Override
	public float sumProbabilities(List<TaskStageMonster> taskStageMonsters) {
		float sumProbabilities = 0.0f;

		for (TaskStageMonster tsm : taskStageMonsters) {
			sumProbabilities += tsm.getChanceToAppear();
		}

		return sumProbabilities;
	}
	
	private boolean monsterSelected(Random rand, TaskStageMonster tsm, float sumOfProbabilities) {
		float chanceToAppear = tsm.getChanceToAppear();
  		float randFloat = rand.nextFloat();
  		
  		float normalizedProb = chanceToAppear/sumOfProbabilities;
  		if (normalizedProb > randFloat) {
  			//random number generated falls within this monster's probability window
  			return true;
  		}
  		return false;
	}
	
	//two additional return values stored in lists: stageExpReward and stageCashReward
	private List<TaskStageForUser> createTaskStagesFromMonsters(UUID userTaskId,
			int stageNum, List<TaskStageMonster> spawnedTaskStageMonsters,
			List<Integer> stageExpReward, List<Integer> stageCashReward) {
		//return values
		List<TaskStageForUser> tsfuList = new ArrayList<TaskStageForUser>();
		int expReward = 0;
		int cashReward = 0;
		
		//create as many TaskStageForUsers as there are TaskStageMonsters and
		//aggregate the exp and cash
		for (TaskStageMonster tsm : spawnedTaskStageMonsters) {
			TaskStageForUser tsfu = createTaskStageFromMonster(userTaskId, stageNum, tsm);
			expReward += tsfu.getExpGained();
			cashReward += tsfu.getCashGained();
			tsfuList.add(tsfu);
		}
		
		stageExpReward.add(expReward);
		stageCashReward.add(cashReward);
		
		return tsfuList;
	}
	
	private TaskStageForUser createTaskStageFromMonster(UUID userTaskId, int stageNum,
			TaskStageMonster tsm) {
		TaskStageForUser tsfu = new TaskStageForUser();
		tsfu.setUserTaskId(userTaskId);
		tsfu.setStageNum(stageNum);
		
		int taskStageMonsterId = tsm.getId();
		tsfu.setTaskStageMonsterId(taskStageMonsterId);
		
		String monsterType = tsm.getMonsterType();
		tsfu.setMonsterType(monsterType);
		
		int expGained = tsm.getExpReward();
		tsfu.setExpGained(expGained);
		
		int cashGained = tsm.getCashDrop();
		tsfu.setCashGained(cashGained);
		
		boolean pieceDropped = tsm.didPuzzlePieceDrop();
		tsfu.setMonsterPieceDropped(pieceDropped);

		//not recorded in db
		int taskStageId = tsm.getStageId();
		tsfu.setTaskStageId(taskStageId);
		int monsterId = tsm.getMonsterId();
		tsfu.setMonsterId(monsterId);
		int monsterLvl = tsm.getLevel();
		tsfu.setMonsterLvl(monsterLvl);
		
		return tsfu;
	}
	
	
	//item refers to special quest items that only tied to a special monster which
	//are only tied to a certain quest. Read comments in QuestMonsterItem.java
	//one TaskStageForUser represents one monster in the current stage
	@Override
	public void generateItemDrops(List<Integer> questIds, 
			Map<Integer, List<TaskStageForUser>> stageNumToStages) {
		
		//need to loop through all stages to compute whether an item dropped or not
		for (Integer stageNum : stageNumToStages.keySet()) {
			
			List<TaskStageForUser> stages = stageNumToStages.get(stageNum);
			
			for(TaskStageForUser tsfu : stages) {
				generateItemDrop(questIds, tsfu);
			}
		}
	}
	
	
	private void generateItemDrop(List<Integer> questIds, TaskStageForUser tsfu) {
		int monsterId = tsfu.getMonsterId();
		
		for (int questId : questIds) {
			QuestMonsterItem qmi = getQuestMonsterItemRetrieveUtils()
					.getItemForQuestAndMonsterId(questId, monsterId);
			
			if (null == qmi) {
				continue;
			}
			
			//roll to see if item should drop
			if (!qmi.didItemDrop()) {
				continue;
			}
			
			//since quest and monster have item associated with it and the item "dropped"
	  		//set item id 
			tsfu.setItemDropped(true);
			int itemId = qmi.getItemId();
			tsfu.setItemId(itemId);
		}
	}

	
	//RETRIEVING STUFF****************************************************************
	@Override
	public List<TaskStageForUser> getAllUserTaskStagesForUserTask(UUID userTaskId) {
		log.debug("retrieve TaskStageForUser data for user task id " + userTaskId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.TASK_STAGE_FOR_USER__TASK_FOR_USER_ID, userTaskId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<TaskStageForUser> tsfuList = getTaskStageForUserEntityManager().get().find(cqlquery);
		
		return tsfuList;
	}
	
	@Override
	public TaskStageForUser getSpecificTaskStageForUser(UUID taskStageForUserId) {
		log.debug("retrieve TaskStageForUser data for id " + taskStageForUserId);

		TaskStageForUser tfuo = getTaskStageForUserEntityManager().get().get(taskStageForUserId);
		return tfuo;
	}

	
	
	
	//INSERTING STUFF****************************************************************
	

	//SAVING STUFF****************************************************************
	@Override
	public void saveUserTaskStages(List<TaskStageForUser> userTaskStages) {
		getTaskStageForUserEntityManager().get().put(userTaskStages);
	}
	

	
	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************
	@Override
	public void deleteExistingTaskStagesForUserTaskId(UUID userTaskId, boolean getMonsterPieces, 
			Map<Integer, Integer> monsterIdToNumPieces) {
		//get from db
		List<TaskStageForUser> existingList = getAllUserTaskStagesForUserTask(userTaskId);
		
		//collect the primary keys that will be deleted
		List<UUID> userTaskStageIdList = new ArrayList<UUID>();
		
		//task stage history objects to be saved later
		List<TaskStageHistory> tshList = new ArrayList<TaskStageHistory>();
		
		//keep track of how many pieces dropped and by which task stage monster
	  	Map<Integer, Integer> taskStageMonsterIdToQuantity =
	  			new HashMap<Integer, Integer>();
	  	
		//convert the task stage for user objects into task stage history objects
		for (TaskStageForUser tsfu : existingList) {
			UUID userTaskStageId = tsfu.getId();
			int stageNum = tsfu.getStageNum();
			int taskStageMonsterId = tsfu.getTaskStageMonsterId();
			int expGained = tsfu.getExpGained();
			int cashGained = tsfu.getCashGained();
			boolean monsterPieceDropped = tsfu.isMonsterPieceDropped();
			
			userTaskStageIdList.add(userTaskStageId);
			
			TaskStageHistory tsh = new TaskStageHistory();
			tsh.setId(userTaskStageId);
			tsh.setTaskForUserId(userTaskId);
			tsh.setStageNum(stageNum);
			tsh.setTaskStageMonsterId(taskStageMonsterId);
			tsh.setExpGained(expGained);
			tsh.setCashGained(cashGained);
			tsh.setMonsterPieceDropped(monsterPieceDropped);
			tshList.add(tsh);

			//since monster piece dropped, update our current stats on monster pieces
			if (taskStageMonsterIdToQuantity.containsKey(taskStageMonsterId)) {
				//saw this task stage monster id before, increment quantity
				int quantity = 1 + taskStageMonsterIdToQuantity.get(taskStageMonsterId);
				taskStageMonsterIdToQuantity.put(taskStageMonsterId, quantity);

			} else {
				//haven't seen this task stage monster id yet, so start off at 1
				taskStageMonsterIdToQuantity.put(taskStageMonsterId, 1);
			}
		}
	
		//delete the existing task stage for user
		deleteUserTaskStages(userTaskStageIdList);
		
		//record the task stage history objects
		getTaskStageHistoryService().saveTaskStageHistories(tshList);

		if (getMonsterPieces) {
			aggregateMonsterPieces(taskStageMonsterIdToQuantity, monsterIdToNumPieces);
		}
	}

	private void aggregateMonsterPieces(Map<Integer, Integer> taskStageMonsterIdToQuantity,
			Map<Integer, Integer> monsterIdToNumPieces) {
		//retrieve those task stage monsters. aggregate the quantities by monster id
		//assume different task stage monsters can be the same monster
		Collection<Integer> taskStageMonsterIds = taskStageMonsterIdToQuantity.keySet();
		Map<Integer, TaskStageMonster> monstersThatDropped = getTaskStageMonsterRetrieveUtils()
				.getTaskStageMonstersForIds(taskStageMonsterIds);

		for (int taskStageMonsterId : taskStageMonsterIds) {
			TaskStageMonster monsterThatDropped = monstersThatDropped.get(taskStageMonsterId);
			int monsterId = monsterThatDropped.getMonsterId();
			int numPiecesDroppedForMonster = taskStageMonsterIdToQuantity.get(taskStageMonsterId); 

			//aggregate pieces based on monsterId, since assuming different task
			//stage monsters can be the same monster
			if (monsterIdToNumPieces.containsKey(monsterId)) {
				int newAmount = numPiecesDroppedForMonster + monsterIdToNumPieces.get(monsterId);
				monsterIdToNumPieces.put(monsterId, newAmount);

			} else {
				//first time seeing this monster, store existing quantity
				monsterIdToNumPieces.put(monsterId, numPiecesDroppedForMonster);
			}
		}
	}
	

	@Override
	public void deleteUserTaskStage(UUID userTaskStageId) {
		getTaskStageForUserEntityManager().get().delete(userTaskStageId);
	}

	@Override
	public void deleteUserTaskStages(List<UUID> userTaskStageIdList) {
		getTaskStageForUserEntityManager().get().delete(userTaskStageIdList);
	}



	//for the setter dependency injection or something
	@Override
	public TaskStageForUserEntityManager getTaskStageForUserEntityManager() {
		return taskStageForUserEntityManager;
	}

	@Override
	public void setTaskStageForUserEntityManager(TaskStageForUserEntityManager taskStageForUserEntityManager) {
		this.taskStageForUserEntityManager = taskStageForUserEntityManager;
	}
	@Override
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	@Override
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	@Override
	public TaskStageHistoryService getTaskStageHistoryService() {
		return taskStageHistoryService;
	}
	@Override
	public void setTaskStageHistoryService(
			TaskStageHistoryService taskStageHistoryService) {
		this.taskStageHistoryService = taskStageHistoryService;
	}
	@Override
	public TaskStageMonsterRetrieveUtils getTaskStageMonsterRetrieveUtils() {
		return taskStageMonsterRetrieveUtils;
	}
	@Override
	public void setTaskStageMonsterRetrieveUtils(
			TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtils) {
		this.taskStageMonsterRetrieveUtils = taskStageMonsterRetrieveUtils;
	}
	@Override
	public QuestMonsterItemRetrieveUtils getQuestMonsterItemRetrieveUtils() {
		return questMonsterItemRetrieveUtils;
	}
	@Override
	public void setQuestMonsterItemRetrieveUtils(
			QuestMonsterItemRetrieveUtils questMonsterItemRetrieveUtils) {
		this.questMonsterItemRetrieveUtils = questMonsterItemRetrieveUtils;
	}
	
}