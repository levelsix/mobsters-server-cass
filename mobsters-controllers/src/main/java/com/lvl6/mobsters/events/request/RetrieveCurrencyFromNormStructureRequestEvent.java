package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class RetrieveCurrencyFromNormStructureRequestEvent extends RequestEvent {
  
  private RetrieveCurrencyFromNormStructureRequestProto retrieveCurrencyFromNormStructureRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      retrieveCurrencyFromNormStructureRequestProto = RetrieveCurrencyFromNormStructureRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = retrieveCurrencyFromNormStructureRequestProto.getSender().getMinUserProto().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public RetrieveCurrencyFromNormStructureRequestProto getRetrieveCurrencyFromNormStructureRequestProto() {
    return retrieveCurrencyFromNormStructureRequestProto;
  }
  
}