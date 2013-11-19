package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestAcceptResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class QuestAcceptResponseEvent extends NormalResponseEvent {

  private QuestAcceptResponseProto questAcceptResponseProto;
  
  public QuestAcceptResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_QUEST_ACCEPT_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = questAcceptResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setQuestAcceptResponseProto(QuestAcceptResponseProto questAcceptResponseProto) {
    this.questAcceptResponseProto = questAcceptResponseProto;
  }
  
}