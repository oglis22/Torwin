package org.torwin.packages;

import org.torwin.utils.Leb128;

import java.io.ByteArrayInputStream;

public class EncryptionRequestPacket {

    private String serverId;
    private int publicKeyLength;
    private byte[] publicKey;
    private int verifyTokenLength;
    private byte[] verifyToken;
    private boolean shoudAuthenticate;

    public EncryptionRequestPacket(ByteArrayInputStream byteArrayInputStream) {

        try {

            int serverIdLength = Leb128.readVarint(byteArrayInputStream);
            byte[] serverIdBytes = new byte[serverIdLength];
            byteArrayInputStream.read(serverIdBytes, 0, serverIdLength);
            this.serverId = new String(serverIdBytes);

            this.publicKeyLength = Leb128.readVarint(byteArrayInputStream);
            byte[] publicKey = new byte[publicKeyLength];
            byteArrayInputStream.read(publicKey);
            this.publicKey = publicKey;

            this.verifyTokenLength = Leb128.readVarint(byteArrayInputStream);
            byte[] verifyToken = new byte[verifyTokenLength];
            byteArrayInputStream.read(verifyToken);
            this.verifyToken = verifyToken;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getServerId() {
        return serverId;
    }

    public int getPublicKeyLength() {
        return publicKeyLength;
    }

    public byte[] getPublicKey() {
        // use Leb128 bytes to hex
        return publicKey;
    }

    public int getVerifyTokenLength() {
        return verifyTokenLength;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }
}
