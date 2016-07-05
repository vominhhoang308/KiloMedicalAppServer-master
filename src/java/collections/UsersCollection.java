/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collections;

import models.User;
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
import filesController.SavingToFiles;

/**
 *
 * @author kirak
 */
public final class UsersCollection implements Serializable {

    private final Map<String, User> users;
    private boolean adminAdded;

    private UsersCollection() {
        if (this.restoreUsers() == null){
            this.users = Collections.synchronizedMap(new HashMap<String, User>());
            this.adminAdded = false;
        }else {
            this.users = Collections.synchronizedMap(new HashMap<String, User>(this.restoreUsers().getUsersMap()));
            this.adminAdded = this.restoreUsers().isAdminAdded();
        }
        //add admin
        if (!this.adminAdded) {
            this.addAdmin();
            this.adminAdded = true;
        }
    }

    public static UsersCollection getInstance() {
        return UsersHolder.INSTANCE;
    }

    private static class UsersHolder {

        private static final UsersCollection INSTANCE = new UsersCollection();
    }

    //get the UsersCollection class from the file
    private UsersCollection restoreUsers() {
        UsersCollection retval = null;
        try {
            FileInputStream in = new FileInputStream("users.ser");
            ObjectInputStream obin = new ObjectInputStream(in);
            retval = (UsersCollection) obin.readObject();
            obin.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open users.ser");
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

    /*private Object readResolve() throws ObjectStreamException {
        return UsersHolder.INSTANCE;
    }*/
    public boolean addUser(String username, User user) {
        if (!this.users.containsKey(username)) {
            this.users.put(username, user);
            new SavingToFiles().saveUsers();
            return true;
        } else {
            return false;
        }
    }

    public boolean isAdminAdded() {
        return adminAdded;
    }

    //check for the valid sign in
    public boolean signIn(String username, String password) {
        if (this.users.containsKey(username)) {
            if (this.users.get(username).Password().equals(password)) {
                return true;
            }
        }
        return false;
    }

    //get all users in ArrayList without the current user
    public ArrayList<User> getUsers(String currentUser) {
        ArrayList<User> retval = new ArrayList<>();
        for (User user : this.users.values()) {
            if (!user.getUsername().equals(currentUser) && !user.getUsername().equals("admin")) {
                retval.add(user);
            }
        }
        return retval;
    }

    //get all orderlies
    public ArrayList<String> getOrderlies() {
        ArrayList<String> retval = new ArrayList<>();
        Collection<User> users = UsersCollection.getInstance().getUsers();
        for (User user : users) {
            if (user.getRole().equals("orderly")) {
                retval.add(user.getUsername());
            }
        }
        return retval;
    }

    //check if there is a user with this username
    public boolean hasUser(String username) {
        boolean retval = false;
        if (this.users.containsKey(username)) {
            retval = true;
        }
        return retval;
    }

    public Collection<User> getUsers() {
        return this.users.values();
    }

    public User getUser(String username) {
        return this.users.get(username);
    }

    public Map<String, User> getUsersMap() {
        return this.users;
    }

    
    public void addAdmin() {
        User admin = new User("", "", "", "admin");
        admin.setUsername("admin");
        this.addUser("admin", admin);
    }
}
