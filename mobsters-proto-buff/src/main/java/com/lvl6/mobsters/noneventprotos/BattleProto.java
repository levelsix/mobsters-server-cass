// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Battle.proto

package com.lvl6.mobsters.noneventprotos;

public final class BattleProto {
  private BattleProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public enum BattleResult
      implements com.google.protobuf.ProtocolMessageEnum {
    ATTACKER_WIN(0, 1),
    DEFENDER_WIN(1, 2),
    ATTACKER_FLEE(2, 3),
    ;
    
    public static final int ATTACKER_WIN_VALUE = 1;
    public static final int DEFENDER_WIN_VALUE = 2;
    public static final int ATTACKER_FLEE_VALUE = 3;
    
    
    public final int getNumber() { return value; }
    
    public static BattleResult valueOf(int value) {
      switch (value) {
        case 1: return ATTACKER_WIN;
        case 2: return DEFENDER_WIN;
        case 3: return ATTACKER_FLEE;
        default: return null;
      }
    }
    
    public static com.google.protobuf.Internal.EnumLiteMap<BattleResult>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<BattleResult>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<BattleResult>() {
            public BattleResult findValueByNumber(int number) {
              return BattleResult.valueOf(number);
            }
          };
    
    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.lvl6.mobsters.noneventprotos.BattleProto.getDescriptor().getEnumTypes().get(0);
    }
    
    private static final BattleResult[] VALUES = {
      ATTACKER_WIN, DEFENDER_WIN, ATTACKER_FLEE, 
    };
    
