import argparser.ArgParser;

public abstract class Command {

    private final String[] _args;
    protected ArgParser _argParser;

    public Command(String commandName, String[] args) {
        _argParser = new ArgParser(commandName);
        _args = args;
        registerOptions();
    }

    protected abstract void registerOptions();

    public String getHelp() {
        return _argParser.getHelpMessage();
    }

    public void run() {
        _argParser.matchAllArgs(_args, 0, ArgParser.EXIT_ON_ERROR);
    }

}
