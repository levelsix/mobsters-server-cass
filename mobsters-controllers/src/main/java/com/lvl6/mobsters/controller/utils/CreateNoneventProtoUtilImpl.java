package com.lvl6.mobsters.controller.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.noneventprotos.BoosterPackStuffProto.BoosterDisplayItemProto;
import com.lvl6.mobsters.noneventprotos.BoosterPackStuffProto.BoosterItemProto;
import com.lvl6.mobsters.noneventprotos.BoosterPackStuffProto.BoosterPackProto;
import com.lvl6.mobsters.noneventprotos.CityProto.CityElementProto;
import com.lvl6.mobsters.noneventprotos.CityProto.CityElementProto.CityElemType;
import com.lvl6.mobsters.noneventprotos.CityProto.CityExpansionCostProto;
import com.lvl6.mobsters.noneventprotos.CityProto.FullCityProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.MonsterLevelInfoProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.MonsterProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.MonsterProto.MonsterElement;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.MonsterProto.MonsterQuality;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserEnhancementProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto.SpeechSegmentProto.DialogueSpeaker;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.FullUserQuestProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto.QuestType;
import com.lvl6.mobsters.noneventprotos.StructureProto.CoordinateProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.FullUserStructureProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.HospitalProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.LabProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResidenceProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceGeneratorProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceStorageProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceType;
import com.lvl6.mobsters.noneventprotos.StructureProto.StructOrientation;
import com.lvl6.mobsters.noneventprotos.StructureProto.StructureInfoProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.StructureInfoProto.StructType;
import com.lvl6.mobsters.noneventprotos.StructureProto.TownHallProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.FullTaskProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.PersistentEventProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.PersistentEventProto.DayOfWeek;
import com.lvl6.mobsters.noneventprotos.TaskProto.PersistentEventProto.EventType;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageMonsterProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageMonsterProto.MonsterType;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageProto;
import com.lvl6.mobsters.noneventprotos.UserProto.FullUserProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.mobsters.noneventprotos.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserFacebookInviteForSlot;
import com.lvl6.mobsters.po.staticdata.BoosterDisplayItem;
import com.lvl6.mobsters.po.staticdata.BoosterItem;
import com.lvl6.mobsters.po.staticdata.BoosterPack;
import com.lvl6.mobsters.po.staticdata.City;
import com.lvl6.mobsters.po.staticdata.CityElement;
import com.lvl6.mobsters.po.staticdata.EventPersistent;
import com.lvl6.mobsters.po.staticdata.ExpansionCost;
import com.lvl6.mobsters.po.staticdata.Monster;
import com.lvl6.mobsters.po.staticdata.MonsterLevelInfo;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.po.staticdata.StructureHospital;
import com.lvl6.mobsters.po.staticdata.StructureLab;
import com.lvl6.mobsters.po.staticdata.StructureResidence;
import com.lvl6.mobsters.po.staticdata.StructureResourceGenerator;
import com.lvl6.mobsters.po.staticdata.StructureResourceStorage;
import com.lvl6.mobsters.po.staticdata.StructureTownHall;
import com.lvl6.mobsters.po.staticdata.Task;
import com.lvl6.mobsters.utils.CoordinatePair;
import com.lvl6.mobsters.utils.Dialogue;

@Component
public class CreateNoneventProtoUtilImpl implements CreateNoneventProtoUtil {

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
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	//BOOSTER PACK PROTO****************************************************************
	@Override
	public BoosterItemProto createBoosterItemProto(BoosterItem bi) {
		BoosterItemProto.Builder b = BoosterItemProto.newBuilder();
		b.setBoosterItemId(bi.getId());
		b.setBoosterPackId(bi.getBoosterPackId());
		b.setMonsterId(bi.getMonsterId());
		b.setNumPieces(bi.getNumPieces());
		b.setIsComplete(bi.isComplete());
		b.setIsSpecial(bi.isSpecial());
		b.setGemReward(bi.getGemReward());
		b.setCashReward(bi.getCashReward());
		b.setChanceToAppear(bi.getChanceToAppear());
		return b.build();
	}
	
