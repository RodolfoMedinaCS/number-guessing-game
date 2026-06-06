package com.learning;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/game")
public class GameEndpoint {

    // this is a shared list of everyone connected, 'static' = ONE shared list
    // across all connections
    //synchronizedSet = safe for many threads to touch at once
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    //shared game across all users
    private static final NumberGame game = new NumberGame();

    //this is the first thing that runs once the clients handshake is complete
    // with the web browser
    //we then add this users session to sessions
    @OnOpen
    public void onOpen(Session session){
        sessions.add(session); // remember this person
        session.getUserProperties().put("attempts", 0);
        sendTo(session, "Please enter a username to join the game");
    }

    //when the user first sends a message this is the first things that fies
    @OnMessage
    public void onMessage(String message, Session sender){

        //set a users name if they haven't already
        if(sender.getUserProperties().get("username") == null){

            sender.getUserProperties().put("username", message.trim());
            sendTo(sender, "Welcome to Guess The Number!");

        }else{
            if(!game.checkValid(message)){
                try{
                    sender.getBasicRemote().sendText("Invalid Input");
                }catch(IOException e){
                    System.out.println("failed to send message " + e.getMessage());
                }
            }else{
                try{
                    //increment the users attempt counter
                    int count = (int) sender.getUserProperties().get("attempts");
                    count++;
                    sender.getUserProperties().put("attempts", count);

                    //display user outcome from guess and attempts
                    String decision = game.guess(message);
                    switch(decision){
                        case "high": {
                            sender.getBasicRemote().sendText(sender.getUserProperties().get("username") +
                                  ": TOO HIGH " + "\n# of attempts: " + sender.getUserProperties().get("attempts"));
                            //broadcast to server who guessed what
                            otherBroadcast(sender, sender.getUserProperties().get("username") + " guessed -> "
                                    + Integer.parseInt(message.trim()));
                        }break;
                        case "low": {
                            sender.getBasicRemote().sendText(sender.getUserProperties().get("username") +
                                    ": TOO LOW " + "\n# of attempts: " + sender.getUserProperties().get("attempts"));
                            //broadcast to server who guessed what
                            otherBroadcast(sender, sender.getUserProperties().get("username") + " guessed -> "
                                    + Integer.parseInt(message.trim()));
                        }break;
                        case "win": {
                            sender.getBasicRemote().sendText(sender.getUserProperties().get("username") +
                                    ": YOU WIN" + "\n# of attempts: " + sender.getUserProperties().get("attempts"));

                            otherBroadcast(sender,sender.getUserProperties().get("username") + ": HAS WON!");
                            resetAttempts();
                            //broadcast to server who guessed what
                            otherBroadcast(sender, sender.getUserProperties().get("username") + " guessed -> "
                                    + Integer.parseInt(message.trim()));

                            broadcast("\n\nSTARTING NEW GAME!\n\n\nRANDOM NUMBER CHOSE START GUESSING!");

                        }break;
                        default:
                            System.out.println("Unknown command.");
                            break;
                    }



                }catch(IOException e){
                    System.out.println("failed to send message " + e.getMessage());
                }
            }

        }

    }

    //when user exits out of the server, this is what fires
    @OnClose
    public void onClose(Session session){
        sessions.remove(session);
        String username = (String) session.getUserProperties().get("username");
        //checks if user has username incase they join and just leave the game
        if(username != null){
            broadcast(username + " has left the game");
        }
    }

    //when user either loses connection or slams laptop shut this fires
    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
    }

    //direct message to a user
    public void sendTo(Session session, String message){
        if(session.isOpen()){
            try{
                session.getBasicRemote().sendText(message);
            }catch(IOException e){
                System.out.println("Failed to send personal message to user " + e.getMessage());
            }
        }
    }

    //server brodcast messaging
    public void broadcast(String message){
        synchronized (sessions){
            for(Session session : sessions){
                sendTo(session, message);
            }
        }
    }

    //brodcast to users except for sender
    public void otherBroadcast(Session sender, String message){
        synchronized (sessions){
            for(Session session : sessions){
                if(session != sender){
                    sendTo(session,message);
                }
            }
        }
    }

    //reset all users attempts
    public void resetAttempts(){
        synchronized (sessions){
            for(Session session :sessions){
                session.getUserProperties().put("attempts", 0);
            }
        }
    }



}
