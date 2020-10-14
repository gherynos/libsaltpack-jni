/*
 * Copyright 2016-2020 Luca Zanconato
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

import java.nio.ByteBuffer;

/**
 * The class used to encrypt or sign a message.
 */
public class MessageWriter {  // NOPMD

    static {

        Loader.loadLibrary();
    }

    private ByteBuffer ptr;  // NOPMD

    /**
     * Creates a new MessageWriter instance to encrypt a message.
     *
     * @param op                the OutputParameters with the destination output stream that will contain the encrypted data.
     * @param senderSecretkey   the private key of the sender, generated by {@link net.nharyes.libsaltpack.Utils#generateKeypair(byte[] publickey, byte[] secretkey)}.
     * @param recipients        the list of public keys of the recipients.
     * @param visibleRecipients if true, the public keys of the recipients will be visible in the encrypted message.
     * @throws SaltpackException
     */
    public MessageWriter(OutputParameters op, byte[] senderSecretkey, byte[][] recipients, boolean visibleRecipients) throws SaltpackException {

        ptr = constructor(op, senderSecretkey, recipients, visibleRecipients);
    }

    /**
     * Creates a new MessageWriter instance to encrypt a message.
     * The recipients public keys will be visible in the encrypted message.
     *
     * @param op              the OutputParameters with the destination output stream that will contain the encrypted data.
     * @param senderSecretkey the private key of the sender, generated by {@link net.nharyes.libsaltpack.Utils#generateKeypair(byte[] publickey, byte[] secretkey)}.
     * @param recipients      the list of public keys of the recipients.
     * @throws SaltpackException
     */
    public MessageWriter(OutputParameters op, byte[] senderSecretkey, byte[][] recipients) throws SaltpackException {

        ptr = constructor(op, senderSecretkey, recipients);
    }

    /**
     * Creates a new MessageWriter instance to encrypt a message remaining anonymous.
     *
     * @param op                the OutputParameters with the destination output stream that will contain the encrypted data.
     * @param recipients        the list of public keys of the recipients.
     * @param visibleRecipients if true, the public keys of the recipients will be visible in the encrypted message.
     * @throws SaltpackException
     */
    public MessageWriter(OutputParameters op, byte[][] recipients, boolean visibleRecipients) throws SaltpackException {

        ptr = constructor(op, recipients, visibleRecipients);
    }

    /**
     * Creates a new MessageWriter instance to encrypt a message remaining anonymous.
     * The recipients public keys will be visible in the encrypted message.
     *
     * @param op         the OutputParameters with the destination output stream that will contain the encrypted data.
     * @param recipients the list of public keys of the recipients.
     * @throws SaltpackException
     */
    public MessageWriter(OutputParameters op, byte[][] recipients) throws SaltpackException {

        ptr = constructor(op, recipients);
    }

    /**
     * Creates a new MessageWriter instance to sign a message.
     *
     * @param op                 the OutputParameters with the destination output stream that will contain the signed data.
     * @param senderSecretkey    the private key of the sender, generated by {@link net.nharyes.libsaltpack.Utils#generateSignKeypair(byte[] publickey, byte[] secretkey)}.
     * @param detatchedSignature attached/detached signature flag.
     * @throws SaltpackException
     */
    public MessageWriter(OutputParameters op, byte[] senderSecretkey, boolean detatchedSignature) throws SaltpackException {

        ptr = constructor(op, senderSecretkey, detatchedSignature);
    }

    /**
     * Creates a new MessageWriter instance to signcrypt a message.
     *
     * @param op                   the OutputParameters with the destination output stream that will contain the signcrypted data.
     * @param senderSecretkey      the private key of the sender, generated by {@link net.nharyes.libsaltpack.Utils#generateSignKeypair(byte[] publickey, byte[] secretkey)}.
     * @param recipientsPublickeys the list of Curve25519 public keys of the recipients. The list can be empty.
     * @param symmetricKeys        the list of symmetric keys of the recipients: the first array is treated as the identifier, the second as the key itself. The list can be empty.
     * @throws SaltpackException
     */
    public MessageWriter(OutputParameters op, byte[] senderSecretkey, byte[][] recipientsPublickeys, byte[][][] symmetricKeys) throws SaltpackException {

        ptr = constructor(op, senderSecretkey, recipientsPublickeys, symmetricKeys);
    }

    /**
     * Creates a new MessageWriter instance to signcrypt a message remaining anonymous.
     *
     * @param op                   the OutputParameters with the destination output stream that will contain the signcrypted data.
     * @param recipientsPublickeys the list of Curve25519 public keys of the recipients. The list can be empty.
     * @param symmetricKeys        the list of symmetric keys of the recipients: the first array is treated as the identifier, the second as the key itself. The list can be empty.
     * @throws SaltpackException
     */
    public MessageWriter(OutputParameters op, byte[][] recipientsPublickeys, byte[][][] symmetricKeys) throws SaltpackException {

        ptr = constructor(op, recipientsPublickeys, symmetricKeys);
    }

    /**
     * Desctructor.
     * <p>
     * Securely deletes the allocated buffers using `sodium_memzero`.
     * </p>
     * <p>
     * This method has to be called when the instance is no longer required.
     * </p>
     */
    public void destroy() {

        if (ptr != null) {

            destructor(ptr);
            ptr = null;  // NOPMD
        }
    }

    /**
     * Adds a block to the current message.
     *
     * @param data    the data for the block, maximum 1MB.
     * @param isFinal the flag defining the last packet of the message.
     * @throws SaltpackException
     */
    public void addBlock(byte[] data, boolean isFinal) throws SaltpackException {

        addBlock(ptr, data, 0, data.length, isFinal);
    }

    /**
     * Adds a block to the current message.
     *
     * @param data    the data for the block, maximum 1MB.
     * @param off     the start offset in the data.
     * @param len     the number of bytes to write.
     * @param isFinal the flag defining the last packet of the message.
     * @throws SaltpackException
     */
    public void addBlock(byte[] data, int off, int len, boolean isFinal) throws SaltpackException {

        addBlock(ptr, data, off, len, isFinal);
    }

    private native ByteBuffer constructor(OutputParameters op, byte[] senderSecretkey, byte[][] recipients, boolean visibleRecipients) throws SaltpackException;

    private native ByteBuffer constructor(OutputParameters op, byte[] senderSecretkey, byte[][] recipients) throws SaltpackException;

    private native ByteBuffer constructor(OutputParameters op, byte[][] recipients, boolean visibleRecipients) throws SaltpackException;

    private native ByteBuffer constructor(OutputParameters op, byte[][] recipients) throws SaltpackException;

    private native ByteBuffer constructor(OutputParameters op, byte[] senderSecretkey, boolean detatchedSignature) throws SaltpackException;

    private native ByteBuffer constructor(OutputParameters op, byte[] senderSecretkey, byte[][] recipientsPublickeys, byte[][][] symmetricKeys) throws SaltpackException;

    private native ByteBuffer constructor(OutputParameters op, byte[][] recipientsPublickeys, byte[][][] symmetricKeys) throws SaltpackException;

    private native void destructor(ByteBuffer ptr);

    private native void addBlock(ByteBuffer ptr, byte[] data, int off, int len, boolean isFinal) throws SaltpackException;
}
