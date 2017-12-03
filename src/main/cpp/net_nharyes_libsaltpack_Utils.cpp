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
#include <common.h>
#include "net_nharyes_libsaltpack_Utils.h"

void Java_net_nharyes_libsaltpack_Utils_generateKeypair(JNIEnv *env, jclass cls, jbyteArray publickeyA,
                                                        jbyteArray secretkeyA) {

    saltpack::BYTE_ARRAY publickey(GET_BYTES_SIZE(publickeyA));
    saltpack::BYTE_ARRAY secretkey(GET_BYTES_SIZE(secretkeyA));
    try {

        saltpack::Utils::generateKeypair(publickey, secretkey);

        env->SetByteArrayRegion(publickeyA, 0, (jsize) publickey.size(), (const jbyte *) publickey.data());
        env->SetByteArrayRegion(secretkeyA, 0, (jsize) secretkey.size(), (const jbyte *) secretkey.data());

        sodium_memzero(publickey.data(), publickey.size());
        sodium_memzero(secretkey.data(), secretkey.size());

    } catch (...) {

        sodium_memzero(publickey.data(), publickey.size());
        sodium_memzero(secretkey.data(), secretkey.size());

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

void Java_net_nharyes_libsaltpack_Utils_generateSignKeypair(JNIEnv *env, jclass cls, jbyteArray publickeyA,
                                                            jbyteArray secretkeyA) {

    saltpack::BYTE_ARRAY publickey(GET_BYTES_SIZE(publickeyA));
    saltpack::BYTE_ARRAY secretkey(GET_BYTES_SIZE(secretkeyA));
    try {

        saltpack::Utils::generateSignKeypair(publickey, secretkey);

        env->SetByteArrayRegion(publickeyA, 0, (jsize) publickey.size(), (const jbyte *) publickey.data());
        env->SetByteArrayRegion(secretkeyA, 0, (jsize) secretkey.size(), (const jbyte *) secretkey.data());

        sodium_memzero(publickey.data(), publickey.size());
        sodium_memzero(secretkey.data(), secretkey.size());

    } catch (...) {

        sodium_memzero(publickey.data(), publickey.size());
        sodium_memzero(secretkey.data(), secretkey.size());

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

jbyteArray Java_net_nharyes_libsaltpack_Utils_derivePublickey(JNIEnv *env, jclass cls, jbyteArray secretkeyA) {

    saltpack::BYTE_ARRAY secretkey;
    try {

        secretkey = copyBytes(env, secretkeyA);
        saltpack::BYTE_ARRAY publikey = saltpack::Utils::derivePublickey(secretkey);

        jbyteArray out = copyBytes(env, publikey);

        sodium_memzero(secretkey.data(), secretkey.size());

        return out;

    } catch (...) {

        sodium_memzero(secretkey.data(), secretkey.size());

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

jint Java_net_nharyes_libsaltpack_Utils_baseXblockSize(JNIEnv *env, jclass cls, jstring alphabetS, jint size) {

    try {

        std::string alphabet(env->GetStringUTFChars(alphabetS, 0));
        return (jint) saltpack::Utils::baseXblockSize(alphabet, (int) size);

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

        return -1;
    }
}

jstring
Java_net_nharyes_libsaltpack_Utils_baseXencode___3BLjava_lang_String_2(JNIEnv *env, jclass cls, jbyteArray dataA,
                                                                       jstring alphabetS) {

    try {

        std::string alphabet(env->GetStringUTFChars(alphabetS, 0));
        saltpack::BYTE_ARRAY data = copyBytes(env, dataA);

        std::string out = saltpack::Utils::baseXencode(data, alphabet);

        return env->NewStringUTF(out.data());

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

jstring
Java_net_nharyes_libsaltpack_Utils_baseXencode___3BILjava_lang_String_2(JNIEnv *env, jclass cls, jbyteArray dataA,
                                                                        jint sz, jstring alphabetS) {

    try {

        std::string alphabet(env->GetStringUTFChars(alphabetS, 0));
        saltpack::BYTE_ARRAY data = copyBytes(env, dataA);

        std::string out = saltpack::Utils::baseXencode(data, (size_t) sz, alphabet);

        return env->NewStringUTF(out.data());

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

jbyteArray Java_net_nharyes_libsaltpack_Utils_baseXdecode(JNIEnv *env, jclass cls, jstring dataS, jstring alphabetS) {

    try {

        std::string alphabet(env->GetStringUTFChars(alphabetS, 0));
        std::string data(env->GetStringUTFChars(dataS, 0));

        saltpack::BYTE_ARRAY dec = saltpack::Utils::baseXdecode(data, alphabet);

        return copyBytes(env, dec);

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

jbyteArray Java_net_nharyes_libsaltpack_Utils_hexToBin(JNIEnv *env, jclass cls, jstring dataS) {

    try {

        std::string data(env->GetStringUTFChars(dataS, 0));

        saltpack::BYTE_ARRAY dec = saltpack::Utils::hexToBin(data);

        return copyBytes(env, dec);

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

jstring Java_net_nharyes_libsaltpack_Utils_binToHex(JNIEnv *env, jclass cls, jbyteArray dataA) {

    try {

        saltpack::BYTE_ARRAY data = copyBytes(env, dataA);

        std::string out = saltpack::Utils::binToHex(data);

        return env->NewStringUTF(out.data());

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

jbyteArray Java_net_nharyes_libsaltpack_Utils_generateRandomBytes(JNIEnv *env, jclass cls, jlong size) {

    try {

        saltpack::BYTE_ARRAY out = saltpack::Utils::generateRandomBytes((size_t) size);

        return copyBytes(env, out);

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

jbyteArray
Java_net_nharyes_libsaltpack_Utils_deriveKeyFromPassword(JNIEnv *env, jclass cls, jlong keySize, jcharArray passwordA,
                                                         jbyteArray saltA, jlong opsLimit, jlong memLimit) {

    std::string password;
    try {

        if (sodium_init() == -1)
            throw saltpack::SaltpackException("Unable to initialise libsodium.");

        size_t size = GET_BYTES_SIZE(passwordA);
        jchar buf[size];
        env->GetCharArrayRegion(passwordA, 0, (jsize) size, buf);
        if (env->ExceptionCheck())
            throw saltpack::SaltpackException("errors while reading char array");

        password = std::string(size, ' ');
        for (size_t i = 0; i < size; i++)
            password[i] = (char) buf[i];
        sodium_memzero(buf, size);

        saltpack::BYTE_ARRAY salt = copyBytes(env, saltA);

        saltpack::BYTE_ARRAY
                out = saltpack::Utils::deriveKeyFromPassword((unsigned long long int) keySize, password, salt,
                                                             (unsigned long long int) opsLimit, (size_t) memLimit);

        sodium_memzero(&password[0], password.length());

        return copyBytes(env, out);

    } catch (...) {

        sodium_memzero(&password[0], password.length());

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
