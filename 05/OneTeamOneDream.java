package seqtournament;

import java.util.*;

public class OneTeamOneDream implements Player{
	private final boolean OPPONENT = true;
	private final int MAX_DEPTH = 5;
	private boolean debug_on = false;
	
	int state;
	int[][] board;
	int row;
	int column;
	
	/** constructor to initialise default state **/
	public OneTeamOneDream() {
		this.state = 2; // default state
    }

	/** constructor to initialise state 
	 * @param state -  1 for fixed behaviour , 2 for adaptive behaviour**/
	public OneTeamOneDream(int state) {
		this.state = state;
    }
	
    /** @param board - current game board 
     *  @return your move as a three element array {r, c, v} intended to indicate that you wish to make board[r][c] = v.
    **/
    public int[] makeMove(int[][] board){

    	this.row = board.length;
		this.column = board[0].length;
		int[] move = searchBestMove(board,0);
		return move;
	}
	
    
	/** selecting best move by considering 4 factors
	 *  1. number of opponent's possible move (less moves are better)
	 *  2. number of my possible move         (more moves are better)
	 *  3. opponent highest number            (lower is better)
	 *  4. my highest number                  (higher is better)
	 *  @param board - current state of the board
	 *  @param next_move - all possible move that can be made
	 *  @return best possible picks
	 * **/
	public ArrayList<int[]> heuristic(int state, ArrayList<int[]> next_move, int[][] board){

		ArrayList<int[]> best_pick = new ArrayList<int[]>();
		
    	if(state == 1){ //one fixed behaviour, focus on getting higher score 
    		best_pick = pick_by_my_highestNumber(board,next_move);
    		best_pick= pick_by_my_number_of_move(board,best_pick);
    		best_pick = pick_by_opponent_highestNumber(board, best_pick);
    		best_pick = pick_by_opp_number_of_move(board,best_pick);
		}

		if(state == 2){ // behaviour of the player adapt to current state of the board
			
			// if my player's highest number is higher than opponent's highest number, focus on blocking opponent from getting higher score
			if(score_diff(board) > 1) { 
				best_pick = pick_by_opponent_highestNumber(board,next_move);
				best_pick = pick_by_opp_number_of_move(board,best_pick);
				best_pick = pick_by_my_highestNumber(board, best_pick);
				best_pick = pick_by_my_number_of_move(board,best_pick);
			}
			// if opponent's highest number is higher than my player's highest number, focus on getting higher score
			else { 
				best_pick = pick_by_my_highestNumber(board,next_move);
				best_pick= pick_by_opp_number_of_move(board,best_pick);
				best_pick = pick_by_my_number_of_move(board, best_pick);
				best_pick = pick_by_opponent_highestNumber(board,best_pick);
			}
		}
		
		/* from testing, the first option tends to win with higher score but the second option has more reliable winning chance */
		
    	return best_pick;
	}


