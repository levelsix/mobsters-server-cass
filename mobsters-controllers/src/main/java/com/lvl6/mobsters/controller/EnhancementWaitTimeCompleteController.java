package com.lvl6.mobsters.controller;

import java.util.ArrayList;
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

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.controller.utils.MiscUtil;
import com.lvl6.mobsters.controller.utils.MonsterStuffUtil;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EnhancementWaitTimeCompleteRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto.EnhancementWaitTimeCompleteStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.EnhancementWaitTimeCompleteRequestEvent;
import com.lvl6.mobsters.events.response.EnhancementWaitTimeCompleteResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterCurrentExpProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterenhancingforuser.MonsterEnhancingForUserService;
import com.lvl6.mobsters.services.monsterenhancingforuser.MonsterEnhancingHistoryService;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.monsterforuserdeleted.MonsterForUserDeletedService;
import com.lvl6.mobsters.services.monsterhealingforuser.MonsterHealingForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class EnhancementWaitTimeCompleteController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected MiscUtil miscUtil;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected MonsterStuffUtil monsterStuffUtil;

	@Autowired
	protected MonsterEnhancingForUserService monsterEnhancingForUserService;
	
	@Autowired
	protected MonsterHealingForUserService monsterHealingForUserService;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	@Autowired
	protected MonsterForUserDeletedService monsterForUserDeletedService;
	
	@Autowired
	protected MonsterEnhancingHistoryService monsterEnhancingHistoryService;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

	

	public EnhancementWaitTimeCompleteController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EnhancementWaitTimeCompleteRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		EnhancementWaitTimeCompleteRequestProto reqProto = 
				((EnhancementWaitTimeCompleteRequestEvent) event).getEnhancementWaitTimeCompleteRequestProto();
		
		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
	    String userIdString = senderProto.getUserUuid();
	    boolean isSpeedUp = reqProto.getIsSpeedup();
	    int gemsForSpeedUp = reqProto.getGemsForSpeedup();
	    UserMonsterCurrentExpProto umcep = reqProto.getUmcep();
	    //ids of monster_enhancing_for_user to delete, does not include main monster
	    List<String> userMonsterIdsThatFinished = reqProto.getUserMonsterUuidsList();
	    List<UUID> finishedUserMonsterIds = getMiscUtil()
	    		.createUUIDListFromStrings(userMonsterIdsThatFinished);
		Date clientTime = new Date();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);
		
		//response to send back to client
		Builder responseBuilder = EnhancementWaitTimeCompleteResponseProto.newBuilder();
		responseBuilder.setStatus(EnhancementWaitTimeCompleteStatus.FAIL_OTHER);
		EnhancementWaitTimeCompleteResponseEvent resEvent =
				new EnhancementWaitTimeCompleteResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			UUID umcepId = UUID.fromString(umcep.getUserMonsterUuid());
			List<UUID> userMonsterIds = new ArrayList<UUID>();
			userMonsterIds.add(umcepId);
			userMonsterIds.addAll(finishedUserMonsterIds);
			
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			Map<UUID, MonsterEnhancingForUser> userMonsterIdsToMefu = getMonsterEnhancingForUserService()
					.getMonstersEnhancingForUser(userId);
			Map<UUID, MonsterForUser> idsToUserMonsters = getMonsterForUserService()
					.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);
			
			
			//validate request
			//do check to make sure one monster has a null start time
			boolean legit = checkLegit(responseBuilder, aUser, userId, umcep, umcepId,
					idsToUserMonsters, userMonsterIdsToMefu, finishedUserMonsterIds, isSpeedUp,
					gemsForSpeedUp);

			boolean successful = false;
			MonsterForUser baseEnhancingMonster = idsToUserMonsters.get(umcepId);
			int prevExp = baseEnhancingMonster.getCurrentExp();
			if (legit) {
				successful = writeChangesToDb(aUser, userId, clientTime, umcepId, umcep,
						idsToUserMonsters, finishedUserMonsterIds, isSpeedUp, gemsForSpeedUp);
			}

			if (successful) {
				responseBuilder.setStatus(EnhancementWaitTimeCompleteStatus.SUCCESS);
			}

			//write to client
			resEvent.setEnhancementWaitTimeCompleteResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//tell the client to update user because user's funds most likely changed
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate);
				writeChangesToHistory(userId, clientTime, prevExp, userMonsterIdsToMefu,
						finishedUserMonsterIds, idsToUserMonsters, umcepId);
			}

		} catch (Exception e) {
			log.error("exception in EnhancementWaitTimeCompleteController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(EnhancementWaitTimeCompleteStatus.FAIL_OTHER);
				resEvent.setEnhancementWaitTimeCompleteResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in EnhancementWaitTimeCompleteController processRequestEvent", e2);
			}
		}
	}
	
	/**
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value.
	 * 
	 * Will return fail if user does not have enough funds. 
	 * For the most part, will always return success. Why?
	 * Answer: For @healedUp, the monsters the client thinks completed healing,
	 * only existing/valid ids will be taken off the healing queue.
	 * 
	 * Ex. Queue is (a,b,c,d). a is the base monster, b,c,d are the feeders.
	 * If user says monster (b, e) finished enhancing a, 
	 * only the valid monsters (b) will be removed from the queue, leaving (a,c,d)
	 * 
	 * @param resBuilder
	 * @param u
	 * @param userId
	 * @param idsToUserMonsters - the monsters the user has
	 * @param inEnhancing - the monsters that are in the enhancing queue
	 * @param umcep - the base monster that is updated from using up some of the feeders
	 * @param usedUpUserMonsterIds - userMonsterIds the user thinks has finished being enhanced
	 * @param speedUUp
	 * @param gemsForSpeedUp
	 * @return
	 */
	private boolean checkLegit(Builder resBuilder, User u, UUID userId,
			UserMonsterCurrentExpProto umcep, UUID umcepId,
			Map<UUID, MonsterForUser> idsToUserMonsters,
			Map<UUID, MonsterEnhancingForUser> inEnhancing,
			List<UUID> usedUpMonsterIds, boolean speedup, int gemsForSpeedup) {

		if (null == u || null == umcep || usedUpMonsterIds.isEmpty()) {
			log.error("unexpected error: user or idList is null. user=" + u +
					"\t umcep="+ umcep + "usedUpMonsterIds=" + usedUpMonsterIds +
					"\t speedup=" + speedup + "\t gemsForSpeedup=" + gemsForSpeedup);
			return false;
		}
		log.info("inEnhancing=" + inEnhancing);

		//make sure that the user monster ids that will be deleted will only be
		//the ids that exist in enhancing table
		Set<UUID> inEnhancingIds = inEnhancing.keySet();
		getMiscUtil().retainValidListEntries(inEnhancingIds, usedUpMonsterIds);

		//check to make sure the base monsterId is in enhancing
		if (!inEnhancingIds.contains(umcepId)) {
			log.error("client did not send updated base monster specifying what new exp and lvl are");
			return false;
		}

		/* NOT SURE IF THESE ARE  NECESSARY, SO DOING IT ANYWAY*/
		//check to make sure the monster being enhanced is part of the
		//user's monsters
		if (!idsToUserMonsters.containsKey(umcepId)) {
			log.error("monster being enhanced doesn't exist!. userMonsterIdBeingEnhanced=" + 
					umcepId + "\t deleteIds=" + usedUpMonsterIds + "\t inEnhancing=" +
					inEnhancing + "\t gemsForSpeedup=" + gemsForSpeedup + "\t speedup=" +
					speedup);
			return false;
		}

		//retain only the valid monster for user ids that will be deleted
		Set<UUID> existingIds = idsToUserMonsters.keySet();
		getMiscUtil().retainValidListEntries(existingIds, usedUpMonsterIds);


		//CHECK MONEY and CHECK SPEEDUP
		if (speedup) {
			int userGems = u.getGems();

			if (userGems < gemsForSpeedup) {
				log.error("user does not have enough gems to speed up enhancing.userGems=" +
						userGems + "\t cost=" + gemsForSpeedup + "\t umcep=" + umcep +
						"\t inEnhancing=" + inEnhancing + "\t deleteIds=" + usedUpMonsterIds);
				resBuilder.setStatus(EnhancementWaitTimeCompleteStatus.FAIL_INSUFFICIENT_FUNDS);
				return false;
			}
		}

		resBuilder.setStatus(EnhancementWaitTimeCompleteStatus.SUCCESS);
		return true;
	}
	
	private boolean writeChangesToDb(User aUser, UUID uId, Date clientTime, UUID umcepId,
	  		UserMonsterCurrentExpProto umcep, Map<UUID, MonsterForUser> idsToUserMonsters, 
	  		List<UUID> userMonsterIds, boolean isSpeedup, int gemsForSpeedup) {
		try {
			
			if(isSpeedup) {
				//CHARGE THE USER
				int gemChange = -1 * gemsForSpeedup;
				//create history first
				List<UserCurrencyHistory> uchList = createCurrencyHistory(aUser, umcep,
						userMonsterIds, clientTime, gemChange);
				int oilChange = 0;
				int cashChange = 0;
				getUserService().updateUserResources(aUser, gemChange, oilChange, cashChange);


				//keep track of currency stuff
				if (!uchList.isEmpty()) {
					getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
				}
			}
			//give the user monster exp
			int newExp = umcep.getExpectedExperience();
			int newLvl = umcep.getExpectedLevel();
			MonsterForUser mfu = idsToUserMonsters.get(umcepId);
			mfu.setCurrentExp(newExp);
			mfu.setCurrentLvl(newLvl);
			getMonsterForUserService().saveUserMonster(mfu);
			
			return true;
		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	private List<UserCurrencyHistory> createCurrencyHistory(User aUser,
			UserMonsterCurrentExpProto umcep, List<UUID> userMonsterIds, Date clientTime,
			int gemsForSpeedup) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__SPED_UP_ENHANCING;
		StringBuilder sb = new StringBuilder();
		
		String umcepId = umcep.getUserMonsterUuid();
		sb.append("baseUserMonsterId=");
		sb.append(umcepId);
		sb.append(" ");
		
		//maybe shouldn't keep track...oh well, more info hopefully is better than none
		if (null != userMonsterIds && !userMonsterIds.isEmpty()) {
			sb.append("deleteIds=");
			String deleteIdsStr = getQueryConstructionUtil().implode(userMonsterIds, " ");
			sb.append(deleteIdsStr);
			sb.append(" ");
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
	
	private void writeChangesToHistory(UUID uId, Date deleteTime, int prevExp,
			Map<UUID, MonsterEnhancingForUser> inEnhancing, List<UUID> finishedMfuIds,
			Map<UUID, MonsterForUser> idsToUserMonsters, UUID enhancingBaseMfuId) {
		String deleteReason = MobstersTableConstants.MFUDR__ENHANCING;
		StringBuilder detailSb = new StringBuilder();
		detailSb.append("enhancing userMonsterId:");
		detailSb.append(enhancingBaseMfuId);
		String detail = detailSb.toString();

		//keep track of the userMonsters that are deleted, except the monster user is
		//enhancing
		Map<UUID, MonsterForUser> idsToUserMonstersCopy = new HashMap<UUID, MonsterForUser>();
		idsToUserMonstersCopy.putAll(idsToUserMonsters);
		idsToUserMonstersCopy.remove(enhancingBaseMfuId);
		
		Map<UUID, String> details = new HashMap<UUID, String>();
		for (UUID mfuId : idsToUserMonstersCopy.keySet()) {
			details.put(mfuId, detail);
		}
		getMonsterForUserDeletedService().createUserMonsterDeletedFromUserMonsters(
				deleteReason, details, deleteTime, idsToUserMonstersCopy);
		

		//keep track of the monsters that were used up in enhancing
		boolean enhancingCancelled = false;
		getMonsterEnhancingHistoryService().insertEnhancingHistory(uId, deleteTime,
				prevExp, inEnhancing, finishedMfuIds, idsToUserMonsters,
				enhancingBaseMfuId, enhancingCancelled);
		

		//delete the selected monsters from  the enhancing table
		List<UUID> finishedMefuIdList = getMonsterStuffUtil()
				.getMonsterEnhancingForUserIds(finishedMfuIds, inEnhancing);
		getMonsterEnhancingForUserService().deleteUserMonstersEnhancing(finishedMefuIdList);
		log.info("deleted monster healing rows. inEnhancing=" + inEnhancing +
				"\t monster_enhancing_ids deleted=" + finishedMefuIdList);


		//delete the userMonsterIds from the monster_for_user table, but don't delete
		//the monster user is enhancing
		getMonsterForUserService().deleteUserMonsters(finishedMfuIds);
		log.info("defeated monster_for_user rows. inEnhancing=" + inEnhancing);

	}
	

	public MiscUtil getMiscUtil() {
		return miscUtil;
	}

	public void setMiscUtil(MiscUtil miscUtil) {
		this.miscUtil = miscUtil;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
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

	public MonsterForUserDeletedService getMonsterForUserDeletedService() {
		return monsterForUserDeletedService;
	}

	public void setMonsterForUserDeletedService(
			MonsterForUserDeletedService monsterForUserDeletedService) {
		this.monsterForUserDeletedService = monsterForUserDeletedService;
	}

	public MonsterEnhancingHistoryService getMonsterEnhancingHistoryService() {
		return monsterEnhancingHistoryService;
	}

	public void setMonsterEnhancingHistoryService(
			MonsterEnhancingHistoryService monsterEnhancingHistoryService) {
		this.monsterEnhancingHistoryService = monsterEnhancingHistoryService;
	}

	public MonsterStuffUtil getMonsterStuffUtil() {
		return monsterStuffUtil;
	}

	public void setMonsterStuffUtil(MonsterStuffUtil monsterStuffUtil) {
		this.monsterStuffUtil = monsterStuffUtil;
	}

	public CreateEventProtoUtil getCreateEventProtoUtil() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtil(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
