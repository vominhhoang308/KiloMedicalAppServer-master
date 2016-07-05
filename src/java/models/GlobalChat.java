/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import collections.UsersCollection;

/**
 *
 * @author kirak
 */
@XmlRootElement
public class GlobalChat extends Chat {

    
    private final String role;

    public GlobalChat() {
        this.role = null;
    }
    
    public GlobalChat(String role, int chatId) {
        super(chatId);
        this.role = role;
    }

    @Override
    public void addUser(String username) {
        if (!this.hasUser(username)) {
            if (UsersCollection.getInstance().getUser(username).getRole().equals(this.role)) {
                super.addUser(username);
            }
        }
    }

    @XmlElement
    public String getRole() {
        return role;
    }

    
}
