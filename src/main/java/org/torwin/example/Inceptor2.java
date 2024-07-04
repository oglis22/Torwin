package org.torwin.example;

import org.torwin.interfaces.TrafficInceptor;
import org.torwin.packages.EncryptionRequestPacket;
import org.torwin.packages.EncryptionResponsePacket;
import org.torwin.packages.Packet;
import org.torwin.utils.ByteUtils;
import org.torwin.utils.Leb128;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Inceptor2 implements TrafficInceptor {
    private SecretKeySpec sharedSecret;
    private Cipher aesCipher;
    private PublicKey serverPublicKey;
    private PrivateKey clientPrivateKey;

    public Inceptor2() {
        try {
            // Generate RSA key pair for client
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            clientPrivateKey = keyPair.getPrivate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClientToServer(Packet packet) {
        System.out.println("[ C -> S ] >> PACKETID: " + Integer.toHexString(packet.getPacket_id()));
        if (packet.getPacket_id() == 0x00) {
            packet.readData();
            packet.write();
            return;
        }
        if (packet.getPacket_id() == 0x01) {
            EncryptionResponsePacket encryptionResponsePacket = new EncryptionResponsePacket(packet.readData());
            System.out.println("SHARED KEY: " + Leb128.bytesToHex(encryptionResponsePacket.getSharedSecert()));

            // Initialize AES with the shared secret
            sharedSecret = new SecretKeySpec(encryptionResponsePacket.getSharedSecert(), "AES");
            try {
                aesCipher = Cipher.getInstance("AES");
                aesCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);
            } catch (Exception e) {
                e.printStackTrace();
            }

            packet.write();
            return;
        }

        packet.readData();
        if (sharedSecret != null) {
            // Decrypt the data
            byte[] decryptedData = decrypt(packet.getData());
            packet.setData(decryptedData);
        }

        packet.readData();
        packet.write();
    }

    @Override
    public void onServerToClient(Packet packet) {
        System.out.println("[ S -> C ] >> PACKETID: " + Integer.toHexString(packet.getPacket_id()));

        if (packet.getPacket_id() == 0x00) {
            packet.readData();
            packet.write();
            return;
        }

        if (packet.getPacket_id() == 0x01) {
            EncryptionRequestPacket encryptionRequestPacket = new EncryptionRequestPacket(packet.readData());
            System.out.println("PUBLIC KEY: " + Leb128.bytesToHex(encryptionRequestPacket.getPublicKey()));

            // Derive the server's public key from the packet
            try {
                byte[] publicKeyBytes = encryptionRequestPacket.getPublicKey();
                X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                serverPublicKey = keyFactory.generatePublic(spec);

                // Generate AES key
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128);
                SecretKey secretKey = keyGen.generateKey();
                byte[] aesKey = secretKey.getEncoded();

                // Encrypt the AES key with the server's public key
                Cipher rsaCipher = Cipher.getInstance("RSA");
                rsaCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
                byte[] encryptedAesKey = rsaCipher.doFinal(aesKey);

                // Create and send the EncryptionResponsePacket with encrypted AES key
                EncryptionResponsePacket responsePacket = new EncryptionResponsePacket();
                packet.setData(responsePacket.toBytes());

                // Initialize AES cipher with the shared secret
                sharedSecret = new SecretKeySpec(aesKey, "AES");
                aesCipher = Cipher.getInstance("AES");
                aesCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);

            } catch (Exception e) {
                e.printStackTrace();
            }

            packet.write();
            return;
        }

        if (sharedSecret != null) {
            // Decrypt the data
            byte[] decryptedData = decrypt(packet.readData());
            packet.setData(decryptedData);
        }

        packet.readData();
        packet.write();
    }

    private byte[] decrypt(byte[] data) {
        try {
            aesCipher.init(Cipher.DECRYPT_MODE, sharedSecret);
            return aesCipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] encrypt(byte[] data) {
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);
            return aesCipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
