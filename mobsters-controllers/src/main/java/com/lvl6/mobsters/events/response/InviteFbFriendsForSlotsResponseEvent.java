package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.InviteFbFriendsForSlotsResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class InviteFbFriendsForSlotsResponseEvent extends NormalResponseEvent {

  private InviteFbFriendsForSlotsResponseProto inviteFbFriendsForSlotsResponseProto;
  
  public InviteFbFriendsForSlotsResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = inviteFbFriendsForSlotsResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setInviteFbFriendsForSlotsResponseProto(InviteFbFriendsForSlotsResponseProto inviteFbFriendsForSlotsResponseProto) {
    this.inviteFbFriendsForSlotsResponseProto = inviteFbFriendsForSlotsResponseProto;
  }
  
}