package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventBoosterPackProto.PurchaseBoosterPackRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class PurchaseBoosterPackRequestEvent extends RequestEvent {

	private PurchaseBoosterPackRequestProto purchaseBoosterPackRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			purchaseBoosterPackRequestProto = PurchaseBoosterPackRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = purchaseBoosterPackRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public PurchaseBoosterPackRequestProto getPurchaseBoosterPackRequestProto() {
		return purchaseBoosterPackRequestProto;
	}

}