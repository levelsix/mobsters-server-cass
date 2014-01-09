package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterResponseProto.HealMonsterStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.HealMonsterRequestEvent;
import com.lvl6.mobsters.events.response.HealMonsterResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterHealingProto;
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
import com.lvl6.mobsters.services.monsterhealingforuser.MonsterHealingHistoryService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class HealMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected MonsterStuffUtil monsterStuffUtil;
	
	@Autowired
	protected UserService userService;

	@Autowired
	protected MonsterHealingForUserService monsterHealingForUserService;
	
	@Autowired
	protected MonsterEnhancingForUserService monsterEnhancingForUserService;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	@Autowired
	protected MonsterHealingHistoryService monsterHealingHistoryService;
	
	@Autowired
	protected MiscUtil miscUtil;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new HealMonsterRequestEvent();
	}
	

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_HEAL_MONSTER_EVENT_VALUE;
	}
	
	
	

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		HealMonsterRequestProto reqProto = 
				((HealMonsterRequestEvent) event).getHealMonsterRequestProto();

		//get the values client sent
		//get values sent from the client (the request proto)
	    MinimumUserProto senderProto = reqProto.getSender();
	    String userIdString = senderProto.getUserUuid();
	    List<UserMonsterHealingProto> umhDelete = reqProto.getUmhDeleteList();
	    List<UserMonsterHealingProto> umhUpdate = reqProto.getUmhUpdateList();
	    List<UserMonsterHealingProto> umhNew = reqProto.getUmhNewList();
	    //positive means refund, negative means charge user
	    int cashChange = reqProto.getCashChange();
	    int gemCostForHealing = reqProto.getGemCostForHealing();
	    
	    //stuff from HealMonsterWaitTimeCompleteController
		boolean isSpeedup = reqProto.getIsSpeedup();
		int gemsForSpeedup = reqProto.getGemsForSpeedup();
		List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();
		//will be populated by MonsterStuffUtil.getUserMonsterIds()
		Map<UUID, Integer> userMonsterIdToExpectedHealth = new HashMap<UUID, Integer>();
		//converts protos to a map and also returns a list of user monster ids
		List<UUID> userMonsterIds = getMonsterStuffUtil()
				.getUserMonsterIds(umchpList, userMonsterIdToExpectedHealth);

	    int totalGemCost = reqProto.getTotalGemCost();
	    Date clientDate = new Date();
	    
	    Map<UUID, UserMonsterHealingProto> deleteMap = getMonsterStuffUtil()
	    		.convertIntoUserMonsterIdToUmhpProtoMap(umhDelete);
	    Map<UUID, UserMonsterHealingProto> updateMap = getMonsterStuffUtil()
	    		.convertIntoUserMonsterIdToUmhpProtoMap(umhUpdate);
	    Map<UUID, UserMonsterHealingProto> newMap = getMonsterStuffUtil()
	    		.convertIntoUserMonsterIdToUmhpProtoMap(umhNew); 
	    
		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = HealMonsterResponseProto.newBuilder();
		responseBuilder.setStatus(HealMonsterStatus.FAIL_OTHER);
		HealMonsterResponseEvent resEvent =
				new HealMonsterResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			Map<UUID, MonsterHealingForUser> alreadyHealing =
	    			getMonsterHealingForUserService().getMonstersHealingForUser(userId);
	    	Map<UUID, MonsterEnhancingForUser> alreadyEnhancing =
						getMonsterEnhancingForUserService().getMonstersEnhancingForUser(userId);
	    	
	    	//retrieve only the new monsters that will be healed
	    	//also get the ones that correspond to the ones that are going to be deleted
	    	//(for history purposes)
	    	Map<UUID, MonsterForUser> existingUserMonsters = new HashMap<UUID, MonsterForUser>();
	    	if (null != newMap && !newMap.isEmpty()) {
	    		Set<UUID> newIds = new HashSet<UUID>();
	    		newIds.addAll(newMap.keySet());
	    		newIds.addAll(deleteMap.keySet());
	    		
	    		//since incorporating HealMonsterWaitTimeCompleteController also get
	    		//the ones that correspond to the list "userMonsterIds"
	    		newIds.addAll(userMonsterIds);
	    		
	    		existingUserMonsters = getMonsterForUserService()
	    				.getSpecificOrAllUserMonstersForUser(userId, newIds);
	    	}
	    	
			//validate request
	    	//for the delete, update, new maps and userMonsterIds, only the valid entries
	    	//will be retained
			boolean validRequest = checkLegit(responseBuilder, aUser, userId,
		      		cashChange, totalGemCost, existingUserMonsters, alreadyHealing,
		      		alreadyEnhancing, deleteMap, updateMap, newMap, userMonsterIds);


			boolean successful = false;
			if (validRequest) {
				//modify map(userMonsterIds -> expected healths) to contain only valid
				//mappings (valid mappings determined by the list "userMonsterIds")
				userMonsterIdToExpectedHealth = getMiscUtil().getValidMapEntries(
						userMonsterIds, userMonsterIdToExpectedHealth);
				successful = writeChangesToDb(aUser, userId, cashChange, totalGemCost,
						clientDate, alreadyHealing, deleteMap, updateMap, newMap,
						userMonsterIds, userMonsterIdToExpectedHealth, existingUserMonsters,
						gemCostForHealing, isSpeedup, gemsForSpeedup);
			}

			if (successful) {
				responseBuilder.setStatus(HealMonsterStatus.SUCCESS);
			}

			//write to client
			resEvent.setHealMonsterResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

			if (successful) {
				//since modified user's resources need to send update client user event
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate);
				
				//the deleteMfuIds contain ids of monster healings that have been cancelled
				Collection<UUID> deletedMfuIds = deleteMap.keySet();
				
				
				writeChangesToHistory(userId, clientDate, alreadyHealing,
						deletedMfuIds, existingUserMonsters, userMonsterIds);
			}
		} catch (Exception e) {
			log.error("exception in HealMonsterController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(HealMonsterStatus.FAIL_OTHER);
				resEvent.setHealMonsterResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in HealMonsterController processRequestEvent", e2);
			}
		}
	}
	

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value. delete, update, new maps
	 * MIGHT BE MODIFIED.
	 * 
	 * from HealMonsterWaitTimeComplete controller logic
	 * @healedUp MIGHT ALSO BE MODIFIED.
	 * 
	 * For the most part, will always return success. Why?
	 * (Will return fail if user does not have enough funds.) 
	 * Answer: For the map
	 * 
	 * delete - The monsters to be removed from healing will only be the ones
	 * the user already has in healing.
	 * update - Same logic as above.
	 * new - Same as above.
	 * 
	 * Ex. If user wants to delete a monster, 'A', that isn't healing, along with some
	 * monsters already healing, 'B', i.e. wants to delete (A, B), then only the valid
	 * monster(s), 'B', will be deleted. Same logic with update and new. 
	 * 
	 */
	private boolean checkLegit(Builder resBuilder, User u, UUID userId, int cashChange,
			int gemCost, Map<UUID, MonsterForUser> existingUserMonsters,
			Map<UUID, MonsterHealingForUser> alreadyHealing,
			Map<UUID, MonsterEnhancingForUser> alreadyEnhancing,
			Map<UUID, UserMonsterHealingProto> deleteMap,
			Map<UUID, UserMonsterHealingProto> updateMap,
			Map<UUID, UserMonsterHealingProto> newMap, List<UUID> healedUp) {
		if (null == u ) {
			log.error("unexpected error: user is null. user=" + u + "\t deleteMap="+ deleteMap +
					"\t updateMap=" + updateMap + "\t newMap=" + newMap);
			return false;
		}

		//CHECK MONEY
		int userGems = u.getGems();
		if (gemCost > userGems) {
			log.error("user error: user does not have enough gems. userGems="
					+ userGems + "\t gemCost=" + gemCost + "\t user=" + u);
			resBuilder.setStatus(HealMonsterStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}

		// scenario can be user has insufficient cash but has enough
		// gems to cover the difference
		int userCash = u.getCash();
		int cashCost = -1 * cashChange;
		if (cashCost > userCash && gemCost == 0) {
			//user doesn't have enough cash and is not paying gems.

			log.error("user error: user has too little cash and not using gems. userCash="
					+ userCash + "\t cashCost=" + cashCost + "\t user=" + u);
			resBuilder.setStatus(HealMonsterStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}
		//if user has insufficient cash but gems is nonzero, take it on full faith
		//client calculated things correctly

		//retain only the userMonsters, the client sent, that are in healing
		boolean keepThingsInDomain = true;
		boolean keepThingsNotInDomain = false;
		Set<UUID> alreadyHealingIds = alreadyHealing.keySet();
		getMiscUtil().retainValidMapEntries(alreadyHealingIds, deleteMap,
				keepThingsInDomain, keepThingsNotInDomain);
		getMiscUtil().retainValidMapEntries(alreadyHealingIds, updateMap,
				keepThingsInDomain, keepThingsNotInDomain);

		//retain only the userMonsters, the client sent, that are in the db
		Set<UUID> existingIds = existingUserMonsters.keySet();
		getMiscUtil().retainValidMapEntries(existingIds, newMap,
				keepThingsInDomain, keepThingsNotInDomain);

		//retain only the userMonsters, the client sent, that are not in enhancing
		keepThingsInDomain = false;
		keepThingsNotInDomain = true;
		Set<UUID> alreadyEnhancingIds = alreadyEnhancing.keySet();
		getMiscUtil().retainValidMapEntries(alreadyEnhancingIds, newMap,
				keepThingsInDomain, keepThingsNotInDomain);

		
		log.info("alreadyHealing=" + alreadyHealing);
		//don't really need to do empty-check, but eh
		if (!healedUp.isEmpty()) {
			//FROM HealMonsterWaitTimeComplete CONTROLLER
			//modify healedUp to contain only those that exist
			getMiscUtil().retainValidListEntries(alreadyHealingIds, healedUp);
		}
		
		resBuilder.setStatus(HealMonsterStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User user, UUID uId, int cashChange, int gemCost,
			Date clientDate, Map<UUID, MonsterHealingForUser> alreadyHealing, 
			Map<UUID, UserMonsterHealingProto> protoDeleteMap,
			Map<UUID, UserMonsterHealingProto> protoUpdateMap,
			Map<UUID, UserMonsterHealingProto> protoNewMap, List<UUID> userMonsterIds,
			Map<UUID, Integer> userMonsterIdsToHealths,
			Map<UUID, MonsterForUser> idsToUserMonsters, int gemCostForHealing, 
			boolean isSpeedup, int gemsForSpeedup) {

		try {
			//CHARGE THE USER
			int gemChange = -1 * gemCost;
			//create history first
			List<UserCurrencyHistory> uchList = createCurrencyHistory(user, clientDate,
					cashChange, gemChange, protoDeleteMap, protoUpdateMap, protoUpdateMap,
					gemCostForHealing, isSpeedup, gemsForSpeedup, userMonsterIds,
					userMonsterIdsToHealths, idsToUserMonsters);
			int oilChange = 0;
			getUserService().updateUserResources(user, gemChange, oilChange, cashChange);

			
			//keep track of currency stuff
			if (!uchList.isEmpty()) {
				getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
			}

			//delete the selected monsters from  the healing table, if there are
			//any to delete
			if (!protoDeleteMap.isEmpty()) {
				List<UUID> deleteIds = new ArrayList<UUID>(protoDeleteMap.keySet());
				getMonsterHealingForUserService().deleteUserMonstersHealing(deleteIds);
				log.info("deleted monster healing rows. idsDeleted=" + deleteIds +
						"\t protoDeleteMap=" + protoDeleteMap + "\t alreadyHealing=" +
						alreadyHealing);
			}

			//convert protos to java counterparts
			List<MonsterHealingForUser> updateList = getMonsterStuffUtil()
					.convertToMonsterHealingForUser(uId, alreadyHealing, protoUpdateMap);
			List<MonsterHealingForUser> newList = getMonsterStuffUtil()
					.convertToMonsterHealingForUser(uId, alreadyHealing, protoNewMap);

			List<MonsterHealingForUser> updateAndNew = new ArrayList<MonsterHealingForUser>();
			updateAndNew.addAll(updateList);
			updateAndNew.addAll(newList);

			//client could have deleted one item from two item queue, or added at least one item
			if (!updateAndNew.isEmpty()) {
				//update and insert the new monsters
				getMonsterHealingForUserService().saveUserMonsterHealing(updateAndNew);
				log.info("updated monster healing rows. monsters updated/inserted=" +
						updateAndNew);
			}

			//don't unequip the monsters
			//		  //for the new monsters, ensure that the monsters are "unequipped"
			//		  if (!protoNewMap.isEmpty()) {
			//		  	//for the new monsters, set the teamSlotNum to 0
			//		  	int size = protoNewMap.size();
			//		  	List<Long> userMonsterIdList = new ArrayList<Long>(protoNewMap.keySet());
			//		  	List<Integer> teamSlotNumList = Collections.nCopies(size, 0);
			//		  	num = UpdateUtils.get().updateNullifyUserMonstersTeamSlotNum(userMonsterIdList, teamSlotNumList);
			//		  	log.info("updated user monster rows. numUpdated=" + num);
			//		  }
			
			
			//LOGIC FROM HealMonsterWaitTimeCompleteController
			//heal the monsters
			if (null != userMonsterIdsToHealths && !userMonsterIdsToHealths.isEmpty()) {
				getMonsterHealingForUserService().healUserMonsters(userMonsterIdsToHealths,
						idsToUserMonsters);
			}
			
			return true;
		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	
	private List<UserCurrencyHistory> createCurrencyHistory(User aUser, Date date,
			int cashChange, int gemChange,
			Map<UUID, UserMonsterHealingProto> protoDeleteMap,
			Map<UUID, UserMonsterHealingProto> protoUpdateMap,
			Map<UUID, UserMonsterHealingProto> protoNewMap,  int gemCostForHealing, 
			boolean isSpeedup, int gemsForSpeedup, List<UUID> userMonsterIds,
			Map<UUID, Integer> userMonsterIdsToHealths,
			Map<UUID, MonsterForUser> idsToUserMonsters) {
		String cashStr = MobstersDbTables.USER__CASH;
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__HEAL_MONSTER_OR_SPED_UP_HEALING;
		StringBuilder detailSb = new StringBuilder();
		
		if (0 != gemCostForHealing || 0 != cashChange) {
			detailSb.append("heal monster info: ");
			detailSb.append("gemChange=");
			detailSb.append(-1*gemCostForHealing);
			detailSb.append(" cashChange=");
			detailSb.append(cashChange);
			detailSb.append(" ");
		}

		//maybe shouldn't keep track...oh well, more info hopefully is better than none
		if (null != protoDeleteMap && !protoDeleteMap.isEmpty()) {
			detailSb.append("deleteIds=");
			Collection<UUID> deleteIds = protoDeleteMap.keySet();
			String deleteIdsStr = getQueryConstructionUtil().implode(deleteIds, " ");
			detailSb.append(deleteIdsStr);
			detailSb.append(" ");
		}
		if (null != protoUpdateMap && !protoUpdateMap.isEmpty()) {
			detailSb.append("updateIds=");
			Collection<UUID> updateIds = protoUpdateMap.keySet();
			String updateIdsStr = getQueryConstructionUtil().implode(updateIds, " ");
			detailSb.append(updateIdsStr);
			detailSb.append(" ");
		}
		if (null != protoNewMap && !protoNewMap.isEmpty()) {
			detailSb.append("newIds=");
			Collection<UUID> newIds = protoNewMap.keySet();
			String newIdsStr = getQueryConstructionUtil().implode(newIds, " ");
			detailSb.append(newIdsStr);
			detailSb.append(" ");
		}
		
		//merging currency tracking from HealMonsterWaitTimeCompleteController 
		if (isSpeedup) {
			detailSb.append("sped up healing info: ");
			detailSb.append("gemChange=");
			detailSb.append(-1*gemsForSpeedup);
			detailSb.append(" (prev,curHp):");

			//maybe shouldn't keep track...oh well, more info hopefully is better than none
			for (UUID id : userMonsterIds) {
				MonsterForUser mfu = idsToUserMonsters.get(id);
				int prevHp = mfu.getCurrentHealth();
				int curHp = userMonsterIdsToHealths.get(id);

				detailSb.append("(");
				detailSb.append(prevHp);
				detailSb.append(",");
				detailSb.append(curHp);
				detailSb.append(") ");
			}
		}

		String details = detailSb.toString();

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
	
	//TODO: COMBINE THESE TWO DB CALLS INTO ONE
	private void writeChangesToHistory(UUID uId, Date now, 
			Map<UUID, MonsterHealingForUser> inHealing, Collection<UUID> deletedMfuIds,
			Map<UUID, MonsterForUser> idsToUserMonsters, List<UUID> finishedMfuIds) {
		try {
			//for now (12/28/13) for the monsters that are deleted don't record them
			boolean healingCancelled = true;
			
			Map<UUID, Integer> prevHps = getMonsterStuffUtil()
					.getHealths(deletedMfuIds, idsToUserMonsters);
			
			getMonsterHealingHistoryService().insertHealingHistory(uId, now,
					prevHps, inHealing, deletedMfuIds, idsToUserMonsters,
					healingCancelled);
			
			//logic from HealMonsterWaitTimeCompleteController
			prevHps = getMonsterStuffUtil().getHealths(finishedMfuIds, idsToUserMonsters);
			
			//don't know why HealMonsterWaitTimeCompleteController has cancelled=true
			healingCancelled = false;
			getMonsterHealingHistoryService().insertHealingHistory(uId, now,
					prevHps, inHealing, finishedMfuIds, idsToUserMonsters,
					healingCancelled);
			
		} catch (Exception e) {
			log.error("unexpected error: problem with saving history to db.", e);
		}
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

	public MonsterEnhancingForUserService getMonsterEnhancingForUserService() {
		return monsterEnhancingForUserService;
	}

	public void setMonsterEnhancingForUserService(
			MonsterEnhancingForUserService monsterEnhancingForUserService) {
		this.monsterEnhancingForUserService = monsterEnhancingForUserService;
	}

	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}

	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}

	public MonsterHealingHistoryService getMonsterHealingHistoryService() {
		return monsterHealingHistoryService;
	}

	public void setMonsterHealingHistoryService(
			MonsterHealingHistoryService monsterHealingHistoryService) {
		this.monsterHealingHistoryService = monsterHealingHistoryService;
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

	public MiscUtil getMiscUtil() {
		return miscUtil;
	}

	public void setMiscUtil(MiscUtil miscUtil) {
		this.miscUtil = miscUtil;
	}
	
}
