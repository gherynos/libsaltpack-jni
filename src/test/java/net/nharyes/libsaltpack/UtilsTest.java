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

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void baseX() throws Exception {

        byte[] data = {(byte) 0, (byte) 255};
        String enc = Utils.baseXencode(data, "0123456789");

        assertEquals(enc, "00255");

        byte[] dec = Utils.baseXdecode(enc, "0123456789");

        assertArrayEquals(data, dec);

        try {

            Utils.baseXdecode("70000", "0123456789");
            throw new Exception();

        } catch (SaltpackException ex) {

            assertEquals(ex.getMessage(), "Illegal block.");
        }

        Random r = new Random();

        data = new byte[64];
        r.nextBytes(data);

        enc = Utils.baseXencode(data, Constants.ALPHABET_BASE85);
        dec = Utils.baseXdecode(enc, Constants.ALPHABET_BASE85);

        assertArrayEquals(data, dec);
        assertEquals(enc.length(), Utils.baseXblockSize(Constants.ALPHABET_BASE85, data.length));

        data = new byte[65];
        r.nextBytes(data);

        enc = Utils.baseXencode(data, Constants.ALPHABET_BASE62);
        dec = Utils.baseXdecode(enc, Constants.ALPHABET_BASE62);

        assertArrayEquals(data, dec);
        assertEquals(enc.length(), Utils.baseXblockSize(Constants.ALPHABET_BASE62, data.length));

        data = new byte[66];
        r.nextBytes(data);

        enc = Utils.baseXencode(data, Constants.ALPHABET_BASE64);
        dec = Utils.baseXdecode(enc, Constants.ALPHABET_BASE64);

        assertArrayEquals(data, dec);
        assertEquals(enc.length(), Utils.baseXblockSize(Constants.ALPHABET_BASE64, data.length));

        data = new byte[68];
        r.nextBytes(data);

        enc = Utils.baseXencode(data, 32, Constants.ALPHABET_BASE85);
        dec = Utils.baseXdecode(enc, Constants.ALPHABET_BASE85);

        byte[] d2 = new byte[32];
        System.arraycopy(data, 0, d2, 0, 32);
        assertArrayEquals(d2, dec);
        assertEquals(enc.length(), Utils.baseXblockSize(Constants.ALPHABET_BASE85, d2.length));

        data = new byte[40];
        r.nextBytes(data);

        enc = Utils.baseXencode(data, 21, Constants.ALPHABET_BASE62);
        dec = Utils.baseXdecode(enc, Constants.ALPHABET_BASE62);

        d2 = new byte[21];
        System.arraycopy(data, 0, d2, 0, 21);
        assertArrayEquals(d2, dec);
        assertEquals(enc.length(), Utils.baseXblockSize(Constants.ALPHABET_BASE62, d2.length));
    }

    @Test
    public void hex() throws Exception {

        Random r = new Random();
        byte[] data = new byte[64];
        r.nextBytes(data);

        String enc = Utils.binToHex(data);
        byte[] dec = Utils.hexToBin(enc);

        assertArrayEquals(data, dec);
    }

    @Test
    public void keys() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        byte[] publickey2 = Utils.derivePublickey(secretkey);

        assertArrayEquals(publickey, publickey2);

        byte[] sSecretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] sPublickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(sPublickey, sSecretkey);

        byte[] sPublickey2 = Utils.derivePublickey(sSecretkey);

        assertArrayEquals(sPublickey, sPublickey2);
    }

    @Test
    public void randomBytes() throws Exception {

        byte[] b1 = Utils.generateRandomBytes(4096);
        byte[] b2 = Utils.generateRandomBytes(4096);

        assertEquals(b1.length, 4096);
        assertEquals(b2.length, 4096);
        Assert.assertThat(b1, IsNot.not(IsEqual.equalTo(b2)));
    }

    @Test
    public void keyDerivation() throws Exception {

        byte[] salt = Utils.generateRandomBytes(Constants.CRYPTO_PWHASH_SALTBYTES);

        assertEquals(salt.length, Constants.CRYPTO_PWHASH_SALTBYTES);

        byte[] key = Utils.deriveKeyFromPassword(128, "The passW0rd".toCharArray(), salt,
                Constants.CRYPTO_PWHASH_OPSLIMIT_MODERATE, Constants.CRYPTO_PWHASH_MEMLIMIT_MODERATE);

        assertEquals(key.length, 128);

        byte[] key2 = Utils.deriveKeyFromPassword(128, "The passw0rd".toCharArray(), salt,
                Constants.CRYPTO_PWHASH_OPSLIMIT_MODERATE, Constants.CRYPTO_PWHASH_MEMLIMIT_MODERATE);

        Assert.assertThat(key, IsNot.not(IsEqual.equalTo(key2)));

        byte[] key3 = Utils.deriveKeyFromPassword(128, "The passW0rd".toCharArray(), salt,
                Constants.CRYPTO_PWHASH_OPSLIMIT_INTERACTIVE, Constants.CRYPTO_PWHASH_MEMLIMIT_MODERATE);

        Assert.assertThat(key, IsNot.not(IsEqual.equalTo(key3)));

        key = Utils.deriveKeyFromPassword(Constants.CRYPTO_BOX_SECRETKEYBYTES, "The passW0rd".toCharArray(), salt,
                Constants.CRYPTO_PWHASH_OPSLIMIT_MODERATE, Constants.CRYPTO_PWHASH_MEMLIMIT_MODERATE);

        assertEquals(key.length, Constants.CRYPTO_BOX_SECRETKEYBYTES);
    }
}
