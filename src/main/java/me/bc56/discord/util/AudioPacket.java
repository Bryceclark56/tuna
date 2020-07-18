package me.bc56.discord.util;

import org.bouncycastle.crypto.engines.XSalsa20Engine;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.eclipse.collections.api.list.primitive.ByteList;

public class AudioPacket {
    private static final byte VERSION = (byte) 0x80;
    private static final byte PAYLOAD_TYPE = (byte) 0x78;

    private byte[] header;
    private ByteList audio;
    private byte[] secretKey;

    public AudioPacket(short sequence, int timestamp, int ssrc, ByteList audio, byte[] secretKey) {
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

        XSalsa20Engine xsalsa20 = new XSalsa20Engine();
        xsalsa20.init(true, new ParametersWithIV(new KeyParameter(secretKey), nonce));

        byte[] subKey = new byte[secretKey.length];
        xsalsa20.processBytes(subKey, 0, secretKey.length, subKey, 0);

        Poly1305 poly1305 = new Poly1305();
        poly1305.init(new KeyParameter(subKey));

        byte[] encrypted = new byte[audio.size() + poly1305.getMacSize()];
        xsalsa20.processBytes(audio.toArray(), 0, audio.size(), encrypted, poly1305.getMacSize());
        poly1305.update(encrypted, poly1305.getMacSize(), audio.size());
        poly1305.doFinal(encrypted, 0);

        return encrypted;
    }
}
