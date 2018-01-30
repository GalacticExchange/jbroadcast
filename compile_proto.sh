#!/usr/bin/env bash

SCRIPT_PATH=$(dirname "$0")

#echo ${SCRIPT_PATH}

cd ${SCRIPT_PATH}/src/main/java
#protoc -I=. --java_out=. ${SCRIPT_PATH}/src/main/java/protobuf/MessagePacket.proto

protoc -I=. --java_out=. ./protobuf/MessagePacket.proto