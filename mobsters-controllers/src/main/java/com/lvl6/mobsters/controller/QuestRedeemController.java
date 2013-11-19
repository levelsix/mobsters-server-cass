package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.QuestRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemRequestProto;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemResponseProto;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemResponseProto.QuestRedeemStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.QuestRedeemRequestEvent;
import com.lvl6.mobsters.events.response.QuestRedeemResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.services.questforuser.QuestForUserService;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class QuestRedeemController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils; 
	
	@Autowired
	protected QuestForUserService questForUserService;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new QuestRedeemRequestEvent();
	}
	
	

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_QUEST_REDEEM_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		QuestRedeemRequestProto reqProto = 
				((QuestRedeemRequestEvent) event).getQuestRedeemRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
		int questId = reqProto.getQuestId();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);
		Date timeRedeemed = new Date();

		//response to send back to client
		Builder responseBuilder = QuestRedeemResponseProto.newBuilder();
		responseBuilder.setSender(senderProto);
		responseBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
		responseBuilder.setQuestId(questId);
		QuestRedeemResponseEvent resEvent = new QuestRedeemResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			QuestForUser userQuest = getQuestForUserService()
					.getSpecificUnredeemedUserQuest(userId, questId);
			Quest quest = getQuestRetrieveUtils().getQuestForId(questId);
			
			boolean legitRedeem = checkLegitRedeem(responseBuilder, userQuest, quest);
			
			boolean successful = false;
			if(legitRedeem) {
				responseBuilder.setCityIdOfRedeemedQuest(quest.getCityId());
				successful = writeChangesToDb(user, userId, questId, quest, timeRedeemed);
			}
			
			if (successful) {
				responseBuilder.setStatus(QuestRedeemStatus.SUCCESS);
			}

			//write to client
			resEvent.setQuestRedeemResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in QuestRedeemController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
				resEvent.setQuestRedeemResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in QuestRedeemController processRequestEvent", e2);
			}
		}
	}

	private boolean checkLegitRedeem(Builder resBuilder, QuestForUser userQuest, Quest quest) {
	    if (userQuest == null || userQuest.isRedeemed()) {
	      resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
	      log.error("user quest is null or redeemed already. userQuest=" + userQuest);
	      return false;
	    }
	    if (!userQuest.isComplete()) {
	      resBuilder.setStatus(QuestRedeemStatus.FAIL_NOT_COMPLETE);
	      log.error("user quest is not complete");
	      return false;
	    }
	    resBuilder.setStatus(QuestRedeemStatus.SUCCESS);
	    return true;  
	  }

	
	private boolean writeChangesToDb(User inDb, UUID userId, int questId, Quest quest,
			Date timeRedeemed) {
		try {
			getQuestForUserService().createNewUserQuestForUser(userId, questId, timeRedeemed);
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
