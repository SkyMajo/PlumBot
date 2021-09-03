package service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import conf.Config;
import entity.danmu_data.Barrage;
import entity.danmu_data.Hbarrage;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static conf.Config.resultStrs;

/**
 * @author 本間Saki
 */
public class MessageHandleService {

    JSONObject jsonObject = null;
    JSONArray array= null;
    String message = "";
    String cmd = "";
    Barrage barrage;
    Hbarrage hbarrage;
    StringBuilder stringBuilder = new StringBuilder();

    public void messageHandle(ByteBuffer message) throws DataFormatException {
        List<String> s = messageToJson(message);
        for (String s1 : s) {
            //todo B站弹幕，发送给用户。
            //弹幕信息
//            Config.convertsThread.setDanMuMsg(s1);
//            Config.resultStrs.put("频道ID","消息");
            //通过频道ID，查询Config里对应的OSUID，发送给绑定的人。
//            System.out.println(s1);
        }
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
