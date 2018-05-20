//
// Created by afirez on 19/5/2018.
//

#ifndef ANDROID_APP_COM_AFIREZ_LAME_MP3_MP3ENCODER_H
#define ANDROID_APP_COM_AFIREZ_LAME_MP3_MP3ENCODER_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

JNIEXPORT jint JNICALL
Java_com_afirez_mp3_lame_Mp3Encoder_init(
        JNIEnv *env,
        jobject instance,
        jstring pcmPath_,
        jint audioChannels,
        jint bitRate, jint sampleRate,
        jstring mp3Path_
);

JNIEXPORT void JNICALL
Java_com_afirez_mp3_lame_Mp3Encoder_encode(
        JNIEnv *env,
        jobject instance
);

JNIEXPORT void JNICALL
Java_com_afirez_mp3_lame_Mp3Encoder_destroy(
        JNIEnv *env,
        jobject instance
);

#ifdef __cplusplus
}
#endif // __cplusplus

#endif //ANDROID_APP_COM_AFIREZ_LAME_MP3_MP3ENCODER_H
