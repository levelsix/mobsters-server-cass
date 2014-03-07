package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventUserProto.UpdateUserCurrencyRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class UpdateUserCurrencyRequestEvent extends RequestEvent {

	private UpdateUserCurrencyRequestProto updateUserCurrencyRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			updateUserCurrencyRequestProto = UpdateUserCurrencyRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = updateUserCurrencyRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public UpdateUserCurrencyRequestProto getUpdateUserCurrencyRequestProto() {
		return updateUserCurrencyRequestProto;
	}

}