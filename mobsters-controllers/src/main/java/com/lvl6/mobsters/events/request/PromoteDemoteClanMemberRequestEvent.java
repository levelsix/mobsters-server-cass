package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventClanProto.PromoteDemoteClanMemberRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class PromoteDemoteClanMemberRequestEvent extends RequestEvent {

	private PromoteDemoteClanMemberRequestProto promoteDemoteClanMemberRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			promoteDemoteClanMemberRequestProto = PromoteDemoteClanMemberRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = promoteDemoteClanMemberRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public PromoteDemoteClanMemberRequestProto getPromoteDemoteClanMemberRequestProto() {
		return promoteDemoteClanMemberRequestProto;
	}

}