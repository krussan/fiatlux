#!/bin/sh
set -ex

if [ ! -d protobuf-2.4.1 ]; then
   wget https://github.com/google/protobuf/releases/download/v2.4.1/protobuf-2.4.1.tar.gz
   tar -xzvf protobuf-2.4.1.tar.gz
   cd protobuf-2.4.1 && ./configure --prefix=/usr && make 
fi

sudo make install


