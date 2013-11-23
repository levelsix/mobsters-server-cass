package com.lvl6.mobsters.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.EquipmentRetrieveUtils;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.CollectUserEquipRequestEvent;
import com.lvl6.mobsters.services.user.UserService;
//import com.lvl6.mobsters.services.userequiprepair.UserEquipRepairService;
import com.lvl6.mobsters.widerows.RestrictionOnNumberOfUserStructure;


@Component
public class CollectUserEquipController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected EquipmentRetrieveUtils equipmentRetrieveUtils; 
//	
//	@Autowired
//	protected UserEquipRepairService userEquipRepairService; 

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
		return new CollectUserEquipRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;//MobstersEventProtocolRequest.C_BUILD_OR_UPGRADE_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		/*//stuff client sent
		CollectUserEquipRequestProto reqProto = 
				((CollectUserEquipRequestEvent) event).getCollectUserEquipRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		List<UserEquipRepairProto> uerList = reqProto.getUerListList();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();

		//response to send back to client
		Builder responseBuilder = CollectUserEquipResponseProto.newBuilder();
		responseBuilder.setStatus(CollectUserEquipStatus.FAIL_OTHER);
		CollectUserEquipResponseEvent resEvent =
				new CollectUserEquipResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			List<UserEquipRepairProto> notFinishedFixingList = new ArrayList<>();

			
			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					uerList, notFinishedFixingList, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, uerList, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(CollectUserEquipStatus.SUCCESS);
			}

			//write to client
			resEvent.setCollectUserEquipResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			responseBuilder.addAllUer2List(notFinishedFixingList);

		} catch (Exception e) {
			log.error("exception in CollectUserEquipController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(CollectUserEquipStatus.FAIL_OTHER);
				resEvent.setCollectUserEquipResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in CollectUserEquipController processRequestEvent", e2);
			}
		}*/
	}
/*
	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, List<UserEquipRepairProto> uerList, List<UserEquipRepairProto> notFinishedFixingList, Date clientDate) throws ConnectionException {
		if (null == inDb || null == uerList) {
			log.error("unexpected error: no user or list of equips to fix exist");
			return false;
		}
		boolean bool = false;
		
		for(UserEquipRepairProto uer: uerList) {
			String equipId = uer.getEquipId();
			//UUID equipId = UUID.fromString(equipName);
			Equipment e = getEquipmentRetrieveUtils().getEquipmentCorrespondingToId(equipId, null);
			long finishTime = uer.getExpectedStartMillis() + e.getDurabilityFixTimeConstant()*(long)(1-e.getDurability()); 
			if(finishTime > clientDate.getTime()) {
				bool = true;
				log.error("an equip's expected finish time has not arrived yet");
				responseBuilder.setStatus(CollectUserEquipStatus.FAIL_NOT_COMPLETE);
				notFinishedFixingList.add(uer);
			}
		}
		
		if(bool) {
			return false;
		}
		
		responseBuilder.setStatus(CollectUserEquipStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, List<UserEquipRepairProto> uerList, Date clientDate) {
		try {
			//delete finished equips from user equip repair and add to user equip
			for(UserEquipRepairProto uer: uerList) {
				Date d = new Date(uer.getQueuedTimeMillis());
				String cqlquery = "select * from user_equip_repair where entered_queue=" + d + ";";
				List <MonsterHealingForUser> uerList2 = getUserEquipRepairEntityManager().get().find(cqlquery);
				getUserEquipRepairEntityManager().get().delete(uerList2.get(0).getId());
				MonsterHealingForUser uer2 = uerList2.get(0);
				MonsterForUser ue = new MonsterForUser();
				ue.setDungeonRoomOrChestAcquiredFrom(uer2.getDungeonRoomOrChestAcquiredFrom());
				ue.setDurability(100.0);
				ue.setEquipId(uer2.getEquipId());
				//ue.setEquipLevel(uer2.getEquipLevel());
				ue.setEquipped(false);
				ue.setId(UUID.randomUUID());
				ue.setLevelOfUserWhenAcquired(uer2.getLevelOfUserWhenAcquired());
				ue.setTimeAcquired(uer2.getTimeAcquired());
				ue.setUserId(inDb.getId());
				getUserEquipEntityManager().get().put(ue);
				
			}
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		
	



	public UserEquipRepairService getUserEquipRepairService() {
		return userEquipRepairService;
	}

	public void setUserEquipRepairService(
			UserEquipRepairService userEquipRepairService) {
		this.userEquipRepairService = userEquipRepairService;
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

	public EquipmentRetrieveUtils getEquipmentRetrieveUtils() {
		return equipmentRetrieveUtils;
	}

	public void setEquipmentRetrieveUtils(
			EquipmentRetrieveUtils equipmentRetrieveUtils) {
		this.equipmentRetrieveUtils = equipmentRetrieveUtils;
	}

	public MonsterForUserEntityManager getUserEquipEntityManager() {
		return monsterForUserEntityManager;
	}

	public void setUserEquipEntityManager(
			MonsterForUserEntityManager monsterForUserEntityManager) {
		this.monsterForUserEntityManager = monsterForUserEntityManager;
	}
*/

}
