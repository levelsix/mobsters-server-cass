package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventChatProto.SendGroupChatResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class SendGroupChatResponseEvent extends NormalResponseEvent {

  private SendGroupChatResponseProto sendGroupChatResponseProto;
  
  public SendGroupChatResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_SEND_GROUP_CHAT_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = sendGroupChatResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setSendGroupChatResponseProto(SendGroupChatResponseProto sendGroupChatResponseProto) {
    this.sendGroupChatResponseProto = sendGroupChatResponseProto;
  }
  
}