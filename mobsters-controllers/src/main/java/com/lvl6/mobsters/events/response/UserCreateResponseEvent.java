package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventUserProto.UserCreateResponseProto;
import com.lvl6.mobsters.events.PreDatabaseResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class UserCreateResponseEvent extends PreDatabaseResponseEvent {

  private UserCreateResponseProto userCreateResponseProto;
  
  public UserCreateResponseEvent(String udid) {
    super(udid);
    eventType = MobstersEventProtocolResponse.S_USER_CREATE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = userCreateResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setUserCreateResponseProto(UserCreateResponseProto userCreateResponseProto) {
    this.userCreateResponseProto = userCreateResponseProto;
  }
  
}