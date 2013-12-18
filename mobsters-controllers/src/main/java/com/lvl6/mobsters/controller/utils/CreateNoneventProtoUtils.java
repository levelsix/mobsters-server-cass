package com.lvl6.mobsters.controller.utils;

import java.util.List;

import com.lvl6.mobsters.noneventprotos.CityProto.CityElementProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.CoordinateProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.FullUserStructureProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageMonsterProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.TaskStageProto;
import com.lvl6.mobsters.noneventprotos.UserProto.FullUserProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.CityElement;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.utils.CoordinatePair;
import com.lvl6.mobsters.utils.Dialogue;


public interface CreateNoneventProtoUtils {
	
	//CITY PROTO****************************************************************
	public abstract CityElementProto createCityElementProtoFromCityElement(CityElement ce);
	
	//MONSTER PROTO****************************************************************
	public abstract List<FullUserMonsterProto> createFullUserMonsterProtoList(
	  		List<MonsterForUser> userMonsters);
	
	public FullUserMonsterProto createFullUserMonsterProtoFromUserMonster(MonsterForUser mfu);
	
	
	//QUEST PROTO****************************************************************
	public abstract QuestProto createQuestProtoFromQuest(Quest q);
	
	public abstract DialogueProto createDialogueProtoFromDialogue(Dialogue d);
	
	public abstract SpeechSegmentProto createSpeechSegmentProto(
			int speakerInt, String speakerText);
	
	//STRUCTURE PROTO****************************************************************
	public abstract FullUserStructureProto createFullUserStructureProtoFromUserStruct(
			StructureForUser userStruct);
	
	public abstract CoordinateProto createCoordinateProtoFromCoordinatePair(CoordinatePair cp);
	
	//TASK PROTO****************************************************************
	public abstract TaskStageProto createTaskStageProtoFromTaskStageForUser(
			int taskStageId, List<TaskStageForUser> tsfuList);
	
	public abstract TaskStageMonsterProto createTaskStageMonsterProtoFromTaskStageForUser(
			TaskStageForUser tsfu);
	
	
	//USER PROTO****************************************************************
	public abstract FullUserProto createFullUserProtoFromUser(User u);
	
	public abstract MinimumUserProto createMinimumUserProtoFromUser(User u);
	
}