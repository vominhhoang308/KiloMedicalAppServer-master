/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import collections.UsersCollection;

/**
 *
 * @author kirak
 */
@XmlRootElement
public class RoomChat extends Chat {

    private String roomNumber;

    public RoomChat() {
        this.roomNumber = null;
    }
    
    public RoomChat(String roomNumber, int chatId) {
        super(chatId);
        this.roomNumber = roomNumber;
    }

    @Override
    public void addUser(String username) {
        if (!this.hasUser(username)) {
            ArrayList<String> rooms = UsersCollection.getInstance().getUser(username).getRooms();
            if (rooms != null) {
                if (rooms.contains(this.roomNumber)) {
                    super.addUser(username);
                }
            }

        }
    }

    @XmlElement
    public String getRoomNumber() {
        return roomNumber;
    }

}
