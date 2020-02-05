import HttpLib.Exceptions.HttpFormatException;
import HttpLib.*;
import argparser.StringHolder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

public class POSTCommand extends Command {

    Vector<String> inlineHeaders;
    StringHolder filePath;
    StringHolder inlineData;

    public POSTCommand(String[] args) {
        super("httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL", args);
    }

    @Override
    protected void registerOptions() {
        inlineHeaders = new Vector<>(10);
        inlineData = new StringHolder();
        filePath = new StringHolder();

        argParser.addOption("-h %s", inlineHeaders);
        argParser.addOption("-d %s", inlineData);
        argParser.addOption("-f %s", filePath);
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

        // Get Data
        HttpRequestBody body = new HttpRequestBody();
        if (inlineData.value != null && filePath.value != null) {
            // Cannot have both a filePath and inline-data
            System.out.println("httpc post cannot use both -d and -f and the same time.");
            System.out.println();
            printHelpAndExit();

        } else {
            if (inlineData.value != null && !inlineData.value.isEmpty()) {
                body.setBody(inlineData.value);
            }
            if (filePath.value != null && !filePath.value.isEmpty()) {
                try {
                    File file = new File(filePath.value);

                    BufferedReader br = new BufferedReader(new FileReader(file));
                    StringBuilder fileStringBuilder = new StringBuilder();

                    String line;
                    while ((line = br.readLine()) != null) {
                        fileStringBuilder.append(line).append("\n");
                    }
                    body.setBody(fileStringBuilder.toString());
                } catch (FileNotFoundException e) {
                    printHelpAndExit(e.getMessage());
                } catch (IOException e) {
                    printHelpAndExit(e.getMessage());
                }
            }
        }


        HttpRequest request = new HttpRequest(url, HttpRequestMethod.POST, header, body);
        HttpResponse response = null;
        try {
            response = new HttpRequestHandler().send(request);
        } catch (Exception e) {
            printHelpAndExit(e.getMessage());
        }


        String responseString = "";
        if (verbose.value) {
            System.out.println("verbose true");
            responseString = response.toString();
        } else {
            responseString = response.getBody();
        }

        //TODO: print to file?
        System.out.println("printing");
        System.out.println(responseString);
    }
}