	/** method that search for the best move on the board 
	 * @param board - current state of the board
	 * @param depth - depth of lookahead search
	 * @return best coordinates to move to and highest number to place**/
    public int[] searchBestMove(int[][] board, int depth) {
    	
    	//get all possible move from current state of the board
    	ArrayList<int[]> next_move = possibleMove(board,!OPPONENT);
    	    	
    	if(depth ==0 && debug_on) {
    		debug(board,next_move);
		}	
    	
        ArrayList<int[]> my_pick = heuristic(this.state, next_move, board);
        
        if(my_pick.size() == 1) { // if there is only one best pick, this is the final option.
        	int[] pick = my_pick.get(0);
        	if(depth == 0) { //final pick
        			return new int[] {pick[0],pick[1],pickNumber(pick,board,!OPPONENT)};    
        	}else { //for look ahead search
             	int[][] next = boardCopy(board);
             	next[pick[0]][pick[1]] = pickNumber(pick,next, !OPPONENT);
                return new int[] {pick[0],pick[1], score_diff(next)};  
        	}
        	
        }else {  // if there are more than one best picks, use look ahead approach
            int[] final_pick = nextDepth(board, my_pick, depth);
            if(depth == 0){
            	final_pick[2] = pickNumber(final_pick,board,!OPPONENT);
                return final_pick;
            }else{
            	final_pick[2] = score_diff(board);
                return  final_pick;
            }
        }
	}

    
    /** lookahead approach. if opponent can still make move, try to cut off opponent's best move if possible
     * otherwise, if there is no move left for opponent. recursively search for next best move (until board is full or reach max depth) to find the move that gives highest result
     * @param board - current state of the board
     * @param possible_pick - candidates for best move
     * @param depth - depth of lookahead search
     * @return one best pick out of all the candidates**/
    public int[] nextDepth(int[][] board, ArrayList<int[]> possible_pick,int depth) {
    	
    	 ArrayList<int[]> opp_possible_move = possibleMove(board,OPPONENT);
    	 
         if(opp_possible_move.size() > 0){ // CASE 1 : if opponent can still make a move
         	int[][] opp_turn = negativeBoard(board);
         	ArrayList<int[]> opp_best_move = pick_by_my_highestNumber(board,opp_possible_move);
         	opp_best_move= pick_by_my_number_of_move(board,opp_best_move);
         	opp_best_move = pick_by_opponent_highestNumber(board, opp_best_move);
         	opp_best_move = pick_by_opp_number_of_move(board,opp_best_move);
         	
         	int[] opp_next_move = opp_best_move.get(0);
         	opp_turn[opp_next_move[0]][opp_next_move[1]] = pickNumber(opp_next_move,opp_turn,!OPPONENT);
         	
         	//try to cut off opponent best move
         	for(int[] pick : possible_pick) {
         		if(Arrays.equals(opp_next_move,pick)) {
         			return new int[] {pick[0],pick[1],pickNumber(pick,board,!OPPONENT)};
         		}
         	}
         	//or random move from my best pick
         	Random r = new Random();
         	int[] pick = possible_pick.get(r.nextInt(possible_pick.size()));
         	return new int[] {pick[0],pick[1],pickNumber(pick,board,!OPPONENT)};
         }
    	
         else{  /* CASE 2 : Opponent has no move */
        	
            int[] score = new int[possible_pick.size()];
            int highest_score = -1;
            ArrayList<int[]> bestPick = new ArrayList<int[]>();
        	
	        for(int i =0;i<possible_pick.size();i++){ 
	        	
	            int[] move = possible_pick.get(i);
	            int[][] next_state = boardCopy(board);
	            next_state[move[0]][move[1]] = pickNumber(move, next_state, !OPPONENT);
	            	
	            if(Utilities.hasMove(next_state) && depth < MAX_DEPTH ){ //After taking this move, check for next move (if exist)
	            	
                    int[] next_move = searchBestMove(next_state, depth+1);  //recursively look at next move
                    next_state[next_move[0]][next_move[1]] = score_diff(next_state);
                    score[i] = next_move[2];
                    if(highest_score == -1 || score[i] > score[highest_score]){
                        highest_score = i;
                        bestPick.clear();
                        bestPick.add(possible_pick.get(i));
                    }else if(score[i] == score[highest_score]){
                        bestPick.add(possible_pick.get(i));
                    }
                }else {// no move left for next turn
                	bestPick.add(possible_pick.get(i));
                	score[i] = score_diff(next_state);
                	return new int[] {bestPick.get(0)[0],bestPick.get(0)[1],score[0]};
                }
	        }
	        if(bestPick.size() > 1){ 
	            return new int[] {bestPick.get(0)[highest_score],bestPick.get(0)[highest_score],score[highest_score]};
	        }else{
	            return new int[] {bestPick.get(0)[0],bestPick.get(0)[1],score[0]};
	        }
	        
    	}
    }
	
