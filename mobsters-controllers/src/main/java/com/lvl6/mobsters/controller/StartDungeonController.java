package com.lvl6.mobsters.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.events.RequestEvent;



@Component
public class StartDungeonController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	
//	@Autowired
//	protected UserConsumableQueueService userConsumableQueueService;
//	
//	@Autowired
//	protected UserConsumableQueueEntityManager userConsumableQueueEntityManager;
//	
//	@Autowired
//	protected StructureForUserService structureForUserService;
//
//	@Autowired
//	protected UserEntityManager userEntityManager;
//	
//	@Autowired
//	protected MonsterForUserService monsterForUserService;
//	
//	@Autowired
//	protected UserCurrencyHistoryService userCurrencyHistoryService;
//	
//	@Autowired
//	protected PreDungeonUserInfoEntityManager preDungeonUserInfoEntityManager;
//
//	@Autowired
//	protected PreDungeonUserEquipInfoEntityManager preDungeonUserEquipInfoEntityManager;
//	
//	@Autowired
//	protected PreDungeonUserConsumableInfoEntityManager preDungeonUserConsumableInfoEntityManager;
//	
//	@Autowired
//	protected TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtils;
//	
//	@Autowired
//	protected TimeUtils timeUtils;
//
//	@Autowired
//	protected UserService userService;


	@Override
	public RequestEvent createRequestEvent() {
		return null;//new StartDungeonRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;//MobstersEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		/*StartDungeonRequestProto reqProto = 
				((StartDungeonRequestEvent) event).getStartDungeonRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		List<UserConsumablesProto> ucpList = reqProto.getUcpListList();
		List<UserEquipmentProto> uepList = reqProto.getUerListList();
		String combatRoomIdStr = reqProto.getCombatRoomId();
		UUID combatRoomId = UUID.fromString(combatRoomIdStr);
				
		Date clientDate = new Date();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = StartDungeonResponseProto.newBuilder();
		responseBuilder.setStatus(StartDungeonStatus.FAIL_OTHER);
		StartDungeonResponseEvent resEvent =
				new StartDungeonResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			
			Map<QuestForUser, Integer> userConsumablesMap = getUserConsumableService().convertListToMap(ucpList);
			List<MonsterForUser> equippedList = getUserEquipService().getAllEquippedUserEquipsForUser(inDb.getId());
			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb, 
					uepList, equippedList, userConsumablesMap, combatRoomIdStr, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, 
						uepList, equippedList, userConsumablesMap, combatRoomId, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(StartDungeonStatus.SUCCESS);
			}

			//write to client
			resEvent.setStartDungeonResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in StartDungeonController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(StartDungeonStatus.FAIL_OTHER);
				resEvent.setStartDungeonResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in StartDungeonController processRequestEvent", e2);
			}
		}
		*/
	}

