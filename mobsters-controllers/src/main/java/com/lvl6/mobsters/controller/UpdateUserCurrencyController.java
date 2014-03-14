package com.lvl6.mobsters.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.eventprotos.EventUserProto.UpdateUserCurrencyRequestProto;
import com.lvl6.mobsters.eventprotos.EventUserProto.UpdateUserCurrencyResponseProto;
import com.lvl6.mobsters.eventprotos.EventUserProto.UpdateUserCurrencyResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventUserProto.UpdateUserCurrencyResponseProto.UpdateUserCurrencyStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.UpdateUserCurrencyRequestEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.events.response.UpdateUserCurrencyResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class UpdateUserCurrencyController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService; 

	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	public UpdateUserCurrencyController() {
		numAllocatedThreads = 4;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new UpdateUserCurrencyRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_UPDATE_USER_CURRENCY_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		UpdateUserCurrencyRequestProto reqProto = 
				((UpdateUserCurrencyRequestEvent) event).getUpdateUserCurrencyRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getSender();
		
		//both positive numbers, server will change to negative
		int cashSpent = reqProto.getCashSpent();
		int oilSpent = reqProto.getOilSpent();
		int gemsSpent = reqProto.getGemsSpent();

		String reason = reqProto.getReason();
		String details = reqProto.getDetails();
		Date clientTime = new Date(reqProto.getClientTime());
		
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = UpdateUserCurrencyResponseProto.newBuilder();
		responseBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_OTHER);
		responseBuilder.setSender(sender);
		UpdateUserCurrencyResponseEvent resEvent =
				new UpdateUserCurrencyResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);

			boolean legit = checkLegit(responseBuilder, user, userId, cashSpent,
					oilSpent, gemsSpent);
			
			boolean successful = false;
			
			
			//update the facebook id
			if (legit) {
				successful = writeChangesToDb(user, userId, cashSpent, oilSpent, gemsSpent,
						clientTime, reason, details);
			}
			
			if (successful) {
				responseBuilder.setStatus(UpdateUserCurrencyStatus.SUCCESS);
			}
			
			//write to client
			resEvent.setUpdateUserCurrencyResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//notify client that his currency changed
				UpdateClientUserResponseEvent update = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(user);
				getEventWriter().handleEvent(update);
			}

		} catch (Exception e) {
			log.error("exception in UpdateUserCurrencyController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_OTHER);
				resEvent.setUpdateUserCurrencyResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in UpdateUserCurrencyController processRequestEvent", e2);
			}
		}
	}
	
	private boolean checkLegit(Builder resBuilder, User u, UUID userId, int cashSpent,
			int oilSpent, int gemsSpent) {
		if (null == u) {
			log.error("unexpected error: user is null. user=" + u);
			return false;
		}

		if (cashSpent != Math.abs(cashSpent) || oilSpent != Math.abs(oilSpent) ||
				gemsSpent != Math.abs(gemsSpent)) {
			log.error("client sent a negative value! all should be positive :(  cashSpent=" +
					cashSpent + "\t oilSpent=" + oilSpent + "\t gemsSpent=" + gemsSpent);
			if (u.isAdmin()) {
				log.info("it's alright. User is admin.");
			} else {
				return false;
			}
		}

		//CHECK MONEY
		if (!hasEnoughCash(resBuilder, u, cashSpent)) {
			if (u.isAdmin()) {
				log.info("it's alright. User is admin.");
			} else {
				return false;
			}
		}

		if (!hasEnoughOil(resBuilder, u, oilSpent)) {
			if (u.isAdmin()) {
				log.info("it's alright. User is admin.");
			} else {
				return false;
			}
		}

		if (!hasEnoughGems(resBuilder, u, gemsSpent)) {
			if (u.isAdmin()) {
				log.info("it's alright. User is admin.");
			} else {
				return false;
			}
		}

		return true;
	}

	private boolean hasEnoughCash(Builder resBuilder, User u, int cashSpent) {
		int userCash = u.getCash();
		//if user's aggregate cash is < cost, don't allow transaction
		if (userCash < cashSpent) {
			log.error("user error: user does not have enough oil. userCash=" + userCash +
					"\t cashSpent=" + cashSpent);
			resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_INSUFFICIENT_CASH);
			return false;
		}

		return true;
	}

	private boolean hasEnoughOil(Builder resBuilder, User u, int oilSpent) {
		int userOil = u.getOil();
		//if user's aggregate oil is < cost, don't allow transaction
		if (userOil < oilSpent) {
			log.error("user error: user does not have enough oil. userOil=" + userOil +
					"\t oilSpent=" + oilSpent);
			resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_INSUFFICIENT_OIL);
			return false;
		}

		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("user error: user does not have enough gems. userGems=" + userGems +
					"\t gemsSpent=" + gemsSpent);
			resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}

		return true;
	}
	
	private boolean writeChangesToDb(User u, UUID uId, int cashSpent, int oilSpent,
			int gemsSpent, Date clientDate, String reason, String details) {
		try {
			//update user currency
			int gemsChange = -1 * Math.abs(gemsSpent);
			int cashChange = -1 * Math.abs(cashSpent);
			int oilChange = -1 * Math.abs(oilSpent);

			//if user is admin then allow any change
			if (u.isAdmin()) {
				gemsChange = gemsSpent;
				cashChange = cashSpent;
				oilChange = oilSpent;
			}
			
			List<UserCurrencyHistory> uchList = createCurrencyHistory(u, clientDate,
					gemsChange, cashChange, oilChange, reason, details);
			getUserService().updateUserResources(u, gemsChange, oilChange, cashChange);
			
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
	
	private List<UserCurrencyHistory> createCurrencyHistory(User u, Date clientDate,
			int gemChange, int cashChange, int oilChange, String reason, String details) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String cashStr = MobstersDbTables.USER__CASH;
		String oilStr = MobstersDbTables.USER__OIL;
		
		boolean saveToDb = false;
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, clientDate, gemsStr, gemChange,
						reason, details, saveToDb);
		UserCurrencyHistory cash = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, clientDate, cashStr, cashChange,
						reason, details, saveToDb);
		UserCurrencyHistory oil = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, clientDate, oilStr, oilChange,
						reason, details, saveToDb);
		
		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != gems) {
			uchList.add(gems);
		}
		if (null != cash) {
			uchList.add(cash);
		}
		if (null != oil) {
			uchList.add(oil);
		}
		return uchList;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
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
