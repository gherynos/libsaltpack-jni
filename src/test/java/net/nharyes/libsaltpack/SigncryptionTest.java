/*
 * Copyright 2017 Luca Zanconato
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

public class SigncryptionTest {

    @Test
    public void binary1() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(publickey, secretkey);

        byte[] receiverSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] receiverPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(receiverPublickey, receiverSecretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{receiverPublickey}, new byte[][][]{});

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock(".".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, receiverSecretkey, new byte[][]{});

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message.");

        assertArrayEquals(mr.getSender(), publickey);

        assertFalse(mr.isIntentionallyAnonymous());

        mr.destroy();
    }

    @Test
    public void binary2() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(publickey, secretkey);

        byte[] symmetricKey = Utils.generateRandomBytes(Constants.CRYPTO_SECRETBOX_KEYBYTES);
        byte[] symmetricKey2 = Utils.generateRandomBytes(Constants.CRYPTO_SECRETBOX_KEYBYTES);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{}, new byte[][][]{
                {{'i', 'd', '2', '1'}, symmetricKey},
                {{'i', 'd', '3'}, symmetricKey2}
        });

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock(".".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, new byte[]{}, new byte[][]{{'i', 'd', '3'}, symmetricKey2});

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message.");

        assertArrayEquals(mr.getSender(), publickey);

        assertFalse(mr.isIntentionallyAnonymous());

        mr.destroy();
    }

    @Test
    public void binary3() throws Exception {

        byte[] receiverSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] receiverPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(receiverPublickey, receiverSecretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        MessageWriter mw = new MessageWriter(op, new byte[][]{receiverPublickey}, new byte[][][]{});

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock("!".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, receiverSecretkey, new byte[][]{});

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message!");

        assertArrayEquals(mr.getSender(), new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

        assertTrue(mr.isIntentionallyAnonymous());

        mr.destroy();
    }

    @Test
    public void binary4() throws Exception {

        byte[] symmetricKey = Utils.generateRandomBytes(Constants.CRYPTO_SECRETBOX_KEYBYTES);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        MessageWriter mw = new MessageWriter(op, new byte[][]{}, new byte[][][]{
                {{'a', 'a', 'a'}, symmetricKey}
        });

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock("?".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, new byte[]{}, new byte[][]{{'a', 'a', 'a'}, symmetricKey});

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message?");

        assertArrayEquals(mr.getSender(), new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

        assertTrue(mr.isIntentionallyAnonymous());

        mr.destroy();
    }

    @Test
    public void binary5() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(publickey, secretkey);

        byte[] receiverSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] receiverPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(receiverPublickey, receiverSecretkey);

        byte[] symmetricKey = Utils.generateRandomBytes(Constants.CRYPTO_SECRETBOX_KEYBYTES);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(false);

        byte[] id = Utils.generateRandomBytes(32);
        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{receiverPublickey}, new byte[][][]{
                {id, symmetricKey},
        });

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock("[*]".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(false);

        MessageReader mr = new MessageReader(ip, new byte[]{}, new byte[][]{id, symmetricKey});

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message[*]");

        assertArrayEquals(mr.getSender(), publickey);

        assertFalse(mr.isIntentionallyAnonymous());

        mr.destroy();

        ByteArrayInputStream bin2 = new ByteArrayInputStream(raw);

        InputParameters ip2 = new InputParameters(bin2);
        ip2.setArmored(false);

        MessageReader mr2 = new MessageReader(ip2, receiverSecretkey, new byte[][]{});

        StringBuilder sb2 = new StringBuilder();
        while (mr2.hasMoreBlocks()) {

            sb2.append(new String(mr2.getBlock(), "UTF-8"));
        }

        assertEquals(sb2.toString(), "Sample message[*]");

        assertArrayEquals(mr2.getSender(), publickey);

        assertFalse(mr2.isIntentionallyAnonymous());

        mr2.destroy();
    }

    @Test
    public void armored1() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(publickey, secretkey);

        byte[] receiverSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] receiverPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(receiverPublickey, receiverSecretkey);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(true);
        op.setLettersInWords(5);
        op.setWordsInPhrase(9);

        byte[] buf1 = new byte[1024 * 1024];
        byte[] buf2 = new byte[1024 * 1024];
        Random r = new Random();
        r.nextBytes(buf1);
        r.nextBytes(buf2);
        ByteArrayOutputStream merged = new ByteArrayOutputStream();
        merged.write(buf1);
        merged.write(buf2);

        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{receiverPublickey}, new byte[][][]{});

        mw.addBlock(buf1, false);
        mw.addBlock(buf2, true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(true);

        MessageReader mr = new MessageReader(ip, receiverSecretkey, new byte[][]{});

        ByteArrayOutputStream dec = new ByteArrayOutputStream();
        while (mr.hasMoreBlocks()) {

            dec.write(mr.getBlock());
        }

        assertArrayEquals(dec.toByteArray(), merged.toByteArray());

        assertArrayEquals(mr.getSender(), publickey);

        assertFalse(mr.isIntentionallyAnonymous());

        mr.destroy();
    }

    @Test
    public void armored2() throws Exception {

        byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
        byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
        Utils.generateSignKeypair(publickey, secretkey);

        byte[] receiverSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
        byte[] receiverPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
        Utils.generateKeypair(receiverPublickey, receiverSecretkey);

        byte[] symmetricKey = Utils.generateRandomBytes(Constants.CRYPTO_SECRETBOX_KEYBYTES);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(true);

        byte[] id = Utils.generateRandomBytes(32);
        MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{receiverPublickey}, new byte[][][]{
                {id, symmetricKey},
        });

        mw.addBlock("Sample message [*]".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(true);

        MessageReader mr = new MessageReader(ip, new byte[]{}, new byte[][]{id, symmetricKey});

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message [*]");

        assertArrayEquals(mr.getSender(), publickey);

        assertFalse(mr.isIntentionallyAnonymous());

        mr.destroy();

        ByteArrayInputStream bin2 = new ByteArrayInputStream(raw);

        InputParameters ip2 = new InputParameters(bin2);
        ip2.setArmored(true);

        MessageReader mr2 = new MessageReader(ip2, receiverSecretkey, new byte[][]{});

        StringBuilder sb2 = new StringBuilder();
        while (mr2.hasMoreBlocks()) {

            sb2.append(new String(mr2.getBlock(), "UTF-8"));
        }

        assertEquals(sb2.toString(), "Sample message [*]");

        assertArrayEquals(mr2.getSender(), publickey);

        assertFalse(mr2.isIntentionallyAnonymous());

        mr2.destroy();
    }

    @Test
    public void armored3() throws Exception {

        byte[] symmetricKey = Utils.generateRandomBytes(Constants.CRYPTO_SECRETBOX_KEYBYTES);
        byte[] symmetricKey2 = Utils.generateRandomBytes(Constants.CRYPTO_SECRETBOX_KEYBYTES);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputParameters op = new OutputParameters(bout);
        op.setArmored(true);
        op.setApp("THEAPP");

        MessageWriter mw = new MessageWriter(op, new byte[][]{}, new byte[][][]{
                {{'b', 'b'}, symmetricKey2},
                {{'a', 'a', 'a'}, symmetricKey}
        });

        mw.addBlock("Sample".getBytes("UTF-8"), false);
        mw.addBlock(" message".getBytes("UTF-8"), false);
        mw.addBlock("?".getBytes("UTF-8"), true);

        mw.destroy();

        byte[] raw = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(raw);

        InputParameters ip = new InputParameters(bin);
        ip.setArmored(true);
        ip.setApp("THEAPP");

        MessageReader mr = new MessageReader(ip, new byte[]{}, new byte[][]{{'a', 'a', 'a'}, symmetricKey});

        StringBuilder sb = new StringBuilder();
        while (mr.hasMoreBlocks()) {

            sb.append(new String(mr.getBlock(), "UTF-8"));
        }

        assertEquals(sb.toString(), "Sample message?");

        assertArrayEquals(mr.getSender(), new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

        assertTrue(mr.isIntentionallyAnonymous());

        mr.destroy();
    }

    @Test
    public void exceptions() throws Exception {

        MessageReader mr = null;
        try {

            byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
            byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
            Utils.generateSignKeypair(publickey, secretkey);

            byte[] receiverSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
            byte[] receiverPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
            Utils.generateKeypair(receiverPublickey, receiverSecretkey);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            OutputParameters op = new OutputParameters(bout);
            op.setArmored(false);

            MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{receiverPublickey}, new byte[][][]{});

            mw.addBlock("Sample".getBytes("UTF-8"), false);
            mw.addBlock(" message".getBytes("UTF-8"), false);
            mw.addBlock(".".getBytes("UTF-8"), true);

            mw.destroy();

            byte[] raw = bout.toByteArray();

            ByteArrayInputStream bin = new ByteArrayInputStream(raw);

            InputParameters ip = new InputParameters(bin);
            ip.setArmored(false);

            Utils.generateKeypair(receiverPublickey, receiverSecretkey);

            mr = new MessageReader(ip, receiverSecretkey, new byte[][]{});

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

            byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
            byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
            Utils.generateSignKeypair(publickey, secretkey);

            byte[] symmetricKey = Utils.generateRandomBytes(Constants.CRYPTO_SECRETBOX_KEYBYTES);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            OutputParameters op = new OutputParameters(bout);
            op.setArmored(true);
            op.setApp("AAA");
            op.setWordsInPhrase(15);
            op.setLettersInWords(23);

            MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{}, new byte[][][]{{{'a'}, symmetricKey}});

            mw.addBlock("Sample".getBytes("UTF-8"), false);
            mw.addBlock(" message".getBytes("UTF-8"), false);
            mw.addBlock(".".getBytes("UTF-8"), true);

            mw.destroy();

            byte[] raw = bout.toByteArray();

            ByteArrayInputStream bin = new ByteArrayInputStream(raw);

            InputParameters ip = new InputParameters(bin);
            ip.setArmored(true);
            ip.setApp("BBB");

            mr = new MessageReader(ip, new byte[]{}, new byte[][]{{'a'}, symmetricKey});

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

            MessageWriter mw = new MessageWriter(op, sk, new byte[][]{}, new byte[][][]{});

            throw new Exception();

        } catch (SaltpackException ex) {

            // OK
        }

        try {

            byte[] sk = new byte[3];

            OutputParameters op = new OutputParameters(new ByteArrayOutputStream());

            MessageWriter mw = new MessageWriter(op, new byte[][]{}, new byte[][][]{{{'a'}, sk}});

            throw new Exception();

        } catch (SaltpackException ex) {

            // OK
        }

        try {

            byte[] sk = new byte[3];

            OutputParameters op = new OutputParameters(new ByteArrayOutputStream());

            MessageWriter mw = new MessageWriter(op, new byte[][]{sk}, new byte[][][]{});

            throw new Exception();

        } catch (SaltpackException ex) {

            // OK
        }

        mr = null;
        try {

            byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
            byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
            Utils.generateSignKeypair(publickey, secretkey);

            byte[] receiverSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
            byte[] receiverPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
            Utils.generateKeypair(receiverPublickey, receiverSecretkey);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            OutputParameters op = new OutputParameters(bout);
            op.setArmored(true);
            op.setApp("AAA");
            op.setWordsInPhrase(15);
            op.setLettersInWords(23);

            MessageWriter mw = new MessageWriter(op, secretkey, new byte[][]{receiverPublickey}, new byte[][][]{});

            mw.addBlock("Sample".getBytes("UTF-8"), false);
            mw.addBlock(" message".getBytes("UTF-8"), false);

            mw.destroy();

            byte[] raw = bout.toByteArray();

            ByteArrayInputStream bin = new ByteArrayInputStream(raw);

            InputParameters ip = new InputParameters(bin);
            ip.setArmored(true);
            ip.setApp("AAA");

            mr = new MessageReader(ip, receiverSecretkey, new byte[][]{});

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
