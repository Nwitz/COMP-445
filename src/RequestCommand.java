import argparser.StringHolder;

import java.util.Vector;

public abstract class RequestCommand extends Command {


    protected Vector<String> inlineHeaders = new Vector<>(10);
    protected StringHolder inFilePath = new StringHolder();

    public RequestCommand(String commandName, String[] args) {
        super(commandName, args);

        argParser.addOption("-v %v #Verbose output flag.", verbose);
        argParser.addOption("-h %s #k:v | An request header entry where k is the key and v is the value.", inlineHeaders);
        argParser.addOption("-o %s #File path of file to write response to", inFilePath );
    }


}
