import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;
import argparser.ArgParser;

import java.io.IOException;

public class Httpc {

    private static ArgParser argParser = new ArgParser("httpc");

    public static void main(String[] args) throws IOException, HttpFormatException, InvalidRequestException {
        if (args.length == 0) {
            System.out.println("\n");

//            args = new String[]{"post", "-h", "key1:value1", "-v", "-o", "output/out1.txt", "-h", "content-type:application/x-www-form-urlencoded",  "-f",  "input/in1.txt", "-h", "key2:value2", "http://postman-echo.com/post?key1=value1"};
            args = new String[]{"get", "-h", "key1:value1", "-v", "-o", "output/out1.txt", "-h", "content-type:application/x-www-form-urlencoded",  "-f",  "input/in1.txt", "-h", "key2:value2", "http://postman-echo.com/get?key1=value1"};
//            args = new String[]{"help", "get"};
//            args = new String[]{"help", "post"};
//            args = new String[]{"help"};
//
//            Command c = new HELPCommand(args);
//            c.run();
        }

        // Strip arguments
        String[] commandArgs = new String[args.length-1];
        for(int i=1; i<args.length; i++)
            commandArgs[i-1] = args[i];

        // Route command
        String command = args[0].toUpperCase();
        switch (command){
            case "GET":
                new GETCommand(commandArgs).run();
                break;
            case "POST":
                new POSTCommand(commandArgs).run();
                break;
            case "HELP":
                new HELPCommand(commandArgs).run();
                break;
            default:
                printHelpAndExit();
        }

    }

    /**
     * Will display the command-line information and exit the program.
     */
    private static void printHelpAndExit() {
        System.out.println(argParser.getHelpMessage());
        System.exit(0);
    }

}