/*

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, List<UserEquipmentProto> ueqList, List<MonsterForUser> equippedList,
			Map<QuestForUser, Integer> userConsumablesMap, 
			String dungeonName, Date clientDate) throws Exception {
	
		if(inDb == null || dungeonName == null) {
			log.error("no user or dungeon exists");
			responseBuilder.setStatus(StartDungeonStatus.FAIL_OTHER);
			return false;
		}
		
		//user can't enter dungeon with full storage
		List<StructureForUser> usList = getUserStructureService().getAllUserStructuresForUser(inDb.getId());
		int equipStorageSize = 0;
		for(StructureForUser us : usList) {
			Structure s = getUserStructureService().getStructureCorrespondingToUserStructure(us);
			if(s.getFunctionalityType() == FunctionalityType.STORAGE_VALUE) {
				equipStorageSize = s.getFunctionalityCapacity();
			}
		}
		
		if(ueqList.size() >= equipStorageSize) {
			log.error("user's equip storage over capacity");
			responseBuilder.setStatus(StartDungeonStatus.FAIL_EQUIP_STORAGE_FULL);
			return false;
		}
		
		//make sure user has hp
		if(inDb.getHp() <= 0) {
			log.error("user's hp is negative or zero");
			responseBuilder.setStatus(StartDungeonStatus.FAIL_NO_HP);
			return false;
		}
		
		//check if user is high enough lvl to enter particular dungeon
		TaskStageMonster dungeonRoom = getCombatRoomRetrieveUtils().getCombatRoomForName(dungeonName);
		if(inDb.getLvl() < dungeonRoom.getLvlRequired()) {
			log.error("user's lvl not high enough for room");
			responseBuilder.setStatus(StartDungeonStatus.FAIL_NOT_AT_REQUIRED_LEVEL);
			return false;
		}
		
		//check to make sure none of user's items have 0 durability
		for(MonsterForUser ue : equippedList) {
			if(ue.getDurability() == 0.0) {
				log.error("an equipped equip has 0 durability");
				responseBuilder.setStatus(StartDungeonStatus.FAIL_ZERO_DURABILITY_EQUIP);
				return false;
			}
		}
		
		return true;
	}


	private boolean writeChangesToDb(User inDb, 
			List<UserEquipmentProto> uepList, List<MonsterForUser> equippedList, 
			Map<QuestForUser, Integer> userConsumablesMap, UUID combatRoomId, Date clientDate) {
		try {
			//save all the info
			PreDungeonUserInfo pdui = new PreDungeonUserInfo();
			pdui.setCombatRoomId(combatRoomId);
			pdui.setHealth(inDb.getHp());
			pdui.setId(UUID.randomUUID());
			pdui.setLevelOfUser(inDb.getLvl());
			pdui.setMana(inDb.getMana());
			pdui.setTimeUserEntersDungeon(clientDate);
			pdui.setUserId(inDb.getId());
			getPreDungeonUserInfoEntityManager().get().put(pdui);
			
			for(MonsterForUser ue : equippedList) {
				PreDungeonUserEquipInfo pduei = new PreDungeonUserEquipInfo();
				pduei.setDurability(ue.getDurability());
				pduei.setEquipId(ue.getId());
				pduei.setId(UUID.randomUUID());
//				pduei.setLvl(ue.getEquipLevel());
				pduei.setUserId(inDb.getId());
				getPreDungeonUserEquipInfoEntityManager().get().put(pduei);
			}
			
			for(Map.Entry<QuestForUser, Integer> entry : userConsumablesMap.entrySet()) {
				QuestForUser uc = entry.getKey();
				Integer quantity = entry.getValue();
				PreDungeonUserConsumableInfo pduci = new PreDungeonUserConsumableInfo();
				pduci.setId(UUID.randomUUID());
				pduci.setConsumableId(uc.getId());
				pduci.setQuantity(quantity);
				pduci.setUserId(inDb.getId());
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

	public UserCurrencyHistoryService getUserConsumableService() {
		return userCurrencyHistoryService;
	}

	public void setUserConsumableService(UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}



	public StructureForUserService getUserStructureService() {
		return structureForUserService;
	}

	public void setUserStructureService(StructureForUserService structureForUserService) {
		this.userStructureService = structureForUserService;
	}

	public TaskStageMonsterRetrieveUtils getCombatRoomRetrieveUtils() {
		return taskStageMonsterRetrieveUtils;
	}

	public void setCombatRoomRetrieveUtils(
			TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtils) {
		this.taskStageMonsterRetrieveUtils = taskStageMonsterRetrieveUtils;
	}

	public PreDungeonUserInfoEntityManager getPreDungeonUserInfoEntityManager() {
		return preDungeonUserInfoEntityManager;
	}

	public void setPreDungeonUserInfoEntityManager(
			PreDungeonUserInfoEntityManager preDungeonUserInfoEntityManager) {
		this.preDungeonUserInfoEntityManager = preDungeonUserInfoEntityManager;
	}

	public PreDungeonUserEquipInfoEntityManager getPreDungeonUserEquipInfoEntityManager() {
		return preDungeonUserEquipInfoEntityManager;
	}

	public void setPreDungeonUserEquipInfoEntityManager(
			PreDungeonUserEquipInfoEntityManager preDungeonUserEquipInfoEntityManager) {
		this.preDungeonUserEquipInfoEntityManager = preDungeonUserEquipInfoEntityManager;
	}

	public PreDungeonUserConsumableInfoEntityManager getPreDungeonUserConsumableInfoEntityManager() {
		return preDungeonUserConsumableInfoEntityManager;
	}

	public void setPreDungeonUserConsumableInfoEntityManager(
			PreDungeonUserConsumableInfoEntityManager preDungeonUserConsumableInfoEntityManager) {
		this.preDungeonUserConsumableInfoEntityManager = preDungeonUserConsumableInfoEntityManager;
	}

	public MonsterForUserService getUserEquipService() {
		return monsterForUserService;
	}

	public void setUserEquipService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}
*/
}

