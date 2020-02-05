import HttpLib.Exceptions.HttpFormatException;
import HttpLib.*;
import argparser.StringHolder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

public class GETCommand extends Command {

    private Vector<String> inlineHeaders;
    private StringHolder filePath;
    private StringHolder inlineData;

    public GETCommand(String[] args) {
        super("httpc get [-v] [-h key:value] URL", args);
    }

    @Override
    protected void registerOptions() {
        inlineHeaders = new Vector<>(10);
        inlineData = new StringHolder();
        filePath = new StringHolder();

        argParser.addOption("-h %s #k:v | An request header entry where k is the key and v is the value.", inlineHeaders);
        argParser.addOption("-d %s #The inline-data to consider as the body of the request.", inlineData);
        argParser.addOption("-f %s #Text file input to use content as the body of the request.", filePath);
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


    }
}
