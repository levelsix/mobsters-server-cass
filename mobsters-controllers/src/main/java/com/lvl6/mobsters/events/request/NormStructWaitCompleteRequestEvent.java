package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventStructureProto.NormStructWaitCompleteRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class NormStructWaitCompleteRequestEvent extends RequestEvent {
  
  private NormStructWaitCompleteRequestProto normStructWaitCompleteRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      normStructWaitCompleteRequestProto = NormStructWaitCompleteRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = normStructWaitCompleteRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public NormStructWaitCompleteRequestProto getNormStructWaitCompleteRequestProto() {
    return normStructWaitCompleteRequestProto;
  }
  
}