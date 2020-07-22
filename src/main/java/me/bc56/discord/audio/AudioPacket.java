package me.bc56.discord.audio;

import org.abstractj.kalium.crypto.SecretBox;

public class AudioPacket {
    private static final byte VERSION = (byte) 0x80;
    private static final byte PAYLOAD_TYPE = (byte) 0x78;

    private byte[] header;
    private byte[] audio;
    private byte[] secretKey;

    public AudioPacket(short sequence, int timestamp, int ssrc, byte[] audio, byte[] secretKey) {
        this.header = new byte[12];

        header[0] = VERSION;
        header[1] = PAYLOAD_TYPE;

        header[2] = (byte) ((sequence >> 8) & 0xFF);
        header[3] = (byte) ((sequence) & 0xFF);

        header[4] = (byte) ((timestamp >> 24) & 0xFF);
        header[5] = (byte) ((timestamp >> 16) & 0xFF);
        header[6] = (byte) ((timestamp >> 8) & 0xFF);
        header[7] = (byte) (timestamp & 0xFF);

        header[8] = (byte) ((ssrc >> 24) & 0xFF);
        header[9] = (byte) ((ssrc >> 16) & 0xFF);
        header[10] = (byte) ((ssrc >> 8) & 0xFF);
        header[11] = (byte) (ssrc & 0xFF);

        this.audio = audio;
        this.secretKey = secretKey;
    }

    public byte[] getEncryptedPacket() {
        byte[] nonce = new byte[24];
        for (int i = 0; i < 12; i++) {
            nonce[i] = header[i];
        }

        SecretBox box = new SecretBox(secretKey);
        byte[] encrypted = box.encrypt(nonce, audio);

        byte[] packet = new byte[header.length + encrypted.length];
        for (int i = 0; i < 12; i++) {
            packet[i] = header[i];
        }
        for (int i = 0; i < encrypted.length; i++) {
            packet[i + header.length] = encrypted[i];
        }

        return packet;
    }
}
