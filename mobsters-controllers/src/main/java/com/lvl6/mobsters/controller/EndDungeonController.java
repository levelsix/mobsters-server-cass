package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.entitymanager.staticdata.utils.TaskRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.TaskStageRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.EndDungeonRequestProto;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.EndDungeonResponseProto;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.EndDungeonResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.EndDungeonResponseProto.EndDungeonStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.EndDungeonRequestEvent;
import com.lvl6.mobsters.events.response.EndDungeonResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserOngoing;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.taskforusercompleted.TaskForUserCompletedService;
import com.lvl6.mobsters.services.taskforuserongoing.TaskForUserOngoingService;
import com.lvl6.mobsters.services.taskstageforuser.TaskStageForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class EndDungeonController extends EventController {

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
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected TaskForUserCompletedService taskForUserCompletedService;
	
	public EndDungeonController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EndDungeonRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_BEGIN_DUNGEON_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		EndDungeonRequestProto reqProto = 
				((EndDungeonRequestEvent) event).getEndDungeonRequestProto();

		//get the values client sent
		MinimumUserProtoWithMaxResources senderResourcesProto = reqProto.getSender();
		MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
		String userTaskUuidStr = reqProto.getUserTaskUuid();
		boolean userWon = reqProto.getUserWon();
		Date clientDate = new Date(reqProto.getClientTime());
		boolean firstTimeUserWonTask = reqProto.getFirstTimeUserWonTask();
//		boolean generateFirstBoss = reqProto.getGenerateFirstBoss();
//		boolean respawnBoss = reqProto.getRespawnBoss();
		int maxCash = senderResourcesProto.getMaxCash();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userTaskUuid = UUID.fromString(userTaskUuidStr);
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = EndDungeonResponseProto.newBuilder();
		responseBuilder.setSender(senderResourcesProto);
		responseBuilder.setUserWon(userWon);
		responseBuilder.setStatus(EndDungeonStatus.FAIL_OTHER);
		
		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			
			TaskForUserOngoing ut = getTaskForUserOngoingService().getSpecificUserTask(userTaskUuid);
			boolean legit = checkLegit(aUser, userId, userTaskUuid, ut);

			
			List<FullUserMonsterProto> protos = new ArrayList<FullUserMonsterProto>();
			boolean successful = false;
			if(legit) {
				successful = writeChangesToDb(aUser, userId, ut, userWon, clientDate,
						protos, maxCash);
				responseBuilder.setTaskId(ut.getTaskId());
			}
			
			if (successful) {
				responseBuilder.setStatus(EndDungeonStatus.SUCCESS);
				responseBuilder.addAllUpdatedOrNew(protos);
			}

			EndDungeonResponseEvent resEvent = new EndDungeonResponseEvent(userIdString);
			resEvent.setTag(event.getTag());
			resEvent.setEndDungeonResponseProto(responseBuilder.build());
			//write to client
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);	
			
			if (successful) {
				int taskId = ut.getTaskId();
				writeToTaskForUserCompleted(userId, taskId, userWon, firstTimeUserWonTask,
						clientDate);
			}

		} catch (Exception e) {
			log.error("exception in EndDungeonController processRequestEvent", e);

			try {
				//try to tell client that something failed
				EndDungeonResponseEvent resEvent = new EndDungeonResponseEvent(userIdString);
				resEvent.setTag(event.getTag());
				responseBuilder.setStatus(EndDungeonStatus.FAIL_OTHER);
				resEvent.setEndDungeonResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in EndDungeonController processRequestEvent", e2);
			}
		}
	}

	private boolean checkLegit(User u, UUID userId, UUID userTaskId, TaskForUserOngoing userTask) {
		if (null == u) {
			log.error("unexpected error: user is null. user=" + u);
			return false;
		}
		
		if (null == userTask) {
	    	log.error("unexpected error: no user task for id userTaskId=" + userTaskId);
	    	return false;
	    }

	    return true;  
	}

	private boolean writeChangesToDb(User u, UUID uId, TaskForUserOngoing ut, boolean userWon,
			  Date clientDate, List<FullUserMonsterProto> protos, int maxCash) {
		try {
			//capping the user's cash
			int cashChange = getUserService().calculateMaxResource(u, 
					MobstersDbTables.USER__CASH, maxCash, ut.getCashGained());
			int gemChange = 0;
			int expChange = ut.getExpGained();

			//update user cash and experience
			if (userWon) {
				int newExp = expChange + u.getExp();
				List<UserCurrencyHistory> uchList = createCurrencyHistory(u, ut,
						clientDate, cashChange);

				u.setExp(newExp);
				int oilChange = 0;
				getUserService().updateUserResources(u, gemChange, oilChange, cashChange);

				//keep track of currency stuff
				if (!uchList.isEmpty()) {
					getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
				}
			}
			
			//delete from user_task and insert it into user_task_history
			UUID userTaskId = ut.getId();
			boolean cancelled = false;
			getTaskForUserOngoingService().deleteExistingUserTask(userTaskId, clientDate,
					userWon, cancelled, ut);
			
			//delete from task stage for user and put it into task stage history
			//give the user his monsters
			boolean getMonsterPieces = true;
			Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
			getTaskStageForUserService().deleteExistingTaskStagesForUserTaskId(userTaskId,
					getMonsterPieces, monsterIdToNumPieces);
			String mfusop = MobstersTableConstants.MFUSOP__END_DUNGEON + " " + userTaskId;
			List<MonsterForUser> rewardList = getMonsterForUserService()
					.updateUserMonstersForUser(uId, monsterIdToNumPieces, mfusop, clientDate);
			List<FullUserMonsterProto> rewardProtoList = getCreateNoneventProtoUtil()
					.createFullUserMonsterProtoList(rewardList);
			
			//send awarded monsters back up to sender
			protos.addAll(rewardProtoList);
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	private List<UserCurrencyHistory> createCurrencyHistory(User u, TaskForUserOngoing ut,
			Date timeRedeemed, int cashGained) {
		int taskId = ut.getTaskId();
		String cashStr = MobstersDbTables.USER__CASH;
		String reasonForChange = MobstersTableConstants.UCHRFC__END_TASK;
		StringBuilder sb = new StringBuilder();
		sb.append("taskId=");
		sb.append(taskId);
		String details = sb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory cash = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, timeRedeemed, cashStr, cashGained,
						reasonForChange, details, saveToDb);

		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != cash) {
			uchList.add(cash);
		}
		return uchList;
	}
	
	private void writeToTaskForUserCompleted(UUID userId, int taskId, 
			boolean userWon, boolean firstTimeUserWonTask, Date now) {
		if (userWon && firstTimeUserWonTask) {
			getTaskForUserCompletedService().insertIntoTaskForUserCompleted(userId,
					taskId, now);

			log.info("innserted into task_for_user_completed: taskId=" + taskId);
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

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}

	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}

	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

	public TaskForUserCompletedService getTaskForUserCompletedService() {
		return taskForUserCompletedService;
	}

	public void setTaskForUserCompletedService(
			TaskForUserCompletedService taskForUserCompletedService) {
		this.taskForUserCompletedService = taskForUserCompletedService;
	}
	
}
