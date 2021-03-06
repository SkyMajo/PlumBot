package service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import conf.Config;
import control.IRCController;
import entity.danmu_data.Barrage;
import entity.danmu_data.Hbarrage;
import tools.CallBack;
import tools.HttpServers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static conf.Config.resultStrs;

/**
 * @author 本間Saki
 * @Changed Sustain ❀
 * @Date 2021年9月3日
 */
public class MessageHandleService {

    private JSONObject jsonObject = null;
    private JSONArray array= null;
    private String message = "";
    private String cmd = "";
    private Barrage barrage;
    private Hbarrage hbarrage;
    private volatile String lastMsg = "";

    StringBuilder stringBuilder = new StringBuilder();

    public void messageHandle(String roomId , ByteBuffer message) throws DataFormatException {
        List<String> s = messageToJson(message);
        for (String s1 : s) {
            try{
                jsonObject = new JSONObject().parseObject(s1);
                cmd = jsonObject.getString("cmd");
                System.out.println(cmd);
                if ("DANMU_MSG".equals(cmd)){
                    //弹幕内容
                    convert();
                    String msg = barrage.getMsg();

                    if (msg.contains("点歌")){
                        String mid = msg.replace("点歌", "").trim();
                        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
                        boolean matches = pattern.matcher(mid).matches();
                        if (matches && !mid.isEmpty()){
                            System.out.println("是数字");

                            new HttpServers()
                                    .setFormValue("b",mid)
                                    .setFormValue("k",Config.k)
                                    .postFormBody(Config.GET_BEATMAPS,new CallBack() {
                                        @Override
                                        public void onSuccess(String data) {
                                            JSONArray array = new JSONArray().parseArray(data);
                                            JSONObject json = array.getJSONObject(0);
                                            String titleUnicode = json.getString("title_unicode");
                                            String bpm = json.getString("bpm");
                                            String hp = json.getString("diff_drain");
                                            String ar = json.getString("diff_approach");
                                            String cs = json.getString("diff_size");
                                            String od = json.getString("diff_overall");
                                            String maxCombo = json.getString("max_combo");
                                            String beatmapsetId = json.getString("beatmapset_id");
                                            String link = "is listening to [osu.ppy.sh/beatmapsets/"+beatmapsetId+"#/"+mid+" "+titleUnicode+"]";

                                            String npStr  = link +"     {||ar:"+ar+" | cs:"+cs+" | hp:"+hp+" | od:"+od+"||} BPM:"+bpm+" || MaxCombo:"+maxCombo+"  --by "+barrage.getUname();
                                            try{
                                                IRCController.bot.send().message(Config.channels4player.get(roomId),npStr);
                                            }catch (Exception e){
                                                System.out.println(e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onFail(String error) {

                                        }
                                    });

                        } else{
                            lastMsg = barrage.getUname()+"说:"+msg;
                            IRCController.bot.send().message(Config.channels4player.get(roomId),lastMsg);
                        }
                    }else{
                        lastMsg = barrage.getUname()+"说:"+msg;
                        IRCController.bot.send().message(Config.channels4player.get(roomId),lastMsg);
                    }
                    System.out.println(lastMsg);
                }
            }catch (com.alibaba.fastjson.JSONException e){
                System.out.println("收到心跳包，包含人气等数据它并不是弹幕直播数据，简单抛一下转换异常");
            }

        }
    }



    private Barrage convert(){
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
        return barrage;
    }


    /**
     * @param message 如果是 message 是弹幕类型，则需要解压拆分
     * @return List<message>
     * @throws DataFormatException DataFormatException
     */
    private List<String> messageToJson(ByteBuffer message) throws DataFormatException {
        byte[] messageBytes = message.array();
        byte[] mainMessageBytes = Arrays
                .copyOfRange(messageBytes, 16, messageBytes.length);

        if (messageBytes[16] != 120) {
            return Arrays.asList(new String(mainMessageBytes, StandardCharsets.UTF_8));
        }

        // 解压缩弹幕信息
        byte[] newByte = new byte[1024 * 5];
        Inflater inflater = new Inflater();
        inflater.setInput(mainMessageBytes);
        newByte = Arrays.copyOfRange(newByte, 16, inflater.inflate(newByte));
        return splitStringToJson(new String(newByte, StandardCharsets.UTF_8));
    }

    /**
     * @param str 包含多条 message 的字符串
     * @return List<message>
     */
    private static List<String> splitStringToJson(String str) {
        List<String> result = new ArrayList<>();
        for (int i = 1, count = 1; i < str.length(); i++) {

            if (str.charAt(i) == '{') {
                count++;
            } else if (str.charAt(i) == '}') {
                count--;
            }

            if (count == 0) {
                result.add(str.substring(0, i + 1));
                int nextIndex = str.indexOf("{", i + 1);
                if (nextIndex != -1) {
                    result.addAll(splitStringToJson(str.substring(nextIndex)));
                }
                return result;
            }
        }
        return result;
    }

}
