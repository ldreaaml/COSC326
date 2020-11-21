import java.util.*;

public class Graph{
	
    public ArrayList<int[]>[] edges;
    public LinkedList<Integer> path = new LinkedList<Integer>();
    public int row;
    public int column;
    public int size;
    
	public Graph(int row, int column) {
    	//add all vertices then link edges
    	this.row = row;
    	this.column = column;
    	this.size = row*column;
    	edges = new ArrayList[row*column];
    	for(int i =0;i<edges.length;i++) {
    		edges[i] = new ArrayList<int[]>();
    	}
    	    	
    	 for(int r = 0; r<row;r++){
             for(int c= 0; c< column;c++){
                 ArrayList<int[]> neighbours = neighbours(r, c, row, column);
                 int[] cur = {r,c};
                 for(int[] n: neighbours){ //add edges
                     addEdge(cur,n);
                 }
             }
         }
    }
    
    /** find shortest and return total distance from source to destination**/
    public int shortestPath(int[] source, int[] destination) {
    	
    	int[] pred = new int[size];
    	int[] distance = new int[size];
    	//if path does not exist
    	if(BFS(source,destination,pred,distance) == false) {
    		return -1;
    	}
    	//path exist
    	int crawl = index(destination);
    	LinkedList<Integer> path = new LinkedList<Integer>();
    	path.add(crawl);

    	while(pred[crawl] != -1) {
    		path.add(pred[crawl]);
    		crawl = pred[crawl];
    	}
    	this.path = path;   
    	//printGraphWithPath();
    	return distance[index(destination)];
    }
    
  
    /** BFS find shortest path from source to destination and check if path exist **/
    public boolean BFS(int[] source, int[]destination, int[] pred, int[] distance) {
    	
    	LinkedList<Integer> queue = new LinkedList<Integer>();
    	boolean[] visited = new boolean[size];
    	for(int i = 0;i<size;i++) {
    		visited[i] = false;
    		distance[i] = -1;
    		pred[i] = -1;
    	}
    	
    	int s = index(source);
    	visited[s] = true;
    	distance[s] = 0;
    	queue.add(s);
    	
    	while(!queue.isEmpty()) {
    		int u = queue.remove();
    		for(int i =0; i<edges[u].size(); i++) {
    			int v = index(edges[u].get(i));
    			if(visited[v] == false) {
    				visited[v] = true;
    				distance[v] = distance[u] + 1;
    				pred[v] = u;
    				queue.add(v);
    				if(v == index(destination)) {
    					return true;
    				}
    			}
    		}
    	}
    	return false;
    }
      
    /**turn coordinate to index**/
    public int index(int[] v) {
    	return (v[0]*column) + v[1];
    }
    /** turn index to coordinate**/
    public int[] point(int index) {
    	if(index == 0) {
    		return new int[] {0,0};
    	}

    	int r = index/column;
    	int c = r == 0? index: index % (column);
    	return new int[] {r,c};
    }
    
    /** add edges **/
    public void addEdge(int[] v1,int[] v2){    	
    	edges[index(v1)].add(v2);
    }
    
    /** remove edge **/
    public void removeEdge(int[] v) {
    	if(v[0]>row || v[1]>column) {
    		return;
    	}
    	ArrayList<int[]> neighbours = neighbours(v[0], v[1], row, column);
    	for(int[] n: neighbours) {
    		int d = index(n);
    		for(int i = 0; i<edges[d].size();i++) {
    			if(Arrays.equals(v, edges[d].get(i))) {
    				edges[d].remove(i);
    			}
    		}
    	}
    	edges[index(v)].clear();
    }
    
    
    /** return neighbours of given position **/
    public ArrayList<int[]> neighbours(final int r, final int c, final int rows, final int cols) {
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
    
    /** reset graph to initial state (all edges are linked) **/
    public void reset() {
		for(int r = 0; r<row;r++){
            for(int c= 0; c< column;c++){
                ArrayList<int[]> neighbours = neighbours(r, c, row, column);
                int[] cur = {r,c};
                for(int[] n: neighbours){ //add edges
                    addEdge(cur,n);
                }
            }
        }
	}
   
    /**print all vertices and edges**/
    public String toString(boolean both) 
    { 
        StringBuilder builder = new StringBuilder(); 
        builder.append("\n");
        for(int r = 0; r<row;r++){
            for(int c= 0; c< column;c++){ 
            	builder.append(Arrays.toString(new int[] {r,c}) + " ");
            }
            builder.append("\n"); 
       }
        builder.append("\n");
        if(both) {
        int i = 0;
	         for(int r = 0; r<row;r++){
	             for(int c= 0; c< column;c++){       
	            	 int[] v = new int[] {r,c};
	            	 builder.append(Arrays.toString(v)+" "+ edges[index(v)].size()+ " : " + Arrays.deepToString(edges[i].toArray()) +"\n");
	            	 i++;
	             }
	         }
        }
        return (builder.toString()); 
    } 
    
    public void printGraph() {
    	System.out.println("\n");
    	 for(int r = 0; r<row;r++){
             for(int c= 0; c< column;c++){
            	 if(edges[index(new int[] {r,c})].size() > 0) {
            		 System.out.print("0 ");
            	 }else {
            		 System.out.print("1 ");
            	 }
             }
             System.out.println("");
    	 }
    	 System.out.println("");
    }
    
    public void printGraphWithPath() {
    	if(path == null) {
    		return;
    	}
    	System.out.println("");
    	 for(int r = 0; r<row;r++){
             for(int c= 0; c< column;c++){
            	 boolean isPath = false;
            	 for(int p: path) {
            		 if(r == point(p)[0] && c==point(p)[1]) {
            			 System.out.print("+ ");
            			 isPath = true;
            			 break;
            		 }
            	 }
            	 if(!isPath) {
            		 if(edges[index(new int[] {r,c})].size() > 0) {
                		 System.out.print("0 ");
                	 }else {
                		 System.out.print("1 ");
                	 }
            	 }            	 
             }
             System.out.println("");
    	 }
    	 System.out.println("");
    }

}


