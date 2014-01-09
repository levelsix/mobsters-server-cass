package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class AcceptAndRejectFbInviteForSlotsResponseEvent extends NormalResponseEvent {

	private AcceptAndRejectFbInviteForSlotsResponseProto acceptAndRejectFbInviteForSlotsResponseProto;

	public AcceptAndRejectFbInviteForSlotsResponseEvent(String playerId) {
		super(playerId);
		eventType = MobstersEventProtocolResponse.S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT_VALUE;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = acceptAndRejectFbInviteForSlotsResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setAcceptAndRejectFbInviteForSlotsResponseProto(AcceptAndRejectFbInviteForSlotsResponseProto acceptAndRejectFbInviteForSlotsResponseProto) {
		this.acceptAndRejectFbInviteForSlotsResponseProto = acceptAndRejectFbInviteForSlotsResponseProto;
	}

}