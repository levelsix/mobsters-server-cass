package com.lvl6.mobsters.services.taskstageforuser;

import java.util.ArrayList;
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
import com.lvl6.mobsters.entitymanager.staticdata.TaskStageMonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageHistory;
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
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	//returns map(stageId ---> List<TaskStageForUser>); also returns
	//expGained (contains the sum of the exp across all stages), same with cashGained,
	//map(stageNum ---> stageId)
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
	
	//RETRIEVING STUFF****************************************************************
	@Override
	public List<TaskStageForUser> getAllUserTaskStagesForUserTask(UUID userTaskId) {
		log.debug("retrieve TaskStageForUser data for user task id " + userTaskId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.TASK_STAGE_FOR_USER__TASK_FOR_USER_ID, userTaskId);

		//query db, "values" is not used
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, values, preparedStatement);
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
	public void deleteExistingTaskStagesForUserTaskId(UUID userTaskId) {
		List<TaskStageForUser> existingList = getAllUserTaskStagesForUserTask(userTaskId);
		
		List<UUID> userTaskStageIdList = new ArrayList<UUID>();
		
		List<TaskStageHistory> tshList = new ArrayList<TaskStageHistory>();
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
		}
	
		//delete the existing task stage for user
		deleteUserTaskStages(userTaskStageIdList);
		
		//record the task stage history objects
		getTaskStageHistoryService().saveTaskStageHistories(tshList);
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
	
}