/*
 * Copyright 2020 Luca Zanconato
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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;

import static org.junit.Assert.*;

public class A_LoaderCopyTest {

    @Test
    public void copyLibrary() throws Exception {

        String[] pathExt = Loader.getPathExt();
        String path = String.format("src%1$smain%1$sresources%1$s%2$s", File.separator, pathExt[0]);

        new File(path).delete();

        assertFalse(new File(path).exists());

        String sourceFile = Loader.LIB_FILENAME + "." + pathExt[1];
        if (sourceFile.endsWith(".dll")) {

            sourceFile = "Release" + File.separator + sourceFile.replace("lib", "");
        }
        assertTrue(new File(sourceFile).exists());

        Loader.main(new String[] {});

        assertTrue(new File(path).exists());
    }
}
