#!/usr/bin/env bash

set -e
set -x

# install java
apt update
apt install -y python-software-properties debconf-utils

add-apt-repository -y ppa:webupd8team/java
apt update

echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections

apt install -y oracle-java8-installer

# install maven
apt install -y maven

# work with project
git clone https://github.com/GalacticExchange/reliable_broadcast.git
cd reliable_broadcast
mvn clean install

sysctl -w net.core.rmem_default=31299200

#echo "java -jar target/reliable_broadcast-1.0-SNAPSHOT.jar"