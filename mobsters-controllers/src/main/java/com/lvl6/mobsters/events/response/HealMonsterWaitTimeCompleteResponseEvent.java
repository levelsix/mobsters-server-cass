//package com.lvl6.mobsters.events.response;
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterWaitTimeCompleteResponseProto;
//import com.lvl6.mobsters.events.NormalResponseEvent;
//import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;
//
//
//public class HealMonsterWaitTimeCompleteResponseEvent extends NormalResponseEvent {
//
//  private HealMonsterWaitTimeCompleteResponseProto healMonsterWaitTimeCompleteResponseProto;
//  
//  public HealMonsterWaitTimeCompleteResponseEvent(String playerId) {
//    super(playerId);
//    eventType = MobstersEventProtocolResponse.S_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT_VALUE;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b = healMonsterWaitTimeCompleteResponseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//  
//  public void setHealMonsterWaitTimeCompleteResponseProto(HealMonsterWaitTimeCompleteResponseProto healMonsterWaitTimeCompleteResponseProto) {
//    this.healMonsterWaitTimeCompleteResponseProto = healMonsterWaitTimeCompleteResponseProto;
//  }
//  
//}