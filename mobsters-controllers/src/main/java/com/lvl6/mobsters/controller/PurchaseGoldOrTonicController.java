package com.lvl6.mobsters.controller;

import java.util.Date;

import java.util.List;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




import com.lvl6.mobsters.entitymanager.UserConsumableQueueEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.eventprotos.PurchaseGoldOrTonicEventProto.PurchaseGoldOrTonicRequestProto;
import com.lvl6.mobsters.eventprotos.PurchaseGoldOrTonicEventProto.PurchaseGoldOrTonicResponseProto;
import com.lvl6.mobsters.eventprotos.PurchaseGoldOrTonicEventProto.PurchaseType;
import com.lvl6.mobsters.eventprotos.PurchaseGoldOrTonicEventProto.PurchaseGoldOrTonicResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.PurchaseGoldOrTonicEventProto.PurchaseGoldOrTonicResponseProto.PurchaseGoldOrTonicStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.PurchaseGoldOrTonicRequestEvent;
import com.lvl6.mobsters.events.response.PurchaseGoldOrTonicResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.noneventprotos.FunctionalityTypeEnum.FunctionalityType;
import com.lvl6.mobsters.noneventprotos.ResourceEnum.ResourceType;
import com.lvl6.mobsters.po.Structure;
import com.lvl6.mobsters.po.UserStructure;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.time.TimeUtils;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.userconsumablequeue.UserConsumableQueueService;
import com.lvl6.mobsters.services.userstructure.UserStructureService;



@Component
public class PurchaseGoldOrTonicController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	
	@Autowired
	protected UserConsumableQueueService userConsumableQueueService;
	
	@Autowired
	protected UserConsumableQueueEntityManager userConsumableQueueEntityManager;
	
	@Autowired
	protected UserStructureService userStructureService;

	@Autowired
	protected UserEntityManager userEntityManager;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UserService userService;


	@Override
	public RequestEvent createRequestEvent() {
		return new PurchaseGoldOrTonicRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		PurchaseGoldOrTonicRequestProto reqProto = 
				((PurchaseGoldOrTonicRequestEvent) event).getPurchaseGoldOrTonicRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		int purchaseType = reqProto.getPurchaseType();
		boolean isGold = reqProto.getIsGold();
				
		Date clientDate = new Date();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = PurchaseGoldOrTonicResponseProto.newBuilder();
		responseBuilder.setStatus(PurchaseGoldOrTonicStatus.FAIL_OTHER);
		PurchaseGoldOrTonicResponseEvent resEvent =
				new PurchaseGoldOrTonicResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			List<UserStructure> usList = getUserStructureService().getAllUserStructuresForUser(inDb.getId());
			int maxGoldStorage = 0;
			int maxTonicStorage = 0;
			for(UserStructure us : usList) {
				Structure s = getUserStructureService().getStructureCorrespondingToUserStructure(us);
				if(s.getFunctionalityType() == FunctionalityType.RESOURCE_STORAGE_VALUE) { 
						if(s.getFunctionalityType() == ResourceType.GOLD_VALUE) {
							maxGoldStorage += s.getFunctionalityCapacity();
						}
						else {
							maxTonicStorage += s.getFunctionalityCapacity();
						}
					
				}
			}
			
			
			
			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb, 
					maxGoldStorage, maxTonicStorage, purchaseType, isGold, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb,maxGoldStorage, maxTonicStorage, 
						purchaseType,isGold, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(PurchaseGoldOrTonicStatus.SUCCESS);
			}

			//write to client
			resEvent.setPurchaseGoldOrTonicResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in PurchaseGoldOrTonicController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(PurchaseGoldOrTonicStatus.FAIL_OTHER);
				resEvent.setPurchaseGoldOrTonicResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in PurchaseGoldOrTonicController processRequestEvent", e2);
			}
		}
	}



	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, int maxGoldStorage, int maxTonicStorage, int purchaseType, boolean isGold, Date clientDate) throws Exception {
	
		if (maxGoldStorage == 0 || maxTonicStorage ==0) {
			log.error("user's resource storage is zero");
			responseBuilder.setStatus(PurchaseGoldOrTonicStatus.FAIL_OTHER);
			return false;
		}

		double percentage = 0;
		if(purchaseType == PurchaseType.TEN_PERCENT_VALUE) {
			percentage = 0.1;
		}
		else if(purchaseType == PurchaseType.FIFTY_PERCENT_VALUE) {
			percentage = 0.5;
		}
		else if(purchaseType == PurchaseType.HUNDRED_PERCENT_VALUE) {
			percentage = 1;
		}
		
		int gemCost;
		if(isGold) {
			gemCost = getUserService().calculateGemCostForPercentageOfResource(inDb, percentage, maxGoldStorage);
		}
		else gemCost = getUserService().calculateGemCostForPercentageOfResource(inDb, percentage, maxTonicStorage);
		
		if(inDb.getGems() < gemCost) {
			log.error("user doesn't have enough gems to buy resources");
			responseBuilder.setStatus(PurchaseGoldOrTonicStatus.FAIL_NOT_ENOUGH_GEMS);
			return false;
		}
		
		if(isGold && inDb.getGold() == maxGoldStorage) {
			log.error("user already has max gold");
			responseBuilder.setStatus(PurchaseGoldOrTonicStatus.FAIL_ALREADY_MAX_RESOURCE);
			return false;
		}
		else if(inDb.getTonic() == maxTonicStorage) {
			log.error("user already has max gold");
			responseBuilder.setStatus(PurchaseGoldOrTonicStatus.FAIL_ALREADY_MAX_RESOURCE);
			return false;
		}
		
		return true;
	}


	private boolean writeChangesToDb(User inDb, int maxGoldStorage, int maxTonicStorage, 
			int purchaseType, boolean isGold, Date clientDate) {
		try {
			double percentage = 0;
			if(purchaseType == PurchaseType.TEN_PERCENT_VALUE) {
				percentage = 0.1;
			}
			else if(purchaseType == PurchaseType.FIFTY_PERCENT_VALUE) {
				percentage = 0.5;
			}
			else if(purchaseType == PurchaseType.HUNDRED_PERCENT_VALUE) {
				percentage = 1;
			}
			
			int gemCost;
			if(isGold) {
				gemCost = getUserService().calculateGemCostForPercentageOfResource(inDb, percentage, maxGoldStorage);
				inDb.setGems(inDb.getGems() - gemCost);
				inDb.setGold(inDb.getGold() + (int)(percentage*maxGoldStorage));
			}
			else {
				gemCost = getUserService().calculateGemCostForPercentageOfResource(inDb, percentage, maxTonicStorage);
				inDb.setGems(inDb.getGems() - gemCost);
				inDb.setTonic(inDb.getTonic() + (int)(percentage*maxTonicStorage));
			}
			
			getUserEntityManager().get().put(inDb);
			
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

	public UserStructureService getUserStructureService() {
		return userStructureService;
	}

	public void setUserStructureService(UserStructureService userStructureService) {
		this.userStructureService = userStructureService;
	}


}

