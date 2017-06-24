#!/bin/bash

NDK_VER=r12b
LIBSODIUM_VER=1.0.11
LIBGMP_VER=6.1.2
MSGPACK_VER=2.1.1

PT=/opt/libsaltpack-jni/android/tmp
cd $PT

apt-get update
apt-get install -y git zip unzip wget build-essential autoconf libtool

# Android NDK
echo "Android NDK"
ARCH=$(uname -m)
if [ $ARCH != "x86_64" ]; then
  $ARCH = "x86"
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
        wget https://download.libsodium.org/libsodium/releases/libsodium-$LIBSODIUM_VER.tar.gz
    fi
    tar -xvzf libsodium-$LIBSODIUM_VER.tar.gz
    cd libsodium-$LIBSODIUM_VER
    ./autogen.sh
    ./dist-build/android-arm.sh
    ./dist-build/android-armv7-a.sh
    ./dist-build/android-armv8-a.sh
    ./dist-build/android-mips32.sh
    ./dist-build/android-mips64.sh
    ./dist-build/android-x86.sh
    ./dist-build/android-x86_64.sh
    cd $PT
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
    cd gmp-$LIBGMP_VER
    ARCH=("armv6" "armv7-a" "armv8-a" "i686" "mips32" "mips64r6" "westmere")
    HOST=("arm-linux-androideabi" "arm-linux-androideabi" "aarch64-linux-android" "i686-linux-android" "mipsel-linux-android" "mips64el-linux-android" "x86_64-linux-android")
    for i in `seq 0 6`;
    do
        arch=${ARCH[i]}
        host=${HOST[i]}

        mkdir android-$arch
        cd android-$arch

        PATH=$PT/libsodium-$LIBSODIUM_VER/android-toolchain-$arch/bin:$PATH
        SYSROOT=$PT/libsodium-$LIBSODIUM_VER/android-toolchain-$arch/sysroot
        CC="$host-gcc --sysroot $SYSROOT"
        CXX="$host-g++ --sysroot $SYSROOT"
        AR="$PT/libsodium-$LIBSODIUM_VER/android-toolchain-$arch/bin/$host-ar"
        RANLIB="$PT/libsodium-$LIBSODIUM_VER/android-toolchain-$arch/bin/$host-ranlib"

        $PT/gmp-$LIBGMP_VER/./configure --host=$host --disable-assembly --enable-cxx --prefix=$PT/gmp-$LIBGMP_VER/libgmp-android-$arch
        make
        make install
    done
    cd $PT
fi
export LIBGMP_PATH=$PT/gmp-$LIBGMP_VER

## MsgPack
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
    git clone https://github.com/Gherynos/libsaltpack.git
    cd libsaltpack/android
    $ANDROID_NDK_HOME/ndk-build
    cd $PT
fi
export LIBSALTPACK_PATH=$PT/libsaltpack

# LibSaltpack-jni
echo "LibSaltpack-jni"
cd /opt/libsaltpack-jni/android
if [ -e "libsaltpack-jni-libs.jar" ]
then
    rm -Rf ./lib
    rm -Rf ./libs
    rm ./libsaltpack-jni-libs.jar
fi
$ANDROID_NDK_HOME/ndk-build
mv ./libs ./lib
zip -r libsaltpack-jni-libs.jar ./lib -x "*.DS_Store"
cd $PT