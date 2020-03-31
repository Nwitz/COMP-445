package Httpc;

import HttpLib.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;


@Command(name = "post", description = "executes a HTTP POST request for a given URL with inline data or from file.")
public class POSTCommand implements Runnable {

    // Create a required option group, with mutual exclusivity
    @CommandLine.ArgGroup(multiplicity = "1")
    ExclusiveParams dataSource;

    static class ExclusiveParams {
        @Option(names = {"-d", "--inline-data"}, required = true, description = "Associates an inline data to the body HTTP POST request.")
        String inlineData;
        @Option(names = {"-f", "--input-file"}, required = true, description = "Associates the content of a file to the body HTTP POST request.")
        File inputFile;
    }

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

        HttpRequestBody body = new HttpRequestBody();
        if (dataSource.inlineData != null && !dataSource.inlineData.isEmpty()) {
            body.setBody(dataSource.inlineData);
        }
        if (dataSource.inputFile != null) {
            if(!dataSource.inputFile.exists()){
                Httpc.printHelpAndExit(this, "The given input file does not exist.");
            }
            try {
                BufferedReader br = new BufferedReader(new FileReader(dataSource.inputFile));
                StringBuilder fileStringBuilder = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    fileStringBuilder.append(line).append("\n");
                }
                body.setBody(fileStringBuilder.toString());
            } catch (Exception e) {
                Httpc.printHelpAndExit(this, e.getMessage());
            }
        }


        // Processing the request
        HttpRequest request = new HttpRequest(url, HttpRequestMethod.POST, header, body);
        HttpResponse response = null;
        try {
            response = new HttpRequestHandler(defaultOptions.getProtocol()).send(request);
        } catch (Exception e) {
            Httpc.printHelpAndExit(this, e.getMessage());
        }

        String responseString = "";
        if (defaultOptions.verbose) {
            responseString = response.toString();
        } else {
            responseString = response.getBody();
        }

        System.out.println(responseString);

        // Saving to file
        try {
            defaultOptions.SaveToFile(responseString);
        } catch (IOException e) {
            Httpc.printHelpAndExit(this, e.getMessage());
        }
    }
}
