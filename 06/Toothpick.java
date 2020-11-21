import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Toothpick{

    public static void main(String[] args){ 
        JFrame frame = new JFrame("toothpick");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        double screen_width = screenSize.getWidth();
        double screen_height = screenSize.getHeight();
        int gen = 0; //default setting
        double ratio = 1;
        for(int i = 0; i<args.length;i++) {
            if(i == 0) {
                gen = Integer.parseInt(args[0]);
            }else if(i ==1) {
                ratio = Double.parseDouble(args[1]);
                break;
            }
        }
        double l = findLength(gen,ratio,screen_height,screen_width);
        frame.getContentPane().add(new MyCanvas(screenSize.width, screenSize.height,gen,ratio,l));
        frame.setSize((int)screenSize.getWidth(), (int)screenSize.getHeight());
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
    }

    public static double findLength(int gen, double ratio,double screen_height,double screen_width){
        
        double initial_size = 1;
        double width = initial_size;
        double height = 0;
        int g = 0;
        double cur_size = initial_size;
        while(g < gen){
            cur_size *= ratio;
            if(g %2 ==0){ //height
                width += cur_size;
            }else{//width
                height += cur_size;
            }            
            g++;
        }
        if(screen_height /screen_width< width/height){
            initial_size *= (screen_height)/width;
        }else{
            initial_size *= (screen_height)/height;
        }
        return initial_size;
    }

}

class MyCanvas extends JComponent {
    private double max_width;
    private double max_height;    
    private double ratio;
    private int gen;
    private double line;

    public MyCanvas(double width, double height,int gen,double ratio,double length){
        this.max_height = height;
        this.max_width = width;
        this.gen = gen;
        this.ratio = ratio;
        this.line = length;
    }

    public void paint(Graphics g) {
        boolean horizontal = true;
        ArrayList<double[]> points = new ArrayList<double[]>(); //current generation
        ArrayList<double[]> next_gen = new ArrayList<double[]>();

        points.add(new double[]{mid(0,max_width),mid(0,max_height)});
        for(int i = 0-1; i< gen;i++){  
            for(double[] mid:points){ //each point to draw in current gen
                if(horizontal){ //draw horizontal line
                    double[] start = {mid[0]-(line/2), mid[1]}; //LEFT
                    double[] end = {mid[0]+(line/2), mid[1]};
                    g.drawLine((int)start[0],(int)start[1],(int)end[0],(int)end[1]);
                    next_gen.add(start);
                    next_gen.add(end);
                }else{ //draw horizontal line
                    double[] start = {mid[0], mid[1]-(line/2)}; //up
                    double[] end = {mid[0], mid[1]+(line/2)};
                    g.drawLine((int)start[0],(int)start[1],(int)end[0],(int)end[1]);
                    next_gen.add(start);
                    next_gen.add(end);
                }
            }
            horizontal = !horizontal;
            points.clear();
            points.addAll(next_gen);
            next_gen.clear();
            line = line*ratio;
            System.out.println(" ");
        }
    }

    public static double mid(double x,double y){
        return ((y-x)/2)+x;
    }
}