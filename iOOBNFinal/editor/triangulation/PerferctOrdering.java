package triangulation;

import java.util.ArrayList;
import java.util.HashMap;

public class PerferctOrdering {
	public HashMap<String, Integer> po;
	public HashMap<String, Integer> cardinality;
	public String startNode;
	public ArrayList<String> vertices;
	public int poCount;
	
	public PerferctOrdering(Graph G){
		po = new HashMap<String, Integer>();
		cardinality = new HashMap<String, Integer>();
		vertices = new ArrayList<>();
		
		G.vertices().forEach(vertices::add);
		startNode = vertices.get(0);
		
		for(String str: vertices){
			po.put(str, 0); // this is also used to check if any node has been already numbered and avoid cardinality increasing
			cardinality.put(str,0);
		}
		
		poCount = 1;
		assignPerfectOrder(G, startNode, poCount);
	}
	
	public PerferctOrdering(Graph G, String startVertex){
		po = new HashMap<String, Integer>();
		cardinality = new HashMap<String, Integer>();
		vertices = new ArrayList<>();
		
		G.vertices().forEach(vertices::add);
		startNode = startVertex;
		
		for(String str: vertices){
			po.put(str, 0);
			cardinality.put(str,0);
		}
		
		poCount = 1;
		assignPerfectOrder(G, startNode, poCount);
	}
	
	public void assignPerfectOrder(Graph G, String vertex, int number){
		System.out.println(" (vertex, number) : (" + vertex + "," + number + ")");
		this.po.put(vertex, number);
		this.poCount = number+1;
		this.updateCardinality(G, vertex);
	}
	
	public void updateCardinality(Graph G, String vertex){
		for (String w : G.adjacentTo(vertex)) {
			if(this.po.get(w) == 0)
				this.cardinality.put(w, this.cardinality.get(w) + 1);
        }
	}
	
	public String findNextNode(){
		String next = "";
		int max = 0;
			for(String nV: vertices){
				if(this.po.get(nV) == 0){
					if(this.cardinality.get(nV) > max){
						max = this.cardinality.get(nV);
						next = nV;
					}
				}
			}
		return next;
	}
	
	public void performPerfectOrdering(Graph G){
		String vertex = "";
		while(this.vertices.size() > (this.poCount-1)){
			vertex = this.findNextNode();
			assignPerfectOrder(G, vertex, this.poCount);
		}
	}
}
