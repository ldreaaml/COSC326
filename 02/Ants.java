
import java.util.*;

public class Ants{
    static final int NORTH = 0;
    static final int EAST = 1;
    static final int SOUTH = 2;
    static final int WEST = 3;
    
    int num_dna = 0;
    int num_state = 0;
    int num_step = 0;
    int step_counter = 0;

    char previousDirection = 'N';
    char[] state;
    char[][] nextState;
    char[][] nextDirection;  
    int[] coordinates = {0,0};
    
    Plane currentPosition;
    Hashtable<String, Plane> hash_plane = new Hashtable<String, Plane>();

    
    /** constructor for Ants object. initialize DNA of the ants and initialize plane and starting position to 0,0
        @param dna - dna of ants, consist of direction and state
        @param step - number of step
     **/
    public Ants(ArrayList<String[]> dna, int step){
        num_dna = dna.size();
        num_step = step;
        state = new char[num_dna];
        nextState = new char[num_dna][4];
        nextDirection = new char[num_dna][4];
        
        int i = 0;
        for(String[] d: dna){ // initialize Ants DNA           
            state[i] = d[0].charAt(0);
            nextDirection[i] = d[1].toCharArray();            
            nextState[i] = d[2].toCharArray();
            i++;
        }
        
        currentPosition = new Plane(0,0); //Initialize new plane
        Plane.setInitialState(state[0]); // set intial state for every plane
        hash_plane.put(Arrays.toString(currentPosition.getCoordinates()), currentPosition); //add plane to hash table
        
    }


    /** calculate the final x,y coordinates of the ants after taking given number of steps **/
    public void calculateFinalPosition(){
        
        int step = 0;
        char[] next;
        char direction = 'N'; //starts with north direction
        
        while(step < num_step){ // while there are more steps to take
            
            // get state of current position
            char cur_state = currentPosition.getState();
            
            //calculate new state and direction based on current state
            next = newDirection(cur_state);
            
            //set state at current position to a new state
            currentPosition.setState(next[1]);
            
            //move to new tile
            int[] xy = nextPosition(next[0]); 
            Plane nextTile = hash_plane.get(Arrays.toString(xy));

            //if plane object at this coordinates doesnt exist in hash table, created new one
            if(nextTile == null){  
                hash_plane.put(Arrays.toString(xy), new Plane(xy[0],xy[1]));                
            }
            //update current position to new position
            currentPosition = hash_plane.get(Arrays.toString(xy));
            coordinates = xy; 
            previousDirection = next[0];            
            step++;        
        }
        
    }

    
    /** method that calculates next direction to take based on state at current position
        @param cur_state - state at current position
        @return new direction and new state for current position
    **/
    public char[] newDirection(char cur_state){
        int index = -1;
        for(int i =0; i<state.length; i++){
            if(state[i] == cur_state){
                index = i; 
            }
        }
        if(index == -1){
            System.out.println("State not found @newDirection");
        }
        
        int d = 0;
        if(previousDirection == 'N'){
            d = NORTH;           
        }else if(previousDirection == 'E'){
            d = EAST;
        }else if(previousDirection == 'W'){
            d = WEST;
        }else if(previousDirection == 'S'){
            d = SOUTH;
        }else{
            System.out.println("invalid direction @newDirection");
        }
        
        return new char[]{nextDirection[index][d],nextState[index][d]};
    }


    /**@param direction which the Ant will head to
       @return coordinates of next direction **/
    public int[] nextPosition(char direction){ 
        coordinates = currentPosition.getCoordinates();
        
        if(direction == 'N'){
            return new int[]{coordinates[0],coordinates[1]+1};
        }
        if(direction == 'S'){
            return new int[]{coordinates[0],coordinates[1]-1};
        }
        if(direction == 'E'){
            return new int[]{coordinates[0]+1,coordinates[1]};
        }
        if(direction == 'W'){
            return new int[]{coordinates[0]-1,coordinates[1]};
        }
        return coordinates;
    }
    

    /** print final coordinates **/
    public void finalPosition(){        
        System.out.println("# " + coordinates[0] + " " + coordinates[1]);
    }

    /** print Ants DNA **/
    public void print(){
        
        for(int i =0;i<num_dna;i++){
            System.out.println(state[i]+ " " + new String(nextDirection[i])+ " " + new String(nextState[i]));
        }
        System.out.println(num_step);
    }  

}
