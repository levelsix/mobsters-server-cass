package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.CombineUserMonsterPiecesResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class CombineUserMonsterPiecesResponseEvent extends NormalResponseEvent {

	private CombineUserMonsterPiecesResponseProto combineUserMonsterPiecesResponseProto;

	public CombineUserMonsterPiecesResponseEvent(String playerId) {
		super(playerId);
		eventType = MobstersEventProtocolResponse.S_COMBINE_USER_MONSTER_PIECES_EVENT_VALUE;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = combineUserMonsterPiecesResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setCombineUserMonsterPiecesResponseProto(CombineUserMonsterPiecesResponseProto combineUserMonsterPiecesResponseProto) {
		this.combineUserMonsterPiecesResponseProto = combineUserMonsterPiecesResponseProto;
	}

}