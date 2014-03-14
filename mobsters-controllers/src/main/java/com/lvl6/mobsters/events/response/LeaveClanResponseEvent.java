package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventClanProto.LeaveClanResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class LeaveClanResponseEvent extends NormalResponseEvent {

	private LeaveClanResponseProto leaveClanResponseProto;

	public LeaveClanResponseEvent(String playerId) {
		super(playerId);
		eventType = MobstersEventProtocolResponse.S_LEAVE_CLAN_EVENT_VALUE;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = leaveClanResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setLeaveClanResponseProto(LeaveClanResponseProto leaveClanResponseProto) {
		this.leaveClanResponseProto = leaveClanResponseProto;
	}

}