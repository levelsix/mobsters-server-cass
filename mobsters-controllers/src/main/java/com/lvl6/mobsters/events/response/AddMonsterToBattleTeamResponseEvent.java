package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AddMonsterToBattleTeamResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class AddMonsterToBattleTeamResponseEvent extends NormalResponseEvent {

  private AddMonsterToBattleTeamResponseProto addMonsterToBattleTeamResponseProto;
  
  public AddMonsterToBattleTeamResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_ADD_MONSTER_TO_BATTLE_TEAM_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = addMonsterToBattleTeamResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setAddMonsterToBattleTeamResponseProto(AddMonsterToBattleTeamResponseProto addMonsterToBattleTeamResponseProto) {
    this.addMonsterToBattleTeamResponseProto = addMonsterToBattleTeamResponseProto;
  }
  
}