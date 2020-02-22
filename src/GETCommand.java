import HttpLib.Exceptions.HttpFormatException;
import HttpLib.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Command(name = "get", description = "executes a HTTP GET request and prints the response.")
public class GETCommand implements Runnable {

    @Parameters(paramLabel = "URL", arity = "1")
    URL url;

    @CommandLine.Mixin
    private CommandMixins.Verbose verbose;

    @CommandLine.Mixin
    private CommandMixins.RequestHeader h;

    @Option(names={"-o", "--output"})
    File outputFile;

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
            response = new HttpRequestHandler().send(request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            CommandLine commandLine = new CommandLine(new GETCommand());
            commandLine.usage(System.out);
            System.exit(0);
        }

        String responseString = "";
        assert response != null;
        if (verbose.active) {
            responseString = response.toString();
        } else {
            responseString = response.getBody();
        }

        // Printing
        System.out.println(responseString);

        // Saving to file
        if(outputFile != null){
            try {
                Httpc.printToFile(outputFile.getPath(), responseString);
                System.out.println();
                System.out.println("Content printed to output file: " + outputFile.getPath());
            }catch (IOException e){
                System.out.println(e.getMessage());
                CommandLine commandLine = new CommandLine(new GETCommand());
                commandLine.usage(System.out);
                System.exit(0);
            }
        }
    }
}
