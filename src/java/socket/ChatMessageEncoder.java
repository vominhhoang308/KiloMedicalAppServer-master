/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket;

import models.HistoryEntry;
import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author kirak
 */
public class ChatMessageEncoder implements Encoder.Text<HistoryEntry> {

    /*@Override
    public String encode(ChatMessage message) throws EncodeException {
        return Json.createObjectBuilder().add("flag", message.isFlag())
                .add("chat", message.getChat())
                .add("room", message.getRoom())
                .add("role", message.getRole())
                .add("usernameTo", message.getUsernameTo())
                .add("username", message.getUsername())
                .add("message", message.getMessage())
                .build().toString();
    }*/

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public String encode(HistoryEntry object) throws EncodeException {
        return Json.createObjectBuilder().add("chatId", object.getChatId())
                .add("flag", object.getFlag())
                .add("username", object.getUsername())
                .add("timestamp", object.getTimestamp())
                .add("message", object.getMessage())
                .build().toString();
    }

}
