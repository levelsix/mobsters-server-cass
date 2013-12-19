package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventCityProto.PurchaseCityExpansionResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class PurchaseCityExpansionResponseEvent extends NormalResponseEvent {

	private PurchaseCityExpansionResponseProto purchaseCityExpansionResponseProto;

	public PurchaseCityExpansionResponseEvent(String playerId) {
		super(playerId);
		eventType = MobstersEventProtocolResponse.S_PURCHASE_CITY_EXPANSION_EVENT_VALUE;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = purchaseCityExpansionResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setPurchaseCityExpansionResponseProto(PurchaseCityExpansionResponseProto purchaseCityExpansionResponseProto) {
		this.purchaseCityExpansionResponseProto = purchaseCityExpansionResponseProto;
	}

}