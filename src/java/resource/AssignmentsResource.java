/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resource;

import collections.AssignmentsCollection;
import collections.UsersCollection;
import models.Assignment;
import models.User;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kirak
 */
@Path("/assignments")
public class AssignmentsResource {

    @Context
    private HttpServletRequest request;

    private final AssignmentsCollection assignments;
    private final UsersCollection users;

    public AssignmentsResource() {
        this.assignments = AssignmentsCollection.getInstance();
        this.users = UsersCollection.getInstance();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<Assignment> getAssignmetns(){
       return  this.assignments.getAssignments(this.checkSession());
    }
    
    
    @Path("/{assignmentId}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Assignment getAssignment(@PathParam("assignmentId") int assignmentId){
        return this.assignments.getAssignment(assignmentId, this.checkSession());
    }
    
    @Path("/acceptAssignment/{assignmentId}")
    @PUT
    public void acceptAssignment(@PathParam("assignmentId") int assignmentId) throws IOException, EncodeException{
        this.assignments.acceptAssignment(assignmentId, this.checkSession());
    }
    
    //mark assignment as done
    @Path("/assignmentDone/{assignmentId}")
    @PUT
    public void assignmentDone(@PathParam("assignmentId") int assignmentId) throws IOException, EncodeException{
        this.assignments.assignmentDone(assignmentId, this.checkSession());
    }

    @Path("/{message}")
    @POST
    public int createAssignment(@PathParam("message") String message) throws IOException, EncodeException {
        int retval = -1;
        User currentUser = this.users.getUser(this.checkSession());
        if (currentUser.getRole().equals("doctor") || currentUser.getRole().equals("nurse")) {
            retval = this.assignments.createAssignment(message, this.checkSession());
        }
        return retval;
    }
    
    //check if there is a session and return the username of user having this session
    private String checkSession() {
        HttpSession session = request.getSession(false);
        String username = null;
        if (session != null) {
            username = session.getAttribute("username").toString();
        }
        return username;
    }

}
