package com.lvl6.aoc2.controller;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.UserEntityManager;
import com.lvl6.aoc2.entitymanager.UserEquipEntityManager;
import com.lvl6.aoc2.entitymanager.UserEquipRepairEntityManager;
import com.lvl6.aoc2.entitymanager.UserStructureEntityManager;
import com.lvl6.aoc2.entitymanager.staticdata.EquipmentRetrieveUtils;
import com.lvl6.aoc2.eventprotos.CollectUserStructureEventProto.CollectUserStructureRequestProto;
import com.lvl6.aoc2.eventprotos.CollectUserStructureEventProto.CollectUserStructureResponseProto;
import com.lvl6.aoc2.eventprotos.CollectUserStructureEventProto.CollectUserStructureResponseProto.CollectUserStructureStatus;
import com.lvl6.aoc2.eventprotos.CollectUserStructureEventProto.CollectUserStructureResponseProto.Builder;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.events.request.CollectUserStructureRequestEvent;
import com.lvl6.aoc2.events.response.CollectUserStructureResponseEvent;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.aoc2.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.aoc2.po.Structure;
import com.lvl6.aoc2.po.User;
import com.lvl6.aoc2.po.UserStructure;
import com.lvl6.aoc2.services.user.UserService;
import com.lvl6.aoc2.services.userstructure.UserStructureService;
import com.lvl6.aoc2.widerows.RestrictionOnNumberOfUserStructure;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@Component
public class CollectUserStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected EquipmentRetrieveUtils equipmentRetrieveUtils; 
	
	@Autowired
	protected UserStructureService userStructureService; 
	
	@Autowired
	protected UserStructureEntityManager userStructureEntityManager;

	@Autowired
	protected UserEquipRepairEntityManager userEquipRepairEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected UserEquipEntityManager userEquipEntityManager;
	
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
		return AocTwoEventProtocolRequest.C_BUILD_OR_UPGRADE_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
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
			UserStructure us = getUserStructureEntityManager().get().get(userStructureId);
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
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, UserStructure us, Structure s, Date clientDate) throws ConnectionException {
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

	private boolean writeChangesToDb(User inDb, UserStructure us, Date clientDate) {
		try {
			us.setFinishedConstructing(true);
			getUserStructureEntityManager().get().put(us);
			
		return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		



	public UserStructureService getUserStructureService() {
		return userStructureService;
	}

	public void setUserStructureService(UserStructureService userStructureService) {
		this.userStructureService = userStructureService;
	}

	public UserEquipRepairEntityManager getUserEquipRepairEntityManager() {
		return userEquipRepairEntityManager;
	}

	public void setUserEquipRepairEntityManager(
			UserEquipRepairEntityManager userEquipRepairEntityManager) {
		this.userEquipRepairEntityManager = userEquipRepairEntityManager;
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

	public EquipmentRetrieveUtils getEquipmentRetrieveUtils() {
		return equipmentRetrieveUtils;
	}

	public void setEquipmentRetrieveUtils(
			EquipmentRetrieveUtils equipmentRetrieveUtils) {
		this.equipmentRetrieveUtils = equipmentRetrieveUtils;
	}

	public UserEquipEntityManager getUserEquipEntityManager() {
		return userEquipEntityManager;
	}

	public void setUserEquipEntityManager(
			UserEquipEntityManager userEquipEntityManager) {
		this.userEquipEntityManager = userEquipEntityManager;
	}

	public UserStructureEntityManager getUserStructureEntityManager() {
		return userStructureEntityManager;
	}

	public void setUserStructureEntityManager(
			UserStructureEntityManager userStructureEntityManager) {
		this.userStructureEntityManager = userStructureEntityManager;
	}


	
	
	
	
	
	
	
	
	

}