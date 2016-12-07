/*
 * Copyright 2016 Luca Zanconato
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

package net.nharyes.libsaltpack;

/**
 * Constants for key sizes and alphabets.
 */
public class Constants {

    /**
     * Constant defining the size of a public key used when encrypting/decrypting a message with {@link net.nharyes.libsaltpack.MessageWriter} and {@link net.nharyes.libsaltpack.MessageReader}.
     */
    public static final int CRYPTO_BOX_PUBLICKEYBYTES = 32;

    /**
     * Constant defining the size of a private key used when encrypting/decrypting a message with {@link net.nharyes.libsaltpack.MessageWriter} and {@link net.nharyes.libsaltpack.MessageReader}.
     */
    public static final int CRYPTO_BOX_SECRETKEYBYTES = 32;

    /**
     * Constant defining the size of a public key used when signing/verifying a message with {@link net.nharyes.libsaltpack.MessageWriter} and {@link net.nharyes.libsaltpack.MessageReader}.
     */
    public static final int CRYPTO_SIGN_PUBLICKEYBYTES = 32;

    /**
     * Constant defining the size of a private key used when signing/verifying a message with {@link net.nharyes.libsaltpack.MessageWriter} and {@link net.nharyes.libsaltpack.MessageReader}.
     */
    public static final int CRYPTO_SIGN_SECRETKEYBYTES = 64;

    /**
     * BASE64 alphabet constant for {@link net.nharyes.libsaltpack.Utils} {@code baseX} methods.
     */
    public static final String ALPHABET_BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    /**
     * BASE62 alphabet constant for {@link net.nharyes.libsaltpack.Utils} {@code baseX} methods.
     */
    public static final String ALPHABET_BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * BASE85 alphabet constant for {@link net.nharyes.libsaltpack.Utils} {@code baseX} methods.
     */
    public static final String ALPHABET_BASE85 = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstu";
}
