package HttpLib;

import java.nio.ByteBuffer;

public class ByteArrayUtils {

    public static byte[] shortToBytes(short from){
        return ByteBuffer.allocate(2).putShort(from).array();
    }

    public static byte[] intToBytes(int from) {
        return ByteBuffer.allocate(4).putInt(from).array();
    }

    public static int bytesToInt(byte[] from) {
        ByteBuffer buffer = ByteBuffer.wrap(from);
        return buffer.getInt();
    }

    public static byte[] stringIPToBytes(String ip) {
        String[] parts = ip.split("\\.");
        byte[] bytes = new byte[4];
        for (int i = 0; i<4; i++) {
            bytes[i] = (byte)Integer.parseInt(parts[i]);
        }
        return bytes;
    }


}
