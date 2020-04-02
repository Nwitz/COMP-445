package Httpfs;

import HttpLib.*;
import HttpLib.protocol.IProtocol;
import HttpLib.protocol.TCP;
import HttpLib.protocol.UDP;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "httpfs",
        description = "httpfs is a simple file server.",
        version = "1.0")
public class Httpfs implements Runnable {
    private int _port = 8080;
    // TODO: remove
    private FileManager fileManager;
    private IProtocol protocol;

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

    @Option(names = {"-u", "--udp"},
        description = "Specifies UDP protocol is to be used for communication\n" +
                "TCP is used by default")
    boolean useUDP;

    /**
     * Command execution
     */
    public void run() {
        if (directory == null || Files.notExists(directory) || !Files.isDirectory(directory)) {
            directory = Paths.get("").toAbsolutePath();
            System.out.println("No valid directory given.");
        }

        System.out.println("Will serve files located at " + directory);
        fileManager = new FileManager(directory);

        if (useUDP) {
            protocol = new UDP();
        } else {
            protocol = new TCP();
        }

        HttpRequestHandler handler = new HttpRequestHandler(protocol);
        try {
            handler.listen(_port, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    IRequestCallback callback = request -> {
        HttpResponse httpResponse = new HttpResponse();
        if (request.getRequestMethod().equals(HttpRequestMethod.POST)) {
            try {
                fileManager.writeToFile(request.getUrl().getPath(), request.getBody().toString());
                httpResponse = new HttpResponse(HttpStatusCode.OK);
            } catch (IOException e) {
                httpResponse = new HttpResponse(HttpStatusCode.NotFound);
                System.out.println("onRequestReceived");
                e.printStackTrace();
            }
        } else if (request.getRequestMethod().equals(HttpRequestMethod.GET)) {
            try {
                String responseBody = null;
                Path path = Paths.get(fileManager.directory.toString(), request.getUrl().getPath());

                System.out.println("Got queried for: " + path.toString());

                // Check what type of information to retrieve
                if (!Files.exists(path)) {
                    httpResponse = new HttpResponse(HttpStatusCode.NotFound);
                } else if (Files.isDirectory(path)) {
                    responseBody = fileManager.listFilesInDirectory(new File(path.toAbsolutePath().toString()), 0);
                } else {
                    responseBody = fileManager.readFile(request.getUrl().getPath());
                }

                if (responseBody == null) {
                    httpResponse = new HttpResponse(HttpStatusCode.NotFound);
                } else {
                    HttpRequestBody body = new HttpRequestBody(responseBody);
                    httpResponse = new HttpResponse(HttpStatusCode.OK, body);
                }
            } catch (IOException e) {
                httpResponse = new HttpResponse(HttpStatusCode.NotFound);
                System.out.println("onRequestReceived");
                e.printStackTrace();
            }
        }


        if(verbose){
            System.out.println("Will respond with:");
            System.out.println();
            System.out.println(httpResponse);
        }

        return httpResponse;
    };

    public static void main(String[] args) {
        // Bootstrap entire app
        System.exit(new CommandLine(new Httpfs()).execute(args));
    }

}
