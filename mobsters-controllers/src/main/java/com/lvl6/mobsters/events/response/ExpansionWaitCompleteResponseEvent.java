package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventStructureProto.ExpansionWaitCompleteResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class ExpansionWaitCompleteResponseEvent extends NormalResponseEvent {

	private ExpansionWaitCompleteResponseProto expansionWaitCompleteResponseProto;

	public ExpansionWaitCompleteResponseEvent(String playerId) {
		super(playerId);
		eventType = MobstersEventProtocolResponse.S_EXPANSION_WAIT_COMPLETE_EVENT_VALUE;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = expansionWaitCompleteResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setExpansionWaitCompleteResponseProto(ExpansionWaitCompleteResponseProto expansionWaitCompleteResponseProto) {
		this.expansionWaitCompleteResponseProto = expansionWaitCompleteResponseProto;
	}

}