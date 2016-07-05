/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket;

import collections.ChatsCollection;
import models.HistoryEntry;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author kirak
 */
@ServerEndpoint(value = "/endpoint", encoders = {ChatMessageEncoder.class}, decoders = {ChatMessageDecoder.class}, configurator = ServletAwareConfig.class)
public class ServerWebSocket {

    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void onOpen(Session peer, EndpointConfig config) {
        peer.getUserProperties().put("username", config.getUserProperties().get("username"));
        peers.add(peer);
    }

    @OnMessage
    public void onMessage(ChatMessage message, Session userSession) throws IOException, EncodeException {
        String username = (String) userSession.getUserProperties().get("username");
        HistoryEntry retval = ChatsCollection.getInstance().addMessage(message.getChatId(), username, message.getMessage(), message.getFlag(), message.getDescription());
        this.sendMessage(ChatsCollection.getInstance().getUsers(message.getChatId()), retval);
    }

    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }

    public void sendMessage(Collection<String> users, HistoryEntry message) throws IOException, EncodeException {
        for (String username : users) {
            for (Session peer : peers) {
                if (peer.getUserProperties().get("username").equals(username)) {
                    peer.getBasicRemote().sendObject(message);
                }
            }
        }

    }
    /*private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void onOpen(Session peer, EndpointConfig config) {
        peer.getUserProperties().put("username", config.getUserProperties().get("username"));
        peers.add(peer);
    }

    @OnMessage
    public void onMessage(ChatMessage message, Session userSession) throws IOException, EncodeException {
        Collection<User> users = Users.getInstance().getUsers();
        boolean flag = message.isFlag();
        if (flag) {
            userSession.getUserProperties().put("username", message.getUsername());
        } else {
            String chat = message.getChat();
            switch (chat) {
                case "global":
                    String role = message.getRole();
                    for (User user : users) {
                        if (user.getRole().equals(role)) {
                            this.sendMessage(user.getUsername(), message);
                        }
                    }
                    break;
                case "room":
                    String room = message.getRoom();
                    for (User user : users) {
                        if (user.getRooms() != null) {
                            if (user.getRooms().contains(room)) {
                                this.sendMessage(user.getUsername(), message);
                            }
                        }
                    }
                    break;
                default:
                    String usernameTo = message.getUsernameTo();
                    this.sendMessage(usernameTo, message);
                    break;
            }
        }
    }

    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }

    private void sendMessage(String username, ChatMessage message) throws IOException, EncodeException {
        for (Session peer : peers) {
            if (peer.getUserProperties().get("username").equals(username)) {
                peer.getBasicRemote().sendObject(message);
            }
        }
    }*/
}
