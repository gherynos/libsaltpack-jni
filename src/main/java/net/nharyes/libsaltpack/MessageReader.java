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

import java.io.InputStream;

/**
 * The class used to decrypt or verify a message.
 */
public class MessageReader {

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
     * Creates a new MessageReader instance to decrypt a message.
     *
     * @param in                 the InputParameters with the source input stream containing the encrypted message.
     * @param recipientSecretkey the private key of the sender.
     * @throws SaltpackException
     */
    public MessageReader(InputParameters in, byte[] recipientSecretkey) throws SaltpackException {

        ptr = constructor(in, recipientSecretkey);
    }

    /**
     * Creates a new MessageReader instance to verify a signed message.
     *
     * @param in the InputParameters with the source input stream containing the message with its signature attached.
     * @throws SaltpackException
     */
    public MessageReader(InputParameters in) throws SaltpackException {

        ptr = constructor(in);
    }

    /**
     * Creates a new MessageReader instance to verify a signed message.
     *
     * @param in        the InputParameters with the source input stream containing the detached signature.
     * @param messageIn the input stream containing the message to verify.
     * @throws SaltpackException if the signature verification fails.
     */
    public MessageReader(InputParameters in, InputStream messageIn) throws SaltpackException {

        ptr = constructor(in, messageIn);
    }

    /**
     * Desctructor.
     * <p>
     * Securely deletes the allocated buffers using `sodium_memzero`.
     * <p>
     * This method has to be called when the instance is no longer required.
     */
    public void destroy() {

        if (ptr != -1) {

            destructor(ptr);
            ptr = -1;
        }
    }

    /**
     * Helper method to process all the blocks.
     *
     * @return true when there are more blocks to read, false otherwise.
     * @throws SaltpackException
     */
    public boolean hasMoreBlocks() throws SaltpackException {

        return hasMoreBlocks(ptr);
    }

    /**
     * Returns the next block of the decrypted/verified message.
     *
     * @return the decrypted/verified data.
     * @throws SaltpackException if the block cannot be decrypted or its signature is not valid.
     */
    public byte[] getBlock() throws SaltpackException {

        return getBlock(ptr);
    }

    /**
     * Returns the public keys of the recipients if they're visible (see flag {@code visibleRecipients} in {@link net.nharyes.libsaltpack.MessageWriter}).
     *
     * @return the recipients if they're visible, an empty array otherwise.
     * @throws SaltpackException
     */
    public byte[][] getRecipients() throws SaltpackException {

        return getRecipients(ptr);
    }

    /**
     * Returns the public key of the sender.
     *
     * @return the sender's public key.
     */
    public byte[] getSender() throws SaltpackException {

        return getSender(ptr);
    }

    /**
     * Sender's anonimity status (see {@link net.nharyes.libsaltpack.MessageWriter#MessageWriter(OutputParameters, byte[][])}).
     *
     * @return true if the sender of the message is intentionally anonymous, false otherwise.
     * @throws SaltpackException
     */
    public boolean isIntentionallyAnonymous() throws SaltpackException {

        return isIntentionallyAnonymous(ptr);
    }

    private native long constructor(InputParameters in, byte[] senderSecretkey) throws SaltpackException;

    private native long constructor(InputParameters in) throws SaltpackException;

    private native long constructor(InputParameters in, InputStream messageIn) throws SaltpackException;

    private native void destructor(long ptr);

    private native boolean hasMoreBlocks(long ptr) throws SaltpackException;

    private native byte[] getBlock(long ptr) throws SaltpackException;

    private native byte[][] getRecipients(long ptr) throws SaltpackException;

    private native byte[] getSender(long ptr) throws SaltpackException;

    private native boolean isIntentionallyAnonymous(long ptr) throws SaltpackException;
}