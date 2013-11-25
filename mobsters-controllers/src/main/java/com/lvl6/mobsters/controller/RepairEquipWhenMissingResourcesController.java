package com.lvl6.mobsters.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureLabRetrieveUtils;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.time.TimeUtils;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class RepairEquipWhenMissingResourcesController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected StructureLabRetrieveUtils structureLabRetrieveUtils; 

	
//	@Autowired
//	protected UserEquipRepairService userEquipRepairService; 
	
	@Autowired
	protected MonsterHealingForUserEntityManager monsterHealingForUserEntityManager;

	@Autowired
	protected MonsterForUserService monsterForUserService;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected MonsterForUserEntityManager monsterForUserEntityManager;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UserService userService;




	@Override
	public RequestEvent createRequestEvent() {
		return null;//new RepairEquipWhenMissingResourcesRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;//MobstersEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		/*//stuff client sent
		RepairEquipWhenMissingResourcesRequestProto reqProto = 
				((RepairEquipWhenMissingResourcesRequestEvent) event).getRepairEquipWhenMissingResourcesRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		String userEquipIdString = reqProto.getUserEquipId();
		Date clientDate = new Date(reqProto.getClientTimeMillis());

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		UUID userEquipId = UUID.fromString(userEquipIdString);

		//response to send back to client
		Builder responseBuilder = RepairEquipWhenMissingResourcesResponseProto.newBuilder();
		responseBuilder.setStatus(RepairEquipWhenMissingResourcesStatus.FAIL_OTHER);
		RepairEquipWhenMissingResourcesResponseEvent resEvent =
				new RepairEquipWhenMissingResourcesResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			MonsterForUser ue = getUserEquipEntityManager().get().get(userEquipId);
			StructureLab e = getUserEquipService().getEquipmentCorrespondingToUserEquip(ue);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender,
					inDb, ue, e, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, ue, e, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(RepairEquipWhenMissingResourcesStatus.SUCCESS);
			}

			//write to client
			resEvent.setRepairEquipWhenMissingResourcesResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RepairEquipWhenMissingResourcesController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(RepairEquipWhenMissingResourcesStatus.FAIL_OTHER);
				resEvent.setRepairEquipWhenMissingResourcesResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in RepairEquipWhenMissingResourcesController processRequestEvent", e2);
			}
		}*/
	}


/*
	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, MonsterForUser ue, StructureLab e, Date clientDate) throws Exception {

		//CHECK IF TIMES ARE IN SYNC
		if (!getTimeUtils().isSynchronizedWithServerTime(clientDate)) {
			log.error("user error: client time diverges from server time. clientTime="
					+ clientDate + ", approximateServerTime=" + new Date());
			responseBuilder.setStatus(RepairEquipWhenMissingResourcesStatus.FAIL_OTHER);
			return false;
		}

		//check if user has enough gems
		int missingResources = getUserEquipRepairService().calculateSingleUserEquipRepairCost(ue) - inDb.getGold();
		if(inDb.getGems() < getUserService().calculateGemCostForMissingResources(inDb, missingResources, ResourceCostType.GOLD_VALUE)) {
			log.error("user doesn't have enough gems to buy remaining gold for repair");
			responseBuilder.setStatus(RepairEquipWhenMissingResourcesStatus.FAIL_NOT_ENOUGH_GEMS);
			return false;
		}

		return true;
	}

	
	private boolean writeChangesToDb(User inDb, MonsterForUser ue, StructureLab e, Date clientDate) {
		try {
			//remove gems/gold from user
			int missingResources = getUserEquipRepairService().calculateSingleUserEquipRepairCost(ue) - inDb.getGold();
			inDb.setGems(inDb.getGems() - getUserService().calculateGemCostForMissingResources(inDb, missingResources, ResourceCostType.GOLD_VALUE));
			inDb.setGold(0);
			getUserEntityManager().get().put(inDb);


			//add userequip to userequiprepair
			MonsterHealingForUser uer = new MonsterHealingForUser();
			uer.setDurability(ue.getDurability());
			uer.setEnteredQueue(clientDate);
			UUID equipId = e.getId();
			uer.setEquipId(equipId);
//			uer.setEquipLevel(ue.getEquipLevel());
			uer.setId(UUID.randomUUID());
			uer.setUserId(inDb.getId());
			uer.setDungeonRoomOrChestAcquiredFrom(ue.getDungeonRoomOrChestAcquiredFrom());
			uer.setLevelOfUserWhenAcquired(ue.getLevelOfUserWhenAcquired());
			uer.setTimeAcquired(ue.getTimeAcquired());
			
			String cqlquery = "select * from user_equip_repair where user_id=" + inDb.getId() + ";";
			List <MonsterHealingForUser> uerList = getUserEquipRepairEntityManager().get().find(cqlquery);
			long expectedEndTimeOfQueue = uerList.get(0).getExpectedStart().getTime() + 
					getEquipmentRetrieveUtils().getEquipmentForId(uerList.get(0).getId()).getDurabilityFixTimeConstant()*(long)(1-uerList.get(0).getDurability());
			for(MonsterHealingForUser uer2 : uerList) {
				if((uer2.getExpectedStart().getTime() + getEquipmentRetrieveUtils().getEquipmentForId(uerList.get(0).getId()).getDurabilityFixTimeConstant()*(long)(1-uerList.get(0).getDurability())) 
						> expectedEndTimeOfQueue) {
					expectedEndTimeOfQueue = uer2.getExpectedStart().getTime() + 
							getEquipmentRetrieveUtils().getEquipmentForId(uerList.get(0).getId()).getDurabilityFixTimeConstant()*(long)(1-uerList.get(0).getDurability());
				}
			}
			Date d = new Date(expectedEndTimeOfQueue);
			uer.setExpectedStart(d);
			
			getUserEquipRepairEntityManager().get().put(uer);
			
			//delete this equip from user equips
			getUserEquipEntityManager().get().delete(ue.getId());
			
			return true;

		} catch (Exception e2) {
			log.error("unexpected error: problem with saving to db.", e2);
		}
		return false;
	}



	public StructureLabRetrieveUtils getEquipmentRetrieveUtils() {
		return structureLabRetrieveUtils;
	}

	public void setEquipmentRetrieveUtils(
			StructureLabRetrieveUtils structureLabRetrieveUtils) {
		this.equipmentRetrieveUtils = structureLabRetrieveUtils;
	}

	public UserEquipRepairService getUserEquipRepairService() {
		return userEquipRepairService;
	}

	public void setUserEquipRepairService(
			UserEquipRepairService userEquipRepairService) {
		this.userEquipRepairService = userEquipRepairService;
	}

	public MonsterForUserService getUserEquipService() {
		return monsterForUserService;
	}

	public void setUserEquipService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public MonsterForUserEntityManager getUserEquipEntityManager() {
		return monsterForUserEntityManager;
	}

	public void setUserEquipEntityManager(
			MonsterForUserEntityManager monsterForUserEntityManager) {
		this.monsterForUserEntityManager = monsterForUserEntityManager;
	}



	public MonsterHealingForUserEntityManager getUserEquipRepairEntityManager() {
		return monsterHealingForUserEntityManager;
	}

	public void setUserEquipRepairEntityManager(
			MonsterHealingForUserEntityManager monsterHealingForUserEntityManager) {
		this.monsterHealingForUserEntityManager = monsterHealingForUserEntityManager;
	}

*/
}

