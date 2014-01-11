package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class ExchangeGemsForResourcesResponseEvent extends NormalResponseEvent {

  private ExchangeGemsForResourcesResponseProto exchangeGemsForResourcesResponseProto;
  
  public ExchangeGemsForResourcesResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_EXCHANGE_GEMS_FOR_RESOURCES_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = exchangeGemsForResourcesResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setExchangeGemsForResourcesResponseProto(ExchangeGemsForResourcesResponseProto exchangeGemsForResourcesResponseProto) {
    this.exchangeGemsForResourcesResponseProto = exchangeGemsForResourcesResponseProto;
  }
  
}