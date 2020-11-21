import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Epidemic{

    public static void main(String[] args){
    	
        Epidemic e = new Epidemic();
        Scanner scan = new Scanner(System.in);
        ArrayList<char[]> input = new ArrayList<char[]>();
        int size = 0;
        
        while(scan.hasNextLine()){
            String str = scan.nextLine();
            if(str.isEmpty()){ //start new universe              
                if(input.size() != 0){
                    char[][] board = new char[input.size()][];
                    for(int i =0; i< input.size();i++){
                    	board[i] = input.get(i);
                    }
                    board = e.next_state(board);
                    e.printBoard(board);
                }
                input.clear(); 
                size = 0;               
            }
            else{//same set of input                
                str = str.trim().replaceAll("\\s", "").toUpperCase();
                if(size == 0 || str.length() == size){ //valid size of column
                    input.add(str.toCharArray());
                    size = str.length();
                }  
            }       
        }
        if(input.size() != 0){
            char[][] board = new char[input.size()][];
            for(int i =0; i< input.size();i++){
            	board[i] = input.get(i);
            }
            board = e.next_state(board);
            e.printBoard(board);
        }
        scan.close();
    }

    /** get final state of the board **/
    public char[][] next_state(char[][] board){
        int num_infected = 0;
        for(int r = 0; r<board.length; r++){
            for(int c = 0; c< board[0].length;c++){
            	//vulnerable cell become sick if two or more neighbours are sick
                if(board[r][c] == '.' && neighboursAreSick(board, r, c)){ 
                    board[r][c] = 'S';
                    num_infected++;
                }
            }
        }
        //while there is vulnerable cells that can be infected
        if(num_infected > 0){ 
            return board = next_state(board);
        }
        
        // no more cells to be infected, return final state
        return board;
    }

    /** check if two or more neighbours are sick **/
    public boolean neighboursAreSick(char[][] board, int r, int c){

        int sick_neighbour = 0;
        ArrayList<int[]> neighbours = get_neighbours(r, c, board.length, board[0].length);

        for(int[] n : neighbours){
            if(board[n[0]][n[1]] == 'S'){
                sick_neighbour++;
            }
        }
        if(sick_neighbour >= 2){ // 2 or more neighbours are sick
            return true;
        }        

        return false;   
    }

    /** get neighbours of given cell **/
    public ArrayList<int[]> get_neighbours(final int r, final int c, final int rows, final int cols) {
        final ArrayList<int[]> list = new ArrayList<int[]>();
        
        if(r-1 >= 0) {
        	list.add(new int[] {r-1,c});
        }
        if(r+1 < rows) {
        	list.add(new int[] {r+1,c});
        }
        if(c-1 >= 0) {
        	list.add(new int[] {r,c-1});
        }
        if(c+1 < cols) {
        	list.add(new int[] {r,c+1});
        }
        return list;
    }
    
    public void printBoard(char[][] board) {
   	 for(char[] r: board){
   		 for(char c: r) {
   			 System.out.print(c);
   		 }
   		 System.out.println();
        }
   	 System.out.println();
   }
    

}
