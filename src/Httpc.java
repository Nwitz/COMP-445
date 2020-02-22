import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

@Command(name = "httpc",
        description = "httpc is a curl-like application but supports HTTP protocol only.",
        synopsisSubcommandLabel = "COMMAND",
        subcommands = {CommandLine.HelpCommand.class, GETCommand.class},
        version = "1.0")
public class Httpc implements Runnable {

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;
    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    public static void main(String[] args) {
        // Bootstrap entire app
        System.exit(new CommandLine(new Httpc()).execute(args));
    }

    public static void printToFile(String path, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(content);
        writer.close();
    }
}
