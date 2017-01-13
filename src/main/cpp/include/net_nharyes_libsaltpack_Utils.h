/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_nharyes_libsaltpack_Utils */

#ifndef _Included_net_nharyes_libsaltpack_Utils
#define _Included_net_nharyes_libsaltpack_Utils
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_nharyes_libsaltpack_Utils
 * Method:    generateKeypair
 * Signature: ([B[B)V
 */
JNIEXPORT void JNICALL Java_net_nharyes_libsaltpack_Utils_generateKeypair
  (JNIEnv *, jclass, jbyteArray, jbyteArray);

/*
 * Class:     net_nharyes_libsaltpack_Utils
 * Method:    generateSignKeypair
 * Signature: ([B[B)V
 */
JNIEXPORT void JNICALL Java_net_nharyes_libsaltpack_Utils_generateSignKeypair
  (JNIEnv *, jclass, jbyteArray, jbyteArray);

/*
 * Class:     net_nharyes_libsaltpack_Utils
 * Method:    derivePublickey
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_nharyes_libsaltpack_Utils_derivePublickey
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     net_nharyes_libsaltpack_Utils
 * Method:    baseXblockSize
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_net_nharyes_libsaltpack_Utils_baseXblockSize
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     net_nharyes_libsaltpack_Utils
 * Method:    baseXencode
 * Signature: ([BLjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_nharyes_libsaltpack_Utils_baseXencode___3BLjava_lang_String_2
  (JNIEnv *, jclass, jbyteArray, jstring);

/*
 * Class:     net_nharyes_libsaltpack_Utils
 * Method:    baseXencode
 * Signature: ([BILjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_nharyes_libsaltpack_Utils_baseXencode___3BILjava_lang_String_2
  (JNIEnv *, jclass, jbyteArray, jint, jstring);

/*
 * Class:     net_nharyes_libsaltpack_Utils
 * Method:    baseXdecode
 * Signature: (Ljava/lang/String;Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_nharyes_libsaltpack_Utils_baseXdecode
  (JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     net_nharyes_libsaltpack_Utils
 * Method:    hexToBin
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_nharyes_libsaltpack_Utils_hexToBin
  (JNIEnv *, jclass, jstring);

/*
 * Class:     net_nharyes_libsaltpack_Utils
 * Method:    binToHex
 * Signature: ([B)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_nharyes_libsaltpack_Utils_binToHex
  (JNIEnv *, jclass, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif