package com.lvl6.mobsters.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventStructureProto.UpgradeNormStructureRequestProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.UpgradeNormStructureResponseProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.UpgradeNormStructureResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventStructureProto.UpgradeNormStructureResponseProto.UpgradeNormStructureStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.UpgradeNormStructureRequestEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.events.response.UpgradeNormStructureResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceType;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;



@Component
public class UpgradeNormStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	@Autowired
	protected StructureForUserService structureForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateEventProtoUtils createEventProtoUtils;
	

	@Override
	public RequestEvent createRequestEvent() {
		return new UpgradeNormStructureRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_UPGRADE_NORM_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		UpgradeNormStructureRequestProto reqProto = 
				((UpgradeNormStructureRequestEvent) event).getUpgradeNormStructureRequestProto();

		//get stuff client sent
	    MinimumUserProto senderProto = reqProto.getSender();
	    String userStructIdStr = reqProto.getUserStructUuid();
	    UUID userStructId = UUID.fromString(userStructIdStr); 
	    Date timeOfUpgrade = new Date(reqProto.getTimeOfUpgrade());
	    //positive value, need to convert to negative when updating user
	    int gemsSpent = reqProto.getGemsSpent();
	    //positive means refund, negative means charge user
	    int resourceChange = reqProto.getResourceChange();
	    ResourceType resourceType = reqProto.getResourceType();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = UpgradeNormStructureResponseProto.newBuilder();
		responseBuilder.setStatus(UpgradeNormStructureStatus.FAIL_OTHER);
		UpgradeNormStructureResponseEvent resEvent = new UpgradeNormStructureResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			String gameCenterId = null;
			User user = getUserService().getUserByGamcenterIdOrUserId(gameCenterId, userId);
			StructureForUser userStruct = getStructureForUserService().getSpecificUserStruct(userStructId);
			Structure currentStruct = null;
			Structure nextLevelStruct = null;
			
			if (null != userStruct) {
				int structId = userStruct.getStructureId();
				currentStruct = getStructureRetrieveUtils().getStructureForId(structId);
				nextLevelStruct = getStructureRetrieveUtils().getUpgradedStructure(currentStruct, structId);
			}

			boolean legitUpgradeNorm = checkLegitUpgrade(responseBuilder, user, userStruct, 
		      		currentStruct, nextLevelStruct, gemsSpent, resourceChange, resourceType,
		      		timeOfUpgrade);

			boolean successful = false;
			Map<String, Integer> previousMoney = new HashMap<String, Integer>();
			Map<String, Integer> moneyChange = new HashMap<String, Integer>();
			if (legitUpgradeNorm) {
				previousMoney.put(MobstersDbTables.USER__GEMS, user.getGems());
				previousMoney.put(MobstersDbTables.USER__OIL, user.getOil());
				previousMoney.put(MobstersDbTables.USER__CASH, user.getCash());
				
		      	successful = writeChangesToDb(user, userStruct, nextLevelStruct, gemsSpent,
		      			resourceChange, resourceType, timeOfUpgrade, moneyChange);
		      	
		      	
			}

			if (successful) {
				responseBuilder.setStatus(UpgradeNormStructureStatus.SUCCESS);
			}

			//write to client
			resEvent.setUpgradeNormStructureResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtils()
						.createUpdateClientUserResponseEvent(user);
				log.info("Writing update event: " + resEventUpdate);
				getEventWriter().handleEvent(resEventUpdate);
				
				//write to currency history
				writeToUserCurrencyHistory(user, userStruct, currentStruct, nextLevelStruct,
						timeOfUpgrade, moneyChange, previousMoney);
			}

		} catch (Exception e) {
			log.error("exception in UpgradeNormStructureController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(UpgradeNormStructureStatus.FAIL_OTHER);
				resEvent.setUpgradeNormStructureResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in UpgradeNormStructureController processRequestEvent", e2);
			}
		}
	}


	private boolean checkLegitUpgrade(Builder resBuilder, User user, StructureForUser userStruct,
			Structure currentStruct, Structure nextLevelStruct, int gemsSpent, int resourceChange, 
			ResourceType rt, Date timeOfUpgrade) {
		if (user == null || userStruct == null || userStruct.getLastCollectTime() == null) {
			log.error("parameter passed in is null. user=" + user + ", user struct=" + userStruct + 
					", userStruct's last collect time=" + userStruct.getLastCollectTime());
			return false;
		}
		if (!userStruct.isComplete()) {
			resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_BUILT_YET);
			log.error("user struct is not complete yet");
			return false;
		}
		if (null == nextLevelStruct) {
			resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_AT_MAX_LEVEL_ALREADY);
			log.error("user struct at max level already. struct is " + currentStruct);
			return false;
		}
		if (timeOfUpgrade.getTime() < userStruct.getLastCollectTime().getTime()) {
			resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_BUILT_YET);
			log.error("the upgrade time " + timeOfUpgrade + " is before the last time the building was retrieved:"
					+ userStruct.getLastCollectTime());
			return false;
		}
		//see if the user can upgrade it
		if (user.getId() != userStruct.getUserId()) {
			resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_USERS_STRUCT);
			log.error("user struct belongs to someone else with id " + userStruct.getUserId());
			return false;
		}

		int userGems = user.getGems();
		if (gemsSpent > 0 && userGems < gemsSpent) {
			log.error("user has " + userGems + " gems; trying to spend " + gemsSpent + " and " +
					resourceChange  + " " + rt + " to upgrade to structure=" + nextLevelStruct);
			resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_ENOUGH_GEMS);
			return false;
		}
		if (ResourceType.CASH.equals(rt)) {
			if (user.getCash() < resourceChange) {
				resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_ENOUGH_CASH);
				log.error("user doesn't have enough cash, has " + user.getCash() + ", needs " + resourceChange);
				return false;
			}
		} else if (ResourceType.OIL.equals(rt)){
			if (user.getOil() < resourceChange) {
				resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_ENOUGH_OIL);
				log.error("user doesn't have enough gems, has " + user.getGems() + ", needs " + resourceChange);
				return false;
			}
		}

		resBuilder.setStatus(UpgradeNormStructureStatus.SUCCESS);
		return true;
	}

	//uStructId will store the newly created user structure
	private boolean writeChangesToDb(User user, StructureForUser userStruct,
			Structure nextLevelStruct, int gemsSpent, int resourceChange, ResourceType resourceType,
			Date timeOfUpgrade, Map<String, Integer> money) {
		try {
			//upgrade the user's struct
			getStructureForUserService().upgradeUserStruct(userStruct, nextLevelStruct, timeOfUpgrade);
			
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

			return true;
		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	
	public void writeToUserCurrencyHistory(User user, StructureForUser sfu, Structure currentStruct,
			Structure nextLevelStruct, Date date, Map<String, Integer> moneyChange, Map<String, Integer> previousMoney) {
		UUID userId = user.getId();
		UUID userStructId = sfu.getId();
		int structId = nextLevelStruct.getId();
				
		
		Map<String, Integer> currentMoney = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = MobstersDbTables.USER__GEMS;
		String oil = MobstersDbTables.USER__OIL;
		String cash = MobstersDbTables.USER__CASH;
		
		String reasonForChange = MobstersTableConstants.UCHRFC__UPGRADE_NORM_STRUCT;
		StringBuilder sb = new StringBuilder();
		sb.append("structId=");
		sb.append(structId);
		sb.append(" uStructId=" + userStructId);
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

	public CreateEventProtoUtils getCreateEventProtoUtils() {
		return createEventProtoUtils;
	}

	public void setCreateEventProtoUtils(CreateEventProtoUtils createEventProtoUtils) {
		this.createEventProtoUtils = createEventProtoUtils;
	}
	
}

