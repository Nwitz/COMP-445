package Httpc;

import HttpLib.protocol.Protocol;
import HttpLib.protocol.TCP;
import HttpLib.protocol.UDP;
import picocli.CommandLine.Option;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CommandMixins {
    public static class DefaultOptions {
        @Option(names = {"-v", "--verbose"},
                description = "Prints the detail of the response such as protocol, status, and headers.")
        boolean verbose;

        @Option(names = {"-o", "--output"})
        File outputFile;

        @Option(names = {"-u", "--udp"},
                description = "Specifies UDP protocol is to be used for communication\n" +
                        "TCP is used by default")
        boolean useUDP;

        public Protocol getProtocol() {
            if (useUDP)
                return new UDP();
            else
                return new TCP();
        }

        public void SaveToFile(String content) throws IOException {
            if (outputFile != null) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getPath()));
                writer.write(content);
                writer.close();

                System.out.println();
                System.out.println("Content printed to output file: " + outputFile.getPath());
            }
        }
    }

    public static class RequestHeader {
        @Option(names = {"-h", "--headers"},
                description = "Associates headers to HTTP Request with the format 'key=value'.")
        Map<String, String> headersMap;
    }
}