    /** method that find all the possible move from current state of the board
     * @param board - current state of the board
     * @param isOpponent - find my player's move or opponent's move
     * @return all the possible move that can be made
     * **/
	public ArrayList<int[]> possibleMove(int[][] board, boolean isOpponent){
		
		int opp = (isOpponent? -1:1); // find my player's cell or opponent's cell
    	ArrayList<int[]> possible_move = new ArrayList<int[]>();
    	
    	for(int r =0; r<row; r++) {
    		for(int c= 0; c<column; c++) {
    			if(board[r][c]*opp > 0) { // if the cell is not empty				
	    			ArrayList<int[]> neighbours = Utilities.neighbours(r,c, row, column); 
	    			for(int[] i: neighbours) {
	        			if(board[i[0]][i[1]] == 0) { // neighbour cells that are empty are count as possible move
	    	    			boolean repeated = false;
	    	    			for(int[] j: possible_move) { //check if duplicated
	    	    				if(Arrays.equals(i, j)) {
	    	    					repeated = true;
	    	    					break;
	    	        			}
	    	    			}
	    	    			if(!repeated) {
	    	    				possible_move.add(i);
	    	    			}
	        			}   			
	        		}
    			}
    		}
    	}

    	return possible_move;
	}

	
	/** method that returns difference between between my highest number and opponent number
	 * @param board - current state of the game
	 * @return score difference between my highest number and opponent number **/
	public int score_diff(int[][] board){
		int my_highest = 0;
		int opp_highest = 0;
		for(int r =0; r<row; r++) {
			for(int c= 0; c<column; c++) {
				if(board[r][c] > my_highest){ //find my highest number
					my_highest = board[r][c];
				}
				if(board[r][c] < opp_highest){ //find opponent's highest number
					opp_highest = board[r][c];
				}
			}
		}
		return (my_highest - Math.abs(opp_highest));
	}

    
	/** method that returns number of possible move from given state of the board 
     * @param board - current state of the board
     * @param isOpponent - find my player's move or opponent's move
     * @return all the possible move that can be made
     * **/
    public int num_of_possible_move(int[][] board, boolean isOpponent) {
    	int opp = (isOpponent? -1:1); // find my player's cell or opponent's cell
    	ArrayList<int[]> possible_move = new ArrayList<int[]>();
    	
    	for(int r =0; r<row; r++) {
    		for(int c= 0; c<column; c++) {
    			if(board[r][c]*opp > 0) {  // if the cell is not empty	   				
	    			ArrayList<int[]> neighbours = Utilities.neighbours(r,c, row, column); 
	    			for(int[] i: neighbours) {
	        			if(board[i[0]][i[1]] == 0) {  // neighbour cells that are empty are count as possible move
	    	    			boolean repeated = false;
	    	    			for(int[] j: possible_move) { //check if duplicated
	    	    				if(Arrays.equals(i, j)) {
	    	    					repeated = true;
	    	    					break;
	    	        			}
	    	    			}
	    	    			if(!repeated) {
	    	    				possible_move.add(i);
	    	    			}
	        			}   			
	        		}
    			}
    		}
    	}
    	
    	return possible_move.size();
	}
	
	
    /** method for copying board  
     * @param board - board to copy
     * @return copy of given board 
     * **/
    public int[][] boardCopy(int[][] board) { 
        final int[][] array = new int[this.row][];
        for (int i = 0; i < this.row; ++i) {
            array[i] = Arrays.copyOf(board[i], this.column);
        }
        return array;
	}

    /** method that picks the highest possible number to be placed
     * @param board - current state of the board
     * @param isOpponent - if the number is for 
     * @return highest number from neighbour cell +1
     * **/
	public int pickNumber(int[] cell, int[][] board,boolean isOpponent) {
		int opp = isOpponent? -1:1;
		int highest_num = 0;
    	int num;
    	ArrayList<int[]> neighbours = Utilities.neighbours(cell[0], cell[1], row, column);    		
		for(int[] i: neighbours) {			
			if((num = board[i[0]][i[1]]*opp) > highest_num) {
				highest_num = num;
			}			
		}		
    	return (highest_num+1)*opp;
	}

    
	/** method that returns best pick which decide based on the highest number that opponent can place in the next turn 
	    * @param board - current state of the board
	    * @param possible_move - all the possible that we can make
	    * @return pick - best pick from all the possible move
	    * **/
    public ArrayList<int[]> pick_by_opp_number_of_move(int[][] board, ArrayList<int[]> possible_move){
		//from all of my possible move, pick the option that give opponent least number of move
	   int opp_lowest = -1;
	   ArrayList<int[]> first_pick = new ArrayList<int[]>();
	   
	   for(int[] move:possible_move) { //for each possible move

		   int[][] next_state = boardCopy(board);
		   next_state[move[0]][move[1]] = 1;
		   
		   /*what is opponent chance of winning if we take this move*/
		   int opp_num = num_of_possible_move(next_state,OPPONENT);
		   

		   /* pick that give least opponent possible move */
		   if(opp_lowest == -1 || opp_num<opp_lowest) {
			   opp_lowest = opp_num;
			   first_pick.clear();
			   first_pick.add(move);
		   }else if(opp_lowest == opp_num) {
			   first_pick.add(move);
		   }
			   
	   }
	   return first_pick;
   }
   
    /** method that returns best pick which decide based on the number of move we can make in the next turn 
     * @param board - current state of the board
     * @param possible_move - all the possible that we can make
     * @return pick - best pick out of all the possible move
     * **/
   public ArrayList<int[]> pick_by_my_number_of_move(int[][] board, ArrayList<int[]> possible_move){
	   ArrayList<int[]> third_pick = new ArrayList<int[]>();
	   int best_thirdPick = -1;
	   if(possible_move.size() > 0) {
		   for(int[] num: possible_move) {
			   int[][] next_state = boardCopy(board);
			   next_state[num[0]][num[1]] = 1;
			   int my_n = num_of_possible_move(next_state,!OPPONENT);
			   if(my_n > best_thirdPick) {
				   best_thirdPick = my_n;
				   third_pick.clear();
				   third_pick.add(num);					
			   }else if(my_n == best_thirdPick){
				   third_pick.add(num);
			   }
		   }
	   }
	   return third_pick;
   }

