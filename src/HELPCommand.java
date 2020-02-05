import argparser.BooleanHolder;

public class HELPCommand extends Command {
    private BooleanHolder get;
    private BooleanHolder post;

    public HELPCommand(String[] args)  {
        super("httpc help [post] [get]", args);
    }

    public HELPCommand(String commandName, String[] args) {
        super(commandName, args);
    }


    @Override
    protected void registerOptions() {
        get = new BooleanHolder();
        post = new BooleanHolder();

        argParser.addOption("get, GET %v # get help for the GET command", get);
        argParser.addOption("post, POST %v # get help for the GET command", post);
    }

    @Override
    public void run() {
        super.run();

        int helpCode = 0;
        if (post.value) {
            helpCode+=1;
        }
        if (get.value) {
            helpCode+=2;
        }

        switch(helpCode) {
            case 1:
                System.out.println("help post");
                break;
            case 2:
                System.out.println("help get");
                break;
            default:
                System.out.println("default help");
        }
    }
}
