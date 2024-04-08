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

#include <jni.h>
#include <iostream>
#include <InputStreamWrapper.h>
#include "com_gherynos_libsaltpack_MessageReader.h"
#include <saltpack.h>
#include <common.h>

struct RObjects {

    saltpack::ArmoredInputStream *ain;
    InputStreamWrapper *iw;
    InputStreamWrapper *mw;
    saltpack::MessageReader *mr;
    jobject inputStream;
    jobject messageIn;
};

jclass inputparameters;
jmethodID mGetInputStream;
jmethodID mIsArmored;
jmethodID mGetApp;

void deleteRObjects(JNIEnv *env, RObjects *objs) {

    if (objs == nullptr)
        return;

    delete objs->ain;

    if (objs->inputStream != nullptr)
        env->DeleteGlobalRef(objs->inputStream);

    if (objs->messageIn != nullptr)
        env->DeleteGlobalRef(objs->messageIn);

    delete objs->iw;

    delete objs->mw;

    delete objs;
}

RObjects *populateInputStreams(JNIEnv *env, jobject inputParameters) {

    if (inputParameters == nullptr)
        throw saltpack::SaltpackException("null input parameters provided");

    if (inputparameters == nullptr) {

        inputparameters = loadClass(env, "com/gherynos/libsaltpack/InputParameters");
        mGetInputStream = loadMethod(env, inputparameters, "getInputStream", "()Ljava/io/InputStream;");
        mIsArmored = loadMethod(env, inputparameters, "isArmored", "()Z");
        mGetApp = loadMethod(env, inputparameters, "getApp", "()Ljava/lang/String;");
    }

    auto *objs = new RObjects();

    objs->inputStream = env->NewGlobalRef(env->CallObjectMethod(inputParameters, mGetInputStream));
    objs->iw = new InputStreamWrapper(env, objs->inputStream);

    bool armored = (bool) env->CallBooleanMethod(inputParameters, mIsArmored);
    if (env->ExceptionCheck()) {

        deleteRObjects(env, objs);
        throw saltpack::SaltpackException("exception thrown while checking armored flag");
    }
    if (armored) {

        jobject oApp = env->CallObjectMethod(inputParameters, mGetApp);
        if (env->ExceptionCheck()) {

            deleteRObjects(env, objs);
            throw saltpack::SaltpackException("exception thrown while checking application name");
        }
        if (oApp != nullptr) {

            const char *appCStr = env->GetStringUTFChars((jstring) oApp, 0);
            if (env->ExceptionCheck()) {

                deleteRObjects(env, objs);
                throw saltpack::SaltpackException("exception thrown while getting application name");
            }
            std::string app(appCStr);
            env->ReleaseStringUTFChars((jstring) oApp, appCStr);

            objs->ain = new saltpack::ArmoredInputStream(*objs->iw, app);

        } else
            objs->ain = new saltpack::ArmoredInputStream(*objs->iw);
    }

    return objs;
}

jobject Java_com_gherynos_libsaltpack_MessageReader_constructor__Lcom_gherynos_libsaltpack_InputParameters_2_3B(JNIEnv *env,
                                                                                                            jobject obj,
                                                                                                            jobject in,
                                                                                                            jbyteArray recipientSecretkeyA) {

    RObjects *objs = nullptr;
    saltpack::BYTE_ARRAY recipientSecretkey;
    try {

        recipientSecretkey = copyBytes(env, recipientSecretkeyA);

        objs = populateInputStreams(env, in);

        if (objs->ain == nullptr)
            objs->mr = new saltpack::MessageReader(*objs->iw, recipientSecretkey);
        else
            objs->mr = new saltpack::MessageReader(*objs->ain, recipientSecretkey);

        sodium_memzero(recipientSecretkey.data(), recipientSecretkey.size());

        return env->NewDirectByteBuffer((void*) objs, sizeof(RObjects));

    } catch (...) {

        deleteRObjects(env, objs);
        sodium_memzero(recipientSecretkey.data(), recipientSecretkey.size());

        std::exception_ptr ex = std::current_exception();
        if (ex)
            try {

                std::rethrow_exception(ex);

            } catch (const std::exception &e) {

                env->ThrowNew(EXCEPTION_CLASS(env), e.what());
            }

        else
            env->ThrowNew(EXCEPTION_CLASS(env), "error");

        return nullptr;
    }
}

