//
// Created by afirez on 19/5/2018.
//
#include "com_afirez_lame_mp3_Mp3Encoder.h"
#include "mp3_encoder.h"
#include "utils.h"

#define LOG_TAG "Mp3Encoder"

Mp3Encoder *encoder = NULL;

JNIEXPORT jint JNICALL
Java_com_afirez_mp3_lame_Mp3Encoder_init(
        JNIEnv *env,
        jobject instance,
        jstring pcmPath_,
        jint audioChannels,
        jint bitRate, jint sampleRate,
        jstring mp3Path_) {
    jint ret;
    const char *pcmPath = env->GetStringUTFChars(pcmPath_, 0);
    const char *mp3Path = env->GetStringUTFChars(mp3Path_, 0);
    LOGI("mp3 path is %s ...", mp3Path);
    encoder = new Mp3Encoder();
    ret = encoder->Init(pcmPath, mp3Path, sampleRate, audioChannels, bitRate);
    env->ReleaseStringUTFChars(pcmPath_, pcmPath);
    env->ReleaseStringUTFChars(mp3Path_, mp3Path);
    return ret;
}

JNIEXPORT void JNICALL
Java_com_afirez_mp3_lame_Mp3Encoder_encode(JNIEnv *env, jobject instance) {
    if(encoder != NULL) {
        encoder->Encode();
    }
}


JNIEXPORT void JNICALL
Java_com_afirez_mp3_lame_Mp3Encoder_destroy(JNIEnv *env, jobject instance) {
    if(encoder != NULL) {
        encoder->Destroy();
        delete encoder;
        encoder = NULL;
    }
}