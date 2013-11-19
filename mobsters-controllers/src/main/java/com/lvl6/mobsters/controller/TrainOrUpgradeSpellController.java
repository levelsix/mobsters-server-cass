package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import com.lvl6.mobsters.entitymanager.staticdata.UserSpellRetrieveUtils;
import com.lvl6.mobsters.entitymanager.UserSpellEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.SpellRetrieveUtils;
import com.lvl6.mobsters.eventprotos.TrainOrUpgradeSpellEventProto.ResourceCostType;
import com.lvl6.mobsters.eventprotos.TrainOrUpgradeSpellEventProto.TrainOrUpgradeSpellRequestProto;
import com.lvl6.mobsters.eventprotos.TrainOrUpgradeSpellEventProto.TrainOrUpgradeSpellResponseProto;
import com.lvl6.mobsters.eventprotos.TrainOrUpgradeSpellEventProto.TrainOrUpgradeSpellResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.TrainOrUpgradeSpellEventProto.TrainOrUpgradeSpellResponseProto.TrainOrUpgradeSpellStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.TrainOrUpgradeSpellRequestEvent;
import com.lvl6.mobsters.events.response.TrainOrUpgradeSpellResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.Spell;
import com.lvl6.mobsters.po.UserSpell;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.userspell.UserSpellService;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@Component
public class TrainOrUpgradeSpellController extends EventController {

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
		return new TrainOrUpgradeSpellRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_TRAIN_OR_UPGRADE_SPELL_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		TrainOrUpgradeSpellRequestProto reqProto = 
				((TrainOrUpgradeSpellRequestEvent) event).getTrainOrUpgradeSpellRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		boolean usingGems = reqProto.getUsingGems();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();
		String userSpellIdString = reqProto.getSpellId();
		UUID userSpellId = UUID.fromString(userSpellIdString);

