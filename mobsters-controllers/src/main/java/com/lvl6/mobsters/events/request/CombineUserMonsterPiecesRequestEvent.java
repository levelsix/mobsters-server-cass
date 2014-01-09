package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.CombineUserMonsterPiecesRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class CombineUserMonsterPiecesRequestEvent extends RequestEvent {

	private CombineUserMonsterPiecesRequestProto combineUserMonsterPiecesRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			combineUserMonsterPiecesRequestProto = CombineUserMonsterPiecesRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = combineUserMonsterPiecesRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public CombineUserMonsterPiecesRequestProto getCombineUserMonsterPiecesRequestProto() {
		return combineUserMonsterPiecesRequestProto;
	}

}