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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import static org.junit.Assert.*;

public class EncryptionTest {

    @Test
    public void binary1() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey});

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock(".".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, secretkey);

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message.");

        assertArrayEquals(mr.getSender(), publickey);

        assertArrayEquals(mr.getRecipients(), new byte[][]{publickey});

        mr.destroy();
    }

    @Test
    public void binary2() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        byte[] rSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] rPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(rPublickey, rSecretkey);

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

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey, rPublickey});

        mw.addBlock(buf1, false);
        mw.addBlock(buf2, true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, rSecretkey);

        ByteArrayOutputStream dec = new ByteArrayOutputStream();
        while (mr.hasMoreBlocks()) {

            dec.write(mr.getBlock());
        }

        assertArrayEquals(dec.toByteArray(), merged.toByteArray());

        assertArrayEquals(mr.getSender(), publickey);

        assertArrayEquals(mr.getRecipients(), new byte[][]{publickey, rPublickey});

        mr.destroy();
    }

    @Test
    public void binary3() throws Exception {

        byte[] rSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] rPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(rPublickey, rSecretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        byte[] buf1 = new byte[1024];
        Random r = new Random();
        r.nextBytes(buf1);

        MessageWriter mw = new MessageWriter(op, new byte[][]{rPublickey});

        mw.addBlock(buf1, true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, rSecretkey);

        ByteArrayOutputStream dec = new ByteArrayOutputStream();
        while (mr.hasMoreBlocks()) {

            dec.write(mr.getBlock());
        }

        assertArrayEquals(dec.toByteArray(), buf1);

        assertEquals(mr.getSender().length, Constants.CRYPTO_BOX_PUBLICKEYBYTES);

        assertArrayEquals(mr.getRecipients(), new byte[][]{rPublickey});

        assertTrue(mr.isIntentionallyAnonymous());

        mr.destroy();
    }

    @Test
    public void binary4() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey}, false);

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock(".".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, secretkey);

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message.");

        assertArrayEquals(mr.getSender(), publickey);

        assertEquals(mr.getRecipients().length, 1);
        assertEquals(mr.getRecipients()[0].length, 0);

        mr.destroy();
    }

    @Test
    public void binary5() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        byte[] rSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] rPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(rPublickey, rSecretkey);

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

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey, rPublickey}, false);

        mw.addBlock(buf1, false);
        mw.addBlock(buf2, true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, rSecretkey);

        ByteArrayOutputStream dec = new ByteArrayOutputStream();
        while (mr.hasMoreBlocks()) {

            dec.write(mr.getBlock());
        }

        assertArrayEquals(dec.toByteArray(), merged.toByteArray());

        assertArrayEquals(mr.getSender(), publickey);

        assertEquals(mr.getRecipients().length, 2);
        assertEquals(mr.getRecipients()[0].length, 0);
        assertEquals(mr.getRecipients()[1].length, 0);

        mr.destroy();
    }

    @Test
    public void binary6() throws Exception {

        byte[] rSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] rPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(rPublickey, rSecretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        byte[] buf1 = new byte[1024];
        Random r = new Random();
        r.nextBytes(buf1);

        MessageWriter mw = new MessageWriter(op, new byte[][]{rPublickey}, false);

        mw.addBlock(buf1, true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, rSecretkey);

        ByteArrayOutputStream dec = new ByteArrayOutputStream();
        while (mr.hasMoreBlocks()) {

            dec.write(mr.getBlock());
        }

        assertArrayEquals(dec.toByteArray(), buf1);

        assertEquals(mr.getSender().length, Constants.CRYPTO_BOX_PUBLICKEYBYTES);

        assertEquals(mr.getRecipients().length, 1);
        assertEquals(mr.getRecipients()[0].length, 0);

        assertTrue(mr.isIntentionallyAnonymous());

        mr.destroy();
    }

    @Test
    public void armored1() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(true);

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey});

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock(" 2.".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(true);

        MessageReader mr = new MessageReader(ip, secretkey);

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message 2.");

        assertArrayEquals(mr.getSender(), publickey);

        assertArrayEquals(mr.getRecipients(), new byte[][]{publickey});

        mr.destroy();
    }

    @Test
    public void armored2() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        byte[] rSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] rPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(rPublickey, rSecretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(true);
        op.setLettersInWords(2);
        op.setWordsInPhrase(15);

        byte[] buf1 = new byte[1024 * 1024];
        byte[] buf2 = new byte[1024];
        Random r = new Random();
        r.nextBytes(buf1);
        r.nextBytes(buf2);
        ByteArrayOutputStream merged = new ByteArrayOutputStream();
        merged.write(buf1);
        merged.write(buf2);

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey, rPublickey});

        mw.addBlock(buf1, false);
        mw.addBlock(buf2, true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(true);

        MessageReader mr = new MessageReader(ip, rSecretkey);

        ByteArrayOutputStream dec = new ByteArrayOutputStream();
        while (mr.hasMoreBlocks()) {

            dec.write(mr.getBlock());
        }

        assertArrayEquals(dec.toByteArray(), merged.toByteArray());

        assertArrayEquals(mr.getSender(), publickey);

        assertArrayEquals(mr.getRecipients(), new byte[][]{publickey, rPublickey});

        mr.destroy();
    }

    @Test
    public void armored3() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        byte[] rSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] rPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(rPublickey, rSecretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(true);
        op.setApp("SAMPLE");

        byte[] buf1 = new byte[1024];
        byte[] buf2 = new byte[1024 * 1024];
        Random r = new Random();
        r.nextBytes(buf1);
        r.nextBytes(buf2);
        ByteArrayOutputStream merged = new ByteArrayOutputStream();
        merged.write(buf1);
        merged.write(buf2);

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey, rPublickey}, false);

        mw.addBlock(buf1, false);
        mw.addBlock(buf2, true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(true);
        ip.setApp("SAMPLE");

        MessageReader mr = new MessageReader(ip, rSecretkey);

        ByteArrayOutputStream dec = new ByteArrayOutputStream();
        while (mr.hasMoreBlocks()) {

            dec.write(mr.getBlock());
        }

        assertArrayEquals(dec.toByteArray(), merged.toByteArray());

        assertArrayEquals(mr.getSender(), publickey);

        assertEquals(mr.getRecipients().length, 2);
        assertEquals(mr.getRecipients()[0].length, 0);
        assertEquals(mr.getRecipients()[1].length, 0);

        mr.destroy();
    }

    @Test
    public void armored4() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(publickey, secretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(true);
        op.setApp("AAA");
        op.setWordsInPhrase(15);
        op.setLettersInWords(23);

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey}, false);

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock(".".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(true);
        ip.setApp("AAA");

        MessageReader mr = new MessageReader(ip, secretkey);

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message.");

        assertArrayEquals(mr.getSender(), publickey);

        assertEquals(mr.getRecipients().length, 1);
        assertEquals(mr.getRecipients()[0].length, 0);

        mr.destroy();
    }

    @Test
    public void exceptions() throws Exception {

        try {

            byte[] secretkey = new byte[2];
            byte[] publickey = new byte[2];

            Utils.generateKeypair(publickey, secretkey);
            throw new Exception();

        } catch (SaltpackException ex) {

            // OK
        }

        MessageReader mr = null;
        try {

            byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
            byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
            Utils.generateKeypair(publickey, secretkey);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            OutputParameters op = new OutputParameters(bout);
            op.setArmored(false);

            MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey});

            mw.addBlock("Sample".getBytes("UTF-8"), false);
            mw.addBlock(" message".getBytes("UTF-8"), false);
            mw.addBlock(".".getBytes("UTF-8"), true);

            mw.destroy();

            byte[] raw = bout.toByteArray();

            ByteArrayInputStream bin = new ByteArrayInputStream(raw);

            InputParameters ip = new InputParameters(bin);
            ip.setArmored(false);

            Utils.generateKeypair(publickey, secretkey);

            mr = new MessageReader(ip, secretkey);

            mr.destroy();

            throw new Exception();

        } catch (SaltpackException ex) {

            // OK

        } finally {

            if (mr != null)
                mr.destroy();
        }

        mr = null;
        try {

            byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
            byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
            Utils.generateKeypair(publickey, secretkey);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            OutputParameters op = new OutputParameters(bout);
            op.setArmored(true);
            op.setApp("AAA");
            op.setWordsInPhrase(15);
            op.setLettersInWords(23);

            MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey}, false);

            mw.addBlock("Sample".getBytes("UTF-8"), false);
            mw.addBlock(" message".getBytes("UTF-8"), false);
            mw.addBlock(".".getBytes("UTF-8"), true);

            mw.destroy();

            byte[] raw = bout.toByteArray();

            ByteArrayInputStream bin = new ByteArrayInputStream(raw);

            InputParameters ip = new InputParameters(bin);
            ip.setArmored(true);
            ip.setApp("BBB");

            mr = new MessageReader(ip, secretkey);

            mr.destroy();

            throw new Exception();

        } catch (SaltpackException ex) {

            // OK

        } finally {

            if (mr != null)
                mr.destroy();
        }

        try {

            byte[] sk = new byte[3];

            OutputParameters op = new OutputParameters(new ByteArrayOutputStream());

            MessageWriter mw = new MessageWriter(op, sk, new byte[][]{});

            throw new Exception();

        } catch (SaltpackException ex) {

            // OK
        }

        mr = null;
        try {

            byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
            byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
            Utils.generateKeypair(publickey, secretkey);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            OutputParameters op = new OutputParameters(bout);
            op.setArmored(true);
            op.setApp("AAA");
            op.setWordsInPhrase(15);
            op.setLettersInWords(23);

            MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{publickey}, false);

            mw.addBlock("Sample".getBytes("UTF-8"), false);
            mw.addBlock(" message".getBytes("UTF-8"), false);

            mw.destroy();

            byte[] raw = bout.toByteArray();

            ByteArrayInputStream bin = new ByteArrayInputStream(raw);

            InputParameters ip = new InputParameters(bin);
            ip.setArmored(true);
            ip.setApp("AAA");

            mr = new MessageReader(ip, secretkey);

            mr.destroy();

            throw new Exception();

        } catch (SaltpackException ex) {

            // OK

        } finally {

            if (mr != null)
                mr.destroy();
        }
    }
}
