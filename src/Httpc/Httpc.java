package Httpc;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "httpc",
        description = "httpc is a curl-like application but supports HTTP protocol only.",
        synopsisSubcommandLabel = "COMMAND",
        subcommands = {
                CommandLine.HelpCommand.class,
                GETCommand.class,
                POSTCommand.class
        },
        version = "1.0")
public class Httpc implements Runnable {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    public static void main(String[] args) {
        // Bootstrap entire app
        System.exit(new CommandLine(new Httpc()).execute(args));
    }

    /////
    // Helper functions

    public static void printHelpAndExit(Runnable command){
        CommandLine commandLine = new CommandLine(command);
        commandLine.usage(System.out);
        System.exit(0);
    }

    public static void printHelpAndExit(Runnable command, String message){
        System.out.println(message);
        printHelpAndExit(command);
    }

    public static void printHelpAndExit(Runnable command, String leading, String trailing){
        System.out.println(leading);
        CommandLine commandLine = new CommandLine(command);
        commandLine.usage(System.out);
        System.out.println(trailing);
        System.exit(0);
    }
}
