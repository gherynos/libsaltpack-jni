/*
 * Copyright 2016-2024 Luca Zanconato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef LIBSALTPACK_JNI_INPUTSTREAMWRAPPER_H
#define LIBSALTPACK_JNI_INPUTSTREAMWRAPPER_H

#include <jni.h>
#include <iostream>

class InputStreamWrapper : public std::istream, std::streambuf {

public:

    InputStreamWrapper(JNIEnv *env, jobject inputStream);

    ~InputStreamWrapper() override;

    int underflow() override;

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
