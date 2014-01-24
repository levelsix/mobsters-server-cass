package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class HealMonsterRequestEvent extends RequestEvent {

	private HealMonsterRequestProto collectUserStructureRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			collectUserStructureRequestProto = HealMonsterRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = collectUserStructureRequestProto.getSender().getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public HealMonsterRequestProto getHealMonsterRequestProto() {
		return collectUserStructureRequestProto;
	}

}