package com.lvl6.mobsters.services.taskstagehistory;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskStageHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;


@Component
public class TaskStageHistoryServiceImpl implements TaskStageHistoryService {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_TASK_STAGE_HISTORY;

	@Autowired
	protected TaskStageHistoryEntityManager taskHistoryEntityManager;


	//CONTROLLER LOGIC STUFF****************************************************************



	//RETRIEVING STUFF****************************************************************


	//INSERTING STUFF****************************************************************
//	@Override
//	public void insertUserTaskStagesIntoHistory(UUID userTaskId,
//			List<TaskStageForUser> taskStagesForUser) {
//		UUID userId = aTaskForUser.getUserId();
//		int taskId = aTaskForUser.getTaskId();
//		int expGained = aTaskForUser.getExpGained();
//		int cashGained = aTaskForUser.getCashGained();
//		int numRevives = aTaskForUser.getNumRevives();
//		Date startDate = aTaskForUser.getStartDate(); //shouldn't null
//
//		TaskStageHistory th = new TaskStageHistory();
//		th.setId(userTaskId);
//		th.setUserId(userId);
//		th.setTaskId(taskId);
//		th.setExpGained(expGained);
//		th.setCashGained(cashGained);
//		th.setNumRevives(numRevives);
//		th.setStartTime(startDate);
//		th.setEndTime(endDate);
//		th.setUserWon(userWon);
//		th.setCancelled(cancelled);
//
//		saveTaskStageHistory(th);
//	}


	//SAVING STUFF****************************************************************
	@Override
	public void saveTaskStageHistory(TaskStageHistory tsh) {
		getTaskStageHistoryEntityManager().get().put(tsh);
	}

	@Override
	public void saveTaskStageHistories(List<TaskStageHistory> tshList) {
		getTaskStageHistoryEntityManager().get().put(tshList);
	}


	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************



	//for the setter dependency injection or something
	@Override
	public TaskStageHistoryEntityManager getTaskStageHistoryEntityManager() {
		return taskHistoryEntityManager;
	}
	@Override
	public void setTaskStageHistoryEntityManager(
			TaskStageHistoryEntityManager taskHistoryEntityManager) {
		this.taskHistoryEntityManager = taskHistoryEntityManager;
	}

}