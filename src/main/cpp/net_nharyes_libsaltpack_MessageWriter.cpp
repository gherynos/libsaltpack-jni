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
#include <OutputStreamWrapper.h>
#include "net_nharyes_libsaltpack_MessageWriter.h"
#include <saltpack.h>
#include <common.h>

struct WObjects {

    saltpack::ArmoredOutputStream *aout;
    OutputStreamWrapper *ow;
    saltpack::MessageWriter *mw;
    jobject outputStream;
};

jclass outputparameters;
jmethodID omGetOutputStream;
jmethodID omIsArmored;
jmethodID omIntParamsPopulated;
jmethodID omGetApp;
jmethodID omGetLettersInWords;
jmethodID omGetWordsInPhrase;

WObjects *populateOutputStreams(JNIEnv *env, jobject outputParameters, int mode) {

    if (outputparameters == NULL) {

        outputparameters = loadClass(env, "net/nharyes/libsaltpack/OutputParameters");
        omGetOutputStream = loadMethod(env, outputparameters, "getOutputStream", "()Ljava/io/OutputStream;");
        omIsArmored = loadMethod(env, outputparameters, "isArmored", "()Z");
        omIntParamsPopulated = loadMethod(env, outputparameters, "intParamsPopulated", "()Z");
        omGetApp = loadMethod(env, outputparameters, "getApp", "()Ljava/lang/String;");
        omGetLettersInWords = loadMethod(env, outputparameters, "getLettersInWords", "()I");
        omGetWordsInPhrase = loadMethod(env, outputparameters, "getWordsInPhrase", "()I");
    }

    WObjects *objs = new WObjects();

    objs->outputStream = env->NewGlobalRef(env->CallObjectMethod(outputParameters, omGetOutputStream));
    objs->ow = new OutputStreamWrapper(env, objs->outputStream);

    bool armored = (bool) env->CallBooleanMethod(outputParameters, omIsArmored);
    if (env->ExceptionCheck())
        throw saltpack::SaltpackException("exception thrown while checking armored flag");
    if (armored) {

        jobject oApp = env->CallObjectMethod(outputParameters, omGetApp);
        if (env->ExceptionCheck())
            throw saltpack::SaltpackException("exception thrown while checking application name");

        bool intp = (bool) env->CallBooleanMethod(outputParameters, omIntParamsPopulated);
        if (env->ExceptionCheck())
            throw saltpack::SaltpackException("exception thrown while checking int params flag");
        int lettersInWords = -1;
        int wordsInPhrase = -1;
        if (intp) {

            lettersInWords = (int) env->CallIntMethod(outputParameters, omGetLettersInWords);
            if (env->ExceptionCheck())
                throw saltpack::SaltpackException("exception thrown while loading lettersInWords");

            wordsInPhrase = (int) env->CallIntMethod(outputParameters, omGetWordsInPhrase);
            if (env->ExceptionCheck())
                throw saltpack::SaltpackException("exception thrown while loading wordsInPhrase");
        }

        if (oApp != NULL) {

            const char *appCStr = env->GetStringUTFChars((jstring) oApp, 0);
            if (env->ExceptionCheck())
                throw saltpack::SaltpackException("exception thrown while getting application name");
            std::string app(appCStr);
            env->ReleaseStringUTFChars((jstring) oApp, appCStr);

            if (intp)
                objs->aout = new saltpack::ArmoredOutputStream(*objs->ow, app, mode, lettersInWords, wordsInPhrase);
            else
                objs->aout = new saltpack::ArmoredOutputStream(*objs->ow, app, mode);

        } else {

            if (intp)
                objs->aout = new saltpack::ArmoredOutputStream(*objs->ow, mode, lettersInWords, wordsInPhrase);
            else
                objs->aout = new saltpack::ArmoredOutputStream(*objs->ow, mode);
        }
    }

    return objs;
}

void deleteWObjects(JNIEnv *env, WObjects *objs) {

    if (objs == NULL)
        return;

    if (objs->aout != NULL)
        delete objs->aout;

    if (objs->outputStream != NULL)
        env->DeleteGlobalRef(objs->outputStream);

    if (objs->ow != NULL)
        delete objs->ow;

    if (objs->mw != NULL)
        delete objs->mw;

    delete objs;
}

