import java.util.*;
import java.util.regex.*;

public class WhereInTheWorld{

    boolean debug = false;
    
    public static void main(String[] args){
        WhereInTheWorld w = new WhereInTheWorld();
        Scanner scan = new Scanner(System.in);

        while(scan.hasNextLine()){
            String output = w.get_latitude_longitude_in_standard_form(scan.nextLine());
            System.out.println(output);
        }
    }
    
    /** method that verify and converts input into latitude and longitude in standard form
     * @param input - input in standard form, Degrees Minute Second form, or etc.
     * @return latitude and longtitude in standard form, or error message if input is not valid
     * **/
    public String get_latitude_longitude_in_standard_form(String input){
        // System.out.println(input);
        String input_copy = input.toUpperCase().trim();
        String standard_form = "";
        String latitude = "";
        String longitude = "";
        boolean isDMSForm = false;
        boolean latitudeFirst = false;

        /* input is in degree minute second format */
        if(input_copy.matches("(.*)[NS(NORTH)(SOUTH)](.*)[EW(EAST)(WEST)]")){
            isDMSForm = true;
            latitudeFirst = true;
        }else if(input_copy.matches("(.*)[EW(EAST)(WEST)](.*)[NS(NORTH)(SOUTH)]")){
            isDMSForm = true;
            latitudeFirst = false;
        }
        
        /* Degree Minute Second Format, nees to convert to standard form */
        if(isDMSForm){
            ArrayList<String> num = new ArrayList<String>();
            ArrayList<String> sign = new ArrayList<String>();  
            ArrayList<String> direction = new ArrayList<String>();    
            boolean prev_is_num = false;
            boolean hasMarker = false;
            String set_sign = "D";
            int i = 0;

            input_copy = input_copy.replaceAll("°", " D ");
            input_copy = input_copy.replaceAll("′", " M ");
            input_copy = input_copy.replaceAll("″", " S ");
            if(input_copy.contains("D") || input_copy.contains("M") || input_copy.contains("S\\s")  ){
                hasMarker = true;
            }
            Scanner sc = new Scanner(input_copy).useDelimiter("[\\,\\s]+");            
            while(sc.hasNext()){
                String s = sc.next().trim();                
                boolean isNum = isNumber(s, isDMSForm);
                if(!hasMarker){
                    if(num.size()<6 && isNum){
                        num.add(s);
                        sign.add(set_sign);
                        set_sign = set_sign == "D"? "M" : "S";
                    }else if(s.matches("[NSEW]|(NORTH)|(EAST)|(WEST)|(SOUTH)")){
                        if(i == 0){
                            i = num.size();
                            set_sign = "D";
                        }
                        direction.add(s);
                    }else{
                        return "Unable to process : " + input;
                    }
                }else{ //with direction indicator
                    if(num.size() < 6 && !prev_is_num && isNum){
                        num.add(s);
                        prev_is_num = !prev_is_num;
                    }
                    else if(sign.size() == num.size()-1 && prev_is_num && s.matches("[DMS]")){
                        sign.add(s);
                        prev_is_num = !prev_is_num;
                    }else if(direction.size() < 2 && !prev_is_num && s.matches("[NSEW]|(NORTH)|(EAST)|(WEST)|(SOUTH)")){
                        if(i == 0) i = num.size();
                        direction.add(s); //complete 1 set
                    }else{
                        return "Unable to process : " + input;
                    }
                }
            }
            if(direction.size() != 2){
                return "Unable to process : " + input;
            }
            
            String[][] first_item = { num.subList(0, i).toArray(new String[0]),
                sign.subList(0, i).toArray(new String[0]) };
                
            String[][] second_item = { num.subList(i, num.size()).toArray(new String[0]),
                sign.subList(i,num.size()).toArray(new String[0]) };
                    
            /*convert from DMS to standard form*/
            if(direction.get(0).matches("(NORTH)|(SOUTH)|[NS]") && direction.get(1).matches("[(WEST)(EAST)WE]")){
                latitude = DMS_to_Decimal(first_item[0], first_item[1],direction.get(0),true);
                longitude = DMS_to_Decimal(second_item[0], second_item[1],direction.get(1),false);
            }else{
                latitude = DMS_to_Decimal(second_item[0], second_item[1],direction.get(1),true);
                longitude = DMS_to_Decimal(first_item[0], first_item[1],direction.get(0),false);
            }
            if(debug){
                System.out.println(Arrays.toString(num.toArray()));
                System.out.println(Arrays.toString(sign.toArray()));
                System.out.println(Arrays.toString(direction.toArray()) + " "  );                        
                System.out.println(latitudeFirst?"Latitude first" : "longitude first"); 
                System.out.println(Arrays.deepToString(first_item));
                System.out.println(Arrays.deepToString(second_item));
            }
            
            if(latitude != "" && longitude != ""){
                standard_form = get_standard_form(latitude,longitude);
                if(standard_form != ""){
                    return standard_form;
                }
            }        
        }

        /* ============================ Standard form ============================*/
        else{
            /* split latitude and longitude */
            ArrayList<String> str = new ArrayList<String>();     
            Scanner scan = new Scanner(input_copy).useDelimiter("[\\s\\,]+");
            while(scan.hasNext()){
                String s = scan.next().trim();
                if(!s.isEmpty()){
                    str.add(s);
                }
            }       
            
            if(str.size() != 2){ //if cannot split into latitude and longitude
                if(debug){
                    System.out.println(str.size() + " : " +Arrays.toString(str.toArray()));
                    System.out.println(Arrays.toString(str.toArray()));
                    System.out.println("DMS = " + isDMSForm + "/" + input_copy);
                    System.out.println("Failed to split latitude and longitude :"+ Arrays.toString(str.toArray()));
                }
                return "Unable to process : " + input;
            }
            for(String s: str){ //check if it's a number
                if(isNumber(s,isDMSForm) == false){                    
                    return "Unable to process : " + input; 
                }
            }
            standard_form = get_standard_form(str.get(0),str.get(1));
            if(standard_form != ""){
                return standard_form;
            }        
        }
        return "Unable to process : " + input;     
    }

