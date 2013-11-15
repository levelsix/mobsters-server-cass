package com.lvl6.mobsters.controller;

import java.util.Date;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




import com.lvl6.mobsters.controller.utils.TimeUtils;
import com.lvl6.mobsters.entitymanager.UserConsumableQueueEntityManager;
import com.lvl6.mobsters.entitymanager.UserEntityManager;
import com.lvl6.mobsters.eventprotos.BuildConsumableEventProto.BuildConsumableRequestProto;
import com.lvl6.mobsters.eventprotos.BuildConsumableEventProto.BuildConsumableResponseProto;
import com.lvl6.mobsters.eventprotos.BuildConsumableEventProto.BuildConsumableResponseProto.BuildConsumableStatus;
import com.lvl6.mobsters.eventprotos.BuildConsumableEventProto.BuildConsumableResponseProto.Builder;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.BuildConsumableRequestEvent;
import com.lvl6.mobsters.events.response.BuildConsumableResponseEvent;
import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.noneventprotos.ResourceEnum.ResourceType;
import com.lvl6.mobsters.noneventprotos.UserConsumableQueue.UserConsumableQueueProto;
import com.lvl6.mobsters.po.User;
import com.lvl6.mobsters.po.UserConsumableQueue;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.userconsumablequeue.UserConsumableQueueService;



