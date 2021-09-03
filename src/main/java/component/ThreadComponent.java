package component;

import conf.danmu.CenterSetConf;
import entity.danmu_data.ShieldMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auth : Sustain ❀
 * @Time : 9-3 -> 2021
 */
public interface ThreadComponent {
    // 开启处理弹幕包线程 core
    boolean startConvertThread(ConcurrentHashMap<ShieldMessage, Boolean> messageControlMap,
                                    CenterSetConf centerSetConf);
}
