package com.lvl6.mobsters.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.StructureRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventStructureProto.PurchaseNormStructureRequestProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.PurchaseNormStructureResponseProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.PurchaseNormStructureResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventStructureProto.PurchaseNormStructureResponseProto.PurchaseNormStructureStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.PurchaseNormStructureRequestEvent;
import com.lvl6.mobsters.events.response.PurchaseNormStructureResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceType;
import com.lvl6.mobsters.noneventprotos.StructureProto.StructOrientation;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;
import com.lvl6.mobsters.utils.CoordinatePair;



@Component
public class PurchaseNormStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	@Autowired
	protected StructureForUserService structureForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	

	@Override
	public RequestEvent createRequestEvent() {
		return new PurchaseNormStructureRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_PURCHASE_NORM_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		PurchaseNormStructureRequestProto reqProto = 
				((PurchaseNormStructureRequestEvent) event).getPurchaseNormStructureRequestProto();

		//get stuff client sent
	    MinimumUserProto senderProto = reqProto.getSender();
	    int structId = reqProto.getStructId();
	    CoordinatePair cp = new CoordinatePair(reqProto.getStructCoordinates().getX(), reqProto.getStructCoordinates().getY());
	    Date timeOfPurchase = new Date(reqProto.getTimeOfPurchase());
	    //positive value, need to convert to negative when updating user
	    int gemsSpent = reqProto.getGemsSpent();
	    //positive means refund, negative means charge user
	    int resourceChange = reqProto.getResourceChange();
	    ResourceType resourceType = reqProto.getResourceType();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = PurchaseNormStructureResponseProto.newBuilder();
		responseBuilder.setStatus(PurchaseNormStructureStatus.FAIL_OTHER);
		PurchaseNormStructureResponseEvent resEvent = new PurchaseNormStructureResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			String gameCenterId = null;
			User user = getUserService().retrieveUser(gameCenterId, userId);
			Structure struct = getStructureRetrieveUtils().getStructureForId(structId);

			boolean legitPurchaseNorm = checkLegitPurchaseNorm(responseBuilder, struct, user,
		      		timeOfPurchase, gemsSpent, resourceChange, resourceType);

			boolean successful = false;
			List<StructureForUser> uStructList = new ArrayList<StructureForUser>();
			StructureForUser sfu = null;
			Map<String, Integer> previousMoney = new HashMap<String, Integer>();
			Map<String, Integer> moneyChange = new HashMap<String, Integer>();
			if (legitPurchaseNorm) {
				previousMoney.put(MobstersDbTables.USER__GEMS, user.getGems());
				previousMoney.put(MobstersDbTables.USER__OIL, user.getOil());
				previousMoney.put(MobstersDbTables.USER__CASH, user.getCash());
				
		      	successful = writeChangesToDb(user, structId, cp, timeOfPurchase, gemsSpent,
		      			resourceChange, resourceType, uStructList, moneyChange);
		      	
		      	
			}

			if (successful) {
				sfu = uStructList.get(0);
				responseBuilder.setStatus(PurchaseNormStructureStatus.SUCCESS);
			}

			//write to client
			resEvent.setPurchaseNormStructureResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			//write to currency history
			writeToUserCurrencyHistory(user, structId, sfu, timeOfPurchase, moneyChange,
					previousMoney);

		} catch (Exception e) {
			log.error("exception in PurchaseNormStructureController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(PurchaseNormStructureStatus.FAIL_OTHER);
				resEvent.setPurchaseNormStructureResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in PurchaseNormStructureController processRequestEvent", e2);
			}
		}
	}


	private boolean checkLegitPurchaseNorm(Builder resBuilder, Structure prospective,
			User user, Date timeOfPurchase, int gemsSpent, int resourceChange,
			ResourceType resourceType) {
		if (user == null || prospective == null || timeOfPurchase == null) {
			log.error("parameter passed in is null. user=" + user + ", struct=" + prospective 
					+ ", timeOfPurchase=" + timeOfPurchase);
			return false;
		}
		ResourceType structResourceType = ResourceType.valueOf(prospective.getBuildResourceType());
		if (resourceType != structResourceType) {
			log.error("client is specifying unexpected resource type. actual=" + resourceType +
					"\t expected=" + structResourceType + "\t structure=" + prospective);
			return false;
		}

		//check if user has enough resources to build it
		int userGems = user.getGems();
		if (gemsSpent > 0 && userGems < gemsSpent) {
			log.error("user has " + userGems + " gems; trying to spend " + gemsSpent + " and " +
					resourceChange  + " " + resourceType + " to buy structure=" + prospective);
			resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}

		//don't think it will ever be positive though
		//positive means refund, negative means charge user
		//if refund, then requiredResourceAmount is negative which would always be < user's amount
		int requiredResourceAmount = -1 * resourceChange;
		if (resourceType == ResourceType.CASH) {
			int userResource = user.getCash();
			if (userResource < requiredResourceAmount) {
				log.error("not enough cash to buy structure. cash=" + userResource +
						"\t cost=" + requiredResourceAmount + "\t structure=" + prospective);
				resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_INSUFFICIENT_CASH);
				return false;
			}
		} else if (resourceType == ResourceType.OIL) {
			int userResource = user.getOil();
			if (userResource < requiredResourceAmount) {
				log.error("not enough oil to buy structure. oil=" + userResource +
						"\t cost=" + requiredResourceAmount + "\t structure=" + prospective);
				resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_INSUFFICIENT_OIL);
				return false;
			}
		} else {
			log.error("unknown resource type: " + resourceType + "\t structure=" + prospective);
			return false;
		}

		return true;
	}

	//uStructId will store the newly created user structure
	private boolean writeChangesToDb(User user, int structId, CoordinatePair cp,
			Date purchaseTime, int gemsSpent, int resourceChange, ResourceType resourceType,
			List<StructureForUser> uStructList, Map<String, Integer> money) {
		try {

			UUID userId = user.getId();
			Timestamp lastRetrievedTime = null;
			boolean isComplete = false;
			String orientation = StructOrientation.POSITION_1.name();
			
			//give the user his new structure
			StructureForUser sfu = getStructureForUserService().insertUserStruct(userId, structId,
					lastRetrievedTime, cp, purchaseTime, isComplete, orientation);
			if (null == sfu) {
				log.error("problem with giving struct " + structId + " at " + purchaseTime +
						" on " + cp);
				return false;
			}

			//TAKE AWAY THE CORRECT RESOURCE
			int gemChange = -1 * gemsSpent;
			int cashChange = 0;
			int oilChange = 0;

			if (resourceType == ResourceType.CASH) {
				cashChange = resourceChange;
			} else if (resourceType == ResourceType.OIL) {
				oilChange = resourceChange;
			}

			getUserService().updateUserResources(user, gemChange, oilChange, cashChange);
			if (0 != gemChange) {
				money.put(MobstersDbTables.USER__GEMS, gemChange * -1);
			}
			if (0 != cashChange) {
				money.put(MobstersDbTables.USER__CASH, cashChange);
			}
			if (0 != oilChange) {
				money.put(MobstersDbTables.USER__OIL, oilChange);
			}

			uStructList.add(sfu);
			return true;
		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	
	public void writeToUserCurrencyHistory(User user, int structId, StructureForUser sfu,
			Date date, Map<String, Integer> moneyChange, Map<String, Integer> previousMoney) {
		UUID userId = user.getId();
		
		Map<String, Integer> currentMoney = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = MobstersDbTables.USER__GEMS;
		String oil = MobstersDbTables.USER__OIL;
		String cash = MobstersDbTables.USER__CASH;
		
		String reasonForChange = MobstersTableConstants.UCHRFC__PURCHASE_NORM_STRUCT;
		StringBuilder sb = new StringBuilder();
		sb.append("structId=");
		sb.append(structId);
		sb.append(" uStructId=" + sfu.getId());
		String detail = sb.toString();
		
		currentMoney.put(gems, user.getGems());
      	currentMoney.put(oil, user.getOil());
      	currentMoney.put(cash, user.getCash());
      	reasonsForChanges.put(gems, reasonForChange);
      	reasonsForChanges.put(oil, reasonForChange);
      	reasonsForChanges.put(cash, reasonForChange);
      	detailsMap.put(gems, detail);
      	detailsMap.put(oil, detail);
      	detailsMap.put(cash, detail);
      	
      	boolean saveToDb = true;
      	getUserCurrencyHistoryService().createUserCurrencyHistories(userId, date,
      			moneyChange, previousMoney, currentMoney, reasonsForChanges,
      			detailsMap, saveToDb);
	}
	

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}

	public StructureForUserService getStructureForUserService() {
		return structureForUserService;
	}

	public void setStructureForUserService(
			StructureForUserService structureForUserService) {
		this.structureForUserService = structureForUserService;
	}

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}
}

