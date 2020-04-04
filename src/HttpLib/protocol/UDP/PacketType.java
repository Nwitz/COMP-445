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

    public static PacketType fromChar(final char c) {
        for (PacketType type : PacketType.values()){
            if (type.asChar == c) {
                return type;
            }
        }
        return null;
    }
}


