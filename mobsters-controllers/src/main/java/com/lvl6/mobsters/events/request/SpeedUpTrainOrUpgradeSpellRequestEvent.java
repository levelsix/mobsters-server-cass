package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.SpeedUpTrainOrUpgradeSpellEventProto.SpeedUpTrainOrUpgradeSpellRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class SpeedUpTrainOrUpgradeSpellRequestEvent extends RequestEvent {
  
  private SpeedUpTrainOrUpgradeSpellRequestProto speedUpTrainOrUpgradeSpellRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      speedUpTrainOrUpgradeSpellRequestProto = SpeedUpTrainOrUpgradeSpellRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = speedUpTrainOrUpgradeSpellRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public SpeedUpTrainOrUpgradeSpellRequestProto getSpeedUpTrainOrUpgradeSpellRequestProto() {
    return speedUpTrainOrUpgradeSpellRequestProto;
  }
  
}