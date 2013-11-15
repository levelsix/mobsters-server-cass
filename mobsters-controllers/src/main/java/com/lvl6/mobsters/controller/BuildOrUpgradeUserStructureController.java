package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.UserEntityManager;
import com.lvl6.mobsters.entitymanager.UserStructureEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureRetrieveUtils;
import com.lvl6.mobsters.eventprotos.BuildOrUpgradeStructureEventProto.BuildOrUpgradeStructureRequestProto;
import com.lvl6.mobsters.eventprotos.BuildOrUpgradeStructureEventProto.BuildOrUpgradeStructureResponseProto;
import com.lvl6.mobsters.eventprotos.BuildOrUpgradeStructureEventProto.ResourceCostType;
import com.lvl6.mobsters.eventprotos.BuildOrUpgradeStructureEventProto.BuildOrUpgradeStructureResponseProto.BuildOrUpgradeStructureStatus;
import com.lvl6.mobsters.eventprotos.BuildOrUpgradeStructureEventProto.BuildOrUpgradeStructureResponseProto.Builder;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.BuildOrUpgradeStructureRequestEvent;
import com.lvl6.mobsters.events.response.BuildOrUpgradeStructureResponseEvent;
import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.Structure;
import com.lvl6.mobsters.po.User;
import com.lvl6.mobsters.po.UserStructure;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.userstructure.UserStructureService;
import com.lvl6.mobsters.widerows.RestrictionOnNumberOfUserStructure;
import com.lvl6.mobsters.widerows.WideRowValue;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@Component
public class BuildOrUpgradeUserStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils; 
	
	@Autowired
	protected UserStructureService userStructureService; 

	@Autowired
	protected UserStructureEntityManager userStructureEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected UserService userService;

	@Autowired
	protected RestrictionOnNumberOfUserStructure restrictionOnNumberOfUserStructure;

	@Override
	public RequestEvent createRequestEvent() {
		return new BuildOrUpgradeStructureRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_BUILD_OR_UPGRADE_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		BuildOrUpgradeStructureRequestProto reqProto = 
				((BuildOrUpgradeStructureRequestEvent) event).getBuildOrUpgradeStructureRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		boolean usingGems = reqProto.hasUsingGems();
		boolean isConstructing = reqProto.getIsBuild();
		Date clientDate = new Date();
		String userStructureIdString = reqProto.getUserStructureId();
		UUID userStructureId = UUID.fromString(userStructureIdString);

		//response to send back to client
		Builder responseBuilder = BuildOrUpgradeStructureResponseProto.newBuilder();
		responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_OTHER);
		BuildOrUpgradeStructureResponseEvent resEvent =
				new BuildOrUpgradeStructureResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			UserStructure us = getUserStructureEntityManager().get().get(userStructureId);
			List<Structure> sList = new ArrayList<Structure>();

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					us, sList, usingGems, isConstructing, clientDate);

			boolean successful = false;
			if (validRequest) {
				Structure s = sList.get(0);
				successful = writeChangesToDb(inDb, us, s, usingGems, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(BuildOrUpgradeStructureStatus.SUCCESS);
			}

			//write to client
			resEvent.setBuildOrUpgradeStructureResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in BuildOrUpgradeStructureController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_OTHER);
				resEvent.setBuildOrUpgradeStructureResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in BuildOrUpgradeStructureController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, UserStructure us, List<Structure> sList, boolean usingGems, boolean isConstructing, Date clientDate) throws ConnectionException {
		if (null == inDb || null == us) {
			log.error("unexpected error: no user exists. sender=" + sender +
					"\t inDb=" + inDb + "\t us=" + us);
			return false;
		}

		UUID id = us.getId();
		UUID structureId = us.getId();
		Structure s = getStructureRetrieveUtils().getStructureForId(id);

		if (null == s) {
			log.error("unexpected error: no structure with id exists. id=" + structureId);
			responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_NO_STRUCTURE_EXISTS);
			return false;
		}

		//check if user meets level requirement of structure
		if(inDb.getLvl() < s.getUserLvlRequired()) {
			log.error("user is not high enough level to build/upgrade. user level=" + inDb.getLvl() + 
					", required level: " + s.getUserLvlRequired());
			responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_NOT_AT_REQUIRED_LEVEL);
			return false;
		}
			
		//check if user's structure has a further upgrade
		if(structureRetrieveUtils.getUpgradedStructure(s) == null) {
			log.error("no upgrade of user's structure with id: " + id + "exists");
			responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_STRUCTURE_AT_MAX_LEVEL);
			return false;
		}
		
		if(!isConstructing) {
			log.error("structure not constructing");
			responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_OTHER);
			return false;
		}
		
		if(!buildIsCompleteBeforeAttemptingUpgrade(s, us, clientDate)) {
			log.error("user trying to upgrade before building is complete");
			responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_CANT_UPGRADE_WHILE_BUILDING);
			return false;
		}
		
		if(us.getStartUpgradeTime().getTime() < us.getLastCollectTime().getTime()) {
			log.error("the upgrade time " + us.getStartUpgradeTime() + " is before the last time the building was retrieved:"
			          + us.getLastCollectTime());
			responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_OTHER);
			return false;
		}
		
		if(!usingGems) {
			if(s.getBuildCostResourceType() == ResourceCostType.GOLD_VALUE) {
				if(inDb.getGold() < s.getBuildCost()) {
					log.error("user doesn't have enough gold to build new building");
					responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_INSUFFICIENT_RESOURCES);
					return false;
				}
			} else if(s.getBuildCostResourceType() == ResourceCostType.TONIC_VALUE) {
				if(inDb.getTonic() < s.getBuildCost()) {
					log.error("user doesn't have enough tonic to build new building");
					responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_INSUFFICIENT_RESOURCES);
					return false;
				}
			} else {
				log.error("structure doesn't cost either gold or tonic...strange...Hi Ashwin");
				responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_OTHER);
				return false;
			}
		}
		else {
			if(s.getBuildCostResourceType() == ResourceCostType.GOLD_VALUE) {
				int missingResources = s.getBuildCost() - inDb.getGold();
				if(inDb.getGems() < getUserService().calculateGemCostForMissingResources(inDb, missingResources, ResourceCostType.GOLD_VALUE)) {
					log.error("user doesn't have enough gems to buy remaining resources");
					responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_INSUFFICIENT_RESOURCES);
					return false;
				}
			}
			else {
				int missingResources = s.getBuildCost() - inDb.getTonic();
				if(inDb.getGems() < getUserService().calculateGemCostForMissingResources(inDb, missingResources, ResourceCostType.TONIC_VALUE)) {
					log.error("user doesn't have enough gems to buy remaining resources");
					responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_INSUFFICIENT_RESOURCES);
					return false;
				}
			}
		}
		
		int count=0;
		List<UserStructure> userStructs = getUserStructureService().getAllUserStructuresForUser(inDb.getId());
		for(UserStructure us2: userStructs) {
			if(us2.isFinishedConstructing())
				count++;
		}
		
		if(count >= 2) {
			log.error("user already has max construction occuring");
			responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_MAXED_CONSTRUCTION);
			return false;
		}
		
		int userLevel = inDb.getLvl();
		int numAllowed = 0;
		//check if there's a restriction on the number of a certain structure you can have
		Collection<WideRowValue<Integer, UUID, Integer>> savedValues =
				getRestrictionOnNumberOfUserStructure().getEntireRow(userLevel);
		if(!savedValues.iterator().hasNext()) {
			log.error("restriction on user structure table is janky");
			responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_OTHER);
			return false;
		}
		else {
			while(savedValues.iterator().hasNext()) {
				WideRowValue<Integer, UUID, Integer> wideRow = savedValues.iterator().next();
				if(wideRow.getColumn()==s.getId()) {
					numAllowed = wideRow.getValue();
				}
			}
		}
		//count how many of this specific building the user is trying to
		//build/upgrade he already has
		int count2 = 0;
		for(UserStructure us3: userStructs) {
			if(us3.getId() == s.getId()) {
				count2++;
			}
		}
		
		if(count2 >= numAllowed) {
			log.error("user has greater than or equal to max allowed of this specific structure. Also fuck Ashwin -Byron");
			responseBuilder.setStatus(BuildOrUpgradeStructureStatus.FAIL_RESTRICTION_ON_NUMBER_OF_STRUCTURES);
			return false;
		}
		
		sList.add(s);
		responseBuilder.setStatus(BuildOrUpgradeStructureStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, UserStructure us,
			Structure s, boolean usingGems, Date clientDate) {
		try {
			int missingResources = 0;
			if(!usingGems){
				if(s.getBuildCostResourceType() == ResourceCostType.GOLD_VALUE) {
					inDb.setGold(inDb.getGold()-s.getBuildCost());
				}
				else {
					inDb.setTonic(inDb.getTonic()-s.getBuildCost());
				}
			}
			else if(s.getBuildCostResourceType() == ResourceCostType.GOLD_VALUE) {
				missingResources = s.getBuildCost() - inDb.getGold();
				inDb.setGems(inDb.getGems()-getUserService().calculateGemCostForMissingResources(inDb, missingResources, ResourceCostType.GOLD_VALUE));
				inDb.setGold(0);
			}
			else {
				missingResources = s.getBuildCost() - inDb.getTonic();
				inDb.setGems(inDb.getGems()-getUserService().calculateGemCostForMissingResources(inDb, missingResources, ResourceCostType.TONIC_VALUE));
				inDb.setTonic(0);
			}
			
			//update user
			getUserEntityManager().get().put(inDb);

			UserStructure us2 = new UserStructure();
			
			us2.setId(UUID.randomUUID());
			us2.setUserId(inDb.getId());
			us2.setStructureId(s.getId());
			//us2.setLvl(s.getLvl());
			us2.setPurchaseTime(clientDate);
			us2.setFinishedConstructing(false);
			us2.setLevelOfUserWhenUpgrading(inDb.getLvl());
			getUserStructureEntityManager().get().put(us2);
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		

	private boolean buildIsCompleteBeforeAttemptingUpgrade(Structure s, UserStructure us, Date clientDate) {
		int buildTime = s.getBuildTimeSeconds()*1000;
		long purchaseTime = us.getPurchaseTime().getTime();
		if(buildTime + purchaseTime < clientDate.getTime())
			return true;
		else return false;
	}
	
	

	
	public UserStructureService getUserStructureService() {
		return userStructureService;
	}

	public void setUserStructureService(UserStructureService userStructureService) {
		this.userStructureService = userStructureService;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}

	public UserStructureEntityManager getUserStructureEntityManager() {
		return userStructureEntityManager;
	}

	public void setUserStructureEntityManager(
			UserStructureEntityManager userStructureEntityManager) {
		this.userStructureEntityManager = userStructureEntityManager;
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


	
	
	
	
	
	
	
	
	

}