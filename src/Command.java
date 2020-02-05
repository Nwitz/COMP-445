import argparser.ArgParser;
import argparser.BooleanHolder;

public abstract class Command {

    protected final String[] args;
    protected ArgParser argParser;
    protected BooleanHolder verbose = new BooleanHolder();

    public Command(String commandName, String[] args) {
        argParser = new ArgParser(commandName);
        this.args = args;
        registerOptions();
        argParser.addOption("-v %v #Verbose output flag.", verbose);
    }

    protected abstract void registerOptions();

    public String getHelp() {
        return argParser.getHelpMessage();
    }

    public void run() {
        argParser.matchAllArgs(this.args, 0, ArgParser.EXIT_ON_ERROR);
    }

    public final void printHelpAndExit(){
        System.out.println(argParser.getHelpMessage());
        System.exit(0);
    }

}
