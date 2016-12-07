#!/usr/bin/env bash

cd src/main/cpp/include
javah -cp ../../java net.nharyes.libsaltpack.MessageReader
javah -cp ../../java net.nharyes.libsaltpack.MessageWriter
javah -cp ../../java net.nharyes.libsaltpack.Utils
cd ../../../
