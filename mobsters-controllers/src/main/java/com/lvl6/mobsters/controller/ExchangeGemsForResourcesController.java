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
import com.lvl6.mobsters.eventprotos.EventInAppPurchaseProto.ExchangeGemsForResourcesRequestProto;
import com.lvl6.mobsters.eventprotos.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto;
import com.lvl6.mobsters.eventprotos.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto.ExchangeGemsForResourcesStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.ExchangeGemsForResourcesRequestEvent;
import com.lvl6.mobsters.events.response.ExchangeGemsForResourcesResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceType;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class ExchangeGemsForResourcesController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService; 

	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	
	
	public ExchangeGemsForResourcesController() {
	    numAllocatedThreads = 2;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return  new ExchangeGemsForResourcesRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_EXCHANGE_GEMS_FOR_RESOURCES_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		ExchangeGemsForResourcesRequestProto reqProto = 
				((ExchangeGemsForResourcesRequestEvent) event).getExchangeGemsForResourcesRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getSender();
	    int numGems = reqProto.getNumGems();
	    int numResources = reqProto.getNumResources();
	    ResourceType resourceType = reqProto.getResourceType();
	    Date curTime = new Date(reqProto.getClientTime());


		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = ExchangeGemsForResourcesResponseProto.newBuilder();
		responseBuilder.setStatus(ExchangeGemsForResourcesStatus.FAIL_OTHER);
		ExchangeGemsForResourcesResponseEvent resEvent =
				new ExchangeGemsForResourcesResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, aUser, numGems,
					resourceType, numGems);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(aUser, numGems, resourceType, numResources,
						curTime);
			}

			if (successful) {
				responseBuilder.setStatus(ExchangeGemsForResourcesStatus.SUCCESS);
			}

			//write to client
			resEvent.setExchangeGemsForResourcesResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//since modified user's resources need to send update client user event
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in ExchangeGemsForResourcesController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(ExchangeGemsForResourcesStatus.FAIL_OTHER);
				resEvent.setExchangeGemsForResourcesResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in ExchangeGemsForResourcesController processRequestEvent", e2);
			}
		}
	}
	
	private boolean isValidRequest(Builder resBuilder, User aUser, int numGems,
	  		ResourceType resourceType, int numResources) {
		if (null == aUser || null == resourceType || 0 == numGems) {
	  		log.error("user or resourceType is null, or numGems is 0. user=" + aUser + 
	  				"\t resourceType=" + resourceType + "\t numGems=" + numGems);
	  		return false;
	  	}
	    
	  	int userGems = aUser.getGems();
	  	
	  	if (userGems < numGems) {
	  		log.error("user does not have enough gems to exchange for resource." +
	  				" userGems=" + userGems + "\t resourceType=" + resourceType +
	  				"\t numResources=" + numResources);
	  		resBuilder.setStatus(ExchangeGemsForResourcesStatus.FAIL_INSUFFICIENT_GEMS);
	  		return false;
	  	}
	  	
	    return true;
	  }

	private boolean writeChangesToDb(User user, int numGems, ResourceType resourceType,
	  		int numResources, Date clientTime) {
		try {
			int cashChange = 0;
		  	int oilChange = 0;
		  	int gemChange = -1 * numGems;
		  	
		  	if (ResourceType.CASH == resourceType) {
		  		cashChange = numResources;
		  	} else if (ResourceType.OIL == resourceType) {
		  		oilChange = numResources;
		  	}

		  	//create history first
		  	List<UserCurrencyHistory> uchList = createCurrencyHistory(user, clientTime,
		  			gemChange, resourceType, numResources);
		  	//charge the user
		  	getUserService().updateUserResources(user, gemChange, oilChange, cashChange);

		  	//keep track of currency stuff
		  	if (!uchList.isEmpty()) {
		  		getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
		  	}

			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	
	
	private List<UserCurrencyHistory> createCurrencyHistory(User aUser, Date date,
			int gemChange, ResourceType resourceType, int numResources) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__CURRENCY_EXCHANGED;
		StringBuilder detailSb = new StringBuilder();
		
		String resourceStr = "";
		if (ResourceType.CASH.equals(resourceType)) {
			resourceStr = MobstersDbTables.USER__CASH;
		} else if (ResourceType.OIL.equals(resourceType)) {
			resourceStr = MobstersDbTables.USER__OIL;
		}
		
		detailSb.append("(gemsExchanged,numResourceGained)");
		detailSb.append("(");
		detailSb.append(-1* gemChange);
		detailSb.append(",");
		detailSb.append(numResources);
		detailSb.append(")");
		
		String details = detailSb.toString();
		
		boolean saveToDb = false;
		UserCurrencyHistory resource = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, date, resourceStr, numResources,
						reasonForChange, details, saveToDb);
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, date, gemsStr, gemChange,
						reasonForChange, details, saveToDb);
		
		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != resource) {
			uchList.add(resource);
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
