package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventStructureProto.FinishNormStructWaittimeWithDiamondsRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class FinishNormStructWaittimeWithDiamondsRequestEvent extends RequestEvent {
  
  private FinishNormStructWaittimeWithDiamondsRequestProto finishNormStructWaittimeWithDiamondsRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      finishNormStructWaittimeWithDiamondsRequestProto = FinishNormStructWaittimeWithDiamondsRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = finishNormStructWaittimeWithDiamondsRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public FinishNormStructWaittimeWithDiamondsRequestProto getFinishNormStructWaittimeWithDiamondsRequestProto() {
    return finishNormStructWaittimeWithDiamondsRequestProto;
  }
  
}