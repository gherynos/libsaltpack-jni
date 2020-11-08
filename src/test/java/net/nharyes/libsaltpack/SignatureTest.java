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

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import static org.junit.Assert.*;

public class SignatureTest {

    @Test
    public void binaryAttached() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(publickey, secretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        MessageWriter mw = new MessageWriter(op, secretkey, false);

        mw.addBlock("A simple".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock(".".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip);

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "A simple message.");

        assertArrayEquals(mr.getSender(), publickey);

        mr.destroy();
    }

    @Test
    public void binaryDetached() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(publickey, secretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        byte[] buf1 = new byte[1024];
        byte[] buf2 = new byte[1024 * 1024];
        Random r = new Random();
        r.nextBytes(buf1);
        r.nextBytes(buf2);
        ByteArrayOutputStream merged = new ByteArrayOutputStream();
        merged.write(buf1);
        merged.write(buf2);

        MessageWriter mw = new MessageWriter(op, secretkey, true);

        mw.addBlock(buf1, false);
        mw.addBlock(buf2, true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, new ByteArrayInputStream(merged.toByteArray()));

        assertArrayEquals(mr.getSender(), publickey);

        mr.destroy();
    }

    @Test
    public void armoredAttached() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(publickey, secretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(true);

        MessageWriter mw = new MessageWriter(op, secretkey, false);

        mw.addBlock("A simple".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock(".".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        assertTrue(new String(raw, "UTF-8").contains("SIGNED MESSAGE"));

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(true);

        MessageReader mr = new MessageReader(ip);

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "A simple message.");

        assertArrayEquals(mr.getSender(), publickey);

        mr.destroy();
    }

    @Test
    public void armoredDetached() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(publickey, secretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(true);

        byte[] buf1 = new byte[1024];
        byte[] buf2 = new byte[1024 * 1024];
        Random r = new Random();
        r.nextBytes(buf1);
        r.nextBytes(buf2);
        ByteArrayOutputStream merged = new ByteArrayOutputStream();
        merged.write(buf1);
        merged.write(buf2);

        MessageWriter mw = new MessageWriter(op, secretkey, true);

        mw.addBlock(buf1, false);
        mw.addBlock(buf2, true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        assertTrue(new String(raw, "UTF-8").contains("DETACHED SIGNATURE"));

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(true);

        MessageReader mr = new MessageReader(ip, new ByteArrayInputStream(merged.toByteArray()));

        assertArrayEquals(mr.getSender(), publickey);

        mr.destroy();
    }

    @Test
    public void exceptions() throws Exception {

        assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                MessageReader mr = null;
                try {

                    byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
                    byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
                    Utils.generateSignKeypair(publickey, secretkey);

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();

                    OutputParameters op = new OutputParameters(bout);
                    op.setArmored(false);

                    MessageWriter mw = new MessageWriter(op, secretkey, false);

                    mw.addBlock("A signed message".getBytes("UTF-8"), true);

                    mw.destroy();

                    byte[] raw = bout.toByteArray();
                    raw[raw.length - 80] %= 12;

                    ByteArrayInputStream bin = new ByteArrayInputStream(raw);

                    InputParameters ip = new InputParameters(bin);
                    ip.setArmored(false);

                    mr = new MessageReader(ip);

                    while (mr.hasMoreBlocks()) {

                        mr.getBlock();
                    }

                    mr.destroy();

                } finally {

                    if (mr != null)
                        mr.destroy();
                }
            }
        });

        assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                MessageReader mr = null;
                try {

                    byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
                    byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
                    Utils.generateSignKeypair(publickey, secretkey);

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();

                    OutputParameters op = new OutputParameters(bout);
                    op.setArmored(true);

                    byte[] buf1 = new byte[1024];
                    byte[] buf2 = new byte[1024 * 1024];
                    Random r = new Random();
                    r.nextBytes(buf1);
                    r.nextBytes(buf2);
                    ByteArrayOutputStream merged = new ByteArrayOutputStream();
                    merged.write(buf1);
                    merged.write(buf2);

                    MessageWriter mw = new MessageWriter(op, secretkey, true);

                    mw.addBlock(buf1, false);
                    mw.addBlock(buf2, true);

                    mw.destroy();

                    byte[] raw = bout.toByteArray();

                    ByteArrayInputStream bin = new ByteArrayInputStream(raw);

                    InputParameters ip = new InputParameters(bin);
                    ip.setArmored(true);

                    merged.write('L');

                    mr = new MessageReader(ip, new ByteArrayInputStream(merged.toByteArray()));

                    mr.destroy();

                } finally {

                    if (mr != null)
                        mr.destroy();
                }
            }
        });

        assertThrows(SaltpackException.class, new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {

                MessageReader mr = null;
                try {

                    byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
                    byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
                    Utils.generateSignKeypair(publickey, secretkey);

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();

                    OutputParameters op = new OutputParameters(bout);
                    op.setArmored(true);

                    byte[] buf1 = new byte[1024];
                    byte[] buf2 = new byte[1024 * 1024];
                    Random r = new Random();
                    r.nextBytes(buf1);
                    r.nextBytes(buf2);
                    ByteArrayOutputStream merged = new ByteArrayOutputStream();
                    merged.write(buf1);
                    merged.write(buf2);

                    MessageWriter mw = new MessageWriter(op, secretkey, true);

                    mw.addBlock(buf1, false);
                    mw.addBlock(buf2, false);

                    mw.destroy();

                    byte[] raw = bout.toByteArray();

                    ByteArrayInputStream bin = new ByteArrayInputStream(raw);

                    InputParameters ip = new InputParameters(bin);
                    ip.setArmored(true);

                    mr = new MessageReader(ip, new ByteArrayInputStream(merged.toByteArray()));

                    mr.destroy();

                } finally {

                    if (mr != null)
                        mr.destroy();
                }
            }
        });
    }
}
