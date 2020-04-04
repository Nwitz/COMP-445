package HttpLib.protocol.UDP;

import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.Exceptions.InvalidResponseException;
import HttpLib.HttpRequest;
import HttpLib.IRequestCallback;
import HttpLib.protocol.IProtocol;

import java.io.IOException;

public class PseudoTCP implements IProtocol {

    @Override
    public String send(HttpRequest httpRequest, int port) throws InvalidRequestException, InvalidResponseException, IOException {
        return null;
    }

    @Override
    public void listen(int port, IRequestCallback callback) throws IOException {

    }
}
