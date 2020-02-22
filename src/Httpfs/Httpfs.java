package Httpfs;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;

@Command(name = "httpfs",
        description = "httpfs is a simple file server.",
        version = "1.0")
public class Httpfs implements Runnable {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Option(names = {"-v", "--verbose"},
            description = "Prints the detail of the response such as protocol, status, and headers.")
    boolean verbose;

    @Option(names = {"-p", "--port"},
            description = "Specifies the port number that the server will listen and serve at. Default: 8080")
    int port = 8080;

    @Option(names = {"-d", "--directory"},
            description = "Specifies the directory that the server will use to read/write\n" +
            "requested files. Default is the current directory when launching the application.")
    File directory;

    public void run() {
        System.out.println(directory);
    }

    public static void main(String[] args) {
        // Bootstrap entire app
        System.exit(new CommandLine(new Httpfs()).execute(args));
    }
}
