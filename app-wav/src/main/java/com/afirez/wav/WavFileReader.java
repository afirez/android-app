package com.afirez.wav;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
