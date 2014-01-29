package com.lvl6.mobsters.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.MonsterStuffUtil;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.UpdateMonsterHealthRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.UpdateMonsterHealthResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.UpdateMonsterHealthResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.UpdateMonsterHealthResponseProto.UpdateMonsterHealthStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.UpdateMonsterHealthRequestEvent;
import com.lvl6.mobsters.events.response.UpdateMonsterHealthResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;


@Component
public class UpdateMonsterHealthController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected MonsterStuffUtil monsterStuffUtil;

	@Autowired
	protected MonsterForUserService monsterForUserService;


	
	public UpdateMonsterHealthController() {
		numAllocatedThreads = 4;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new UpdateMonsterHealthRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_UPDATE_MONSTER_HEALTH_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		UpdateMonsterHealthRequestProto reqProto = 
				((UpdateMonsterHealthRequestEvent) event).getUpdateMonsterHealthRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getSender();
		String userIdString = sender.getUserUuid();
		List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = UpdateMonsterHealthResponseProto.newBuilder();
		responseBuilder.setStatus(UpdateMonsterHealthStatus.FAIL_OTHER);
		UpdateMonsterHealthResponseEvent resEvent =
				new UpdateMonsterHealthResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			Map<UUID, Integer> userMonsterIdToExpectedHealth = new HashMap<UUID, Integer>();
			//extract the ids so it's easier to get userMonsters from db
			List<UUID> userMonsterIds = getMonsterStuffUtils().getUserMonsterIds(umchpList,
					userMonsterIdToExpectedHealth);
			
			//get whatever we need from the database
			Map<UUID, MonsterForUser> existingUserMonsters = getMonsterForUserService()
					.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);


			//validate request
			boolean validRequest = isValidRequest(responseBuilder, existingUserMonsters,
					userMonsterIds, umchpList);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(userMonsterIdToExpectedHealth,
						existingUserMonsters);
			}

			if (successful) {
				responseBuilder.setStatus(UpdateMonsterHealthStatus.SUCCESS);
			}

			//write to client
			resEvent.setUpdateMonsterHealthResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in UpdateMonsterHealthController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(UpdateMonsterHealthStatus.FAIL_OTHER);
				resEvent.setUpdateMonsterHealthResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in UpdateMonsterHealthController processRequestEvent", e2);
			}
		}
	}
	
	private boolean isValidRequest(Builder resBuilder, Map<UUID, MonsterForUser> mfuList,
			List<UUID> userMonsterIds, List<UserMonsterCurrentHealthProto> umchpList) {
		if (null == umchpList || umchpList.isEmpty()) {
	  		log.error("client error: no user monsters sent.");
	  		return false;
	  	}

		if (null == mfuList || mfuList.isEmpty()) {
			log.error("unexpected error: userMonsterIds don't exist. ids=" + userMonsterIds);
			return false;
		}

		//see if the user has the equips
	  	if (mfuList.size() != umchpList.size()) {
	  		log.error("unexpected error: mismatch between user equips client sent and " +
	  				"what is in the db. clientUserMonsterIds=" + userMonsterIds + "\t inDb=" +
	  				mfuList + "\t continuing the processing");
	  	}
	  	
		return true;
	}

	private boolean writeChangesToDb(Map<UUID, Integer> userMonsterIdToExpectedHealth,
			Map<UUID, MonsterForUser> existingUserMonsters) {
		try {
			//replace existing health for these user monsters with new values 
			getMonsterForUserService().updateUserMonstersHealths(
					userMonsterIdToExpectedHealth, existingUserMonsters);
		  	
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	
	

	public MonsterStuffUtil getMonsterStuffUtils() {
		return monsterStuffUtil;
	}

	public void setMonsterStuffUtils(MonsterStuffUtil monsterStuffUtil) {
		this.monsterStuffUtil = monsterStuffUtil;
	}

	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}

	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}
		
}
