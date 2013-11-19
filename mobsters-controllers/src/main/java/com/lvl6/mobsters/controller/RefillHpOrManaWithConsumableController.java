package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.QuestForUserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.ConsumableRetrieveUtils;
import com.lvl6.mobsters.eventprotos.RefillHpOrManaWithConsumableEventProto.RefillHpOrManaWithConsumableRequestProto;
import com.lvl6.mobsters.eventprotos.RefillHpOrManaWithConsumableEventProto.RefillHpOrManaWithConsumableResponseProto;
import com.lvl6.mobsters.eventprotos.RefillHpOrManaWithConsumableEventProto.RefillHpOrManaWithConsumableResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.RefillHpOrManaWithConsumableEventProto.RefillHpOrManaWithConsumableResponseProto.RefillHpOrManaWithConsumableStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.RefillHpOrManaWithConsumableRequestEvent;
import com.lvl6.mobsters.events.response.RefillHpOrManaWithConsumableResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.Consumable.ConsumableType;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.Consumable;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;


@Component
public class RefillHpOrManaWithConsumableController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected ConsumableRetrieveUtils consumableRetrieveUtils; 

	@Autowired
	protected QuestForUserEntityManager questForUserEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;

	@Override
	public RequestEvent createRequestEvent() {
		return new RefillHpOrManaWithConsumableRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_REFILL_HP_OR_MANA_WITH_CONSUMABLE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		RefillHpOrManaWithConsumableRequestProto reqProto = 
				((RefillHpOrManaWithConsumableRequestEvent) event).getRefillHpOrManaWithConsumableRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();
		String userConsumableIdString = reqProto.getUserConsumableId();
		UUID userConsumableId = UUID.fromString(userConsumableIdString);

		//response to send back to client
		Builder responseBuilder = RefillHpOrManaWithConsumableResponseProto.newBuilder();
		responseBuilder.setStatus(RefillHpOrManaWithConsumableStatus.FAIL_OTHER);
		RefillHpOrManaWithConsumableResponseEvent resEvent =
				new RefillHpOrManaWithConsumableResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			QuestForUser uc = getUserConsumableEntityManager().get().get(userConsumableId);
			List<Consumable> cList = new ArrayList<Consumable>();

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					uc, cList, clientDate);

			boolean successful = false;
			if (validRequest) {
				Consumable c = cList.get(0);
				successful = writeChangesToDb(inDb, uc, c, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(RefillHpOrManaWithConsumableStatus.SUCCESS);
			}

			//write to client
			resEvent.setRefillHpOrManaWithConsumableResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RefillHpOrManaWithConsumableController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(RefillHpOrManaWithConsumableStatus.FAIL_OTHER);
				resEvent.setRefillHpOrManaWithConsumableResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in RefillHpOrManaWithConsumableController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, QuestForUser uc, List<Consumable> cList, Date clientDate) {
		if (null == inDb || null == uc) {
			log.error("unexpected error: no user exists. sender=" + sender +
					"\t inDb=" + inDb + "\t uc=" + uc);
			return false;
		}

		UUID consumableId = uc.getConsumableId();
		Consumable c = null; //getConsumableRetrieveUtils().getConsumableForConsumableId(consumableId);

		if (null == c) {
			log.error("unexpected error: no consumable with id exists. id=" + consumableId);
			responseBuilder.setStatus(RefillHpOrManaWithConsumableStatus.FAIL_NO_POT_EXISTS);
			return false;
		}

		//check if user is already at max hp and consumable is hp
		int cType = c.getFunctionalityType();
		if (ConsumableType.HEALTH_VALUE == cType &&
				inDb.getMaxHp() == inDb.getHp()) {
			log.error("client error: can't use pot, user is already at max " +
					"hp. user=" + inDb);
			responseBuilder.setStatus(RefillHpOrManaWithConsumableStatus.FAIL_ALREADY_AT_MAX_HP);
			return false;
		}

		//check if user is already at max mana and consumable is mana
		if (ConsumableType.MANA_VALUE == cType &&
				inDb.getMaxMana() == inDb.getMana()) {
			log.error("client error: can't use pot, user is already at max " +
					"mana. user=" + inDb);
			responseBuilder.setStatus(RefillHpOrManaWithConsumableStatus.FAIL_ALREADY_AT_MAX_MANA);
			return false;
		}

		//make sure user has enough of the pots
		if (uc.getQuantity() == 0) {
			log.error("client error: user does not have enough pots. user pot=" + uc +
					"\t consumable=" + c + "\t user=" + inDb);
			responseBuilder.setStatus(RefillHpOrManaWithConsumableStatus.FAIL_NOT_ENOUGH_POTS);
			return false;
		}

		if (uc.getQuantity() < 0) {
			log.error("unexpected error: somehow user has negative progress " +
					"of pots. consumable=" + c + " \t user pot=" + uc +
					"\t user=" + inDb);
			responseBuilder.setStatus(RefillHpOrManaWithConsumableStatus.FAIL_OTHER);
			return false;
		}

		//cutting down on retrieving consumable again
		cList.add(c);
		return true;
	}

	private boolean writeChangesToDb(User inDb, QuestForUser uc,
			Consumable c, Date clientDate) {
		try {
			//Figure out how much to regen
			int cType = c.getFunctionalityType();
			if (ConsumableType.HEALTH_VALUE == cType) {
				updateUserHealth(inDb, c);

			} else if (ConsumableType.MANA_VALUE == cType) {
				updateUserMana(inDb, c);

			} //more cases for consumables here

			//update user
			getUserEntityManager().get().put(inDb);

			//and update his consumable progress
			int newQuantity = uc.getQuantity() - 1;
			uc.setQuantity(newQuantity);
			getUserConsumableEntityManager().get().put(uc);

			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	private void updateUserHealth(User inDb, Consumable c) {
		int maxHp = inDb.getMaxHp();
		int newHp = inDb.getHp();

		double amount = c.getFunctionalityConstant();
		if (amount < 1.0d) {
			//acts as a percent refill based on max hp
			newHp += (int) (amount * maxHp);

		} else {
			//flat refill
			newHp += (int) amount;
		}

		newHp = Math.min(newHp, maxHp);

		inDb.setHp(newHp);
	}

	private void updateUserMana(User inDb, Consumable c) {
		int maxMana = inDb.getMaxMana();
		int newMana = inDb.getMana();

		double amount = c.getFunctionalityConstant();
		if (amount < 1.0d) {
			//percent refill
			newMana += (int) (amount * maxMana);

		} else {
			newMana += (int) amount;
		}

		newMana = Math.min(newMana, maxMana);
		inDb.setMana(newMana);
	}



	public ConsumableRetrieveUtils getConsumableRetrieveUtils() {
		return consumableRetrieveUtils;
	}

	public void setConsumableRetrieveUtils(
			ConsumableRetrieveUtils consumableRetrieveUtils) {
		this.consumableRetrieveUtils = consumableRetrieveUtils;
	}

	public QuestForUserEntityManager getUserConsumableEntityManager() {
		return questForUserEntityManager;
	}

	public void setUserConsumableEntityManager(
			QuestForUserEntityManager questForUserEntityManager) {
		this.questForUserEntityManager = questForUserEntityManager;
	}

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}


}