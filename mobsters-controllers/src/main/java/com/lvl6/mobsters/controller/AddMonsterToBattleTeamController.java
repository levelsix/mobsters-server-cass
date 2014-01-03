package com.lvl6.mobsters.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.eventprotos.EventMonsterProto.AddMonsterToBattleTeamRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AddMonsterToBattleTeamResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AddMonsterToBattleTeamResponseProto.AddMonsterToBattleTeamStatus;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AddMonsterToBattleTeamResponseProto.Builder;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.AddMonsterToBattleTeamRequestEvent;
import com.lvl6.mobsters.events.response.AddMonsterToBattleTeamResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;


@Component
public class AddMonsterToBattleTeamController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


	@Autowired
	protected MonsterForUserService monsterForUserService;


	@Override
	public RequestEvent createRequestEvent() {
		return new AddMonsterToBattleTeamRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_ADD_MONSTER_TO_BATTLE_TEAM_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		AddMonsterToBattleTeamRequestProto reqProto = 
				((AddMonsterToBattleTeamRequestEvent) event).getAddMonsterToBattleTeamRequestProto();

		//get values sent from the client (the request proto)
	    MinimumUserProto senderProto = reqProto.getSender();
	    String userIdString = senderProto.getUserUuid();
	    int teamSlotNum = reqProto.getTeamSlotNum();
	    String userMonsterIdString = reqProto.getUserMonsterUuid();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);
		UUID userMonsterId = UUID.fromString(userMonsterIdString);

		//response to send back to client
		Builder responseBuilder = AddMonsterToBattleTeamResponseProto.newBuilder();
		responseBuilder.setStatus(AddMonsterToBattleTeamStatus.FAIL_OTHER);
		AddMonsterToBattleTeamResponseEvent resEvent =
				new AddMonsterToBattleTeamResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			//getting all because want to get the monster with team slot: teamSlotNum
			//and want to get the monster with mfuId: userMonsterId
			Map<UUID, MonsterForUser> idsToMonsters = getMonsterForUserService()
	    			.getSpecificOrAllUserMonstersForUser(userId, null);
//	    	//get the ones that aren't in enhancing or healing
//	    	Map<UUID, MonsterEnhancingForUser> inEnhancing =
//	    			MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
//	    	Map<UUID, MonsterHealingForUser> inHealing =
//	    			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
	    	Map<UUID, MonsterEnhancingForUser> inEnhancing =
	    			new HashMap<UUID, MonsterEnhancingForUser>();
	    	Map<UUID, MonsterHealingForUser> inHealing =
	    			new HashMap<UUID, MonsterHealingForUser>();
			

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, userId, teamSlotNum,
					userMonsterId, idsToMonsters, inEnhancing, inHealing);

			boolean successful = false;
			if (validRequest) {
				MonsterForUser mfu = idsToMonsters.get(userMonsterId);
				successful = writeChangesToDb(teamSlotNum, mfu);
			}

			if (successful) {
				responseBuilder.setStatus(AddMonsterToBattleTeamStatus.SUCCESS);
			}

			//write to client
			resEvent.setAddMonsterToBattleTeamResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in AddMonsterToBattleTeamController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(AddMonsterToBattleTeamStatus.FAIL_OTHER);
				resEvent.setAddMonsterToBattleTeamResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in AddMonsterToBattleTeamController processRequestEvent", e2);
			}
		}
	}

	private boolean isValidRequest(Builder resBuilder, UUID userId, int teamSlotNum,
	  		UUID userMonsterId, Map<UUID, MonsterForUser> idsToMonsters,
	  		Map<UUID, MonsterEnhancingForUser> inEnhancing, 
			  Map<UUID, MonsterHealingForUser> inHealing) {
		if (!idsToMonsters.containsKey(userMonsterId)) {
	  		log.error("no monster_for_user exists with id=" + userMonsterId +
	  				" and userId=" + userId);
	  		return false;
	  	}

	  	//if a monster is already occupying the slot, remove him from said slot
	  	int newTeamSlotNum = 0;
	  	List<MonsterForUser> modifiedMfuList = getMonsterForUserService()
	  			.replaceBattleTeamSlot(teamSlotNum, newTeamSlotNum, idsToMonsters);
	  	if (!modifiedMfuList.isEmpty()) {
	  		getMonsterForUserService().saveUserMonsters(modifiedMfuList);
	  	}
	  	
	  	MonsterForUser mfu = idsToMonsters.get(userMonsterId);
	  	
	  	//CHECK TO MAKE SURE THE USER MONSTER IS COMPLETE
	  	if (!mfu.isComplete()) {
	  		log.error("user error: user trying to equip incomplete monster. userId=" +
	  				userId + "\t monsterForUser=" + mfu);
	  		return false;
	  	}

	  	//inEnhancing has userMonsterId values for keys
	  	//NOT IN ENHANCING
	  	if (inEnhancing.containsKey(userMonsterId)) {
	  		log.error("user error: user is trying to \"equip\" a monster that is in" +
	  				" enhancing." + "\t userId=" + userId + "\t monsterForUser=" + mfu +
	  				" inEnhancing=" + inEnhancing);
	  		return false;
	  	}

	  	//inHealing has userMonsterId values for keys
	  	//NOT IN HEALING 
	  	if (inHealing.containsKey(userMonsterId)) {
	  		log.error("user error: user is trying to \"equip\" a monster that is in" +
	  				" healing." + "\t userId=" + userId + "\t monsterForUser=" + mfu +
	  				" inHealing=" + inHealing);
	  		return false;
	  	}

		return true;
	}


	private boolean writeChangesToDb(int teamSlotNum, MonsterForUser mfu) {
		try {
			getMonsterForUserService().updateBattleTeamSlot(teamSlotNum, mfu);
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}

	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

}
