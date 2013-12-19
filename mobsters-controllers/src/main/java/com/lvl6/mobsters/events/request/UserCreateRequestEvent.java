package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventUserProto.UserCreateRequestProto;
import com.lvl6.mobsters.events.PreDatabaseRequestEvent;

public class UserCreateRequestEvent extends PreDatabaseRequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	private UserCreateRequestProto userCreateRequestProto;

	@Override
	public void read(ByteBuffer bb) {
		try {
			userCreateRequestProto = UserCreateRequestProto.parseFrom(ByteString.copyFrom(bb));

			//Player id is "" since it may not be initialized yet.
			playerId = "";//userCreateRequestProto.getMup().getUserID();

			udid = userCreateRequestProto.getUdid(); 
		} catch (InvalidProtocolBufferException e) {
			log.error("unexpected error: ", e);
			//      e.printStackTrace();
		}
	}

	public UserCreateRequestProto getUserCreateRequestProto() {
		return userCreateRequestProto;
	}

}