//package com.lvl6.mobsters.events.response;
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.mobsters.eventprotos.CollectUserStructureEventProto.CollectUserStructureResponseProto;
//import com.lvl6.mobsters.events.NormalResponseEvent;
//import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;
//
//
//public class CollectUserStructureResponseEvent extends NormalResponseEvent {
//
//  private CollectUserStructureResponseProto collectUserStructureResponseProto;
//  
//  public CollectUserStructureResponseEvent(String playerId) {
//    super(playerId);
//    eventType = AocTwoEventProtocolResponse.S_REFILL_HP_OR_MANA_WITH_CONSUMABLE_EVENT_VALUE;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b = collectUserStructureResponseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//  
//  public void setCollectUserStructureResponseProto(CollectUserStructureResponseProto collectUserStructureResponseProto) {
//    this.collectUserStructureResponseProto = collectUserStructureResponseProto;
//  }
//  
//}