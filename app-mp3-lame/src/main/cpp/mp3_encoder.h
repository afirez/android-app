//
// Created by afirez on 19/5/2018.
//

#ifndef ANDROID_APP_MP3_ENCODER_H
#define ANDROID_APP_MP3_ENCODER_H

#include "lame.h"

class Mp3Encoder {
    private:
        FILE *pcmFile;
        FILE *mp3File;
        lame_t lameClient;

    public:
        Mp3Encoder();
        ~Mp3Encoder();
        int Init(const char *pcmFilePath, const char *mp3FilePath, int sampleRate, int channels, int bitRate);
        void Encode();
        void Destroy();
};

#endif //ANDROID_APP_MP3_ENCODER_H
