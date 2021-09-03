import conf.Config;
import control.IRCController;
import org.pircbotx.exception.IrcException;
import service.MakeClientService;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @Auth : Sustain ❀
 * @Time : 9-2 -> 2021
 */
public  class BotStarter {



    public static void main(String[] args) throws IOException, IrcException, URISyntaxException, InterruptedException {
        //Configure what we want our bot to do
        Config.controller = new IRCController();
        Config.controller.start();

    }

}
