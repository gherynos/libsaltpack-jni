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
 * The class used to encrypt or sign a message.
 */
public class MessageWriter {

    static {

        try {

            System.loadLibrary("saltpack-jni");

        } catch (UnsatisfiedLinkError e) {

            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    private long ptr = -1;

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
     * Desctructor.
     * <p>
     * Securely deletes the allocated buffers using `sodium_memzero`.
     * </p>
     * <p>
     * This method has to be called when the instance is no longer required.
     * </p>
     */
    public void destroy() {

        if (ptr != -1) {

            destructor(ptr);
            ptr = -1;
        }
    }

    /**
     * Adds a block to the current message.
     *
     * @param data the data for the block, maximum 1MB.
     * @param isFinal the flag defining the last packet of the message.
     * @throws SaltpackException
     */
    public void addBlock(byte[] data, boolean isFinal) throws SaltpackException {

        addBlock(ptr, data, isFinal);
    }

    private native long constructor(OutputParameters op, byte[] senderSecretkey, byte[][] recipients, boolean visibleRecipients) throws SaltpackException;

    private native long constructor(OutputParameters op, byte[] senderSecretkey, byte[][] recipients) throws SaltpackException;

    private native long constructor(OutputParameters op, byte[][] recipients, boolean visibleRecipients) throws SaltpackException;

    private native long constructor(OutputParameters op, byte[][] recipients) throws SaltpackException;

    private native long constructor(OutputParameters op, byte[] senderSecretkey, boolean detatchedSignature) throws SaltpackException;

    private native void destructor(long ptr);

    private native void addBlock(long ptr, byte[] data, boolean isFinal) throws SaltpackException;
}
