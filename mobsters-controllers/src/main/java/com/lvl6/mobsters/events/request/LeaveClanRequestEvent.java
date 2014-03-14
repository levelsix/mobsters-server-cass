package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventClanProto.LeaveClanRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class LeaveClanRequestEvent extends RequestEvent {

	private LeaveClanRequestProto leaveClanRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			leaveClanRequestProto = LeaveClanRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = leaveClanRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public LeaveClanRequestProto getLeaveClanRequestProto() {
		return leaveClanRequestProto;
	}

}