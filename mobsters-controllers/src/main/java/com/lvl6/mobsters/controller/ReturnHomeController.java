package com.lvl6.mobsters.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;



@Component
public class ReturnHomeController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	

	@Autowired
	protected MonsterForUserEntityManager monsterForUserEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected MonsterForUserService monsterForUserService; 
	
	@Override
	public RequestEvent createRequestEvent() {
		return null;// new ReturnHomeRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;// MobstersEventProtocolRequest.C_RETURN_HOME_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		/*//stuff client sent
		ReturnHomeRequestProto reqProto = 
				((ReturnHomeRequestEvent) event).getReturnHomeRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		int userHp = reqProto.getUserHp();
		int userMana = reqProto.getUserMana();
		int actionsPerformed = reqProto.getActionsPerformed();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();

		//response to send back to client
		Builder responseBuilder = ReturnHomeResponseProto.newBuilder();
		responseBuilder.setStatus(ReturnHomeStatus.FAIL_OTHER);
		ReturnHomeResponseEvent resEvent =
				new ReturnHomeResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			List<MonsterForUser> ueList = getUserEquipService().getAllUserEquipsForUser(userId);
			List<MonsterForUser> equippedEquips = new ArrayList<MonsterForUser>();
			//List<Structure> sList = new ArrayList<Structure>(); 
			getUserEquipService().getEquippedUserEquips(ueList, equippedEquips);
			
			boolean successful = false;
			successful = writeChangesToDb(inDb, userHp, userMana, actionsPerformed, equippedEquips, clientDate);
			
			if (successful) {
				responseBuilder.setStatus(ReturnHomeStatus.SUCCESS);
			}

			//write to client
			resEvent.setReturnHomeResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in ReturnHomeController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(ReturnHomeStatus.FAIL_OTHER);
				resEvent.setReturnHomeResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in ReturnHomeController processRequestEvent", e2);
			}
		}*/
	}

	/*
	private boolean writeChangesToDb(User inDb, int userHp, int userMana, int actionsPerformed, List<MonsterForUser> equippedEquips, Date clientDate) {

			try {
			//update user
			inDb.setHp(userHp);
			inDb.setMana(userMana);
			getUserEntityManager().get().put(inDb);
			
			//update durability
			double percentDamage = getEquipmentService().DurabilityCostsDueToActionsPerformed(actionsPerformed);
			for(MonsterForUser ue : equippedEquips) {
				ue.setDurability(ue.getDurability()-percentDamage);
				getUserEquipEntityManager().get().put(ue);
			}
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	

	public MonsterForUserEntityManager getUserEquipEntityManager() {
		return monsterForUserEntityManager;
	}

	public void setUserEquipEntityManager(
			MonsterForUserEntityManager monsterForUserEntityManager) {
		this.monsterForUserEntityManager = monsterForUserEntityManager;
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


	public MonsterForUserService getUserEquipService() {
		return monsterForUserService;
	}

	public void setUserEquipService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

	public StructureLabService getEquipmentService() {
		return structureLabService;
	}

	public void setEquipmentService(StructureLabService structureLabService) {
		this.equipmentService = structureLabService;
	}

		*/

}