		//response to send back to client
		Builder responseBuilder = TrainOrUpgradeSpellResponseProto.newBuilder();
		responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_OTHER);
		TrainOrUpgradeSpellResponseEvent resEvent =
				new TrainOrUpgradeSpellResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			UserSpell us = getUserSpellEntityManager().get().get(userSpellId);
			List<Spell> sList = new ArrayList<Spell>();

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					us, sList, usingGems, clientDate);

			boolean successful = false;
			if (validRequest) {
				Spell s = sList.get(0);
				successful = writeChangesToDb(inDb, us, s, usingGems, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(TrainOrUpgradeSpellStatus.SUCCESS);
			}

			//write to client
			resEvent.setTrainOrUpgradeSpellResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in TrainOrUpgradeSpellController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_OTHER);
				resEvent.setTrainOrUpgradeSpellResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in TrainOrUpgradeSpellController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, UserSpell us, List<Spell> sList, boolean usingGems, Date clientDate) throws ConnectionException {
		if (null == inDb || null == us) {
			log.error("unexpected error: no user exists. sender=" + sender +
					"\t inDb=" + inDb + "\t us=" + us);
			return false;
		}

		UUID id = us.getId();
		UUID spellId = us.getId();
		Spell s = getSpellRetrieveUtils().getSpellForId(id);

		if (null == s) {
			log.error("unexpected error: no spell with id exists. id=" + spellId);
			responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_OTHER);
			return false;
		}

		//check if user meets level requirement of spell
		if(inDb.getLvl() < s.getUserLevelRequired()) {
			log.error("user is not high enough level to train/upgrade. user level=" + inDb.getLvl() + 
					", required level: " + s.getUserLevelRequired());
			responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_NOT_AT_REQUIRED_LEVEL);
			return false;
		}
			
		//check if user's spell has a further upgrade
		if(spellRetrieveUtils.getUpgradedSpell(s) == null) {
			log.error("no upgrade of user's spell with id: " + id + "exists");
			responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_SPELL_AT_MAX_LEVEL);
			return false;
		}
		
		if(!trainingIsCompleteBeforeAttemptingUpgrade(s, us, clientDate)) {
			log.error("user trying to upgrade before training is complete");
			responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_CANT_UPGRADE_WHILE_TRAINING);
			return false;
		}
		
		if(!usingGems) {
			if(s.getResearchCostResource() == ResourceCostType.GOLD_VALUE) {
				if(inDb.getGold() < s.getResearchCost()) {
					log.error("user doesn't have enough gold to train new spell");
					responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_INSUFFICIENT_RESOURCES);
					return false;
				}
			} else if(s.getResearchCostResource() == ResourceCostType.TONIC_VALUE) {
				if(inDb.getTonic() < s.getResearchCost()) {
					log.error("user doesn't have enough tonic to train new spell");
					responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_INSUFFICIENT_RESOURCES);
					return false;
				}
			} else {
				log.error("spell doesn't cost either gold or tonic...strange...Hi Ashwin/Art");
				responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_OTHER);
				return false;
			}
		}
		else {
			if(s.getResearchCostResource() == ResourceCostType.GOLD_VALUE) {
				int missingGoldResources = s.getResearchCost() - inDb.getGold();
				if(inDb.getGems() < getUserService().calculateGemCostForMissingResources(inDb, missingGoldResources, ResourceCostType.GOLD_VALUE)) {
					log.error("user doesn't have enough gems to buy missing resources");
					responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_NOT_ENOUGH_GEMS);
					return false;
				}
			}
			else {
				int missingTonicResources = s.getResearchCost() - inDb.getTonic();
				if(inDb.getGems() < getUserService().calculateGemCostForMissingResources(inDb, missingTonicResources, ResourceCostType.TONIC_VALUE)) {
					log.error("user doesn't have enough gems to buy missing resources");
					responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_NOT_ENOUGH_GEMS);
					return false;
				}
			}
			
			
		}
		
		int count=0;
		List<UserSpell> userSpells = getUserSpellService().getAllUserSpellsForUser(inDb.getId());
		for(UserSpell us2: userSpells) {
			if(us2.getIsTraining())
				count++;
		}
		
		if(count >= 1) {
			log.error("user already has max training occuring");
			responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_MAXED_TRAINING);
			return false;
		}
		
		//check class
		if(inDb.getClassType() != s.getClassType()) {
			log.error("user is not appropriate class for spell");
			responseBuilder.setStatus(TrainOrUpgradeSpellStatus.FAIL_WRONG_CLASS_TYPE);
			return false;
		}
		
		sList.add(s);
		responseBuilder.setStatus(TrainOrUpgradeSpellStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, UserSpell us,
			Spell s, boolean usingGems, Date clientDate) {
		try {
			if(!usingGems) {
				if(s.getResearchCost() == ResourceCostType.GOLD_VALUE) {
					inDb.setGold(inDb.getGold()-s.getResearchCost());
				}
				else {
					inDb.setTonic(inDb.getTonic()-s.getResearchCost());
				}
			}
			else {
				if(s.getResearchCost() == ResourceCostType.GOLD_VALUE) {
					int missingGoldResources = s.getResearchCost() - inDb.getGold();
					inDb.setGold(0);
					inDb.setGems(inDb.getGems() - getUserService().calculateGemCostForMissingResources
							(inDb, missingGoldResources, ResourceCostType.GOLD_VALUE));
				}
				else {
					int missingTonicResources = s.getResearchCost() - inDb.getTonic();
					inDb.setTonic(0);
					inDb.setGems(inDb.getGems() - getUserService().calculateGemCostForMissingResources
							(inDb, missingTonicResources, ResourceCostType.TONIC_VALUE));
				}
			}
			//update user
			getUserEntityManager().get().put(inDb);
			UUID newId = UUID.randomUUID();
			//and update his user spell rows
			
			//update user spell
			UserSpell us2 = new UserSpell();
			
			us2.setId(newId);
			us2.setUserId(inDb.getId());
			us2.setSpellId(s.getId());
//			us2.setSpellLvl(s.getLvl());
			us2.setTimeAcquired(clientDate);
			us2.setIsTraining(true);
			us2.setLevelOfUserWhenUpgrading(inDb.getLvl());
			getUserSpellEntityManager().get().put(us2);
		
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		
	
	private boolean trainingIsCompleteBeforeAttemptingUpgrade(Spell s, UserSpell us, Date clientDate) {
		int buildTime = s.getResearchTimeMillis();
		long purchaseTime = us.getTimeAcquired().getTime();
		if(buildTime + purchaseTime < clientDate.getTime())
			return true;
		else return false;
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