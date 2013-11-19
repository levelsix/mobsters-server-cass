package com.lvl6.mobsters.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.QuestRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestAcceptRequestProto;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestAcceptResponseProto;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestAcceptResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestAcceptResponseProto.QuestAcceptStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.QuestAcceptRequestEvent;
import com.lvl6.mobsters.events.response.QuestAcceptResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.services.questforuser.QuestForUserService;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class QuestAcceptController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils; 
	
	@Autowired
	protected QuestForUserService questForUserService;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new QuestAcceptRequestEvent();
	}
	
	

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_QUEST_ACCEPT_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		QuestAcceptRequestProto reqProto = 
				((QuestAcceptRequestEvent) event).getQuestAcceptRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
		int questId = reqProto.getQuestId();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);
		Date timeAccepted = new Date();

		//response to send back to client
		Builder responseBuilder = QuestAcceptResponseProto.newBuilder();
		responseBuilder.setSender(senderProto);
		responseBuilder.setStatus(QuestAcceptStatus.FAIL_OTHER);
		QuestAcceptResponseEvent resEvent = new QuestAcceptResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			String gameCenterId = null;
			User user = getUserService().retrieveUser(gameCenterId, userIdString);
			Quest quest = getQuestRetrieveUtils().getQuestForId(questId);
			
			boolean legitAccept = checkLegitAccept(responseBuilder, user,
					userId, quest, questId);
			
			boolean successful = false;
			if(legitAccept) {
				responseBuilder.setCityIdOfAcceptedQuest(quest.getCityId());
				successful = writeChangesToDb(user, userId, questId, quest, timeAccepted);
			}
			
			if (successful) {
				responseBuilder.setStatus(QuestAcceptStatus.SUCCESS);
			}

			//write to client
			resEvent.setQuestAcceptResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in QuestAcceptController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(QuestAcceptStatus.FAIL_OTHER);
				resEvent.setQuestAcceptResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in QuestAcceptController processRequestEvent", e2);
			}
		}
	}

	private boolean checkLegitAccept(Builder responseBuilder, User user, UUID userId,
			Quest quest, int questId) {
	    if (user == null || quest == null) {
	      log.error("parameter passed in is null. user=" + user + ", quest=" + quest +
	    		  "\t userId=" + userId + "\t questId=" + questId);
	      
	      return false;
	    }
	    
	    
	    List<Integer> availableQuestIds = getQuestForUserService()
	    		.getAvailableQuestIdsForUser(userId);
	    if (availableQuestIds != null && availableQuestIds.contains(quest.getId())) {
	    	responseBuilder.setStatus(QuestAcceptStatus.SUCCESS);
	    	return true;
	    } else {
	    	responseBuilder.setStatus(QuestAcceptStatus.FAIL_NOT_AVAIL_TO_USER);
	    	log.error("quest with id " + quest.getId() + " is not available to user");
	    	return false;
	    }

	}

	
	private boolean writeChangesToDb(User inDb, UUID userId, int questId, Quest quest,
			Date timeAccepted) {
		try {
			getQuestForUserService().createNewUserQuestForUser(userId, questId, timeAccepted);
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	
	
	

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public QuestRetrieveUtils getQuestRetrieveUtils() {
		return questRetrieveUtils;
	}

	public void setQuestRetrieveUtils(QuestRetrieveUtils questRetrieveUtils) {
		this.questRetrieveUtils = questRetrieveUtils;
	}

	public QuestForUserService getQuestForUserService() {
		return questForUserService;
	}

	public void setQuestForUserService(QuestForUserService questForUserService) {
		this.questForUserService = questForUserService;
	}
	
}
