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
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventStructureProto.NormStructWaitCompleteRequestProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.NormStructWaitCompleteResponseProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.NormStructWaitCompleteResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventStructureProto.NormStructWaitCompleteResponseProto.NormStructWaitCompleteStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.NormStructWaitCompleteRequestEvent;
import com.lvl6.mobsters.events.response.NormStructWaitCompleteResponseEvent;
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
import com.lvl6.mobsters.utils.QueryConstructionUtil;



@Component
public class NormStructWaitCompleteController extends EventController {

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
	protected QueryConstructionUtil queryConstructionUtil;
	

	
	
	@Override
	public RequestEvent createRequestEvent() {
		return new NormStructWaitCompleteRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_NORM_STRUCT_WAIT_COMPLETE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		NormStructWaitCompleteRequestProto reqProto = 
				((NormStructWaitCompleteRequestEvent) event).getNormStructWaitCompleteRequestProto();

		//get stuff client sent
	    MinimumUserProto senderProto = reqProto.getSender();
	    List<String> userStructIdStrs = reqProto.getUserStructUuidList();
	    Date clientTime = new Date(reqProto.getCurTime());

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);
		List<UUID> userStructIds = getQueryConstructionUtil().createUUIDListFromStrings(userStructIdStrs);

		//response to send back to client
		Builder responseBuilder = NormStructWaitCompleteResponseProto.newBuilder();
		responseBuilder.setStatus(NormStructWaitCompleteStatus.FAIL_OTHER);
		NormStructWaitCompleteResponseEvent resEvent = new NormStructWaitCompleteResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			Map<UUID, StructureForUser> idsToUserStructs = getStructureForUserService()
					.getSpecificOrAllUserStructuresForUser(userId, userStructIds);
			List<StructureForUser> userStructs = new ArrayList<StructureForUser>(
					idsToUserStructs.values());
			
			//get the newRetrievedTimes that checkLegitWaitComplete(...) will calculate
			//userStructs and userStructIds might be modified
			List<Date> newRetrievedTimes = new ArrayList<Date>();
			boolean legitUpgradeNorm = checkLegitWaitComplete(responseBuilder, userStructs,
		      		userStructIds, userId, clientTime, newRetrievedTimes);

			boolean successful = false;
			if (legitUpgradeNorm) {
				//upgrading and building a building is the same thing
		        successful = writeChangesToDb(userStructs, newRetrievedTimes);
			}

			if (successful) {
				responseBuilder.setStatus(NormStructWaitCompleteStatus.SUCCESS);
			}

			//write to client
			resEvent.setNormStructWaitCompleteResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
		} catch (Exception e) {
			log.error("exception in NormStructWaitCompleteController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(NormStructWaitCompleteStatus.FAIL_OTHER);
				resEvent.setNormStructWaitCompleteResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in NormStructWaitCompleteController processRequestEvent", e2);
			}
		}
	}

	//newRetrievedTimes will hold the second return value from this method
	private boolean checkLegitWaitComplete(Builder resBuilder,
		      List<StructureForUser> userStructs, List<UUID> userStructIds,
		      UUID userId, Date clientTime, List<Date> newRetrievedTimes) {
		
		if (userStructs == null || userStructIds == null || clientTime == null || userStructIds.size() != userStructs.size()) {
		      resBuilder.setStatus(NormStructWaitCompleteStatus.FAIL_OTHER);
		      log.error("userStructs is null, userStructIds is null, clientTime is null, or array lengths different. userStructs="
		          + userStructs + ", userStructIds=" + userStructIds + ", clientTime=" + clientTime);
		      return false;
		    }
	    
		//for each user structure complete the ones the client said are done.
	    //replace what client sent with the ones that are actually done
	    List<StructureForUser> validUserStructs = new ArrayList<StructureForUser>();
	    List<UUID> validUserStructIds = new ArrayList<UUID>();
	    
	    List<Date> timesBuildsFinished = getStructureForUserService().calculateValidUserStructs(
	    		clientTime, userStructs, validUserStructIds, validUserStructs);
	    
	    if (userStructs.size() != validUserStructs.size()) {
	    	log.warn("some of what the client sent is invalid. idsClientSent=" +
	    			userStructIds + "\t validIds=" + validUserStructIds);
	    	userStructs.clear();
	    	userStructs.addAll(validUserStructs);
	    	
	    	userStructIds.clear();
	    	userStructIds.addAll(validUserStructIds);
	    }
	    
	    newRetrievedTimes.addAll(timesBuildsFinished);
	    return true;  
	}

	private boolean writeChangesToDb(List<StructureForUser> buildsDone,
	  		List<Date> newRetrievedTimes) {
		try {
			//upgrade the user's struct
			getStructureForUserService().updateUserStructsBuildingIsComplete(buildsDone,
					newRetrievedTimes);

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

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}

