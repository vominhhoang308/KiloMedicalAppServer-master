/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kirak
 */
@XmlTransient
@XmlSeeAlso({GlobalChat.class, RoomChat.class, PrivateChat.class})
public abstract class Chat implements Serializable{

    private ArrayList<HistoryEntry> history;
    private ArrayList<String> users;
    private int chatId;
    // new messages counter for each user inside the chat
    private HashMap<String, Integer> newMessages;

    public Chat() {
    }

    public Chat(int chatId) {
        this.history = new ArrayList<>();
        this.users = new ArrayList<>();
        this.newMessages = new HashMap<>();
        this.chatId = chatId;
    }

    public boolean hasUser(String username) {
        return this.users.contains(username);
    }

    public HistoryEntry addMessage(String username, String message, int flag) {
        HistoryEntry retval = null;
        if (this.hasUser(username)) {
            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            retval = new HistoryEntry(this.chatId, username, message, timestamp, flag);
            this.history.add(retval);
            for (String user : this.newMessages.keySet()) {
                if (!user.equals(username)) {
                    int count = this.newMessages.get(user);
                    System.out.println("User:" + user + "countBefore:" + this.newMessages.get(user));
                    this.newMessages.replace(user, count + 1);
                    System.out.println("User:" + user + "countAfter:" + this.newMessages.get(user));
                }
            }
        }
        return retval;
    }

    //check if this user is inside this chat and set new messages counter to zero
    public ArrayList<HistoryEntry> getHistory(String username) {
        if (!this.hasUser(username)) {
            return null;
        }
        this.newMessages.replace(username, 0);
        return this.history;
    }

    public void addUser(String username) {
        if (!this.hasUser(username)) {
            this.users.add(username);
        }
        if (!this.newMessages.containsKey(username)) {
            this.newMessages.put(username, 0);
        }
    }

    @XmlElement
    public ArrayList<String> getUsers() {
        return this.users;
    }

    @XmlElement
    public int getChatId() {
        return chatId;
    }

    public int getNewMessagesCount(String username) {
        return this.newMessages.get(username);
    }

    /**
     * @param users the users to set
     */
    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    /**
     * @param chatId the chatId to set
     */
    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

}
