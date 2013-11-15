package com.lvl6.aoc2.controller;


import java.util.Date;
import java.util.Map;

import java.util.List;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.controller.utils.TimeUtils;
import com.lvl6.aoc2.entitymanager.UserConsumableQueueEntityManager;
import com.lvl6.aoc2.entitymanager.UserEntityManager;
import com.lvl6.aoc2.eventprotos.SpeedUpBuildUserConsumableEventProto.SpeedUpBuildUserConsumableRequestProto;
import com.lvl6.aoc2.eventprotos.SpeedUpBuildUserConsumableEventProto.SpeedUpBuildUserConsumableResponseProto;
import com.lvl6.aoc2.eventprotos.SpeedUpBuildUserConsumableEventProto.SpeedUpBuildUserConsumableResponseProto.Builder;
import com.lvl6.aoc2.eventprotos.SpeedUpBuildUserConsumableEventProto.SpeedUpBuildUserConsumableResponseProto.SpeedUpBuildUserConsumableStatus;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.events.request.SpeedUpBuildUserConsumableRequestEvent;
import com.lvl6.aoc2.events.response.SpeedUpBuildUserConsumableResponseEvent;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.aoc2.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.aoc2.noneventprotos.UserConsumableQueue.UserConsumableQueueProto;
import com.lvl6.aoc2.po.User;
import com.lvl6.aoc2.po.UserConsumableQueue;
import com.lvl6.aoc2.services.user.UserService;
import com.lvl6.aoc2.services.userconsumablequeue.UserConsumableQueueService;


@Component
public class SpeedUpBuildUserConsumableController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	
	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected UserConsumableQueueEntityManager userConsumableQueueEntityManager;
	
	@Autowired
	protected UserConsumableQueueService userConsumableQueueService;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UserService userService;

	@Override
	public RequestEvent createRequestEvent() {
		return new SpeedUpBuildUserConsumableRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		SpeedUpBuildUserConsumableRequestProto reqProto = 
				((SpeedUpBuildUserConsumableRequestEvent) event).getSpeedUpBuildUserConsumableRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		List<UserConsumableQueueProto> consumablesInQueue = reqProto.getQueuedConsumablesList();
		
		
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();

		//response to send back to client
		Builder responseBuilder = SpeedUpBuildUserConsumableResponseProto.newBuilder();
		responseBuilder.setStatus(SpeedUpBuildUserConsumableStatus.FAIL_OTHER);
		SpeedUpBuildUserConsumableResponseEvent resEvent =
				new SpeedUpBuildUserConsumableResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender,
					inDb, consumablesInQueue, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, consumablesInQueue, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(SpeedUpBuildUserConsumableStatus.SUCCESS);
			}

			//write to client
			resEvent.setSpeedUpBuildUserConsumableResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in SpeedUpBuildUserConsumableController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(SpeedUpBuildUserConsumableStatus.FAIL_OTHER);
				resEvent.setSpeedUpBuildUserConsumableResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in SpeedUpBuildUserConsumableController processRequestEvent", e2);
			}
		}
	}



	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, List<UserConsumableQueueProto> consumablesInQueue, Date clientDate) throws Exception {

		//CHECK IF TIMES ARE IN SYNC
		if (!getTimeUtils().isSynchronizedWithServerTime(clientDate)) {
			log.error("user error: client time diverges from server time. clientTime="
					+ clientDate + ", approximateServerTime=" + new Date());
			responseBuilder.setStatus(SpeedUpBuildUserConsumableStatus.FAIL_OTHER);
			return false;
		}
		
		Map<UserConsumableQueue, Integer> consumablesInQueueMap = getUserConsumableQueueService().convertListToMap(consumablesInQueue);
		
		int secondsRemaining = getUserConsumableQueueService().calculateTotalTimeOfQueuedUserConsumable(consumablesInQueueMap, clientDate);
		if(secondsRemaining <= 0) {
			log.error("Queue time is less than or equal to zero");
			responseBuilder.setStatus(SpeedUpBuildUserConsumableStatus.FAIL_QUEUE_TIME_IS_ZERO);
			return false;
		}
		
		//check if user has enough gems
		int gemCostToSpeedUp = getUserService().calculateGemCostForSpeedUp(secondsRemaining);
		if(gemCostToSpeedUp > inDb.getGems()) {
			log.error("user doesn't have enough gems to speed up");
			responseBuilder.setStatus(SpeedUpBuildUserConsumableStatus.FAIL_NOT_ENOUGH_GEMS);
			return false;
		}

		return true;
	}

	
	private boolean writeChangesToDb(User inDb, List<UserConsumableQueueProto> consumablesInQueue, Date clientDate) {
		try {
			//remove gems from user
			Map<UserConsumableQueue, Integer> consumablesInQueueMap = getUserConsumableQueueService().convertListToMap(consumablesInQueue);

			int secondsRemaining = getUserConsumableQueueService().calculateTotalTimeOfQueuedUserConsumable(consumablesInQueueMap, clientDate);
			int gemCostToSpeedUp = getUserService().calculateGemCostForSpeedUp(secondsRemaining);
			
			inDb.setGems(inDb.getGems() - gemCostToSpeedUp);
			getUserEntityManager().get().put(inDb);


			//change column is finished building to true
			for(UserConsumableQueue ucq : consumablesInQueueMap.keySet()) {
				ucq.setFinishedBuilding(true);
				getUserConsumableQueueEntityManager().get().put(ucq);
			}
			
			return true;

		} catch (Exception e2) {
			log.error("unexpected error: problem with saving to db.", e2);
		}
		return false;
	}




	public UserConsumableQueueEntityManager getUserConsumableQueueEntityManager() {
		return userConsumableQueueEntityManager;
	}

	public void setUserConsumableQueueEntityManager(
			UserConsumableQueueEntityManager userConsumableQueueEntityManager) {
		this.userConsumableQueueEntityManager = userConsumableQueueEntityManager;
	}

	public UserConsumableQueueService getUserConsumableQueueService() {
		return userConsumableQueueService;
	}

	public void setUserConsumableQueueService(
			UserConsumableQueueService userConsumableQueueService) {
		this.userConsumableQueueService = userConsumableQueueService;
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

	
	
	

}

