package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventUserProto.UpdateUserCurrencyResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class UpdateUserCurrencyResponseEvent extends NormalResponseEvent {

	private UpdateUserCurrencyResponseProto updateUserCurrencyResponseProto;

	public UpdateUserCurrencyResponseEvent(String playerId) {
		super(playerId);
		eventType = MobstersEventProtocolResponse.S_UPDATE_USER_CURRENCY_EVENT_VALUE;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = updateUserCurrencyResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setUpdateUserCurrencyResponseProto(UpdateUserCurrencyResponseProto updateUserCurrencyResponseProto) {
		this.updateUserCurrencyResponseProto = updateUserCurrencyResponseProto;
	}

}