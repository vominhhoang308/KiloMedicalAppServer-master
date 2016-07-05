/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resource;

import collections.UsersCollection;
import collections.ChatsCollection;
import models.RoomChat;
import models.PrivateChat;
import models.HistoryEntry;
import models.GlobalChat;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kirak
 */
@Path("/chats")
public class ChatsResource {

    @Context
    private HttpServletRequest request;

    private final ChatsCollection chats;
    private final UsersCollection users;

    public ChatsResource() {
        this.chats = ChatsCollection.getInstance();
        this.users = UsersCollection.getInstance();
    }

    @Path("/global")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public GlobalChat getGlobalChat() {
        ArrayList<GlobalChat> globalChats = this.chats.getUserChats(this.checkSession(), GlobalChat.class);
        if (globalChats.size() == 1) {
            return globalChats.get(0);
        }
        return null;
    }

    @Path("/rooms")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<RoomChat> getRoomChats() {
        return this.chats.getUserChats(this.checkSession(), RoomChat.class);
    }

    //get history of the chat based on the chatId
    @Path("/history/{chatId}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<HistoryEntry> getHistory(@PathParam("chatId") int chatId) {
        return this.chats.getChatHistory(chatId, this.checkSession());
    }

    @Path("/privateChat")
    @POST
    public int createPrivateChat() throws IOException, EncodeException {
        int chatId = this.chats.addPrivateChat();
        this.chats.addUserToChat(chatId, this.checkSession());
        return chatId;
    }

    @Path("/privateChats")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<PrivateChat> getPrivateChats() {
        return this.chats.getUserChats(this.checkSession(), PrivateChat.class);
    }

    @Path("/privateChat/{chatId}")
    @DELETE
    public boolean leavePrivateChat(@PathParam("chatId") int chatId) {
        return this.chats.leavePrivateChat(chatId, this.checkSession());
    }

    //add user to the private chat
    @Path("/privateChat/{chatId}/{username}")
    @PUT
    public boolean addToPrivateChat(@PathParam("username") String username, @PathParam("chatId") int chatId) {
        boolean retval = false;
        if (this.chats.getUsers(chatId).contains(this.checkSession())) {
            retval = this.chats.addUserToChat(chatId, username);
        }
        return retval;
    }

    //get new messages counter
    @Path("/newMessagesCount/{chatId}")
    @GET
    public int getNewMessagesCount(@PathParam("chatId") int chatId){
        return this.chats.getNewMessagesCount(chatId, this.checkSession());
    }
    
    public void addUserToGlobalChat(String role, String username) {
        int chatId = 0;
        ArrayList<GlobalChat> globalChats = this.chats.getChats(GlobalChat.class);
        for (GlobalChat globalChat : globalChats) {
            if (globalChat.getRole().equals(role)) {
                chatId = globalChat.getChatId();
            }
        }
        System.out.println("addUserMethod");
        this.chats.addUserToChat(chatId, username);
    }

    public void addUserToRoomChat(ArrayList<String> rooms, String username) {
        if (rooms != null) {
            ArrayList<RoomChat> roomChats = this.chats.getChats(RoomChat.class);
            for (String room : rooms) {
                for (RoomChat roomChat : roomChats) {
                    if (roomChat.getRoomNumber().equals(room)) {
                        this.chats.addUserToChat(roomChat.getChatId(), username);
                    }
                }
            }
        }
    }

    //check if there is a session and return the username of user having this session
    private String checkSession() {
        HttpSession session = request.getSession(false);
        String username = null;
        if (session != null) {
            username = session.getAttribute("username").toString();
        }
        return username;
    }
}
