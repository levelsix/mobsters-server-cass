package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AcceptAndRejectFbInviteForSlotsRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class AcceptAndRejectFbInviteForSlotsRequestEvent extends RequestEvent {

	private AcceptAndRejectFbInviteForSlotsRequestProto acceptAndRejectFbInviteForSlotsRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			acceptAndRejectFbInviteForSlotsRequestProto = AcceptAndRejectFbInviteForSlotsRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = acceptAndRejectFbInviteForSlotsRequestProto.getSender().getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public AcceptAndRejectFbInviteForSlotsRequestProto getAcceptAndRejectFbInviteForSlotsRequestProto() {
		return acceptAndRejectFbInviteForSlotsRequestProto;
	}

}