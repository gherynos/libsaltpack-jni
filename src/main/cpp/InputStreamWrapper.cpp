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

#include "InputStreamWrapper.h"
#include "common.h"

const int BUF_SIZE = 2048;

InputStreamWrapper::InputStreamWrapper(JNIEnv *env, jobject inputStream) : std::istream(this) {

    this->env = env;
    this->inputStream = inputStream;

    jclass clazz = loadClass(env, "java/io/InputStream");
    mRead = loadMethod(env, clazz, "read", "([BII)I");

    dataReady = false;
    index = 0;
    dataSize = 0;

    jbyteArray local_jarray = env->NewByteArray(BUF_SIZE);
    if (local_jarray == nullptr) {

        return; /* out of memory error thrown */
    }
    buf = (jbyteArray) env->NewGlobalRef(local_jarray);
    env->DeleteLocalRef(local_jarray);
}

InputStreamWrapper::~InputStreamWrapper() {

    env->DeleteGlobalRef(buf);
}

int InputStreamWrapper::underflow() {

    try {

        if (!dataReady && inputStream == nullptr)
            return std::istream::traits_type::eof();

        if (!dataReady) {

            // refill internal buffer with data from Java
            dataSize = env->CallIntMethod(inputStream, mRead, buf, 0, BUF_SIZE);
            if (env->ExceptionCheck())
                return -1;

            if (dataSize != -1) {

                data = GET_BYTES(buf);
                dataReady = true;

            } else {

                inputStream = nullptr;
                return std::istream::traits_type::eof();
            }
        }

        // output current char
        ch = (char) data[index];
        setg(&ch, &ch, &ch + 1);

        // check for end of internal buffer
        if (++index == dataSize) {

            RELEASE_BYTES(buf, data);

            dataReady = false;
            index = 0;
        }

        return std::istream::traits_type::to_int_type(*gptr());

    } catch (const std::exception &ex) {

        dataReady = false;
        return std::istream::traits_type::eof();
    }
}
