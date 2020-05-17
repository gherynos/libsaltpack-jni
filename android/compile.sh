#!/bin/bash

NDK_VER=r20b
NDK_PLATFORM_NUM=21
export NDK_PLATFORM=android-$NDK_PLATFORM_NUM
LIBSODIUM_VER=1.0.18
MSGPACK_VER=2.1.5
LIBSALTPACK_VER=v0.3.0

PT=/opt/libsaltpack-jni/android/tmp
cd $PT || exit

apt update
apt install -y git zip unzip wget build-essential autoconf libtool python openjdk-8-jdk maven

# Android NDK
echo "Android NDK"
ARCH=$(uname -m)
if [ "$ARCH" != "x86_64" ]; then
  ARCH="x86"
fi
if [ ! -e "$PT/android-ndk-$NDK_VER" ]
then
    if [ ! -e "$PT/android-ndk-$NDK_VER-linux-$ARCH.zip" ]
    then
        wget https://dl.google.com/android/repository/android-ndk-$NDK_VER-linux-$ARCH.zip
    fi
    unzip -o android-ndk-$NDK_VER-linux-$ARCH.zip
fi
export ANDROID_NDK_HOME=$PT/android-ndk-$NDK_VER

# LibSodium
echo "LibSodium"
if [ ! -e "$PT/libsodium-$LIBSODIUM_VER" ]
then
    if [ ! -e "$PT/libsodium-$LIBSODIUM_VER.tar.gz" ]
    then
        wget https://github.com/jedisct1/libsodium/releases/download/$LIBSODIUM_VER-RELEASE/libsodium-$LIBSODIUM_VER.tar.gz -O libsodium-$LIBSODIUM_VER.tar.gz
    fi
    tar -xvzf libsodium-$LIBSODIUM_VER.tar.gz
    cd libsodium-$LIBSODIUM_VER || exit
    ./autogen.sh
    ./dist-build/android-armv7-a.sh
    ./dist-build/android-armv8-a.sh
    ./dist-build/android-x86.sh
    ./dist-build/android-x86_64.sh
    cd $PT || exit
fi
export LIBSODIUM_PATH=$PT/libsodium-$LIBSODIUM_VER

# MsgPack
echo "MsgPack"
if [ ! -e "$PT/msgpack-$MSGPACK_VER" ]
then
    if [ ! -e "$PT/msgpack-$MSGPACK_VER.tar.gz" ]
    then
        wget https://github.com/msgpack/msgpack-c/releases/download/cpp-$MSGPACK_VER/msgpack-$MSGPACK_VER.tar.gz
    fi
    tar -xvzf msgpack-$MSGPACK_VER.tar.gz
fi
export MSGPACK_PATH=$PT/msgpack-$MSGPACK_VER

# LibSaltpack
echo "LibSaltpack"
if [ ! -e "$PT/libsaltpack" ]
then
    git clone https://github.com/gherynos/libsaltpack.git
    cd libsaltpack || exit
    git checkout $LIBSALTPACK_VER
    git submodule init
    git submodule update
    mkdir android
    cp -r $PT/../sp android/jni
    cd android || exit
    $ANDROID_NDK_HOME/ndk-build
    cd $PT || exit
fi
export LIBSALTPACK_PATH=$PT/libsaltpack

# LibSaltpack-jni
echo "LibSaltpack-jni"
cd /opt/libsaltpack-jni/android || exit
printf "\nAPP_PLATFORM := %s\n" $NDK_PLATFORM  >> ./jni/Application.mk
if [ -e "../src/main/resources/lib" ]
then
    rm -Rf ../src/main/resources/lib
fi
$ANDROID_NDK_HOME/ndk-build
mv ./libs ../src/main/resources/lib
cd /opt/libsaltpack-jni || exit
mvn clean
mvn package -DskipTests
cd $PT || exit
