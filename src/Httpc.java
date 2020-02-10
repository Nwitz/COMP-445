import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;
import argparser.ArgParser;

import java.io.IOException;

public class Httpc {

    private static ArgParser argParser = new ArgParser("httpc");

    public static void main(String[] args) throws IOException, HttpFormatException, InvalidRequestException {
        if (args.length == 0)
            printHelpAndExit();

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
