package Httpfs;

import HttpLib.HttpRequestHandler;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "httpfs",
        description = "httpfs is a simple file server.",
        version = "1.0")
public class Httpfs implements Runnable {
    private int _port = 8080;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Option(names = {"-v", "--verbose"},
            description = "Prints debugging messages.")
    boolean verbose;

    @Option(names = {"-d", "--directory"},
            description = "Specifies the directory that the server will use to read/write\n" +
                    "requested files. Default is the current directory when launching the application.")
    Path directory;

    @Option(names = {"-p", "--port"},
            description = "Specifies the port number that the server will listen and serve at. Default: 8080")
    public void setPort(int port) {
        // Check range
        if (port < 1024 || port > 65535) {
            throw new CommandLine.ParameterException(
                    spec.commandLine(),
                    String.format("The port has to be in a valid, non-reserved range [1024,65535].")
            );
        }
    }

    /**
     * Command execution
     */
    public void run() {
        if (directory == null || Files.notExists(directory)){
            directory = Paths.get("").toAbsolutePath();
            System.out.println("No valid directory given. Will serve files located at " + directory);
        }

        HttpRequestHandler handler = new HttpRequestHandler();
        try {
            handler.listen(_port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Bootstrap entire app
        System.exit(new CommandLine(new Httpfs()).execute(args));
    }

}
