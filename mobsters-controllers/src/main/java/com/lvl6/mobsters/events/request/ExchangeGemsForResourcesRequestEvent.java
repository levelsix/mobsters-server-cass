package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventInAppPurchaseProto.ExchangeGemsForResourcesRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class ExchangeGemsForResourcesRequestEvent extends RequestEvent {
  
  private ExchangeGemsForResourcesRequestProto exchangeGemsForResourcesRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      exchangeGemsForResourcesRequestProto = ExchangeGemsForResourcesRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = exchangeGemsForResourcesRequestProto.getSender().getMinUserProto().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public ExchangeGemsForResourcesRequestProto getExchangeGemsForResourcesRequestProto() {
    return exchangeGemsForResourcesRequestProto;
  }
  
}