package ext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.security.auth.module.UnixSystem;
import conf.Config;
import conf.WebSocket;
import control.IRCController;
import entity.danmu_data.Barrage;
import entity.danmu_data.Hbarrage;
import entity.danmu_data.ShieldMessage;
import org.apache.commons.lang3.StringUtils;
import tools.ParseIndentityTools;

import java.util.concurrent.ConcurrentHashMap;

public class ConvertsThread extends  Thread{

    public volatile boolean FLAG = false;
    private String danMu ;
    private ConcurrentHashMap<ShieldMessage, Boolean> messageControlMap;

    @Override
    public void run() {
        super.run();

        JSONObject jsonObject = null;
        JSONArray array= null;
        String message = "";
        String cmd = "";
        Barrage barrage;
        Hbarrage hbarrage;
        StringBuilder stringBuilder = new StringBuilder();

        while (!FLAG) {
            if (FLAG) {
                System.out.println("数据解析线程手动中止");
                return;
            }
            if (null != Config.resultStrs && !Config.resultStrs.isEmpty()
                    && !StringUtils.isEmpty(Config.resultStrs.get(0))) {
                //get最新的消息
                message = Config.resultStrs.get(0);
                try {
                    jsonObject = JSONObject.parseObject(message);
                } catch (Exception e) {
                    // TODO: handle exception
                    System.out.println("抛出解析异常:" + e);
                    synchronized (Config.convertsThread) {
                        try {
                            Config.convertsThread.wait();
                        } catch (InterruptedException e1) {
                            // TODO 自动生成的 catch 块
                            System.out.println("处理弹幕包信息线程关闭:" + e1);
                        }
                    }
                }
                cmd = jsonObject.getString("cmd");
                switch (cmd){
                    case "DANMU_MSG":
                        array = jsonObject.getJSONArray("info");
                        barrage = Barrage.getBarrage(((JSONArray) array.get(2)).getLong(0),
                                ((JSONArray) array.get(2)).getString(1), array.getString(1),
                                ((JSONArray) array.get(0)).getShort(9), ((JSONArray) array.get(0)).getLong(4),
                                ((JSONArray) array.get(2)).getShort(2), ((JSONArray) array.get(2)).getShort(3),
                                ((JSONArray) array.get(2)).getShort(4), ((JSONArray) array.get(2)).getInteger(5),
                                ((JSONArray) array.get(2)).getShort(6),
                                ((JSONArray) array.get(3)).size() <= 0 ? 0 : ((JSONArray) array.get(3)).getShort(0),
                                ((JSONArray) array.get(3)).size() <= 0 ? "" : ((JSONArray) array.get(3)).getString(1),
                                ((JSONArray) array.get(3)).size() <= 0 ? "" : ((JSONArray) array.get(3)).getString(2),
                                ((JSONArray) array.get(3)).size() <= 0 ? 0L : ((JSONArray) array.get(3)).getLong(3),
                                ((JSONArray) array.get(4)).getShort(0), ((JSONArray) array.get(4)).getString(3),
                                ((JSONArray) array.get(5)).getString(0), ((JSONArray) array.get(5)).getString(1),
                                array.getShort(7));
                        // 过滤礼物自动弹幕
                        if (barrage.getMsg_type() == 0) {
                            hbarrage = Hbarrage.copyHbarrage(barrage);
                            if(barrage.getUid().equals(Config.AUID)) {
                                hbarrage.setManager((short)2);
                            }
                            // 判断类型输出
                            stringBuilder.append(":收到弹幕:");
                            if (getMessageControlMap().get(ShieldMessage.is_barrage_vip) != null
                                    && getMessageControlMap().get(ShieldMessage.is_barrage_vip)) {
                            }else {
                                hbarrage.setVip((short)0);
                                hbarrage.setSvip((short)0);
                            }
                            if (getMessageControlMap().get(ShieldMessage.is_barrage_guard) != null
                                    && getMessageControlMap().get(ShieldMessage.is_barrage_guard)) {
                                // 舰长
                                stringBuilder.append(ParseIndentityTools.parseGuard(barrage.getUguard()));
                            }else {
                                hbarrage.setUguard((short)0);
                            }
                            if (getMessageControlMap().get(ShieldMessage.is_barrage_manager) != null
                                    && getMessageControlMap().get(ShieldMessage.is_barrage_manager)) {
                                // 房管
                                stringBuilder
                                        .append(ParseIndentityTools.parseManager(barrage.getUid(), barrage.getManager()));
                            }else {
                                hbarrage.setManager((short)0);
                            }
                            if (getMessageControlMap().get(ShieldMessage.is_barrage_medal) != null
                                    && getMessageControlMap().get(ShieldMessage.is_barrage_medal)) {
                                // 勋章+勋章等级
                                if (!StringUtils.isEmpty(barrage.getMedal_name())) {
                                    stringBuilder.append("[").append(barrage.getMedal_name()).append(" ")
                                            .append(barrage.getMedal_level()).append("]");
                                }
                            }else {
                                hbarrage.setMedal_level(null);
                                hbarrage.setMedal_name(null);
                                hbarrage.setMedal_room(null);
                                hbarrage.setMedal_anchor(null);
                            }
                            if (getMessageControlMap().get(ShieldMessage.is_barrage_ul) != null
                                    && getMessageControlMap().get(ShieldMessage.is_barrage_ul)) {
                                // ul等级
                                stringBuilder.append("[").append("UL").append(barrage.getUlevel()).append("]");
                            }else {
                                hbarrage.setUlevel(null);
                            }
                            stringBuilder.append(barrage.getUname());
                            stringBuilder.append(" 它说:");
                            stringBuilder.append(barrage.getMsg());

                            //todo 获取弹幕
                            //控制台打印
                            if (getMessageControlMap().get(ShieldMessage.is_cmd) != null
                                    && getMessageControlMap().get(ShieldMessage.is_cmd)) {
                                System.out.println(stringBuilder.toString());
                                if ((barrage.getMsg().contains("点歌")) && (barrage.getMsg().length()>4)){
                                    char[] chars = barrage.getMsg().toCharArray();
                                    StringBuilder builder = new StringBuilder();
                                    for (int i = 0; i < chars.length; i++) {
                                        if ('点'!= chars[i] && '歌'!= chars[i]){
                                            builder.append(chars[i]);
                                        }
                                    }
                                    Config.controller.sendMessage("","https://osu.ppy.sh/b/"+builder.toString());
                                    System.out.println("收到"+barrage.getUname()+"的点歌 ==>  https://osu.ppy.sh/b/"+builder.toString());
                                }
                            }
                            try {
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println(stringBuilder.toString());
                            stringBuilder.delete(0, stringBuilder.length());
                        } else {
                        }
                        break;

                }
            }

        }


    }

    public ConcurrentHashMap<ShieldMessage, Boolean> getMessageControlMap() {
        return messageControlMap;
    }

    public void setMessageControlMap(ConcurrentHashMap<ShieldMessage, Boolean> messageControlMap) {
        this.messageControlMap = messageControlMap;
    }
    public void setDanMuMsg(String msg){
        danMu = msg;
    }





}
