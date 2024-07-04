package org.torwin.utils;

import java.io.ByteArrayInputStream;

public class ByteUtils {
    public static byte[] convertByteArrayInputStreamToByteArray(ByteArrayInputStream inputStream) {
        // Byte-Array erstellen, um die Daten zu speichern
        byte[] buffer = new byte[inputStream.available()]; // Initialgröße auf verfügbare Bytes setzen

        // Daten aus dem ByteArrayInputStream in das Byte-Array lesen
        int bytesRead = 0;
        while (bytesRead < buffer.length) {
            int read = inputStream.read(buffer, bytesRead, buffer.length - bytesRead);
            if (read == -1) {
                break; // End-of-stream erreicht
            }
            bytesRead += read;
        }

        return buffer;
    }
}


