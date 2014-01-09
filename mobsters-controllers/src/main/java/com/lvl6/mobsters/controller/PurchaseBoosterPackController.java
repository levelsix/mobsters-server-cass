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

import com.lvl6.mobsters.controller.utils.BoosterPackStuffUtils;
import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.entitymanager.staticdata.utils.BoosterItemRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.BoosterPackRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventBoosterPackProto.PurchaseBoosterPackRequestProto;
import com.lvl6.mobsters.eventprotos.EventBoosterPackProto.PurchaseBoosterPackResponseProto;
import com.lvl6.mobsters.eventprotos.EventBoosterPackProto.PurchaseBoosterPackResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventBoosterPackProto.PurchaseBoosterPackResponseProto.PurchaseBoosterPackStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.PurchaseBoosterPackRequestEvent;
import com.lvl6.mobsters.events.response.PurchaseBoosterPackResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.po.staticdata.BoosterItem;
import com.lvl6.mobsters.po.staticdata.BoosterPack;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class PurchaseBoosterPackController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected BoosterPackRetrieveUtils boosterPackRetrieveUtils;
	
	@Autowired
	protected BoosterItemRetrieveUtils boosterItemRetrieveUtils;
	
	@Autowired
	protected BoosterPackStuffUtils boosterPackStuffUtils;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new PurchaseBoosterPackRequestEvent();
	}
	
	

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_ENABLE_APNS_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		PurchaseBoosterPackRequestProto reqProto = 
				((PurchaseBoosterPackRequestEvent) event).getPurchaseBoosterPackRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
	    int boosterPackId = reqProto.getBoosterPackId();
	    Date now = new Date(reqProto.getClientTime());
	    
	    
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = PurchaseBoosterPackResponseProto.newBuilder();
		responseBuilder.setSender(senderProto);
		responseBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
		PurchaseBoosterPackResponseEvent resEvent = new PurchaseBoosterPackResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);
			BoosterPack aPack = getBoosterPackRetrieveUtils().getBoosterPackForBoosterPackId(boosterPackId);
			int gemPrice = aPack.getGemPrice();
			Map<Integer, BoosterItem> boosterItemIdsToBoosterItems = getBoosterItemRetrieveUtils()
					.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);

			List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
			int gemReward = 0;
			boolean legit = checkLegitPurchase(responseBuilder, user, userId, now, aPack,
					boosterPackId, gemPrice, boosterItemIdsToBoosterItems);
			
			boolean successful = false;
			if (legit) {
				int numBoosterItemsUserWants = 1;
				log.info("determining the booster items the user receives.");
				itemsUserReceives = getBoosterPackStuffUtils().determineBoosterItemsUserReceives(
						numBoosterItemsUserWants, boosterItemIdsToBoosterItems);
				gemReward = getBoosterPackStuffUtils().determineGemReward(itemsUserReceives);
				
				//set the FullUserMonsterProtos (in responseBuilder) to send to the client
		        successful = writeChangesToDB(responseBuilder, user, boosterPackId,
		        		itemsUserReceives, gemPrice, now, gemReward);
			}
			
			if (successful) {
				responseBuilder.setStatus(PurchaseBoosterPackStatus.SUCCESS);
			}
			
			//write to client
			resEvent.setPurchaseBoosterPackResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				UpdateClientUserResponseEvent update = getCreateEventProtoUtils()
						.createUpdateClientUserResponseEvent(user);
				getEventWriter().handleEvent(update);
			}

		} catch (Exception e) {
			log.error("exception in PurchaseBoosterPackController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
				resEvent.setPurchaseBoosterPackResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in PurchaseBoosterPackController processRequestEvent", e2);
			}
		}
	}

	private boolean checkLegitPurchase(Builder resBuilder, User aUser, UUID userId, 
			Date now, BoosterPack aPack, int boosterPackId, int gemPrice,
			Map<Integer, BoosterItem> idsToBoosterItems) {

		if (null == aUser || null == aPack || null == idsToBoosterItems ||
				idsToBoosterItems.isEmpty()) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no user for id: " + userId + ", or no BoosterPack for id: " +
					boosterPackId + ", or no booster items for BoosterPack id. items=" +
					idsToBoosterItems);
			return false;
		}

		int userGems = aUser.getGems();
		//check if user can afford to buy however many more user wants to buy
		if (userGems < gemPrice) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_INSUFFICIENT_GEMS);
			return false; //resBuilder status set in called function 
		}

		resBuilder.setStatus(PurchaseBoosterPackStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder, User user, int bPackId,
			List<BoosterItem> itemsUserReceives, int gemPrice, Date now, int gemReward) {
		try {
			//update user, then user monsters
			int gemChange = -1 * gemPrice; //make negative
			gemChange += gemReward;

			//keep track of user currency
			List<UserCurrencyHistory> uchList = createCurrencyHistory(user, now, gemChange,
					bPackId, itemsUserReceives);

			//update user's money
			if (0 != gemChange) {
				int oilChange = 0;
				int cashChange = 0;
				getUserService().updateUserResources(user, gemChange, oilChange, cashChange);
			}
			//record user currency
			if (!uchList.isEmpty()) {
				getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
			}
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		  
	private List<UserCurrencyHistory> createCurrencyHistory(User u, Date timeRedeemed,
			int gemChange, int bPackId, List<BoosterItem> itemsUserReceives) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__PURHCASED_BOOSTER_PACK;
		StringBuilder sb = new StringBuilder();
		sb.append("packId=");
		sb.append(bPackId);
		sb.append(" bItemId:");
		List<Integer> biIds = getBoosterPackStuffUtils().getIdsFromBoosterItems(itemsUserReceives);
		String biIdsStr = getQueryConstructionUtil().implode(biIds, ",");
		sb.append(biIdsStr);
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
	
	

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public BoosterPackRetrieveUtils getBoosterPackRetrieveUtils() {
		return boosterPackRetrieveUtils;
	}

	public void setBoosterPackRetrieveUtils(
			BoosterPackRetrieveUtils boosterPackRetrieveUtils) {
		this.boosterPackRetrieveUtils = boosterPackRetrieveUtils;
	}

	public BoosterItemRetrieveUtils getBoosterItemRetrieveUtils() {
		return boosterItemRetrieveUtils;
	}

	public void setBoosterItemRetrieveUtils(
			BoosterItemRetrieveUtils boosterItemRetrieveUtils) {
		this.boosterItemRetrieveUtils = boosterItemRetrieveUtils;
	}

	public BoosterPackStuffUtils getBoosterPackStuffUtils() {
		return boosterPackStuffUtils;
	}

	public void setBoosterPackStuffUtils(BoosterPackStuffUtils boosterPackStuffUtils) {
		this.boosterPackStuffUtils = boosterPackStuffUtils;
	}

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	public CreateEventProtoUtil getCreateEventProtoUtils() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtils(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}

}
