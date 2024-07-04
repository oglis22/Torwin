package org.torwin.example;

import org.torwin.interfaces.TrafficInceptor;
import org.torwin.packages.EncryptionRequestPacket;
import org.torwin.packages.EncryptionResponsePacket;
import org.torwin.packages.Packet;
import org.torwin.utils.Leb128;

import java.util.Arrays;

public class Inceptor implements TrafficInceptor {
    @Override
    public void onClientToServer(Packet packet) {
        System.out.println("[ C -> S ] >> PACKETID: " + Integer.toHexString(packet.getPacket_id()));

        if (packet.getPacket_id() == 0x01) {
            EncryptionResponsePacket encryptionResponsePacket = new EncryptionResponsePacket(packet.readData());
            System.out.println("SHARED KEY: " + Leb128.bytesToHex(encryptionResponsePacket.getSharedSecert()));
            packet.write();
            return;
        }
        System.out.println(Arrays.toString(packet.getData()));
        packet.readData();
        System.out.println(Arrays.toString(packet.getData()));
        packet.write();
    }

    @Override
    public void onServerToClient(Packet packet) {

        System.out.println("[ S -> C ] >> PACKETID: " + Integer.toHexString(packet.getPacket_id()));
        if (packet.getPacket_id() == 0x01) {
            EncryptionRequestPacket encryptionRequestPacket = new EncryptionRequestPacket(packet.readData());
            System.out.println("PUBLIC KEY: " + Leb128.bytesToHex(encryptionRequestPacket.getPublicKey()));
            packet.write();
        } else {
            packet.readData();
            packet.write();
        }

    }
}
