package com.learning;

import java.util.concurrent.ThreadLocalRandom;

public class NumberGame {

    private int secreteNumber;


    public NumberGame(){
        resetNumber();
    }



    public  synchronized void resetNumber(){
        secreteNumber = ThreadLocalRandom.current().nextInt(1, 101);
    }


    public  synchronized String guess(String guess){
        int userGuess = Integer.parseInt(guess.trim());

        if(userGuess > secreteNumber){
            return "high";
        }else if (userGuess < secreteNumber){
            return "low";
        }else{
            new NumberGame();
            return "win";
        }

    }

    public static boolean checkValid(String message){
        try{
            Integer.parseInt(message.trim());
            return true;
        }catch(NumberFormatException e){
            System.out.println("nota valid Integer " + e.getMessage());
            return false;
        }
    }

}
