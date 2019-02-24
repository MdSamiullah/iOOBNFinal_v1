package triangulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

/******************************************************************************
 *  Compilation:  javac AllPaths.java
 *  Execution:    java AllPaths
 *  Depedencies:  Graph.java
 *  
 *  Find all paths from s to t.
 *  
 *  % java AllPaths
 *  A: B C 
 *  B: A F 
 *  C: A D F 
 *  D: C E F G 
 *  E: D G 
 *  F: B C D 
 *  G: D E 
 * 
 *  [A, B, F, C, D, E, G]
 *  [A, B, F, C, D, G]
 *  [A, B, F, D, E, G]
 *  [A, B, F, D, G]
 *  [A, C, D, E, G]
 *  [A, C, D, G]
 *  [A, C, F, D, E, G]
 *  [A, C, F, D, G]
 *
 *  [B, A, C, D, F]
 *  [B, A, C, F]
 *  [B, F]
 *
 *  Remarks
 *  --------
 *   -  Currently prints in reverse order due to stack toString()
 *
 ******************************************************************************/

public class AllPaths<Vertex> {

	class VERTEX{
		String v;
		int num;
		public VERTEX(String v, int num) {
			this.v = v;
			this.num = num;
		}
		public String toString() {
			return this.v + "(" + this.num + ")";
		}
	}
	
//    private Stack<String> path  = new Stack<String>();   // the current path
	private Stack<VERTEX> path  = new Stack<VERTEX>();   // the current path
    private SET<String> onPath  = new SET<String>();     // the set of vertices on the path
    private ArrayList<ArrayList<VERTEX>> allPaths = new ArrayList<ArrayList<VERTEX>>();
    

    public AllPaths(Graph G, String s, String t) {
        enumerate(G, s, t);
    }
    
    public AllPaths(Graph G, boolean realAdd) {
        findAllFillInEdges(G, realAdd);
    }

    public void convertStackToList(Stack<VERTEX> path) {
    		ArrayList<VERTEX> pathList = new ArrayList<VERTEX>();
    		
    		for(VERTEX V: path) {
    			pathList.add(V);
    		}
    		
    		allPaths.add(pathList);
    }
    
    // use DFS
    private void enumerate(Graph G, String v, String t) {

        // add node v to current path from s
    	
    		VERTEX V = new VERTEX(v, G.po.get(v));
    		
        path.push(V);
        onPath.add(v);

        // found path from s to t - currently prints in reverse order because of stack
        if (v.equals(t)) { 
        		//System.out.println(path);
        		convertStackToList(path);
        }

        // consider all neighbors that would continue path with repeating a node
        else {
            for (String w : G.adjacentTo(v)) {
                if (!onPath.contains(w)) enumerate(G, w, t);
            }
        }

        // done exploring from v, so remove from path
        path.pop();
        onPath.delete(v);
    }
    
    public boolean fillingEdgeRequired(int largestEndVertexOrder) {
    		boolean found = false;
    		for(ArrayList<VERTEX> path: this.allPaths) {
    			found = true;
    			for(int i = 1; i < path.size()-1; i++) {
    				if(path.get(i).num < largestEndVertexOrder) {
    					found = false;
    					break;
    				}
    			}
    			if(found) return found;
    		}
    		
    		return found;
    }
    
    public void findAllFillInEdges(Graph G, boolean realAdd) {
    	
    		Iterable<String> vertices = G.vertices();
    		ArrayList<String> target = new ArrayList<>();
    		vertices.forEach(target::add);
    		
    		for(int I = 0; I < target.size()-1; I++) {
    			for(int J = I+1; J < target.size(); J++) {
    				if(!G.hasEdge(target.get(I), target.get(J))) {
	    				AllPaths aP = new AllPaths(G, target.get(I), target.get(J));
	    		        int bigOrder = G.po.get(target.get(I)) > G.po.get(target.get(J)) ? G.po.get(target.get(I)) : G.po.get(target.get(J));
	    		        if(aP.fillingEdgeRequired(bigOrder) == true) {
	    		        		System.out.println("Add a fill-in between: " + target.get(I) + " -- " +  target.get(J));
	    		        		if(realAdd)	G.addEdge(target.get(I), target.get(J));;
	    		        }
    				}
    			}
    		}
    	
    }
    
    public String toString() {
    		StringBuilder sB = new StringBuilder();
    		
    		for(ArrayList<VERTEX> path: this.allPaths) {
    			sB.append(path + "\n");
    		}
    		
    		return sB.toString();
    }

    public static void main(String[] args) throws IOException {
//        Graph G = new Graph();
//        G.addEdge("A", "B");
//        G.addEdge("A", "C");
//        G.addEdge("C", "D");
//        G.addEdge("D", "E");
//        G.addEdge("C", "F");
//        G.addEdge("B", "F");
//        G.addEdge("F", "D");
//        G.addEdge("D", "G");
//        G.addEdge("E", "G");
//        
//        System.out.println(G);
    	
    	
  	     Graph G = new Graph("testPerfectOrdering.txt", "[ :=]+");
//    	Graph G = new Graph("test2.txt", "[ :=]+");
		
  		 System.out.println(G);    	
        
//        AllPaths aP = new AllPaths(G, "S", "V");
////        
//        int bigOrder = G.po.get("S") > G.po.get("V") ? G.po.get("S") : G.po.get("V"); 
//        
//        System.out.println(aP);
//        System.out.println(aP.fillingEdgeRequired(bigOrder));
    		
  		AllPaths aP = new AllPaths(G, false);
  		
//  		PerferctOrdering PO = new PerferctOrdering(G);
  		PerferctOrdering PO = new PerferctOrdering(G, "E");
  		PO.performPerfectOrdering(G);
  		
  		
  		
  		System.out.println("Filling edges after new PO" + PO.po);
  		
  		G = new Graph(G, PO.po);
  		System.out.println(G);
  		AllPaths aP2 = new AllPaths(G, true);
  		System.out.println(G);
    }

}