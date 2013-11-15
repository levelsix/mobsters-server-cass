package com.lvl6.mobsters.controller;


import java.util.Date;

import java.util.List;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.TimeUtils;
import com.lvl6.mobsters.entitymanager.UserEntityManager;
import com.lvl6.mobsters.entitymanager.UserEquipEntityManager;
import com.lvl6.mobsters.entitymanager.UserEquipRepairEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.EquipmentRetrieveUtils;
import com.lvl6.mobsters.eventprotos.BuildOrUpgradeStructureEventProto.ResourceCostType;
import com.lvl6.mobsters.eventprotos.RepairEquipWhenMissingResourcesEventProto.RepairEquipWhenMissingResourcesRequestProto;
import com.lvl6.mobsters.eventprotos.RepairEquipWhenMissingResourcesEventProto.RepairEquipWhenMissingResourcesResponseProto;
import com.lvl6.mobsters.eventprotos.RepairEquipWhenMissingResourcesEventProto.RepairEquipWhenMissingResourcesResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.RepairEquipWhenMissingResourcesEventProto.RepairEquipWhenMissingResourcesResponseProto.RepairEquipWhenMissingResourcesStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.RepairEquipWhenMissingResourcesRequestEvent;
import com.lvl6.mobsters.events.response.RepairEquipWhenMissingResourcesResponseEvent;
import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.User;
import com.lvl6.mobsters.po.UserEquip;
import com.lvl6.mobsters.po.UserEquipRepair;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.userequip.UserEquipService;
import com.lvl6.mobsters.services.userequiprepair.UserEquipRepairService;


@Component
public class RepairEquipWhenMissingResourcesController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected EquipmentRetrieveUtils equipmentRetrieveUtils; 

	
	@Autowired
	protected UserEquipRepairService userEquipRepairService; 
	
	@Autowired
	protected UserEquipRepairEntityManager userEquipRepairEntityManager;

	@Autowired
	protected UserEquipService userEquipService;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected UserEquipEntityManager userEquipEntityManager;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UserService userService;




	@Override
	public RequestEvent createRequestEvent() {
		return new RepairEquipWhenMissingResourcesRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
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
			UserEquip ue = getUserEquipEntityManager().get().get(userEquipId);
			Equipment e = getUserEquipService().getEquipmentCorrespondingToUserEquip(ue);

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
		}
	}



	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, UserEquip ue, Equipment e, Date clientDate) throws Exception {

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

	
	private boolean writeChangesToDb(User inDb, UserEquip ue, Equipment e, Date clientDate) {
		try {
			//remove gems/gold from user
			int missingResources = getUserEquipRepairService().calculateSingleUserEquipRepairCost(ue) - inDb.getGold();
			inDb.setGems(inDb.getGems() - getUserService().calculateGemCostForMissingResources(inDb, missingResources, ResourceCostType.GOLD_VALUE));
			inDb.setGold(0);
			getUserEntityManager().get().put(inDb);


			//add userequip to userequiprepair
			UserEquipRepair uer = new UserEquipRepair();
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
			List <UserEquipRepair> uerList = getUserEquipRepairEntityManager().get().find(cqlquery);
			long expectedEndTimeOfQueue = uerList.get(0).getExpectedStart().getTime() + 
					getEquipmentRetrieveUtils().getEquipmentForId(uerList.get(0).getId()).getDurabilityFixTimeConstant()*(long)(1-uerList.get(0).getDurability());
			for(UserEquipRepair uer2 : uerList) {
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



	public EquipmentRetrieveUtils getEquipmentRetrieveUtils() {
		return equipmentRetrieveUtils;
	}

	public void setEquipmentRetrieveUtils(
			EquipmentRetrieveUtils equipmentRetrieveUtils) {
		this.equipmentRetrieveUtils = equipmentRetrieveUtils;
	}

	public UserEquipRepairService getUserEquipRepairService() {
		return userEquipRepairService;
	}

	public void setUserEquipRepairService(
			UserEquipRepairService userEquipRepairService) {
		this.userEquipRepairService = userEquipRepairService;
	}

	public UserEquipService getUserEquipService() {
		return userEquipService;
	}

	public void setUserEquipService(UserEquipService userEquipService) {
		this.userEquipService = userEquipService;
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

	public UserEquipEntityManager getUserEquipEntityManager() {
		return userEquipEntityManager;
	}

	public void setUserEquipEntityManager(
			UserEquipEntityManager userEquipEntityManager) {
		this.userEquipEntityManager = userEquipEntityManager;
	}



	public UserEquipRepairEntityManager getUserEquipRepairEntityManager() {
		return userEquipRepairEntityManager;
	}

	public void setUserEquipRepairEntityManager(
			UserEquipRepairEntityManager userEquipRepairEntityManager) {
		this.userEquipRepairEntityManager = userEquipRepairEntityManager;
	}


	

}

