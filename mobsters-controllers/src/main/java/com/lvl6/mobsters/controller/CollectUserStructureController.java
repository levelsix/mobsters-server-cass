package com.lvl6.mobsters.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.StructureForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureLabRetrieveUtils;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.CollectUserStructureRequestEvent;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.widerows.RestrictionOnNumberOfUserStructure;


@Component
public class CollectUserStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected StructureLabRetrieveUtils structureLabRetrieveUtils; 
	
	@Autowired
	protected StructureForUserService structureForUserService; 
	
	@Autowired
	protected StructureForUserEntityManager structureForUserEntityManager;

	@Autowired
	protected MonsterHealingForUserEntityManager monsterHealingForUserEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected MonsterForUserEntityManager monsterForUserEntityManager;
	
	@Autowired
	protected UserService userService;

	@Autowired
	protected RestrictionOnNumberOfUserStructure restrictionOnNumberOfUserStructure;

	@Override
	public RequestEvent createRequestEvent() {
		return new CollectUserStructureRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;//MobstersEventProtocolRequest.C_BUILD_OR_UPGRADE_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
/*		//stuff client sent
		CollectUserStructureRequestProto reqProto = 
				((CollectUserStructureRequestEvent) event).getCollectUserStructureRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		String userStructureIdString = reqProto.getUserStructureId();
		UUID userStructureId = UUID.fromString(userStructureIdString);
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();

		//response to send back to client
		Builder responseBuilder = CollectUserStructureResponseProto.newBuilder();
		responseBuilder.setStatus(CollectUserStructureStatus.FAIL_OTHER);
		CollectUserStructureResponseEvent resEvent =
				new CollectUserStructureResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			StructureForUser us = getUserStructureEntityManager().get().get(userStructureId);
			Structure s = getUserStructureService().getStructureCorrespondingToUserStructure(us);
			
			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					us, s, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, us, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(CollectUserStructureStatus.SUCCESS);
			}

			//write to client
			resEvent.setCollectUserStructureResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in CollectUserStructureController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(CollectUserStructureStatus.FAIL_OTHER);
				resEvent.setCollectUserStructureResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in CollectUserStructureController processRequestEvent", e2);
			}
		}*/
	}
/*
	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, StructureForUser us, Structure s, Date clientDate) throws ConnectionException {
		if (null == inDb || null == us) {
			log.error("unexpected error: no user or list of equips to fix exist");
			return false;
		}
		
		if(s.getLvl()==1) {
			if(us.getPurchaseTime().getTime() + s.getBuildTimeSeconds()*1000 > clientDate.getTime()) {
				log.error("not done building yet");
				responseBuilder.setStatus(CollectUserStructureStatus.FAIL_NOT_COMPLETE);
				return false;
			}
		}
		else {
			if(us.getStartUpgradeTime().getTime() + s.getBuildTimeSeconds()*1000 > clientDate.getTime()) {
				log.error("not done upgrading yet");
				responseBuilder.setStatus(CollectUserStructureStatus.FAIL_NOT_COMPLETE);
				return false;
			}
		}
		
		
		responseBuilder.setStatus(CollectUserStructureStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, StructureForUser us, Date clientDate) {
		try {
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

	public MonsterHealingForUserEntityManager getUserEquipRepairEntityManager() {
		return monsterHealingForUserEntityManager;
	}

	public void setUserEquipRepairEntityManager(
			MonsterHealingForUserEntityManager monsterHealingForUserEntityManager) {
		this.monsterHealingForUserEntityManager = monsterHealingForUserEntityManager;
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

	public StructureLabRetrieveUtils getEquipmentRetrieveUtils() {
		return structureLabRetrieveUtils;
	}

	public void setEquipmentRetrieveUtils(
			StructureLabRetrieveUtils structureLabRetrieveUtils) {
		this.equipmentRetrieveUtils = structureLabRetrieveUtils;
	}

	public MonsterForUserEntityManager getUserEquipEntityManager() {
		return monsterForUserEntityManager;
	}

	public void setUserEquipEntityManager(
			MonsterForUserEntityManager monsterForUserEntityManager) {
		this.monsterForUserEntityManager = monsterForUserEntityManager;
	}

	public StructureForUserEntityManager getUserStructureEntityManager() {
		return structureForUserEntityManager;
	}

	public void setUserStructureEntityManager(
			StructureForUserEntityManager structureForUserEntityManager) {
		this.userStructureEntityManager = structureForUserEntityManager;
	}

*/
	

}
