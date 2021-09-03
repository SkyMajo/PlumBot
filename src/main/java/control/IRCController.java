package control;

import conf.Config;
import conf.Conn;
import ext.ConvertsThread;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;
import service.MakeClientService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Auth : Sustain ❀
 * @Time : 9-2 -> 2021
 * @PS : Contention of OSU IRC Server
 */
public class IRCController extends ListenerAdapter {


    //OsuChannelThread
    private Thread osuThread;
    private int index = 0;
    private boolean isTest = true;
    public static volatile PircBotX bot = null;

    @Override
    public void onGenericMessage(GenericMessageEvent event) throws Exception {
        super.onGenericMessage(event);
        String msg = event.getMessage();

        if (msg.isEmpty()) {
            return;
        }
        if (msg.contains("!help")){
            event.respondPrivateMessage(Conn.HELP_TIPS);
        }
        if (msg.contains("!bind")){
            System.out.println( Timer()+" "+event.getUser().getNick()+"： "+event.getMessage());
            String nickName = event.getUser().getNick();
            String biLiveUid = msg.replace("!bind", "").trim();
            if (biLiveUid.isEmpty()){
                event.respondPrivateMessage(Conn.HELP_TIPS_2);
            }else {
                Config.player4channels.put(nickName,biLiveUid);
                Config.channels4player.put(biLiveUid,nickName);
                event.respondPrivateMessage(Conn.BLINDED_TIPS+biLiveUid+Conn.HOME);
                System.out.println(Conn.BLINDED_TIPS+biLiveUid+Conn.HOME);
                startBiLiBiLiLiveWebSocket(biLiveUid);
//                if (Config.convertsThread == null) {
//                    System.out.println("DEBUG == > convertsThread IS NULL");
//                    Config.convertsThread = new ConvertsThread();
//                    Config.convertsThread.FLAG = false;
//                    Config.convertsThread.start();
//                }else{
//                    System.out.println("DEBUG == > convertsThread Not NULL");
//                }
//                if (Config.player4channels.get(nickName).isEmpty()){
//                    System.out.println("DEBUG ==> 该用户未绑定");
//                    Config.convertsThread = new ConvertsThread();
//                    Config.convertsThread.FLAG = false;
//                    Config.convertsThread.start();
//                }else{
//                    System.out.println("DEBUG ==> 该用户已绑定");
//                }
            }
        }


    }


    public void sendMessage(String roomId , String str){
        System.out.println("收到点歌===>"+str);
    }

    private void startBiLiBiLiLiveWebSocket(String roomId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new MakeClientService(roomId).start();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private String Timer(){
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
       return sdf.format(date); // 输出已经格式化的现在时间（24小时制）
    }

    public void start() {
        osuThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {  //Try catch so we can handle the interrupted exception we will throw
                    while (!Thread.interrupted()) { //while loop so we can throw interrupted exception in order to close
                        //Configure osu! bot with given info
                        Configuration configuration = new Configuration.Builder()
                                .setAutoNickChange(false)
                                .setOnJoinWhoEnabled(false)
                                .setCapEnabled(true)
                                .setName(Config.IRC_NAME) //Set the nick of the bot. CHANGE IN YOUR CODE
                                .setServerPassword(Config.IRC_PASSWORD)//Server password from https://osu.ppy.sh/p/irc
                                .addServer(Config.OSU_IP) //Join the freenode network
                                .addAutoJoinChannel("#OSU") //Join the official #pircbotx channel
                                .addListener(new IRCController()) //Add our listener that will be called on Events
                                .buildConfiguration();
                        //Create our bot with the configuration
                        bot = new PircBotX(configuration);
                        //Connect to the server
                        try {
                            bot.startBot();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IrcException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    //We may ignore it since we only wanted it so we can break the loop
                } finally {
                    //Close the osuBot so the thread my close properly.
                    bot.close();
                }
            }
        });
        osuThread.start();
    }
}
