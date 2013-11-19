package com.lvl6.mobsters.controller.utils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto.SpeechSegmentProto.DialogueSpeaker;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto.QuestType;
import com.lvl6.mobsters.noneventprotos.UserProto.FullUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.utils.Dialogue;

@Component
public class CreateNoneventProtoUtilsImpl implements CreateNoneventProtoUtils {

	/*//static initializing this map because didn't know how else to initialize
	private static Map<Integer, ClassType> classTypeNumToClassType =
			new HashMap<Integer, ClassType>();
    static {
    	classTypeNumToClassType.put(ClassType.ALL_VALUE, ClassType.ALL);
    	classTypeNumToClassType.put(ClassType.ARCHER_VALUE, ClassType.ARCHER);
    	classTypeNumToClassType.put(ClassType.NOOB_VALUE, ClassType.NOOB);
    	classTypeNumToClassType.put(ClassType.WARRIOR_VALUE, ClassType.WARRIOR);
    	classTypeNumToClassType.put(ClassType.WIZARD_VALUE, ClassType.WIZARD);
    }*/
	
	@Override
	public FullUserProto createFullUserProtoFromUser(User u) {
		// TODO Auto-generated method stub
		FullUserProto.Builder fupb = FullUserProto.newBuilder();
		
		String uId = u.getId().toString();
		String name = u.getName();
		int level = u.getLvl();
		int gems = u.getGems();
		int cash = u.getCash();
		int exp = u.getExp();
		int tasksCompleted = u.getTasksCompleted();
		int battlesWon = u.getBattlesWon();
		int battlesLost = u.getBattlesLost();
		int flees = u.getFlees();
		String referralCode = u.getReferralCode();
		int numReferrals = u.getNumReferrals();
		Date lastLoginDate = u.getLastLogin();
		Date lastLogoutDate = u.getLastLogout();
		boolean isFake = u.isFake();
		boolean isAdmin = u.isAdmin();
		int numCoinsRetrievedFromStructs = u.getNumCoinsRetrievedFromStructs();
		UUID clanId = u.getClanId();
		boolean hasReceivedFbReward = u.isHasReceivedFbReward();
		int numAdditionalMonsterSlots = u.getNumAdditionalMonsterSlots();
		int numBeginnerSalesPurchased = u.getNumBeginnerSalesPurchased();
		boolean hasActiveShield = u.isHasActiveShield();
		Date shieldEndTime = u.getShieldEndTime();
		int elo = u.getElo();
		String rank = u.getRank();
		Date lastTimeQueued = u.getLastTimeQueued();
		int attacksWon = u.getAttacksWon();
		int defensesWon = u.getDefensesWon();
		int attacksLost = u.getAttacksLost();
		int defensesLost = u.getDefensesLost();
		String facebookId = u.getFacebookId();
		
		
//		String gameCenterId = u.getGameCenterId();
		//Date dateCreated = u.getDateCreated();
		
		fupb.setUserUuid(uId.toString());
		fupb.setName(name);
		fupb.setLevel(level);
		fupb.setGems(gems);
		fupb.setCash(cash);
		fupb.setExperience(exp);
		fupb.setTasksCompleted(tasksCompleted);
		fupb.setBattlesWon(battlesWon);
		fupb.setBattlesLost(battlesLost);
		fupb.setFlees(flees);
		fupb.setReferralCode(referralCode);
		fupb.setNumReferrals(numReferrals);
		
		if (null != lastLoginDate) {
			fupb.setLastLoginTime(lastLoginDate.getTime());
		}
		//TODO: FINISH THE REST OF THIS
		
		return fupb.build();
	}

	@Override
	public QuestProto createQuestProtoFromQuest(Quest q) {
		QuestProto.Builder qpb = QuestProto.newBuilder();
		
		int questId = q.getId();
		int cityId = q.getCityId();
		String name = q.getName();
		String description = q.getDescription();
		String doneResponse = q.getDoneResponse();
		Dialogue acceptDialogue = q.getDialogue();
		String questType = q.getQuestType(); 
		String jobDescription = q.getJobDescription();
		int staticDataId = q.getStaticDataId();
		int quantity = q.getStaticDataQuantity();
		int cashReward = q.getCashReward();
		int gemReward = q.getGemReward();
		int expReward = q.getExpReward();
		int monsterIdReward = q.getMonsterIdReward();
		boolean isCompleteMonster = q.isCompleteMonster();
		Set<Integer> questsRequiredForThis = q.getQuestsRequiredForThisAsSet();
		String questGiverImageSuffix = q.getQuestGiverImgSuffix();
		int priority = q.getPriority();
		String carrotId = q.getCarrotId();
		
		qpb.setQuestId(questId);
		qpb.setCityId(cityId);
		
		if (null != name) {
			qpb.setName(name);
		}
		if (null != description) {
			qpb.setDescription(description);
		}
		if (null != doneResponse) {
			qpb.setDoneResponse(doneResponse);
		}
		
		DialogueProto dp = createDialogueProtoFromDialogue(acceptDialogue);
		if (null != dp) {
			qpb.setAcceptDialogue(dp);
		}
		
		if (null != questType) {
			QuestType qt = QuestType.valueOf(questType);
			if (null != qt) {
				qpb.setQuestType(qt);
			}
		}
		
		if (null != jobDescription) {
			qpb.setJobDescription(jobDescription);
		}
				
		qpb.setStaticDataId(staticDataId);
		qpb.setQuantity(quantity);
		qpb.setCashReward(cashReward);
		qpb.setGemReward(gemReward);
		qpb.setExpReward(expReward);
		qpb.setMonsterIdReward(monsterIdReward);
		qpb.setIsCompleteMonster(isCompleteMonster);
		qpb.addAllQuestIdsRequiredForThis(questsRequiredForThis);
		
		if (null != questGiverImageSuffix) {
			qpb.setQuestGiverImageSuffix(questGiverImageSuffix);
		}
		
		qpb.setPriority(priority);
		
		if (null != carrotId) {
			qpb.setCarrotId(carrotId);
		}
		
		return qpb.build();
	}
	
	@Override
	public DialogueProto createDialogueProtoFromDialogue(Dialogue d) {
		if (null == d) {
			return null;
		}
		DialogueProto.Builder dpb = DialogueProto.newBuilder();

		List<Integer> speakerIntList = d.getSpeakerInts();
		List<String> speakerTextList = d.getSpeakerTexts();
		
		for (int index = 0; index < speakerIntList.size(); index++) {
			Integer speakerInt = speakerIntList.get(index);
			String speakerText = speakerTextList.get(index);
			
			SpeechSegmentProto ssp = createSpeechSegmentProto(speakerInt, speakerText);
			dpb.addSpeechSegment(ssp);
		}
		
		return dpb.build();
	}
	
	@Override
	public SpeechSegmentProto createSpeechSegmentProto(int speakerInt, String speakerText) {
		
		SpeechSegmentProto.Builder sspb = SpeechSegmentProto.newBuilder();

		DialogueSpeaker speaker = DialogueSpeaker.valueOf(speakerInt);
		sspb.setSpeaker(speaker);
		sspb.setSpeakerText(speakerText);
		
		return sspb.build();
	}

}
