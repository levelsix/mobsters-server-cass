package com.lvl6.mobsters.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.noneventprotos.CityProto.CityElementProto;
import com.lvl6.mobsters.noneventprotos.CityProto.CityElementProto.CityElemType;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto.SpeechSegmentProto.DialogueSpeaker;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto.QuestType;
import com.lvl6.mobsters.noneventprotos.StructureProto.CoordinateProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.FullUserStructureProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.StructOrientation;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageMonsterProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageMonsterProto.MonsterType;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageProto;
import com.lvl6.mobsters.noneventprotos.UserProto.FullUserProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.mobsters.noneventprotos.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserFacebookInviteForSlot;
import com.lvl6.mobsters.po.staticdata.CityElement;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.utils.CoordinatePair;
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

	//CITY PROTO****************************************************************
	@Override
	public CityElementProto createCityElementProtoFromCityElement(CityElement ce) {
		CityElementProto.Builder builder = CityElementProto.newBuilder();
		builder.setCityId(ce.getCityId());
		builder.setAssetId(ce.getAssetId());
		
		String aStr = ce.getCityElemType();
		CityElemType cet = CityElemType.valueOf(aStr); 
		if (null != cet) {
			builder.setType(cet);
		}
		builder.setCoords(createCoordinateProtoFromCoordinatePair(ce.getCoords()));

		if (ce.getxLength() > 0) {
			builder.setXLength(ce.getxLength());
		}
		if (ce.getyLength() > 0) {
			builder.setYLength(ce.getyLength());
		}
		
		aStr = ce.getImgGood();
		if (null != aStr) {
			builder.setImgId(aStr);
		}
		
		aStr = ce.getStructOrientation();
		StructOrientation orientation = StructOrientation.valueOf(aStr);
		if (null != orientation) {
			builder.setOrientation(orientation);
		}

		builder.setSpriteCoords(createCoordinateProtoFromCoordinatePair(ce.getSpriteCoords()));

		return builder.build();
	}


	//MONSTER PROTO****************************************************************
	@Override
	public List<FullUserMonsterProto> createFullUserMonsterProtoList(
			List<MonsterForUser> userMonsters) {
		List<FullUserMonsterProto> protos = new ArrayList<FullUserMonsterProto>();
		if (null == userMonsters) {
			return protos;
		}

		for (MonsterForUser mfu : userMonsters) {
			FullUserMonsterProto ump = createFullUserMonsterProtoFromUserMonster(mfu);
			protos.add(ump);
		}

		return protos;
	}
	@Override
	public FullUserMonsterProto createFullUserMonsterProtoFromUserMonster(MonsterForUser mfu) {
		FullUserMonsterProto.Builder fumpb = FullUserMonsterProto.newBuilder();
		UUID id = mfu.getId();
		String aStr = id.toString();
		fumpb.setUserMonsterUuid(aStr);
		
		id = mfu.getUserId();
		aStr = id.toString();
		fumpb.setUserUuid(aStr);
		
		fumpb.setMonsterId(mfu.getMonsterId());
		fumpb.setCurrentExp(mfu.getCurrentExp());
		fumpb.setCurrentLvl(mfu.getCurrentLvl());
		fumpb.setCurrentHealth(mfu.getCurrentHealth());
		fumpb.setNumPieces(mfu.getNumPieces());
		fumpb.setIsComplete(mfu.isComplete());

		Date combineStartTime = mfu.getCombineStartTime();
		if (null != combineStartTime) {
			fumpb.setCombineStartTime(combineStartTime.getTime());
		}

		fumpb.setTeamSlotNum(mfu.getTeamSlotNum());
		return fumpb.build();
	}
	
	  
	

	//QUEST PROTO****************************************************************
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


	//STRUCTURE PROTO****************************************************************
	public FullUserStructureProto createFullUserStructureProtoFromUserStruct(
			StructureForUser userStruct) {
		FullUserStructureProto.Builder builder = FullUserStructureProto.newBuilder();
		UUID aUuid = userStruct.getId();
		String aStr = aUuid.toString();
		builder.setUserStructUuid(aStr);

		aUuid = userStruct.getUserId();
		aStr = aUuid.toString(); 
		builder.setUserUuid(aStr);

		builder.setStructId(userStruct.getStructureId());
		//		    builder.setLevel(userStruct.getLevel());
		builder.setFbInviteStructLvl(userStruct.getFbInviteStructLvl());
		builder.setIsComplete(userStruct.isComplete());
		builder.setCoordinates(createCoordinateProtoFromCoordinatePair(userStruct.getCoordinates()));
		
		aStr = userStruct.getStructOrientation();
		StructOrientation orientation = StructOrientation.valueOf(aStr);
		if (null != orientation) {
			builder.setOrientation(orientation);
		}
		
		if (userStruct.getPurchaseTime() != null) {
			builder.setPurchaseTime(userStruct.getPurchaseTime().getTime());
		}
		if (userStruct.getLastCollectTime() != null) {
			builder.setLastRetrieved(userStruct.getLastCollectTime().getTime());
		}
		//		    if (userStruct.getLastUpgradeTime() != null) {
		//		      builder.setLastUpgradeTime(userStruct.getLastUpgradeTime().getTime());
		//		    }
		return builder.build();
	}

	@Override
	public CoordinateProto createCoordinateProtoFromCoordinatePair(CoordinatePair cp) {
		CoordinateProto.Builder builder = CoordinateProto.newBuilder();
		builder.setX(cp.getX());
		builder.setY(cp.getY());
		return builder.build();
	}
	
	//TASK PROTO****************************************************************
	@Override
	public TaskStageProto createTaskStageProtoFromTaskStageForUser(int taskStageId,
			List<TaskStageForUser> tsfuList) {
		TaskStageProto.Builder tspb = TaskStageProto.newBuilder();
		
		tspb.setStageId(taskStageId);
		
		for (TaskStageForUser tsfu : tsfuList) {
			TaskStageMonsterProto tsmp = createTaskStageMonsterProtoFromTaskStageForUser(tsfu);
			tspb.addStageMonsters(tsmp);
		}
		return tspb.build();
	}
	
	@Override
	public TaskStageMonsterProto createTaskStageMonsterProtoFromTaskStageForUser(
			TaskStageForUser tsfu) {
		TaskStageMonsterProto.Builder tsmpb = TaskStageMonsterProto.newBuilder();
		
		int monsterId = tsfu.getMonsterId();
		tsmpb.setMonsterId(monsterId);
		
		String monsterType = tsfu.getMonsterType();
		MonsterType type = MonsterType.valueOf(monsterType);
		if (null != type) {
			tsmpb.setMonsterType(type);
		}
		
		int expReward = tsfu.getExpGained();
		tsmpb.setExpReward(expReward);
		
		int cashReward = tsfu.getCashGained();
		tsmpb.setCashReward(cashReward);
		
		boolean puzzlePieceDropped = tsfu.isMonsterPieceDropped();
		tsmpb.setPuzzlePieceDropped(puzzlePieceDropped);
		
		int lvl = tsfu.getMonsterLvl();
		tsmpb.setLevel(lvl);
		
		return tsmpb.build();
	}
	
	//USER PROTO****************************************************************
	@Override
	public FullUserProto createFullUserProtoFromUser(User u) {
		// TODO Auto-generated method stub
		FullUserProto.Builder fupb = FullUserProto.newBuilder();
		
		String aStr = u.getId().toString();
		fupb.setUserUuid(aStr);
		
		aStr = u.getName();
		if (null != aStr) {
			fupb.setName(aStr);
		}
		
		int anInt = u.getLvl();
		fupb.setLevel(anInt);
		
		anInt = u.getGems();
		fupb.setGems(anInt);
		
		anInt= u.getCash();
		fupb.setCash(anInt);
		
		anInt = u.getOil();
		fupb.setOil(anInt);
		
		anInt = u.getExp();
		fupb.setExperience(anInt);
		
		anInt = u.getTasksCompleted();
		fupb.setTasksCompleted(anInt);
		
		anInt = u.getBattlesWon();
		fupb.setBattlesWon(anInt);
		
		anInt = u.getBattlesLost();
		fupb.setBattlesLost(anInt);
		
		anInt = u.getFlees();
		fupb.setFlees(anInt);
		
		aStr = u.getReferralCode();
		if (null != aStr) {
			fupb.setReferralCode(aStr);
		}
		
		anInt = u.getNumReferrals();
		fupb.setNumReferrals(anInt);
		
		Date aDate = u.getLastLogin();
		if (null != aDate) {
			fupb.setLastLoginTime(aDate.getTime());
		}
		
		aDate = u.getLastLogout();
		if (null != aDate) {
			fupb.setLastLogoutTime(aDate.getTime());
		}
		
		boolean aBool = u.isFake();
		fupb.setIsFake(aBool);
		
		aBool = u.isAdmin();
		fupb.setIsAdmin(aBool);
		
		anInt = u.getNumCashRetrievedFromStructs();
		fupb.setNumCashRetrievedFromStructs(anInt);
		
		anInt = u.getNumOilRetrievedFromStructs();
		fupb.setNumOilRetrievedFromStructs(anInt);
		
		//TODO: make the clan proto
		UUID clanId = u.getClanId();
		
		aBool = u.isHasReceivedFbReward();
		fupb.setHasReceivedfbReward(aBool);
		
		anInt = u.getNumAdditionalMonsterSlots();
		fupb.setNumAdditionalMonsterSlots(anInt);
		
		anInt = u.getNumBeginnerSalesPurchased();
		fupb.setNumBeginnerSalesPurchased(anInt);
		
		aBool = u.isHasActiveShield();
		fupb.setHasActiveShield(aBool);
		
		
		aDate = u.getShieldEndTime();
		if (null != aDate) {
			fupb.setShieldEndTime(aDate.getTime());
		}
		
		anInt = u.getElo();
		fupb.setElo(anInt);
		
		aStr = u.getRank();
		if (null != aStr) {
			fupb.setRank(aStr);
		}
		
		aDate = u.getLastTimeQueued();
		fupb.setLastTimeQueued(aDate.getTime());
		
		anInt = u.getAttacksWon();
		fupb.setAttacksWon(anInt);
		
		anInt = u.getDefensesWon();
		fupb.setDefensesWon(anInt);
		
		anInt = u.getAttacksLost();
		fupb.setAttacksLost(anInt);
		
		anInt = u.getDefensesLost();
		fupb.setDefensesLost(anInt);
		
		aStr = u.getFacebookId();
		if (null != aStr) {
			fupb.setFacebookId(aStr);
		}
		
//		String gameCenterId = u.getGameCenterId();
		//Date dateCreated = u.getDateCreated();
		
		if (null != aDate) {
			fupb.setLastLoginTime(aDate.getTime());
		}
		//TODO: FINISH THE REST OF THIS
		
		return fupb.build();
	}
	
	@Override
	public MinimumUserProto createMinimumUserProtoFromUser(User u) {
		UUID userId = u.getId();
		String userIdStr = userId.toString();
		
		MinimumUserProto.Builder builder = MinimumUserProto.newBuilder();
		builder.setName(u.getName());
		builder.setUserUuid(userIdStr);
		//TODO: IMPLEMENT CLANS
//		if (null != u.getClanId()) {
//			Clan clan = ClanRetrieveUtils.getClanWithId(u.getClanId());
//			builder.setClan(createMinimumClanProtoFromClan(clan));
//		}
		return builder.build();
	}
	
	@Override
	public MinimumUserProtoWithFacebookId createMinimumUserProtoWithFacebookIdFromUser(User u) {
		MinimumUserProto mup = createMinimumUserProtoFromUser(u);
		MinimumUserProtoWithFacebookId.Builder b = MinimumUserProtoWithFacebookId.newBuilder();
		b.setMinUserProto(mup);
		String facebookId = u.getFacebookId();
		if (null != facebookId) {
			b.setFacebookId(facebookId);
		}

		return b.build();
	}
	
	@Override
	public UserFacebookInviteForSlotProto createUserFacebookInviteForSlotProtoFromInvite(
			UserFacebookInviteForSlot invite, User inviter, MinimumUserProtoWithFacebookId inviterProto) {
		UserFacebookInviteForSlotProto.Builder inviteProtoBuilder =
				UserFacebookInviteForSlotProto.newBuilder();
		UUID inviteId = invite.getId();
		String inviteIdStr = inviteId.toString();
		inviteProtoBuilder.setInviteUuid(inviteIdStr);

		if (null == inviterProto) {
			inviterProto = createMinimumUserProtoWithFacebookIdFromUser(inviter);

		}

		inviteProtoBuilder.setInviter(inviterProto);
		String aStr = invite.getRecipientFbId();
		if (null != aStr) {
			inviteProtoBuilder.setRecipientFacebookId(aStr);
		}

		Date d = invite.getTimeOfInvite();
		if (null != d) {
			inviteProtoBuilder.setTimeOfInvite(d.getTime());
		}

		d = invite.getTimeAccepted();
		if (null != d) {
			inviteProtoBuilder.setTimeAccepted(d.getTime());
		}

		UUID userStructId = invite.getUserStructId();
		aStr = userStructId.toString();
		if (null != aStr) {
			inviteProtoBuilder.setUserStructUuid(aStr);
		}

		int userStructFbLvl = invite.getUserStructFbLvl();
		inviteProtoBuilder.setStructFbLvl(userStructFbLvl);

		d = invite.getTimeRedeemed();
		if (null != d) {
			inviteProtoBuilder.setRedeemedTime(d.getTime());
		}

		return inviteProtoBuilder.build();
	}
}
