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

package net.nharyes.libsaltpack;

/**
 * Utilities class.
 */
public class Utils {

    static {

        try {

            System.loadLibrary("saltpack-jni");

        } catch (UnsatisfiedLinkError e) {

            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    private Utils() {
    }

    /**
     * Generates an encryption keypair using `libsodium`.
     *
     * @param publickey the array that will be populated with the public key.
     * @param secretkey the array that will be populated with the private key.
     * @throws SaltpackException
     */
    public static native void generateKeypair(byte[] publickey, byte[] secretkey) throws SaltpackException;

    /**
     * Generates a signing keypair using `libsodium`.
     *
     * @param publickey the array that will be populated with the public key.
     * @param secretkey the array that will be populated with the private key.
     * @throws SaltpackException
     */
    public static native void generateSignKeypair(byte[] publickey, byte[] secretkey) throws SaltpackException;

    /**
     * Derives the public key from a private key.
     *
     * @param secretkey the private key.
     * @return the public key.
     * @throws SaltpackException
     */
    public static native byte[] derivePublickey(byte[] secretkey) throws SaltpackException;

    /**
     * Returns the number of required characters to represent in BaseX, for a given {@code alphabet}, {@code size} characters.
     *
     * @param alphabet the alphabet for the BaseX encoding.
     * @param size     the size of the data to represent.
     * @return the number of characters required to encode {@code size} characters.
     * @throws SaltpackException
     */
    public static native int baseXblockSize(String alphabet, int size) throws SaltpackException;

    /**
     * Encodes the data in BaseX using the given {@code alphabet}.
     *
     * @param data     the data to encode.
     * @param alphabet the alphabet for the BaseX encoding.
     * @return the encoded string.
     * @throws SaltpackException
     */
    public static native String baseXencode(byte[] data, String alphabet) throws SaltpackException;

    /**
     * Encodes the data in BaseX using the given {@code alphabet}.
     *
     * @param data     the data to encode.
     * @param size     the number of characters to encode from {@code data}.
     * @param alphabet the alphabet for the BaseX encoding.
     * @return the encoded string.
     * @throws SaltpackException
     */
    public static native String baseXencode(byte[] data, int size, String alphabet) throws SaltpackException;

    /**
     * Decodes the string from BaseX and the given {@code alphabet}.
     *
     * @param data     data the string to decode.
     * @param alphabet the alphabet for the BaseX decoding.
     * @return the decoded data.
     * @throws SaltpackException
     */
    public static native byte[] baseXdecode(String data, String alphabet) throws SaltpackException;

    /**
     * Hexadecial to binary encoding.
     *
     * @param hex the hexadecimal string.
     * @return the binary data.
     * @throws SaltpackException
     */
    public static native byte[] hexToBin(String hex) throws SaltpackException;

    /**
     * Binary to hexadecimal encoding.
     *
     * @param bin the binary data.
     * @return the hexadecimal string.
     * @throws SaltpackException
     */
    public static native String binToHex(byte[] bin) throws SaltpackException;

    /**
     * Generates some random bytes using `libsodium`.
     *
     * @param size the amount of bytes to generate.
     * @return the random bytes.
     * @throws SaltpackException
     */
    public static native byte[] generateRandomBytes(long size) throws SaltpackException;

    /**
     * Wrapper for the `crypto_pwhash` function from `libsodium`.
     *
     * @param keySize  the size of the key.
     * @param password the password used to derive the key.
     * @param salt     the salt used to derive the key.
     * @param opsLimit the maximum amount of computations to perform.
     * @param memLimit the maximum amount of RAM that the function will use, in bytes.
     * @return the derived key.
     * @throws SaltpackException
     */
    public static native byte[] deriveKeyFromPassword(long keySize, String password, byte[] salt, long opsLimit, long memLimit) throws SaltpackException;
}
