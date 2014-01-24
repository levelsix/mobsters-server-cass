package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SellUserMonsterRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class SellUserMonsterRequestEvent extends RequestEvent {

	private SellUserMonsterRequestProto sellUserMonsterRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			sellUserMonsterRequestProto = SellUserMonsterRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = sellUserMonsterRequestProto.getSender().getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public SellUserMonsterRequestProto getSellUserMonsterRequestProto() {
		return sellUserMonsterRequestProto;
	}

} 