package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventClanProto.RequestJoinClanRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class RequestJoinClanRequestEvent extends RequestEvent {

	private RequestJoinClanRequestProto requestJoinClanRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			requestJoinClanRequestProto = RequestJoinClanRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = requestJoinClanRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public RequestJoinClanRequestProto getRequestJoinClanRequestProto() {
		return requestJoinClanRequestProto;
	}

}