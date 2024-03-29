package com.lvl6.mobsters.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.LevelUpRequestEvent;



@Component
public class LevelUpController extends EventController {

//	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	
//	@Autowired
//	protected UserConsumableQueueService userConsumableQueueService;
//	
//	@Autowired
//	protected TaskHistoryEntityManager userConsumableQueueEntityManager;
//
//	@Autowired
//	protected UserEntityManager userEntityManager;
//	
//	@Autowired
//	protected StructureResourceStorageRetrieveUtils classLevelInfoRetrieveUtils;
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
		return new LevelUpRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;//MobstersEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		/*//stuff client sent
		LevelUpRequestProto reqProto = 
				((LevelUpRequestEvent) event).getLevelUpRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();		
		Date clientDate = new Date();
		List<DungeonProto> dpList = reqProto.getAllDungeonRoomsList();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = LevelUpResponseProto.newBuilder();
		responseBuilder.setStatus(LevelUpStatus.FAIL_OTHER);
		LevelUpResponseEvent resEvent =
				new LevelUpResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			
			StructureResourceStorage cli = getClassLevelInfoRetrieveUtils().getClassLevelInfoForClassAndLevel(inDb.getClassType(), inDb.getLvl()+1);
			

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb, cli, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, cli, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(LevelUpStatus.SUCCESS);
			}

			//write to client
			resEvent.setLevelUpResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			responseBuilder.setNewLevel(inDb.getLvl());
			responseBuilder.setNewAttack(cli.getAttack());
			responseBuilder.setNewDefense(cli.getDefense());
			responseBuilder.setNewHp(cli.getMaxHp());
			responseBuilder.setNewMana(cli.getMaxMana());
			responseBuilder.setNewNextLevel(inDb.getLvl()+1);
			responseBuilder.setExperienceRequiredForNewNextLevel(cli.getMaxExp());
			
			for(DungeonProto dp : dpList) {
				if(dp.getLevelReq() != inDb.getLvl())
					dpList.remove(dp);
			}
			
			responseBuilder.addAllDungeonsNewlyAvailableToUser(dpList);
			
		} catch (Exception e) {
			log.error("exception in LevelUpController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(LevelUpStatus.FAIL_OTHER);
				resEvent.setLevelUpResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in LevelUpController processRequestEvent", e2);
			}
		}*/
	}

/*

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, StructureResourceStorage cli, Date clientDate) throws Exception {
	
		if(inDb == null || cli == null) {
			log.error("user or class level info is null");
			responseBuilder.setStatus(LevelUpStatus.FAIL_OTHER);
			return false;
		}
		
		if(inDb.getExp() < cli.getMaxExp()) {
			log.error("user doesn't have required exp to level");
			responseBuilder.setStatus(LevelUpStatus.FAIL_NOT_ENOUGH_EXP);
			return false;
		}
		
		return true;
	}


	private boolean writeChangesToDb(User inDb, StructureResourceStorage cli, Date clientDate) {
		try {
			inDb.setLvl(inDb.getLvl()+1);
			inDb.setMaxHp(cli.getMaxHp());
			inDb.setMaxMana(cli.getMaxMana());
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

	public TaskHistoryEntityManager getUserConsumableQueueEntityManager() {
		return userConsumableQueueEntityManager;
	}

	public void setUserConsumableQueueEntityManager(
			TaskHistoryEntityManager userConsumableQueueEntityManager) {
		this.userConsumableQueueEntityManager = userConsumableQueueEntityManager;
	}

	public StructureResourceStorageRetrieveUtils getClassLevelInfoRetrieveUtils() {
		return classLevelInfoRetrieveUtils;
	}

	public void setClassLevelInfoRetrieveUtils(
			StructureResourceStorageRetrieveUtils classLevelInfoRetrieveUtils) {
		this.classLevelInfoRetrieveUtils = classLevelInfoRetrieveUtils;
	}

	public TaskStageMonsterRetrieveUtils getCombatRoomRetrieveUtils() {
		return taskStageMonsterRetrieveUtils;
	}

	public void setCombatRoomRetrieveUtils(
			TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtils) {
		this.taskStageMonsterRetrieveUtils = taskStageMonsterRetrieveUtils;
	}
*/	

}
