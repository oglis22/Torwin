package org.torwin.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Leb128 {

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;


    public static int readVarint(InputStream inputStream) throws IOException {
        int result = 0;
        int shift = 0;
        int b;
        do {
            b = inputStream.read();
            if (b == -1) {
                throw new IOException("Stream ended unexpectedly");
            }
            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return result;
    }

    public static String readString(ByteArrayInputStream in) throws IOException {
        int length = Leb128.readVarint(in);
        byte[] bytes = new byte[length];
        in.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static int sizeofVarint(int value) {
        int size = 0;
        do {
            size++;
            value >>>= 7;
        } while (value != 0);
        return size;
    }

    public static int writeVarint(int value, OutputStream out) throws IOException {

        int bytesWritten = 0;

        while (true) {
            byte byteValue = (byte) (value & 0xFF);
            value >>= 7;
            if (value != 0) {
                byteValue |= CONTINUE_BIT;
            }

            out.write(byteValue);
            bytesWritten++;

            if (value == 0) {
                return bytesWritten;
            }
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }



}
