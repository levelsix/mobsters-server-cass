package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.TrainOrUpgradeSpellEventProto.TrainOrUpgradeSpellRequestProto;

public class TrainOrUpgradeSpellRequestEvent extends RequestEvent {
  
  private TrainOrUpgradeSpellRequestProto trainOrUpgradeSpellRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      trainOrUpgradeSpellRequestProto = TrainOrUpgradeSpellRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = trainOrUpgradeSpellRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public TrainOrUpgradeSpellRequestProto getTrainOrUpgradeSpellRequestProto() {
    return trainOrUpgradeSpellRequestProto;
  }
  
}