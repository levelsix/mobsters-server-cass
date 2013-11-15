package com.lvl6.aoc2.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.aoc2.events.NormalResponseEvent;
import com.lvl6.aoc2.eventprotos.SpeedUpBuildOrUpgradeStructureEventProto.SpeedUpBuildOrUpgradeStructureResponseProto;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;


public class SpeedUpBuildOrUpgradeStructureResponseEvent extends NormalResponseEvent {

  private SpeedUpBuildOrUpgradeStructureResponseProto speedUpBuildOrUpgradeStructureResponseProto;
  
  public SpeedUpBuildOrUpgradeStructureResponseEvent(String playerId) {
    super(playerId);
    eventType = AocTwoEventProtocolResponse.S_REPAIR_EQUIP_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = speedUpBuildOrUpgradeStructureResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setSpeedUpBuildOrUpgradeStructureResponseProto(SpeedUpBuildOrUpgradeStructureResponseProto speedUpBuildOrUpgradeStructureResponseProto) {
    this.speedUpBuildOrUpgradeStructureResponseProto = speedUpBuildOrUpgradeStructureResponseProto;
  }
  
}