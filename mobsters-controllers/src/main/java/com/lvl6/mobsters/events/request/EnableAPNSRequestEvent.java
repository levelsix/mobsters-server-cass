package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventApnsProto.EnableAPNSRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class EnableAPNSRequestEvent extends RequestEvent {

	private EnableAPNSRequestProto enableAPNSRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			enableAPNSRequestProto = EnableAPNSRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = enableAPNSRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public EnableAPNSRequestProto getEnableAPNSRequestProto() {
		return enableAPNSRequestProto;
	}

}