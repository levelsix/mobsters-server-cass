package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.entitymanager.staticdata.utils.TaskRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.TaskStageRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.BeginDungeonRequestProto;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.BeginDungeonResponseProto;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.BeginDungeonResponseProto.BeginDungeonStatus;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.BeginDungeonResponseProto.Builder;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.BeginDungeonRequestEvent;
import com.lvl6.mobsters.events.response.BeginDungeonResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserOngoing;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.po.staticdata.Task;
import com.lvl6.mobsters.po.staticdata.TaskStage;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.eventpersistentforuser.EventPersistentForUserService;
import com.lvl6.mobsters.services.taskforuserongoing.TaskForUserOngoingService;
import com.lvl6.mobsters.services.taskstageforuser.TaskStageForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class BeginDungeonController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected TaskRetrieveUtils taskRetrieveUtils;
	
	@Autowired
	protected TaskStageRetrieveUtils taskStageRetrieveUtils;
	
	@Autowired
	protected TaskForUserOngoingService taskForUserOngoingService;
	
	@Autowired
	protected TaskStageForUserService taskStageForUserService;
	
	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;

	@Autowired
	protected EventPersistentForUserService eventPersistentForUserService;

	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;

	public BeginDungeonController() {
		numAllocatedThreads = 8;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new BeginDungeonRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_BEGIN_DUNGEON_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		BeginDungeonRequestProto reqProto = 
				((BeginDungeonRequestEvent) event).getBeginDungeonRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
		Date clientDate = new Date(reqProto.getClientTime());
		int taskId = reqProto.getTaskId();
//		boolean spawnBoss = reqProto.getSpawnBoss();
		
		//if is event, start the cool down timer in event_persistent_for_user
	    boolean isEvent = reqProto.getIsEvent();
	    int eventId = reqProto.getPersistentEventId();
	    int gemsSpent = reqProto.getGemsSpent();
		List<Integer> questIds = reqProto.getQuestIdsList();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = BeginDungeonResponseProto.newBuilder();
		responseBuilder.setSender(senderProto);
		responseBuilder.setTaskId(taskId);
		responseBuilder.setStatus(BeginDungeonStatus.FAIL_OTHER);
		
		//don't think need a lock
		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			Task aTask = getTaskRetrieveUtils().getTaskForId(taskId);
			Map<Integer, TaskStage> tsMap = getTaskStageRetrieveUtils()
					.getTaskStagesForTaskId(taskId);

			boolean legitRequest = checkLegit(responseBuilder, aUser, userId, aTask,
					taskId, clientDate, tsMap, isEvent, eventId, gemsSpent);
			
			List<UUID> userTaskIdList = new ArrayList<UUID>();
			Map<Integer, TaskStageProto> stageNumsToProtos = new HashMap<Integer, TaskStageProto>();
			boolean successful = false;
			if(legitRequest) {
		      	//determine the specifics for each stage (stored in stageNumsToProtos)
		      	//then record specifics in db
		    	  successful = writeChangesToDb(aUser, userId, aTask, taskId, tsMap,
		    			  clientDate, isEvent, eventId, gemsSpent, userTaskIdList,
		    			  stageNumsToProtos, questIds);
			}
			
			if (successful) {
				setResponseBuilder(responseBuilder, userTaskIdList, stageNumsToProtos);
			}

			BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(userIdString);
			resEvent.setTag(event.getTag());
			resEvent.setBeginDungeonResponseProto(responseBuilder.build());
			//write to client
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);	

		} catch (Exception e) {
			log.error("exception in BeginDungeonController processRequestEvent", e);

			try {
				//try to tell client that something failed
				BeginDungeonResponseEvent resEvent = new BeginDungeonResponseEvent(userIdString);
				resEvent.setTag(event.getTag());
				responseBuilder.setStatus(BeginDungeonStatus.FAIL_OTHER);
				resEvent.setBeginDungeonResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in BeginDungeonController processRequestEvent", e2);
			}
		}
	}

	private boolean checkLegit(Builder resBuilder, User u, UUID userId, Task aTask,
			int taskId, Date clientDate, Map<Integer, TaskStage> tsMap, boolean isEvent,
			int eventId, int gemsSpent) {
		if (null == u || null == aTask) {
			log.error("unexpected error: user or task is null. user=" + u + "\t task="+ aTask);
			return false;
		}
		if (null == tsMap) {
			log.error("unexpected error: task has no taskStages. task=" + aTask);
			return false;
		}
		
		TaskForUserOngoing existing = getTaskForUserOngoingService()
				.getAllUserTaskForUser(userId);
		if (null != existing) {
			log.warn("(will continue processing, but) user has existing task when" +
					" beginning another. No task should exist. user=" + u + "\t task=" +
					aTask + "\t userTask=" + existing);
			
			//DELETE TASK AND PUT IT INTO TASK HISTORY
			//could put all this logic into TaskForUserOngoingService, but making it
			//so that other classes can use it.
			UUID userTaskId = existing.getId();
			boolean userWon = false;
			boolean cancelled = true;
			getTaskForUserOngoingService().deleteExistingUserTask(userTaskId, clientDate,
					userWon, cancelled, existing);
			//DELETE FROM TASK STAGE FOR USER AND PUT IT INTO TASK STAGE HISTORY,
			//don't want to find out what monster pieces the user could have gotten
			boolean getMonsterPieces = false;
			Map<Integer, Integer> monsterIdToNumPieces = null;
			getTaskStageForUserService().deleteExistingTaskStagesForUserTaskId(userTaskId,
					getMonsterPieces, monsterIdToNumPieces);
		}
		
		//TODO: if event, maybe somehow check if user has enough gems to reset event
	    //right now just relying on user
	    if (isEvent) {
	    	if (eventId > 0) {
	    		log.info("user initiating persistent event");
	    	} else {
	    		log.error("isEvent set to true but eventId not positive " + "\t eventId=" +
	    				eventId + "\t gemsSpent=" + gemsSpent);
	    		return false;
	    	}
	    }

		resBuilder.setStatus(BeginDungeonStatus.SUCCESS);
	    return true;  
	}

	private boolean writeChangesToDb(User u, UUID userId, Task t, int taskId,
			  Map<Integer, TaskStage> tsMap, Date clientDate, boolean isEvent,
			  int eventId, int gemsSpent, List<UUID> utIdList,
			  Map<Integer, TaskStageProto> stageNumsToProtos, List<Integer> questIds) {
		try {
			//create user task
			TaskForUserOngoing tfuo = new TaskForUserOngoing();
			
			//return values from creating the user task stages
			List<Integer> expList = new ArrayList<Integer>();
			List<Integer> cashList = new ArrayList<Integer>();
			
			//generate the stages for this task
			UUID userTaskId = tfuo.getId();
			Map<Integer, List<TaskStageForUser>> stageNumToStages =
					getTaskStageForUserService().generateUserTaskStagesFromTaskStages(
							userTaskId, tsMap, expList, cashList);
			
			//save the user task
			tfuo.setTaskId(taskId);
			int expGained = expList.get(0);
			tfuo.setExpGained(expGained);
			int cashGained = cashList.get(0);
			tfuo.setCashGained(cashGained);
			tfuo.setStartDate(clientDate);
			getTaskForUserOngoingService().saveTaskForUserOngoing(tfuo);
			
			//send stuff back up to caller
			utIdList.add(userTaskId);
			//save the user task stages and create the protos
			recordStages(stageNumToStages, stageNumsToProtos);
			
			//start the cool down timer if for event
			if (isEvent) {
				getEventPersistentForUserService()
				.createOrUpdateUserEventPersistentForUser(userId, eventId, clientDate);
				chargeUser(u, clientDate, gemsSpent, eventId, taskId);
			}
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	
	//save these task stage for user objects; convert into protos and store into
	//stageNumsToProtos. one TaskStageForUser represents one monster in the current stage
	private void recordStages(Map<Integer, List<TaskStageForUser>> stageNumToStages,
			Map<Integer, TaskStageProto> stageNumsToProtos) {
		Set<Integer> stageNums = stageNumToStages.keySet();
		List<Integer> stageNumList = new ArrayList<Integer>(stageNums);
		
		//don't know why this is sorted (mysql server version did it)...
		Collections.sort(stageNumList);
		int size = stageNumList.size();
		
		//loop through the individual stages, creating protos and aggregate them
		//to later be saved to the db
		List<TaskStageForUser> allTsfu = new ArrayList<TaskStageForUser>(); 
		for (int i = 0; i < size; i++) {
			Integer stageNum = stageNumList.get(i);
			
			//aggregate to later be saved to db
			List<TaskStageForUser> tsfuList = stageNumToStages.get(stageNum);
			allTsfu.addAll(tsfuList);
			
			//create the proto
			TaskStageForUser tsfu = tsfuList.get(0);
			int taskStageId = tsfu.getTaskStageId();
			TaskStageProto tsp = getCreateNoneventProtoUtil()
					.createTaskStageProtoFromTaskStageForUser(taskStageId, tsfuList);
			
			//return to sender
			stageNumsToProtos.put(stageNum, tsp);
		}
	}
	
	private void chargeUser(User user, Date now, int gemsSpent, int eventId, int taskId) {
		
		//charge user if client says so
		if (0 == gemsSpent) {
			return;
		}
		int gemChange = -1 * gemsSpent;
		List<UserCurrencyHistory> uchList = createCurrencyHistory(user, now, gemChange,
				eventId, taskId);
		
		int oilChange = 0;
		int cashChange = 0;
		getUserService().updateUserResources(user, gemChange, oilChange, cashChange);
		
		//record user currency
		if (!uchList.isEmpty()) {
			getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
		}
	}
	
	private List<UserCurrencyHistory> createCurrencyHistory(User u, Date timeRedeemed,
			int gemChange, int eventPersistentId, int taskId) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__SPED_UP_PERSISTENT_EVENT_TIMER;
		StringBuilder sb = new StringBuilder();
		sb.append("eventId=");
		sb.append(eventPersistentId);
		sb.append(" taskId=");
		sb.append(taskId);
		String details = sb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, timeRedeemed, gemsStr, gemChange,
						reasonForChange, details, saveToDb);
		
		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
	}
	
	

	private void setResponseBuilder(Builder resBuilder, List<UUID> userTaskIdList,
			Map<Integer, TaskStageProto> stageNumsToProtos) {
		//stuff to send to the client
		UUID userTaskId = userTaskIdList.get(0);
		String userTaskUuid = userTaskId.toString();
		resBuilder.setUserTaskUuid(userTaskUuid);

		//to handle the case if there are gaps in stageNums for a task, we
		//order the available stage numbers. Then we give it all to the client
		//sequentially, just because.
		Set<Integer> stageNums = stageNumsToProtos.keySet();
		List<Integer> stageNumsOrdered = new ArrayList<Integer>(stageNums);
		Collections.sort(stageNumsOrdered);

		for (Integer i : stageNumsOrdered) {
			TaskStageProto tsp = stageNumsToProtos.get(i);
			resBuilder.addTsp(tsp);
		}
	}

	
	
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public TaskRetrieveUtils getTaskRetrieveUtils() {
		return taskRetrieveUtils;
	}

	public void setTaskRetrieveUtils(TaskRetrieveUtils taskRetrieveUtils) {
		this.taskRetrieveUtils = taskRetrieveUtils;
	}

	public TaskStageRetrieveUtils getTaskStageRetrieveUtils() {
		return taskStageRetrieveUtils;
	}

	public void setTaskStageRetrieveUtils(
			TaskStageRetrieveUtils taskStageRetrieveUtils) {
		this.taskStageRetrieveUtils = taskStageRetrieveUtils;
	}

	public TaskForUserOngoingService getTaskForUserOngoingService() {
		return taskForUserOngoingService;
	}

	public void setTaskForUserOngoingService(
			TaskForUserOngoingService taskForUserOngoingService) {
		this.taskForUserOngoingService = taskForUserOngoingService;
	}

	public TaskStageForUserService getTaskStageForUserService() {
		return taskStageForUserService;
	}

	public void setTaskStageForUserService(
			TaskStageForUserService taskStageForUserService) {
		this.taskStageForUserService = taskStageForUserService;
	}

	public CreateNoneventProtoUtil getCreateNoneventProtoUtil() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtil(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}

	public EventPersistentForUserService getEventPersistentForUserService() {
		return eventPersistentForUserService;
	}

	public void setEventPersistentForUserService(
			EventPersistentForUserService eventPersistentForUserService) {
		this.eventPersistentForUserService = eventPersistentForUserService;
	}

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}
	
}
