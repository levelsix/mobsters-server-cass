package com.lvl6.aoc2.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.aoc2.eventprotos.ForceLogoutEventProto.ForceLogoutResponseProto;
import com.lvl6.aoc2.events.PreDatabaseResponseEvent;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;

public class ForceLogoutResponseEvent extends PreDatabaseResponseEvent {

  private ForceLogoutResponseProto forceLogoutResponseProto;
  
  public ForceLogoutResponseEvent(String udid) {
    super(udid);
    eventType = AocTwoEventProtocolResponse.S_FORCE_LOGOUT_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = forceLogoutResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setForceLogoutResponseProto(ForceLogoutResponseProto forceLogoutResponseProto) {
    this.forceLogoutResponseProto = forceLogoutResponseProto;
  }
  
}