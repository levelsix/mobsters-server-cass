package com.lvl6.mobsters.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureLabRetrieveUtils;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.time.TimeUtils;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class SpeedUpRepairEquipController extends EventController {

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
		return null;//new SpeedUpRepairEquipRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;//MobstersEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
/*		//stuff client sent
		SpeedUpRepairEquipRequestProto reqProto = 
				((SpeedUpRepairEquipRequestEvent) event).getSpeedUpRepairEquipRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		List<UserEquipRepairProto> equipsInQueue = reqProto.getEquipsInQueueList();
		
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();

		//response to send back to client
		Builder responseBuilder = SpeedUpRepairEquipResponseProto.newBuilder();
		responseBuilder.setStatus(SpeedUpRepairEquipStatus.FAIL_OTHER);
		SpeedUpRepairEquipResponseEvent resEvent =
				new SpeedUpRepairEquipResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender,
					inDb, equipsInQueue, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, equipsInQueue, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(SpeedUpRepairEquipStatus.SUCCESS);
			}

			//write to client
			resEvent.setSpeedUpRepairEquipResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in SpeedUpRepairEquipController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(SpeedUpRepairEquipStatus.FAIL_OTHER);
				resEvent.setSpeedUpRepairEquipResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in SpeedUpRepairEquipController processRequestEvent", e2);
			}
		}*/
	}

/*

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, List<UserEquipRepairProto> equipsInQueue, Date clientDate) throws Exception {

		//CHECK IF TIMES ARE IN SYNC
		if (!getTimeUtils().isSynchronizedWithServerTime(clientDate)) {
			log.error("user error: client time diverges from server time. clientTime="
					+ clientDate + ", approximateServerTime=" + new Date());
			responseBuilder.setStatus(SpeedUpRepairEquipStatus.FAIL_OTHER);
			return false;
		}

		int secondsRemaining = getUserEquipRepairService().calculateTotalTimeOfQueuedUserEquips(equipsInQueue, clientDate);
		if(secondsRemaining <= 0) {
			log.error("Queue time is less than or equal to zero");
			responseBuilder.setStatus(SpeedUpRepairEquipStatus.FAIL_QUEUE_TIME_IS_ZERO);
			return false;
		}
		
		//check if user has enough gems
		int gemCostToSpeedUp = getUserService().calculateGemCostForSpeedUp(secondsRemaining);
		if(gemCostToSpeedUp > inDb.getGems()) {
			log.error("user doesn't have enough gems to speed up");
			responseBuilder.setStatus(SpeedUpRepairEquipStatus.FAIL_NOT_ENOUGH_GEMS);
			return false;
		}

		return true;
	}

	
	private boolean writeChangesToDb(User inDb, List<UserEquipRepairProto> equipsInQueue, Date clientDate) {
		try {
			//remove gems from user
			int secondsRemaining = getUserEquipRepairService().calculateTotalTimeOfQueuedUserEquips(equipsInQueue, clientDate);
			int gemCostToSpeedUp = getUserService().calculateGemCostForSpeedUp(secondsRemaining);
			
			inDb.setGems(inDb.getGems() - gemCostToSpeedUp);
			getUserEntityManager().get().put(inDb);


			//delete from user equip repair and add to user equip
			for(UserEquipRepairProto uerp : equipsInQueue) {
				MonsterForUser ue = new MonsterForUser();
				ue.setDungeonRoomOrChestAcquiredFrom(uerp.getDungeonRoomOrChestAcquiredFrom());
				ue.setDurability(100.0);
				
				String equipIdStr = uerp.getEquipId();
				UUID equipId = UUID.fromString(equipIdStr);
				
				ue.setEquipId(equipId);
//				ue.setEquipLevel(uerp.getEquipLevel());
				ue.setEquipped(false);
				ue.setId(UUID.randomUUID());
				ue.setLevelOfUserWhenAcquired(uerp.getLevelOfUserWhenAcquired());
				Date d = new Date(uerp.getTimeAcquired());
				ue.setTimeAcquired(d);
				ue.setUserId(inDb.getId());
				getUserEquipEntityManager().get().put(ue);

				UUID userEquipRepairId = UUID.fromString(uerp.getUserEquipRepairID());
				getUserEquipRepairEntityManager().get().delete(userEquipRepairId);
			}
			
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

