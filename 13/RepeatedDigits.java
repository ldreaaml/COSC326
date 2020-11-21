import java.util.*;

public class RepeatedDigits{
    
    public static void main(String[] args){
        RepeatedDigits r = new RepeatedDigits();
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            String input = sc.nextLine();
            r.process_input(input);
        }
        sc.close();
    }

    /** process line of input, perform task A or task B based on given parameters */
    public void process_input(String input){
        input = input.trim();
        if(input.matches("[AB]\s+\\d+\s+\\d+")){
            String[] s = input.split("\s+");
            if(s[0].equals("A")){
                System.out.println(findLongestRepeatedNum(Integer.parseInt(s[1]),Integer.parseInt(s[2])));
            }
            else if(s[0].equals("B")){
                System.out.println( smallestNumberOf2Base(Integer.parseInt(s[1]),Integer.parseInt(s[2])) );
            }
        }else{
            System.out.println("Bad Line: " + input);
        }
    }
    
    /** Task A - given a base b and an integer n finds the longest block of integers all having
repeated digits in base b that are less than n */
    public String findLongestRepeatedNum(int base, int num){
        
        int[] longestRepeated = {0,0};

        for(int i = num-1; i > 0 ; i--){
            String n = convertBase10(base, i);
            if(n.length() < longestRepeated[1]){ //current longest block is longer than this num, no way to get higher number
                break;
            }
            int[] repeatedNum = countRepeatedNum(base, n);
            // System.out.println(i + " : " + n + "\t\t" + Arrays.toString(repeatedNum));
            if(repeatedNum[1] >= longestRepeated[1]){
                longestRepeated = repeatedNum;
            }
        }
        return longestRepeated[0] + " " + longestRepeated[1];
    }

    /** Task A - find the longest block of repeated  number */
    public int[] countRepeatedNum(int base, String num){
        int longest = 0;
        int prev = -1;
        int count = 1;
        int[] b = new int[base];
        
        for(int i =0;i<num.length();i++){
            int n = Character.getNumericValue(num.charAt(i));
            if(prev != n){ // start new block
                count = 1;
            }else{ //same block             
                count++;   
            }
            if(count>b[n]){ //longest block for number n
                b[n] = count;
                if(b[n] > b[longest]){ //longest block for all number
                    longest = n;
                }
            } 
            prev = n;
        }
        return new int[]{longest, b[longest]};
    }

    
    /** Task B - given base b and c, find smallest int n which has repeated digit in both base */
    public int smallestNumberOf2Base(int b, int c){        
        int smallest = b>c?b:c;
        while(true){
            if(hasRepeatedDigits(b, smallest) && hasRepeatedDigits(c, smallest)){
                break;
            }
            smallest++;
        }
        return smallest;      
    }


     /** Task B - convert number to given base and check if there's a repeated digit */
     public boolean hasRepeatedDigits(int base, int num){

        String number = convertBase10(base, num); 
        boolean repeated = false;
        int[] b = new int[base];
        
        for(int i =0;i<number.length();i++){
            int ch = Character.getNumericValue(number.charAt(i));
            if(b[ch] == 1){
                repeated = true;
                break;
            }
            b[ch]++;
        }
        
        return repeated?true:false;
    }

    /** convert number in base 10 to given base */
    public String convertBase10(int base, int num){
		StringBuilder n = new StringBuilder();
        while(num != 0){
            int remainder = num%base;
            n.append(Integer.toString(remainder));
            num = (int) num/base;
        }
        n = n.reverse(); //actual program doesnt need reverse
		return n.toString();
	}

}