   /** method that returns best pick which decide based the highest number that can be placed in the next turn 
    * @param board - current state of the board
    * @param possible_move - all the possible that we can make
    * @return pick - best pick out of all the possible move
    * **/
   public ArrayList<int[]> pick_by_my_highestNumber(int[][] board, ArrayList<int[]> possible_move){
	   ArrayList<int[]> fourth_pick = new ArrayList<int[]>();
	   int my_highest_possible = 0;
	   for(int[] num:possible_move){
		   int number_to_place = pickNumber(num, board,!OPPONENT);
		   if(number_to_place > my_highest_possible){
			   fourth_pick.clear();
			   fourth_pick.add(num);
			   my_highest_possible = number_to_place;
		   }else if (number_to_place == my_highest_possible){
			   fourth_pick.add(num); 
		   }
	   }
	   return fourth_pick;
   }
   
   /** method that returns best pick which decide based on the number of move opponent can make in the next turn 
    * @param board - current state of the board
    * @param possible_move - all the possible that we can make
    * @return pick - best pick out of all the possible move
    * **/
   public ArrayList<int[]> pick_by_opponent_highestNumber(int[][] board, ArrayList<int[]> possible_move){
	   int opp_highest_possible = 0;
	   ArrayList<int[]> fifth_pick = new ArrayList<int[]>();
	   for(int[] num:possible_move){
		   int[][] next_state = boardCopy(board);
		   next_state[num[0]][num[1]] = 1;
		   int number_to_place = pickNumber(num, next_state,OPPONENT);
		   //System.out.println("* " + Arrays.toString(num) + " " + number_to_place);
		   if(opp_highest_possible == 0 || number_to_place < opp_highest_possible){
			   fifth_pick.clear();
			   fifth_pick.add(num);
			   opp_highest_possible = number_to_place;
		   }else if (number_to_place == opp_highest_possible){
			   fifth_pick.add(num); 
		   }
	   }
	   return fifth_pick;
   }
   	
    /** method that prints board
     * @param board to print **/
	public void printBoard(int[][] board){
		final StringBuilder b = new StringBuilder();
        for (int i = 0; i < this.row; ++i) {
            for (int j = 0; j < this.column; ++j) {
                b.append(String.format("%3d", board[i][j]));
            }
            b.append("\n");
        }
        System.out.print(b.toString());
	}


	/** method that reverse the board, make negative number positive and make positive number negative 
	 * @board board to be reverse
	 * @return new board**/
    public int[][] negativeBoard(int[][] board){
    	int[][] new_board = boardCopy(board);
    	for(int r =0; r<row; r++) {
    		for(int c= 0; c<column; c++) {
    			new_board[r][c] *= -1;
    		}
    	}
    	return new_board;
    }

	/** @return Player's name **/
    public String getName(){
    	return "One Team One Dream";    	
	}
    
    /** method for debugging, show number of possible move and highest number for my player and opponent **/
	public void debug(int[][] board, ArrayList<int[]> next_move) {    	
    	System.out.println("possible move = " + Arrays.deepToString(next_move.toArray()));
		int highest = -1;
    	int opp_lowest = -1;
    	ArrayList<int[]> opp_all_move = new ArrayList<int[]>();
    	ArrayList<int[]> first_pick = new ArrayList<int[]>(); //pick based on number of opponent possible move
    	ArrayList<int[]> second_pick = new ArrayList<int[]>(); //pick based on number of my possible move
    	
		if(true) {
	    	for(int[] move:next_move) { //for each possible move
	    		int[][] next_state = boardCopy(board);
	    		next_state[move[0]][move[1]] = 1;	    		
	    		int opp_num = num_of_possible_move(next_state,OPPONENT);
	    		
	    		ArrayList<int[]> opp_next_move = possibleMove(next_state,OPPONENT);
				/* pick that give least opponent possible move */
	    		if(opp_lowest == -1 || opp_num<opp_lowest) {
					opp_lowest = opp_num;
	    			opp_all_move = possibleMove(next_state,!OPPONENT);
	    			first_pick.clear();
	    			first_pick.add(move);
	    		}else if(opp_lowest == opp_num) {
	    			first_pick.add(move);
				}
					
				int my_n = num_of_possible_move(next_state,!OPPONENT);
				if(my_n > highest) {
					highest = my_n;
					second_pick.clear();
					second_pick.add(move);
				}else if(my_n == highest) {
					second_pick.add(move);
				}
				int number_to_place = pickNumber(move, board,!OPPONENT);
	
				int opp_highest_number = -1;
				for(int[] num:opp_next_move){
					int opp_n = pickNumber(num, next_state,OPPONENT);				
					if(opp_n < opp_highest_number){
						opp_highest_number = opp_n;
					}
				}
	    		System.out.println(Arrays.toString(move)+": " +opp_num + " | " + my_n + "       & " + opp_highest_number + " | "+ number_to_place);
	    	}
    	}
	}
    

}