package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.RemoveMonsterFromBattleTeamResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class RemoveMonsterFromBattleTeamResponseEvent extends NormalResponseEvent {

  private RemoveMonsterFromBattleTeamResponseProto removeMonsterFromBattleTeamResponseProto;
  
  public RemoveMonsterFromBattleTeamResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = removeMonsterFromBattleTeamResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setRemoveMonsterFromBattleTeamResponseProto(RemoveMonsterFromBattleTeamResponseProto removeMonsterFromBattleTeamResponseProto) {
    this.removeMonsterFromBattleTeamResponseProto = removeMonsterFromBattleTeamResponseProto;
  }
  
}