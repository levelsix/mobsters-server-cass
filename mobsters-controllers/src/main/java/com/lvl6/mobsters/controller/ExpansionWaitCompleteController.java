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
import com.lvl6.mobsters.eventprotos.EventStructureProto.ExpansionWaitCompleteRequestProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.ExpansionWaitCompleteResponseProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.ExpansionWaitCompleteResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventStructureProto.ExpansionWaitCompleteResponseProto.ExpansionWaitCompleteStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.ExpansionWaitCompleteRequestEvent;
import com.lvl6.mobsters.events.response.ExpansionWaitCompleteResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.CityProto.UserCityExpansionDataProto;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.ExpansionPurchaseForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.po.staticdata.ExpansionCost;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.expansionpurchaseforuser.ExpansionPurchaseForUserService;
import com.lvl6.mobsters.services.time.TimeUtils;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class ExpansionWaitCompleteController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


	@Autowired
	protected UserService userService;

	@Autowired
	protected ExpansionPurchaseForUserService expansionPurchaseForUserService;
	
	@Autowired
	protected CreateEventProtoUtils createEventProtoUtils;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected TimeUtils timeUtils;

	@Override
	public RequestEvent createRequestEvent() {
		return new ExpansionWaitCompleteRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_EXPANSION_WAIT_COMPLETE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		ExpansionWaitCompleteRequestProto reqProto = 
				((ExpansionWaitCompleteRequestEvent) event).getExpansionWaitCompleteRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userIdString = senderProto.getUserUuid();
		Date clientTime = new Date(reqProto.getCurTime());
		int xPosition = reqProto.getXPosition();
		int yPosition = reqProto.getYPosition();
		boolean speedUp = reqProto.getSpeedUp();
		int gemCostToSpeedup = reqProto.getGemCostToSpeedup();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = ExpansionWaitCompleteResponseProto.newBuilder();
		responseBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_OTHER);
		ExpansionWaitCompleteResponseEvent resEvent =
				new ExpansionWaitCompleteResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);
			List<ExpansionPurchaseForUser> expansions = getExpansionPurchaseForUserService()
					.getExpansionPurchasesForUser(userId);
			ExpansionPurchaseForUser epfu = getExpansionPurchaseForUserService()
					.selectSpecificExpansion(xPosition, yPosition, expansions);

			boolean legitExpansionComplete = checkLegitExpansionComplete(user, responseBuilder,
					expansions, epfu, clientTime, speedUp, gemCostToSpeedup);

			boolean successful = false;
			if (legitExpansionComplete) {
				successful = writeChangesToDb(user, epfu, speedUp, clientTime, gemCostToSpeedup);
			}

			if (successful) {
				responseBuilder.setStatus(ExpansionWaitCompleteStatus.SUCCESS);
			}

			//write to client
			resEvent.setExpansionWaitCompleteResponseProto(responseBuilder.build());
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
			log.error("exception in ExpansionWaitCompleteController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_OTHER);
				resEvent.setExpansionWaitCompleteResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in ExpansionWaitCompleteController processRequestEvent", e2);
			}
		}
	}

	private boolean checkLegitExpansionComplete(User user, Builder resBuilder,
			List<ExpansionPurchaseForUser> epfuList, ExpansionPurchaseForUser epfu,
			Date clientTime, boolean speedup, int gemCostToSpeedup) {

		if (null == epfu) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_WAS_NOT_EXPANDING);
			log.error("unexpected error: user has no expansion pending");
			return false;
		}
		
		int nthExpansion = epfuList.size();
		ExpansionCost ec = getExpansionPurchaseForUserService().getExpansionCost(nthExpansion);
		int expansionMins = ec.getNumMinutesToExpand();
		Date expansionStartTime = epfu.getExpandStartTime();
		
		//check if user has waited long enough to complete expansion (sans using gems)
		int additionalMinsClientTime = 0;
		boolean waitedLongEnough = getTimeUtils().isFirstEarlierThanSecond(clientTime,
				additionalMinsClientTime, expansionStartTime, expansionMins);
		if(!speedup && waitedLongEnough) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_NOT_DONE_YET);
			log.error("client error: time is out of sync. Client incorrectly thinks" +
					" that the expansion is done. userCityExpansionData=" + epfu);
			return false; 
		}

		//check if speeding up and user has enough gems
		if (speedup && user.getGems() < gemCostToSpeedup) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_INSUFFICIENT_GEMS);
			log.error("user error: user does not have enough gems to speed up expansion." +
					" userCityExpansionData=" + epfu + "cost=" + gemCostToSpeedup);
			return false;      
		}
		
		resBuilder.setStatus(ExpansionWaitCompleteStatus.SUCCESS);
		return true;  
	}

	private boolean writeChangesToDb(User user, ExpansionPurchaseForUser epfu,
			boolean speedup, Date clientTime, int gemCost) {
		try {
			if (speedup) {
				//update user
				int gemChange = -1 * gemCost; //make negative
				//keep track of user currency
				List<UserCurrencyHistory> uchList = createCurrencyHistory(user, epfu,
						clientTime, gemChange);
				//update user's money
				if (0 != gemChange) {
					int newCash = gemChange + user.getCash();
					user.setCash(newCash);
					getUserService().saveUser(user);
				}
				//record user currency
				if (!uchList.isEmpty()) {
					getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
				}
			}
			
			//update the expansion purchase
			epfu.setExpandStartTime(clientTime);
			epfu.setExpanding(false);
			getExpansionPurchaseForUserService().saveExpansionPurchaseForUser(epfu);
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	private List<UserCurrencyHistory> createCurrencyHistory(User u, 
			ExpansionPurchaseForUser epfu, Date timePurchased, int gemChange) {
		int xPosition = epfu.getxPosition();
		int yPosition = epfu.getyPosition();
		
		String gemStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__SPED_UP_CITY_EXPANSION;
		StringBuilder sb = new StringBuilder();
		sb.append("(user,x,y)=(");
		sb.append(u.getId());
		sb.append(",");
		sb.append(xPosition);
		sb.append(",");
		sb.append(yPosition);
		sb.append(")");
		
		String details = sb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, timePurchased, gemStr, gemChange,
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

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

}
