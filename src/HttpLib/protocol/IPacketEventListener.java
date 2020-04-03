package HttpLib.protocol;

public interface IPacketEventListener {

    void onCanRequest();
    void onBaseSync(int newBase);

}
