package org.torwin.packages;

import org.torwin.utils.Leb128;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Packet {

    protected int packet_id;
    protected int packet_length;
    protected int data_length;
    protected byte[] data;
    protected OutputStream out;
    protected InputStream in;

    public int getPacket_id() {
        return packet_id;
    }

    public int getPacket_length() {
        return packet_length;
    }

    public int getData_length() {
        return data_length;
    }

    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) { this.data = data; }

    public OutputStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }

    public Packet() {

    }

    public Packet(InputStream in, OutputStream out) {

        try {

            this.packet_length = Leb128.readVarint(in);
            this.packet_id = Leb128.readVarint(in);
            this.data_length = packet_length - Leb128.sizeofVarint(packet_id);
            this.data = new byte[data_length];
            this.out = out;
            this.in = in;

            if (packet_length < 0) {
                throw new IOException("Invaild Packet Length: " + packet_length);
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public ByteArrayInputStream readData() {
        ByteArrayInputStream byteArrayInputStream;
        try {
            int bytesRead = 0;
            while (bytesRead < packet_length - Leb128.sizeofVarint(packet_id)) {
                bytesRead += in.read(data);
            }
        byteArrayInputStream = new ByteArrayInputStream(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return byteArrayInputStream;
    }

    public void write() {

        try {
            Leb128.writeVarint(packet_length, out);
            Leb128.writeVarint(packet_id, out);
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
