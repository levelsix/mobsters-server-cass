package com.lvl6.mobsters.services.taskhistory;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserOngoing;
import com.lvl6.mobsters.po.nonstaticdata.TaskHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;

@Component
public class TaskHistoryServiceImpl implements TaskHistoryService {
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_TASK_HISTORY;
	
	@Autowired
	protected TaskHistoryEntityManager taskHistoryEntityManager;
	
	
	//CONTROLLER LOGIC STUFF****************************************************************



	//RETRIEVING STUFF****************************************************************


	//INSERTING STUFF****************************************************************
	@Override
	public void insertUserTaskIntoHistory(UUID userTaskId, Date endDate,
			boolean userWon, boolean cancelled, TaskForUserOngoing aTaskForUser) {
		UUID userId = aTaskForUser.getUserId();
		int taskId = aTaskForUser.getTaskId();
	  	int expGained = aTaskForUser.getExpGained();
	  	int cashGained = aTaskForUser.getCashGained();
	  	int numRevives = aTaskForUser.getNumRevives();
	  	Date startDate = aTaskForUser.getStartDate(); //shouldn't null
		
		TaskHistory th = new TaskHistory();
		th.setId(userTaskId);
		th.setUserId(userId);
		th.setTaskId(taskId);
		th.setExpGained(expGained);
		th.setCashGained(cashGained);
		th.setNumRevives(numRevives);
		th.setStartTime(startDate);
		th.setEndTime(endDate);
		th.setUserWon(userWon);
		th.setCancelled(cancelled);

		saveTaskHistory(th);
	}


	//SAVING STUFF****************************************************************
	@Override
	public void saveTaskHistory(TaskHistory th) {
		getTaskHistoryEntityManager().get().put(th);
	}

	@Override
	public void saveTaskHistories(List<TaskHistory> thList) {
		getTaskHistoryEntityManager().get().put(thList);
	}


	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************



	//for the setter dependency injection or something
	@Override
	public TaskHistoryEntityManager getTaskHistoryEntityManager() {
		return taskHistoryEntityManager;
	}
	@Override
	public void setTaskHistoryEntityManager(
			TaskHistoryEntityManager taskHistoryEntityManager) {
		this.taskHistoryEntityManager = taskHistoryEntityManager;
	}



}