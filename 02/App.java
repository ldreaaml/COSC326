
import java.util.*;
import java.io.*;

public class App {
    
    public static void main(String[] args) {
        
        Scanner scan = new Scanner(System.in);   
        ArrayList<Ants> ants = new ArrayList<Ants>();        
        ArrayList<String[]> DNA = new ArrayList<String[]>();
        String[] dna = new String[3];
        int number_of_ants = 0;
        Integer number_of_steps = 0;
      
        while(scan.hasNextLine()){
            
            String input = scan.nextLine();
            dna = input.split(" ");            

            if(dna.length == 1 && !dna[0].isEmpty()){ //input is an integer representing number of step
                try{
                    number_of_steps = Integer.parseInt(dna[0]);
                    
                    //initialize ANT with DNA and step
                    Ants ant = new Ants(DNA, number_of_steps);
                    ants.add(ant);                    
                    ant.calculateFinalPosition(); 
                    number_of_steps =0;
                    number_of_ants++;
                    DNA.clear();                    
                    
                }catch(NumberFormatException e){
                    System.out.println("not an integer :"+ dna[0] +"\n");
                    DNA.clear();
                }
            }
            else if(!dna[0].equals("#") && !dna[0].isEmpty()){ //input is not comments or empty space
                if(dna.length == 3){
                    DNA.add(dna);
                }else{
                    System.out.println("invalid DNA input");
                }
            }
           
        }

        /* print DNA and final position */
        int i = 0;
        for(Ants a: ants){
            a.print();
            a.finalPosition();
            if(i+1< ants.size()){ //add empty line between dna
                System.out.println();
            }
            i++;
        }
        
        scan.close();
    }

  
}
