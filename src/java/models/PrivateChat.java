/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kirak
 */
@XmlRootElement
public class PrivateChat extends Chat{
    
    public PrivateChat() {
    } 

    public PrivateChat(int chatId) {
        super(chatId);
    }
    
    public boolean removeUser(String username){
        boolean retval = false;
        if (this.hasUser(username)) {
            this.getUsers().remove(username);
            retval = true;
        }
        return retval;
    }
    
}
