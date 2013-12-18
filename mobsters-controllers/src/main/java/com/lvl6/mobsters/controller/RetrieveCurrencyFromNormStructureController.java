package com.lvl6.mobsters.controller;

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

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto.StructRetrieval;
import com.lvl6.mobsters.eventprotos.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.RetrieveCurrencyFromNormStructureStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.RetrieveCurrencyFromNormStructureRequestEvent;
import com.lvl6.mobsters.events.response.RetrieveCurrencyFromNormStructureResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceType;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.StructureResourceGenerator;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;



@Component
public class RetrieveCurrencyFromNormStructureController extends EventController {

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
	
	@Autowired
	protected StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils;

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrieveCurrencyFromNormStructureRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		RetrieveCurrencyFromNormStructureRequestProto reqProto = 
				((RetrieveCurrencyFromNormStructureRequestEvent) event).getRetrieveCurrencyFromNormStructureRequestProto();

		//get stuff client sent
	    MinimumUserProto senderProto = reqProto.getSender();
	    List<StructRetrieval> structRetrievals = reqProto.getStructRetrievalsList();
	    Date timeOfPurchase = new Date();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);
		
		Map<UUID, Date> userStructIdsToTimesOfRetrieval =  new HashMap<UUID, Date>();
	    Map<UUID, Integer> userStructIdsToAmountCollected = new HashMap<UUID, Integer>();
	    List<UUID> duplicates = new ArrayList<UUID>();
	    //create map from ids to times and check for duplicates
	    demultiplexRetrievals(structRetrievals, duplicates,
	    		userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected);

		//response to send back to client
		Builder responseBuilder = RetrieveCurrencyFromNormStructureResponseProto.newBuilder();
		responseBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
		RetrieveCurrencyFromNormStructureResponseEvent resEvent = new RetrieveCurrencyFromNormStructureResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			String gameCenterId = null;
			User user = getUserService().retrieveUser(gameCenterId, userId);
			List<UUID> userStructIds = new ArrayList<UUID>(userStructIdsToTimesOfRetrieval.keySet());
			Map<UUID, StructureForUser> userStructIdsToUserStructs = getStructureForUserService()
					.getSpecificOrAllUserStructuresForUser(userId, userStructIds);
			Map<UUID, StructureResourceGenerator> userStructIdsToGenerators =
					getStructureForUserService().getUserStructIdsToResourceGenerators(
							userStructIdsToUserStructs.values());

			//this will contain the amount user collects
			Map<String, Integer> resourcesGained = new HashMap<String, Integer>();
			//userStructIdsToTimesOfRetrieval and userStructIdsToUserStructs will be
			//modified to contain only the valid user structs user can retrieve currency from
			boolean legitRetrieval = checkLegitRetrieval(responseBuilder, user, userStructIds,
					userStructIdsToUserStructs, userStructIdsToGenerators, duplicates,
					userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected, resourcesGained);


			boolean successful = false;
			Map<String, Integer> previousMoney = new HashMap<String, Integer>();
			if (legitRetrieval) {
				previousMoney.put(MobstersDbTables.USER__GEMS, user.getGems());
				previousMoney.put(MobstersDbTables.USER__OIL, user.getOil());
				previousMoney.put(MobstersDbTables.USER__CASH, user.getCash());
				
				int cashGain = resourcesGained.get(MobstersDbTables.USER__CASH);
				int oilGain = resourcesGained.get(MobstersDbTables.USER__OIL);
		      	successful = writeChangesToDb(user, cashGain, oilGain, userStructIdsToUserStructs,
		        		userStructIdsToTimesOfRetrieval);
			}

			if (successful) {
				responseBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.SUCCESS);
			}

			//write to client
			resEvent.setRetrieveCurrencyFromNormStructureResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtils()
						.createUpdateClientUserResponseEvent(user);
				log.info("Writing update event: " + resEventUpdate);
				getEventWriter().handleEvent(resEventUpdate);
				
				//write to currency history
				writeToUserCurrencyHistory(user, previousMoney, resourcesGained,
						timeOfPurchase, userStructIdsToUserStructs, userStructIdsToGenerators,
		        		userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected);
			}

		} catch (Exception e) {
			log.error("exception in RetrieveCurrencyFromNormStructureController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
				resEvent.setRetrieveCurrencyFromNormStructureResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in RetrieveCurrencyFromNormStructureController processRequestEvent", e2);
			}
		}
	}
	
	//separate the duplicate ids from the unique ones
	private void demultiplexRetrievals(List<StructRetrieval> srList,  List<UUID> duplicates,
			Map<UUID, Date> structIdsToTimesOfRetrieval,
			Map<UUID, Integer> structIdsToAmountCollected) {
		if (srList.isEmpty()) {
			log.error("RetrieveCurrencyFromNormStruct request did not send any user struct ids.");
			return;
		}

		for(StructRetrieval sr : srList) {
			String keyStr = sr.getUserStructUuid();
			UUID key = UUID.fromString(keyStr);
			Date value = new Date(sr.getTimeOfRetrieval());
			int amount = sr.getAmountCollected();

			if(structIdsToTimesOfRetrieval.containsKey(key)) {
				duplicates.add(key);
			} else {
				structIdsToTimesOfRetrieval.put(key, value);
				structIdsToAmountCollected.put(key, amount);
			}
		}
	}


	private boolean checkLegitRetrieval(Builder resBuilder, User user, List<UUID> userStructIds, 
	        Map<UUID, StructureForUser> userStructIdsToUserStructs,
	        Map<UUID, StructureResourceGenerator> userStructIdsToGenerators,
	        List<UUID> duplicates, Map<UUID, Date> userStructIdsToTimesOfRetrieval,
	        Map<UUID, Integer> userStructIdsToAmountCollected,
	        Map<String, Integer> resourcesGained) {

		if (user == null || userStructIds.isEmpty() || userStructIdsToUserStructs.isEmpty()
				|| userStructIdsToGenerators.isEmpty() || userStructIdsToTimesOfRetrieval.isEmpty()) { 
			log.error("user is null, or no struct ids, user structs, structures, or retrieval times . user=" +
					user + "\t userStructIds=" + userStructIds + "\t structIdsToUserStructs=" +
					userStructIdsToUserStructs + "\t userStructIdsToGenerators=" + userStructIdsToGenerators +
					"\t userStructIdsToRetrievalTimes=" + userStructIdsToTimesOfRetrieval);
			return false;
		}
		if (!duplicates.isEmpty()) {
			resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
			log.warn("duplicate struct ids in request. ids=" + duplicates);
		}

		getStructureForUserService().collectFromUserStructs(user, userStructIds,
				userStructIdsToUserStructs, userStructIdsToGenerators,
				userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected,
				resourcesGained);

		return true;
	}

	//uStructId will store the newly created user structure
	private boolean writeChangesToDb(User user, int cashGain, int oilGain,
	  		Map<UUID, StructureForUser> userStructIdsToUserStructs,
	  		Map<UUID, Date> userStructIdsToTimesOfRetrieval) {
		try {

			getStructureForUserService().updateUserStructsLastCollectedTime(
					userStructIdsToTimesOfRetrieval, userStructIdsToUserStructs);

			//GIVE THE CORRECT RESOURCES
			int gemChange = 0;
			getUserService().updateUserResources(user, gemChange, oilGain, cashGain);
			
			return true;
		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	
	public void writeToUserCurrencyHistory(User user, Map<String, Integer> previousMoney,
			Map<String, Integer> resourcesGained, Date curTime,
	  		Map<UUID, StructureForUser> userStructIdsToUserStructs,
	  		Map<UUID, StructureResourceGenerator> userStructIdsToGenerators,
	  		Map<UUID, Date> userStructIdsToTimesOfRetrieval,
	  		Map<UUID, Integer> userStructIdsToAmountCollected) {
		
		UUID userId = user.getId();
		
		Map<String, Integer> currentMoney = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String oil = MobstersDbTables.USER__OIL;
		String cash = MobstersDbTables.USER__CASH;

		String reasonForChange = MobstersTableConstants.UCHRFC__RETRIEVE_CURRENCY_FROM_NORM_STRUCT;
		StringBuilder cashDetailSb = new StringBuilder();
		cashDetailSb.append("(userStructId,time,amount)=");
		StringBuilder oilDetailSb = new StringBuilder();
		oilDetailSb.append("(userStructId,time,amount)=");
		
		//being descriptive, separating cash stuff from oil stuff
		for(UUID id : userStructIdsToAmountCollected.keySet()) {
			StructureResourceGenerator struct = userStructIdsToGenerators.get(id);
			Date t = userStructIdsToTimesOfRetrieval.get(id);
			int amount = userStructIdsToAmountCollected.get(id);

			String type = struct.getResourceType();
			ResourceType rt = ResourceType.valueOf(type);
			if (ResourceType.CASH.equals(rt)) {
				cashDetailSb.append("(");
				cashDetailSb.append(id);
				cashDetailSb.append(",");
				cashDetailSb.append(t);
				cashDetailSb.append(",");
				cashDetailSb.append(amount);
				cashDetailSb.append(")");

			} else if (ResourceType.OIL.equals(rt)) {
				oilDetailSb.append("(");
				oilDetailSb.append(id);
				oilDetailSb.append(",");
				oilDetailSb.append(t);
				oilDetailSb.append(",");
				oilDetailSb.append(amount);
				oilDetailSb.append(")");
			}
		}
		
      	currentMoney.put(oil, user.getOil());
      	currentMoney.put(cash, user.getCash());
      	reasonsForChanges.put(oil, reasonForChange);
      	reasonsForChanges.put(cash, reasonForChange);
      	detailsMap.put(oil, oilDetailSb.toString());
      	detailsMap.put(cash, cashDetailSb.toString());
      	
      	boolean saveToDb = true;
      	getUserCurrencyHistoryService().createUserCurrencyHistories(userId, curTime,
      			resourcesGained, previousMoney, currentMoney, reasonsForChanges,
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

	public StructureResourceGeneratorRetrieveUtils getStructureResourceGeneratorRetrieveUtils() {
		return structureResourceGeneratorRetrieveUtils;
	}

	public void setStructureResourceGeneratorRetrieveUtils(
			StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils) {
		this.structureResourceGeneratorRetrieveUtils = structureResourceGeneratorRetrieveUtils;
	}
}

