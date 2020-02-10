import HttpLib.Exceptions.HttpFormatException;
import HttpLib.*;
import argparser.StringHolder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Pattern;

public class POSTCommand extends RequestCommand {

    StringHolder inlineData;
    StringHolder filePath;

    public POSTCommand(String[] args) {
        super("httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL", args);
    }

    @Override
    protected void registerOptions() {
        inlineData = new StringHolder();
        filePath = new StringHolder();

//        argParser.addOption("-d %s #The inline-data to consider as the body of the request.", inlineData);
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

        // Get Data
        inlineData.value = getInlineData(args);

        HttpRequestBody body = new HttpRequestBody();
        if (!inlineData.value.isEmpty() && filePath.value != null) {
            // Cannot have both a filePath and inline-data
            System.out.println("httpc post cannot use both -d and -f and the same time.");
            System.out.println();
            printHelpAndExit();

        } else {
            if (!inlineData.value.isEmpty()) {
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
            responseString = response.toString();
        } else {
            responseString = response.getBody();
        }

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

    private String getInlineData(String[] args) {
        boolean record = false;
        boolean addLeadingSpace = false;
        String inlineEnd = ".*'$";
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (arg.equals("-d")) {
                record = true;
                continue;
            }
            if (record) {
                if (!addLeadingSpace) {
                    addLeadingSpace = true;
                    if  (arg.matches(inlineEnd)) {
                        sb.append(arg, 1, arg.length()-1);
                        break;
                    } else {
                        sb.append(arg.substring(1));
                    }
                } else {
                    sb.append(" ");
                    if (arg.matches(inlineEnd)) {
                        sb.append(arg, 0, arg.length() - 1);
                        break;
                    } else {
                        sb.append(arg);
                    }
                }
            }

        }
        return sb.toString();
    }
}
