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
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SellUserMonsterRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SellUserMonsterResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SellUserMonsterResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SellUserMonsterResponseProto.SellUserMonsterStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.SellUserMonsterRequestEvent;
import com.lvl6.mobsters.events.response.SellUserMonsterResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.MinimumUserMonsterSellProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.monsterforuserdeleted.MonsterForUserDeletedService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class SellUserMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected MonsterStuffUtil monsterStuffUtil;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected MiscUtil miscUtil;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	@Autowired
	protected MonsterForUserDeletedService monsterForUserDeletedService;
	
	

	public SellUserMonsterController() {
		numAllocatedThreads = 4;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new SellUserMonsterRequestEvent();
	}
	
	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_SELL_USER_MONSTER_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		SellUserMonsterRequestProto reqProto = 
				((SellUserMonsterRequestEvent) event).getSellUserMonsterRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getSender();
		List<MinimumUserMonsterSellProto> userMonsters = reqProto.getSalesList();
		Map<UUID, Integer> userMonsterIdsToCashAmounts = getMonsterStuffUtil()
				.convertToMonsterForUserIdToCashAmount(userMonsters);
		Set<UUID> userMonsterIdsSet = userMonsterIdsToCashAmounts.keySet();
		List<UUID> userMonsterIds = new ArrayList<UUID>(userMonsterIdsSet);
		Date deleteDate = new Date();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = SellUserMonsterResponseProto.newBuilder();
		responseBuilder.setStatus(SellUserMonsterStatus.FAIL_OTHER);
		SellUserMonsterResponseEvent resEvent =
				new SellUserMonsterResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			Map<UUID, MonsterForUser> idsToUserMonsters =  getMonsterForUserService()
					.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, userId, aUser,
					userMonsterIds, idsToUserMonsters);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(aUser, userMonsterIds,
						userMonsterIdsToCashAmounts, idsToUserMonsters, deleteDate);
			}

			if (successful) {
				responseBuilder.setStatus(SellUserMonsterStatus.SUCCESS);
			}

			//write to client
			resEvent.setSellUserMonsterResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//since modified user's resources need to send update client user event
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate);
				
				//record why monster was deleted
				writeChangesToHistory(userId, deleteDate, userMonsterIdsToCashAmounts,
						idsToUserMonsters);
			}

		} catch (Exception e) {
			log.error("exception in SellUserMonsterController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(SellUserMonsterStatus.FAIL_OTHER);
				resEvent.setSellUserMonsterResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in SellUserMonsterController processRequestEvent", e2);
			}
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the builder
	 * status to the appropriate value. "userMonsterIds" might be modified to
	 * contain only those user monsters that exist (and hence can be sold)
	 * 
	 * Example. client gives ids (a, b, c, d). Let's say 'a,' 'b,' and 'c' don't
	 * exist but 'd' does, so "userMonsterIds" will be modified to only contain
	 * 'd'
	 */
	private boolean isValidRequest(Builder responseBuilder, UUID userId, User u,
			List<UUID> userMonsterIds, Map<UUID, MonsterForUser> idsToUserMonsters) {

		if (null == u) {
			log.error("user is null. no user exists with id=" + userId + "");
			return false;
		}
		if (null == userMonsterIds || userMonsterIds.isEmpty()
				|| idsToUserMonsters.isEmpty()) {
			log.error("no user monsters exist. userMonsterIds=" + userMonsterIds
					+ "\t idsToUserMonsters=" + idsToUserMonsters);
			return false;
		}

		// can only sell the user monsters that exist
		if (userMonsterIds.size() != idsToUserMonsters.size()) {
			log.warn("not all monster_for_user_ids exist. userMonsterIds="
					+ userMonsterIds + "\t idsToUserMonsters=" + idsToUserMonsters
					+ "\t. Will continue processing");

			// retaining only the user monster ids that exist
			userMonsterIds.clear();
			userMonsterIds.addAll(idsToUserMonsters.keySet());
		}

		return true;
	}
	
	private boolean writeChangesToDb(User aUser, List<UUID> userMonsterIds,
			Map<UUID, Integer> userMonsterIdsToCashAmounts,
			Map<UUID, MonsterForUser> idsToUserMonsters, Date clientTime) {
		try {
			// sum up the monies and give it to the user
			int cashChange = getMiscUtil().sumMapValues(userMonsterIdsToCashAmounts);
			if (0 != cashChange) {
				//create history first
				List<UserCurrencyHistory> uchList = createCurrencyHistory(aUser,
						clientTime, cashChange, userMonsterIdsToCashAmounts,
						idsToUserMonsters);
				int oilChange = 0;
				int gemChange = 0;
				getUserService().updateUserResources(aUser, gemChange, oilChange, cashChange);

				//keep track of currency stuff
				if (!uchList.isEmpty()) {
					getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
				}
			}
			
			//delete the monsters
			if (null != userMonsterIds && !userMonsterIds.isEmpty()) {
				getMonsterForUserService().deleteUserMonsters(userMonsterIds);
			}
			
			return true;

		} catch (Exception e2) {
			log.error("unexpected error: problem with saving to db.", e2);
		}
		return false;
	}
	
	private List<UserCurrencyHistory> createCurrencyHistory(User aUser, Date date,
			int cashChange, Map<UUID, Integer> userMonsterIdsToCashAmounts,
			Map<UUID, MonsterForUser> idsToUserMonsters) {
		String cashStr = MobstersDbTables.USER__CASH;
		String reasonForChange = MobstersTableConstants.UCHRFC__SOLD_USER_MONSTERS;
		StringBuilder detailSb = new StringBuilder();
		detailSb.append("(id,amount)");
		
		for (UUID mfuId : userMonsterIdsToCashAmounts.keySet()) {
			int amount = userMonsterIdsToCashAmounts.get(mfuId);
			detailSb.append("(");
			detailSb.append(mfuId);
			detailSb.append(",");
			detailSb.append(amount);
			detailSb.append(") ");
		}
		String details = detailSb.toString();
		
		boolean saveToDb = false;
		UserCurrencyHistory cash = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, date, cashStr, cashChange,
						reasonForChange, details, saveToDb);
		
		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != cash) {
			uchList.add(cash);
		}
		return uchList;
	}
	
	//FOR USER MONSTER HISTORY PURPOSES
	private void writeChangesToHistory(UUID userId, Date deleteDate,
			Map<UUID, Integer> userMonsterIdsToCashAmounts,
			Map<UUID, MonsterForUser> idsToUserMonsters) {
		
		if (null == idsToUserMonsters || idsToUserMonsters.isEmpty()) {
			return;
		}
		
		String deleteReason = MobstersTableConstants.MFUDR__SELL;
		String detail = "cashBack=";
		Map<UUID, String> details = new HashMap<UUID, String>();
		
		for (UUID mfuId : idsToUserMonsters.keySet()) {
			int amount = userMonsterIdsToCashAmounts.get(mfuId);
			StringBuilder detailAmt = new StringBuilder();
			detailAmt.append(detail);
			detailAmt.append(amount);
			
			details.put(mfuId, detailAmt.toString());
		}
		getMonsterForUserDeletedService().createUserMonsterDeletedFromUserMonsters(
				deleteReason, details, deleteDate, idsToUserMonsters);
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

	public MiscUtil getMiscUtil() {
		return miscUtil;
	}

	public void setMiscUtil(MiscUtil miscUtil) {
		this.miscUtil = miscUtil;
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

	public MonsterForUserDeletedService getMonsterForUserDeletedService() {
		return monsterForUserDeletedService;
	}

	public void setMonsterForUserDeletedService(
			MonsterForUserDeletedService monsterForUserDeletedService) {
		this.monsterForUserDeletedService = monsterForUserDeletedService;
	}
	
}
