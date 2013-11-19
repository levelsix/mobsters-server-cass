package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventQuestProto.QuestRedeemResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class QuestRedeemResponseEvent extends NormalResponseEvent {

  private QuestRedeemResponseProto questRedeemResponseProto;
  
  public QuestRedeemResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_QUEST_REDEEM_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = questRedeemResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setQuestRedeemResponseProto(QuestRedeemResponseProto questRedeemResponseProto) {
    this.questRedeemResponseProto = questRedeemResponseProto;
  }
  
}