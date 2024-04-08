#!/usr/bin/env bash

cd src/main/cpp/include
javah -cp ../../java com.gherynos.libsaltpack.MessageReader
javah -cp ../../java com.gherynos.libsaltpack.MessageWriter
javah -cp ../../java com.gherynos.libsaltpack.Utils
cd ../../../
