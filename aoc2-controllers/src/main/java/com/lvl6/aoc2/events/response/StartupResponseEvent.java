package com.lvl6.aoc2.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.aoc2.events.PreDatabaseResponseEvent;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;
import com.lvl6.aoc2.eventprotos.StartupEventProto.StartupResponseProto;

public class StartupResponseEvent extends PreDatabaseResponseEvent {
  private StartupResponseProto startupResponseProto;
  
  public StartupResponseEvent(String udid) {
    super(udid);
    eventType = AocTwoEventProtocolResponse.S_STARTUP_EVENT_VALUE;
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