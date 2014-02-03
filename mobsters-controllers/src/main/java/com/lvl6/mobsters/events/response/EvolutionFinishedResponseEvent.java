package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolutionFinishedResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class EvolutionFinishedResponseEvent extends NormalResponseEvent {

  private EvolutionFinishedResponseProto evolutionFinishedResponseProto;
  
  public EvolutionFinishedResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_EVOLUTION_FINISHED_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = evolutionFinishedResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  public void setEvolutionFinishedResponseProto(EvolutionFinishedResponseProto evolutionFinishedResponseProto) {
    this.evolutionFinishedResponseProto = evolutionFinishedResponseProto;
  }
  
}