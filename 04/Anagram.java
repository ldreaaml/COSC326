import java.io.*;
import java.util.*;

public class Anagram{

    public ArrayList<String> dictionary = new ArrayList<String>(); //original copy of dictionary
    public ArrayList<String> words = new ArrayList<String>(); //words to find anagram
    public Map<String,String> map = new HashMap<String,String>(); //store sorted version of dictionary
    
    public ArrayList<String> test = new ArrayList<String>();
    public ArrayList<String> best_pick = new ArrayList<String>();
    public ArrayList<ArrayList<String>> all = new ArrayList<ArrayList<String>>();
    public boolean found = false;
    public boolean bestCaseFound = false;
    public int max_length = 0;
    public int smallest_size = -1;
    public int word_length = 0;
    
    public File f = new File("/home/cshome/p/pjitsawatpaiboon/326/04/Mine/d3.txt");

    public static void main(String[] args){
        Anagram a = new Anagram();
        a.processInput();
        a.getAnagram();
    }
    
    /** process input, initialise dictionary and sort in alphabetical order**/
    public void processInput(){
    	Scanner scan = new Scanner(System.in);			
	        Boolean setDict = false;
	        while(scan.hasNextLine()){ 
	            String str = scan.nextLine().trim().toLowerCase();
	            str = str.replaceAll("(?:-|[\\[\\]\\s{}()+/\\\\',])", "");
	            if(str.isEmpty()){
	                setDict = true;
	            }else{
	                if(setDict){
	                    dictionary.add(str);
	                }else{
	                    words.add(str);
	                }
	            }                
	        }
	        Collections.sort(dictionary); //sort dictionary in alphabetical order
    }
    
    public void getAnagram(){
    	for(int i=0;i<words.size();i++){
            ArrayList<String> anagram = findAnagram(words.get(i));
            System.out.print(words.get(i) + ": ");
            for(String s: anagram) {
            	System.out.print(s+ " ");
            } 
            System.out.println();
        }
    }
    
    public ArrayList<String> findAnagram(String word){
    	
//    	System.out.println(word + " : " + word.length() + "\n");
    	map.clear();
    	best_pick.clear();
    	max_length = 0;
    	smallest_size = -1;
    	word_length = word.length();
    	found = false;
    	bestCaseFound = false;
        ArrayList<String>[] anagramPool = anagramPool(word); //find anagram pool of given word
        ArrayList<String> cur_best = new ArrayList<String>();        
        int length = word.length()<max_length? word.length()-1:max_length-1; //length of anagram to be search (biggest number)
                
        while(length>= 1) {
        	
        	dfs(word,anagramPool,cur_best,length);
            length--;
            max_length--;
	        if( length > 0 && word.length()/length >= smallest_size){ 
                break;
            }
        }
        ArrayList<String> unsorted = new ArrayList<String>();
    	for(String s:best_pick) {
    		unsorted.add(map.get(s));
    	}
        return unsorted;
    }
    
   
    public void dfs(String word, ArrayList<String>[] pool, ArrayList<String> prev, int size){
    	
    	if(size < 0 || bestCaseFound){ //size(index of pool) is -1, anagram is not found and nothing left to search
    		return;
        }
    	for(String candidates : pool[size]){ 
    		String anagram = checkAnagram(candidates,word);
    		
    		if(!anagram.equals(word)){//found anagram

    			ArrayList<String> new_set = new ArrayList<String>();
    			new_set.addAll(prev);
    			new_set.add(candidates);
    			
    			if(anagram.length() == 0){//last item
                    //System.out.println("+" + Arrays.toString(new_set.toArray()));
                    if(best_pick.size() == 0 || new_set.size() < best_pick.size()) {
    					
    					best_pick.clear();
    					best_pick = new_set;
    					smallest_size = best_pick.size();
                        found = true;
                        //System.out.println("Found");
    					if(!bestCaseFound) { //check for best case
    						int l = word_length;
    						boolean b = true;
    				    	for(int i =0; i<best_pick.size();i++) {
    				    		if(best_pick.get(i).length() == (l>max_length?max_length:l)) {
    				    			l -= max_length;
    				    		}else {//not base case
    				    			b = false;
    				    			break;
    				    		}
    				    	}
                            if(b) {//is best case possible, stop searching
    				    		bestCaseFound = true;
    				    	}
    					}
    					return;
    				}
   			 	}else {//not last item, keep searching
   			 		
   			 		int length =  anagram.length()< max_length?  anagram.length()-1: max_length-1;
   			 		//search deeper won't give better option
   			 		if(found && new_set.get(new_set.size()-1).length() < best_pick.get(new_set.size()-1).length()) {
                        
                        // System.out.println("?");    
                        continue;
   			 		}
   			 		dfs(anagram,pool,new_set,length);   			 		
   			 	}
    	
            }
            
    	}//end for loop

    	//found anagram but might find better option?
    	if(size > 1 && found && ( (prev.size()+ word.length()/(size-1)) >= smallest_size)) {
    		dfs(word,pool,prev,size-1);    	
    	}    	
        else if(!found) { //doesn't find anagram, keep searching
    		dfs(word,pool,prev,size-1);    		
    	}
    }
       
    public ArrayList<String>[] anagramPool(String word){
    	//char a-z
    	ArrayList<String>[] p = new ArrayList[word.length()];
    	if(!word.matches("^[A-Za-z\\s]+")) {
//    		System.out.println("not a-z");
    		return p;
    	}
    	
    	int[] char_count = new int[26];
    	for(char c: word.toCharArray()) {
    		char_count[c-'a'] += 1;
    	}
    	
         for(int i=0;i<p.length;i++){
             p[i] = new ArrayList<String>();
         }
         
         for(String s: dictionary){
         	//check if size is valid and not contains character that's not part of word
             if(s.length() <= word.length() && !s.matches(".*[^"+word+"].*")) {
             	
                 char[] c = s.toCharArray(); //sorted word
                 int[] count = new int[26];
                 boolean valid = true;
                 for(char i: c) {
                	 count[i-'a'] += 1;
                	 if(char_count[i-'a'] - count[i-'a'] < 0) {
                		 valid=false;
                	 }
                 }
                 if(!valid) {
                	 continue;
                 }
                 Arrays.sort(c);
                 String sorted = String.valueOf(c);
                 if(c.length > max_length) {
                 	max_length = c.length;
                 }
                 if(!map.containsKey(sorted)){ //don't store duplicated key, set map once
                     map.put(sorted,s);
                     p[s.length()-1].add(sorted); //store sorted version
                 }
             }      
         }
         
         p = Arrays.copyOf(p, max_length);
//         int count = 0;
//         for(ArrayList<String> s : p) { 
//        	System.out.println(s.size() + ":"+Arrays.toString(s.toArray()));
//        	count += s.size();
//         }
//         System.out.println(count);
         return p;
    }



    /** check if given word is anagram of the other word **/
    public String checkAnagram(String candidate, String word){
        StringBuilder w = new StringBuilder(word);
        for(char letter: candidate.toCharArray()){
            int index = w.indexOf(Character.toString(letter));
            if(index != -1){ //found index
                w.deleteCharAt(index);
            }else{//don't index, not an anagram
                return word; //return original word, don't modify word
            }
        }
        //found anagram , return the rest of string that's not part of anagram
        return w.toString();
    }

}
