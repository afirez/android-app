package com.afirez.wav.api.wav;

/**
 * Created by afirez on 2018/2/27.
 */

public class WavFileHeader {

    public static final int WAV_HEADER_SIZE = 44;
    public static final int WAV_CHUNK_SIZE_EXCLUDE_DATA = 36;

    public static final int WAV_CHUNK_SIZE_OFFSET = 4;
    public static final int WAV_SUB_CHUNK1_SIZE_OFFSET = 16;
    public static final int WAV_SUB_CHUNK2_SIZE_OFFSET = 40;

    public String chunkId = "RIFF";
    public int chunkSize = 0;
    public String format = "WAVE";

    public String subChunk1Id = "fmt";
    public int subChunk1Size = 16;
    public short audioFormat = 1;
    public short numChannel = 1;
    public int sampleRate = 8000;
    public int byteRate = 0;
    public short blockAlign = 0;
    public short bitsPerSample = 8;

    public String subChunk2Id = "data";
    public int subChunk2Size = 0;        //data size

    public WavFileHeader() {

    }

    public WavFileHeader(int sampleRateInHz, int bitsPerSample, int numChannel) {
        this.sampleRate = sampleRateInHz;
        this.bitsPerSample = (short) bitsPerSample;
        this.numChannel = (short) numChannel;
        this.byteRate = this.sampleRate * this.numChannel * this.bitsPerSample / 8;
        this.blockAlign = (short) (this.numChannel * this.bitsPerSample / 8);
    }
}
