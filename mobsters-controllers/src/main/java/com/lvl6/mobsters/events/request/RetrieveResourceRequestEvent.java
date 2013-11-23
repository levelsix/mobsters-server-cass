//package com.lvl6.mobsters.events.request;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.lvl6.mobsters.eventprotos.RetrieveResourceEventProto.RetrieveResourceRequestProto;
//import com.lvl6.mobsters.events.RequestEvent;
//
//public class RetrieveResourceRequestEvent extends RequestEvent {
//  
//  private RetrieveResourceRequestProto retrieveResourceRequestProto;
//
//  @Override
//  public void read(ByteBuffer bb) {
//    try {
//      retrieveResourceRequestProto = RetrieveResourceRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = retrieveResourceRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
//  }
//  
//  public RetrieveResourceRequestProto getRetrieveResourceRequestProto() {
//    return retrieveResourceRequestProto;
//  }
//  
//}