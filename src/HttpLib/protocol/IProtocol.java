package HttpLib.protocol;

import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.Exceptions.InvalidResponseException;
import HttpLib.HttpRequest;
import HttpLib.IRequestCallback;

import java.io.IOException;

public interface IProtocol {

    public String send(HttpRequest httpRequest, int port) throws InvalidRequestException, IOException;

    public void listen(int port, IRequestCallback callback) throws IOException;

}
