//
// Created by Gherynos on 20/11/2016.
//

#ifndef LIBSALTPACK_JNI_OUTPUTSTREAMWRAPPER_H
#define LIBSALTPACK_JNI_OUTPUTSTREAMWRAPPER_H

#include <jni.h>
#include <iostream>
#include <saltpack/types.h>

class OutputStreamWrapper : public std::ostream, std::streambuf {

public:
    OutputStreamWrapper(JNIEnv *env, jobject outputStream);

    virtual ~OutputStreamWrapper();

    virtual int overflow(int __c) override;

    void finalise();

private:
    JNIEnv *env;
    jobject outputStream;
    jmethodID mWrite;

    saltpack::BYTE_ARRAY buf;
    int count;

    void writeToOutput();
};

#endif //LIBSALTPACK_JNI_OUTPUTSTREAMWRAPPER_H
