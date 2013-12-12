package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventApnsProto.EnableAPNSResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class EnableAPNSResponseEvent extends NormalResponseEvent {

  private EnableAPNSResponseProto enableAPNSResponseProto;
  
  public EnableAPNSResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_ENABLE_APNS_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = enableAPNSResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setEnableAPNSResponseProto(EnableAPNSResponseProto enableAPNSResponseProto) {
    this.enableAPNSResponseProto = enableAPNSResponseProto;
  }
  
}