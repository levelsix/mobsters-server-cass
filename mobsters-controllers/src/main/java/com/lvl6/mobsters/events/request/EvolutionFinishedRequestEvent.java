package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolutionFinishedRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class EvolutionFinishedRequestEvent extends RequestEvent {
  
  private EvolutionFinishedRequestProto evolutionFinishedRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      evolutionFinishedRequestProto = EvolutionFinishedRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = evolutionFinishedRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public EvolutionFinishedRequestProto getEvolutionFinishedRequestProto() {
    return evolutionFinishedRequestProto;
  }
  
}