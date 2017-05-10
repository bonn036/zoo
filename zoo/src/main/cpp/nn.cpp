#include <jni.h>

extern "C" {
jint
Java_com_mmnn_zoo_NativeActivity_intFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    return 100;
}

}