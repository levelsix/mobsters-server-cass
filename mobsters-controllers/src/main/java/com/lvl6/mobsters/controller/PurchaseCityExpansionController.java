package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtils;
import com.lvl6.mobsters.eventprotos.EventCityProto.PurchaseCityExpansionRequestProto;
import com.lvl6.mobsters.eventprotos.EventCityProto.PurchaseCityExpansionResponseProto;
import com.lvl6.mobsters.eventprotos.EventCityProto.PurchaseCityExpansionResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventCityProto.PurchaseCityExpansionResponseProto.PurchaseCityExpansionStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.PurchaseCityExpansionRequestEvent;
import com.lvl6.mobsters.events.response.PurchaseCityExpansionResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.CityProto.UserCityExpansionDataProto;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.ExpansionPurchaseForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.expansionpurchaseforuser.ExpansionPurchaseForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class PurchaseCityExpansionController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


	@Autowired
	protected UserService userService;

	@Autowired
	protected ExpansionPurchaseForUserService expansionPurchaseForUserService;
	
	@Autowired
	protected CreateEventProtoUtils createEventProtoUtils;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;

	@Override
	public RequestEvent createRequestEvent() {
		return new PurchaseCityExpansionRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_PURCHASE_CITY_EXPANSION_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		PurchaseCityExpansionRequestProto reqProto = 
				((PurchaseCityExpansionRequestEvent) event).getPurchaseCityExpansionRequestProto();

		//variables client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userIdString = senderProto.getUserUuid();
		//in relation to center square (the origin 0,0)
		int xPosition = reqProto.getXPosition();
		int yPosition = reqProto.getYPosition();
		Date timeOfPurchase = new Date(reqProto.getTimeOfPurchase());
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = PurchaseCityExpansionResponseProto.newBuilder();
		responseBuilder.setStatus(PurchaseCityExpansionStatus.FAIL_OTHER);
		PurchaseCityExpansionResponseEvent resEvent =
				new PurchaseCityExpansionResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);
			List<ExpansionPurchaseForUser> expansions = getExpansionPurchaseForUserService()
					.getExpansionPurchasesForUser(userId);
			//used to calculate cost for buying expansion
			int numExpansions = 0;
			if (null != expansions) {
				numExpansions = expansions.size();
			}

			//validate request
			List<Integer> cityExpansionCostList = new ArrayList<Integer>();
			boolean legitExpansion = checkLegitExpansion(responseBuilder, timeOfPurchase,
					user, expansions, numExpansions, cityExpansionCostList);

			boolean successful = false;
			if (legitExpansion) {
				int cost = cityExpansionCostList.get(0);
				writeChangesToDb(user, timeOfPurchase, xPosition, yPosition, true,
						numExpansions, cost);
			}

			if (successful) {
				responseBuilder.setStatus(PurchaseCityExpansionStatus.SUCCESS);
			}

			//write to client
			resEvent.setPurchaseCityExpansionResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//modified user object, need to update the client to reflect this
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtils()
						.createUpdateClientUserResponseEvent(user);
				ExpansionPurchaseForUser uced = getExpansionPurchaseForUserService()
						.getSpecificExpansionPurchaseForUser(userId, xPosition, yPosition);
				UserCityExpansionDataProto ucedp = getCreateEventProtoUtils()
						.createUserCityExpansionDataProtoFromUserExpansion(uced);
				responseBuilder.setUcedp(ucedp);
				resEventUpdate.setTag(event.getTag());
			}

		} catch (Exception e) {
			log.error("exception in PurchaseCityExpansionController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(PurchaseCityExpansionStatus.FAIL_OTHER);
				resEvent.setPurchaseCityExpansionResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in PurchaseCityExpansionController processRequestEvent", e2);
			}
		}
	}

	private boolean checkLegitExpansion(Builder resBuilder, Date timeOfPurchase, User user, 
			List<ExpansionPurchaseForUser> expansions, int numOfExpansions,
			List<Integer> costList) {

		boolean isExpanding = false;
		//loop through each expansion and see if any expansions are still expanding
		if (expansions != null) {
			for(ExpansionPurchaseForUser uced : expansions) {
				if(uced.isExpanding()) {
					isExpanding = true;
					break;
				}
			}
		}

		//user cannot buy another expansion while an expansion is still being constructed
		if(isExpanding) {
			log.error("user has a current expansion going on still. expansions=" + expansions);
			resBuilder.setStatus(PurchaseCityExpansionStatus.FAIL_ALREADY_EXPANDING);
			return false;
		}

		//see if user has enough to buy next expansion
		int cost = getExpansionPurchaseForUserService().calculateExpansionCost(numOfExpansions + 1);
		if (user.getCash() < cost) {
			resBuilder.setStatus(PurchaseCityExpansionStatus.FAIL_NOT_ENOUGH_COINS);
			return false;            
		}

		costList.add(cost);
		resBuilder.setStatus(PurchaseCityExpansionStatus.SUCCESS);
		return true;  
	}

	private boolean writeChangesToDb(User user, Date timeOfPurchase, int xPosition,
			int yPosition, boolean isExpanding, int numOfExpansions, int cashCost) {
		try {
			//update user, then user monsters
			int cashChange = -1 * cashCost; //make negative

			//keep track of user currency
			List<UserCurrencyHistory> uchList = createCurrencyHistory(user, xPosition,
					yPosition, timeOfPurchase, cashChange, numOfExpansions);

			//update user's money
			if (0 != cashChange) {
				int newCash = cashChange + user.getCash();
				user.setCash(newCash);
				getUserService().saveUser(user);
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

	private List<UserCurrencyHistory> createCurrencyHistory(User u, int xPosition,
			int yPosition, Date timePurchased, int cashChange, int numExpansions) {
		String cashStr = MobstersDbTables.USER__CASH;
		String reasonForChange = MobstersTableConstants.UCHRFC__PURHCASED_CITY_EXPANSION;
		StringBuilder sb = new StringBuilder();
		sb.append("(user,x,y)=(");
		sb.append(u.getId());
		sb.append(",");
		sb.append(xPosition);
		sb.append(",");
		sb.append(yPosition);
		sb.append(") curNumExpansions=");
		sb.append(numExpansions);
		
		String details = sb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, timePurchased, cashStr, cashChange,
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

	public ExpansionPurchaseForUserService getExpansionPurchaseForUserService() {
		return expansionPurchaseForUserService;
	}

	public void setExpansionPurchaseForUserService(
			ExpansionPurchaseForUserService expansionPurchaseForUserService) {
		this.expansionPurchaseForUserService = expansionPurchaseForUserService;
	}

	public CreateEventProtoUtils getCreateEventProtoUtils() {
		return createEventProtoUtils;
	}

	public void setCreateEventProtoUtils(CreateEventProtoUtils createEventProtoUtils) {
		this.createEventProtoUtils = createEventProtoUtils;
	}

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}

}
