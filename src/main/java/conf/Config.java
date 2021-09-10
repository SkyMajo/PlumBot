package conf;

import control.IRCController;
import ext.ConvertsThread;
import tools.SustainHashMap;

import java.util.HashMap;
import java.util.Vector;

/**
 * @Auth : Sustain ❀
 * @Time : 9-2 -> 2021
 */
public class Config {

    /**
     * =====OSU=====
     * irc.ppy.sh
     * 6667
     * Sustain
     * a57754a5
     */

    public static final String IRC_NAME = "PlumBot";
    public static final String OSU_IP = "irc.ppy.sh";
    public static final String OSU_PORT = "6667";
    public static final String IRC_PASSWORD = "bec8ba5e";
    public static final String k = "3f48757aeff51c8be7fcf328d78632c55d82b12e";
    public static IRCController controller = null;
    public static final String PPY_URL = "https://osu.ppy.sh/";
    public static final String GET_BEATMAPS = "api/get_beatmaps";

    /**
     * ====BiliBili====
     */
    //主播uid
    public static Long AUID = null;
    //直播状态 0不直播 1直播 2轮播
    public static Short lIVE_STATUS = 0;
    //处理信息分类线程
    public static ConvertsThread convertsThread;
    //处理弹幕包集合
    public  static HashMap<String,String> resultStrs = new HashMap<String,String>();
    //双向绑定的id数据集，OSUID是唯一key，频道ID是Value
    public final static SustainHashMap<String , String> player4channels = new SustainHashMap<>();
    public final static SustainHashMap<String , String> channels4player = new SustainHashMap<>();
    //绑定WebSocket与bilibili直播间ID

    public final static SustainHashMap<WebSocket , String> webSocket4RoomId = new SustainHashMap<>();
    public final static SustainHashMap<String , WebSocket> roomId4webSocket = new SustainHashMap<>();


}
