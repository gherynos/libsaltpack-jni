#!/bin/bash
set -e

NDK_VER=r26c
NDK_PLATFORM_NUM=21
export NDK_PLATFORM=android-$NDK_PLATFORM_NUM
LIBSODIUM_VER=1.0.19
MSGPACK_VER=6.1.0
BOOST_VER=1.84.0
LIBSALTPACK_VER=v1.0.0

PT=/opt/libsaltpack-jni/android/tmp
cd $PT || exit

apt-get update
apt-get install -y git zip unzip wget build-essential autoconf libtool python3 openjdk-17-jdk maven

# Android NDK
echo "Android NDK"
if [ ! -e "$PT/android-ndk-$NDK_VER" ]
then
    if [ ! -e "$PT//android-ndk-$NDK_VER-linux.zip" ]
    then
        wget https://dl.google.com/android/repository/android-ndk-$NDK_VER-linux.zip
    fi
    unzip -o android-ndk-$NDK_VER-linux.zip
fi
export ANDROID_NDK_HOME=$PT/android-ndk-$NDK_VER

# LibSodium
echo "LibSodium"
if [ ! -e "$PT/libsodium-stable" ]
then
    if [ ! -e "$PT/libsodium-$LIBSODIUM_VER.tar.gz" ]
    then
        wget https://github.com/jedisct1/libsodium/releases/download/$LIBSODIUM_VER-RELEASE/libsodium-$LIBSODIUM_VER.tar.gz -O libsodium-$LIBSODIUM_VER.tar.gz
    fi
    tar -xvzf libsodium-$LIBSODIUM_VER.tar.gz
    cd libsodium-stable || exit
    ./autogen.sh
    ./dist-build/android-armv7-a.sh
    ./dist-build/android-armv8-a.sh
    ./dist-build/android-x86.sh
    ./dist-build/android-x86_64.sh
    cd $PT || exit
fi
export LIBSODIUM_PATH=$PT/libsodium-stable

# Boost
echo "Boost"
if [ ! -e "$PT/boost_${BOOST_VER//./_}" ]
then
  if [ ! -e "$PT/boost_${BOOST_VER//./_}.tar.gz" ]
  then
      wget https://boostorg.jfrog.io/artifactory/main/release/$BOOST_VER/source/boost_${BOOST_VER//./_}.tar.gz
  fi
  tar -xzf boost_${BOOST_VER//./_}.tar.gz
fi
export BOOST_PATH=$PT/boost_${BOOST_VER//./_}

# MsgPack
echo "MsgPack"
if [ ! -e "$PT/msgpack-cxx-$MSGPACK_VER" ]
then
    if [ ! -e "$PT/msgpack-cxx-$MSGPACK_VER.tar.gz" ]
    then
        wget https://github.com/msgpack/msgpack-c/releases/download/cpp-$MSGPACK_VER/msgpack-cxx-$MSGPACK_VER.tar.gz
    fi
    tar -xvzf msgpack-cxx-$MSGPACK_VER.tar.gz
fi
export MSGPACK_PATH=$PT/msgpack-cxx-$MSGPACK_VER

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
if [ -e "../src/main/resources/lib" ]
then
    rm -Rf ../src/main/resources/lib
fi
$ANDROID_NDK_HOME/ndk-build
mv ./libs ../src/main/resources/lib
cd /opt/libsaltpack-jni || exit
mvn package -DskipTests
cd $PT || exit
