/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collections;

import models.Assignment;
import models.HistoryEntry;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.EncodeException;
import filesController.SavingToFiles;
import socket.ServerWebSocket;

/**
 *
 * @author kirak
 */
public class AssignmentsCollection implements Serializable{

    private final Map<Integer, Assignment> assignments;
    private int assignmentCounter;

    private AssignmentsCollection() {
        if (this.restoreAssignments() == null) {
            this.assignmentCounter = 0;
            this.assignments = Collections.synchronizedMap(new HashMap<Integer, Assignment>());
        }else{
            this.assignmentCounter = this.restoreAssignments().getAssignmentCounter();
            this.assignments = Collections.synchronizedMap(new HashMap<Integer, Assignment>(this.restoreAssignments().getAssignments()));
        }
    }

    public static AssignmentsCollection getInstance() {
        return AssignmentsCollectionHolder.INSTANCE;
    }

    private static class AssignmentsCollectionHolder {

        private static final AssignmentsCollection INSTANCE = new AssignmentsCollection();
    }

    //get the AssignmentsCollection class from the file
    private AssignmentsCollection restoreAssignments() {
        AssignmentsCollection retval = null;
        try {
            FileInputStream in = new FileInputStream("assignments.ser");
            ObjectInputStream obin = new ObjectInputStream(in);
            retval = (AssignmentsCollection) obin.readObject();
            obin.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open assignments.ser");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error reading object");
            e.printStackTrace();
        }
        return retval;
    }

    public int getAssignmentCounter() {
        return assignmentCounter;
    }

    public Map<Integer, Assignment> getAssignments() {
        return assignments;
    }

    //create new Assignment
    public int createAssignment(String message, String usernameFrom) throws IOException, EncodeException {
        ArrayList<String> orderlies = UsersCollection.getInstance().getOrderlies();
        int assignmentId = -1;
        if (orderlies != null) {
            assignmentId = this.assignmentCounter;
            Assignment assignment = new Assignment(orderlies, message, assignmentId, usernameFrom);
            this.assignments.put(assignment.getAssignmentId(), assignment);
            this.assignmentCounter++;
            this.sendNotification(assignment);
            new SavingToFiles().saveAssignments();
        }
        System.out.println("assignmentId" + assignmentId);
        return assignmentId;
    }

    public void acceptAssignment(int assignmentId, String username) throws IOException, EncodeException {
        Assignment assignment = this.getAssignment(assignmentId, username);
        if (assignment != null) {
            if (assignment.getUsersPending().contains(username)) {
                if (assignment.getStatus() == 4) {
                    assignment.setStatus(5);
                    assignment.setUserAccepted(username);
                    sendNotification(assignment);
                    assignment.setUsersPendingToNull();
                    new SavingToFiles().saveAssignments();
                }
            }
        }
    }

    //mark assignment as done
    public void assignmentDone(int assignmentId, String username) throws IOException, EncodeException {
        Assignment assignment = this.getAssignment(assignmentId, username);
        if (assignment != null) {
            if (username.equals(assignment.getUserAccepted())) {
                if (assignment.getStatus() == 5) {
                    assignment.setStatus(6);
                    assignment.setUserDone(username);
                    assignment.setUserAccepted("");
                    sendNotification(assignment);
                    new SavingToFiles().saveAssignments();
                }
            }
        }
    }

    //send notification via webSocket about change in assignments
    private void sendNotification(Assignment assignment) throws IOException, EncodeException {
        ServerWebSocket server = new ServerWebSocket();
        HistoryEntry toSend = new HistoryEntry(assignment.getAssignmentId(), "", "", "", assignment.getStatus());
        Collection<String> usernames = new ArrayList<>();
        switch (assignment.getStatus()) {
            case 4:
                usernames.addAll(assignment.getUsersPending());
                break;
            case 5:
                usernames.add(assignment.getUserAccepted());
                break;
            case 6:
                usernames.add(assignment.getUserDone());
                break;
            default:
                break;
        }
        usernames.add(assignment.getUsernameFrom());
        server.sendMessage(usernames, toSend);
    }

    //get assignment by id
    public Assignment getAssignment(int assignmentId, String username) {
        Assignment retval = null;
        if (this.assignments.containsKey(assignmentId)) {
            if (this.assignments.get(assignmentId).hasUser(username)) {
                retval = this.assignments.get(assignmentId);
            }
        }
        return retval;
    }

    //get all assignmetns in ArrayList
    public ArrayList<Assignment> getAssignments(String username) {
        ArrayList<Assignment> retval = new ArrayList<>();
        for (int id : this.assignments.keySet()) {
            Assignment assignment = this.getAssignment(id, username);
            if (assignment != null) {
                retval.add(assignment);
            }
        }
        return retval;
    }

}
