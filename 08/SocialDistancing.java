import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SocialDistancing {
	
	/** calculate minimum distance and optimal total distance**/
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		try{
			File f = new File("/home/cshome/p/pjitsawatpaiboon/326/08/input.txt");
			Scanner sc2 = new Scanner(f);
			sc =sc2;
		}catch(FileNotFoundException f){
			System.out.println("fnf");
		}
		
		boolean setGraph = false;
		Graph g = null;
		ArrayList<int[]> people = new ArrayList<int[]>();
		int row = 0;
		int column = 0;
		//process input
		while(sc.hasNextLine()) {
			
			String[] co = sc.nextLine().trim().split(" ");
			if(co.length == 2) {
			    int[] p = new int[] {Integer.parseInt(co[0]) ,Integer.parseInt(co[1])};
				if(g == null) { // new graph
					row = p[0];
					column = p[1];
					g = new Graph(p[0],p[1]);
					setGraph = true;
				}else {					
					people.add(p);
				}				
			}else { //is empty space
				if(setGraph) {
					findMinDistance(g,people,row,column);
					setGraph = false;
					people.clear();
					row = 0;
					column = 0;
					g = null;
				}
			}								
		}
		
		if(setGraph) { //last scenario
			findMinDistance(g,people,row,column);
			setGraph = false;
			people.clear();
			row = 0;
			column = 0;
			g = null;
		}
		
	}
	
	public static void findMinDistance(Graph g, ArrayList<int[]> people, int row, int column) {
		
		int min_distance = 0;
		if(people.size() != 0) {
			while(true) {
				for(int[] p: people) {
					// increase minimum distance and mark neighbour as invalid
					if(min_distance != 0) {
						ArrayList<int[]> edgesToRemove = neighbours(p[0],p[1],row,column,min_distance-1);
						for(int[] e: edgesToRemove) {			
							g.removeEdge(e);
						}					
					}
					
				}
				
				if(g.shortestPath(new int[] {0,0},new int[] {row-1,column-1}) != -1) { //find path
					min_distance++;
				}else {
					break;
				}
			}		
			min_distance = min_distance - 1;		
		}
		//after finding minimal distance, find optimal distance
		findOptimalDistance(g,people,row,column,min_distance);
	
	}
	
	public static void findOptimalDistance(Graph g, ArrayList<int[]> people, int row, int column, int min_distance) {
		int[] optimal_distance = new int[people.size()];
		Arrays.fill(optimal_distance, min_distance); 
		//find optimal distance for each person
		
		if(min_distance >=0) {
			for(int i = 0; i<optimal_distance.length;i++) {
				g.reset();
				int max = optimal_distance[i];
				
				do{
					max++;
					for(int p = 0; p<people.size();p++) {
						if(p != i) {
							ArrayList<int[]> edgesToRemove = neighbours(people.get(p)[0],people.get(p)[1],row,column,optimal_distance[p]-1); //-1?
							//System.out.println(Arrays.toString(people.get(p))+" : " +Arrays.deepToString(edgesToRemove.toArray()));
							for(int[] e: edgesToRemove) {			
								g.removeEdge(e);
							}
						}else {
							ArrayList<int[]> edgesToRemove = neighbours(people.get(p)[0],people.get(p)[1],row,column,max-1);
							for(int[] e: edgesToRemove) {			
								g.removeEdge(e);
							}
						}
					}
				}while(g.shortestPath(new int[] {0,0},new int[] {row-1,column-1}) != -1);
				
				optimal_distance[i] = max -1;
			}
			
		}
		//g.printGraphWithPath();
		int total =  0;
		for(int t:optimal_distance) {
			total += t;
		}
		System.out.println("min "+min_distance + ", total "+ total);
		
	}
	
	
	
	/** return Manhattan distance from first to second position **/
	public static int manhattanDistance(int x, int y, int x2,int y2){        
        return Math.abs(x-x2) + Math.abs(y-y2);
    }

	/** return position of neighbour cell **/
	public static ArrayList<int[]> neighbours(final int r, final int c, final int rows, final int cols, int length) {
    	final ArrayList<int[]> list = new ArrayList<int[]>();       
    	
    	for(int x = r-length; x<=r+length;x++) {
    		for(int y = c-length; y<=c+length;y++) {
    			if(x>=0 && x< rows && y>= 0 && y < cols && manhattanDistance(r,c,x,y) <= length) {
    				if(!(x == r && y==c)) {
    					list.add(new int[] {x,y});
    				}
    			}
    		}
    	}
    	if(length != -1) {
    		list.add(new int[] {r,c});    		
    	}
        return list;
    }
	

}