    /** convert input in degree minute second format to a decimal format.
     * @return deecimal format of given input
     * **/
    public String DMS_to_Decimal(String[] dms, String[] sign, String direction, boolean isLatitude){
        int d = 0, m = 0, s = 0;
        double degree = 0, minute =0, second = 0;
        for(int i =0; i<sign.length; i++){
            if(d==0 && sign[i].matches("[D°]")){
                degree = Double.parseDouble(dms[i]);
                if(isLatitude && (degree < 0 || 90 < degree) ){
                    if(debug) System.out.println("degree not in range 0-90");
                    return "";
                }
                d++;
            }
            else if(m==0 && sign[i].matches("[M′]")){
                minute = Double.parseDouble(dms[i]);
                if(minute <0 || minute > 60){
                    if(debug) System.out.println("minute not in range 0-60");
                    return "";
                }
                m++;
            }
            else if(s==0 && sign[i].matches("[S″]")) {
                second = Double.parseDouble(dms[i]);
                if(second <0 || second > 60){
                    if(debug) System.out.println("second not in range 0-60");
                    return "";
                }
                s++;
            }else{
                // System.out.println("wrong sign " + sign[i]);
                return ""; 
            }
        }
        if((m == 1 && d==0) || (s==1 && (d==0||m==0))){
            if(debug) System.out.println("incomplete dms");
            return "";
        }
        double standard_form = 0;
        standard_form  = degree + (minute/60) + (second/3600);
        if(direction.matches("S|SOUTH|W|WEST")){
            standard_form *= -1;
        }
        return String.format("%.6f",standard_form);
    }
      

    /** method to convert input into a standard form and check if the given latitude and longitude is valid
     * @param latitude - cooridinates that represents latitude
     * @param longitude - cooridinates that represents longitude
     * @return standard form of given input
     * **/
    public String get_standard_form(String latitude, String longitude){
        String standard_form = "";
        double d_lat = Double.parseDouble(latitude);
        double d_long = Double.parseDouble(longitude);
                
        if(d_long < -180 || d_long > 180){ //wrap around
            int i = d_long>0?1:-1;
            d_long = Math.abs(d_long); 
            double n = 360;
            while(n<d_long){ 
                n += 360;
            }
            n = d_long-n;  
            if(n < -180){
                n = 360 + n;
            }
            if(n != 0){
                n *= i;
            }
            d_long = n;
        }  
        if(debug && (d_long < -180 || d_long > 180)){
            System.out.println("fail to conver longitude : " + d_long);
        }        
        if(d_lat >= -90 && d_lat <= 90   &&   d_long >= -180 && d_long <= 180){
            standard_form += String.format("%.6f",d_lat) + ", " + String.format("%.6f",d_long);
            return standard_form;
        }else{
            if(debug && (d_lat < -90 || d_lat >90)){
                System.out.println("Latitude must be in range -90 and 90");
            }
            return "";
        }
    }
    
    /** method to check if input is a number
     * @param input - number to check
     * @return true if input is a number, otherwise false
     * **/
    public boolean isNumber(String input, boolean dms){
        try{            
            double d = Double.parseDouble(input);
            if(dms && d<0){
                if(debug){
                    System.out.println("Negative number/ num > 60 is not allowed for DMS format");
                }
                return false;
            }
        }catch(NumberFormatException nfe){
            if(debug){
                // System.out.println("input is not a number [" + input+"]");  
            }            
            return false;
        }
        return true;
    }
}