package com.lvl6.mobsters.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.ChestEntityManager;
import com.lvl6.mobsters.entitymanager.EquipmentEntityManager;
import com.lvl6.mobsters.entitymanager.UserChestEntityManager;
import com.lvl6.mobsters.entitymanager.UserEntityManager;
import com.lvl6.mobsters.entitymanager.UserEquipEntityManager;
import com.lvl6.mobsters.entitymanager.UserItemEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.ChestRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.ItemRetrieveUtils;
import com.lvl6.mobsters.eventprotos.OpenChestEventProto.OpenChestRequestProto;
import com.lvl6.mobsters.eventprotos.OpenChestEventProto.OpenChestResponseProto;
import com.lvl6.mobsters.eventprotos.OpenChestEventProto.OpenChestResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.OpenChestEventProto.OpenChestResponseProto.OpenChestStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.OpenChestRequestEvent;
import com.lvl6.mobsters.events.response.OpenChestResponseEvent;
import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.Chest;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.Item;
import com.lvl6.mobsters.po.User;
import com.lvl6.mobsters.po.UserChest;
import com.lvl6.mobsters.po.UserEquip;
import com.lvl6.mobsters.po.UserItem;
import com.lvl6.mobsters.services.equipment.EquipmentService;
import com.lvl6.mobsters.services.userchest.UserChestService;
import com.lvl6.mobsters.services.userequip.UserEquipService;
import com.lvl6.mobsters.services.useritem.UserItemService;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@Component
public class OpenChestController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	

	@Autowired
	protected UserEquipEntityManager userEquipEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected EquipmentEntityManager equipmentEntityManager;
	
	@Autowired
	protected ChestEntityManager chestEntityManager;
	
	@Autowired
	protected UserEquipService userEquipService; 
	
	@Autowired
	protected ChestRetrieveUtils chestRetrieveUtils; 
	
	@Autowired
	protected UserChestService userChestService; 
	
	@Autowired
	protected ItemRetrieveUtils itemRetrieveUtils; 
	
	@Autowired
	protected EquipmentService equipmentServices;
	
	@Autowired
	protected UserItemService userItemService;

	@Autowired
	protected UserChestEntityManager userChestEntityManager;
	
	@Autowired
	protected UserItemEntityManager userItemEntityManager;
	

	
	
	@Override
	public RequestEvent createRequestEvent() {
		return new OpenChestRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_RETURN_HOME_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		OpenChestRequestProto reqProto = 
				((OpenChestRequestEvent) event).getOpenChestRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		String chestIdString = reqProto.getChestId();
		UUID chestId = UUID.fromString(chestIdString);
		boolean usedKey = reqProto.getUsedKey();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();

		//response to send back to client
		Builder responseBuilder = OpenChestResponseProto.newBuilder();
		responseBuilder.setStatus(OpenChestStatus.FAIL_OTHER);
		OpenChestResponseEvent resEvent =
				new OpenChestResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			List<UserItem> uiList = getUserItemService().getAllUserItemsForUser(userId);
			List<UserChest> ucList = getUserChestService().getAllUserChestsForUser(userId);

			
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb, chestId, usedKey, clientDate);

			boolean successful = false;
			if(validRequest) {
				successful = writeChangesToDb(inDb, chestId, usedKey, uiList, ucList, clientDate);
			}
			
			if (successful) {
				responseBuilder.setStatus(OpenChestStatus.SUCCESS);
			}

			//write to client
			resEvent.setOpenChestResponseProto(responseBuilder.build());
			String equipName = chooseEquipFromChest(chestId).getName(); 
			responseBuilder.setEquipName(equipName);
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in OpenChestController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(OpenChestStatus.FAIL_OTHER);
				resEvent.setOpenChestResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in OpenChestController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, UUID chestId, boolean usedKey, Date clientDate) throws ConnectionException {
		
		if (null == inDb || null == chestId) {
			log.error("unexpected error: no user or chest exists");
			responseBuilder.setStatus(OpenChestStatus.FAIL_OTHER);
			return false;
		}

		Chest chest = getChestRetrieveUtils().getChestForId(chestId);
		if(usedKey){
			Item key = getItemRetrieveUtils().findMatchingKeyToChest(chest.getChestType());
			UUID keyId = key.getId();
			int numOfMatchingUserKeys = getUserItemService().getNumberOfSpecificUserKeys(keyId, inDb.getId());
			
			if(numOfMatchingUserKeys < chest.getKeysRequiredToOpen()) {
				log.error("user doesn't have key to open chest");
				responseBuilder.setStatus(OpenChestStatus.FAIL_NOT_ENOUGH_KEYS);
				return false;
			}
		} else {
			if(inDb.getGems() < chest.getGemsRequiredToOpen()) {
				log.error("user doesn't have enough gems to open chest");
				responseBuilder.setStatus(OpenChestStatus.FAIL_NOT_ENOUGH_GEMS);
				return false;
			}
		}
		
		responseBuilder.setStatus(OpenChestStatus.SUCCESS);
		return true;
	}

	
	private boolean writeChangesToDb(User inDb, UUID chestId, boolean usedKey, List<UserItem> uiList,  List<UserChest> ucList, Date clientDate) {
		try {
			Chest chest = getChestRetrieveUtils().getChestForId(chestId);
				
			//update useritem or user 
			if(usedKey) {
				Item key = getItemRetrieveUtils().findMatchingKeyToChest(chest.getChestType());
				int keysCount = chest.getKeysRequiredToOpen();
				int count = 0;
				for(UserItem ui : uiList) {
					if((ui.getItemId() == key.getId()) && (count < keysCount)) {
						getUserItemEntityManager().get().delete(ui.getId());
						count++;
					}
				}
			}
			else {
				inDb.setGems(inDb.getGems()-chest.getGemsRequiredToOpen());
				getUserEntityManager().get().put(inDb);
			}
				
			//update userequip
			UUID newId = UUID.randomUUID();
			Equipment equip = chooseEquipFromChest(chestId);
			UserEquip ue = new UserEquip();
			ue.setDungeonRoomOrChestAcquiredFrom(chest.getChestName());
			ue.setDurability(100.0);
			ue.setEquipId(equip.getId());
			//ue.setEquipLevel(1);
			ue.setEquipped(false);
			ue.setId(newId);
			ue.setLevelOfUserWhenAcquired(inDb.getLvl());
			ue.setTimeAcquired(clientDate);
			ue.setUserId(inDb.getId());
			
			getUserEquipEntityManager().get().put(ue);
				
			//remove chest from userchest
			
			for(UserChest uc : ucList) {
				if(uc.getChestId() == chest.getId()) {
					getUserItemEntityManager().get().delete(uc.getId());
					break;
				}
			}
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	public Equipment chooseEquipFromChest(UUID chestId) {
		String cqlquery = "select * from chest where chestId =" + chestId + ";"; 
		List <Chest> cList = getChestEntityManager().get().find(cqlquery);
		double randomValue = Math.random()*1;
		double dropRate = 0.0;
		Equipment e = new Equipment();
		for(Chest c : cList) {
			dropRate = dropRate + c.getChestDropRate();
			if(dropRate > randomValue) {
				e = getEquipmentEntityManager().get().get(c.getEquipId());
				break;
			}
		}
		return e;
	}
	
	
	
	
	public UserEquipEntityManager getUserEquipEntityManager() {
		return userEquipEntityManager;
	}

	public void setUserEquipEntityManager(
			UserEquipEntityManager userEquipEntityManager) {
		this.userEquipEntityManager = userEquipEntityManager;
	}

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}

	public UserEquipService getUserEquipService() {
		return userEquipService;
	}

	public void setUserEquipService(UserEquipService userEquipService) {
		this.userEquipService = userEquipService;
	}

	public EquipmentService getEquipmentServices() {
		return equipmentServices;
	}

	public void setEquipmentServices(EquipmentService equipmentServices) {
		this.equipmentServices = equipmentServices;
	}

	public UserItemService getUserItemService() {
		return userItemService;
	}

	public void setUserItemService(UserItemService userItemService) {
		this.userItemService = userItemService;
	}

	public UserChestEntityManager getUserChestEntityManager() {
		return userChestEntityManager;
	}

	public void setUserChestEntityManager(
			UserChestEntityManager userChestEntityManager) {
		this.userChestEntityManager = userChestEntityManager;
	}

	public UserItemEntityManager getUserItemEntityManager() {
		return userItemEntityManager;
	}

	public void setUserItemEntityManager(UserItemEntityManager userItemEntityManager) {
		this.userItemEntityManager = userItemEntityManager;
	}

	public ItemRetrieveUtils getItemRetrieveUtils() {
		return itemRetrieveUtils;
	}

	public void setItemRetrieveUtils(ItemRetrieveUtils itemRetrieveUtils) {
		this.itemRetrieveUtils = itemRetrieveUtils;
	}

	public ChestRetrieveUtils getChestRetrieveUtils() {
		return chestRetrieveUtils;
	}

	public void setChestRetrieveUtils(ChestRetrieveUtils chestRetrieveUtils) {
		this.chestRetrieveUtils = chestRetrieveUtils;
	}

	public ChestEntityManager getChestEntityManager() {
		return chestEntityManager;
	}

	public void setChestEntityManager(ChestEntityManager chestEntityManager) {
		this.chestEntityManager = chestEntityManager;
	}

	public EquipmentEntityManager getEquipmentEntityManager() {
		return equipmentEntityManager;
	}

	public void setEquipmentEntityManager(
			EquipmentEntityManager equipmentEntityManager) {
		this.equipmentEntityManager = equipmentEntityManager;
	}

	public UserChestService getUserChestService() {
		return userChestService;
	}

	public void setUserChestService(UserChestService userChestService) {
		this.userChestService = userChestService;
	}


	
	
	

	//used at start of dungeon instead
//	public int emptyStorageSlots(List<UserEquip> ueList, List<UserStructure> usList, Structure storageStructure) {
//		for(UserStructure us : usList) {
//			Structure s = getStructureRetrieveUtils().getStructureForId(us.getStructureId());
//			if(s.getFunctionalityType() == FunctionalityType.STORAGE_VALUE)
//				storageStructure = s;
//		}
//		int storageSpace = storageStructure.getFunctionalityCapacity();
//		int emptyStorageSlots = storageSpace - ueList.size();
//		return emptyStorageSlots;
//			
//	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	

}