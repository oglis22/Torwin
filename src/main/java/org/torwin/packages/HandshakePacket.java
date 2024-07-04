package org.torwin.packages;

import org.torwin.utils.Leb128;

import java.io.ByteArrayInputStream;

public class HandshakePacket {

    private int protocolVersion;
    private String serverAdress;
    private int serverPort;

    public HandshakePacket(ByteArrayInputStream byteArrayInputStream) {
        try {
            this.protocolVersion = Leb128.readVarint(byteArrayInputStream);

            int adressLength = Leb128.readVarint(byteArrayInputStream);
            byte[] adressBytes = new byte[adressLength];
            byteArrayInputStream.read(adressBytes, 0, adressLength);
            this.serverAdress = new String(adressBytes);

            this.serverPort = byteArrayInputStream.read() << 8 | byteArrayInputStream.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getServerAdress() {
        return serverAdress;
    }

    public int getServerPort() {
        return serverPort;
    }


}
