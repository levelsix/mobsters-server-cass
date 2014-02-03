package com.lvl6.mobsters.controller.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.noneventprotos.BoosterPackStuffProto.BoosterDisplayItemProto;
import com.lvl6.mobsters.noneventprotos.BoosterPackStuffProto.BoosterItemProto;
import com.lvl6.mobsters.noneventprotos.BoosterPackStuffProto.BoosterPackProto;
import com.lvl6.mobsters.noneventprotos.CityProto.CityElementProto;
import com.lvl6.mobsters.noneventprotos.CityProto.CityExpansionCostProto;
import com.lvl6.mobsters.noneventprotos.CityProto.FullCityProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.MonsterLevelInfoProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.MonsterProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserEnhancementProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.FullUserQuestProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.CoordinateProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.FullUserStructureProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.HospitalProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.LabProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResidenceProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceGeneratorProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceStorageProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.StructureInfoProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.TownHallProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.FullTaskProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.PersistentEventProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageMonsterProto;
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


public interface CreateNoneventProtoUtil {
	
	//BOOSTER STUFF PROTO****************************************************************
	public abstract BoosterItemProto createBoosterItemProto(BoosterItem bi);
	
	public abstract BoosterDisplayItemProto createBoosterDisplayItemProto(
	  		BoosterDisplayItem bdi);
	
	public abstract BoosterPackProto createBoosterPackProto(BoosterPack bp,
	  		Collection<BoosterItem> biList, Collection<BoosterDisplayItem> bdiList);
	
	//CITY PROTO****************************************************************
	public abstract CityExpansionCostProto createCityExpansionCostProtoFromCityExpansionCost(
			ExpansionCost ec);
	
	public abstract CityElementProto createCityElementProtoFromCityElement(CityElement ce);
	
	public abstract FullCityProto createFullCityProtoFromCity(City c,
			List<Task> allTasksForCity);
	
	//MONSTER PROTO****************************************************************
	public abstract List<FullUserMonsterProto> createFullUserMonsterProtoList(
	  		List<MonsterForUser> userMonsters);
	
	public abstract FullUserMonsterProto createFullUserMonsterProtoFromUserMonster(
			MonsterForUser mfu);
	
	public abstract UserMonsterHealingProto createUserMonsterHealingProtoFromObj(
			MonsterHealingForUser mhfu);
	
	public abstract UserEnhancementProto createUserEnhancementProtoFromObj(UUID userId,
			UserEnhancementItemProto baseMonster, List<UserEnhancementItemProto> feeders);
	
	public abstract UserEnhancementItemProto createUserEnhancementItemProtoFromObj(
	  		MonsterEnhancingForUser mefu);
	
	public abstract MonsterProto createMonsterProto(Monster aMonster,
			Map<Integer, MonsterLevelInfo> levelToInfo);
	
	public abstract List<MonsterLevelInfoProto> createMonsterLevelInfoFromInfo(
			Map<Integer, MonsterLevelInfo> lvlToInfo);
	
	//QUEST PROTO****************************************************************
	public abstract QuestProto createQuestProtoFromQuest(Quest q);
	
	public abstract DialogueProto createDialogueProtoFromDialogue(Dialogue d);
	
	public abstract SpeechSegmentProto createSpeechSegmentProto(
			int speakerInt, String speakerText);
	
	public abstract List<FullUserQuestProto> createFullUserQuestProtos(
			List<QuestForUser> userQuests, Map<Integer, Quest> questIdsToQuests);
	
	//STRUCTURE PROTO****************************************************************
	public abstract FullUserStructureProto createFullUserStructureProtoFromUserStruct(
			StructureForUser userStruct);
	
	public abstract CoordinateProto createCoordinateProtoFromCoordinatePair(CoordinatePair cp);
	
	public abstract StructureInfoProto createStructureInfoProtoFromStructure(Structure s);
	
	public abstract ResourceGeneratorProto createResourceGeneratorProto(Structure s,
	  		StructureInfoProto sip, StructureResourceGenerator srg);
	
	public abstract ResourceStorageProto createResourceStorageProto(Structure s,
	  		StructureInfoProto sip,  StructureResourceStorage srs);
	
	public abstract HospitalProto createHospitalProto(Structure s, StructureInfoProto sip,
	  		StructureHospital sh);
	
	public abstract ResidenceProto createResidenceProto(Structure s, StructureInfoProto sip,
	  		StructureResidence sr);
	
	public abstract TownHallProto createTownHallProto(Structure s, StructureInfoProto sip,
	  		StructureTownHall sth);
	
	public abstract LabProto createLabProto(Structure s, StructureInfoProto sip,
	  		StructureLab sl);
	
	//TASK PROTO****************************************************************
	public abstract TaskStageProto createTaskStageProtoFromTaskStageForUser(
			int taskStageId, List<TaskStageForUser> tsfuList);
	
	public abstract TaskStageMonsterProto createTaskStageMonsterProtoFromTaskStageForUser(
			TaskStageForUser tsfu);
	
	public abstract FullTaskProto createFullTaskProtoFromTask(Task task);
	
	public abstract PersistentEventProto createPersistentEventProtoFromEvent(
	  		EventPersistent event);
	
	//USER PROTO****************************************************************
	public abstract FullUserProto createFullUserProtoFromUser(User u);
	
	public abstract MinimumUserProto createMinimumUserProtoFromUser(User u);
	
	public abstract MinimumUserProtoWithFacebookId createMinimumUserProtoWithFacebookIdFromUser(User u);
	
	public abstract UserFacebookInviteForSlotProto createUserFacebookInviteForSlotProtoFromInvite(
			UserFacebookInviteForSlot invite, User inviter,
			MinimumUserProtoWithFacebookId inviterProto);
}
