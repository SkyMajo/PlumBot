package conf;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import service.MessageHandleService;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

/**
 * @Auth : Sustain ❀
 * @Time : 9-3 -> 2021
 */
public class WebSocket extends WebSocketClient {

    private String url;
    private MessageHandleService messageHandleService;

	public WebSocket(String url) throws URISyntaxException {
        super(new URI(url));
        this.url = url;
    }
 
    @Override
    public void onOpen(ServerHandshake shake) {
        messageHandleService = new MessageHandleService();
    }

    @Override
    public void onMessage(ByteBuffer message) {
        try {
            if (messageHandleService == null){
                return;
            }
            //2021年9月7日 info:已将MessageHandleService修改为全局对象，防止大量创建内存抖动 --Sustain
            String roomId = Config.webSocket4RoomId.get(this);
            messageHandleService.messageHandle(roomId,message);
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
        System.out.println("Closed");
    }
 
    @Override
    public void onError(Exception e) {
        System.out.println(e);
    }

    @Override
    public void onMessage(String message) {
    }

}