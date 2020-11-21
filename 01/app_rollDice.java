package rollin;

import java.util.*;

public class app_rollDice{

    /** Main method that calls the random method used to complete the rollin task
     **/
    public static void main(final String[] args) {

        random();
        
    }

    public static void random(){
        int num_of_roll = 0;
        //Dice dice = new Dice(new int[]{3,5,5,5,4,6});
        Dice dice = new Dice();
        Random rand = new Random();
        while(!dice.isComplete()){
            int roll = rand.nextInt(6);
            int d[] = dice.getDice();
            System.out.println("\n\n===============================\n" +Arrays.toString(dice.getDice()) + " + new roll: " + (roll+1));
            
            int diceToReplace = dice.handleRoll(roll);
            if(diceToReplace != -1){
                System.out.println("index to replace = "+diceToReplace  +"   ^"+d[diceToReplace]);
            }
            num_of_roll++;
        }       
        System.out.println("\n!! TWO SET COMPLETED !!\n Took "+ num_of_roll + " roll");
        System.out.println("+"+Arrays.toString(dice.getDice()));
    }


}
