import argparser.BooleanHolder;

public class HELPCommand extends Command {
    private BooleanHolder get;
    private BooleanHolder post;

    public HELPCommand(String[] args)  {
        super("httpc command [arguments]", args);
    }

    public HELPCommand(String commandName, String[] args) {
        super(commandName, args);
    }


    @Override
    protected void registerOptions() {
        get = new BooleanHolder();
        post = new BooleanHolder();

        argParser.addOption("get %v #get help for the GET command", get);
        argParser.addOption("post %v #get help for the POST command", post);
    }

    @Override
    public void run() {
        super.run();

        int helpCode = 0;
        if (post.value) {
            helpCode += 1;
        }
        if (get.value) {
            helpCode += 2;
        }

        Command c;
        switch (helpCode) {
            case 1:
                c = new POSTCommand(args);
                c.printHelpAndExit("Post executes a HTTP POST request for a given URL with inline data or from file.");
                break;
            case 2:
                c = new GETCommand(args);
                c.printHelpAndExit("Get executes a HTTP GET request for a given URL.");
                break;
            default:
                printHelpAndExit("Httpc is a curl-like application but supports HTTP protocol only.", "Use \"httpc help [command]\" for more information about a command");
        }
    }
}
