
import java.util.*;

public class Plane {
    int x;
    int y;
    static char initialState;
    char state = ' ';
    
    /** constructor of Plane object, initialize plane with given x,y coordinates
     **/
    public Plane(int x, int y){
        this.x = x;
        this.y = y;
    }

    /** method that set initial state of the plane to given state
        @param state - initial state of every position
     **/
    public static void setInitialState(char state){
        initialState = state;
    }

    /** set state of current position to given state
        @param state
    **/
    public void setState(char state){
        this.state = state;
    }

    /** @return state of this position **/
    public char getState(){
        if(state == ' '){ //if state has not been set before, return initial state
            //System.out.print(" #$%@ ");
            return initialState;
        }
        return this.state;
    }

    /** @return x,y coordinates of this position**/
    public int[] getCoordinates(){
        return new int[]{x,y};
    }
    

}
