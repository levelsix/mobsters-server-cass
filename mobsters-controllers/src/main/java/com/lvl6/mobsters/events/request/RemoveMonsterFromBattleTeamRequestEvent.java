package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.RemoveMonsterFromBattleTeamRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class RemoveMonsterFromBattleTeamRequestEvent extends RequestEvent {
  
  private RemoveMonsterFromBattleTeamRequestProto removeMonsterFromBattleTeamRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      removeMonsterFromBattleTeamRequestProto = RemoveMonsterFromBattleTeamRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = removeMonsterFromBattleTeamRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public RemoveMonsterFromBattleTeamRequestProto getRemoveMonsterFromBattleTeamRequestProto() {
    return removeMonsterFromBattleTeamRequestProto;
  }
  
}