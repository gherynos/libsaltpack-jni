//
// Created by Gherynos on 14/11/2016.
//

#ifndef LIBSALTPACK_JNI_INPUTSTREAMWRAPPER_H
#define LIBSALTPACK_JNI_INPUTSTREAMWRAPPER_H

#include <jni.h>
#include <iostream>

class InputStreamWrapper : public std::istream, std::streambuf {

public:

    InputStreamWrapper(JNIEnv *env, jobject inputStream);

    virtual ~InputStreamWrapper();

    virtual int underflow() override;

private:
    JNIEnv *env;
    jobject inputStream;
    jmethodID mRead;

    jbyteArray buf;
    unsigned char *data;
    jint dataSize;
    int index;
    char ch;
    bool dataReady;
};

#endif //LIBSALTPACK_JNI_INPUTSTREAMWRAPPER_H
