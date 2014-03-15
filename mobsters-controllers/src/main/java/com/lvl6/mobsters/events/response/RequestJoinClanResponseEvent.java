package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventClanProto.RequestJoinClanResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class RequestJoinClanResponseEvent extends NormalResponseEvent {

	private RequestJoinClanResponseProto requestJoinClanResponseProto;

	public RequestJoinClanResponseEvent(String playerId) {
		super(playerId);
		eventType = MobstersEventProtocolResponse.S_REQUEST_JOIN_CLAN_EVENT_VALUE;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = requestJoinClanResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRequestJoinClanResponseProto(RequestJoinClanResponseProto requestJoinClanResponseProto) {
		this.requestJoinClanResponseProto = requestJoinClanResponseProto;
	}

}