    public static BattleResult valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }
    
    private final int index;
    private final int value;
    
    private BattleResult(int index, int value) {
      this.index = index;
      this.value = value;
    }
    
    // @@protoc_insertion_point(enum_scope:proto.BattleResult)
  }
  
  public interface MinimumUserProtoWithBattleHistoryOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // optional .proto.MinimumUserProtoWithLevel minUserProtoWithLevel = 1;
    boolean hasMinUserProtoWithLevel();
    com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel getMinUserProtoWithLevel();
    com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevelOrBuilder getMinUserProtoWithLevelOrBuilder();
    
    // optional int32 battlesWon = 2;
    boolean hasBattlesWon();
    int getBattlesWon();
    
    // optional int32 battlesLost = 3;
    boolean hasBattlesLost();
    int getBattlesLost();
    
    // optional int32 battlesFled = 4;
    boolean hasBattlesFled();
    int getBattlesFled();
  }
  public static final class MinimumUserProtoWithBattleHistory extends
      com.google.protobuf.GeneratedMessage
      implements MinimumUserProtoWithBattleHistoryOrBuilder {
    // Use MinimumUserProtoWithBattleHistory.newBuilder() to construct.
    private MinimumUserProtoWithBattleHistory(Builder builder) {
      super(builder);
    }
    private MinimumUserProtoWithBattleHistory(boolean noInit) {}
    
    private static final MinimumUserProtoWithBattleHistory defaultInstance;
    public static MinimumUserProtoWithBattleHistory getDefaultInstance() {
      return defaultInstance;
    }
    
    public MinimumUserProtoWithBattleHistory getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.lvl6.mobsters.noneventprotos.BattleProto.internal_static_proto_MinimumUserProtoWithBattleHistory_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.lvl6.mobsters.noneventprotos.BattleProto.internal_static_proto_MinimumUserProtoWithBattleHistory_fieldAccessorTable;
    }
    
    private int bitField0_;
    // optional .proto.MinimumUserProtoWithLevel minUserProtoWithLevel = 1;
    public static final int MINUSERPROTOWITHLEVEL_FIELD_NUMBER = 1;
    private com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel minUserProtoWithLevel_;
    public boolean hasMinUserProtoWithLevel() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel getMinUserProtoWithLevel() {
      return minUserProtoWithLevel_;
    }
    public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevelOrBuilder getMinUserProtoWithLevelOrBuilder() {
      return minUserProtoWithLevel_;
    }
    
    // optional int32 battlesWon = 2;
    public static final int BATTLESWON_FIELD_NUMBER = 2;
    private int battlesWon_;
    public boolean hasBattlesWon() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    public int getBattlesWon() {
      return battlesWon_;
    }
    
    // optional int32 battlesLost = 3;
    public static final int BATTLESLOST_FIELD_NUMBER = 3;
    private int battlesLost_;
    public boolean hasBattlesLost() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    public int getBattlesLost() {
      return battlesLost_;
    }
    
    // optional int32 battlesFled = 4;
    public static final int BATTLESFLED_FIELD_NUMBER = 4;
    private int battlesFled_;
    public boolean hasBattlesFled() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    public int getBattlesFled() {
      return battlesFled_;
    }
    
    private void initFields() {
      minUserProtoWithLevel_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.getDefaultInstance();
      battlesWon_ = 0;
      battlesLost_ = 0;
      battlesFled_ = 0;
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
        output.writeMessage(1, minUserProtoWithLevel_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt32(2, battlesWon_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt32(3, battlesLost_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeInt32(4, battlesFled_);
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
          .computeMessageSize(1, minUserProtoWithLevel_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, battlesWon_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, battlesLost_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, battlesFled_);
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
    
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseDelimitedFrom(
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
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory prototype) {
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
       implements com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistoryOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.lvl6.mobsters.noneventprotos.BattleProto.internal_static_proto_MinimumUserProtoWithBattleHistory_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.lvl6.mobsters.noneventprotos.BattleProto.internal_static_proto_MinimumUserProtoWithBattleHistory_fieldAccessorTable;
      }
      
      // Construct using com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
          getMinUserProtoWithLevelFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        if (minUserProtoWithLevelBuilder_ == null) {
          minUserProtoWithLevel_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.getDefaultInstance();
        } else {
          minUserProtoWithLevelBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        battlesWon_ = 0;
        bitField0_ = (bitField0_ & ~0x00000002);
        battlesLost_ = 0;
        bitField0_ = (bitField0_ & ~0x00000004);
        battlesFled_ = 0;
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory.getDescriptor();
      }
      
      public com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory getDefaultInstanceForType() {
        return com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory.getDefaultInstance();
      }
      
      public com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory build() {
        com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory buildPartial() {
        com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory result = new com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        if (minUserProtoWithLevelBuilder_ == null) {
          result.minUserProtoWithLevel_ = minUserProtoWithLevel_;
        } else {
          result.minUserProtoWithLevel_ = minUserProtoWithLevelBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.battlesWon_ = battlesWon_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.battlesLost_ = battlesLost_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.battlesFled_ = battlesFled_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory) {
          return mergeFrom((com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory other) {
        if (other == com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory.getDefaultInstance()) return this;
        if (other.hasMinUserProtoWithLevel()) {
          mergeMinUserProtoWithLevel(other.getMinUserProtoWithLevel());
        }
        if (other.hasBattlesWon()) {
          setBattlesWon(other.getBattlesWon());
        }
        if (other.hasBattlesLost()) {
          setBattlesLost(other.getBattlesLost());
        }
        if (other.hasBattlesFled()) {
          setBattlesFled(other.getBattlesFled());
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
              com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.Builder subBuilder = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.newBuilder();
              if (hasMinUserProtoWithLevel()) {
                subBuilder.mergeFrom(getMinUserProtoWithLevel());
              }
              input.readMessage(subBuilder, extensionRegistry);
              setMinUserProtoWithLevel(subBuilder.buildPartial());
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              battlesWon_ = input.readInt32();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              battlesLost_ = input.readInt32();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              battlesFled_ = input.readInt32();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // optional .proto.MinimumUserProtoWithLevel minUserProtoWithLevel = 1;
      private com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel minUserProtoWithLevel_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.getDefaultInstance();
      private com.google.protobuf.SingleFieldBuilder<
          com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.Builder, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevelOrBuilder> minUserProtoWithLevelBuilder_;
      public boolean hasMinUserProtoWithLevel() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel getMinUserProtoWithLevel() {
        if (minUserProtoWithLevelBuilder_ == null) {
          return minUserProtoWithLevel_;
        } else {
          return minUserProtoWithLevelBuilder_.getMessage();
        }
      }
      public Builder setMinUserProtoWithLevel(com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel value) {
        if (minUserProtoWithLevelBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          minUserProtoWithLevel_ = value;
          onChanged();
        } else {
          minUserProtoWithLevelBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      public Builder setMinUserProtoWithLevel(
          com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.Builder builderForValue) {
        if (minUserProtoWithLevelBuilder_ == null) {
          minUserProtoWithLevel_ = builderForValue.build();
          onChanged();
        } else {
          minUserProtoWithLevelBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      public Builder mergeMinUserProtoWithLevel(com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel value) {
        if (minUserProtoWithLevelBuilder_ == null) {
          if (((bitField0_ & 0x00000001) == 0x00000001) &&
              minUserProtoWithLevel_ != com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.getDefaultInstance()) {
            minUserProtoWithLevel_ =
              com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.newBuilder(minUserProtoWithLevel_).mergeFrom(value).buildPartial();
          } else {
            minUserProtoWithLevel_ = value;
          }
          onChanged();
        } else {
          minUserProtoWithLevelBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      public Builder clearMinUserProtoWithLevel() {
        if (minUserProtoWithLevelBuilder_ == null) {
          minUserProtoWithLevel_ = com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.getDefaultInstance();
          onChanged();
        } else {
          minUserProtoWithLevelBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.Builder getMinUserProtoWithLevelBuilder() {
        bitField0_ |= 0x00000001;
        onChanged();
        return getMinUserProtoWithLevelFieldBuilder().getBuilder();
      }
      public com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevelOrBuilder getMinUserProtoWithLevelOrBuilder() {
        if (minUserProtoWithLevelBuilder_ != null) {
          return minUserProtoWithLevelBuilder_.getMessageOrBuilder();
        } else {
          return minUserProtoWithLevel_;
        }
      }
      private com.google.protobuf.SingleFieldBuilder<
          com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.Builder, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevelOrBuilder> 
          getMinUserProtoWithLevelFieldBuilder() {
        if (minUserProtoWithLevelBuilder_ == null) {
          minUserProtoWithLevelBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevel.Builder, com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithLevelOrBuilder>(
                  minUserProtoWithLevel_,
                  getParentForChildren(),
                  isClean());
          minUserProtoWithLevel_ = null;
        }
        return minUserProtoWithLevelBuilder_;
      }
      
      // optional int32 battlesWon = 2;
      private int battlesWon_ ;
      public boolean hasBattlesWon() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      public int getBattlesWon() {
        return battlesWon_;
      }
      public Builder setBattlesWon(int value) {
        bitField0_ |= 0x00000002;
        battlesWon_ = value;
        onChanged();
        return this;
      }
      public Builder clearBattlesWon() {
        bitField0_ = (bitField0_ & ~0x00000002);
        battlesWon_ = 0;
        onChanged();
        return this;
      }
      
      // optional int32 battlesLost = 3;
      private int battlesLost_ ;
      public boolean hasBattlesLost() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      public int getBattlesLost() {
        return battlesLost_;
      }
      public Builder setBattlesLost(int value) {
        bitField0_ |= 0x00000004;
        battlesLost_ = value;
        onChanged();
        return this;
      }
      public Builder clearBattlesLost() {
        bitField0_ = (bitField0_ & ~0x00000004);
        battlesLost_ = 0;
        onChanged();
        return this;
      }
      
      // optional int32 battlesFled = 4;
      private int battlesFled_ ;
      public boolean hasBattlesFled() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      public int getBattlesFled() {
        return battlesFled_;
      }
      public Builder setBattlesFled(int value) {
        bitField0_ |= 0x00000008;
        battlesFled_ = value;
        onChanged();
        return this;
      }
      public Builder clearBattlesFled() {
        bitField0_ = (bitField0_ & ~0x00000008);
        battlesFled_ = 0;
        onChanged();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:proto.MinimumUserProtoWithBattleHistory)
    }
    
    static {
      defaultInstance = new MinimumUserProtoWithBattleHistory(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:proto.MinimumUserProtoWithBattleHistory)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_proto_MinimumUserProtoWithBattleHistory_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_proto_MinimumUserProtoWithBattleHistory_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\014Battle.proto\022\005proto\032\nUser.proto\"\242\001\n!Mi" +
      "nimumUserProtoWithBattleHistory\022?\n\025minUs" +
      "erProtoWithLevel\030\001 \001(\0132 .proto.MinimumUs" +
      "erProtoWithLevel\022\022\n\nbattlesWon\030\002 \001(\005\022\023\n\013" +
      "battlesLost\030\003 \001(\005\022\023\n\013battlesFled\030\004 \001(\005*E" +
      "\n\014BattleResult\022\020\n\014ATTACKER_WIN\020\001\022\020\n\014DEFE" +
      "NDER_WIN\020\002\022\021\n\rATTACKER_FLEE\020\003B/\n com.lvl" +
      "6.mobsters.noneventprotosB\013BattleProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_proto_MinimumUserProtoWithBattleHistory_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_proto_MinimumUserProtoWithBattleHistory_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_proto_MinimumUserProtoWithBattleHistory_descriptor,
              new java.lang.String[] { "MinUserProtoWithLevel", "BattlesWon", "BattlesLost", "BattlesFled", },
              com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory.class,
              com.lvl6.mobsters.noneventprotos.BattleProto.MinimumUserProtoWithBattleHistory.Builder.class);
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
