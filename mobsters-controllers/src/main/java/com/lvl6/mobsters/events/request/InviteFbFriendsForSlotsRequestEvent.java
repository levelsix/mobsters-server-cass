package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.InviteFbFriendsForSlotsRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class InviteFbFriendsForSlotsRequestEvent extends RequestEvent {
  
  private InviteFbFriendsForSlotsRequestProto inviteFbFriendsForSlotsRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      inviteFbFriendsForSlotsRequestProto = InviteFbFriendsForSlotsRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = inviteFbFriendsForSlotsRequestProto.getSender().getMinUserProto().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public InviteFbFriendsForSlotsRequestProto getInviteFbFriendsForSlotsRequestProto() {
    return inviteFbFriendsForSlotsRequestProto;
  }
  
}