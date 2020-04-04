package HttpLib.Test;

import HttpLib.ByteArrayUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

public class ByteArrayUtilsTest {

    @Test
    public void test_StringIpToBytes() {
        // Arrange
        String s = "192.168.2.10";
        byte[] bytes;

        // Act
        bytes = ByteArrayUtils.stringIPToBytes(s);

        // Assert
        assert((bytes[0] & 0xff) == 192);
        assert((bytes[1] & 0xff) == 168);
        assert((bytes[2] & 0xff) == 2);
        assert((bytes[3] & 0xff) == 10);
    }

    @Test
    public void test_ShortToBytes() {
        // Arrange
        int x = 41830;
        byte[] bytes;

        // Act
        bytes = ByteArrayUtils.intToBytes(x);

        // Assert
        assert ((bytes[2] & 0xff) == 163);
        assert ((bytes[3] & 0xff) == 102);
    }

    @Test
    public void test_BytesToInt() {
        // Arrange
        int from = 1435;

        // Act
        byte[] bytes = ByteArrayUtils.intToBytes(from);
        int fromReceived = ByteArrayUtils.bytesToInt(bytes);

        // Assert
        assert(fromReceived == from);

    }
}
