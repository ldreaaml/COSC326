public class Threes{

    public static void main(String[] args){
        Threes t = new Threes();
        t.firstTask();
        System.out.println("");
        t.secondTask();
    }

    /**Find the first 70 sets ordered by increasing x, for x, y and z which satisfy the above*/
    public void firstTask(){
        /*
            rule is z < x < y  and  x,y,z do not share common factor
            
            square of odd number is always odd number, square of even number is always even number
            even +- even = even
            odd +- odd = even
            even +- odd = odd

            if x is EVEN number
            EVEN(x) = ODD(z) + ODD - EVEN(y) <- two even number , not possilbe
            EVEN(x) = EVEN(z) + ODD - ODD(y) <- two even number , not possilbe
            if x is ODD number
            ODD(x) = EVEN(z) + ODD - EVEN(y)  <- two even number , not possilbe

            ODD(x) = ODD(z) + ODD - ODD(y) <- therefore we can assume x,y,z will always be odd number
         */

        int complete_set = 0;
        int y;
        int x = 3;
        int z;
        while(complete_set<70){

            // z^3 = x^2 + x^2 -1 is the smallest possible number
            z = (int) Math.cbrt((Math.pow(x,2)*2)-1); 
            if(z%2 ==0){
                z++;
            }
            while(z<x){ //for all possible z
                //get y from known x,z
                y = (int) Math.sqrt((Math.pow(z,3) + 1 - Math.pow(x,2)));

                if(valid_xyz(x, y, z) && !shareCommonFactor(x, y, z)){
                    System.out.println((complete_set+1)+" "+x+" "+y+" "+z+" ");
                    complete_set++;
                    // break;
                }
                z+=2;
            }            
            x+=2; //x can only be odd number
        }
       
    }

    /** Find the first 70 sets ordered by increasing z, for x, y and z which satisfy the above. */
    public void secondTask(){
        /** same logic as first task - x,y,z cannot be even number*/
        int complete_set = 0;
        int y;
        int x;
        int z = 3;
        int max;

        while(complete_set<70){
            //largest number of x is x^2 = z^3 + 1 -> sqrt(z^3 - 1)
            max = (int) Math.sqrt(Math.pow(z,3)-1);
            //from possible x, find y and check if it make complete set
            x = 1;
            while(max>x && complete_set<70){
                y = (int) Math.sqrt((Math.pow(z,3) + 1 - Math.pow(x,2)));

                if(valid_xyz(x, y, z) && !shareCommonFactor(x, y, z)){
                    System.out.println((complete_set+1)+" "+x+" "+y+" "+z+" ");
                    complete_set++;
                }
                x+=2;
            }            
            z+=2;
        }
    }

    /** check if x,y,z share common factor */
    public boolean shareCommonFactor(int x,int y,int z){
        int count = 0;
        int i = 3;
        while(i<=x){            
            count = 0;
            if(x%i == 0 && y%i == 0){
                count++;
            }
            else if(x%i == 0 && z%i == 0){
                count++;
            }
            else if(y%i == 0 && z%i == 0){
                count++;
            }
            if(count > 0){ // if share common factor
                return true;
            }
            i += 2;
        }       
        return false;
    }

    /** check if z < x < y and check if it satisfy the equation x^2 + y^2 = z^3 + 1 */
    public boolean valid_xyz(int x, int y,int z){        
        if(z < x && x<y){
            if(Math.pow(x,2) + Math.pow(y,2)  == Math.pow(z,3)+1){
                return true;
            }
        }
        return false;
    }
}
