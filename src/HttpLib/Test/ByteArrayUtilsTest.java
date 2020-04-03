package HttpLib.Test;

import HttpLib.ByteArrayUtils;
import org.junit.jupiter.api.Test;

public class ByteArrayUtilsTest {

    @Test
    public void test_StringIpToBytes() {
        // Arrange
        String s = "192.168.2.10";
        byte[] bytes;

        // Act
        bytes = ByteArrayUtils.stringIPToBytes(s);

        // Assert
        for (byte b: bytes) {
            System.out.println(b & 0xff);
        }
    }

    @Test
    public void testShortToBytes(){
        // Arrange
        short s = 3000;
        short s2 = (short) (s - Short.MAX_VALUE);
        byte[] bytes;
        // Act
        bytes = ByteArrayUtils.shortToBytes(s2);

        // Assert
        System.out.println(bytes[0] & 0xff);
        System.out.println(bytes[1] & 0xff);
    }
}
