package com.lvl6.mobsters.controller;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.eventprotos.EventMonsterProto.RemoveMonsterFromBattleTeamRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.RemoveMonsterFromBattleTeamResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.RemoveMonsterFromBattleTeamResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.RemoveMonsterFromBattleTeamResponseProto.RemoveMonsterFromBattleTeamStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.RemoveMonsterFromBattleTeamRequestEvent;
import com.lvl6.mobsters.events.response.RemoveMonsterFromBattleTeamResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;


@Component
public class RemoveMonsterFromBattleTeamController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


	@Autowired
	protected MonsterForUserService monsterForUserService;

	@Override
	public RequestEvent createRequestEvent() {
		return new RemoveMonsterFromBattleTeamRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		RemoveMonsterFromBattleTeamRequestProto reqProto = 
				((RemoveMonsterFromBattleTeamRequestEvent) event).getRemoveMonsterFromBattleTeamRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
	    String userIdString = senderProto.getUserUuid();
	    String userMonsterIdString = reqProto.getUserMonsterUuid();


		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);
		UUID userMonsterId = UUID.fromString(userMonsterIdString);

		//response to send back to client
		Builder responseBuilder = RemoveMonsterFromBattleTeamResponseProto.newBuilder();
		responseBuilder.setStatus(RemoveMonsterFromBattleTeamStatus.FAIL_OTHER);
		RemoveMonsterFromBattleTeamResponseEvent resEvent =
				new RemoveMonsterFromBattleTeamResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			MonsterForUser mfu = getMonsterForUserService()
	    			.getSpecificUserMonster(userMonsterId);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, userId, userMonsterId, mfu);

			boolean successful = false;
			if (validRequest) {
				int teamSlotNum = 0;
				successful = writeChangesToDb(teamSlotNum, mfu);
			}

			if (successful) {
				responseBuilder.setStatus(RemoveMonsterFromBattleTeamStatus.SUCCESS);
			}

			//write to client
			resEvent.setRemoveMonsterFromBattleTeamResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RemoveMonsterFromBattleTeamController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(RemoveMonsterFromBattleTeamStatus.FAIL_OTHER);
				resEvent.setRemoveMonsterFromBattleTeamResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in RemoveMonsterFromBattleTeamController processRequestEvent", e2);
			}
		}
	}


	private boolean isValidRequest(Builder resBuilder, UUID userId,
	  		UUID userMonsterId, MonsterForUser mfu) {


	  	if (null == mfu) {
	  		log.error("no monster_for_user exists with id=" + userMonsterId);
	  		return false;
	  	}

	  	//check to make sure this is indeed the user's monster
	  	UUID mfuUserId = mfu.getUserId();
	  	if (!mfuUserId.equals(userId)) {
	  		log.error("what is this I don't even...client trying to \"unequip\" " +
	  				"another user's monster. userId=" + userId + "\t monsterForUser=" + mfu);
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

