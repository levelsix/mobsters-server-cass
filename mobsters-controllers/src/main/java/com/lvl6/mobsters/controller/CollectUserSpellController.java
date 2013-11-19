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
import com.lvl6.mobsters.eventprotos.CollectUserSpellEventProto.CollectUserSpellRequestProto;
import com.lvl6.mobsters.eventprotos.CollectUserSpellEventProto.CollectUserSpellResponseProto;
import com.lvl6.mobsters.eventprotos.CollectUserSpellEventProto.CollectUserSpellResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.CollectUserSpellEventProto.CollectUserSpellResponseProto.CollectUserSpellStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.CollectUserSpellRequestEvent;
import com.lvl6.mobsters.events.response.CollectUserSpellResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.Spell;
import com.lvl6.mobsters.po.UserSpell;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.userspell.UserSpellService;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@Component
public class CollectUserSpellController extends EventController {

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
		return new CollectUserSpellRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_TRAIN_OR_UPGRADE_SPELL_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		CollectUserSpellRequestProto reqProto = 
				((CollectUserSpellRequestEvent) event).getCollectUserSpellRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		boolean isTraining = reqProto.getIsTraining();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();
		String userSpellIdString = reqProto.getTrainedSpellId();
		UUID userSpellId = UUID.fromString(userSpellIdString);

		//response to send back to client
		Builder responseBuilder = CollectUserSpellResponseProto.newBuilder();
		responseBuilder.setStatus(CollectUserSpellStatus.FAIL_OTHER);
		CollectUserSpellResponseEvent resEvent =
				new CollectUserSpellResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			UserSpell us = getUserSpellEntityManager().get().get(userSpellId);
			Spell s = getUserSpellService().getSpellCorrespondingToUserSpell(us);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					us, s, isTraining, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, us, s, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(CollectUserSpellStatus.SUCCESS);
			}

			//write to client
			resEvent.setCollectUserSpellResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in CollectUserSpellController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(CollectUserSpellStatus.FAIL_OTHER);
				resEvent.setCollectUserSpellResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in CollectUserSpellController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, UserSpell us, Spell s, boolean isTraining, Date clientDate) throws ConnectionException {
		if (null == inDb || null == us) {
			log.error("unexpected error: no user exists. sender=" + sender +
					"\t inDb=" + inDb + "\t us=" + us);
			return false;
		}

		if(!isTraining) {
			log.error("user spell not training");
			responseBuilder.setStatus(CollectUserSpellStatus.FAIL_OTHER);
			return false;
		}
		
		if(us.getTimeAcquired().getTime() + s.getResearchTimeMillis() > clientDate.getTime()) {
			log.error("user spell not done training");
			responseBuilder.setStatus(CollectUserSpellStatus.FAIL_NOT_COMPLETE);
			return false;
		}
		
		responseBuilder.setStatus(CollectUserSpellStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, UserSpell us,
			Spell s, Date clientDate) {
		try {
			//update userspell
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