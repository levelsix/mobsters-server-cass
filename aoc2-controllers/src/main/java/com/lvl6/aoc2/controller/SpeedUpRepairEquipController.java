package com.lvl6.aoc2.controller;


import java.util.Date;

import java.util.List;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.controller.utils.TimeUtils;
import com.lvl6.aoc2.entitymanager.UserEntityManager;
import com.lvl6.aoc2.entitymanager.UserEquipEntityManager;
import com.lvl6.aoc2.entitymanager.UserEquipRepairEntityManager;
import com.lvl6.aoc2.entitymanager.staticdata.EquipmentRetrieveUtils;
import com.lvl6.aoc2.eventprotos.SpeedUpRepairEquipEventProto.SpeedUpRepairEquipRequestProto;
import com.lvl6.aoc2.eventprotos.SpeedUpRepairEquipEventProto.SpeedUpRepairEquipResponseProto;
import com.lvl6.aoc2.eventprotos.SpeedUpRepairEquipEventProto.SpeedUpRepairEquipResponseProto.Builder;
import com.lvl6.aoc2.eventprotos.SpeedUpRepairEquipEventProto.SpeedUpRepairEquipResponseProto.SpeedUpRepairEquipStatus;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.events.request.SpeedUpRepairEquipRequestEvent;
import com.lvl6.aoc2.events.response.SpeedUpRepairEquipResponseEvent;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.aoc2.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.aoc2.noneventprotos.UserEquipRepair.UserEquipRepairProto;
import com.lvl6.aoc2.po.User;
import com.lvl6.aoc2.po.UserEquip;
import com.lvl6.aoc2.services.user.UserService;
import com.lvl6.aoc2.services.userequip.UserEquipService;
import com.lvl6.aoc2.services.userequiprepair.UserEquipRepairService;


@Component
public class SpeedUpRepairEquipController extends EventController {

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
		return new SpeedUpRepairEquipRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
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
		}
	}



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
				UserEquip ue = new UserEquip();
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

