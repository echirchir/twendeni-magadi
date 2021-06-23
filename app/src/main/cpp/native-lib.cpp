#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_org_etma_etma_EndTimeMessageMainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
