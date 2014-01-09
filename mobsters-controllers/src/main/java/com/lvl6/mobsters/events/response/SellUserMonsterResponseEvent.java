package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SellUserMonsterResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class SellUserMonsterResponseEvent extends NormalResponseEvent {

	private SellUserMonsterResponseProto sellUserMonsterResponseProto;

	public SellUserMonsterResponseEvent(String playerId) {
		super(playerId);
		eventType = MobstersEventProtocolResponse.S_SELL_USER_MONSTER_EVENT_VALUE;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = sellUserMonsterResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setSellUserMonsterResponseProto(SellUserMonsterResponseProto sellUserMonsterResponseProto) {
		this.sellUserMonsterResponseProto = sellUserMonsterResponseProto;
	}

}