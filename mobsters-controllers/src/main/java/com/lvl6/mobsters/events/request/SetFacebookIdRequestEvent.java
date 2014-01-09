package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventUserProto.SetFacebookIdRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class SetFacebookIdRequestEvent extends RequestEvent {

	private SetFacebookIdRequestProto setFacebookIdRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			setFacebookIdRequestProto = SetFacebookIdRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = setFacebookIdRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public SetFacebookIdRequestProto getSetFacebookIdRequestProto() {
		return setFacebookIdRequestProto;
	}

}