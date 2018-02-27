package com.afirez.wav.api.wav;

import android.util.Log;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by lenovo on 2018/2/27.
 */

public class WavFileReader {
    private static final String TAG = "WavFileReader";

    private volatile DataInputStream mDataInputStream;

    public boolean openFile(String path) throws IOException {
        closeFile();
        mDataInputStream = new DataInputStream(new FileInputStream(path));
        return readHeader();
    }

    public void closeFile() throws IOException {
        if (mDataInputStream != null) {
            mDataInputStream.close();
            mDataInputStream = null;
        }
    }

    private WavFileHeader mWavFileHeader;

    private boolean readHeader() {
        if (mDataInputStream == null) {
            return false;
        }

        WavFileHeader header = new WavFileHeader();

        byte[] intBuffer = new byte[4];
        byte[] shortBuffer = new byte[2];

        try {
            header.chunkId = "" + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte();
            Log.d(TAG, "readHeader: chunkId: " + header.chunkId);

            mDataInputStream.read(intBuffer);
            header.chunkSize = ByteUtils.byteArrayToInt(intBuffer);
            Log.d(TAG, "readHeader: chunkSize: " + header.chunkSize);

            header.format = "" + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte();
            Log.d(TAG, "readHeader: format: " + header.format);

            header.subChunk1Id = "" + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte();
            Log.d(TAG, "readHeader: subChunk1Id: " + header.subChunk1Id);

            mDataInputStream.read(intBuffer);
            header.subChunk1Size = ByteUtils.byteArrayToInt(intBuffer);
            Log.d(TAG, "readHeader: subChunk1Size: " + header.subChunk1Size);

            mDataInputStream.read(shortBuffer);
            header.audioFormat = ByteUtils.byteArrayToShort(shortBuffer);
            Log.d(TAG, "readHeader: audioFormat: " + header.audioFormat);

            mDataInputStream.read(shortBuffer);
            header.numChannel = ByteUtils.byteArrayToShort(shortBuffer);
            Log.d(TAG, "readHeader: numChannel: " + header.numChannel);

            mDataInputStream.read(intBuffer);
            header.sampleRate = ByteUtils.byteArrayToInt(intBuffer);
            Log.d(TAG, "readHeader: sampleRate: " + header.sampleRate);

            mDataInputStream.read(intBuffer);
            header.byteRate = ByteUtils.byteArrayToInt(intBuffer);
            Log.d(TAG, "readHeader: byteRate: " + header.byteRate);

            mDataInputStream.read(shortBuffer);
            header.blockAlign = ByteUtils.byteArrayToShort(shortBuffer);
            Log.d(TAG, "readHeader: blockAlign: " + header.blockAlign);

            mDataInputStream.read(shortBuffer);
            header.bitsPerSample = ByteUtils.byteArrayToShort(shortBuffer);
            Log.d(TAG, "readHeader: bitsPerSample: " + header.bitsPerSample);

            header.subChunk2Id = "" + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte()
                    + (char) mDataInputStream.readByte();
            Log.d(TAG, "readHeader: subChunk2Id: " + header.subChunk2Id);

            mDataInputStream.read(intBuffer);
            header.subChunk2Size = ByteUtils.byteArrayToInt(intBuffer);
            Log.d(TAG, "readHeader: subChunk2Size: " + header.subChunk2Size);

            Log.d(TAG, "readHeader: success!");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        mWavFileHeader = header;
        return true;
    }

    public int readData(byte[] buffer, int offset, int count) {
        if (mDataInputStream == null || mWavFileHeader == null) {
            return -1;
        }

        try {
            int read = mDataInputStream.read(buffer, offset, count);
            if (read == -1) {
                return 0;
            }
            return read;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
