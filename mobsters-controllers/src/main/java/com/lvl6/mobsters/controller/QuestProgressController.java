package com.lvl6.mobsters.controller;

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
import com.lvl6.mobsters.controller.utils.MiscUtil;
import com.lvl6.mobsters.controller.utils.MonsterStuffUtil;
import com.lvl6.mobsters.entitymanager.staticdata.utils.QuestRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestProgressRequestProto;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestProgressResponseProto;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestProgressResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestProgressResponseProto.QuestProgressStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.QuestProgressRequestEvent;
import com.lvl6.mobsters.events.response.QuestProgressResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto.QuestType;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.monsterforuserdeleted.MonsterForUserDeletedService;
import com.lvl6.mobsters.services.questforuser.QuestForUserService;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class QuestProgressController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService;

	@Autowired
	protected QuestRetrieveUtils questRetrieveUtil; 

	@Autowired
	protected QuestForUserService questForUserService;

	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;

	@Autowired
	protected MonsterForUserService monsterForUserService;

	@Autowired
	protected MonsterStuffUtil monsterStuffUtil;

	@Autowired
	protected MiscUtil miscUtil;

	@Autowired
	protected MonsterForUserDeletedService monsterForUserDeletedService;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new QuestProgressRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_QUEST_PROGRESS_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		QuestProgressRequestProto reqProto = 
				((QuestProgressRequestEvent) event).getQuestProgressRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
		int questId = reqProto.getQuestId();
		int currentProgress = reqProto.getCurrentProgress();
		boolean isComplete = reqProto.getIsComplete();
		//at the moment used for donate monster quests
		List<String> deleteUserMonsterIdStrings = reqProto.getDeleteUserMonsterUuidsList();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);
		List<UUID> deleteUserMonsterIds = getMiscUtil().createUUIDListFromStrings(deleteUserMonsterIdStrings);
		Date timeCompleted = new Date();

		//response to send back to client
		Builder responseBuilder = QuestProgressResponseProto.newBuilder();
		responseBuilder.setSender(senderProto);
		responseBuilder.setStatus(QuestProgressStatus.FAIL_OTHER);
		QuestProgressResponseEvent resEvent = new QuestProgressResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			Quest quest = getQuestRetrieveUtil().getQuestForId(questId);
			QuestForUser questForUser = getQuestForUserService().getSpecificUnredeemedUserQuest(userId, questId);
			Map<UUID, MonsterForUser> deletedUserMonsters = getMonsterForUserService().getSpecificOrAllUserMonstersForUser(userId, deleteUserMonsterIds);

			boolean legitProgress = checkLegitProgress(responseBuilder, userId, currentProgress, 
					questId, quest, questForUser, deleteUserMonsterIds, deletedUserMonsters);

			if (legitProgress) {
				writeChangesToDB(questForUser, currentProgress, isComplete, timeCompleted, deleteUserMonsterIds);
			}

			//write to client
			resEvent.setQuestProgressResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

			if (legitProgress) {
				// write to monster deleted history
				writeChangesToHistory(userId, questId, timeCompleted, deletedUserMonsters);
			}

		} catch (Exception e) {
			log.error("exception in QuestAcceptController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(QuestProgressStatus.FAIL_OTHER);
				resEvent.setQuestProgressResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in QuestAcceptController processRequestEvent", e2);
			}
		}
	}


	private boolean checkLegitProgress(Builder resBuilder, UUID userId,
			int newProgress, int questId, Quest quest,
			QuestForUser questForUser, List<UUID> deleteUserMonsterIds, 
			Map<UUID, MonsterForUser> deletedUserMonsters) {
		//make sure the quest, relating to the user_quest updated, exists
		if (quest == null) {
			log.error("parameter passed in is null.  questid =" + questId);
			resBuilder.setStatus(QuestProgressStatus.FAIL_NO_QUEST_EXISTS);
			return false;
		}

		int questMaxProgress = quest.getStaticDataQuantity();
		if (newProgress > questMaxProgress) {
			log.warn("client is trying to set user_quest past the max progress. quest=" +
					quest + "\t ");
		}

		//CHECK TO MAKE SURE THAT THE USER HAS THIS QUEST
		if (questForUser == null) {
			log.error("user trying to update progress for nonexisting user_quest. " +
					"progress=" + newProgress + "\t quest=" + quest);
			return false;
		}

		//if user wants to delete some monsters, make sure it's the right amount
		if (null != deleteUserMonsterIds && !deleteUserMonsterIds.isEmpty()) {
			//user shouldn't delete user monsters when the quest isn't a donate quest
			if (QuestType.valueOf(quest.getQuestType()) != QuestType.DONATE_MONSTER) {
				log.error("user trying to delete user monsters for a non donate monster quest." +
						" quest=" + quest + "\t deleteUserMonsterIds=" + deleteUserMonsterIds);
				resBuilder.setStatus(QuestProgressStatus.FAIL_OTHER);
				return false;
			}

			int deleteSize = deleteUserMonsterIds.size();
			//make sure that length of ids to delete = the amount required by the quest
			if (questMaxProgress != deleteSize) {
				log.error("amount of user monster ids being deleted does not match quest." +
						" questAmount=" + questMaxProgress + "\t deleteAmount=" + deleteSize +
						"\t quest=" + quest + "\t");
				resBuilder.setStatus(QuestProgressStatus.FAIL_DELETE_AMOUNT_DOES_NOT_MATCH_QUEST);
				return false;
			}

			//make sure the deleted user monster ids exist
			int existingSize = deletedUserMonsters.size();
			if (deleteSize != existingSize) {
				log.error("user trying to delete some nonexisting user_monsters. deleteIds=" +
						deleteUserMonsterIds + "\t existing user_monsters=" + deletedUserMonsters);
				resBuilder.setStatus(QuestProgressStatus.FAIL_NONEXISTENT_USER_MONSTERS);
				return false;
			}

			//make sure the monsters are all complete
			for (UUID deleteId : deleteUserMonsterIds) {
				//this assumes all the deleted user monster ids are retrieved from db
				MonsterForUser mfu = deletedUserMonsters.get(deleteId);
				if (mfu.isComplete()) {
					continue;
				}
				//user trying to delete incomplete user monster
				log.error("user trying to delete incomplete user monster. userMonster=" +
						mfu + "\t quest=" + quest);
				return false;
			}
		}

		resBuilder.setStatus(QuestProgressStatus.SUCCESS);
		return true;
	}

	private void writeChangesToDB(QuestForUser questForUser, 
			int currentProgress, boolean isComplete, Date timeCompleted, 
			List<UUID> deleteUserMonsterIds) {
		//if userQuest's progress reached the progress specified in quest then
		//also set userQuest.isComplete = true;

		questForUser.setProgress(currentProgress);

		if (isComplete) {
			questForUser.setComplete(isComplete);
			questForUser.setTimeCompleted(timeCompleted);
		}

		getQuestForUserService().saveQuestForUser(questForUser);

		getMonsterForUserService().deleteUserMonsters(deleteUserMonsterIds);
	}

	private void writeChangesToHistory(UUID userId, int questId,  Date deleteDate,
			Map<UUID, MonsterForUser> idsToUserMonsters) {

		if (null == idsToUserMonsters || idsToUserMonsters.isEmpty()) {
			return;
		}
		String deleteReason = MobstersTableConstants.MFUDR__QUEST;
		String detailStr = "questId=" + questId;

		Map<UUID, String> details = new HashMap<UUID, String>();

		for (UUID mfuId : idsToUserMonsters.keySet()) {
			details.put(mfuId, detailStr);
		}

		getMonsterForUserDeletedService().createUserMonsterDeletedFromUserMonsters(
				deleteReason, details, deleteDate, idsToUserMonsters);
		

		log.info("user monsters deleted for questId=" + questId + ". ids=" + details.keySet());
	}




	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public QuestForUserService getQuestForUserService() {
		return questForUserService;
	}

	public void setQuestForUserService(QuestForUserService questForUserService) {
		this.questForUserService = questForUserService;
	}

	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}

	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

	public MiscUtil getMiscUtil() {
		return miscUtil;
	}

	public void setMiscUtil(MiscUtil miscUtil) {
		this.miscUtil = miscUtil;
	}

	public CreateNoneventProtoUtil getCreateNoneventProtoUtil() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtil(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}

	public MonsterStuffUtil getMonsterStuffUtil() {
		return monsterStuffUtil;
	}

	public void setMonsterStuffUtil(MonsterStuffUtil monsterStuffUtil) {
		this.monsterStuffUtil = monsterStuffUtil;
	}

	public QuestRetrieveUtils getQuestRetrieveUtil() {
		return questRetrieveUtil;
	}

	public void setQuestRetrieveUtil(QuestRetrieveUtils questRetrieveUtil) {
		this.questRetrieveUtil = questRetrieveUtil;
	}

	public MonsterForUserDeletedService getMonsterForUserDeletedService() {
		return monsterForUserDeletedService;
	}

	public void setMonsterForUserDeletedService(
			MonsterForUserDeletedService monsterForUserDeletedService) {
		this.monsterForUserDeletedService = monsterForUserDeletedService;
	}

}
