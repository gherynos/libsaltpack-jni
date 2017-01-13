/*
 * Copyright 2016-2017 Luca Zanconato
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

#include <common.h>
#include "OutputStreamWrapper.h"

const int BUF_SIZE = 2048;

OutputStreamWrapper::OutputStreamWrapper(JNIEnv *env, jobject outputStream) : std::ostream(this) {

    this->env = env;
    this->outputStream = outputStream;

    jclass clazz = loadClass(env, "java/io/OutputStream");
    mWrite = loadMethod(env, clazz, "write", "([BII)V");

    buf = saltpack::BYTE_ARRAY(BUF_SIZE);
    count = 0;
}

OutputStreamWrapper::~OutputStreamWrapper() {

    buf.clear();
    buf.shrink_to_fit();
}

int OutputStreamWrapper::overflow(int c) {

    if (count < BUF_SIZE) {

        buf[count] = (saltpack::BYTE) c;
        count += 1;

    } else {

        writeToOutput();

        buf[0] = (saltpack::BYTE) c;
        count = 1;
    }

    return c;
}

void OutputStreamWrapper::finalise() {

    if (count > 0) {

        writeToOutput();

        count = 0;
    }
}

void OutputStreamWrapper::writeToOutput() {

    jbyteArray out = env->NewByteArray((jsize) count);
    if (out == NULL) {

        return; /* out of memory error thrown */
    }

    env->SetByteArrayRegion(out, 0, (jsize) count, (const jbyte *) buf.data());

    env->CallVoidMethod(outputStream, mWrite, out, 0, count);
    if (env->ExceptionCheck())
        return;

    env->DeleteLocalRef(out);
}
