package com.afirez.wav.api.wav;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by afirez on 18-2-27.
 */

public class WavFileWriter {

    private DataOutputStream dataOutputStream;
    private String path;
    private int dataSize;

    public boolean openFile(
            String path,
            int sampleRateInHz,
            int channels,
            int bitsPerSample) throws IOException {
        closeFile();
        this.path = path;
        dataSize = 0;
        dataOutputStream = new DataOutputStream(new FileOutputStream(path));
        return writeHeader(sampleRateInHz, bitsPerSample, channels);
    }


    public boolean closeFile() throws IOException {
        boolean ret = true;
        if (dataOutputStream != null) {
            ret = writeDataSize();
            dataOutputStream.close();
            dataOutputStream = null;
        }
        return ret;
    }

    private boolean writeHeader(int sampleRateInHz, int bitsPerSample, int channels) {
        if (dataOutputStream == null) {
            return false;
        }

        WavFileHeader header = new WavFileHeader(sampleRateInHz, bitsPerSample, channels);

        try {
            dataOutputStream.writeBytes(header.chunkId);
            dataOutputStream.write(ByteUtils.intToByteArray(header.chunkSize), 0, 4);
            dataOutputStream.writeBytes(header.format);
            dataOutputStream.writeBytes(header.subChunk1Id);
            dataOutputStream.write(ByteUtils.intToByteArray(header.subChunk1Size), 0, 4);
            dataOutputStream.write(ByteUtils.shortToByteArray(header.audioFormat), 0, 2);
            dataOutputStream.write(ByteUtils.shortToByteArray(header.numChannel), 0, 2);
            dataOutputStream.write(ByteUtils.intToByteArray(header.sampleRate), 0, 4);
            dataOutputStream.write(ByteUtils.intToByteArray(header.byteRate), 0, 4);
            dataOutputStream.write(ByteUtils.shortToByteArray(header.blockAlign), 0, 2);
            dataOutputStream.write(ByteUtils.shortToByteArray(header.bitsPerSample), 0, 2);
            dataOutputStream.writeBytes(header.subChunk2Id);
            dataOutputStream.write(ByteUtils.intToByteArray(header.subChunk2Size), 0, 4);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean writeDataSize() {
        if (dataOutputStream == null) {
            return false;
        }

        try {
            RandomAccessFile raf = new RandomAccessFile(this.path, "rw");
            raf.seek(WavFileHeader.WAV_CHUNK_SIZE_OFFSET);
            raf.write(ByteUtils.intToByteArray(dataSize + WavFileHeader.WAV_CHUNK_SIZE_EXCLUDE_DATA), 0, 4);
            raf.seek(WavFileHeader.WAV_SUB_CHUNK2_SIZE_OFFSET);
            raf.write(ByteUtils.intToByteArray(dataSize), 0, 4);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean writeData(byte[] buffer, int offset, int count) {
        if (dataOutputStream == null) {
            return false;
        }

        try {
            dataOutputStream.write(buffer, offset, count);
            dataSize += count;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
