//
// Created by Jungle on 2017/9/30.
//

#include "com_alpha_demo_jni_Hello.h"

const char *HOST_SERVER = "http://192.168.1.117:3000/";
const char *HOST_IMG = "http://192.168.1.117:3000/";

/*
 * Class:     com_alpha_jni_Hello
 * Method:    sayHello
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_cn_alpha_demo_jni_Hello_getServerUrl
        (JNIEnv *jniEnv, jclass jclazz) {
    return (*jniEnv)->NewStringUTF(jniEnv, HOST_SERVER);
}

/*
 * Class:     com_alpha_demo_jni_Hello
 * Method:    getIMGUrl
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_cn_alpha_demo_jni_Hello_getIMGUrl
        (JNIEnv *jniEnv, jclass jclazz) {
    return (*jniEnv)->NewStringUTF(jniEnv, HOST_IMG);
}