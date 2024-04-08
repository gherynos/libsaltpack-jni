/*
 * Copyright 2020-2024 Luca Zanconato
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public final class Loader {

    private static final String LIB_FILENAME = "libsaltpack-jni";

    private static Path tempDir;

    private static boolean libraryLoaded;

    private Loader() {
    }

    public static void loadLibrary() {

        if (libraryLoaded) {

            return;
        }

        try {

            System.loadLibrary("saltpack-jni");
            libraryLoaded = true;

        } catch (UnsatisfiedLinkError e) {

            try {

                System.load(getLib());
                libraryLoaded = true;

            } catch (IOException ex) {

                System.err.println("Native code library failed to load.\n" + ex);  // NOPMD
                System.exit(1);  // NOPMD
            }
        }
    }

    private static String[] getPathExt() {

        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");

        String ext;
        if (os.contains("mac")) {

            ext = "dylib";

        } else if (os.contains("win")) {

            ext = "dll";

        } else {

            ext = "so";
        }

        return new String[] {
                String.format("/lib/%s/%s/%s.%s", os, arch, LIB_FILENAME, ext),
                ext
        };
    }

    private static String getLib() throws IOException {

        String[] pathExt = getPathExt();

        synchronized (LIB_FILENAME) {

            if (tempDir == null) {

                tempDir = Files.createTempDirectory(LIB_FILENAME);
            }
        }

        // create temporary library file to load
        File lib = new File(String.format("%s%s%s.%s", tempDir, File.separator, LIB_FILENAME, pathExt[1]));
        if (!lib.exists()) {

            try (InputStream in = Loader.class.getResourceAsStream(pathExt[0])) {

                if (in == null) {

                    throw new IOException("Library not found in JAR file.");
                }

                Files.copy(in, lib.toPath(), StandardCopyOption.REPLACE_EXISTING);
                lib.deleteOnExit();
            }
        }

        return lib.getAbsolutePath();
    }

    public static void main(String[] args) throws IOException {

        String[] pathExt = getPathExt();
        String path = String.format("src%1$smain%1$sresources%1$s%2$s", File.separator, pathExt[0]);

        File dest = new File(path);
        dest.mkdirs();

        String sourceFile = LIB_FILENAME + "." + pathExt[1];
        if (sourceFile.endsWith(".dll")) {

            sourceFile = "Release" + File.separator + sourceFile.replace("lib", "");
        }

        Files.copy(new File(sourceFile).toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
