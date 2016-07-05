/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resource;

import collections.UsersCollection;
import models.User;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kirak
 */
@Path("/users")
public class UsersResource {
    // public static String username;
    @Context
    private HttpServletRequest request;

    private ChatsResource chatsResource;
    private final UsersCollection users;

    public UsersResource() {
        this.users = UsersCollection.getInstance();
        //this.chats = ChatsCollection.getInstance();
        this.chatsResource = new ChatsResource();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<User> getUsers() {
        return this.users.getUsers(this.checkSession());
    }

    /**
     * Checks the session
     *
     * @return
     */
    @Path("/auth")
    @GET
    public String checkSession() {
        HttpSession session = request.getSession(false);
        String username = null;
        if (session != null) {
            username = session.getAttribute("username").toString();
        }
        return username;
    }

    @Path("/user")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public User getUser() {
        String username = this.checkSession();
        return this.users.getUser(username);
    }

    /**
     * Log out and invalidate the session
     */
    @Path("/auth")
    @POST
    public void logOut() {
        HttpSession session = request.getSession(false);
        session.invalidate();
    }

    /**
     * Add a new user to users collection
     *
     * @param user - XML document from client, representing user object
     * @param password
     * @param adminUsername
     * @param adminPassword
     * @return
     */
    @POST
    @Path("/auth/signUp/{password}/{adminUsername}/{adminPassword}")
    @Consumes(MediaType.APPLICATION_XML)
    public String addUsers(User user, @PathParam("password") String password, @PathParam("adminUsername") String adminUsername, @PathParam("adminPassword") String adminPassword) {
        /*if (!Status.getInstance().isAdminAdded()) {
            this.users.addAdmin();
            Status.getInstance().setAdminAdded();
        }*/

        if (this.adminSignIn(adminUsername, adminPassword)) {
            /*if (!Status.getInstance().isRoomsCreated()) {
                this.chatsResource.createRoomChats(5);
            }
            if (!Status.getInstance().isGlobalCreated()) {
                this.chatsResource.createGlobalChats();
            }*/
            if (user.getRole().equals("orderly")) {
                user.setRooms(new ArrayList<>());
            } 
            user.Password(password);
            if (this.users.addUser(user.getUsername(), user)) {
                this.chatsResource.addUserToGlobalChat(user.getRole(), user.getUsername());
                this.chatsResource.addUserToRoomChat(user.getRooms(), user.getUsername());
                return "true";
            } else {
                return "same-user";
            }
        }
        return "";
    }

    /**
     * Implements login functionality and session
     *
     * @param username
     * @param password
     * @return
     */
    @POST
    @Path("/auth/signIn/{username}/{password}")
    public boolean login(@PathParam("username") String username, @PathParam("password") String password) {
        this.checkOldSession();
        if (username.equals("admin")) {
            return false;
        }
        if (this.users.signIn(username, password)) {
            HttpSession session = request.getSession(true);
            session.setAttribute("username", username);
            //this.username = username;
            return true;
        }
        return false;
    }

    private boolean adminSignIn(String username, String password) {
        return this.users.signIn(username, password);
    }

    //check if there is a session left
    private void checkOldSession() {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
    }

}
