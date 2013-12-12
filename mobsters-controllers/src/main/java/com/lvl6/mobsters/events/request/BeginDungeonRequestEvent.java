package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.BeginDungeonRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class BeginDungeonRequestEvent extends RequestEvent {

	private BeginDungeonRequestProto beginDungeonRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			beginDungeonRequestProto = BeginDungeonRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = beginDungeonRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public BeginDungeonRequestProto getBeginDungeonRequestProto() {
		return beginDungeonRequestProto;
	}

}