import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.*;
import HttpLib.Exceptions.InvalidResponseException;
import argparser.ArgParser;
import argparser.BooleanHolder;
import argparser.StringHolder;

import java.io.*;
import java.net.URL;
import java.util.Vector;

public class Httpc {

    private static ArgParser _argParser = new ArgParser("httpc");

    public static void main(String[] args) throws IOException, HttpFormatException, InvalidRequestException {
        if (args.length == 0) {
            args = new String[]{"test", "-h", "key1:value1", "-d", "{String: Hello how's it going?}", "-h", "key2:value2", "http://postman-echo.com/post?key1=vaelue1"};
        }

        Vector<StringHolder> inlineHeaders = new Vector<>(10);
        StringHolder filePath = new StringHolder();
        StringHolder inlineData = new StringHolder();
        BooleanHolder verbose = new BooleanHolder();

        _argParser.addOption("-h %s #k:v | An request header entry where k is the key and v is the value.", inlineHeaders);
        _argParser.addOption("-d %s #The inline-data to consider as the body of the request.", inlineData);
        _argParser.addOption("-f %s #Text file input to use content as the body of the request.", filePath);
        _argParser.addOption("-v %v #Verbose output flag.", verbose);

        _argParser.matchAllArgs(args, 1, ArgParser.EXIT_ON_ERROR);

        // Extract sub-command
        HttpRequestMethod method = extractCommand(args[0]);

        // Get URL from last argument
        URL url = new URL(args[args.length - 1]);

        // Extract all headers from command options
        HttpMessageHeader header = new HttpMessageHeader();
        for (StringHolder inlineHeader : inlineHeaders)
            header.parseLine(inlineHeader.value);


        System.out.println("inline: " + inlineData.value);
        System.out.println("file: " + filePath.value);

        // Get Data
        HttpRequestBody body = new HttpRequestBody();
        if (inlineData.value != null && filePath.value != null) {
            // Cannot have both a filePath and inline-data
            System.out.println("HTTPc cannot use both -d and -f and the same time.");
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
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        HttpRequest request = new HttpRequest(url, method, header, body);
        HttpResponse response = null;
        try {
            response = new HttpRequestHandler().send(request);
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        }

        System.out.println("\n\nREQUEST: \n" + request);
        System.out.println("\n\nRESPONSE: \n" +response);
    }

    /**
     * Ensure a HTTP method name as the sub-command token.
     * @return the sub-command as a HttpRequestMethod
     */
    private static HttpRequestMethod extractCommand(String token) {
        HttpRequestMethod method = HttpRequestMethod.get(token);
        if( method == null )
            printHelpAndExit();

        return method;
    }

    /**
     * Will display the command-line information and exit the program.
     */
    private static void printHelpAndExit() {
        System.out.println(_argParser.getHelpMessage());
        System.exit(0);
    }

}
