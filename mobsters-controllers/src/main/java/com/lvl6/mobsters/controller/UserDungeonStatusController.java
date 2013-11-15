package com.lvl6.mobsters.controller;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


//import com.lvl6.mobsters.entitymanager.staticdata.UserSpellRetrieveUtils;
import com.lvl6.mobsters.entitymanager.UserDungeonStatusEntityManager;
import com.lvl6.mobsters.entitymanager.UserDungeonStatusHistoryEntityManager;
import com.lvl6.mobsters.entitymanager.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.CombatRoomRetrieveUtils;
import com.lvl6.mobsters.eventprotos.UserDungeonStatusProto.UserDungeonStatusRequestProto;
import com.lvl6.mobsters.eventprotos.UserDungeonStatusProto.UserDungeonStatusResponseProto;
import com.lvl6.mobsters.eventprotos.UserDungeonStatusProto.UserDungeonStatusResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.UserDungeonStatusProto.UserDungeonStatusResponseProto.UserDungeonStatusStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.UserDungeonStatusRequestEvent;
import com.lvl6.mobsters.events.response.UserDungeonStatusResponseEvent;
import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.User;
import com.lvl6.mobsters.po.UserDungeonStatus;
import com.lvl6.mobsters.po.UserDungeonStatusHistory;
import com.lvl6.mobsters.services.userdungeonstatus.UserDungeonStatusService;
import com.lvl6.mobsters.services.userdungeonstatushistory.UserDungeonStatusHistoryService;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@Component
public class UserDungeonStatusController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserDungeonStatusService userDungeonStatusService; 
	
	@Autowired
	protected UserDungeonStatusHistoryService userDungeonStatusHistoryService;
	
	@Autowired
	protected UserDungeonStatusEntityManager userDungeonStatusEntityManager; 

	@Autowired
	protected UserDungeonStatusHistoryEntityManager userDungeonStatusHistoryEntityManager;
	
	@Autowired 
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected CombatRoomRetrieveUtils combatRoomRetrieveUtils;
	

	@Override
	public RequestEvent createRequestEvent() {
		return new UserDungeonStatusRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_TRAIN_OR_UPGRADE_SPELL_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		UserDungeonStatusRequestProto reqProto = 
				((UserDungeonStatusRequestEvent) event).getUserDungeonStatusRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		
		Date clientDate = new Date();
		
		int userHp = reqProto.getUserHp();
		int userMana = reqProto.getUserMana();
		int actionsPerformed = reqProto.getActionsPerformed();	
	
		
		String combatRoomIdStr = reqProto.getCombatRoomId();
		UUID combatRoomId = UUID.fromString(combatRoomIdStr);
//		UUID dungeonRoomId = UUID.fromString(dungeonRoomName);
//		CombatRoom dungeonRoom = getCombatRoomRetrieveUtils().getCombatRoomForId(dungeonRoomId);
		
		
		//response to send back to client
		Builder responseBuilder = UserDungeonStatusResponseProto.newBuilder();
		responseBuilder.setStatus(UserDungeonStatusStatus.FAIL_OTHER);
		UserDungeonStatusResponseEvent resEvent =
				new UserDungeonStatusResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			UserDungeonStatus uds = getUserDungeonStatusEntityManager().get().get(userId);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					userHp, userMana, actionsPerformed, clientDate);

			boolean successful = false;
			if (validRequest) 
				successful = writeChangesToDb(inDb, userHp, userMana,
						actionsPerformed, uds, userId, combatRoomId,
						clientDate);
			

			if (successful) {
				responseBuilder.setStatus(UserDungeonStatusStatus.SUCCESS);
			}

			//write to client
			resEvent.setUserDungeonStatusResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in UserDungeonStatusController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(UserDungeonStatusStatus.FAIL_OTHER);
				resEvent.setUserDungeonStatusResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in UserDungeonStatusController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, int userHp, int userMana, int actionsPerformed, Date clientDate) throws ConnectionException {
		if (null == inDb) {
			log.error("unexpected error: no user exists or has no status. sender=" + sender +
					"\t inDb=" + inDb + "\t us=");
			responseBuilder.setStatus(UserDungeonStatusStatus.FAIL_OTHER);
			return false;
		}
		
		//check if user's hp and mana make sense
		if(inDb.getHp() < 0 || inDb.getMana() < 0) {
			log.error("user has negative hp/mana");
			responseBuilder.setStatus(UserDungeonStatusStatus.FAIL_NEGATIVE_HP_OR_MANA);
			return false;
		}
			
		//check if user's actions > 0
		if(actionsPerformed <= 0) {
			log.error("no actions performed");
			responseBuilder.setStatus(UserDungeonStatusStatus.FAIL_NO_ACTIONS_PERFORMED);
			return false;
		}
		
		responseBuilder.setStatus(UserDungeonStatusStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, int userHp, int userMana,
			int actionsPerformed, UserDungeonStatus uds, UUID userId, UUID combatRoomId, Date clientDate) {
			
		try {
			//update user dungeon status
			uds.setHp(userHp);
			uds.setMana(userMana);
			uds.setActionsPerformed(uds.getActionsPerformed() + actionsPerformed);
			uds.setCurrentTime(clientDate);
			//TODO: FIX THIS
//			uds.setDungeonRoomName(combatRoomId);
			getUserDungeonStatusEntityManager().get().put(uds);
			
			//add to user dungeon status history
			UUID newId = UUID.randomUUID();
			UserDungeonStatusHistory udsh = new UserDungeonStatusHistory();
			udsh.setActionsPerformed(actionsPerformed);
			udsh.setHp(userHp);
			udsh.setMana(userMana);
			udsh.setCurrentTime(clientDate);
			udsh.setId(newId);
			udsh.setUserId(userId);
			//TODO: FIX THIS
//			udsh.setDungeonRoomName(dungeonRoomName);
			getUserDungeonStatusHistoryEntityManager().get().put(udsh);
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	
	



	public UserDungeonStatusService getUserDungeonStatusService() {
		return userDungeonStatusService;
	}

	public void setUserDungeonStatusService(
			UserDungeonStatusService userDungeonStatusService) {
		this.userDungeonStatusService = userDungeonStatusService;
	}

	public UserDungeonStatusHistoryService getUserDungeonStatusHistoryService() {
		return userDungeonStatusHistoryService;
	}

	public void setUserDungeonStatusHistoryService(
			UserDungeonStatusHistoryService userDungeonStatusHistoryService) {
		this.userDungeonStatusHistoryService = userDungeonStatusHistoryService;
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

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}

	public CombatRoomRetrieveUtils getCombatRoomRetrieveUtils() {
		return combatRoomRetrieveUtils;
	}

	public void setCombatRoomRetrieveUtils(
			CombatRoomRetrieveUtils combatRoomRetrieveUtils) {
		this.combatRoomRetrieveUtils = combatRoomRetrieveUtils;
	}
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}