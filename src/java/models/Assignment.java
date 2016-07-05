/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kirak
 */
@XmlRootElement
public class Assignment implements Serializable{

    private String usernameFrom;
    private ArrayList<String> usersPending;
    private String userAccepted;
    private String userDone;
    private String message;
    private int status; //4-pending; 5-accepted; 6-done
    private int assignmentId;

    private Assignment() {
    }

    public Assignment(ArrayList<String> orderlies, String message, int assignmentId, String usernameFrom) {
        this.usersPending = orderlies;
        this.message = message;
        this.status = 4;
        this.assignmentId = assignmentId;
        this.usernameFrom = usernameFrom;
        this.userAccepted = "";
        this.userDone = "";
    }

    public void setUsersPendingToNull() {
        this.usersPending = new ArrayList<>();
    }

    public boolean hasUser(String username) {
        boolean retval = false;
        switch (this.status) {
            case 4:
                retval = this.usersPending.contains(username);
                break;
            case 5:
                retval = this.userAccepted.equals(username);
                break;
            case 6:
                retval = this.userDone.equals(username);
                break;
            default:
                break;
        }
        if (this.usernameFrom.equals(username)) {
            retval = true;
        }
        return retval;
    }

    /**
     * @return the usersPending
     */
    @XmlTransient
    public ArrayList<String> getUsersPending() {
        return usersPending;
    }

    @XmlTransient
    public String getUsernameFrom() {
        return usernameFrom;
    }

    /**
     * @return the userAccepted
     */
    @XmlTransient
    public String getUserAccepted() {
        return userAccepted;
    }

    /**
     * @param userAccepted the userAccepted to set
     */
    public void setUserAccepted(String userAccepted) {
        this.userAccepted = userAccepted;
    }

    /**
     * @return the userDone
     */
    @XmlTransient
    public String getUserDone() {
        return userDone;
    }

    /**
     * @param userDone the userDone to set
     */
    public void setUserDone(String userDone) {
        this.userDone = userDone;
    }

    /**
     * @return the message
     */
    @XmlElement
    public String getMessage() {
        return message;
    }

    /**
     * @return the status
     */
    @XmlElement
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the assignmentId
     */
    @XmlElement
    public int getAssignmentId() {
        return assignmentId;
    }

}
