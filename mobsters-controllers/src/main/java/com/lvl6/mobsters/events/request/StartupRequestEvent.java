package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventStartupProto.StartupRequestProto;
import com.lvl6.mobsters.events.PreDatabaseRequestEvent;

public class StartupRequestEvent extends PreDatabaseRequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private StartupRequestProto startupRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	public void read(ByteBuffer buff) {
		try {
			startupRequestProto = StartupRequestProto.parseFrom(ByteString.copyFrom(buff));

			//Player id is "" since it won't be initialized yet.
			playerId = "";//startupRequestProto.getSender().getUserId();

			udid = startupRequestProto.getUdid();
		} catch (InvalidProtocolBufferException e) {
			log.error("unexpected error: StartupRequestEvent. ", e);
		}
	}

	public StartupRequestProto getStartupRequestProto() {
		return startupRequestProto;
	}

	public void setStartupRequestProto(StartupRequestProto startupRequestProto) {
		this.startupRequestProto = startupRequestProto;
	}
}
