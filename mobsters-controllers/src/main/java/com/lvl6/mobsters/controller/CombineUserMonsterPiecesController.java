package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.controller.utils.MiscUtil;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.CombineUserMonsterPiecesRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.CombineUserMonsterPiecesResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.CombineUserMonsterPiecesResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.CombineUserMonsterPiecesResponseProto.CombineUserMonsterPiecesStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.CombineUserMonsterPiecesRequestEvent;
import com.lvl6.mobsters.events.response.CombineUserMonsterPiecesResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class CombineUserMonsterPiecesController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


	@Autowired
	protected MiscUtil miscUtil;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;

	@Override
	public RequestEvent createRequestEvent() {
		return new CombineUserMonsterPiecesRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_COMBINE_USER_MONSTER_PIECES_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		CombineUserMonsterPiecesRequestProto reqProto = 
				((CombineUserMonsterPiecesRequestEvent) event).getCombineUserMonsterPiecesRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getSender();
		List<String> userMonsterIdsStrList = reqProto.getUserMonsterUuidsList();
	    List<UUID> userMonsterIds = getMiscUtil().createUUIDListFromStrings(userMonsterIdsStrList);
	    int gemCost = reqProto.getGemCost();
	    Date clientDate = new Date();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = CombineUserMonsterPiecesResponseProto.newBuilder();
		responseBuilder.setStatus(CombineUserMonsterPiecesStatus.FAIL_OTHER);
		CombineUserMonsterPiecesResponseEvent resEvent =
				new CombineUserMonsterPiecesResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			Map<UUID, MonsterForUser> idsToUserMonsters = getMonsterForUserService()
					.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);

			boolean validRequest = isValidRequest(responseBuilder, userId, aUser,
					userMonsterIds, idsToUserMonsters, gemCost);

			boolean successful = false;
			if(validRequest) {
				successful = writeChangesToDb(aUser, userMonsterIds, idsToUserMonsters,
						clientDate, gemCost);
			}

			if (successful) {
				responseBuilder.setStatus(CombineUserMonsterPiecesStatus.SUCCESS);
			}

			//write to client
			resEvent.setCombineUserMonsterPiecesResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful && gemCost > 0) {
				//since modified user's resources need to send update client user event
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in CombineUserMonsterPiecesController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(CombineUserMonsterPiecesStatus.FAIL_OTHER);
				resEvent.setCombineUserMonsterPiecesResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in CombineUserMonsterPiecesController processRequestEvent", e2);
			}
		}
	}

	
	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value.
	 * userMonsterIds might be modified to contain only those user monsters that
	 * can be combined
	 * 
	 * Example. client gives ids (a, b, c, d). Let's say 'a' is already
	 *  completed/combined, 'b' is missing a piece, 'c' doesn't exist and 'd'
	 *  can be completed  so "userMonsterIds" will be modified to only contain
	 * 'd'
	 */
	private boolean isValidRequest(Builder responseBuilder, UUID userId, User u,
	  		List<UUID> userMonsterIds, Map<UUID, MonsterForUser> idsToUserMonsters,
	  		int gemCost) {

		if (null == u) {
	  		log.error("user is null. no user exists with id=" + userId + "");
	  		return false;
	  	}
	  	if (null == userMonsterIds || userMonsterIds.isEmpty() ||
	  			idsToUserMonsters.isEmpty()) {
	  		log.error("no user monsters exist. userMonsterIds=" + userMonsterIds +
	  				"\t idsToUserMonsters=" + idsToUserMonsters);
	  		return false;
	  	}
	  	
	  	//only complete the user monsters that exist
	  	if (userMonsterIds.size() != idsToUserMonsters.size()) {
	  		log.warn("not all monster_for_user_ids exist. userMonsterIds=" + userMonsterIds +
	  				"\t idsToUserMonsters=" + idsToUserMonsters + "\t. Will continue processing");

	  		//retaining only the user monster ids that exist
	  		userMonsterIds.clear();
	  		userMonsterIds.addAll(idsToUserMonsters.keySet());
	  	}

	  	List<UUID> wholeUserMonsterIds = getMonsterForUserService()
	  			.selectWholeButNotCombinedUserMonsters(idsToUserMonsters);
	  	if (wholeUserMonsterIds.size() != userMonsterIds.size()) {
	  		log.warn("client trying to combine already combined or incomplete monsters." +
	  				" clientSentIds=" + userMonsterIds + "\t wholeButIncompleteMonsterIds=" +
	  				wholeUserMonsterIds + "\t idsToUserMonsters=" + idsToUserMonsters +
	  				"\t Will continue processing");
	  		
	  		//retaining only user monsters that have all pieces but are incomplete
	  		userMonsterIds.clear();
	  		userMonsterIds.addAll(wholeUserMonsterIds);
	  	}
	  	
	  	if (gemCost > 0 && userMonsterIds.size() > 1) {
	  		//user speeding up combining multiple monsters, can only speed up one
	  		log.error("user speeding up combining pieces for multiple monsters can only " +
	  				"speed up one monster. gemCost=" + gemCost + "\t userMonsterIds=" + userMonsterIds);
	  		responseBuilder.setStatus(CombineUserMonsterPiecesStatus
	  				.FAIL_MORE_THAN_ONE_MONSTER_FOR_SPEEDUP);
	  		return false;
	  	}
	  	
	  	//check user gems
	  	int userGems = u.getGems();
	  	if (userGems < gemCost) {
	  		log.error("user doesn't have enough gems to speed up combining. userGems=" +
	  				userGems + "\t gemCost=" + gemCost + "\t userMonsterIds=" + userMonsterIds);
	  		responseBuilder.setStatus(CombineUserMonsterPiecesStatus.FAIL_INSUFFUCIENT_GEMS);
	  		return false;
	  	}
	  	
		return true;
	}


	private boolean writeChangesToDb(User aUser, List<UUID> userMonsterIds,
			Map<UUID, MonsterForUser> idsToUserMonsters, Date clientDate, int gemCost) {
		try {
			//if user sped up stuff, then charge him
		  	if (gemCost > 0) {
		  		int gemChange = -1 * gemCost;
		  		//create history first
		  		List<UserCurrencyHistory> uchList = createCurrencyHistory(aUser,
		  				clientDate, gemChange, userMonsterIds);
		  		int oilChange = 0;
		  		int cashChange = 0;
		  		getUserService().updateUserResources(aUser, gemChange, oilChange, cashChange);

		  		//keep track of currency stuff
		  		if (!uchList.isEmpty()) {
		  			getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
		  		}
		  	}
		  	
		  	getMonsterForUserService().updateCompleteUserMonsters(userMonsterIds,
		  			idsToUserMonsters);
		  		
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	private List<UserCurrencyHistory> createCurrencyHistory(User aUser,
			Date clientDate, int gemChange, List<UUID> userMonsterIds) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__SPED_UP_COMBINING_MONSTER;
		StringBuilder detailSb = new StringBuilder();
		detailSb.append("combined userMonsterIds:");
		detailSb.append(userMonsterIds);
		
		String details = detailSb.toString();
		boolean saveToDb = false;
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, clientDate, gemsStr, gemChange,
						reasonForChange, details, saveToDb);
		
		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
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

	public CreateEventProtoUtil getCreateEventProtoUtil() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtil(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}

}
