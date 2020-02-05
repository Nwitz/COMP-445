import HttpLib.*;
import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidResponseException;
import argparser.StringHolder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class POSTCommand extends Command {

    private Vector<StringHolder> inlineHeaders = new Vector<>(10);
    private StringHolder filePath = new StringHolder();
    private StringHolder inlineData = new StringHolder();

    public POSTCommand(String[] args) {
        super("httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL", args);
    }

    @Override
    protected void registerOptions() {
        argParser.addOption("-h %s #k:v | An request header entry where k is the key and v is the value.", inlineHeaders);
        argParser.addOption("-d %s #The inline-data to consider as the body of the request.", inlineData);
        argParser.addOption("-f %s #Text file input to use content as the body of the request.", filePath);
    }

    @Override
    public void run() {
        super.run();

        // TODO: Add Verbose functionality (argument already parsed from abstract class)

        // Get URL from last argument
        URL url = null;
        try {
            url = new URL(args[args.length - 1]);
        } catch (MalformedURLException e) {
            // TODO: Add message stating the problem
            printHelpAndExit();
        }

        // Extract all headers from command options
        HttpMessageHeader header = new HttpMessageHeader();
        try {
            for (StringHolder inlineHeader : inlineHeaders)
                header.parseLine(inlineHeader.value);
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
                    // TODO
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO
                    e.printStackTrace();
                }
            }
        }


        HttpRequest request = new HttpRequest(url, HttpRequestMethod.POST, header, body);
        HttpResponse response = null;
        try {
            response = new HttpRequestHandler().send(request);
        } catch (Exception e) {
            // TODO: Handle exceptions less broadly
            e.printStackTrace();
        }
    }
}
