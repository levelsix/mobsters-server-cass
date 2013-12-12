package com.lvl6.mobsters.services.taskstageforuser;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskStageForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.TaskStageMonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;
import com.lvl6.mobsters.po.staticdata.TaskStage;
import com.lvl6.mobsters.po.staticdata.TaskStageMonster;
import com.lvl6.mobsters.services.taskstagehistory.TaskStageHistoryService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface TaskStageForUserService {
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	//returns map(stageNum ---> List<TaskStageForUser>); also returns
	//expGained (contains the sum of the exp across all stages), same with cashGained,
	//(given that these taskStages are for one task, stageNum and stageId are one-to-one)
	public abstract Map<Integer, List<TaskStageForUser>> generateUserTaskStagesFromTaskStages(
			UUID userTaskId, Map<Integer, TaskStage> tsIdToTs, List<Integer> expGained,
			List<Integer> cashGained);
	
	public abstract List<TaskStageMonster> generateSpawnedMonsters(int taskStageId,
			int quantity);
	
	public abstract float sumProbabilities(List<TaskStageMonster> taskStageMonsters);
	
	//RETRIEVING STUFF****************************************************************
	public abstract List<TaskStageForUser> getAllUserTaskStagesForUserTask(UUID userTaskId);
	
	public abstract TaskStageForUser getSpecificTaskStageForUser(UUID taskStageForUserId);
	
	
	//INSERTING STUFF****************************************************************
	

	//SAVING STUFF****************************************************************
	public abstract void saveUserTaskStages(List<TaskStageForUser> userTaskStages);
	
	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************
	public abstract void deleteExistingTaskStagesForUserTaskId(UUID userTaskId);
	
	public abstract void deleteUserTaskStage(UUID userTaskStageId);
	
	public abstract void deleteUserTaskStages(List<UUID> userTaskStageIdList);


	//for the setter dependency injection or something
	public abstract TaskStageForUserEntityManager getTaskStageForUserEntityManager();
	
	public abstract void setTaskStageForUserEntityManager(
			TaskStageForUserEntityManager taskStageForUserEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);

	public abstract TaskStageHistoryService getTaskStageHistoryService();

	public abstract void setTaskStageHistoryService(
			TaskStageHistoryService taskStageHistoryService);
	
	public abstract TaskStageMonsterRetrieveUtils getTaskStageMonsterRetrieveUtils();

	public abstract void setTaskStageMonsterRetrieveUtils(
			TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtils);
}
