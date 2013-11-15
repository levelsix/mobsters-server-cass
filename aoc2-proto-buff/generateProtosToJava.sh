#!/bin/bash

SRC_DIR=src/main/protos
DST_DIR=src/main/java

echo Generating protos to Java

for file in $SRC_DIR/*.proto;
do

protoc -I=$SRC_DIR --java_out=$DST_DIR $file

done

echo Done generating protos to Java
