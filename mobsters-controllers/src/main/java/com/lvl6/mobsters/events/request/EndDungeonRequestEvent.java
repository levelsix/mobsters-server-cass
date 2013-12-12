package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.EndDungeonRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class EndDungeonRequestEvent extends RequestEvent {

	private EndDungeonRequestProto endDungeonRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			endDungeonRequestProto = EndDungeonRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = endDungeonRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public EndDungeonRequestProto getEndDungeonRequestProto() {
		return endDungeonRequestProto;
	}

}