jobject Java_com_gherynos_libsaltpack_MessageReader_constructor__Lcom_gherynos_libsaltpack_InputParameters_2(JNIEnv *env,
                                                                                                         jobject obj,
                                                                                                         jobject in) {

    RObjects *objs = nullptr;
    try {

        objs = populateInputStreams(env, in);

        if (objs->ain == nullptr)
            objs->mr = new saltpack::MessageReader(*objs->iw);
        else
            objs->mr = new saltpack::MessageReader(*objs->ain);

        return env->NewDirectByteBuffer((void*) objs, sizeof(RObjects));

    } catch (...) {

        deleteRObjects(env, objs);

        std::exception_ptr ex = std::current_exception();
        if (ex)
            try {

                std::rethrow_exception(ex);

            } catch (const std::exception &e) {

                env->ThrowNew(EXCEPTION_CLASS(env), e.what());
            }

        else
            env->ThrowNew(EXCEPTION_CLASS(env), "error");

        return nullptr;
    }
}

jobject
Java_com_gherynos_libsaltpack_MessageReader_constructor__Lcom_gherynos_libsaltpack_InputParameters_2Ljava_io_InputStream_2(
        JNIEnv *env, jobject obj, jobject in, jobject msgIn) {

    RObjects *objs = nullptr;
    try {

        objs = populateInputStreams(env, in);

        objs->messageIn = env->NewGlobalRef(msgIn);
        objs->mw = new InputStreamWrapper(env, objs->messageIn);

        if (objs->ain == nullptr)
            objs->mr = new saltpack::MessageReader(*objs->iw, *objs->mw);
        else
            objs->mr = new saltpack::MessageReader(*objs->ain, *objs->mw);

        return env->NewDirectByteBuffer((void*) objs, sizeof(RObjects));

    } catch (...) {

        deleteRObjects(env, objs);

        std::exception_ptr ex = std::current_exception();
        if (ex)
            try {

                std::rethrow_exception(ex);

            } catch (const std::exception &e) {

                env->ThrowNew(EXCEPTION_CLASS(env), e.what());
            }

        else
            env->ThrowNew(EXCEPTION_CLASS(env), "error");

        return nullptr;
    }
}

jobject
Java_com_gherynos_libsaltpack_MessageReader_constructor__Lcom_gherynos_libsaltpack_InputParameters_2_3B_3_3B(JNIEnv *env,
                                                                                                           jobject obj,
                                                                                                           jobject in,
                                                                                                           jbyteArray recipientSecretkeyA,
                                                                                                           jobjectArray keyA) {

    RObjects *objs = nullptr;
    saltpack::BYTE_ARRAY recipientSecretkey;
    std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY> key;
    try {

        recipientSecretkey = copyBytes(env, recipientSecretkeyA);
        key = convertPair(env, keyA);

        objs = populateInputStreams(env, in);

        if (objs->ain == nullptr)
            objs->mr = new saltpack::MessageReader(*objs->iw, recipientSecretkey, key);
        else
            objs->mr = new saltpack::MessageReader(*objs->ain, recipientSecretkey, key);

        sodium_memzero(recipientSecretkey.data(), recipientSecretkey.size());
        sodium_memzero(key.second.data(), key.second.size());

        return env->NewDirectByteBuffer((void*) objs, sizeof(RObjects));

    } catch (...) {

        deleteRObjects(env, objs);
        sodium_memzero(recipientSecretkey.data(), recipientSecretkey.size());
        sodium_memzero(key.second.data(), key.second.size());

        std::exception_ptr ex = std::current_exception();
        if (ex)
            try {

                std::rethrow_exception(ex);

            } catch (const std::exception &e) {

                env->ThrowNew(EXCEPTION_CLASS(env), e.what());
            }

        else
            env->ThrowNew(EXCEPTION_CLASS(env), "error");

        return nullptr;
    }
}

void Java_com_gherynos_libsaltpack_MessageReader_destructor(JNIEnv *env, jobject obj, jobject ptr) {

    auto *objs = (RObjects *) env->GetDirectBufferAddress(ptr);

    deleteRObjects(env, objs);
}

