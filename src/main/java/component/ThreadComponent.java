package component;

import conf.danmu.CenterSetConf;
import entity.danmu_data.ShieldMessage;

import java.util.concurrent.ConcurrentHashMap;

public interface ThreadComponent {
    // 开启处理弹幕包线程 core
    boolean startConvertThread(ConcurrentHashMap<ShieldMessage, Boolean> messageControlMap,
                                    CenterSetConf centerSetConf);
}
