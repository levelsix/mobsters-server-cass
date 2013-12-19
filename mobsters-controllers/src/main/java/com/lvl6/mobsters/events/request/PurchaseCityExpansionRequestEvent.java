package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventCityProto.PurchaseCityExpansionRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class PurchaseCityExpansionRequestEvent extends RequestEvent {

	private PurchaseCityExpansionRequestProto purchaseCityExpansionRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			purchaseCityExpansionRequestProto = PurchaseCityExpansionRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = purchaseCityExpansionRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public PurchaseCityExpansionRequestProto getPurchaseCityExpansionRequestProto() {
		return purchaseCityExpansionRequestProto;
	}

}