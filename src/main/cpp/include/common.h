/*
 * Copyright 2016-2020 Luca Zanconato
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

#ifndef LIBSALTPACK_JNI_COMMONS_H
#define LIBSALTPACK_JNI_COMMONS_H

#include <saltpack.h>
#include <sodium.h>
#include "InputStreamWrapper.h"

#define GET_BYTES(array) ((unsigned char *) env->GetByteArrayElements(array, NULL))
#define RELEASE_BYTES(array, data) (env->ReleaseByteArrayElements(array, (jbyte *) data, 0))
#define GET_BYTES_SIZE(array) ((size_t) env->GetArrayLength(array))

#define EXCEPTION_CLASS(env) (loadClass(env, "net/nharyes/libsaltpack/SaltpackException"))
#define BYTE_ARRAY_CLASS(env) (loadClass(env, "[B"))

inline jclass loadClass(JNIEnv *env, const char *cls) {

    jclass res = env->FindClass(cls);
    if (res == nullptr)
        throw saltpack::SaltpackException(std::string("class not found: ") + cls);

    return res;
}

inline jmethodID loadMethod(JNIEnv *env, jclass cls, const char *method, const char *sig) {

    jmethodID res = env->GetMethodID(cls, method, sig);
    if (res == nullptr)
        throw saltpack::SaltpackException(std::string("method not found: ") + method);

    return res;
}

inline saltpack::BYTE_ARRAY copyBytes(JNIEnv *env, jbyteArray array) {

    size_t size = GET_BYTES_SIZE(array);
    saltpack::BYTE_ARRAY out(size);

    env->GetByteArrayRegion(array, 0, (jsize) size, reinterpret_cast<jbyte *>(out.data()));
    if (env->ExceptionCheck())
        throw saltpack::SaltpackException("errors while reading byte array");

    return out;
}

inline jbyteArray copyBytes(JNIEnv *env, saltpack::BYTE_ARRAY array) {

    jbyteArray out = env->NewByteArray((jsize) array.size());
    if (out == nullptr) {

        return nullptr; /* out of memory error thrown */
    }

    env->SetByteArrayRegion(out, 0, (jsize) array.size(), (const jbyte *) array.data());
    if (env->ExceptionCheck())
        throw saltpack::SaltpackException("errors while populating byte array");

    return out;
}

inline std::list<saltpack::BYTE_ARRAY> convertRecipients(JNIEnv *env, jobjectArray recipients) {

    jsize len = env->GetArrayLength(recipients);
    std::list<saltpack::BYTE_ARRAY> lRecipients;
    for (jsize i = 0; i < len; i++) {

        auto rec = (jbyteArray) env->GetObjectArrayElement(recipients, i);

        saltpack::BYTE_ARRAY recipient = copyBytes(env, rec);
        lRecipients.push_back(recipient);
    }

    return lRecipients;
}

inline std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY> convertPair(JNIEnv *env, jobjectArray pair) {

    jsize len = env->GetArrayLength(pair);
    if (len != 2)
        return std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY>();

    auto pair1 = (jbyteArray) env->GetObjectArrayElement(pair, 0);
    auto pair2 = (jbyteArray) env->GetObjectArrayElement(pair, 1);

    saltpack::BYTE_ARRAY pair1A = copyBytes(env, pair1);
    saltpack::BYTE_ARRAY pair2A = copyBytes(env, pair2);

    return std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY>(pair1A, pair2A);
}

inline std::list<std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY>> convertKeys(JNIEnv *env, jobjectArray keys) {

    jsize len = env->GetArrayLength(keys);
    std::list<std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY>> lKeys;
    for (jsize i = 0; i < len; i++) {

        auto current = (jobjectArray) env->GetObjectArrayElement(keys, i);
        lKeys.push_back(convertPair(env, current));
    }

    return lKeys;
}

#endif //LIBSALTPACK_JNI_COMMONS_H
