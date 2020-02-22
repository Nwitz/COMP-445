import picocli.CommandLine.Option;

import java.util.Map;

public class CommandMixins {
    public static class Verbose {
        @Option(names = {"-v", "--verbose"})
        boolean active;
    }

    public static class RequestHeader {
        @Option(names = {"-h", "--headers"}, split=":")
        Map<String, String> headersMap;
    }
}
