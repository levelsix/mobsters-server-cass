package com.lvl6.mobsters.controller;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import com.lvl6.mobsters.entitymanager.staticdata.UserSpellRetrieveUtils;
import com.lvl6.mobsters.entitymanager.UserSpellEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.SpellRetrieveUtils;
import com.lvl6.mobsters.eventprotos.SpeedUpTrainOrUpgradeSpellEventProto.SpeedUpTrainOrUpgradeSpellRequestProto;
import com.lvl6.mobsters.eventprotos.SpeedUpTrainOrUpgradeSpellEventProto.SpeedUpTrainOrUpgradeSpellResponseProto;
import com.lvl6.mobsters.eventprotos.SpeedUpTrainOrUpgradeSpellEventProto.SpeedUpTrainOrUpgradeSpellResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.SpeedUpTrainOrUpgradeSpellEventProto.SpeedUpTrainOrUpgradeSpellResponseProto.SpeedUpTrainOrUpgradeSpellStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.SpeedUpTrainOrUpgradeSpellRequestEvent;
import com.lvl6.mobsters.events.response.SpeedUpTrainOrUpgradeSpellResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.Spell;
import com.lvl6.mobsters.po.UserSpell;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.userspell.UserSpellService;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@Component
public class SpeedUpTrainOrUpgradeSpellController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected SpellRetrieveUtils spellRetrieveUtils; 
	
	@Autowired
	protected UserEntityManager userEntityManager; 
	
	@Autowired
	protected UserService userService; 

	@Autowired
	protected UserSpellService userSpellService; 
	
	@Autowired
	protected UserSpellEntityManager userSpellEntityManager;

	@Override
	public RequestEvent createRequestEvent() {
		return new SpeedUpTrainOrUpgradeSpellRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_TRAIN_OR_UPGRADE_SPELL_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		SpeedUpTrainOrUpgradeSpellRequestProto reqProto = 
				((SpeedUpTrainOrUpgradeSpellRequestEvent) event).getSpeedUpTrainOrUpgradeSpellRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();
		String userSpellIdString = reqProto.getSpellId();
		UUID userSpellId = UUID.fromString(userSpellIdString);

		//response to send back to client
		Builder responseBuilder = SpeedUpTrainOrUpgradeSpellResponseProto.newBuilder();
		responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.FAIL_OTHER);
		SpeedUpTrainOrUpgradeSpellResponseEvent resEvent =
				new SpeedUpTrainOrUpgradeSpellResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			UserSpell us = getUserSpellEntityManager().get().get(userSpellId);
			Spell s = getUserSpellService().getSpellCorrespondingToUserSpell(us);;

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					us, s, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, us, s, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.SUCCESS);
			}

			//write to client
			resEvent.setSpeedUpTrainOrUpgradeSpellResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in SpeedUpTrainOrUpgradeSpellController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.FAIL_OTHER);
				resEvent.setSpeedUpTrainOrUpgradeSpellResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in SpeedUpTrainOrUpgradeSpellController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, UserSpell us, Spell s, Date clientDate) throws ConnectionException {
		if (null == inDb || null == us) {
			log.error("unexpected error: no user exists. sender=" + sender +
					"\t inDb=" + inDb + "\t us=" + us);
			responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.FAIL_OTHER);
			return false;
		}

		int millisPassed = (int)(clientDate.getTime() - us.getTimeAcquired().getTime());
		int minutesRemaining = (s.getResearchTimeMillis() - millisPassed)/60000;
		int gemSpeedUpCost = getUserService().calculateGemCostForSpeedUp(minutesRemaining);
		
		if(inDb.getGems() < gemSpeedUpCost) {
			log.error("user doesnt have enough gems to speed up");
			responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.FAIL_NOT_ENOUGH_GEMS);
			return false;
		}
		
		responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, UserSpell us,
			Spell s, Date clientDate) {
		try {
			int millisPassed = (int)(clientDate.getTime() - us.getTimeAcquired().getTime());
			int minutesRemaining = (s.getResearchTimeMillis() - millisPassed)/60000;
			int gemSpeedUpCost = getUserService().calculateGemCostForSpeedUp(minutesRemaining);

			inDb.setGems(inDb.getGems()-gemSpeedUpCost);
			getUserEntityManager().get().put(inDb);
			
			//update user spell
			us.setIsTraining(false);
			getUserSpellEntityManager().get().put(us);
		
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		
	
	
	

	
	public UserSpellService getUserSpellService() {
		return userSpellService;
	}

	public void setUserSpellService(UserSpellService userSpellService) {
		this.userSpellService = userSpellService;
	}

	public SpellRetrieveUtils getSpellRetrieveUtils() {
		return spellRetrieveUtils;
	}

	public void setSpellRetrieveUtils(
			SpellRetrieveUtils spellRetrieveUtils) {
		this.spellRetrieveUtils = spellRetrieveUtils;
	}

	public UserSpellEntityManager getUserSpellEntityManager() {
		return userSpellEntityManager;
	}

	public void setUserSpellEntityManager(
			UserSpellEntityManager userSpellEntityManager) {
		this.userSpellEntityManager = userSpellEntityManager;
	}

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	
	
	
	
	
	
	
	
	
	

}