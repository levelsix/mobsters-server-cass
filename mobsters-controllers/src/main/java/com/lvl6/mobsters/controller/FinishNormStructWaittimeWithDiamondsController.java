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
import com.lvl6.mobsters.entitymanager.staticdata.StructureRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventStructureProto.FinishNormStructWaittimeWithDiamondsRequestProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto.FinishNormStructWaittimeStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.FinishNormStructWaittimeWithDiamondsRequestEvent;
import com.lvl6.mobsters.events.response.FinishNormStructWaittimeWithDiamondsResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
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
public class FinishNormStructWaittimeWithDiamondsController extends EventController {

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
		return new FinishNormStructWaittimeWithDiamondsRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_PURCHASE_NORM_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		FinishNormStructWaittimeWithDiamondsRequestProto reqProto = 
				((FinishNormStructWaittimeWithDiamondsRequestEvent) event).getFinishNormStructWaittimeWithDiamondsRequestProto();

		//get stuff client sent
	    MinimumUserProto senderProto = reqProto.getSender();
	    String userStructIdStr = reqProto.getUserStructUuid();
	    UUID userStructId = UUID.fromString(userStructIdStr); 
	    Date timeOfSpeedup = new Date(reqProto.getTimeOfSpeedup());
	    //positive value, need to convert to negative when updating user
	    int gemsSpent = reqProto.getGemCostToSpeedup();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = FinishNormStructWaittimeWithDiamondsResponseProto.newBuilder();
		responseBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_OTHER);
		FinishNormStructWaittimeWithDiamondsResponseEvent resEvent = new FinishNormStructWaittimeWithDiamondsResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			String gameCenterId = null;
			User user = getUserService().retrieveUser(gameCenterId, userId);
			StructureForUser userStruct = getStructureForUserService().getSpecificUserStruct(userStructId);
			Structure currentStruct = null;
			
			if (null != userStruct) {
				int structId = userStruct.getStructureId();
				currentStruct = getStructureRetrieveUtils().getStructureForId(structId);
			}

			boolean legitUpgradeNorm = checkLegitSpeedup(responseBuilder, user, userStruct, 
		      		timeOfSpeedup, currentStruct, gemsSpent);

			boolean successful = false;
			Map<String, Integer> previousMoney = new HashMap<String, Integer>();
			Map<String, Integer> moneyChange = new HashMap<String, Integer>();
			if (legitUpgradeNorm) {
				previousMoney.put(MobstersDbTables.USER__GEMS, user.getGems());
				
		      	successful = writeChangesToDb(user, userStruct, timeOfSpeedup, currentStruct,
		      			gemsSpent, moneyChange);
			}

			if (successful) {
				responseBuilder.setStatus(FinishNormStructWaittimeStatus.SUCCESS);
			}

			//write to client
			resEvent.setFinishNormStructWaittimeWithDiamondsResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtils()
						.createUpdateClientUserResponseEvent(user);
				log.info("Writing update event: " + resEventUpdate);
				getEventWriter().handleEvent(resEventUpdate);
				
				//write to currency history
				writeToUserCurrencyHistory(user, userStruct, currentStruct, timeOfSpeedup,
						moneyChange, previousMoney);
			}

		} catch (Exception e) {
			log.error("exception in FinishNormStructWaittimeWithDiamondsController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_OTHER);
				resEvent.setFinishNormStructWaittimeWithDiamondsResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in FinishNormStructWaittimeWithDiamondsController processRequestEvent", e2);
			}
		}
	}


	private boolean checkLegitSpeedup(Builder resBuilder, User user,
	  		StructureForUser userStruct, Date timeOfSpeedup, Structure struct,
	  		int gemCostToSpeedup) {
	    if (user == null || userStruct == null || struct == null ||
	    		userStruct.getUserId() != user.getId() || userStruct.isComplete()) {
	      resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_OTHER);
	      log.error("something passed in is null. user=" + user + ", userStruct=" + userStruct +
	           ", struct=" + struct + ", struct owner's id=" + userStruct.getUserId());
	      return false;
	    }
	    
	    if (user.getGems() < gemCostToSpeedup) {
	      resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_NOT_ENOUGH_GEMS);
	      log.error("user doesn't have enough diamonds. has " + user.getGems() +", needs " +
	      		gemCostToSpeedup);
	      return false;
	    }
	    
	    return true;
	}

	//uStructId will store the newly created user structure
	private boolean writeChangesToDb(User user, StructureForUser userStruct, Date timeOfSpeedup,
			Structure curentStruct, int gemCost, Map<String, Integer> money) {
		try {
			//upgrade the user's struct
			getStructureForUserService().updateUserStructBuildingIsComplete(userStruct,
					timeOfSpeedup);
			
			//TAKE AWAY THE CORRECT RESOURCE
			int gemChange = -1 * gemCost;
			int cashChange = 0;
			int oilChange = 0;

			getUserService().updateUserResources(user, gemChange, oilChange, cashChange);
			if (0 != gemChange) {
				money.put(MobstersDbTables.USER__GEMS, gemChange * -1);
			}

			return true;
		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	
	public void writeToUserCurrencyHistory(User user, StructureForUser sfu, Structure currentStruct,
			Date date, Map<String, Integer> moneyChange, Map<String, Integer> previousMoney) {
		UUID userId = user.getId();
		UUID userStructId = sfu.getId();
		int structId = currentStruct.getId();
				
		
		Map<String, Integer> currentMoney = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = MobstersDbTables.USER__GEMS;
		String oil = MobstersDbTables.USER__OIL;
		String cash = MobstersDbTables.USER__CASH;
		
		String reasonForChange = MobstersTableConstants.UCHRFC__SPED_UP_NORM_STRUCT;
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

