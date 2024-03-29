// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: EventReferral.proto

package com.lvl6.mobsters.eventprotos;

public final class EventReferralProto {
  private EventReferralProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface ReferralCodeUsedResponseProtoOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // optional .proto.MinimumUserProto sender = 1;
    boolean hasSender();
    com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto getSender();
    com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder getSenderOrBuilder();
    
    // optional .proto.MinimumUserProto referredPlayer = 2;
    boolean hasReferredPlayer();
    com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto getReferredPlayer();
    com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder getReferredPlayerOrBuilder();
    
    // optional int32 cashGivenToReferrer = 3;
    boolean hasCashGivenToReferrer();
    int getCashGivenToReferrer();
  }
  public static final class ReferralCodeUsedResponseProto extends
      com.google.protobuf.GeneratedMessage
      implements ReferralCodeUsedResponseProtoOrBuilder {
    // Use ReferralCodeUsedResponseProto.newBuilder() to construct.
    private ReferralCodeUsedResponseProto(Builder builder) {
      super(builder);
    }
    private ReferralCodeUsedResponseProto(boolean noInit) {}
    
    private static final ReferralCodeUsedResponseProto defaultInstance;
    public static ReferralCodeUsedResponseProto getDefaultInstance() {
      return defaultInstance;
    }
    
    public ReferralCodeUsedResponseProto getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.lvl6.mobsters.eventprotos.EventReferralProto.internal_static_proto_ReferralCodeUsedResponseProto_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.lvl6.mobsters.eventprotos.EventReferralProto.internal_static_proto_ReferralCodeUsedResponseProto_fieldAccessorTable;
    }
    
    private int bitField0_;
    // optional .proto.MinimumUserProto sender = 1;
    public static final int SENDER_FIELD_NUMBER = 1;
    private com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto sender_;
    public boolean hasSender() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto getSender() {
      return sender_;
    }
    public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder getSenderOrBuilder() {
      return sender_;
    }
    
    // optional .proto.MinimumUserProto referredPlayer = 2;
    public static final int REFERREDPLAYER_FIELD_NUMBER = 2;
    private com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto referredPlayer_;
    public boolean hasReferredPlayer() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto getReferredPlayer() {
      return referredPlayer_;
    }
    public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder getReferredPlayerOrBuilder() {
      return referredPlayer_;
    }
    
    // optional int32 cashGivenToReferrer = 3;
    public static final int CASHGIVENTOREFERRER_FIELD_NUMBER = 3;
    private int cashGivenToReferrer_;
    public boolean hasCashGivenToReferrer() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    public int getCashGivenToReferrer() {
      return cashGivenToReferrer_;
    }
    
    private void initFields() {
      sender_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance();
      referredPlayer_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance();
      cashGivenToReferrer_ = 0;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeMessage(1, sender_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeMessage(2, referredPlayer_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt32(3, cashGivenToReferrer_);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, sender_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, referredPlayer_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, cashGivenToReferrer_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProtoOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.lvl6.mobsters.eventprotos.EventReferralProto.internal_static_proto_ReferralCodeUsedResponseProto_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.lvl6.mobsters.eventprotos.EventReferralProto.internal_static_proto_ReferralCodeUsedResponseProto_fieldAccessorTable;
      }
      
      // Construct using com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
          getSenderFieldBuilder();
          getReferredPlayerFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        if (senderBuilder_ == null) {
          sender_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance();
        } else {
          senderBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        if (referredPlayerBuilder_ == null) {
          referredPlayer_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance();
        } else {
          referredPlayerBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        cashGivenToReferrer_ = 0;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto.getDescriptor();
      }
      
      public com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto getDefaultInstanceForType() {
        return com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto.getDefaultInstance();
      }
      
      public com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto build() {
        com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto buildPartial() {
        com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto result = new com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        if (senderBuilder_ == null) {
          result.sender_ = sender_;
        } else {
          result.sender_ = senderBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        if (referredPlayerBuilder_ == null) {
          result.referredPlayer_ = referredPlayer_;
        } else {
          result.referredPlayer_ = referredPlayerBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.cashGivenToReferrer_ = cashGivenToReferrer_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto) {
          return mergeFrom((com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto other) {
        if (other == com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto.getDefaultInstance()) return this;
        if (other.hasSender()) {
          mergeSender(other.getSender());
        }
        if (other.hasReferredPlayer()) {
          mergeReferredPlayer(other.getReferredPlayer());
        }
        if (other.hasCashGivenToReferrer()) {
          setCashGivenToReferrer(other.getCashGivenToReferrer());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public final boolean isInitialized() {
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              onChanged();
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                onChanged();
                return this;
              }
              break;
            }
            case 10: {
              com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder subBuilder = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.newBuilder();
              if (hasSender()) {
                subBuilder.mergeFrom(getSender());
              }
              input.readMessage(subBuilder, extensionRegistry);
              setSender(subBuilder.buildPartial());
              break;
            }
            case 18: {
              com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder subBuilder = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.newBuilder();
              if (hasReferredPlayer()) {
                subBuilder.mergeFrom(getReferredPlayer());
              }
              input.readMessage(subBuilder, extensionRegistry);
              setReferredPlayer(subBuilder.buildPartial());
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              cashGivenToReferrer_ = input.readInt32();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // optional .proto.MinimumUserProto sender = 1;
      private com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto sender_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance();
      private com.google.protobuf.SingleFieldBuilder<
          com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder> senderBuilder_;
      public boolean hasSender() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto getSender() {
        if (senderBuilder_ == null) {
          return sender_;
        } else {
          return senderBuilder_.getMessage();
        }
      }
      public Builder setSender(com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto value) {
        if (senderBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          sender_ = value;
          onChanged();
        } else {
          senderBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      public Builder setSender(
          com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder builderForValue) {
        if (senderBuilder_ == null) {
          sender_ = builderForValue.build();
          onChanged();
        } else {
          senderBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      public Builder mergeSender(com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto value) {
        if (senderBuilder_ == null) {
          if (((bitField0_ & 0x00000001) == 0x00000001) &&
              sender_ != com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance()) {
            sender_ =
              com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.newBuilder(sender_).mergeFrom(value).buildPartial();
          } else {
            sender_ = value;
          }
          onChanged();
        } else {
          senderBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      public Builder clearSender() {
        if (senderBuilder_ == null) {
          sender_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance();
          onChanged();
        } else {
          senderBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder getSenderBuilder() {
        bitField0_ |= 0x00000001;
        onChanged();
        return getSenderFieldBuilder().getBuilder();
      }
      public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder getSenderOrBuilder() {
        if (senderBuilder_ != null) {
          return senderBuilder_.getMessageOrBuilder();
        } else {
          return sender_;
        }
      }
      private com.google.protobuf.SingleFieldBuilder<
          com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder> 
          getSenderFieldBuilder() {
        if (senderBuilder_ == null) {
          senderBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder>(
                  sender_,
                  getParentForChildren(),
                  isClean());
          sender_ = null;
        }
        return senderBuilder_;
      }
      
      // optional .proto.MinimumUserProto referredPlayer = 2;
      private com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto referredPlayer_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance();
      private com.google.protobuf.SingleFieldBuilder<
          com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder> referredPlayerBuilder_;
      public boolean hasReferredPlayer() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto getReferredPlayer() {
        if (referredPlayerBuilder_ == null) {
          return referredPlayer_;
        } else {
          return referredPlayerBuilder_.getMessage();
        }
      }
      public Builder setReferredPlayer(com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto value) {
        if (referredPlayerBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          referredPlayer_ = value;
          onChanged();
        } else {
          referredPlayerBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      public Builder setReferredPlayer(
          com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder builderForValue) {
        if (referredPlayerBuilder_ == null) {
          referredPlayer_ = builderForValue.build();
          onChanged();
        } else {
          referredPlayerBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      public Builder mergeReferredPlayer(com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto value) {
        if (referredPlayerBuilder_ == null) {
          if (((bitField0_ & 0x00000002) == 0x00000002) &&
              referredPlayer_ != com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance()) {
            referredPlayer_ =
              com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.newBuilder(referredPlayer_).mergeFrom(value).buildPartial();
          } else {
            referredPlayer_ = value;
          }
          onChanged();
        } else {
          referredPlayerBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      public Builder clearReferredPlayer() {
        if (referredPlayerBuilder_ == null) {
          referredPlayer_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.getDefaultInstance();
          onChanged();
        } else {
          referredPlayerBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }
      public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder getReferredPlayerBuilder() {
        bitField0_ |= 0x00000002;
        onChanged();
        return getReferredPlayerFieldBuilder().getBuilder();
      }
      public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder getReferredPlayerOrBuilder() {
        if (referredPlayerBuilder_ != null) {
          return referredPlayerBuilder_.getMessageOrBuilder();
        } else {
          return referredPlayer_;
        }
      }
      private com.google.protobuf.SingleFieldBuilder<
          com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder> 
          getReferredPlayerFieldBuilder() {
        if (referredPlayerBuilder_ == null) {
          referredPlayerBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto.Builder, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoOrBuilder>(
                  referredPlayer_,
                  getParentForChildren(),
                  isClean());
          referredPlayer_ = null;
        }
        return referredPlayerBuilder_;
      }
      
      // optional int32 cashGivenToReferrer = 3;
      private int cashGivenToReferrer_ ;
      public boolean hasCashGivenToReferrer() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      public int getCashGivenToReferrer() {
        return cashGivenToReferrer_;
      }
      public Builder setCashGivenToReferrer(int value) {
        bitField0_ |= 0x00000004;
        cashGivenToReferrer_ = value;
        onChanged();
        return this;
      }
      public Builder clearCashGivenToReferrer() {
        bitField0_ = (bitField0_ & ~0x00000004);
        cashGivenToReferrer_ = 0;
        onChanged();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:proto.ReferralCodeUsedResponseProto)
    }
    
    static {
      defaultInstance = new ReferralCodeUsedResponseProto(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:proto.ReferralCodeUsedResponseProto)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_proto_ReferralCodeUsedResponseProto_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_proto_ReferralCodeUsedResponseProto_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023EventReferral.proto\022\005proto\032\nUser.proto" +
      "\"\226\001\n\035ReferralCodeUsedResponseProto\022\'\n\006se" +
      "nder\030\001 \001(\0132\027.proto.MinimumUserProto\022/\n\016r" +
      "eferredPlayer\030\002 \001(\0132\027.proto.MinimumUserP" +
      "roto\022\033\n\023cashGivenToReferrer\030\003 \001(\005B3\n\035com" +
      ".lvl6.mobsters.eventprotosB\022EventReferra" +
      "lProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_proto_ReferralCodeUsedResponseProto_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_proto_ReferralCodeUsedResponseProto_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_proto_ReferralCodeUsedResponseProto_descriptor,
              new java.lang.String[] { "Sender", "ReferredPlayer", "CashGivenToReferrer", },
              com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto.class,
              com.lvl6.mobsters.eventprotos.EventReferralProto.ReferralCodeUsedResponseProto.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.lvl6.mobsters.noneventprotos.UserProto.getDescriptor(),
        }, assigner);
  }
  
  // @@protoc_insertion_point(outer_class_scope)
}
