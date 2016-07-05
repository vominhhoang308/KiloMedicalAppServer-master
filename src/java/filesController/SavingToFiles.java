/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesController;

import collections.AssignmentsCollection;
import collections.UsersCollection;
import collections.ChatsCollection;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author kirak
 */
public class SavingToFiles {

    public void saveUsers() {
        UsersCollection users = UsersCollection.getInstance();
        try {
            FileOutputStream out = new FileOutputStream("users.ser");
            ObjectOutputStream obout = new ObjectOutputStream(out);
            obout.writeObject(users);
            obout.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open users.ser");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error writing into file");
            e.printStackTrace();
        }
    }

    public void saveChats() {
        ChatsCollection chats = ChatsCollection.getInstance();
        try {
            FileOutputStream out = new FileOutputStream("chats.ser");
            ObjectOutputStream obout = new ObjectOutputStream(out);
            obout.writeObject(chats);
            obout.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open chats.ser");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error writing into file");
            e.printStackTrace();
        }
    }

    public void saveAssignments() {
        AssignmentsCollection assignments = AssignmentsCollection.getInstance();
        try {
            FileOutputStream out = new FileOutputStream("assignments.ser");
            ObjectOutputStream obout = new ObjectOutputStream(out);
            obout.writeObject(assignments);
            obout.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open assignments.ser");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error writing into file");
            e.printStackTrace();
        }
    }
}
