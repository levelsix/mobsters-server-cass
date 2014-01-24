package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class QuestRedeemRequestEvent extends RequestEvent {

	private QuestRedeemRequestProto questRedeemRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			questRedeemRequestProto = QuestRedeemRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = questRedeemRequestProto.getSender().getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public QuestRedeemRequestProto getQuestRedeemRequestProto() {
		return questRedeemRequestProto;
	}

}