import argparser.BooleanHolder;
import argparser.StringHolder;

import java.util.Vector;

public class GETCommand extends Command {

    private Vector<StringHolder> inlineHeaders = new Vector<>(10);
    private StringHolder filePath = new StringHolder();
    private StringHolder inlineData = new StringHolder();

    public GETCommand(String[] args) {
        super("httpc get [-v] [-h key:value] URL", args);
    }

    @Override
    protected void registerOptions() {
        argParser.addOption("-h %s #k:v | An request header entry where k is the key and v is the value.", inlineHeaders);
        argParser.addOption("-d %s #The inline-data to consider as the body of the request.", inlineData);
        argParser.addOption("-f %s #Text file input to use content as the body of the request.", filePath);
    }

    @Override
    public void run() {
        super.run();

        // Logic here
        // TODO: Similar to POST command
    }
}
