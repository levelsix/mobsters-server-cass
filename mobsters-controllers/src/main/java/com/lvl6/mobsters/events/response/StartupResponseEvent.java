package com.lvl6.mobsters.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventStartupProto.StartupResponseProto;
import com.lvl6.mobsters.events.PreDatabaseResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;

public class StartupResponseEvent extends PreDatabaseResponseEvent {
	private StartupResponseProto startupResponseProto;

	public StartupResponseEvent(String udid) {
		super(udid);
		eventType = MobstersEventProtocolResponse.S_STARTUP_EVENT_VALUE;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = startupResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setStartupResponseProto(StartupResponseProto startupResponseProto) {
		this.startupResponseProto = startupResponseProto;
	}

}