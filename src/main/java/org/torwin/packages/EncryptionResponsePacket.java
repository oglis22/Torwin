package org.torwin.packages;

import org.torwin.utils.Leb128;

import java.io.ByteArrayInputStream;

public class EncryptionResponsePacket {

    private int sharedSecretLength;
    private byte[] sharedSecert;
    private int verifyTokenLength;
    private byte[] verifyToken;

    public EncryptionResponsePacket(ByteArrayInputStream byteArrayInputStream) {

        try {

            this.sharedSecretLength = Leb128.readVarint(byteArrayInputStream);

            byte[] sharedSecert = new byte[sharedSecretLength];
            byteArrayInputStream.read(sharedSecert);
            this.sharedSecert = sharedSecert;

            this.verifyTokenLength = Leb128.readVarint(byteArrayInputStream);

            byte[] verifyToken = new byte[verifyTokenLength];
            byteArrayInputStream.read(verifyToken);
            this.verifyToken = verifyToken;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getSharedSecretLength() {
        return sharedSecretLength;
    }

    public byte[] getSharedSecert() {
        return sharedSecert;
    }

    public int getVerifyTokenLength() {
        return verifyTokenLength;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }
}
