package com.learning;

import java.util.concurrent.ThreadLocalRandom;

public class NumberGame {

    private static int secreteNumber;


    public NumberGame(){
        resetNumber();
    }



    public static synchronized void resetNumber(){
        secreteNumber = ThreadLocalRandom.current().nextInt(1, 101);
    }


    public static synchronized String guess(String guess){
        int userGuess = Integer.parseInt(guess.trim());

        if(userGuess > secreteNumber){
            return "TOO HIGH!";
        }else if (userGuess < secreteNumber){
            return "TOO LOW";
        }else{
            new NumberGame();
            return "YOU WIN!\n\nGame restarted guess the new number!";
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
