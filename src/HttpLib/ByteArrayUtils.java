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

    public static String bytesToStringIP(byte[] from) {
        int[] ints = new int[4];
        for (int i = 0; i < 4; i++) {
            ints[i] = bytesToInt(padByteToInteger(from[i]));
        }

        return "" + ints[0] +
                "." + ints[1] +
                "." + ints[2] +
                "." + ints[3];
    }

    public static int bytesToFakeShort(byte[] from) {
        byte[] b = pad2ByteToInteger(from);
        return bytesToInt(b);
    }

    public static byte[] fakeShortToBytes(int from) {
        byte[] bytesFull = intToBytes(from);
        return new byte[]{bytesFull[2], bytesFull[3]};
    }

    private static byte[] padByteToInteger(byte b){
        byte[] bytes = new byte[4];
        bytes[3] = b;
        return bytes;
    }

    private static byte[] pad2ByteToInteger(byte[] from) {
        byte[] bytes = new byte[4];
        bytes[2] = from[0];
        bytes[3] = from[1];
        return bytes;
    }
}
