/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kirak
 */
@XmlRootElement
public class HistoryEntry implements Serializable{

    private int chatId;
    private int flag;
    // 0-standard message
    // 1-emergency message
    // 2-assignment inside room message
    // 3-file
    // 4-new assignment (assignmentId inside chatId)
    // 5-assignment accepted (assignmentId inside chatId)
    // 6-assignment done (assignmentId inside chatId)
    private String message;
    private String timestamp;
    private String username;

    public HistoryEntry() {
    }

    public HistoryEntry(int chatId, String username, String message, String timestamp, int flag) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.flag = flag;
        this.chatId = chatId;
    }

    /**
     * @return the message
     */
    @XmlElement
    public String getMessage() {
        return message;
    }

    @XmlElement
    public String getUsername() {
        return username;
    }

    /**
     * @return the timestamp
     */
    @XmlElement
    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return this.username + " : " + this.message + " : " + this.timestamp;
    }

    @XmlElement
    public int getFlag() {
        return flag;
    }

    @XmlElement
    public int getChatId() {
        return chatId;
    }

    
}
