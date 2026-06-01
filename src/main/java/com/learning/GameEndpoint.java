package com.learning;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/chat")
public class GameEndpoint {
    // static = belongs to the class itself
    // Set<Session> = a collection that holds 'Session' objects and doesn't allow duplicates
    // final = the variable cannot be reassigned

    /*"A single (static), permanent (final), internal (private),
    thread-safe set that holds every connected client's session
    shared across all connections so I can broadcast to everyone."*/

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void welcomeUsers(Session session){
        //add user to sessions when connected
        sessions.add(session);
        System.out.println(session.getId() + " has joined the game!");

    }

    @OnClose
    public void closingUser(Session session){
        System.out.println(session.getId() + " has left the game!");
    }

    @OnMessage
    public void sendMessage(Session session, String message){


    }
}
