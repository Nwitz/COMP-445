package HttpLib.protocol.UDP;

public enum PacketType {
    DATA('d'),
    ACK('a'),
    NACK('n');




    public char asChar() {
        return asChar;
    }

    private final char asChar;

    PacketType(char d) {
        this.asChar = d;
    }
}


