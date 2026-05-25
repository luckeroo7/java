#include <jni.h>
#include "Calculator.h"
#include <stdio.h>

extern "C" JNIEXPORT jint JNICALL Java_Calculator_add
(JNIEnv* env, jobject obj, jint a, jint b) {

    printf("C++ function called with %d and %d!\n", a, b);
    return a+b;
}