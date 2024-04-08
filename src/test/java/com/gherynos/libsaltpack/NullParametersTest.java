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

package com.gherynos.libsaltpack;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class NullParametersTest {

    @Test
    public void writer() throws Exception {

        final byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        final byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        Exception exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(op, null, new byte[][]{publickey});
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(op, secretkey, new byte[][]{null});
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        final byte[] nullsSecretkey = null;
        final byte[] sSecretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        final byte[] sPublickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(sPublickey, sSecretkey);

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(op, nullsSecretkey, false);
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(null, sSecretkey, false);
            }
        });
        assertTrue(exception.getMessage().contains("null output parameters provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(null, secretkey, new byte[][]{publickey});
            }
        });
        assertTrue(exception.getMessage().contains("null output parameters provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(null, secretkey, new byte[][]{publickey}, true);
            }
        });
        assertTrue(exception.getMessage().contains("null output parameters provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(op, secretkey, new byte[][]{null}, false);
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(op, secretkey, null, false);
            }
        });
        assertTrue(exception.getMessage().contains("null recipients provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(op, secretkey, new byte[][]{null}, new byte[][][]{});
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(op, secretkey, new byte[][]{publickey}, new byte[][][]{null, null, null});
            }
        });
        assertTrue(exception.getMessage().contains("null pair provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(op, secretkey, null, new byte[][][]{});
            }
        });
        assertTrue(exception.getMessage().contains("null recipients provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageWriter(op, secretkey, new byte[][]{publickey}, null);
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        final MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey});

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                mw.addBlock(null, false);
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                mw.addBlock(null, 0, 12, false);
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        mw.destroy();
    }

    @Test
    public void reader() throws Exception {

        ByteArrayInputStream bin = new ByteArrayInputStream("test".getBytes());
        final InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        final byte[] nullKey = null;
        final byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        final byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        Exception exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageReader(ip, nullKey);
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageReader(ip, secretkey, new byte[][]{null, null});
            }
        });
        assertTrue(exception.getMessage().contains("null byte array provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageReader(ip, secretkey, null);
            }
        });
        assertTrue(exception.getMessage().contains("null pair provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageReader(null, secretkey, new byte[][]{publickey});
            }
        });
        assertTrue(exception.getMessage().contains("null input parameters provided"));

        exception = assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                new MessageReader(null);
            }
        });
        assertTrue(exception.getMessage().contains("null input parameters provided"));
    }
}