jlong Java_net_nharyes_libsaltpack_MessageWriter_constructor__Lnet_nharyes_libsaltpack_OutputParameters_2_3B_3_3BZ(
        JNIEnv *env, jobject obj, jobject op, jbyteArray senderSecretkeyA, jobjectArray recipients,
        jboolean visibleRecipients) {

    WObjects *objs = NULL;
    try {

        saltpack::BYTE_ARRAY senderSecretkey = copyBytes(env, senderSecretkeyA);

        objs = populateOutputStreams(env, op, saltpack::MODE_ENCRYPTION);

        if (objs->aout == NULL)
            objs->mw = new saltpack::MessageWriter(*objs->ow, senderSecretkey, convertRecipients(env, recipients),
                                                   (bool) visibleRecipients);
        else
            objs->mw = new saltpack::MessageWriter(*objs->aout, senderSecretkey, convertRecipients(env, recipients),
                                                   (bool) visibleRecipients);

        sodium_memzero(senderSecretkey.data(), senderSecretkey.size());

    } catch (...) {

        deleteWObjects(env, objs);

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

    return (long) objs;
}

jlong
Java_net_nharyes_libsaltpack_MessageWriter_constructor__Lnet_nharyes_libsaltpack_OutputParameters_2_3B_3_3B(JNIEnv *env,
                                                                                                            jobject obj,
                                                                                                            jobject op,
                                                                                                            jbyteArray senderSecretkeyA,
                                                                                                            jobjectArray recipients) {

    WObjects *objs = NULL;
    try {

        saltpack::BYTE_ARRAY senderSecretkey = copyBytes(env, senderSecretkeyA);

        objs = populateOutputStreams(env, op, saltpack::MODE_ENCRYPTION);

        if (objs->aout == NULL)
            objs->mw = new saltpack::MessageWriter(*objs->ow, senderSecretkey, convertRecipients(env, recipients));
        else
            objs->mw = new saltpack::MessageWriter(*objs->aout, senderSecretkey, convertRecipients(env, recipients));

        sodium_memzero(senderSecretkey.data(), senderSecretkey.size());

    } catch (...) {

        deleteWObjects(env, objs);

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

    return (long) objs;
}

jlong
Java_net_nharyes_libsaltpack_MessageWriter_constructor__Lnet_nharyes_libsaltpack_OutputParameters_2_3_3BZ(JNIEnv *env,
                                                                                                          jobject obj,
                                                                                                          jobject op,
                                                                                                          jobjectArray recipients,
                                                                                                          jboolean visibleRecipients) {

    WObjects *objs = NULL;
    try {

        objs = populateOutputStreams(env, op, saltpack::MODE_ENCRYPTION);

        if (objs->aout == NULL)
            objs->mw = new saltpack::MessageWriter(*objs->ow, convertRecipients(env, recipients),
                                                   (bool) visibleRecipients);
        else
            objs->mw = new saltpack::MessageWriter(*objs->aout, convertRecipients(env, recipients),
                                                   (bool) visibleRecipients);

    } catch (...) {

        deleteWObjects(env, objs);

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

    return (long) objs;
}

jlong
Java_net_nharyes_libsaltpack_MessageWriter_constructor__Lnet_nharyes_libsaltpack_OutputParameters_2_3_3B(JNIEnv *env,
                                                                                                         jobject obj,
                                                                                                         jobject op,
                                                                                                         jobjectArray recipients) {

    WObjects *objs = NULL;
    try {

        objs = populateOutputStreams(env, op, saltpack::MODE_ENCRYPTION);

        if (objs->aout == NULL)
            objs->mw = new saltpack::MessageWriter(*objs->ow, convertRecipients(env, recipients));
        else
            objs->mw = new saltpack::MessageWriter(*objs->aout, convertRecipients(env, recipients));

    } catch (...) {

        deleteWObjects(env, objs);

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

    return (long) objs;
}

jlong
Java_net_nharyes_libsaltpack_MessageWriter_constructor__Lnet_nharyes_libsaltpack_OutputParameters_2_3BZ(JNIEnv *env,
                                                                                                        jobject obj,
                                                                                                        jobject op,
                                                                                                        jbyteArray senderSecretkeyA,
                                                                                                        jboolean detatchedSignature) {

    WObjects *objs = NULL;
    try {

        saltpack::BYTE_ARRAY senderSecretkey = copyBytes(env, senderSecretkeyA);

        bool ds = (bool) detatchedSignature;
        objs = populateOutputStreams(env, op,
                                     ds ? saltpack::MODE_DETACHED_SIGNATURE : saltpack::MODE_ATTACHED_SIGNATURE);

        if (objs->aout == NULL)
            objs->mw = new saltpack::MessageWriter(*objs->ow, senderSecretkey, ds);
        else
            objs->mw = new saltpack::MessageWriter(*objs->aout, senderSecretkey, ds);

        sodium_memzero(senderSecretkey.data(), senderSecretkey.size());

    } catch (...) {

        deleteWObjects(env, objs);

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

    return (long) objs;
}

jlong
Java_net_nharyes_libsaltpack_MessageWriter_constructor__Lnet_nharyes_libsaltpack_OutputParameters_2_3B_3_3B_3_3_3B(
        JNIEnv *env, jobject obj, jobject op, jbyteArray senderSecretkeyA, jobjectArray recipients, jobjectArray keysA) {

    WObjects *objs = NULL;
    try {

        saltpack::BYTE_ARRAY senderSecretkey = copyBytes(env, senderSecretkeyA);
        std::list<std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY>> keys = convertKeys(env, keysA);

        objs = populateOutputStreams(env, op, saltpack::MODE_ENCRYPTION);

        if (objs->aout == NULL)
            objs->mw = new saltpack::MessageWriter(*objs->ow, senderSecretkey, convertRecipients(env, recipients), keys);
        else
            objs->mw = new saltpack::MessageWriter(*objs->aout, senderSecretkey, convertRecipients(env, recipients), keys);

        sodium_memzero(senderSecretkey.data(), senderSecretkey.size());
        for (std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY> key: keys)
            sodium_memzero(key.second.data(), key.second.size());

    } catch (...) {

        deleteWObjects(env, objs);

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

    return (long) objs;
}

jlong Java_net_nharyes_libsaltpack_MessageWriter_constructor__Lnet_nharyes_libsaltpack_OutputParameters_2_3_3B_3_3_3B(
        JNIEnv *env, jobject obj, jobject op, jobjectArray recipients, jobjectArray keysA) {

    WObjects *objs = NULL;
    try {

        std::list<std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY>> keys = convertKeys(env, keysA);

        objs = populateOutputStreams(env, op, saltpack::MODE_ENCRYPTION);

        if (objs->aout == NULL)
            objs->mw = new saltpack::MessageWriter(*objs->ow, convertRecipients(env, recipients), keys);
        else
            objs->mw = new saltpack::MessageWriter(*objs->aout, convertRecipients(env, recipients), keys);

        for (std::pair<saltpack::BYTE_ARRAY, saltpack::BYTE_ARRAY> key: keys)
            sodium_memzero(key.second.data(), key.second.size());

    } catch (...) {

        deleteWObjects(env, objs);

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

    return (long) objs;
}

void Java_net_nharyes_libsaltpack_MessageWriter_destructor(JNIEnv *env, jobject obj, jlong ptr) {

    WObjects *objs = reinterpret_cast<WObjects *>(ptr);

    deleteWObjects(env, objs);
}

void Java_net_nharyes_libsaltpack_MessageWriter_addBlock(JNIEnv *env, jobject obj, jlong ptr, jbyteArray dataA,
                                                         jboolean isFinal) {

    try {

        WObjects *objs = reinterpret_cast<WObjects *>(ptr);

        saltpack::BYTE_ARRAY data = copyBytes(env, dataA);

        objs->mw->addBlock(data, isFinal);

        if (isFinal) {

            if (objs->aout != NULL)
                objs->aout->finalise();

            objs->ow->finalise();
        }

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
    }
}
