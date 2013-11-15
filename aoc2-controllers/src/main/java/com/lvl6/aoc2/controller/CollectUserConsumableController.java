package com.lvl6.aoc2.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.UserConsumableEntityManager;
import com.lvl6.aoc2.entitymanager.UserConsumableQueueEntityManager;
import com.lvl6.aoc2.entitymanager.UserEntityManager;

import com.lvl6.aoc2.eventprotos.CollectUserConsumableEventProto.CollectUserConsumableRequestProto;
import com.lvl6.aoc2.eventprotos.CollectUserConsumableEventProto.CollectUserConsumableResponseProto;
import com.lvl6.aoc2.eventprotos.CollectUserConsumableEventProto.CollectUserConsumableResponseProto.CollectUserConsumableStatus;
import com.lvl6.aoc2.eventprotos.CollectUserConsumableEventProto.CollectUserConsumableResponseProto.Builder;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.events.request.CollectUserConsumableRequestEvent;
import com.lvl6.aoc2.events.response.CollectUserConsumableResponseEvent;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.aoc2.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.aoc2.noneventprotos.UserConsumableQueue.UserConsumableQueueProto;
import com.lvl6.aoc2.po.Consumable;
import com.lvl6.aoc2.po.User;
import com.lvl6.aoc2.po.UserConsumable;
import com.lvl6.aoc2.po.UserConsumableQueue;

import com.lvl6.aoc2.services.user.UserService;
import com.lvl6.aoc2.services.userconsumablequeue.UserConsumableQueueService;
import com.lvl6.aoc2.widerows.RestrictionOnNumberOfUserStructure;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@Component
public class CollectUserConsumableController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected UserConsumableQueueEntityManager userConsumableQueueEntityManager;
	
	@Autowired
	protected UserConsumableEntityManager userConsumableEntityManager;
	
	@Autowired
	protected UserConsumableQueueService userConsumableQueueService;
	
	@Autowired
	protected UserService userService;

	@Autowired
	protected RestrictionOnNumberOfUserStructure restrictionOnNumberOfUserStructure;

	@Override
	public RequestEvent createRequestEvent() {
		return new CollectUserConsumableRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_BUILD_OR_UPGRADE_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		CollectUserConsumableRequestProto reqProto = 
				((CollectUserConsumableRequestEvent) event).getCollectUserConsumableRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		List<UserConsumableQueueProto> ucqpList = reqProto.getUcqpListList();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();

		//response to send back to client
		Builder responseBuilder = CollectUserConsumableResponseProto.newBuilder();
		responseBuilder.setStatus(CollectUserConsumableStatus.FAIL_OTHER);
		CollectUserConsumableResponseEvent resEvent =
				new CollectUserConsumableResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			List<UserConsumableQueueProto> notFinishedBuildingList = new ArrayList<>();

			
			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					ucqpList, notFinishedBuildingList, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, ucqpList, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(CollectUserConsumableStatus.SUCCESS);
			}

			//write to client
			resEvent.setCollectUserConsumableResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			responseBuilder.addAllUcqpList2(notFinishedBuildingList);

		} catch (Exception e) {
			log.error("exception in CollectUserConsumableController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(CollectUserConsumableStatus.FAIL_OTHER);
				resEvent.setCollectUserConsumableResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in CollectUserConsumableController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, List<UserConsumableQueueProto> ucqpList, List<UserConsumableQueueProto> notFinishedBuildingList, 
			Date clientDate) throws ConnectionException {
		if (null == inDb || null == ucqpList) {
			log.error("unexpected error: no user or list of consumables to build exist");
			return false;
		}
		
		boolean bool = false;
		for(UserConsumableQueueProto ucqp: ucqpList) {
			String consumableId = ucqp.getConsumableId();
			Consumable c = getUserConsumableQueueService().getConsumableCorrespondingToUserConsumableQueue(consumableId);
			long finishTime = ucqp.getExpectedStartMillis() + c.getCreateTimeSeconds()*1000; 
			if(finishTime > clientDate.getTime()) {
				log.error("a consumable's expected finish time has not arrived yet");
				responseBuilder.setStatus(CollectUserConsumableStatus.FAIL_NOT_COMPLETE);
				notFinishedBuildingList.add(ucqp);
				bool = true;
			}
		}
		
		if(bool) {
			return false;
		}
		
		responseBuilder.setStatus(CollectUserConsumableStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, List<UserConsumableQueueProto> ucqpList, Date clientDate) {
		try {
			//delete finished consumables from user consumable and add to user equip
			Map<UserConsumableQueue, Integer> userConsumableQueueMap = getUserConsumableQueueService().convertListToMap(ucqpList);
			
			for(Map.Entry<UserConsumableQueue, Integer> entry : userConsumableQueueMap.entrySet()) {
				UserConsumableQueue ucq = entry.getKey();
				Integer quantityRemoved = entry.getValue();
				if(ucq.getQuantity() == quantityRemoved) {
					getUserConsumableQueueEntityManager().get().delete(ucq.getId());
				}
				else {
					ucq.setQuantity(ucq.getQuantity() - quantityRemoved);
				}
				
				UserConsumable uc = new UserConsumable();
				uc.setId(UUID.randomUUID());
				uc.setConsumableId(ucq.getConsumableId());
				uc.setQuantity(quantityRemoved);
				uc.setUserId(inDb.getId());
				getUserConsumableEntityManager().get().put(uc);
				
			}
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
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

	public UserConsumableEntityManager getUserConsumableEntityManager() {
		return userConsumableEntityManager;
	}

	public void setUserConsumableEntityManager(
			UserConsumableEntityManager userConsumableEntityManager) {
		this.userConsumableEntityManager = userConsumableEntityManager;
	}



	
	
	
	
	
	
	
	
	

}