jboolean Java_com_gherynos_libsaltpack_MessageReader_hasMoreBlocks(JNIEnv *env, jobject obj, jobject ptr) {

    try {

        auto *objs = (RObjects *) env->GetDirectBufferAddress(ptr);

        return (jboolean) objs->mr->hasMoreBlocks();

    } catch (...) {

        std::exception_ptr ex = std::current_exception();
        if (ex)
            try {

                std::rethrow_exception(ex);

            } catch (const std::exception &e) {

                env->ThrowNew(EXCEPTION_CLASS(env), e.what());
            }

        else
            env->ThrowNew(EXCEPTION_CLASS(env), "error");

        return (jboolean) false;
    }
}

jbyteArray Java_com_gherynos_libsaltpack_MessageReader_getBlock(JNIEnv *env, jobject obj, jobject ptr) {

    try {

        auto *objs = (RObjects *) env->GetDirectBufferAddress(ptr);
        saltpack::BYTE_ARRAY data = objs->mr->getBlock();

        return copyBytes(env, data);

    } catch (...) {

        std::exception_ptr ex = std::current_exception();
        if (ex)
            try {

                std::rethrow_exception(ex);

            } catch (const std::exception &e) {

                env->ThrowNew(EXCEPTION_CLASS(env), e.what());
            }

        else
            env->ThrowNew(EXCEPTION_CLASS(env), "error");

        return nullptr;
    }
}

jobjectArray Java_com_gherynos_libsaltpack_MessageReader_getRecipients(JNIEnv *env, jobject obj, jobject ptr) {

    try {

        auto *objs = (RObjects *) env->GetDirectBufferAddress(ptr);
        std::list<saltpack::BYTE_ARRAY> recipients = objs->mr->getRecipients();

        jobjectArray out = env->NewObjectArray((jsize) recipients.size(), BYTE_ARRAY_CLASS(env), env->NewByteArray(1));
        if (out == nullptr) {

            return nullptr; /* out of memory error thrown */
        }

        // convert recipients
        jsize idx = 0;
        for (saltpack::BYTE_ARRAY recipient: recipients) {

            jbyteArray rec = env->NewByteArray((jsize) recipient.size());
            if (rec == nullptr) {

                return nullptr; /* out of memory error thrown */
            }

            env->SetByteArrayRegion(rec, 0, (jsize) recipient.size(), (const jbyte *) recipient.data());
            env->SetObjectArrayElement(out, idx, rec);
            env->DeleteLocalRef(rec);
            if (env->ExceptionCheck())
                throw saltpack::SaltpackException("errors while converting recipient");

            idx++;
        }

        return out;

    } catch (...) {

        std::exception_ptr ex = std::current_exception();
        if (ex)
            try {

                std::rethrow_exception(ex);

            } catch (const std::exception &e) {

                env->ThrowNew(EXCEPTION_CLASS(env), e.what());
            }

        else
            env->ThrowNew(EXCEPTION_CLASS(env), "error");

        return nullptr;
    }
}

jbyteArray Java_com_gherynos_libsaltpack_MessageReader_getSender(JNIEnv *env, jobject obj, jobject ptr) {

    try {

        auto *objs = (RObjects *) env->GetDirectBufferAddress(ptr);
        saltpack::BYTE_ARRAY sender = objs->mr->getSender();

        return copyBytes(env, sender);

    } catch (...) {

        std::exception_ptr ex = std::current_exception();
        if (ex)
            try {

                std::rethrow_exception(ex);

            } catch (const std::exception &e) {

                env->ThrowNew(EXCEPTION_CLASS(env), e.what());
            }

        else
            env->ThrowNew(EXCEPTION_CLASS(env), "error");

        return nullptr;
    }
}

jboolean Java_com_gherynos_libsaltpack_MessageReader_isIntentionallyAnonymous(JNIEnv *env, jobject obj, jobject ptr) {

    try {

        auto *objs = (RObjects *) env->GetDirectBufferAddress(ptr);

        return (jboolean) objs->mr->isIntentionallyAnonymous();

    } catch (...) {

        std::exception_ptr ex = std::current_exception();
        if (ex)
            try {

                std::rethrow_exception(ex);

            } catch (const std::exception &e) {

                env->ThrowNew(EXCEPTION_CLASS(env), e.what());
            }

        else
            env->ThrowNew(EXCEPTION_CLASS(env), "error");

        return (jboolean) false;
    }
}
