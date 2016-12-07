//
// Created by Gherynos on 14/11/2016.
//

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

    buf = env->NewByteArray(BUF_SIZE);
    if (buf == NULL) {

        return; /* out of memory error thrown */
    }
}

InputStreamWrapper::~InputStreamWrapper() {

    env->DeleteLocalRef(buf);
}

int InputStreamWrapper::underflow() {

    try {

        if (!dataReady && inputStream == NULL)
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

                inputStream = NULL;
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
