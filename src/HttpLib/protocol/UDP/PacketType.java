package HttpLib.protocol.UDP;

public enum PacketType {
    DATA('d'),
    SYN('s'),
    SYNACK('z'),
    ACK('a'),
    FIN('f'),       // End of message data signal
    TER('t');       // Connection closure signal




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


