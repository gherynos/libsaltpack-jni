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

package net.nharyes.libsaltpack;

import java.io.InputStream;

/**
 * POJO class containing the InputStream and the parameters to parse BaseX armored content.
 * <p>
 * The alphabet used is BASE62.
 * </p>
 */
public class InputParameters {  // NOPMD

    private final InputStream inputStream;

    private boolean armored;

    private String app;

    /**
     * Creates a new InputParameters instance for a specific InputStream.
     *
     * @param inputStream the source input stream containing binary or armored data.
     */
    public InputParameters(InputStream inputStream) {

        this.inputStream = inputStream;
    }

    /**
     * Returns the instance of {@link java.io.InputStream}.
     *
     * @return the input stream.
     */
    public InputStream getInputStream() {

        return inputStream;
    }

    /**
     * Armored content flag.
     *
     * @return true if the input stream contains armored content, false otherwise.
     */
    public boolean isArmored() {

        return armored;
    }

    /**
     * Armored content flag.
     *
     * @param armored set true if the {@code InputStream} contains armored code, false otherwise.
     */
    public void setArmored(boolean armored) {

        this.armored = armored;
    }

    /**
     * Returns the name of the application (used when {@code armored} is set to true).
     *
     * @return the application name that will be verified in the header/footer of the message contained.
     */
    public String getApp() {

        return app;
    }

    /**
     * Sets the name of the application (used when {@code armored} is set to true).
     *
     * @param app the application name that will be verified in the header/footer of the message contained.
     */
    public void setApp(String app) {

        this.app = app;
    }
}
