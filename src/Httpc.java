import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;
import argparser.ArgParser;
import java.io.*;

public class Httpc {

    private static ArgParser argParser = new ArgParser("httpc");

    public static void main(String[] args) throws IOException, HttpFormatException, InvalidRequestException {
        if (args.length == 0) {
            // TODO: Print help simply in that case
            args = new String[]{"test", "-h", "key1:value1", "-d", "{String: Hello how's it going?}", "-h", "key2:value2", "http://postman-echo.com/post?key1=vaelue1"};
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
                // TODO
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
