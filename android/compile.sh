#!/bin/bash

NDK_VER=r20b
NDK_PLATFORM_NUM=21
export NDK_PLATFORM=android-$NDK_PLATFORM_NUM
LIBSODIUM_VER=1.0.18
LIBGMP_VER=6.1.2
MSGPACK_VER=2.1.5
LIBSALTPACK_VER=0.2.4

PT=/opt/libsaltpack-jni/android/tmp
cd $PT || exit

apt-get update
apt-get install -y git zip unzip wget build-essential autoconf libtool python

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

# LibGMP
echo "LibGMP"
if [ ! -e "$PT/gmp-$LIBGMP_VER" ]
then
    if [ ! -e "$PT/gmp-$LIBGMP_VER.tar.bz2" ]
    then
        wget https://gmplib.org/download/gmp/gmp-$LIBGMP_VER.tar.bz2
    fi
    tar -xvjf gmp-$LIBGMP_VER.tar.bz2
    cd gmp-$LIBGMP_VER || exit
    ARCH=("armv7-a" "armv8-a" "i686" "westmere")
    HOST=("armv7a-linux-androideabi" "aarch64-linux-android" "i686-linux-android" "x86_64-linux-android")
    for i in $(seq 0 3);
    do
        arch=${ARCH[i]}
        host=${HOST[i]}
        if [ "$host" == "armv7a-linux-androideabi" ]
        then
            host_ar="arm-linux-androideabi"
        else
            host_ar="$host"
        fi
        toolchain=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64

        mkdir android-$arch
        cd android-$arch || exit

        PATH=$toolchain/bin:$PATH
        SYSROOT=$toolchain/sysroot
        export CC="$host$NDK_PLATFORM_NUM-clang --sysroot $SYSROOT"
        export CXX="$host$NDK_PLATFORM_NUM-clang++ --sysroot $SYSROOT"
        export AR="$toolchain/bin/$host_ar-ar"
        export RANLIB="$toolchain/bin/$host_ar-ranlib"

        $PT/gmp-$LIBGMP_VER/./configure --host="$host" --disable-assembly --enable-cxx --with-pic --prefix=$PT/gmp-$LIBGMP_VER/libgmp-android-$arch
        make
        make install
        cd $PT/gmp-$LIBGMP_VER || exit
    done
    cd $PT || exit
fi
export LIBGMP_PATH=$PT/gmp-$LIBGMP_VER

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
if [ ! -e "$PT/libsaltpack-$LIBSALTPACK_VER" ]
then
    git clone https://github.com/Gherynos/libsaltpack.git
    if [ ! -e "$PT/libsaltpack-$LIBSALTPACK_VER.tar.gz" ]
    then
        wget -O $PT/libsaltpack-$LIBSALTPACK_VER.tar.gz https://github.com/gherynos/libsaltpack/archive/v$LIBSALTPACK_VER.tar.gz
    fi
    tar -xvzf libsaltpack-$LIBSALTPACK_VER.tar.gz
    cd libsaltpack-$LIBSALTPACK_VER || exit
    mkdir android
    cp -r $PT/../sp android/jni
    cd android || exit
    $ANDROID_NDK_HOME/ndk-build
    cd $PT || exit
fi
export LIBSALTPACK_PATH=$PT/libsaltpack-$LIBSALTPACK_VER

# LibSaltpack-jni
echo "LibSaltpack-jni"
cd /opt/libsaltpack-jni/android || exit
printf "\nAPP_PLATFORM := %s\n" $NDK_PLATFORM  >> ./jni/Application.mk
if [ -e "libsaltpack-jni-libs.jar" ]
then
    rm -Rf ./lib
    rm -Rf ./libs
    rm ./libsaltpack-jni-libs.jar
fi
$ANDROID_NDK_HOME/ndk-build
mv ./libs ./lib
zip -r libsaltpack-jni-libs.jar ./lib -x "*.DS_Store"
cd $PT || exit
