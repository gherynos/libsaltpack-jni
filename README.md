libsaltpack-jni
===============
A Java Native Interface wrapper for [libsaltpack](https://github.com/Gherynos/libsaltpack).

Dependencies
------------

* [libsaltpack](https://github.com/Gherynos/libsaltpack)
* [libsodium](https://download.libsodium.org/doc/) >= 1.0.9
* [msgpack](https://github.com/msgpack/msgpack-c) >= 2.0.0
* [GMP](https://gmplib.org/) >= 6.0.0 (or [MPIR](http://mpir.org/) >= 2.6.0 on Windows)

Building
--------

Here's how to build the JAR package and the dynamic library on Linux or OSX:

```bash
mvn compile
cd target
cmake ../
make
cd ..
mvn package
```

### Android

Here's how to build libsaltpack-jni and all its dependencies for Android using Docker:

```bash
docker run -v `pwd`:/opt/libsaltpack-jni -t ubuntu:16.04 /bin/bash /opt/libsaltpack-jni/android/compile.sh
```

This will produce the `libsaltpack-jni-libs.jar` file under the `android` directory; add that file together with the JAR created by the previous step to the Android Studio project.

Documentation
-------------

The Javadoc can be found here: [https://gherynos.github.io/libsaltpack-jni](https://gherynos.github.io/libsaltpack-jni).

Examples
--------

### Encrypt/decrypt message

```java
import net.nharyes.libsaltpack.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class Test {

    public static void main(String[] args) {

        try {

            // generate keypair
            byte[] publickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
            byte[] secretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
            Utils.generateKeypair(publickey, secretkey);

            // recipients
            byte[][] recipients = {publickey};

            // encrypt message
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputParameters op = new OutputParameters(out);
            op.setArmored(true);
            MessageWriter enc = new MessageWriter(op, secretkey, recipients);
            enc.addBlock(new byte[]{'T', 'h', 'e', ' ', 'm', 'e', 's', 's', 'a', 'g', 'e'});
            enc.addBlock(new byte[]{' ', ':', ')'});
            enc.finalise();

            out.flush();
            enc.destroy();

            // display encrypted message
            System.out.println(new String(out.toByteArray(), "UTF-8"));

            // decrypt message
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputParameters ip = new InputParameters(in);
            ip.setArmored(true);
            StringBuffer msg = new StringBuffer();
            MessageReader dec = new MessageReader(ip, secretkey);
            while (dec.hasMoreBlocks())
                msg.append(new String(dec.getBlock(), "UTF-8"));
            dec.destroy();

            // display decrypted message
            System.out.println(msg.toString());

        } catch (SaltpackException | IOException ex) {

            System.err.println(ex.getMessage());
        }
    }
}
```
### Sign/verify message

#### Attached signature

```java
import net.nharyes.libsaltpack.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class Test {

    public static void main(String[] args) {

        try {

            // generate keypair
            byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
            byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
            Utils.generateSignKeypair(publickey, secretkey);

            // sign message
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputParameters op = new OutputParameters(out);
            op.setArmored(true);
            MessageWriter enc = new MessageWriter(op, secretkey, false);
            enc.addBlock(new byte[]{'a', ' ', 's', 'i', 'g', 'n', 'e', 'd', ' ', 'm', 'e', 's', 's', 'a', 'g', 'e'});
            enc.finalise();

            out.flush();
            enc.destroy();

            // display signed message
            System.out.println(new String(out.toByteArray(), "UTF-8"));

            // verify message
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputParameters ip = new InputParameters(in);
            ip.setArmored(true);
            StringBuffer msg = new StringBuffer();
            MessageReader dec = new MessageReader(ip);
            while (dec.hasMoreBlocks())
                msg.append(new String(dec.getBlock(), "UTF-8"));
            dec.destroy();

            // display verified message
            System.out.println(msg.toString());

        } catch (SaltpackException | IOException ex) {

            System.err.println(ex.getMessage());
        }
    }
}
```

#### Detached signature

```java
import net.nharyes.libsaltpack.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class Test {

    public static void main(String[] args) {

        try {

            // generate keypair
            byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
            byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
            Utils.generateSignKeypair(publickey, secretkey);

            // sign message
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputParameters op = new OutputParameters(out);
            op.setArmored(true);
            MessageWriter enc = new MessageWriter(op, secretkey, true);
            enc.addBlock(new byte[]{'a', ' ', 's', 'i', 'g', 'n', 'e', 'd', ' ', 'm', 'e', 's', 's', 'a', 'g', 'e'});
            enc.finalise();

            out.flush();
            enc.destroy();

            // display signature
            System.out.println(new String(out.toByteArray(), "UTF-8"));

            // verify message
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputParameters ip = new InputParameters(in);
            ip.setArmored(true);
            ByteArrayInputStream msg = new ByteArrayInputStream("a signed message".getBytes());
            MessageReader dec = new MessageReader(ip, msg);
            dec.destroy();

        } catch (SaltpackException | IOException ex) {

            System.err.println(ex.getMessage());
        }
    }
}
```

Copyright and license
---------------------

Copyright 2016-2017 Luca Zanconato (<luca.zanconato@nharyes.net>)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.