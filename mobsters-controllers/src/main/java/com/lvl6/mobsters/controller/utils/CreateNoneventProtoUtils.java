package com.lvl6.mobsters.controller.utils;

import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto;
import com.lvl6.mobsters.noneventprotos.UserProto.FullUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.utils.Dialogue;


public interface CreateNoneventProtoUtils {
	
	public abstract FullUserProto createFullUserProtoFromUser(User u);
	
	public abstract QuestProto createQuestProtoFromQuest(Quest q);
	
	public abstract DialogueProto createDialogueProtoFromDialogue(Dialogue d);
	
	public abstract SpeechSegmentProto createSpeechSegmentProto(
			int speakerInt, String speakerText);
}