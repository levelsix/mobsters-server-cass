package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestAcceptRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class QuestAcceptRequestEvent extends RequestEvent {

	private QuestAcceptRequestProto questAcceptRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			questAcceptRequestProto = QuestAcceptRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = questAcceptRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public QuestAcceptRequestProto getQuestAcceptRequestProto() {
		return questAcceptRequestProto;
	}

}