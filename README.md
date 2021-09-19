# libsaltpack-jni

[![Build Status](https://github.com/gherynos/libsaltpack-jni/workflows/build/badge.svg)](https://github.com/gherynos/libsaltpack-jni/actions/workflows/build.yaml)
[![Coverage Status](https://coveralls.io/repos/github/gherynos/libsaltpack-jni/badge.svg?branch=main)](https://coveralls.io/github/gherynos/libsaltpack-jni?branch=main)
[![pre-commit](https://img.shields.io/badge/pre--commit-enabled-brightgreen?logo=pre-commit&logoColor=white)](https://github.com/pre-commit/pre-commit)

A Java Native Interface wrapper for [libsaltpack](https://github.com/Gherynos/libsaltpack).

## Dependencies

* [libsaltpack](https://github.com/Gherynos/libsaltpack)
* [libsodium](https://download.libsodium.org/doc/) >= 1.0.9
* [msgpack](https://github.com/msgpack/msgpack-c) >= 2.0.0

`libsodium` should be compiled with PIC enabled (```./configure --with-pic```).

## Building

Here's how to build the JAR package and the dynamic library on Linux or OSX:

```bash
mvn compile
cmake .
make
mvn exec:java -Dexec.mainClass="net.nharyes.libsaltpack.Loader"
mvn package
```

### Android

Here's how to build libsaltpack-jni and all its dependencies for Android using Docker:

```bash
docker run --rm -v `pwd`:/opt/libsaltpack-jni -t ubuntu:xenial /bin/bash /opt/libsaltpack-jni/android/compile.sh
```

This will produce the `libsaltpack-jni-<version>.jar` file under the `target` directory, containing the Java classes and the binary libraries.

## Documentation

The Javadoc can be found here: [https://gherynos.github.io/libsaltpack-jni](https://gherynos.github.io/libsaltpack-jni).

## Examples

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
            enc.addBlock(new byte[]{'T', 'h', 'e', ' ', 'm', 'e', 's', 's', 'a', 'g', 'e'}, false);
            enc.addBlock(new byte[]{' ', ':', ')'}, true);

            out.flush();
            enc.destroy();

            // display encrypted message
            System.out.println(new String(out.toByteArray(), "UTF-8"));

            // decrypt message
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputParameters ip = new InputParameters(in);
            ip.setArmored(true);
            StringBuilder msg = new StringBuilder();
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
            enc.addBlock(new byte[]{'a', ' ', 's', 'i', 'g', 'n', 'e', 'd', ' ', 'm', 's', 'g'}, true);

            out.flush();
            enc.destroy();

            // display signed message
            System.out.println(new String(out.toByteArray(), "UTF-8"));

            // verify message
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputParameters ip = new InputParameters(in);
            ip.setArmored(true);
            StringBuilder msg = new StringBuilder();
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
            enc.addBlock(new byte[]{'a', ' ', 's', 'i', 'g', 'n', 'e', 'd', ' ', 'm', 's', 'g'}, true);

            out.flush();
            enc.destroy();

            // display signature
            System.out.println(new String(out.toByteArray(), "UTF-8"));

            // verify message
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputParameters ip = new InputParameters(in);
            ip.setArmored(true);
            ByteArrayInputStream msg = new ByteArrayInputStream("a signed msg".getBytes());
            MessageReader dec = new MessageReader(ip, msg);
            dec.destroy();

        } catch (SaltpackException | IOException ex) {

            System.err.println(ex.getMessage());
        }
    }
}
```

### Signcrypt message

#### Curve25519 key

```java
import net.nharyes.libsaltpack.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class Test {

    public static void main(String[] args) {

        try {

            // generate signer keypair
            byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
            byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
            Utils.generateSignKeypair(publickey, secretkey);

            // generate recipient keypair
            byte[] recipientPublickey = new byte[Constants.CRYPTO_BOX_PUBLICKEYBYTES];
            byte[] recipientSecretkey = new byte[Constants.CRYPTO_BOX_SECRETKEYBYTES];
            Utils.generateKeypair(recipientPublickey, recipientSecretkey);

            // asymmetric keys
            byte[][] recipients = {recipientPublickey};

            // symmetric keys (empty)
            byte[][][] symmetricKeys = {};

            // signcrypt message
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputParameters op = new OutputParameters(out);
            op.setArmored(true);
            MessageWriter enc = new MessageWriter(op, secretkey, recipients, symmetricKeys);
            enc.addBlock(new byte[]{'T', 'h', 'e', ' ', 'm', 'e', 's', 's', 'a', 'g', 'e'}, false);
            enc.addBlock(new byte[]{' ', ':', ')'}, true);

            out.flush();
            enc.destroy();

            // display signcrypted message
            System.out.println(new String(out.toByteArray(), "UTF-8"));

            // verify message
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputParameters ip = new InputParameters(in);
            ip.setArmored(true);
            StringBuilder msg = new StringBuilder();
            MessageReader dec = new MessageReader(ip, recipientSecretkey, new byte[][]{});
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

#### Symmetric key

```java
import net.nharyes.libsaltpack.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class Test {

    public static void main(String[] args) {

        try {

            // generate signer keypair
            byte[] secretkey = new byte[Constants.CRYPTO_SIGN_SECRETKEYBYTES];
            byte[] publickey = new byte[Constants.CRYPTO_SIGN_PUBLICKEYBYTES];
            Utils.generateSignKeypair(publickey, secretkey);

            // asymmetric keys (empty)
            byte[][] recipients = {};

            // symmetric keys
            byte[][] key = {
                    Utils.generateRandomBytes(32),
                    Utils.generateRandomBytes(Constants.CRYPTO_SECRETBOX_KEYBYTES)};
            byte[][][] symmetricKeys = {key};

            // signcrypt message
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputParameters op = new OutputParameters(out);
            op.setArmored(true);
            MessageWriter enc = new MessageWriter(op, secretkey, recipients, symmetricKeys);
            enc.addBlock(new byte[]{'T', 'h', 'e', ' ', 'm', 'e', 's', 's', 'a', 'g', 'e'}, false);
            enc.addBlock(new byte[]{' ', ':', 'D'}, true);

            out.flush();
            enc.destroy();

            // display signcrypted message
            System.out.println(new String(out.toByteArray(), "UTF-8"));

            // verify message
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputParameters ip = new InputParameters(in);
            ip.setArmored(true);
            StringBuilder msg = new StringBuilder();
            MessageReader dec = new MessageReader(ip, new byte[]{}, key);
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

## Author

> GitHub [@gherynos](https://github.com/gherynos)

## License

libsaltpack-jni is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
