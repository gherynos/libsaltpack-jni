package net.nharyes.libsaltpack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Loader {

    private static final String LIB_FILENAME = "libsaltpack-jni";

    private static Path tempDir;

    private static boolean libraryLoaded = false;

    public static void loadLibrary() {

        if (libraryLoaded)
            return;

        try {

            System.loadLibrary("saltpack-jni");
            libraryLoaded = true;

        } catch (UnsatisfiedLinkError e) {

            try {

                System.load(getLib());
                libraryLoaded = true;

            } catch (IOException ex) {

                System.err.println("Native code library failed to load.\n" + ex);
                System.exit(1);
            }
        }
    }

    private static String[] getPathExt() {

        String os = System.getProperty("os.name").toLowerCase().replaceAll(" ", "_");
        String arch = System.getProperty("os.arch").toLowerCase().replaceAll(" ", "_");

        String ext = "so";
        if (os.contains("mac")) {

            ext = "dylib";

        } else if (os.contains("win")) {

            ext = "dll";
        }

        return new String[] {
                String.format("/lib/%s/%s/%s.%s", os, arch, LIB_FILENAME, ext),
                ext
        };
    }

    private static String getLib() throws IOException {

        String[] pathExt = getPathExt();

        // local execution (not in Jar)
        URL path = Loader.class.getResource(pathExt[0]);
        if (path.getProtocol().equals("file"))
            return path.toString().replace("file:", "");

        if (tempDir == null)
            tempDir = Files.createTempDirectory(LIB_FILENAME);

        // create temporary library file to load
        File lib = new File(String.format("%s%s%s.%s", tempDir, File.separator, LIB_FILENAME, pathExt[1]));
        if (!lib.exists()) {

            try (InputStream in = Loader.class.getResourceAsStream(pathExt[0])) {

                if (in == null)
                    throw new IOException("Library not found in JAR file.");

                Files.copy(in, lib.toPath(), StandardCopyOption.REPLACE_EXISTING);
                lib.deleteOnExit();
            }
        }

        return lib.getAbsolutePath();
    }

    public static void main(String[] args) throws Exception {

        String[] pathExt = getPathExt();
        String path = String.format("src%1$smain%1$sresources%1$s%2$s", File.separator, pathExt[0]);

        File dest = new File(path);
        dest.mkdirs();

        String sourceFile = LIB_FILENAME + "." + pathExt[1];
        if (sourceFile.endsWith(".dll"))
            sourceFile = "Release" + File.separator + sourceFile.replace("lib", "");

        Files.copy(new File(sourceFile).toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
