package huginIntegration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Thinning {
	
	HashMap <String, HashSet<String>> graph = new HashMap<String, HashSet<String>>();
	HashMap <String, HashSet<String>> fillInEdges = new HashMap<String, HashSet<String>>();
	HashMap <String, HashSet<String>> ImportantEdges;// = new HashMap<String, HashSet<String>>();
	
	void displayGraphSegment(HashMap<String, HashSet<String>> gs)
	{
		int count = 0;
        for(String key: gs.keySet()){
       	 count += gs.get(key).size();
       	 System.out.println(key + " : "+ gs.get(key) + " ( " + gs.get(key).size() + " ) ");
        }
        System.out.println("Total: " + count + "\n\n");
	}
	
	public void getRedundantFillins(String filename1, String delimiter1, String filename2, String delimiter2) throws IOException{
		
		// extract contents from file 1 that contains graph with (new and old) fill in edges, delimiter here is ":", ","
		File file1 = new File(filename1);
		FileReader fileReader1 = new FileReader(file1);
		BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
		StringBuffer stringBuffer1 = new StringBuffer();
		String line1 = "";
        while ((line1 = bufferedReader1.readLine()) != null) {
            String[] names1 = line1.split(delimiter1);
            String[] listTargetVertices = names1[1].split(",");
            
            HashSet<String> temp = new HashSet<String>();
            for(String str : listTargetVertices){
            	temp.add(str);
            }
            graph.put(names1[0], temp);
        }
        
     // extract contents from file 2 that contains fill-in edges to be checked, delimiter here is "-"
 		File file2 = new File(filename2);
 		FileReader fileReader2 = new FileReader(file2);
 		BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
 		StringBuffer stringBuffer2 = new StringBuffer();
 		String line2 = "";
         while ((line2 = bufferedReader2.readLine()) != null) {
             String[] names2 = line2.split(delimiter2);
             if(fillInEdges.containsKey(names2[0])){
            	 fillInEdges.get(names2[0]).add(names2[1]);
             }
             else{
            	 HashSet<String> temp = new HashSet<String>();
            	 temp.add(names2[1]);
            	 fillInEdges.put(names2[0], temp);
             }
             
         }
         
         
         // construct a set of edges those are important that is not deletable initially it is the causal, 
         // moral (initial) and initial fill-in edges
         // which we can get by subtracting the whole graph by fillInEdges
         
         for(String src: fillInEdges.keySet()){
        	 for(String dest: fillInEdges.get(src)){
        		 graph.get(src).remove(dest);
        		 graph.get(dest).remove(src);
        	 }
         }
         
         displayGraphSegment(fillInEdges);
         
         displayGraphSegment(graph);
         ImportantEdges = graph;
         // now start checking for redundancy: this should be recursive thinning
         recusriveThinning();
         
         System.out.println("After Thinning ");
         displayGraphSegment(ImportantEdges);
	}
	
	
	public void getRedundantFillins(String bigGraphFile, String orgGraphFile, String delimiter) throws IOException{
		
		// extract contents from file 1 that contains graph with (new and old) fill in edges, delimiter here is ":", ","
		File file1 = new File(bigGraphFile);
		FileReader fileReader1 = new FileReader(file1);
		BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
//		StringBuffer stringBuffer1 = new StringBuffer();
		String line1 = "";
        while ((line1 = bufferedReader1.readLine()) != null) {
            String[] names1 = line1.split(delimiter);
            String[] listTargetVertices = names1[1].split(",");
            
            HashSet<String> temp = new HashSet<String>();
            for(String str : listTargetVertices){
            	temp.add(str);
            }
            graph.put(names1[0], temp);
        }
        
        ImportantEdges = new HashMap<String, HashSet<String>>();
        
		// extract contents from file 2 that contains graph with only old/initial edges, delimiter here is ":", ","
        File file2 = new File(orgGraphFile);
		FileReader fileReader2 = new FileReader(file2);
		BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
//		StringBuffer stringBuffer2 = new StringBuffer();
		String line2 = "";
        while ((line2 = bufferedReader2.readLine()) != null) {
            String[] names2 = line2.split(delimiter);
            String[] listTargetVertices = names2[1].split(",");
            
            HashSet<String> temp = new HashSet<String>();
            for(String str : listTargetVertices){
            	temp.add(str);
            }
            ImportantEdges.put(names2[0], temp);
        }
         
        System.out.println("The big graph ");
        displayGraphSegment(graph);
        
        System.out.println("Before Thinning, Original is ");
        displayGraphSegment(ImportantEdges);
         // construct a set of edges those are extra added by our proposed algorithm
         // which we can get by subtracting the whole big graph by original graph
         
         for(String key: graph.keySet()){
        	 HashSet<String> diff = setDifference(graph.get(key), ImportantEdges.get(key));
        	 
        	 fillInEdges.put(key, diff);
         }
         
         System.out.println("The fill in edges ");
         displayGraphSegment(fillInEdges);
         
         // now start checking for redundancy: this should be recursive thinning
         recusriveThinning();
         
         System.out.println("After Thinning, Original becomes ");
         displayGraphSegment(ImportantEdges);
	}
	
	
	// here we will start with any arbitrary edge, say A-B
	// if Neighbor(A) \cap Neighbor(B) = 
	// 									(1) {} : then A-B is important and shouldn't be deleted, keep it in important set
	//									(2) {a,b,c,d,e, ...} : then look for all of the edges possible by {a, b, c, d, e, ...} i.e. complete, 
	//												if any of them is missing in important set, then it is not redundant and important 
	//																then remove from fillIn and keep in important edge set
	//												if found complete then remove A-B
	//											
	public void recusriveThinning(){
		System.out.println("\n############## The execution of recursive thinning ###################\n");
		for(String src: fillInEdges.keySet()){
			HashSet<String> temp = new HashSet<String>();
			
			for(String key: fillInEdges.get(src))
					temp.add(key);
			if(temp != null)
			for(String tar : temp){
				
				HashSet<String> common = setIntersection(ImportantEdges.get(src), ImportantEdges.get(tar));
				
				// checking for completeness of the common nodes
				boolean isComplete = isComplete(common);
				
				System.out.print(src + " --- " + tar + " : " + ImportantEdges.get(src) + " ^ " + ImportantEdges.get(tar) + " = " + common + " is complete? " + isComplete);
				
				if(isComplete == false){
					// add A-B in important edges
					ImportantEdges.get(src).add(tar);
					ImportantEdges.get(tar).add(src);
					System.out.println(" [ Keep/Add this Fill-in. ]");
				}
				else System.out.println(" [ Skip/Delete this Fill-in. ]");
				// actually irrespective of complete/incomplete you have to delete A-B
				// removing B from A
				fillInEdges.get(src).remove(tar);
			}
		}
		System.out.println("\n############## End of the execution of recursive thinning ###################\n");
	}
	
	public boolean isComplete(HashSet<String> common)
	{
		if(common == null || common.size() == 0) return false;// for 0 vertex, it is not a graph hence not a complete graph
		if(common.size() == 1) return true;// for 1 vertex, it is always complete graph
		
		for(String keySrc: common){
			for(String keyTar: common){
				if(!ImportantEdges.get(keySrc).contains(keyTar)){
					return false;
				}
			}
		}
		return true; 
	}
	
	public HashSet<String> setDifference(HashSet<String> setA, HashSet<String> setB)
	{
		HashSet<String> temp = new HashSet<String>();
		
		for(String key: setA)
			temp.add(key);
		if(setB != null){
//			System.out.println("Set A : " + setA + " SetB : " + setB);
			temp.removeAll(setB);
		}
		
		return temp;
	}
	
	public HashSet<String> setIntersection(HashSet<String> setA, HashSet<String> setB)
	{
		HashSet<String> temp = new HashSet<String>();
		for(String key: setA){
			if(setB.contains(key)){
				temp.add(key);
			}
		}
		
		return temp;
	}
	
	public static void main(String[] args) throws IOException{
		Thinning thn = new Thinning();
//		thn.getRedundantFillins("graph.txt", "[:]", "edges.txt", "[-]");
		
		thn.getRedundantFillins("graph.txt", "graphOriginal.txt", "[:]");
	}

}
