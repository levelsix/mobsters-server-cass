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

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.controller.utils.MonsterStuffUtil;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.ReviveInDungeonRequestProto;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.ReviveInDungeonResponseProto;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.ReviveInDungeonResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.ReviveInDungeonResponseProto.ReviveInDungeonStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.ReviveInDungeonRequestEvent;
import com.lvl6.mobsters.events.response.ReviveInDungeonResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.taskforuserongoing.TaskForUserOngoingService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class ReviveInDungeonController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected MonsterStuffUtil monsterStuffUtil;

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;

	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;

	@Autowired
	protected TaskForUserOngoingService taskForUserOngoingService;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	public ReviveInDungeonController() {
		numAllocatedThreads = 2;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new ReviveInDungeonRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_REVIVE_IN_DUNGEON_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		ReviveInDungeonRequestProto reqProto = 
				((ReviveInDungeonRequestEvent) event).getReviveInDungeonRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getSender();
		String userTaskIdStr = reqProto.getUserTaskUuid();
		Date curTime = new Date(reqProto.getClientTime());
		List<UserMonsterCurrentHealthProto> umchpList = reqProto.getReviveMeList();
		//positive value, need to convert to negative when updating user
	    int gemsSpent = reqProto.getGemsSpent();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserUuid();
		UUID userId = UUID.fromString(userIdString);
		UUID userTaskId = UUID.fromString(userTaskIdStr);
		
		Map<UUID, Integer> userMonsterIdToExpectedHealth = new HashMap<UUID, Integer>();
		//extract the ids so it's easier to get userMonsters from db
		//also fills up the userMonsterIdToExpectedHealth map
		List<UUID> userMonsterIds = getMonsterStuffUtils().getUserMonsterIds(umchpList,
				userMonsterIdToExpectedHealth);
		
		
		//response to send back to client
		Builder responseBuilder = ReviveInDungeonResponseProto.newBuilder();
		responseBuilder.setStatus(ReviveInDungeonStatus.FAIL_OTHER);
		ReviveInDungeonResponseEvent resEvent =
				new ReviveInDungeonResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			Map<UUID, MonsterForUser> existingUserMonsters = getMonsterForUserService()
					.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);


			//validate request
			boolean validRequest = isValidRequest(responseBuilder, aUser, gemsSpent,
					userTaskId, existingUserMonsters, userMonsterIds, umchpList);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(aUser, userTaskId, gemsSpent, curTime,
						userMonsterIdToExpectedHealth, existingUserMonsters);
			}

			if (successful) {
				responseBuilder.setStatus(ReviveInDungeonStatus.SUCCESS);
			}

			//write to client
			resEvent.setReviveInDungeonResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

			if (successful) {
				//since modified user's resources need to send update client user event
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate);
			}
			
		} catch (Exception e) {
			log.error("exception in ReviveInDungeonController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(ReviveInDungeonStatus.FAIL_OTHER);
				resEvent.setReviveInDungeonResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in ReviveInDungeonController processRequestEvent", e2);
			}
		}
	}
	
	private boolean isValidRequest(Builder resBuilder, User u, int gemsSpent,
			UUID userTaskId, Map<UUID, MonsterForUser> mfuIdsToMonstersForUser,
			List<UUID> userMonsterIds, List<UserMonsterCurrentHealthProto> umchpList) {
		if (null == u) {
			log.error("unexpected error: user is null. user=" + u);
			return false;
		}
		
		if (null == umchpList || umchpList.isEmpty()) {
	  		log.error("client error: no user monsters sent.");
	  		return false;
	  	}

		if (null == mfuIdsToMonstersForUser || mfuIdsToMonstersForUser.isEmpty()) {
			log.error("unexpected error: userMonsterIds don't exist. ids=" + userMonsterIds);
			return false;
		}

		//see if the user has the monsters
	  	if (mfuIdsToMonstersForUser.size() != umchpList.size()) {
	  		log.error("unexpected error: mismatch between user equips client sent and " +
	  				"what is in the db. clientUserMonsterIds=" + userMonsterIds + "\t inDb=" +
	  				mfuIdsToMonstersForUser + "\t continuing the processing");
	  	}
	  	
	  	//make sure user has enough gems
	  	int userGems = u.getGems();
	  	int cost = gemsSpent;
	  	if (cost > userGems) {
	  		log.error("user error: user does not have enough gems to revive. " +
	  				"cost=" + cost + "\t userGems=" + userGems);
	  		resBuilder.setStatus(ReviveInDungeonStatus.FAIL_INSUFFICIENT_FUNDS);
	  		return false;
	  	}
	  	
	  	
		return true;
	}

	//charge the user gems for reviving in the dungeon
	//record the charge
	//update the user task
	//update monster's healths
	private boolean writeChangesToDb(User user, UUID userTaskId, int gemsSpent,
			Date curTime, Map<UUID, Integer> userMonsterIdsToHealths,
			Map<UUID, MonsterForUser> idsToUserMonsters) {
		try {
			//CHARGE THE USER
			int gemChange = -1 * gemsSpent;
			
			//create history first
			List<UserCurrencyHistory> uchList = createCurrencyHistory(user, curTime,
					gemChange, userMonsterIdsToHealths, idsToUserMonsters);
			int cashChange = 0;
			int oilChange = 0;
			getUserService().updateUserResources(user, gemChange, oilChange, cashChange);

			//keep track of currency stuff
			if (!uchList.isEmpty()) {
				getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
			}
			
			
			// update task for user ongoing num revives
			int numRevivesDelta = 1;
			getTaskForUserOngoingService().updateIncrementUserTaskNumRevives(
					userTaskId, numRevivesDelta);
			
			//replace existing health for these user monsters with new values 
			getMonsterForUserService().updateUserMonstersHealths(
					userMonsterIdsToHealths, idsToUserMonsters);
		  	
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	
	private List<UserCurrencyHistory> createCurrencyHistory(User aUser, Date date,
			int gemChange, Map<UUID, Integer> userMonsterIdsToHealths,
			Map<UUID, MonsterForUser> idsToUserMonsters) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__REVIVED_MONSTER;
		StringBuilder detailSb = new StringBuilder();
		
		detailSb.append("userMonsterIds: ");
		detailSb.append(userMonsterIdsToHealths.keySet());
		
		String details = detailSb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, date, gemsStr, gemChange,
						reasonForChange, details, saveToDb);
		
		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
	}
	

	public MonsterStuffUtil getMonsterStuffUtils() {
		return monsterStuffUtil;
	}

	public void setMonsterStuffUtils(MonsterStuffUtil monsterStuffUtil) {
		this.monsterStuffUtil = monsterStuffUtil;
	}
	
	public MonsterStuffUtil getMonsterStuffUtil() {
		return monsterStuffUtil;
	}

	public void setMonsterStuffUtil(MonsterStuffUtil monsterStuffUtil) {
		this.monsterStuffUtil = monsterStuffUtil;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}

	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}

	public TaskForUserOngoingService getTaskForUserOngoingService() {
		return taskForUserOngoingService;
	}

	public void setTaskForUserOngoingService(
			TaskForUserOngoingService taskForUserOngoingService) {
		this.taskForUserOngoingService = taskForUserOngoingService;
	}

	public CreateEventProtoUtil getCreateEventProtoUtil() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtil(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}
		
}
