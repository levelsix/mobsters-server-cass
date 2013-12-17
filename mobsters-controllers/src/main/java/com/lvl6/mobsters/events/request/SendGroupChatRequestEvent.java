package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventChatProto.SendGroupChatRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class SendGroupChatRequestEvent extends RequestEvent {

	private SendGroupChatRequestProto sendGroupChatRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			sendGroupChatRequestProto = SendGroupChatRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = sendGroupChatRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public SendGroupChatRequestProto getSendGroupChatRequestProto() {
		return sendGroupChatRequestProto;
	}

}