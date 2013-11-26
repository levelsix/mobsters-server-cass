package com.lvl6.mobsters.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.StructureForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureRetrieveUtils;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.widerows.RestrictionOnNumberOfUserStructure;


@Component
public class SpeedUpBuildOrUpgradeStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils; 
	
	@Autowired
	protected StructureForUserService structureForUserService; 

	@Autowired
	protected StructureForUserEntityManager structureForUserEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected UserService userService;

	@Autowired
	protected RestrictionOnNumberOfUserStructure restrictionOnNumberOfUserStructure;

	@Override
	public RequestEvent createRequestEvent() {
		return null;//new SpeedUpBuildOrUpgradeStructureRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;//MobstersEventProtocolRequest.C_BUILD_OR_UPGRADE_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		/*//stuff client sent
		SpeedUpBuildOrUpgradeStructureRequestProto reqProto = 
				((SpeedUpBuildOrUpgradeStructureRequestEvent) event).getSpeedUpBuildOrUpgradeStructureRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();
		String userStructureIdString = reqProto.getUserStructureId();
		UUID userStructureId = UUID.fromString(userStructureIdString);
		boolean constructing = reqProto.getFinishedConstructing();

		//response to send back to client
		Builder responseBuilder = SpeedUpBuildOrUpgradeStructureResponseProto.newBuilder();
		responseBuilder.setStatus(SpeedUpBuildOrUpgradeStructureStatus.FAIL_OTHER);
		
		SpeedUpBuildOrUpgradeStructureResponseEvent resEvent =
				new SpeedUpBuildOrUpgradeStructureResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			StructureForUser us = getUserStructureEntityManager().get().get(userStructureId);
			Structure s = getUserStructureService().getStructureCorrespondingToUserStructure(us);

			
			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					us, s, constructing, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, us, s, clientDate);
			}

			if (successful) {
				responseBuilder.setFinishedConstructing(true);
				responseBuilder.setStatus(SpeedUpBuildOrUpgradeStructureStatus.SUCCESS);
			}

			//write to client
			
			resEvent.setSpeedUpBuildOrUpgradeStructureResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in SpeedUpBuildOrUpgradeStructureController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(SpeedUpBuildOrUpgradeStructureStatus.FAIL_OTHER);
				resEvent.setSpeedUpBuildOrUpgradeStructureResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in SpeedUpBuildOrUpgradeStructureController processRequestEvent", e2);
			}
		}*/
	}
/*
	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, StructureForUser us, Structure s, boolean constructing, Date clientDate) throws ConnectionException {
		int secondsRemaining;
		//TODO: FIX THIS
//		if(us.getLvl() == 1) {
//			long secondsPassed = (clientDate.getTime() - us.getPurchaseTime().getTime())/1000;
//			secondsRemaining = (int)(s.getBuildTimeSeconds() - secondsPassed);
//		}
//		else {
			long secondsPassed = (clientDate.getTime() - us.getStartUpgradeTime().getTime())/1000;
			secondsRemaining = (int)(s.getBuildTimeSeconds() - secondsPassed);
//		}
		
		if(getUserService().calculateGemCostForSpeedUp(secondsRemaining) > inDb.getGems()) {
			log.error("user doesn't have enough gems to speed up");
			responseBuilder.setStatus(SpeedUpBuildOrUpgradeStructureStatus.FAIL_NOT_ENOUGH_GEMS);
			return false;
		}
		
		if(!constructing) {
			log.error("structure not constructing, nothing to speed up");
			responseBuilder.setStatus(SpeedUpBuildOrUpgradeStructureStatus.FAIL_OTHER);
			return false;
		}

		responseBuilder.setStatus(SpeedUpBuildOrUpgradeStructureStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, StructureForUser us,
			Structure s, Date clientDate) {
		try {
			//TODO: FIX THIS
//			if(us.getLvl() == 1) {
//				long secondsPassed = (clientDate.getTime() - us.getPurchaseTime().getTime())/1000;
//				int secondsRemaining = (int)(s.getBuildTimeSeconds() - secondsPassed);
//				inDb.setGems(inDb.getGems() - getUserService().calculateGemCostForSpeedUp(secondsRemaining));
//			}
//			else {
				long secondsPassed = (clientDate.getTime() - us.getStartUpgradeTime().getTime())/1000;
				int secondsRemaining = (int)(s.getBuildTimeSeconds() - secondsPassed);
				inDb.setGems(inDb.getGems() - getUserService().calculateGemCostForSpeedUp(secondsRemaining));
//			}
			
			
			//update user
			getUserEntityManager().get().put(inDb);

			//and update his user structure rows
			us.setFinishedConstructing(true);
			getUserStructureEntityManager().get().put(us);
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		

	

	public StructureForUserService getUserStructureService() {
		return structureForUserService;
	}

	public void setUserStructureService(StructureForUserService structureForUserService) {
		this.userStructureService = structureForUserService;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}

	public StructureForUserEntityManager getUserStructureEntityManager() {
		return structureForUserEntityManager;
	}

	public void setUserStructureEntityManager(
			StructureForUserEntityManager structureForUserEntityManager) {
		this.userStructureEntityManager = structureForUserEntityManager;
	}

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}
	
	public RestrictionOnNumberOfUserStructure getRestrictionOnNumberOfUserStructure() {
		return restrictionOnNumberOfUserStructure;
	}

	public void setRestrictionOnNumberOfUserStructure(
			RestrictionOnNumberOfUserStructure restrictionOnNumberOfUserStructure) {
		this.restrictionOnNumberOfUserStructure = restrictionOnNumberOfUserStructure;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

*/

}