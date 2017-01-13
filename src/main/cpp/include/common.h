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
    if (res == NULL)
        throw saltpack::SaltpackException(std::string("class not found: ") + cls);

    return res;
}

inline jmethodID loadMethod(JNIEnv *env, jclass cls, const char *method, const char *sig) {

    jmethodID res = env->GetMethodID(cls, method, sig);
    if (res == NULL)
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
    if (out == NULL) {

        return NULL; /* out of memory error thrown */
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

        jbyteArray rec = (jbyteArray) env->GetObjectArrayElement(recipients, i);

        saltpack::BYTE_ARRAY recipient = copyBytes(env, rec);
        lRecipients.push_back(recipient);
    }

    return lRecipients;
}

#endif //LIBSALTPACK_JNI_COMMONS_H
