package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.lvl6.mobsters.entitymanager.UserDungeonStatusEntityManager;
import com.lvl6.mobsters.entitymanager.UserDungeonStatusHistoryEntityManager;
import com.lvl6.mobsters.entitymanager.UserEntityManager;
import com.lvl6.mobsters.entitymanager.UserEquipEntityManager;
import com.lvl6.mobsters.eventprotos.ReturnHomeEventProto.ReturnHomeRequestProto;
import com.lvl6.mobsters.eventprotos.ReturnHomeEventProto.ReturnHomeResponseProto;
import com.lvl6.mobsters.eventprotos.ReturnHomeEventProto.ReturnHomeResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.ReturnHomeEventProto.ReturnHomeResponseProto.ReturnHomeStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.ReturnHomeRequestEvent;
import com.lvl6.mobsters.events.response.ReturnHomeResponseEvent;
import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.User;
import com.lvl6.mobsters.po.UserEquip;
import com.lvl6.mobsters.services.equipment.EquipmentService;
import com.lvl6.mobsters.services.userequip.UserEquipService;



@Component
public class ReturnHomeController extends EventController {

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
	protected UserEquipService userEquipService; 
	
	@Autowired
	protected EquipmentService equipmentService;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new ReturnHomeRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_RETURN_HOME_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
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
			List<UserEquip> ueList = getUserEquipService().getAllUserEquipsForUser(userId);
			List<UserEquip> equippedEquips = new ArrayList<UserEquip>();
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
		}
	}

	
	private boolean writeChangesToDb(User inDb, int userHp, int userMana, int actionsPerformed, List<UserEquip> equippedEquips, Date clientDate) {

			try {
			//update user
			inDb.setHp(userHp);
			inDb.setMana(userMana);
			getUserEntityManager().get().put(inDb);
			
			//update durability
			double percentDamage = getEquipmentService().DurabilityCostsDueToActionsPerformed(actionsPerformed);
			for(UserEquip ue : equippedEquips) {
				ue.setDurability(ue.getDurability()-percentDamage);
				getUserEquipEntityManager().get().put(ue);
			}
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
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


	public UserEquipService getUserEquipService() {
		return userEquipService;
	}

	public void setUserEquipService(UserEquipService userEquipService) {
		this.userEquipService = userEquipService;
	}

	public EquipmentService getEquipmentService() {
		return equipmentService;
	}

	public void setEquipmentService(EquipmentService equipmentService) {
		this.equipmentService = equipmentService;
	}

		

	
	
	
	
	
	
	
	
	
	

}