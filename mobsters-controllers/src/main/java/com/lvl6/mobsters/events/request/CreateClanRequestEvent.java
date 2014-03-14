package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventClanProto.CreateClanRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class CreateClanRequestEvent extends RequestEvent {

	private CreateClanRequestProto createClanRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			createClanRequestProto = CreateClanRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = createClanRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public CreateClanRequestProto getCreateClanRequestProto() {
		return createClanRequestProto;
	}

}