package Httpc;

import HttpLib.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Command(name = "get", description = "executes a HTTP GET request and prints the response.")
public class GETCommand implements Runnable {

    @Parameters(paramLabel = "URL", arity = "1")
    URL url;

    @CommandLine.Mixin
    private CommandMixins.DefaultOptions defaultOptions;

    @CommandLine.Mixin
    private CommandMixins.RequestHeader h;

    @Override
    public void run() {
        // Extract all headers from command options
        HttpMessageHeader header = new HttpMessageHeader();
        if (h.headersMap != null) {
            for (Map.Entry<String, String> entry : h.headersMap.entrySet()) {
                header.addEntry(entry.getKey(), entry.getValue());
            }
        }

        HttpRequest request = new HttpRequest(url, HttpRequestMethod.GET, header);
        HttpResponse response = null;
        try {
            response = new HttpRequestHandler(defaultOptions.getProtocol()).send(request);
        } catch (Exception e) {
            Httpc.printHelpAndExit(this, e.getMessage());
        }

        String responseString = "";
        assert response != null;
        if (defaultOptions.verbose) {
            responseString = response.toString();
        } else {
            responseString = response.getBody();
        }

        // Printing
        System.out.println(responseString);

        // Saving to file
        try{
            defaultOptions.SaveToFile(responseString);
        }catch (IOException e){
            Httpc.printHelpAndExit(this, e.getMessage());
        }
    }
}
