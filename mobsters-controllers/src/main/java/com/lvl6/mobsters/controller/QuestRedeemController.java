package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.entitymanager.staticdata.utils.QuestRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemRequestProto;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemResponseProto;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemResponseProto.QuestRedeemStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.QuestRedeemRequestEvent;
import com.lvl6.mobsters.events.response.QuestRedeemResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.questforuser.QuestForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class QuestRedeemController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils; 
	
	@Autowired
	protected QuestForUserService questForUserService;
	
	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	

	
	public QuestRedeemController() {
		numAllocatedThreads = 4;
	}	
	
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
				//calculate the available quests for this user
				setAvailableQuests(responseBuilder, userId, questId);
				
				//give user the monster reward, if any, and send this to the client
				legitRedeem = awardMonsterReward(responseBuilder, userId, quest, questId, timeRedeemed);
			}
			
			if (legitRedeem) {
				User u = getUserService().getUserWithId(userId);
				successful = writeChangesToDb(u, userId, questId, quest, userQuest,
						timeRedeemed);
			}
			
			if (successful) {
				responseBuilder.setStatus(QuestRedeemStatus.SUCCESS);
			}

			resEvent.setQuestRedeemResponseProto(responseBuilder.build());
			//write to client
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
	      log.error("user quest is null or redeemed already. userQuest=" + userQuest);
	      return false;
	    }
	    if (!userQuest.isComplete()) {
	      resBuilder.setStatus(QuestRedeemStatus.FAIL_NOT_COMPLETE);
	      log.error("user quest is not complete");
	      return false;
	    }
	    return true;  
	  }

	private void setAvailableQuests(Builder responseBuilder, UUID userId, int questIdJustRedeemed) {
		List<Integer> availableQuestIds = getQuestForUserService()
				.getAvailableQuestIdsForUser(userId, questIdJustRedeemed);
		
		if (null == availableQuestIds) {
			return;
		}
		
		List<Quest> availableQuests = getQuestForUserService().selectQuestsHavingPrerequisiteQuestId(
				availableQuestIds, questIdJustRedeemed);
		
		for (Quest q : availableQuests) {
			QuestProto qp = getCreateNoneventProtoUtils().createQuestProtoFromQuest(q);
			responseBuilder.addNewlyAvailableQuests(qp);
		}
	}
	
	private boolean awardMonsterReward(Builder resBuilder, UUID userId, Quest quest,
			int questId, Date combineStartDate) {
		boolean legitRedeem = true;
		
		int monsterIdReward = quest.getMonsterIdReward();
		if (monsterIdReward > 0) {
	    	Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
	    	monsterIdToNumPieces.put(monsterIdReward, 1);
	    	
	    	String mfusop = MobstersTableConstants.MFUSOP__QUEST + " " + questId;
	    	List<MonsterForUser> rewardList = getMonsterForUserService()
	    			.updateUserMonstersForUser(userId, monsterIdToNumPieces, mfusop, combineStartDate);
	    	List<FullUserMonsterProto> rewardProtoList = getCreateNoneventProtoUtils()
	    			.createFullUserMonsterProtoList(rewardList);
	    	
	      if (rewardProtoList.isEmpty()) {
	        resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
	        log.error("problem with giving user 1 monster after completing the quest, monsterId=" 
	            + monsterIdReward + ", quest= " + quest);
	        legitRedeem = false;
	      } else {
	      	FullUserMonsterProto fump = rewardProtoList.get(0);
	        resBuilder.setFump(fump);
	      }
		}
		
		return legitRedeem;
	}
	
	private boolean writeChangesToDb(User u, UUID userId, int questId, Quest quest,
			QuestForUser userQuest, Date timeRedeemed) {
		try {
			//complete the quest
			userQuest.setTimeRedeemed(timeRedeemed);
			getQuestForUserService().saveQuestForUser(userQuest);
			
			log.info("user before awarding quest rewards. u=" + u);
			int cashChange = quest.getCashReward();
			int gemChange = quest.getGemReward();
			int expChange = quest.getExpReward();
			
			int newExp = expChange + u.getExp();
			
			List<UserCurrencyHistory> uchList = createCurrencyHistory(u, questId,
					timeRedeemed, cashChange, gemChange);

			u.setExp(newExp);
			int oilChange = 0;
			getUserService().updateUserResources(u, gemChange, oilChange, cashChange);
				
			//keep track of currency stuff
			if (!uchList.isEmpty()) {
				getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
			}
			
			
			log.info("user after awarding quest rewards. u=" + u);
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	
	private List<UserCurrencyHistory> createCurrencyHistory(User u, int questId,
			Date timeRedeemed, int cashGained, int gemsGained) {
		String cashStr = MobstersDbTables.USER__CASH;
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__REDEEMED_QUEST;
		StringBuilder sb = new StringBuilder();
		sb.append("questId=");
		sb.append(questId);
		String details = sb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory cash = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, timeRedeemed, cashStr, cashGained,
						reasonForChange, details, saveToDb);
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, timeRedeemed, gemsStr, gemsGained,
						reasonForChange, details, saveToDb);
		
		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != cash) {
			uchList.add(cash);
		}
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
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

	public CreateNoneventProtoUtil getCreateNoneventProtoUtils() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtils(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}

	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}

	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}
}
