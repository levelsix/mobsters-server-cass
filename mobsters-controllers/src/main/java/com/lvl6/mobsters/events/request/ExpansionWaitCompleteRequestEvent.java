package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventStructureProto.ExpansionWaitCompleteRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class ExpansionWaitCompleteRequestEvent extends RequestEvent {

	private ExpansionWaitCompleteRequestProto expansionWaitCompleteRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			expansionWaitCompleteRequestProto = ExpansionWaitCompleteRequestProto.parseFrom(ByteString.copyFrom(bb));
			playerId = expansionWaitCompleteRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public ExpansionWaitCompleteRequestProto getExpansionWaitCompleteRequestProto() {
		return expansionWaitCompleteRequestProto;
	}

}