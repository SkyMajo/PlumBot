package component;

import conf.Config;
import conf.danmu.CenterSetConf;
import entity.danmu_data.ShieldMessage;
import ext.ConvertsThread;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auth : Sustain ❀
 * @Time : 9-3 -> 2021
 */
public class ThreadComponentImp implements ThreadComponent{

    /**
     * 开启弹幕处理线程
     */
    @Override
    public boolean startConvertThread(ConcurrentHashMap<ShieldMessage, Boolean> messageControlMap,
                                           CenterSetConf centerSetConf) {
        // TODO 自动生成的方法存根

        if (Config.convertsThread != null && !Config.convertsThread.getState().toString().equals("TERMINATED")) {
            Config.convertsThread.setMessageControlMap(messageControlMap);
//            Config.convertsThread.setThankGiftSetConf(centerSetConf.getThank_gift());
//            Config.convertsThread.setThankFollowSetConf(centerSetConf.getFollow());
//            Config.convertsThread.setThankGiftRuleSets(thankGiftRuleSets);
//            Config.convertsThread.setThankWelcomeSetConf(centerSetConf.getWelcome());
            return false;
        }
        Config.convertsThread = new ConvertsThread();
        Config.convertsThread.FLAG = false;
        Config.convertsThread.start();
        Config.convertsThread.setMessageControlMap(messageControlMap);
//        Config.convertsThread.setThankGiftSetConf(centerSetConf.getThank_gift());
//        Config.convertsThread.setThankFollowSetConf(centerSetConf.getFollow());
//        Config.convertsThread.setThankWelcomeSetConf(centerSetConf.getWelcome());
//        Config.convertsThread.setThankGiftRuleSets(thankGiftRuleSets);
        if (Config.convertsThread != null
                && !Config.convertsThread.getState().toString().equals("TERMINATED")) {
            return true;
        }
        return false;
    }

}
