package com.lvl6.aoc2.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.UserChestEntityManager;
import com.lvl6.aoc2.entitymanager.UserDungeonStatusEntityManager;
import com.lvl6.aoc2.entitymanager.UserDungeonStatusHistoryEntityManager;
import com.lvl6.aoc2.entitymanager.UserEntityManager;
import com.lvl6.aoc2.entitymanager.UserEquipEntityManager;
import com.lvl6.aoc2.entitymanager.UserItemEntityManager;
import com.lvl6.aoc2.entitymanager.staticdata.StructureRetrieveUtils;
import com.lvl6.aoc2.eventprotos.CompleteDungeonEventProto.CompleteDungeonRequestProto;
import com.lvl6.aoc2.eventprotos.CompleteDungeonEventProto.CompleteDungeonResponseProto;
import com.lvl6.aoc2.eventprotos.CompleteDungeonEventProto.CompleteDungeonResponseProto.CompleteDungeonStatus;
import com.lvl6.aoc2.eventprotos.CompleteDungeonEventProto.CompleteDungeonResponseProto.Builder;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.events.request.CompleteDungeonRequestEvent;
import com.lvl6.aoc2.events.response.CompleteDungeonResponseEvent;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.aoc2.noneventprotos.Chest.ChestProto;
import com.lvl6.aoc2.noneventprotos.Equipment.EquipmentProto;
import com.lvl6.aoc2.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.aoc2.noneventprotos.Item.ItemProto;
import com.lvl6.aoc2.po.User;
import com.lvl6.aoc2.po.UserChest;
import com.lvl6.aoc2.po.UserDungeonStatus;
import com.lvl6.aoc2.po.UserEquip;
import com.lvl6.aoc2.po.UserItem;
import com.lvl6.aoc2.services.equipment.EquipmentService;
import com.lvl6.aoc2.services.userequip.UserEquipService;
import com.lvl6.aoc2.services.userstructure.UserStructureService;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@Component
public class CompleteDungeonController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	

	@Autowired
	protected UserEquipEntityManager userEquipEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected UserDungeonStatusEntityManager userDungeonStatusEntityManager;
	
	@Autowired
	protected UserDungeonStatusHistoryEntityManager userDungeonStatusHistoryEntityManager;

	@Autowired
	protected UserStructureService userStructureService;
		
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	@Autowired
	protected EquipmentService equipmentServices;
	
	@Autowired
	protected UserEquipService userEquipService;

	@Autowired
	protected UserChestEntityManager userChestEntityManager;
	
	@Autowired
	protected UserItemEntityManager userItemEntityManager;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new CompleteDungeonRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_RETURN_HOME_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		CompleteDungeonRequestProto reqProto = 
				((CompleteDungeonRequestEvent) event).getCompleteDungeonRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		List<ChestProto> chestsRewarded = reqProto.getChestGainedList();
		List<EquipmentProto> equipmentsRewarded = reqProto.getEquipsGainedList();
		List<ItemProto> itemsRewarded = reqProto.getItemsGainedList();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();
		String dungeonRoomIdStr = reqProto.getDungeonRoomName();
		UUID dungeonRoomId = UUID.fromString(dungeonRoomIdStr);

		//response to send back to client
		Builder responseBuilder = CompleteDungeonResponseProto.newBuilder();
		responseBuilder.setStatus(CompleteDungeonStatus.FAIL_OTHER);
		CompleteDungeonResponseEvent resEvent =
				new CompleteDungeonResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			List<UserEquip> ueList = getUserEquipService().getAllUserEquipsForUser(userId);

			List<UserEquip> equippedEquips = new ArrayList<UserEquip>();
			getUserEquipService().getEquippedUserEquips(ueList, equippedEquips);

			boolean validRequest = isValidRequest(responseBuilder, sender, inDb, equipmentsRewarded, clientDate);

			boolean successful = false;
			if(validRequest) {
				successful = writeChangesToDb(inDb, userId, chestsRewarded,
						equipmentsRewarded, itemsRewarded, equippedEquips,
						dungeonRoomId, clientDate);
			}
			
			if (successful) {
				responseBuilder.setStatus(CompleteDungeonStatus.SUCCESS);
			}

			//write to client
			resEvent.setCompleteDungeonResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in CompleteDungeonController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(CompleteDungeonStatus.FAIL_OTHER);
				resEvent.setCompleteDungeonResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in CompleteDungeonController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, List<EquipmentProto> equipmentsRewarded, Date clientDate) throws ConnectionException {
		
		if (null == inDb || null == equipmentsRewarded) {
			log.error("unexpected error: no user exists or no equips rewarded which" +
					" is dumb it better give at least one drop per stage");
			responseBuilder.setStatus(CompleteDungeonStatus.FAIL_CHEST_OR_EQUIPS_DONT_EXIST);
			return false;
		}

		if(inDb.getHp() == 0) {
			log.error("user is dead, how did he finish dungeon?");
			responseBuilder.setStatus(CompleteDungeonStatus.FAIL_OTHER);
			return false;
		}

		responseBuilder.setStatus(CompleteDungeonStatus.SUCCESS);
		return true;
	}

	
	private boolean writeChangesToDb(User inDb, UUID userId,
			List<ChestProto> chestsRewarded, List<EquipmentProto> equipmentsRewarded,
			List<ItemProto> itemsRewarded, List<UserEquip> equippedEquips,
			UUID dungeonRoomId, Date clientDate) {

			try {
			//update user
			String cqlquery = "select * from user_dungeon_status where user_id= \"" + userId + "\";"; 
			List<UserDungeonStatus> uds = getUserDungeonStatusEntityManager().get().find(cqlquery);
			inDb.setHp(uds.get(0).getHp());
			inDb.setMana(uds.get(0).getMana());
			getUserEntityManager().get().put(inDb);
			
			//update durability
			double percentDamage = getEquipmentServices().DurabilityCostsDueToActionsPerformed(uds.get(0).getActionsPerformed());
			for(UserEquip ue : equippedEquips) {
				ue.setDurability(ue.getDurability()-percentDamage);
				getUserEquipEntityManager().get().put(ue);
			}
			
			//add chests, items, and equips to user chest, item, and equip table
			for(ChestProto crp : chestsRewarded) {
				UserChest uc = new UserChest();
				UUID newId = UUID.randomUUID();
				UUID chestId = UUID.fromString(crp.getChestID());
				uc.setChestId(chestId);
				uc.setCombatRoomId(dungeonRoomId);
				uc.setId(newId);
				uc.setLevelOfUserWhenAcquired(inDb.getLvl());
				uc.setTimeAcquired(clientDate);
				uc.setUserId(inDb.getId());
				getUserChestEntityManager().get().put(uc);
			}
			for(ItemProto ip : itemsRewarded) {
				UserItem ui = new UserItem();
				UUID newId = UUID.randomUUID();
				//TODO: CORRECT THESE
//				ui.setDungeonRoomAcquiredIn(dungeonRoomId);
				ui.setId(newId);
				//TODO: CORRECT THESE
//				ui.setName(ip.getItemName());
				ui.setLevelOfUserWhenAcquired(inDb.getLvl());
				ui.setTimeAcquired(clientDate);
				ui.setUserId(inDb.getId());
				getUserItemEntityManager().get().put(ui);
			}
			for(EquipmentProto ep : equipmentsRewarded) {
				UserEquip ue = new UserEquip();
				UUID newId = UUID.randomUUID();
//				ue.setDungeonRoomOrChestAcquiredFrom(dungeonRoomName);
				ue.setLevelOfUserWhenAcquired(inDb.getLvl());
				ue.setTimeAcquired(clientDate);
				ue.setId(newId);
				ue.setUserId(inDb.getId());
				ue.setEquipped(false);
//				ue.setEquipLevel(1);
//				ue.setName(ep.getName());
				ue.setDurability(100.0);
				getUserEquipEntityManager().get().put(ue);
				
			}
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
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

	public UserDungeonStatusEntityManager getUserDungeonStatusEntityManager() {
		return userDungeonStatusEntityManager;
	}

	public void setUserDungeonStatusEntityManager(
			UserDungeonStatusEntityManager userDungeonStatusEntityManager) {
		this.userDungeonStatusEntityManager = userDungeonStatusEntityManager;
	}

	public UserDungeonStatusHistoryEntityManager getUserDungeonStatusHistoryEntityManager() {
		return userDungeonStatusHistoryEntityManager;
	}

	public void setUserDungeonStatusHistoryEntityManager(
			UserDungeonStatusHistoryEntityManager userDungeonStatusHistoryEntityManager) {
		this.userDungeonStatusHistoryEntityManager = userDungeonStatusHistoryEntityManager;
	}


	public UserStructureService getUserStructureService() {
		return userStructureService;
	}

	public void setUserStructureService(UserStructureService userStructureService) {
		this.userStructureService = userStructureService;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}

	public EquipmentService getEquipmentServices() {
		return equipmentServices;
	}

	public void setEquipmentServices(EquipmentService equipmentServices) {
		this.equipmentServices = equipmentServices;
	}

	public UserEquipService getUserEquipService() {
		return userEquipService;
	}

	public void setUserEquipService(UserEquipService userEquipService) {
		this.userEquipService = userEquipService;
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


	
	
	
	
	
	
	
	
	
	

}