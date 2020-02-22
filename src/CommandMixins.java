import picocli.CommandLine.Option;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CommandMixins {
    public static class DefaultOptions {
        @Option(names = {"-v", "--verbose"})
        boolean verbose;

        @Option(names={"-o", "--output"})
        File outputFile;

        public void SaveToFile(String content) throws IOException {
            if(outputFile != null){
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getPath()));
                writer.write(content);
                writer.close();

                System.out.println();
                System.out.println("Content printed to output file: " + outputFile.getPath());
            }
        }
    }

    public static class RequestHeader {
        @Option(names = {"-h", "--headers"})
        Map<String, String> headersMap;
    }
}