	@Override
	public BoosterDisplayItemProto createBoosterDisplayItemProto(
	  		BoosterDisplayItem bdi) {
		BoosterDisplayItemProto.Builder b = BoosterDisplayItemProto.newBuilder();

		b.setBoosterPackId(bdi.getBoosterPackId());
		b.setIsMonster(bdi.isMonster());
		b.setIsComplete(bdi.isComplete());

		String monsterQuality = bdi.getMonsterQuality();
		MonsterQuality mq = MonsterQuality.valueOf(monsterQuality);
		if (null != mq) {
			b.setQuality(mq);
		}

		b.setGemReward(bdi.getGemReward());
		b.setQuantity(bdi.getQuantity());

		return b.build();
	}


	
	@Override
	public BoosterPackProto createBoosterPackProto(BoosterPack bp,
	  		Collection<BoosterItem> biList, Collection<BoosterDisplayItem> bdiList) {
		BoosterPackProto.Builder b = BoosterPackProto.newBuilder();
		b.setBoosterPackId(bp.getId());

		String str = bp.getName();
		if (null != str && !str.isEmpty()) {
			b.setBoosterPackName(str);
		}

		b.setGemPrice(bp.getGemPrice());

		str = bp.getListBgImgName();
		if (null != str && !str.isEmpty()) {
			b.setListBackgroundImgName(str);
		}

		str = bp.getListDescription();
		if (null != str && !str.isEmpty()) {
			b.setListDescription(str);
		}

		str = bp.getNavBarImgName();
		if (null != str && !str.isEmpty()) {
			b.setNavBarImgName(str);
		}

		str = bp.getNavTitleImgName();
		if (null != str && !str.isEmpty()) {
			b.setNavTitleImgName(str);
		}

		str = bp.getMachineImgName();
		if (null != str && !str.isEmpty()) {
			b.setMachineImgName(str);
		}


		if (biList != null) {
			for(BoosterItem bi : biList) {
				//only want special booster items
				if (bi.isSpecial()) {
					BoosterItemProto bip = createBoosterItemProto(bi); 
					b.addSpecialItems(bip);
				}
			}
		}

		if (null != bdiList) {
			for (BoosterDisplayItem bdi : bdiList) {
				BoosterDisplayItemProto bdip = createBoosterDisplayItemProto(bdi);
				b.addDisplayItems(bdip);
			}
		}

		return b.build();
	}
	
	

	//CITY PROTO****************************************************************
	@Override
	public CityExpansionCostProto createCityExpansionCostProtoFromCityExpansionCost(
			ExpansionCost ec) {
		CityExpansionCostProto.Builder builder = CityExpansionCostProto.newBuilder();
		builder.setExpansionNum(ec.getId());
		builder.setExpansionCostCash(ec.getExpansionCostCash());
		builder.setNumMinutesToExpand(ec.getNumMinutesToExpand());
		return builder.build();
	}
	
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

