package control;

import conf.Config;
import conf.Conn;
import conf.WebSocket;
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
import java.util.regex.Pattern;


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
//        System.out.println(event.getUser().getNick()+": = > "+msg);
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
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            boolean matches = pattern.matcher(biLiveUid).matches();
            if (biLiveUid.isEmpty()){
                event.respondPrivateMessage(Conn.HELP_TIPS_2);
            }else {
                if (!matches){
                    event.respondPrivateMessage("是房间号啦baka，不是中文！");
                }else{
                    /**
                     * @url https://github.com/SkyMajo/PlumBot/issues/1
                     * !bind 已绑定过的直播间 会出现两个WebSocket来推送消息
                     * 看了下HashMap的put方法，因为Key是泛型，这里源码判断是否相等只是判断了Object.equals || ==
                     * 并不是String的方法(泛型被擦除了
                     * so,这里需要去重写一下传值
                     */
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
        else if (msg.contains("!unbind")){
            System.out.println("DEBUG ==> 收到unbind指令");
            //  U sie rin jo xi zo ra
            String nickName = event.getUser().getNick();
            String biLiveUid = Config.player4channels.get(nickName);
            if (biLiveUid.isEmpty()){
                event.respondPrivateMessage("您还尚未绑定。");
            }else{
                Config.player4channels.remove(nickName);
                Config.channels4player.remove(biLiveUid);
                WebSocket webSocket = Config.roomId4webSocket.get(biLiveUid);
                Config.webSocket4RoomId.remove(webSocket);
                Config.roomId4webSocket.remove(biLiveUid);
                webSocket.close();
                event.respondPrivateMessage("已取消房间"+biLiveUid+"与您的绑定。");
                System.out.println("已取消房间"+biLiveUid+"与"+nickName+"的绑定。");
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
