package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Collection;
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

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtils;
import com.lvl6.mobsters.controller.utils.MonsterStuffUtils;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterWaitTimeCompleteRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto.HealMonsterWaitTimeCompleteStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.HealMonsterWaitTimeCompleteRequestEvent;
import com.lvl6.mobsters.events.response.HealMonsterWaitTimeCompleteResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.monsterhealingforuser.MonsterHealingForUserService;
import com.lvl6.mobsters.services.monsterhealingforuser.MonsterHealingHistoryService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;

@Component
public class HealMonsterWaitTimeCompleteController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected UserService userService;

	@Autowired
	protected MonsterHealingForUserService monsterHealingForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;

	@Autowired
	protected MonsterForUserService monsterForUserService;

	@Autowired
	protected CreateEventProtoUtils createEventProtoUtils;
	
	@Autowired
	protected MonsterHealingHistoryService monsterHealingHistoryService;
	

	@Override
	public RequestEvent createRequestEvent() {
		return new HealMonsterWaitTimeCompleteRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		HealMonsterWaitTimeCompleteRequestProto reqProto = 
				((HealMonsterWaitTimeCompleteRequestEvent) event).getHealMonsterWaitTimeCompleteRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userIdString = senderProto.getUserUuid();
		boolean isSpeedUp = reqProto.getIsSpeedup();
		List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();
		//will be populated by MonsterStuffUtils.getUserMonsterIds()
		Map<UUID, Integer> userMonsterIdToExpectedHealth = new HashMap<UUID, Integer>();
		//converts protos to a map and also returns a list of user monster ids
		List<UUID> userMonsterIds = getMonsterStuffUtils()
				.getUserMonsterIds(umchpList, userMonsterIdToExpectedHealth);
		int gemsForSpeedUp = reqProto.getGemsForSpeedup();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();

		//response to send back to client
		Builder responseBuilder = HealMonsterWaitTimeCompleteResponseProto.newBuilder();
		responseBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.FAIL_OTHER);
		HealMonsterWaitTimeCompleteResponseEvent resEvent =
				new HealMonsterWaitTimeCompleteResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			Map<UUID, MonsterHealingForUser> inHealing = getMonsterHealingForUserService()
					.getSpecificOrAllUserMonstersHealingForUser(userId, userMonsterIds);
			Map<UUID, MonsterForUser> idsToUserMonsters = getMonsterForUserService()
					.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);
			

			boolean legit = isValidRequest(responseBuilder, aUser, userId, inHealing,
					userMonsterIds, isSpeedUp, gemsForSpeedUp);


			boolean successful = false;
			if (legit) {
				//modify map, of userMonsterIds to expected healths, to contain only valid
				//mappings (valid mappings determined by collection of valid keys/ids)
				userMonsterIdToExpectedHealth = getMonsterStuffUtils().getValidEntries(userMonsterIds, userMonsterIdToExpectedHealth);
				successful = writeChangesToDb(aUser, userId, userMonsterIds,
		    	  		userMonsterIdToExpectedHealth, isSpeedUp, gemsForSpeedUp,
		    	  		clientDate, idsToUserMonsters);
			}

			if (successful) {
				responseBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.SUCCESS);
			}

			//write to client
			resEvent.setHealMonsterWaitTimeCompleteResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//since modified user's resources need to send update client user event
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtils()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate);
				
				Map<UUID, Integer> prevHps = getMonsterStuffUtils().getHealths(
						userMonsterIds, idsToUserMonsters);
				writeChangesToHistory(userId, clientDate, prevHps, inHealing,
						userMonsterIds, idsToUserMonsters);
			}

		} catch (Exception e) {
			log.error("exception in HealMonsterWaitTimeCompleteController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.FAIL_OTHER);
				resEvent.setHealMonsterWaitTimeCompleteResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in HealMonsterWaitTimeCompleteController processRequestEvent", e2);
			}
		}
	}


	/**
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value. @healedUp
	 * MIGHT BE MODIFIED.
	 * 
	 * Will return fail if user does not have enough funds. 
	 * For the most part, will always return success. Why?
	 * Answer: For @healedUp, the monsters the client thinks completed healing,
	 * only existing/valid ids will be taken off the healing queue.
	 * 
	 * Ex. Queue is (a,b,c,d). If user says monster (a,b,e) finished healing, 
	 * only the valid monsters (a,b) will be removed from the queue.
	 * 
	 * @param resBuilder
	 * @param u
	 * @param userId
	 * @param alreadyHealing - the monsters that are in the healing queue
	 * @param healedUp - userMonsterIds the user thinks has finished healing
	 * @param speedUUp
	 * @param gemsForSpeedUp
	 * @return
	 */
	private boolean isValidRequest(Builder resBuilder, User u, UUID userId,
	  		Map<UUID, MonsterHealingForUser> alreadyHealing, List<UUID> healedUp,
	  		boolean speedup, int gemsForSpeedup) {
		
		if (null == u || null == healedUp || healedUp.isEmpty()) {
			log.error("unexpected error: user or idList is null. user=" + u +
					"\t healedUp="+ healedUp + "\t speedUp=" + speedup);
			return false;
		}
		log.info("alreadyHealing=" + alreadyHealing);

		//modify healedUp to contain only those that exist
		Set<UUID> alreadyHealingIds = alreadyHealing.keySet();
		getMonsterStuffUtils().retainValidMonsterIds(alreadyHealingIds, healedUp);

		//CHECK MONEY and CHECK SPEEDUP
		if (speedup) {
			int userGems = u.getGems();
			if (userGems < gemsForSpeedup) {
				log.error("user error: user sped up healing but has too little gems userGems=" +
						userGems + "\t gemsForSpeedup=" + gemsForSpeedup);
				resBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.FAIL_INSUFFICIENT_FUNDS);
				return false;
			}
		}
		//TODO:update monster's healths <--don't know why this is here

		resBuilder.setStatus(HealMonsterWaitTimeCompleteStatus.SUCCESS);
	    return true;
	}

	private boolean writeChangesToDb(User aUser, UUID uId, List<UUID> userMonsterIds,
			Map<UUID, Integer> userMonsterIdsToHealths, boolean isSpeedup,
			int gemsForSpeedup, Date clientTime, Map<UUID, MonsterForUser> idsToUserMonsters) {
		try {
			if(isSpeedup) {
				//CHARGE THE USER
				int gemChange = -1 * gemsForSpeedup;
				int newGems = gemChange + aUser.getGems();
				//create history first
				List<UserCurrencyHistory> uchList = createCurrencyHistory(aUser,
						userMonsterIds, idsToUserMonsters, userMonsterIdsToHealths,
						clientTime, gemChange);
				aUser.setGems(newGems);
				getUserService().saveUser(aUser);

				//keep track of currency stuff
				if (!uchList.isEmpty()) {
					getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
				}
			}
			
			//heal the monsters
			getMonsterHealingForUserService().healUserMonsters(userMonsterIdsToHealths,
					idsToUserMonsters);

			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	private List<UserCurrencyHistory> createCurrencyHistory(User aUser,
			List<UUID> userMonsterIds, Map<UUID, MonsterForUser> idsToUserMonsters, 
			Map<UUID, Integer> userMonsterIdsToHealths, Date clientTime, int gemsForSpeedup) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__SPED_UP_HEALING;
		StringBuilder sb = new StringBuilder();
		sb.append("(prevHp,curHp):");

		//maybe shouldn't keep track...oh well, more info hopefully is better than none
		for (UUID id : userMonsterIds) {
			MonsterForUser mfu = idsToUserMonsters.get(id);
			int prevHp = mfu.getCurrentHealth();
			int curHp = userMonsterIdsToHealths.get(id);
			
			sb.append("(");
			sb.append(prevHp);
			sb.append(",");
			sb.append(curHp);
			sb.append(") ");
		}
		String details = sb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, clientTime, gemsStr, gemsForSpeedup,
						reasonForChange, details, saveToDb);

		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
	}
	
	private void writeChangesToHistory(UUID uId, Date now, Map<UUID, Integer> prevHps,
			Map<UUID, MonsterHealingForUser> inHealing, Collection<UUID> finishedMfuIds,
			Map<UUID, MonsterForUser> idsToUserMonsters) {
		try {
			boolean healingCancelled = true;

			getMonsterHealingHistoryService().insertHealingHistory(uId, now,
					prevHps, inHealing, finishedMfuIds, idsToUserMonsters,
					healingCancelled);

		} catch (Exception e) {
			log.error("unexpected error: problem with saving history to db.", e);
		}
	}
	

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public MonsterHealingForUserService getMonsterHealingForUserService() {
		return monsterHealingForUserService;
	}

	public void setMonsterHealingForUserService(
			MonsterHealingForUserService monsterHealingForUserService) {
		this.monsterHealingForUserService = monsterHealingForUserService;
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

	public CreateEventProtoUtils getCreateEventProtoUtils() {
		return createEventProtoUtils;
	}

	public void setCreateEventProtoUtils(CreateEventProtoUtils createEventProtoUtils) {
		this.createEventProtoUtils = createEventProtoUtils;
	}

	public MonsterHealingHistoryService getMonsterHealingHistoryService() {
		return monsterHealingHistoryService;
	}

	public void setMonsterHealingHistoryService(
			MonsterHealingHistoryService monsterHealingHistoryService) {
		this.monsterHealingHistoryService = monsterHealingHistoryService;
	}

}