@Component
public class BuildConsumableController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	
	@Autowired
	protected UserConsumableQueueService userConsumableQueueService;
	
	@Autowired
	protected UserConsumableQueueEntityManager userConsumableQueueEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UserService userService;


	@Override
	public RequestEvent createRequestEvent() {
		return new BuildConsumableRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		BuildConsumableRequestProto reqProto = 
				((BuildConsumableRequestEvent) event).getBuildConsumableRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		List<UserConsumableQueueProto> ucqpDelete = reqProto.getUcqDeleteList();
		List<UserConsumableQueueProto> ucqpUpdate = reqProto.getUcqUpdateList();
		List<UserConsumableQueueProto> ucqpNew = reqProto.getUcqNewList();
		boolean usingGems = reqProto.getUsingGems();
				
		Date clientDate = new Date(reqProto.getClientTimeMillis());

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = BuildConsumableResponseProto.newBuilder();
		responseBuilder.setStatus(BuildConsumableStatus.FAIL_OTHER);
		BuildConsumableResponseEvent resEvent =
				new BuildConsumableResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			
			Map<UserConsumableQueue, Integer> alreadyQueuedConsumables = getUserConsumableQueueService().getConsumablesBeingMade(inDb.getId());

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb, 
					ucqpDelete, ucqpUpdate, ucqpNew, alreadyQueuedConsumables, usingGems, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, ucqpDelete, ucqpUpdate, 
						ucqpNew, usingGems);
			}

			if (successful) {
				responseBuilder.setStatus(BuildConsumableStatus.SUCCESS);
			}

			//write to client
			resEvent.setBuildConsumableResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in BuildConsumableController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(BuildConsumableStatus.FAIL_OTHER);
				resEvent.setBuildConsumableResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in BuildConsumableController processRequestEvent", e2);
			}
		}
	}



	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, List<UserConsumableQueueProto> ucqDelete,
			List<UserConsumableQueueProto> ucqUpdate, List<UserConsumableQueueProto> ucqNew,
			Map<UserConsumableQueue, Integer> alreadyQueuedConsumables,
			boolean usingGems, Date clientDate) throws Exception {
	
		if(inDb == null) {
			log.error("user doesn't exist");
			responseBuilder.setStatus(BuildConsumableStatus.FAIL_OTHER);
			return false;
		}
		
		if (ucqDelete.isEmpty() && ucqUpdate.isEmpty() && ucqNew.isEmpty()) {
			log.error("everything sent is empty");
			responseBuilder.setStatus(BuildConsumableStatus.FAIL_OTHER);
			return false;
		}

		Set<UserConsumableQueue> alreadyQueuedConsumablesKeys = alreadyQueuedConsumables.keySet();
		Set<UUID> alreadyQueuedConsumablesIds = new HashSet<UUID>();
		Set<UUID> deletedQueuedConsumablesIds = new HashSet<UUID>();
		for(UserConsumableQueue ucq : alreadyQueuedConsumablesKeys) {
			alreadyQueuedConsumablesIds.add(ucq.getConsumableId());
		}

		for(UserConsumableQueueProto ucqp : ucqDelete) {
			UUID consumableId = UUID.fromString(ucqp.getConsumableId());
			deletedQueuedConsumablesIds.add(consumableId);
		}
		
		//check if what client deletes/updates are in the db.
		if (!alreadyQueuedConsumablesIds.containsAll(deletedQueuedConsumablesIds)) {
			log.error("unexpected error: either server did not modify" +
					"equips-to-be-repaired, or client is sending incorrectly. " +
					"uerDeleteIds=" + deletedQueuedConsumablesIds);
			return false;
		}
		
		Set<String> updatedQueuedConsumablesNames = new HashSet<>();
		for(UserConsumableQueueProto ucqp : ucqUpdate) {
			updatedQueuedConsumablesNames.add(ucqp.getConsumableId());
		}
		
		if (!alreadyQueuedConsumablesIds.containsAll(updatedQueuedConsumablesNames)) {
			log.error("unexpected error: either server did not modify" +
					"equips-to-be-repaired, or client is sending incorrectly. " +
					"uerUpdateIds=" + updatedQueuedConsumablesNames);
			return false;
		}

		//CHECK IF TIMES ARE IN SYNC
		if (!getTimeUtils().isSynchronizedWithServerTime(clientDate)) {
			log.error("user error: client time diverges from server time. clientTime="
					+ clientDate + ", approximateServerTime=" + new Date());
			responseBuilder.setStatus(BuildConsumableStatus.FAIL_UNSYNCHRONIZED_TIMES);
			return false;
		}
		
		//check if number of deleted pots and update is same as number in table
		Map<UserConsumableQueue, Integer> userQueuedConsumables = getUserConsumableQueueService().getConsumablesBeingMade(inDb.getId());
		int count =0;
		for(Integer value : userQueuedConsumables.values()) {
			count += value;
		}
		
		if(ucqDelete.size() + ucqUpdate.size() != count) {
			log.error("number of update and delete pots doesn't match what number user has in queue");
			responseBuilder.setStatus(BuildConsumableStatus.FAIL_OTHER);
			return false;
		}
		
		
		Map<UserConsumableQueue, Integer> ucqDeleteMap = getUserConsumableQueueService().convertListToMap(ucqDelete);
		int tonicRefund = getUserConsumableQueueService().calculateBuildCost(ucqDeleteMap);
		
		Map<UserConsumableQueue, Integer> ucqNewMap = getUserConsumableQueueService().convertListToMap(ucqNew);
		int tonicCost = getUserConsumableQueueService().calculateBuildCost(ucqNewMap);

		if(!usingGems){
			if(inDb.getTonic() + tonicRefund < tonicCost) {
				log.error("user doesn't have enough tonic");
				responseBuilder.setStatus(BuildConsumableStatus.FAIL_NOT_ENOUGH_TONIC);
				return false;
			}
		}
		else {
			int missingTonic = tonicCost - inDb.getTonic() - tonicRefund;
			if(inDb.getGems() < getUserService().calculateGemCostForMissingResources(inDb, missingTonic, ResourceType.TONIC_VALUE)) {
				log.error("user doesn't have enough gem to buy remaining tonic required");
				responseBuilder.setStatus(BuildConsumableStatus.FAIL_OTHER);
				return false;
			}
		}
		
		return true;
	}


	private boolean writeChangesToDb(User inDb,
			List<UserConsumableQueueProto> ucqpDelete, List<UserConsumableQueueProto> ucqpUpdate,
			List<UserConsumableQueueProto> ucqpNew, boolean usingGems) {
		try {
			//refund user's tonic
			if(!ucqpDelete.isEmpty()) {
				Map<UserConsumableQueue, Integer> ucqDeleteMap = getUserConsumableQueueService().convertListToMap(ucqpDelete);
				int tonicRefund = getUserConsumableQueueService().calculateBuildCost(ucqDeleteMap);

				inDb.setTonic(inDb.getTonic() + tonicRefund);
				getUserEntityManager().get().put(inDb);
			}
			
			Map<UserConsumableQueue, Integer> ucqDeleteMap = getUserConsumableQueueService().convertListToMap(ucqpDelete);
			int tonicRefund = getUserConsumableQueueService().calculateBuildCost(ucqDeleteMap);
			
			Map<UserConsumableQueue, Integer> ucqNewMap = getUserConsumableQueueService().convertListToMap(ucqpNew);
			int tonicCost = getUserConsumableQueueService().calculateBuildCost(ucqNewMap);
				
			if(usingGems) {	
				int missingTonic = tonicCost - inDb.getTonic() - tonicRefund;
				int gemCost = getUserService().calculateGemCostForMissingResources(inDb, missingTonic, ResourceType.TONIC_VALUE);
				inDb.setGems(inDb.getGems()-gemCost);
				inDb.setTonic(0);
			}
			else {
				inDb.setTonic(inDb.getTonic()-tonicCost+tonicRefund);
			}
			getUserEntityManager().get().put(inDb);
			
			Map<UserConsumableQueue, Integer> currentQueue = getUserConsumableQueueService().getConsumablesBeingMade(inDb.getId());
			
			//delete these pots from user consumable queue
			getUserConsumableQueueService().deleteFromQueue(ucqDeleteMap, currentQueue);
			
			//update leftover queue 
			Map<UserConsumableQueue, Integer> ucqUpdateMap = getUserConsumableQueueService().convertListToMap(ucqpUpdate);

			for(UserConsumableQueue ucq : ucqUpdateMap.keySet()) {
				getUserConsumableQueueEntityManager().get().put(ucq);
			}
			
			//add new consumables 
			Map<UserConsumableQueue, Integer> ucqNewMap2 = getUserConsumableQueueService().convertListToMap(ucqpUpdate);

			for(UserConsumableQueue ucq : ucqNewMap2.keySet()) {
				getUserConsumableQueueEntityManager().get().put(ucq);
			}
			
			return true;
			

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
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

	public UserConsumableQueueEntityManager getUserConsumableQueueEntityManager() {
		return userConsumableQueueEntityManager;
	}

	public void setUserConsumableQueueEntityManager(
			UserConsumableQueueEntityManager userConsumableQueueEntityManager) {
		this.userConsumableQueueEntityManager = userConsumableQueueEntityManager;
	}

	

}

