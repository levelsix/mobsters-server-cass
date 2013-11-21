package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestProgressRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class QuestProgressRequestEvent extends RequestEvent {

	private QuestProgressRequestProto questProgressRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			questProgressRequestProto = QuestProgressRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = questProgressRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public QuestProgressRequestProto getQuestProgressRequestProto() {
		return questProgressRequestProto;
	}

}