	@Override
	public FullCityProto createFullCityProtoFromCity(City c, List<Task> allTasksForCity) {
		FullCityProto.Builder builder = FullCityProto.newBuilder();
		builder.setCityId(c.getId());
		builder.setName(c.getName());
		builder.setMapImgName(c.getMapImgName());
		CoordinatePair center = c.getCenterCoords();
		builder.setCenter(createCoordinateProtoFromCoordinatePair(center));
		
		if (allTasksForCity != null) {
			for (Task t : allTasksForCity) {
				builder.addTaskIds(t.getId());
			}
		}

		String roadImgName = c.getRoadImgName();
		if (null != roadImgName) {
			builder.setRoadImgName(roadImgName);
		}

		String mapTmxName = c.getMapTmxName();
		if (null != mapTmxName) {
			builder.setMapTmxName(mapTmxName);
		}

		builder.setRoadImgCoords(createCoordinateProtoFromCoordinatePair(c.getRoadImgCoords()));
		String atkMapLabelImgName = c.getAttackMapLabelImgName();
		if (null != atkMapLabelImgName) {
			builder.setAttackMapLabelImgName(c.getAttackMapLabelImgName());
		}

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
	
	@Override
	public UserMonsterHealingProto createUserMonsterHealingProtoFromObj(
			MonsterHealingForUser mhfu) {
		UserMonsterHealingProto.Builder umhpb = UserMonsterHealingProto.newBuilder();
		UUID aUid = mhfu.getUserId();
		String aUidStr = aUid.toString();
		umhpb.setUserUuid(aUidStr);
		
		aUid = mhfu.getMonsterForUserId();
		aUidStr = aUid.toString();
		umhpb.setUserMonsterUuid(aUidStr);

		Date aDate = mhfu.getQueuedTime();
		if (null != aDate) {
			umhpb.setQueuedTimeMillis(aDate.getTime());
		}

//		aUid = mhfu.getUserStructHospitalId();
//		aUidStr = aUid.toString();
//		umhpb.setUserHospitalStructUuid(aUidStr);
		umhpb.setHealthProgress(mhfu.getHealthProgress());
		umhpb.setPriority(mhfu.getPriority());

		//		  	aDate = mhfu.getQueuedTime();
		//		  	if (null != aDate) {
		//		  		umhpb.setQueuedTimeMillis(aDate.getTime());
		//		  	}

		return umhpb.build();
	}
	
	@Override
	public UserEnhancementProto createUserEnhancementProtoFromObj(
			UUID userId, UserEnhancementItemProto baseMonster, List<UserEnhancementItemProto> feeders) {

		UserEnhancementProto.Builder uepb = UserEnhancementProto.newBuilder();

		String userIdStr = userId.toString();
		uepb.setUserUuid(userIdStr);
		uepb.setBaseMonster(baseMonster);
		uepb.addAllFeeders(feeders);

		return uepb.build();
	}

	@Override
	public UserEnhancementItemProto createUserEnhancementItemProtoFromObj(
			MonsterEnhancingForUser mefu) {

		UserEnhancementItemProto.Builder ueipb = UserEnhancementItemProto.newBuilder();
		UUID mfuId = mefu.getMonsterForUserId();
		String mfuIdStr = mfuId.toString();
		ueipb.setUserMonsterUuid(mfuIdStr);

		Date startTime = mefu.getExpectedStartTime();
		if (null != startTime) {
			ueipb.setExpectedStartTimeMillis(startTime.getTime());
		}
		
		ueipb.setEnhancingCost(mefu.getEnhancingCost());

		return ueipb.build();
	}
	
	@Override
	public MonsterProto createMonsterProto(Monster aMonster,
			Map<Integer, MonsterLevelInfo> levelToInfo) {
		MonsterProto.Builder mpb = MonsterProto.newBuilder();
		mpb.setMonsterId(aMonster.getId());
		
		String aStr = aMonster.getName();
		if (null != aStr) {
			mpb.setName(aStr);
		}
		String mGroup = aMonster.getMonsterGroup();
		if (null != mGroup) {
			mpb.setMonsterGroup(mGroup);
		}
		
		aStr = aMonster.getMonsterQuality();
		try {
			MonsterQuality mq = MonsterQuality.valueOf(aStr);
			mpb.setQuality(mq);
		} catch (Exception e) {
			log.error("monster quality incorrectly set. monster=" + aMonster);
		}
		
		mpb.setEvolutionLevel(aMonster.getEvolutionLvl());
		aStr = aMonster.getDisplayName(); 
		if (null != aStr) {
			mpb.setDisplayName(aStr);
		}

		aStr = aMonster.getMonsterElement();
		try {
			MonsterElement me = MonsterElement.valueOf(aStr);
			mpb.setElement(me);
		} catch (Exception e) {
			log.error("monster element incorrectly set. monster=" + aMonster);
		}
		aStr = aMonster.getImagePrefix(); 
		if (null != aStr) {
			mpb.setImagePrefix(aStr);
		}
		mpb.setNumPuzzlePieces(aMonster.getNumPuzzlePieces());
		mpb.setMinutesToCombinePieces(aMonster.getMinutesToCombinePieces());
		mpb.setMaxLevel(aMonster.getMaxLvl());

		int evolId = aMonster.getEvolutionMonsterId();
		if (evolId > 0) {
			mpb.setEvolutionMonsterId(evolId);
		}
		String carrot = aMonster.getCarrotRecruited();
		if (null != carrot) {
			mpb.setCarrotRecruited(carrot);
		}
		carrot = aMonster.getCarrotDefeated();
		if (null != carrot) {
			mpb.setCarrotDefeated(carrot);
		}
		carrot = aMonster.getCarrotEvolved();
		if (null != carrot) {
			mpb.setCarrotEvolved(carrot);
		}
		String description = aMonster.getDescription();
		if (null != description) {
			mpb.setDescription(description);
		}
		
		int evolutionCatalystMonsterId = aMonster.getEvolutionCatalystMonsterId();
	    mpb.setEvolutionCatalystMonsterId(evolutionCatalystMonsterId);
	    int minutesToEvolve = aMonster.getMinutesToEvolve();
	    mpb.setMinutesToEvolve(minutesToEvolve);
	    
	    int num = aMonster.getNumCatalystsRequired();
	    mpb.setNumCatalystMonstersRequired(num);
	    
	    List<MonsterLevelInfoProto> lvlInfoProtos = createMonsterLevelInfoFromInfo(levelToInfo);
	    mpb.addAllLvlInfo(lvlInfoProtos);
	    
	    int evolutionCost = aMonster.getEvolutionCost();
	    mpb.setEvolutionCost(evolutionCost);
	    
		return mpb.build();
	}
	
	public List<MonsterLevelInfoProto> createMonsterLevelInfoFromInfo(
			Map<Integer, MonsterLevelInfo> lvlToInfo) {

		if (null == lvlToInfo || lvlToInfo.isEmpty()) {
			return new ArrayList<MonsterLevelInfoProto>();
		}

		//order the MonsterLevelInfoProto by ascending lvl
		Set<Integer> lvls = lvlToInfo.keySet();
		List<Integer> ascendingLvls = new ArrayList<Integer>(lvls);
		Collections.sort(ascendingLvls);

		List<MonsterLevelInfoProto> lvlInfoProtos = new ArrayList<MonsterLevelInfoProto>();
		for (Integer lvl : ascendingLvls) {
			MonsterLevelInfo info = lvlToInfo.get(lvl);

			//create the proto
			MonsterLevelInfoProto.Builder mlipb = MonsterLevelInfoProto.newBuilder();
			mlipb.setLvl(lvl);
			mlipb.setHp(info.getHp());
			mlipb.setCurLvlRequiredExp(info.getCurLvlRequiredExp());
			mlipb.setFeederExp(info.getFeederExp());
			mlipb.setFireDmg(info.getFireDmg());
			mlipb.setGrassDmg(info.getGrassDmg());
			mlipb.setWaterDmg(info.getWaterDmg());
			mlipb.setLightningDmg(info.getLightningDmg());
			mlipb.setDarknessDmg(info.getDarknessDmg());
			mlipb.setRockDmg(info.getRockDmg());

			lvlInfoProtos.add(mlipb.build());
		}

		return lvlInfoProtos;
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
	
	//second argument, the map, is not needed
	@Override
	public List<FullUserQuestProto> createFullUserQuestProtos(
			List<QuestForUser> userQuests, Map<Integer, Quest> questIdsToQuests) {
		List<FullUserQuestProto> userQuestProtos = new ArrayList<FullUserQuestProto>();

		for (QuestForUser userQuest : userQuests) {
			Quest quest = questIdsToQuests.get(userQuest.getQuestId());
			FullUserQuestProto.Builder builder = FullUserQuestProto.newBuilder();

			if (quest != null) {
				UUID userId = userQuest.getUserId();
				builder.setUserUuid(userId.toString());
				builder.setQuestId(quest.getId());
				builder.setIsRedeemed(userQuest.isRedeemed());
				builder.setIsComplete(userQuest.isComplete());
				builder.setProgress(userQuest.getProgress());
				userQuestProtos.add(builder.build());

			} else {
				log.error("no quest with id " + userQuest.getQuestId() + ", userQuest=" + userQuest);
			}
		}
		return userQuestProtos;
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
		
		try {
			aStr = userStruct.getStructOrientation();
			StructOrientation orientation = StructOrientation.valueOf(aStr);
			if (null != orientation) {
				builder.setOrientation(orientation);
			}
		} catch (Exception e) {
			log.error("user struct orientation incorrectly set. userStruct=" + userStruct);
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
	
	@Override
	public StructureInfoProto createStructureInfoProtoFromStructure(Structure s) {
		StructureInfoProto.Builder builder = StructureInfoProto.newBuilder();
		builder.setStructId(s.getId());

		String aStr = s.getName();
		if (null != aStr) {
			builder.setName(s.getName());
		}

		builder.setLevel(s.getLvl());
		aStr = s.getStructType();
		StructType st = StructType.valueOf(aStr);
		if (null != st) {
			builder.setStructType(st);
		} else {
			log.error("can't create enum type. structType=" + aStr + ".\t structure=" + s);
		}

		aStr = s.getBuildResourceType();
		try {
			ResourceType rt = ResourceType.valueOf(aStr);
			builder.setBuildResourceType(rt);
		} catch (Exception e) {
			log.error("can't create enum type. resourceType=" + aStr + ". structure=" + s);
		}

		builder.setBuildCost(s.getBuildCost());
		builder.setMinutesToBuild(s.getBuildTimeMinutes());
		builder.setPrerequisiteTownHallLvl(s.getPrerequisiteTownHallLvl());
		builder.setWidth(s.getWidth());
		builder.setHeight(s.getHeight());

		if (s.getPredecessorStructId() > 0) {
			builder.setPredecessorStructId(s.getPredecessorStructId());
		}
		if (s.getSuccessorStructId() > 0) {
			builder.setSuccessorStructId(s.getSuccessorStructId());
		}

		aStr = s.getImgName();
		if (null != aStr) {
			builder.setImgName(aStr);
		}

		builder.setImgVerticalPixelOffset(s.getImgVerticalPixelOffset());

		aStr = s.getDescription();
		if (null != aStr) {
			builder.setDescription(aStr);
		}

		aStr = s.getShortDescription();
		if (null != aStr) {
			builder.setShortDescription(aStr);
		}

		return builder.build();
	}
	
	@Override
	public ResourceGeneratorProto createResourceGeneratorProto(Structure s,
	  		StructureInfoProto sip, StructureResourceGenerator srg) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		ResourceGeneratorProto.Builder rgpb = ResourceGeneratorProto.newBuilder();
		rgpb.setStructInfo(sip);

		String aStr = srg.getResourceType();
		ResourceType rt = ResourceType.valueOf(aStr);
		if (null != rt) {
			rgpb.setResourceType(rt);
		} else {
			log.error("can't create enum type. resourceType=" + aStr +
					". resourceGenerator=" + srg);
		}

		rgpb.setProductionRate(srg.getProductionRate());
		rgpb.setCapacity(srg.getCapacity());

		return rgpb.build();
	}
	
	@Override
	public ResourceStorageProto createResourceStorageProto(Structure s,
	  		StructureInfoProto sip,  StructureResourceStorage srs) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		ResourceStorageProto.Builder rspb = ResourceStorageProto.newBuilder();
		rspb.setStructInfo(sip);

		String aStr = srs.getResourceType();
		ResourceType rt = ResourceType.valueOf(aStr);
		if (null != rt) {
			rspb.setResourceType(rt);
		} else {
			log.error("can't create enum type. resourceType=" + aStr +
					". resourceStorage=" + srs);
		}
		rspb.setCapacity(srs.getCapacity());

		return rspb.build();
	}
	
	@Override
	public HospitalProto createHospitalProto(Structure s, StructureInfoProto sip,
	  		StructureHospital sh) {
		if (null == sip) {
	  		sip = createStructureInfoProtoFromStructure(s);
	  	}
	  	
	  	HospitalProto.Builder hpb = HospitalProto.newBuilder();
	  	hpb.setStructInfo(sip);
	  	hpb.setQueueSize(sh.getQueueSize());
	  	hpb.setHealthPerSecond(sh.getHealthPerSecond());
	  	
	  	return hpb.build();
	}
	
	@Override
	public ResidenceProto createResidenceProto(Structure s, StructureInfoProto sip,
	  		StructureResidence sr) {
		if (null == sip) {
	  		sip = createStructureInfoProtoFromStructure(s);
	  	}
	  	
	  	ResidenceProto.Builder rpb = ResidenceProto.newBuilder();
	  	rpb.setStructInfo(sip);
	  	rpb.setNumMonsterSlots(sr.getNumMonsterSlots());
	  	rpb.setNumBonusMonsterSlots(sr.getNumBonusMonsterSlots());
	  	rpb.setNumGemsRequired(sr.getNumGemsRequired());
	  	rpb.setNumAcceptedFbInvites(sr.getNumAcceptedFbInvites());
	  	String str = sr.getOccupationName();
	  	if (null != str) {
	  		rpb.setOccupationName(str);
	  	}
	  	return rpb.build();
	}
	
	@Override
	public TownHallProto createTownHallProto(Structure s, StructureInfoProto sip,
	  		StructureTownHall sth) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		TownHallProto.Builder thpb = TownHallProto.newBuilder();
		thpb.setStructInfo(sip);
		thpb.setNumResourceOneGenerators(sth.getNumResourceOneGenerators());
		thpb.setNumResourceOneStorages(sth.getNumResourceOneStorages());
		thpb.setNumResourceTwoGenerators(sth.getNumResourceTwoGenerators());
		thpb.setNumResourceTwoStorages(sth.getNumResourceTwoStorages());
		thpb.setNumHospitals(sth.getNumHospitals());
		thpb.setNumResidences(sth.getNumResidences());
		thpb.setNumMonsterSlots(sth.getNumMonsterSlots());
		thpb.setNumLabs(sth.getNumLabs());

		return thpb.build();
	}
	
	public LabProto createLabProto(Structure s, StructureInfoProto sip,
	  		StructureLab sl) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		LabProto.Builder lpb = LabProto.newBuilder();
		lpb.setStructInfo(sip);
		lpb.setQueueSize(sl.getQueueSize());
		lpb.setPointsPerSecond(sl.getPointsPerSecond());

		return lpb.build();
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
	
	@Override
	public FullTaskProto createFullTaskProtoFromTask(Task task) {
		String name = task.getGoodName();
		String description = task.getDescription();

		FullTaskProto.Builder builder = FullTaskProto.newBuilder();
		builder.setTaskId(task.getId());
		if (null != name) {
			builder.setName(name);
		}

		if (null != description) {
			builder.setDescription(description);
		}

		builder.setCityId(task.getCityId());
		builder.setAssetNumWithinCity(task.getAssetNumberWithinCity());

		int prerequisiteTaskId = task.getPrerequisiteTaskId();
		if (prerequisiteTaskId > 0) {
			builder.setPrerequisiteTaskId(prerequisiteTaskId);
		}

		int prerequisiteQuestId = task.getPrerequisiteQuestId();
		if (prerequisiteQuestId > 0) {
			builder.setPrerequisiteQuestId(prerequisiteQuestId);
		}

		return builder.build();
	}
	
	@Override
	public PersistentEventProto createPersistentEventProtoFromEvent(
			EventPersistent event) {
		PersistentEventProto.Builder pepb = PersistentEventProto.newBuilder();

		int eventId = event.getId();
		String dayOfWeekStr = event.getDayOfWeek();
		int startHour = event.getStartHour();
		int eventDurationMinutes = event.getEventDurationMinutes();
		int taskId = event.getTaskId();
		int cooldownMinutes = event.getCoolDownMinutes();
		String eventTypeStr = event.getEventType();
		String monsterElem = event.getMonsterElement();

		pepb.setEventId(eventId);
		try {
			DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr);
			pepb.setDayOfWeek(dayOfWeek);
		} catch (Exception e) {
			log.error("can't create enum type. dayOfWeek=" + dayOfWeekStr + ".\t event=" + event);
		}

		pepb.setStartHour(startHour);
		pepb.setEventDurationMinutes(eventDurationMinutes);
		pepb.setTaskId(taskId);
		pepb.setCooldownMinutes(cooldownMinutes);

		try {
			EventType typ = EventType.valueOf(eventTypeStr);
			pepb.setType(typ);
		} catch (Exception e) {
			log.error("can't create enum type. eventType=" + eventTypeStr + ".\t event=" + event);
		}
		try {
			MonsterElement elem = MonsterElement.valueOf(monsterElem);
			pepb.setMonsterElement(elem);
		} catch (Exception e) {
			log.error("can't create enum type. monster elem=" + monsterElem + 
					".\t event=" + event);
		}

		return pepb.build();
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
