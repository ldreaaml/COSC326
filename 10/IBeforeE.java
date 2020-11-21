import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class IBeforeE{
	
	public ArrayList<String> all_possible_set = new ArrayList<String>();
	public ArrayList<String> forbidden = new ArrayList<String>();
	public ArrayList<String[]> exceptions = new ArrayList<String[]>();
	public ArrayList<Integer>[] nextLength;
	public long[] valid_set;
	public int valid_count = 0;
	public int cur_length = 0;
	
	
    public static void main(String[] args){
        IBeforeE i = new IBeforeE();
		File input 
		= new File("C:/Users/Dream/Desktop/COSC326/10/allSample/r1.txt");
		Scanner sc = new Scanner(System.in);
		boolean forbiddenString = true;
		String allchar = "";
		
		while(sc.hasNextLine()){
			if(allchar.isEmpty()){ //this line is set of chars
				allchar = sc.nextLine().trim();
			}else if(forbiddenString){
				String f = sc.nextLine();
				if(f.isEmpty()){ //line after is not forbidden string
					forbiddenString = false;
				}else{
					i.forbidden(f);
				}
			}else if(!forbiddenString){ //word instance
				
				String s = sc.nextLine().trim(); 
				System.out.print(s + "\t");
				if(s.matches("\\d+")){ //if instance is number, find number of valid sets
					int num = Integer.parseInt(s);
					if(num<=5) {
						i.generateAllPossibleSet(num,allchar);
						long valid_count = i.validSet(allchar,num);
						System.out.println(valid_count);
					}
					if(num>5) {
						i.generateAllPossibleSet(5,allchar);
						i.validSet(allchar,5);
						i.validSetofNextLength(allchar);
						long valid_count = i.validSetOfGivenLength(5,num);
						System.out.println(valid_count);
					}
					
				}else if(s.length() > 0){ //if instance is word, check if word is valid
					boolean result = i.wordIsValid(s, allchar);
					System.out.println(result?"Valid":"Invalid");
				}
			}
		}
	}
    
    
    /** from all possible set, find and return the total number of valid set**/
    public int validSet(String allchar,int length) {
    	if(cur_length == length) { //don't need to recalculate same length
    		return valid_count;
    	}
    	cur_length = length;
    	long[] valid_set = new long[all_possible_set.size()];
    	valid_count = 0;
    	int i = 0;
    	for(String set : all_possible_set) { //for each possible set
    		if(wordIsValid(set,allchar)) { //check if set is valid
    			valid_set[i] = 1;  
    			valid_count++;
    		}else {
    			valid_set[i] = 0;
    		}
    		i++;
    	}
    	this.valid_set = valid_set;
    	return valid_count;
	}
	
    /** check if word is valid, not containing any forbidden string or is an exception **/
    public boolean wordIsValid(String set,String allchar){
        if(set.matches(".*[^"+allchar+"].*")) { //contains other character
            return false;
        }
        
        for(String f: forbidden) { //forbidden string with NO exception
        	if(set.contains(f)) {
        		return false;
        	}
        }
      			
		
        for(String[] f: exceptions){ //each forbidden string with exception
        	
			if(!set.contains(f[0])){ //doesn't contain this forbidden string
                continue;
			}else{ // has forbidden string
                int last = set.length();
                while(last>0){ //check every occurrence of forbidden string
                	int ex = set.substring(0,last).lastIndexOf(f[0]); //position of forbidden index
                	if(ex == -1) { // no index found, exit
                		break;
                	}
                	if(ex == 0) { //exception at first string = not possible!           		
                		return false;
                	}
                	//exception at valid position, check each prefix
                    boolean isException = false;
                    for(int e=1; e<f.length;e++){
                        if(set.substring(0,ex).matches(".*"+f[e])){
                            isException = true;
                            break;
                        }
                    }
                    if(!isException){ //this set doesn't match exception
                        return false;
                    }
                    last -=1;
                }               
			}			
		}
		
  		
        // doesn't contain any forbidden string or is an exception
      	return true;
        
    }
    
    /** find other set that can be use to create new set for next length **/
    public void validSetofNextLength(String allchar) {
    	if(nextLength != null) { //don't recalculate same length
    		return;
    	}
    	nextLength = new ArrayList[all_possible_set.size()];
//    	for(int i = 0; i< nextLength.length;i++) {
//    		nextLength[i] = new ArrayList<Integer>();
//    	}
    	
    	// calculate number of valid set for next length
    	for(int i = 0; i<nextLength.length; i++) {
			String set = all_possible_set.get(i);
			
    		if(checkValidForNext(set,allchar)) { //if is valid set
    			
    			String ending = set.substring(0,set.length()-1); //ending of set that can use to create next length 			
        		ArrayList<Integer> next = new ArrayList<Integer>();
        		for(int c = 0; c<allchar.length();c++) { // find set that has the ending we looking
        			int index = all_possible_set.indexOf(allchar.charAt(c)+ending);
        			next.add(index);
        		}
        		nextLength[i] = next;
    		}else {
    			nextLength[i] = new ArrayList<Integer>(); 
    		}    		
    	}	
    }
    

	/** check if set is valid to be use for finding next set (not naturally forbidden string) **/
	public boolean checkValidForNext(String set,String allchar){
        
        if(set.matches(".*[^"+allchar+"].*")) { //contains other character
            return false;
        }
        for(String f: forbidden) { //forbidden string with NO exception
        	if(set.contains(f)) {
        		return false;
        	}
        }
		
        for(String[] f: exceptions){ //each forbidden string with exception
        	
			if(!set.contains(f[0])){ //doesn't contain this forbidden string, go to next one
                continue;
			}else{ // has forbidden string
                
                int last = set.length();
                while(last>f[0].length()){ //check every occurrence of forbidden string
                	
                	int ex = set.substring(0,last).lastIndexOf(f[0]); //position of forbidden index
                	if(ex == -1) { // no index found
                		break;
                	}
                	if(ex == 0) {// exception at first string = possible
                		break;
                	}
                	//exception at valid position, check each prefix                	
                	
                    boolean isException = false;
                    for(int e=1; e<f.length;e++){ //go through each prefix
                    	String exception = f[e];
                    	if(exception.length() > ex) {
                    		exception = exception.substring(exception.length()-ex,exception.length());
                    	}
                        if(set.substring(0,ex).matches(".*"+exception)){
                            isException = true;
                            break;
                        }
                    }
                    if(!isException){ //gone through every prefix and doesn't find exception
                        return false;
                    }  
                    //last -= f[0].length(); <- work too
                    last = ex; //keep checking the rest of the string
                }
			}
		}
        //is exception or no valid string
      	return true;
        
    }
   
    

	/** calculate number of valid set of words length n */
	public long validSetOfGivenLength(int cur,int length) {
    	long sum = 0;
    	long[] prev = Arrays.copyOf(valid_set, valid_set.length);
    	while(cur<length) {    	
    		long[] next = new long[valid_set.length];
    		for(int i = 0; i< valid_set.length;i++) {
    			for(int set: nextLength[i]) {
    				next[i] += prev[set];		
        		}    			
    		}
    		cur++;
    		prev = next;
    	}
    	
    	for(long l:prev){
    		sum += l;
    	}
    	return sum;
    }
	
	
    /** use given characters to generate all possible combinations of given length.
     * forbidden and prefix are never more than 3 characters
     * **/
    public void generateAllPossibleSet(int length, String allchar) {
    	if(cur_length == length) {//don;t recalculate same length
    		return;
    	}
    	all_possible_set.clear();    	
    	wordCreator(length,allchar.toCharArray(),"");
    }
    public void wordCreator(int length, char[] all_char, String w) {
    	if(w.length() == length) { 
    		StringBuilder s = new StringBuilder(w);
    		s = s.reverse();
    		all_possible_set.add(s.toString());
    		return;
    	}
    	for(char c: all_char) { //adding more character till we get specified length
    		wordCreator(length,all_char,w+c);
    	}    	
    }

    /** set forbidden string and exceptions */
    public void forbidden(String f) {
    	String[] prefix = f.split("\\s");
    	if(prefix.length > 1) { //has exceptions
    		exceptions.add(prefix);
    	}else {
    		forbidden.add(prefix[0]);
    	}
    }
    
    
		

  

}//end file