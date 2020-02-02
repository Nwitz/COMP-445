import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.*;
import argparser.ArgParser;
import argparser.BooleanHolder;
import argparser.StringHolder;


import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

public class Httpc {

    // TODO: Handle get|post command (2 different method maybe?)
    // TODO: FileReader -> to body
    // TODO: Inline-Data -> body
    // TODO: Check for application argument library


    public void parseArgs(String[] args) throws IOException, HttpFormatException, InvalidRequestException {
        Vector<String> headers = new Vector<>(10);
        StringHolder filePath = new StringHolder();
        StringHolder inlineData = new StringHolder();
        BooleanHolder verbose = new BooleanHolder();
        BooleanHolder isPost = new BooleanHolder();

        ArgParser argParser = new ArgParser("Httpc");
        argParser.addOption("-h %s", headers);
        argParser.addOption("-d %s", inlineData);
        argParser.addOption("-f %s", filePath);
        argParser.addOption("-v %v", verbose);
        argParser.addOption("POST %v", isPost);

        argParser.matchAllArgs(args, 1, ArgParser.EXIT_ON_ERROR);

        // Get Method from first argument
        HttpRequestMethod method = HttpRequestMethod.valueOf(args[0]);

        // Get URL from last argument
        URL url = new URL(args[args.length - 1]);



        // Get headers
        HttpMessageHeader header = new HttpMessageHeader();
        Iterator value = headers.iterator();
        while (value.hasNext()) {
            header.parseLine(((StringHolder) value.next()).value);
        }

        System.out.println("inline: " + inlineData.value);
        System.out.println("file: " + filePath.value);

        //Get Data
        HttpRequestBody body = new HttpRequestBody();
        if (inlineData.value != null && filePath.value != null) {
            // Cannot have both a filePath and inline-data
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
        HttpResponse response = new HttpRequestHandler().send(request);
        System.out.println("\n\nREQUEST: \n" + request);

        System.out.println("\n\nRESPONSE: \n" +response);
    }

    public void sendRequest() {
    }

}
