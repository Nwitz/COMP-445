import HttpLib.Exceptions.HttpFormatException;
import HttpLib.*;
import argparser.StringHolder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

public class GETCommand extends RequestCommand {

    public GETCommand(String[] args) {
        super("httpc get [-v] [-h key:value] URL", args);
    }

    @Override
    protected void registerOptions() {
        inlineHeaders = new Vector<>(10);
    }

    @Override
    public void run() {
        super.run();

        // Get URL from last argument
        URL url = null;
        try {
            url = new URL(args[args.length - 1]);
        } catch (MalformedURLException e) {
            printHelpAndExit(e.getMessage());
        }

        // Extract all headers from command options
        HttpMessageHeader header = new HttpMessageHeader();
        try {
            Iterator value = inlineHeaders.iterator();
            while (value.hasNext()) {
                header.parseLine(((StringHolder) value.next()).value);
            }
        }catch (HttpFormatException e){
            printHelpAndExit();
        }


        HttpRequest request = new HttpRequest(url, HttpRequestMethod.GET, header);
        HttpResponse response = null;
        try {
            response = new HttpRequestHandler().send(request);
        } catch (Exception e) {
            printHelpAndExit(e.getMessage());
        }

        String responseString = "";
        if (verbose.value) {
            assert response != null;
            responseString = response.toString();
        } else {
            assert response != null;
            responseString = response.getBody();
        }

        // Printing
        System.out.println(responseString);

        if (inFilePath.value != null && !inFilePath.value.isEmpty()) {
            try {
                System.out.println(inFilePath.value);
                printToFile(inFilePath.value, responseString);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Invalid file path");
                printHelpAndExit();
            }
        }
    }
}
