# libsaltpack-jni

    mvn compile
    cd target
    cmake ../
    make
    cd ..
    mvn package

## Android

    docker run -v `pwd`:/opt/libsaltpack-jni -t ubuntu:16.04 /bin/bash /opt/libsaltpack-jni/android/compile.sh