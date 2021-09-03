package conf;

import control.IRCController;
import ext.ConvertsThread;

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

    public static final String IRC_NAME = "Sustain";
    public static final String OSU_IP = "irc.ppy.sh";
    public static final String OSU_PORT = "6667";
    public static final String IRC_PASSWORD = "a57754a5";
    public static IRCController controller = null;

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
    public final static HashMap<String , String> player4channels = new HashMap<>();



}
