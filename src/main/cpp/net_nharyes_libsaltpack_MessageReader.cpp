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

#include <jni.h>
#include <iostream>
#include <InputStreamWrapper.h>
#include "net_nharyes_libsaltpack_MessageReader.h"
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

RObjects *populateInputStreams(JNIEnv *env, jobject inputParameters) {

    if (inputparameters == NULL) {

        inputparameters = loadClass(env, "net/nharyes/libsaltpack/InputParameters");
        mGetInputStream = loadMethod(env, inputparameters, "getInputStream", "()Ljava/io/InputStream;");
        mIsArmored = loadMethod(env, inputparameters, "isArmored", "()Z");
        mGetApp = loadMethod(env, inputparameters, "getApp", "()Ljava/lang/String;");
    }

    RObjects *objs = new RObjects();

    objs->inputStream = env->NewGlobalRef(env->CallObjectMethod(inputParameters, mGetInputStream));
    objs->iw = new InputStreamWrapper(env, objs->inputStream);

    bool armored = (bool) env->CallBooleanMethod(inputParameters, mIsArmored);
    if (env->ExceptionCheck())
        throw saltpack::SaltpackException("exception thrown while checking armored flag");
    if (armored) {

        jobject oApp = env->CallObjectMethod(inputParameters, mGetApp);
        if (env->ExceptionCheck())
            throw saltpack::SaltpackException("exception thrown while checking application name");
        if (oApp != NULL) {

            const char *appCStr = env->GetStringUTFChars((jstring) oApp, 0);
            if (env->ExceptionCheck())
                throw saltpack::SaltpackException("exception thrown while getting application name");
            std::string app(appCStr);
            env->ReleaseStringUTFChars((jstring) oApp, appCStr);

            objs->ain = new saltpack::ArmoredInputStream(*objs->iw, app);

        } else
            objs->ain = new saltpack::ArmoredInputStream(*objs->iw);
    }

    return objs;
}

void deleteRObjects(JNIEnv *env, RObjects *objs) {

    if (objs == NULL)
        return;

    if (objs->ain != NULL)
        delete objs->ain;

    if (objs->inputStream != NULL)
        env->DeleteGlobalRef(objs->inputStream);

    if (objs->messageIn != NULL)
        env->DeleteGlobalRef(objs->messageIn);

    if (objs->iw != NULL)
        delete objs->iw;

    if (objs->mw != NULL)
        delete objs->mw;

    delete objs;
}

jlong Java_net_nharyes_libsaltpack_MessageReader_constructor__Lnet_nharyes_libsaltpack_InputParameters_2_3B(JNIEnv *env,
                                                                                                            jobject obj,
                                                                                                            jobject in,
                                                                                                            jbyteArray recipientSecretkeyA) {

    RObjects *objs = NULL;
    try {

        saltpack::BYTE_ARRAY recipientSecretkey = copyBytes(env, recipientSecretkeyA);

        objs = populateInputStreams(env, in);

        if (objs->ain == NULL)
            objs->mr = new saltpack::MessageReader(*objs->iw, recipientSecretkey);
        else
            objs->mr = new saltpack::MessageReader(*objs->ain, recipientSecretkey);

        return (long) objs;

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

        return -1;
    }
}

jlong Java_net_nharyes_libsaltpack_MessageReader_constructor__Lnet_nharyes_libsaltpack_InputParameters_2(JNIEnv *env,
                                                                                                         jobject obj,
                                                                                                         jobject in) {

    RObjects *objs = NULL;
    try {

        objs = populateInputStreams(env, in);

        if (objs->ain == NULL)
            objs->mr = new saltpack::MessageReader(*objs->iw);
        else
            objs->mr = new saltpack::MessageReader(*objs->ain);

        return (long) objs;

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

        return -1;
    }
}

jlong
Java_net_nharyes_libsaltpack_MessageReader_constructor__Lnet_nharyes_libsaltpack_InputParameters_2Ljava_io_InputStream_2(
        JNIEnv *env, jobject obj, jobject in, jobject msgIn) {

    RObjects *objs = NULL;
    try {

        objs = populateInputStreams(env, in);

        objs->messageIn = env->NewGlobalRef(msgIn);
        objs->mw = new InputStreamWrapper(env, objs->messageIn);

        if (objs->ain == NULL)
            objs->mr = new saltpack::MessageReader(*objs->iw, *objs->mw);
        else
            objs->mr = new saltpack::MessageReader(*objs->ain, *objs->mw);

        return (long) objs;

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

        return -1;
    }
}

void Java_net_nharyes_libsaltpack_MessageReader_destructor(JNIEnv *env, jobject obj, jlong ptr) {

    RObjects *objs = reinterpret_cast<RObjects *>(ptr);

    deleteRObjects(env, objs);
}

jboolean Java_net_nharyes_libsaltpack_MessageReader_hasMoreBlocks(JNIEnv *env, jobject obj, jlong ptr) {

    try {

        RObjects *objs = reinterpret_cast<RObjects *>(ptr);

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

jbyteArray Java_net_nharyes_libsaltpack_MessageReader_getBlock(JNIEnv *env, jobject obj, jlong ptr) {

    try {

        RObjects *objs = reinterpret_cast<RObjects *>(ptr);
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

        return NULL;
    }
}

jobjectArray Java_net_nharyes_libsaltpack_MessageReader_getRecipients(JNIEnv *env, jobject obj, jlong ptr) {

    try {

        RObjects *objs = reinterpret_cast<RObjects *>(ptr);
        std::list<saltpack::BYTE_ARRAY> recipients = objs->mr->getRecipients();

        jobjectArray out = env->NewObjectArray((jsize) recipients.size(), BYTE_ARRAY_CLASS(env), env->NewByteArray(1));
        if (out == NULL) {

            return NULL; /* out of memory error thrown */
        }

        // convert recipients
        jsize idx = 0;
        for (saltpack::BYTE_ARRAY recipient: recipients) {

            jbyteArray rec = env->NewByteArray((jsize) recipient.size());
            if (rec == NULL) {

                return NULL; /* out of memory error thrown */
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

        return NULL;
    }
}

jbyteArray Java_net_nharyes_libsaltpack_MessageReader_getSender(JNIEnv *env, jobject obj, jlong ptr) {

    try {

        RObjects *objs = reinterpret_cast<RObjects *>(ptr);
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

        return NULL;
    }
}

jboolean Java_net_nharyes_libsaltpack_MessageReader_isIntentionallyAnonymous(JNIEnv *env, jobject obj, jlong ptr) {

    try {

        RObjects *objs = reinterpret_cast<RObjects *>(ptr);

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
