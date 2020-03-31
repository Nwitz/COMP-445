package HttpLib.protocol;

import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.Exceptions.InvalidResponseException;
import HttpLib.HttpRequest;
import HttpLib.IRequestCallback;

import java.io.IOException;

public class UDP implements Protocol {

    @Override
    public String send(HttpRequest httpRequest, int port) throws InvalidRequestException, InvalidResponseException, IOException {
        return null;
    }

    @Override
    public void listen(int port, IRequestCallback callback) throws IOException {

    }
}
