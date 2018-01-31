#!/usr/bin/env bash

set -e
set -x

SCRIPT_PATH=$(dirname "$0")

cd ${SCRIPT_PATH}/src/main/java

protoc -I=. --java_out=. ./protobuf/Fragment.proto

cd -

echo 'Compiled!'