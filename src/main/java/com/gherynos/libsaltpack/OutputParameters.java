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

import java.io.OutputStream;

/**
 * POJO class containing the OutputStream and the parameters to generate BaseX armored content.
 * <p>
 * The alphabet used is BASE62.
 * </p>
 */
public class OutputParameters {  // NOPMD

    private final OutputStream outputStream;

    private boolean armored;

    private String app;

    private int lettersInWords = -1;

    private int wordsInPhrase = -1;

    /**
     * Creates a new OutputParameters instance for a specific OutputStream.
     *
     * @param outputStream the destination output stream that will contain binary or armored data.
     */
    public OutputParameters(OutputStream outputStream) {

        this.outputStream = outputStream;
    }

    /**
     * Returns the instance of {@link java.io.OutputStream}.
     *
     * @return the output stream.
     */
    public OutputStream getOutputStream() {

        return outputStream;
    }

    /**
     * Armored content flag.
     *
     * @return true if the output stream will contain armored content, false otherwise.
     */
    public boolean isArmored() {

        return armored;
    }

    /**
     * Armored content flag.
     *
     * @param armored set true if the {@code OutputStream} will contain armored code, false otherwise.
     */
    public void setArmored(boolean armored) {

        this.armored = armored;
    }

    /**
     * Returns the name of the application (used when {@code armored} is set to true).
     *
     * @return the application name that will be added in the header and footer of the message.
     */
    public String getApp() {

        return app;
    }

    /**
     * Sets the name of the application (used when {@code armored} is set to true).
     *
     * @param app the application name that will be added to the header and footer of the message.
     */
    public void setApp(String app) {

        this.app = app;
    }

    /**
     * Returns the total number of letters in a word (used when {@code armored} is set to true).
     *
     * @return the number of letters before producing a space during the armoring.
     */
    public int getLettersInWords() {

        return lettersInWords;
    }

    /**
     * Sets the total number of letters in a word (used when {@code armored} is set to true).
     *
     * @param lettersInWords the number of letters before producing a space during the armoring.
     */
    public void setLettersInWords(int lettersInWords) {

        this.lettersInWords = lettersInWords;
    }

    /**
     * Returns the total number of words in a phrase (used when {@code armored} is set to true).
     *
     * @return the number of words before producing a new line during the armoring.
     */
    public int getWordsInPhrase() {

        return wordsInPhrase;
    }

    /**
     * Sets the total number of words in a phrase (used when {@code armored} is set to true).
     *
     * @param wordsInPhrase the number of words before producing a new line during the armoring.
     */
    public void setWordsInPhrase(int wordsInPhrase) {

        this.wordsInPhrase = wordsInPhrase;
    }

    /**
     * Internal method used by the JNI interface.
     *
     * @return parameters populated.
     */
    public boolean intParamsPopulated() {

        return lettersInWords != -1 && wordsInPhrase != -1;
    }
}
