package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SubmitMonsterEnhancementRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SubmitMonsterEnhancementResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SubmitMonsterEnhancementResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SubmitMonsterEnhancementResponseProto.SubmitMonsterEnhancementStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.SubmitMonsterEnhancementRequestEvent;
import com.lvl6.mobsters.events.response.SubmitMonsterEnhancementResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterenhancingforuser.MonsterEnhancingForUserService;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.monsterhealingforuser.MonsterHealingForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class SubmitMonsterEnhancementController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected MonsterEnhancingForUserService monsterEnhancingForUserService;
	
	@Autowired
	protected MonsterHealingForUserService monsterHealingForUserService;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateEventProtoUtils createEventProtoUtils;

	@Override
	public RequestEvent createRequestEvent() {
		return new SubmitMonsterEnhancementRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_SUBMIT_MONSTER_ENHANCEMENT_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		SubmitMonsterEnhancementRequestProto reqProto = 
				((SubmitMonsterEnhancementRequestEvent) event).getSubmitMonsterEnhancementRequestProto();
		
		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
		List<UserEnhancementItemProto> ueipDelete = reqProto.getUeipDeleteList();
		List<UserEnhancementItemProto> ueipUpdated = reqProto.getUeipUpdateList();
		List<UserEnhancementItemProto> ueipNew = reqProto.getUeipNewList();
		String userIdString = senderProto.getUserUuid();
		//positive value, need to convert to negative when updating user
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int cashChange = reqProto.getCashChange();
		Date clientTime = new Date();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);
		
		Map<UUID, UserEnhancementItemProto> deleteMap = getMonsterStuffUtils().
				convertIntoUserMonsterIdToUeipProtoMap(ueipDelete);
		Map<UUID, UserEnhancementItemProto> updateMap = getMonsterStuffUtils().
				convertIntoUserMonsterIdToUeipProtoMap(ueipUpdated);
		Map<UUID, UserEnhancementItemProto> newMap = getMonsterStuffUtils().
				convertIntoUserMonsterIdToUeipProtoMap(ueipNew);
		
		//response to send back to client
		Builder responseBuilder = SubmitMonsterEnhancementResponseProto.newBuilder();
		responseBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_OTHER);
		SubmitMonsterEnhancementResponseEvent resEvent =
				new SubmitMonsterEnhancementResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			Map<UUID, MonsterEnhancingForUser> alreadyEnhancing = getMonsterEnhancingForUserService()
					.getMonstersEnhancingForUser(userId);
			Map<UUID, MonsterHealingForUser> alreadyHealing = getMonsterHealingForUserService()
					.getMonstersHealingForUser(userId);
			
			//retrieve only the new monsters that will be used in enhancing
			Set<UUID> newIds = new HashSet<UUID>();
			newIds.addAll(newMap.keySet());
			Map<UUID, MonsterForUser> existingUserMonsters = getMonsterForUserService()
					.getSpecificOrAllUserMonstersForUser(userId, newIds);

			
			//validate request
			boolean validRequest = checkLegit(responseBuilder, aUser, userId, existingUserMonsters, 
					alreadyEnhancing, alreadyHealing, deleteMap, updateMap, newMap,
					gemsSpent, cashChange);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(aUser, userId, gemsSpent, cashChange,
						deleteMap, updateMap, newMap, clientTime);
			}

			if (successful) {
				responseBuilder.setStatus(SubmitMonsterEnhancementStatus.SUCCESS);
			}

			//write to client
			resEvent.setSubmitMonsterEnhancementResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//since modified user's resources need to send update client user event
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtils()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in SubmitMonsterEnhancementController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_OTHER);
				resEvent.setSubmitMonsterEnhancementResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in SubmitMonsterEnhancementController processRequestEvent", e2);
			}
		}
	}
	
	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value. delete, update, new maps
	 * MIGHT BE MODIFIED.
	 * 
	 * For the most part, will always return success. Why?
	 * (Will return fail if user does not have enough funds.) 
	 * Answer: For the map
	 * 
	 * delete - The monsters to be removed from enhancing will only be the ones
	 * the user already has in enhancing.
	 * update - Same logic as above.
	 * new - Same as above.
	 * 
	 * Ex. If user wants to delete a monster (A) that isn't enhancing, along with some
	 * monsters already enhancing (B), only the valid monsters (B) will be deleted.
	 * Same logic with update and new. 
	 * 
	 */
	private boolean checkLegit(Builder resBuilder, User u, UUID userId,
			Map<UUID, MonsterForUser> existingUserMonsters,
			Map<UUID, MonsterEnhancingForUser> alreadyEnhancing,
			Map<UUID, MonsterHealingForUser> alreadyHealing,
			Map<UUID, UserEnhancementItemProto> deleteMap,
			Map<UUID, UserEnhancementItemProto> updateMap,
			Map<UUID, UserEnhancementItemProto> newMap, int gemsSpent, int cashChange) {
		if (null == u ) {
			log.error("unexpected error: user is null. user=" + u + "\t deleteMap="+ deleteMap +
					"\t updateMap=" + updateMap + "\t newMap=" + newMap);
			return false;
		}
		//NOTE: RETAIN CASES ONLY FILTER THINGS, AND NOT CAUSE THIS REQUEST TO FAIL
		//retain only the userMonsters in deleteMap and updateMap that are in enhancing
		boolean keepThingsInDomain = true;
		boolean keepThingsNotInDomain = false;
		Set<UUID> alreadyEnhancingIds = alreadyEnhancing.keySet();
		if (null != deleteMap && !deleteMap.isEmpty()) {
			getMonsterStuffUtils().retainValidMonsters(alreadyEnhancingIds, deleteMap,
					keepThingsInDomain, keepThingsNotInDomain);
		}

		if (null != updateMap && !updateMap.isEmpty()) {
			getMonsterStuffUtils().retainValidMonsters(alreadyEnhancingIds, updateMap,
					keepThingsInDomain, keepThingsNotInDomain);
		}

		if (null != newMap && !newMap.isEmpty()) {
			//retain only the userMonsters in newMap that are in the db
			Set<UUID> existingIds = existingUserMonsters.keySet();
			getMonsterStuffUtils().retainValidMonsters(existingIds, newMap,
					keepThingsInDomain, keepThingsNotInDomain);

			//retain only the userMonsters in newMap that are not in healing
			keepThingsInDomain = false;
			keepThingsNotInDomain = true;
			Set<UUID> alreadyHealingIds = alreadyHealing.keySet();
			getMonsterStuffUtils().retainValidMonsters(alreadyHealingIds, newMap,
					keepThingsInDomain, keepThingsNotInDomain);
		}

		//CHECK MONEY
		if (!hasEnoughGems(resBuilder, u, gemsSpent, cashChange, deleteMap, updateMap, newMap)) {
			return false;
		}

		if (!hasEnoughCash(resBuilder, u, cashChange, deleteMap, updateMap, newMap)) {
			return false;
		}

		return true;
	}
	
	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent,
			int cashChange, Map<UUID, UserEnhancementItemProto> deleteMap,
			Map<UUID, UserEnhancementItemProto> updateMap,
			Map<UUID, UserEnhancementItemProto> newMap) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("user error: user does not have enough gems. userGems=" + userGems +
					"\t gemsSpent=" + gemsSpent + "\t deleteMap=" + deleteMap + "\t newMap=" +
					newMap + "\t updateMap=" + updateMap + "\t cashChange=" + cashChange +
					"\t user=" + u);
			resBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}

	private boolean hasEnoughCash(Builder resBuilder, User u, int cashChange,
			Map<UUID, UserEnhancementItemProto> deleteMap,
			Map<UUID, UserEnhancementItemProto> updateMap,
			Map<UUID, UserEnhancementItemProto> newMap) {
		int userCash = u.getCash(); 
		//positive 'cashChange' means refund, negative means charge user
		int cost = -1 * cashChange;
		if (userCash < cost) {
			log.error("user error: user does not have enough cash. userCash=" + userCash +
					"\t cost=" + cost + "\t deleteMap=" + deleteMap + "\t newMap=" +
					newMap + "\t updateMap=" + updateMap + "\t user=" + u);
			resBuilder.setStatus(SubmitMonsterEnhancementStatus.FAIL_INSUFFICIENT_CASH);
			return false;
		}
		return true;
	}
	

	private boolean writeChangesToDb(User user, UUID uId, int gemsSpent,
			int cashChange, Map<UUID, UserEnhancementItemProto> protoDeleteMap,
			  Map<UUID, UserEnhancementItemProto> protoUpdateMap,
			  Map<UUID, UserEnhancementItemProto> protoNewMap, Date clientTime) {
		try {
			//CHARGE THE USER
			int gemChange = -1 * gemsSpent;
			int newCash = cashChange + user.getCash();
			int newGems = gemChange + user.getGems();
			//create history first
			List<UserCurrencyHistory> uchList = createCurrencyHistory(user, clientTime,
					cashChange, gemChange, protoDeleteMap, protoUpdateMap, protoUpdateMap);
			user.setCash(newCash);
			user.setGems(newGems);
			getUserService().saveUser(user);
			
			//keep track of currency stuff
			if (!uchList.isEmpty()) {
				getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
			}

			//delete everything left in the map, if there are any
			if (!protoDeleteMap.isEmpty()) {
				List<UUID> deleteIds = new ArrayList<UUID>(protoDeleteMap.keySet());
				getMonsterEnhancingForUserService().deleteUserMonstersEnhancing(deleteIds);
				log.info("deleted monster enhancing rows. protoDeleteMap=" + protoDeleteMap);
			}
			
			//convert protos to java counterparts
			List<MonsterEnhancingForUser> updateMap = getMonsterStuffUtils()
					.convertToMonsterEnhancingForUser(uId, protoUpdateMap);
			log.info("updateMap=" + updateMap);
			List<MonsterEnhancingForUser> newMap = getMonsterStuffUtils()
					.convertToMonsterEnhancingForUser(uId, protoNewMap);
			log.info("newMap=" + newMap);

			List<MonsterEnhancingForUser> updateAndNew = new ArrayList<MonsterEnhancingForUser>();
			updateAndNew.addAll(updateMap);
			updateAndNew.addAll(newMap);
			//update everything in enhancing table that is in "update" and "new" map
			getMonsterEnhancingForUserService().saveUserMonsters(updateAndNew);
			
			return true;
		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	private List<UserCurrencyHistory> createCurrencyHistory(User aUser, Date date,
			int cashChange, int gemChange,
			Map<UUID, UserEnhancementItemProto> protoDeleteMap,
			Map<UUID, UserEnhancementItemProto> protoUpdateMap,
			Map<UUID, UserEnhancementItemProto> protoNewMap) {
		String cashStr = MobstersDbTables.USER__CASH;
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__ENHANCING;
		StringBuilder sb = new StringBuilder();
		
		//maybe shouldn't keep track...oh well, more info hopefully is better than none
		if (null != protoDeleteMap && !protoDeleteMap.isEmpty()) {
			sb.append("deleteIds=");
			for (UserEnhancementItemProto ueip : protoDeleteMap.values()) {
				String id = ueip.getUserMonsterUuid();
				sb.append(id);
				sb.append(" ");
			}
		}
		if (null != protoUpdateMap && !protoUpdateMap.isEmpty()) {
			sb.append("updateIds=");
			for (UserEnhancementItemProto ueip : protoUpdateMap.values()) {
				String id = ueip.getUserMonsterUuid();
				sb.append(id);
				sb.append(" ");
			}
		}
		if (null != protoNewMap && !protoNewMap.isEmpty()) {
			sb.append("newIds=");
			for (UserEnhancementItemProto ueip : protoNewMap.values()) {
				String id = ueip.getUserMonsterUuid();
				sb.append(id);
				sb.append(" ");
			}
		}
		
		String details = sb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory cash = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, date, cashStr, cashChange,
						reasonForChange, details, saveToDb);
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, date, gemsStr, gemChange,
						reasonForChange, details, saveToDb);

		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != cash) {
			uchList.add(cash);
		}
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public MonsterEnhancingForUserService getMonsterEnhancingForUserService() {
		return monsterEnhancingForUserService;
	}

	public void setMonsterEnhancingForUserService(
			MonsterEnhancingForUserService monsterEnhancingForUserService) {
		this.monsterEnhancingForUserService = monsterEnhancingForUserService;
	}

	public MonsterHealingForUserService getMonsterHealingForUserService() {
		return monsterHealingForUserService;
	}

	public void setMonsterHealingForUserService(
			MonsterHealingForUserService monsterHealingForUserService) {
		this.monsterHealingForUserService = monsterHealingForUserService;
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

	public CreateEventProtoUtils getCreateEventProtoUtils() {
		return createEventProtoUtils;
	}

	public void setCreateEventProtoUtils(CreateEventProtoUtils createEventProtoUtils) {
		this.createEventProtoUtils = createEventProtoUtils;
	}
	
}
