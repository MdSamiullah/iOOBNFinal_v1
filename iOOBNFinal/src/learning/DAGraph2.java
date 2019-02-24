package learning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import learning.HierarchyTree2.HierarchyNode2;

public class DAGraph2 
{	
	public static HashMap<HashSet<String>, String> labelSetToNotationMap = new HashMap<HashSet<String>, String>();
	public static HashMap<String, String> DAGFileNamesTolabelSetMap = new HashMap<String, String>();// file name to label map
	public static HashMap<String, String> labelSetToDAGFileNamesMap = new HashMap<String, String>();// file name to label map
	
	public static int maxNotationLen = 0;
	public static int LabelSize = 0;
	public static int DagLabelSize = 0;
	public static int maxFileNameSize = 0;
	
	public class Node2
	{
		String name;
		HashSet<String> label;
		HashMap<String, HashSet<String>> neighbors;// neighbors HashMap contains a set of neighbor node in keyset and their corresponding graph label in the mapped value (Set)
		
		/*
		 * The 1st hashmap is the neighbor node name
		 * the inner hashset is the graphLabel of the edge
		*/ 
		
		public Node2(){
			this.name = "";
			this.label = new HashSet<String>();
			this.neighbors = new HashMap<String, HashSet<String>>(); 
			
		}
		public Node2(String name, HashSet<String> label, HashMap<String, HashSet<String>> neighbors)
		{
			this.name = name;
			this.label = label;
			this.neighbors = neighbors;
		}
		
		public String toString()
		{
			String str = "";
			
			str += this.name + " <" + this.label + "> : [" + this.neighbors + "]";
			
			return str;
		}
	}
	
	HashMap<String, Node2> daGraph;
	HashSet<String> neighborSet;
	
	public DAGraph2()
	{
		this.daGraph = new HashMap<String, Node2>(); 
		this.neighborSet = new HashSet<String>();
		/*
		 * in construct graph method, and any method which adds a neighbor (e.g. add another graph to construct super-graph)
		 * this neighborSet should be updated accordingly
		*/ 
	}
	
	public DAGraph2(DAGraph2 g)
	{
		this.daGraph = g.daGraph; 
		this.neighborSet = g.neighborSet;
		/*
		 * in construct graph method, and any method which adds a neighbor (e.g. add another graph to construct super-graph)
		 * this neighborSet should be updated accordingly
		*/ 
	}
	
	/*
	 * roots = those nodes having no incoming edges
	 *       = having 0 in-degrees
	 *       = nodes not in neighbor list
	 *       = all nodes - nodes in neighbor list
	 * */
	public HashSet<String> findRoots()
	{
		HashSet<String> roots = new HashSet<String>();
		roots.addAll(this.daGraph.keySet());
		
		roots.removeAll(this.neighborSet);
		
		return roots;
	}
	
	public void constructGraph(String fileName, String graphLabel) throws IOException
	{
		/*
		 * assuming graph is to be constructed from the file where the filename given as parameter
		 * graph input format:
		 * 
		 * vertex : <Neighbor vertex>
		 * 
		 *  X : A B
		 *  A : B
		 *  B : 
		 * 
		 * */ 
		
		File file = new File(fileName);

        BufferedReader buff = new BufferedReader(new FileReader(file));
		
        String readLine = "";
        
        while ((readLine = buff.readLine()) != null) {
        	String nodes [] = readLine.split(":");
        	String src = nodes[0].trim();
//        	System.out.println(readLine);
        	String destinations [] = nodes[1].split(" ");
        	
        	DAGraph2.Node2 tempNode = this.new Node2();
        	tempNode.name = src;
        	tempNode.label.add(graphLabel); // for all the nodes in same graph, label will be same
        	
        	for(String s: destinations){
        		if(!s.equalsIgnoreCase("")){
        			/*
        			 * Be very carefull about next 2 lines. If you put them outside of (i.e. before) the loop
        			 * then only one instance but several references to that single instance of set 
        			 * will be created which will affect the neighbor information. Like if you change one of them
        			 * all will be same  
        			 * */
        			HashSet<String> setLabel = new HashSet<String>();
                	setLabel.add(graphLabel);// for all the neighbors of a node in same graph, label will be same
        			tempNode.neighbors.put(s, setLabel);
        			this.neighborSet.add(s);
        		}
        	}
        	
        	this.daGraph.put(tempNode.name, tempNode);
        }
	}
	
	public void addGraph(DAGraph2 g)
	{
		/*
		 * Just for further graph BFSVisit, we need a list of roots and for that we need neighborSet
		 * neighbor set is the summation of the neighbor sets of both graphs
		 * */
		this.neighborSet.addAll(g.neighborSet);
		
		// node adding ... 
		/*
		 * for each node in the graph g
		 * 			if the node n is in the current graph, just add n's labels and neighbor information to the node of current graph
		 * 			else add the whole node along with it's label and neighbor information to the graph   
		 * */
		for(String nodeToAddKey : g.daGraph.keySet()){
			Node2 nodeToAdd = g.daGraph.get(nodeToAddKey);
			Node2 currNode = this.daGraph.get(nodeToAddKey);
			if(currNode != null){// if the node is already in there, add only the labels of the node then in the DAG ... and we also need to add labels to the edges later
				currNode.label.addAll(nodeToAdd.label);
				// edge label adding ...
				/*
				 * for each neighbor "neigh" of node n, 
				 * 			if it is in curNode neighbor list, then add the graph labels of the neigh node to the node in neighbor list
				 * 			else add the "neigh" in neighbor list along with all the labels 
				 * */  
				for(String childNodeForEdgesKey : nodeToAdd.neighbors.keySet()){// the edges that is connected with nodeToAdd
//					System.out.println("\n\nAdded edge <" + childNodeForEdgesKey + "> to <" + nodeToAddKey + " > : " + nodeToAdd.neighbors.get(childNodeForEdgesKey) + "\n");
//					System.out.println("Neighbors before Adding " + currNode.neighbors + "\n\n");
					if(currNode.neighbors.containsKey(childNodeForEdgesKey)){
						currNode.neighbors.get(childNodeForEdgesKey).addAll(nodeToAdd.neighbors.get(childNodeForEdgesKey));
					}
					else{
						currNode.neighbors.put(childNodeForEdgesKey, nodeToAdd.neighbors.get(childNodeForEdgesKey));
					}
//					System.out.println("Neighbors after Adding " + currNode.neighbors + "\n\n");
				}
			}
			else{// if the node is absent, add the node in the DAG ... and we also need to add the edges later
				this.daGraph.put(nodeToAddKey, nodeToAdd);
			}
		}		
	}
	
	// RootNodes are the nodes those have no incoming edges and will be visited in level 1 of the BFSVisit
	public void BFSVisit(HashSet<String> RootNodes)
	{
		Queue<String> NodeQ = new LinkedList<String>();
		
		/*
		 * if not found a node name = not discovered
		 * if not found a node name with value 0 = discovered but not explored
		 * if not found a node name with value 1 = explored
		 * */ 
		HashMap<String, Integer> color = new HashMap<String, Integer>();
		
		for(String root: RootNodes){
			NodeQ.add(this.daGraph.get(root).name);
			color.put(root, 0);
		}
		
		while(!NodeQ.isEmpty()){
			String curNode = NodeQ.remove();
			color.put(curNode, 1);
//			System.out.println("current node " + curNode + " " + this.daGraph.get(curNode));
			
			HashMap<String, HashSet<String>> neighbors = this.daGraph.get(curNode).neighbors;
			
			for(String nextNode: neighbors.keySet()){
				if(!color.containsKey(nextNode)){
					NodeQ.add(nextNode);
					color.put(nextNode, 0);
				}
			}
		}
		
	}
	
	public HashSet<HashSet<String>> getLabelSet()
	{ 
		HashSet<HashSet<String>> labelSet = new HashSet<HashSet<String>>();
		
		for(String n : this.daGraph.keySet()){
			labelSet.add(this.daGraph.get(n).label);
			for(String e: this.daGraph.get(n).neighbors.keySet()){
				labelSet.add(this.daGraph.get(n).neighbors.get(e));
			}
		}
		
		return labelSet;
	}
	
	public ArrayList<String> fileListFromDir(String path)
	{
		ArrayList<String> fileList = new ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String fileName = path + listOfFiles[i].getName();
				fileList.add(fileName);
			} 
			else if (listOfFiles[i].isDirectory()) {

			}
		}
		
//		System.out.println(fileList);
		return fileList;
	}
	
	// upperLowerCase = 65 for Upper, 97 for lower 
	public String generateString(int i, int upperLowerCase) {
	    return i < 0 ? "" : generateString((i / 26) - 1, upperLowerCase) + (char)(upperLowerCase + i % 26);
	}
	
	public void performMappingSetLabel(HashSet<HashSet<String>> labels) {
		int LS = (int)(Math.log(labels.size()) / Math.log(26)) + 1;
		
		int setNotation = 0; // 0 for 'A', 26 for AA
		String notation = "";
		for(HashSet<String> setLab: labels){
			notation = generateString(setNotation++, 65);
			for(int I = 0; I < LS - notation.length(); I++)
				notation = "0" + notation;// just for padding 0 with the notation
			
			labelSetToNotationMap.put(setLab, notation);
		}
		LabelSize = LS;
	}
	
	public String toString()
	{
		String str = "";
		
		for (String key: this.daGraph.keySet()){
			str += this.daGraph.get(key)+"\n";
		}
			
		return str;
	}
	
	public String removeDirectoryNamePrefix(String fileName){
		String strArr [] = fileName.split("\\\\");
		
		return strArr[strArr.length-1];
	}
	
	public int getAndConstructDAGs(ArrayList<DAGraph2> dags, int numOfDAGs, ArrayList<String> dagFiles) throws IOException {
		int countNumOfDAG = 0;
		for(int I = 0; I < numOfDAGs; I++){
			if(dagFiles.get(I).endsWith(".txt")){
				String graphLabel = generateString(countNumOfDAG, 97);// 97 for lower case
				DAGraph2 tempDAG = new DAGraph2();
				int lenGL = graphLabel.length();
				for(int K = 0; K < DagLabelSize - lenGL; K++)	graphLabel = "0" + graphLabel;
				// prefix of a file name i.e. directory info is removed by the function "removeDirectoryNamePrefix", .txt is removed at the end
				DAGFileNamesTolabelSetMap.put(removeDirectoryNamePrefix(dagFiles.get(I)).replace(".txt", ""), graphLabel);
				labelSetToDAGFileNamesMap.put(graphLabel, removeDirectoryNamePrefix(dagFiles.get(I)).replace(".txt", ""));
				tempDAG.constructGraph(dagFiles.get(I), graphLabel);
				
				if(maxFileNameSize < dagFiles.get(I).length()) {
					maxFileNameSize = dagFiles.get(I).length();
//					System.out.append(dagFiles.get(I));
				}
				
				tempDAG.BFSVisit(tempDAG.findRoots());
				dags.add(tempDAG);
				countNumOfDAG++;
			}
		}
		return countNumOfDAG;
	}
	
	public HashMap<String, String> sortHashMap(HashMap<String, String> sorted, HashMap<String, String> labelSetToNotationMap) {
		sorted = 
				labelSetToNotationMap.entrySet().stream()
			    .sorted(Entry.comparingByValue())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));
		
		return sorted;
	}
	
	public void printFileLegends(String directory) {
		int col1Width = DAGraph2.DagLabelSize;
		int col2Width = maxFileNameSize - directory.length();
		String headerCol1 = "DAG Label";
		String headerCol2 = "File Name";
		String setNotation = "";
		int extraLen = 12;
//		int extraLen = 0;
		String horizLine = "";
		if( col1Width < headerCol1.length() ) col1Width = headerCol1.length();
		if( col2Width < headerCol2.length() ) col2Width = headerCol2.length();
		
		for(int I = 0; I < col1Width + col2Width + extraLen; I++)
			horizLine += "-";
		
		/* now print header*/
		// print 1st line
		System.err.println(horizLine);
		// padding extra space after headerCol1 and headerCol2
		for(int I = 0; I <col1Width - headerCol1.length(); I++)
			headerCol1 += " ";
		// padding space after label name
		int hdcl2W = headerCol2.length();
		for(int I = 0; I < col2Width - hdcl2W; I++){
			headerCol2 += " ";
		}
		
		System.err.println("|   " + headerCol1 + " |   " + headerCol2 + "  |");
		// print header end line and data entry 1st line
		System.err.println(horizLine);
		
		HashMap<String, String> sorted = new HashMap<String, String>();
		sorted = sortHashMap(sorted, DAGraph2.DAGFileNamesTolabelSetMap);
		
		if(sorted == null)	System.out.println("Sorted map is empty !!!");
		
		for(String labset : sorted.keySet())
		{
			String Col2Data = "";
			if(labset != null) {
				Col2Data = labset.toString().replaceAll("dag_", "");
//				Col2Data.replace(directory, "");
			}
			else{
				labset = "";
			}
			setNotation = sorted.get(labset);
			if(setNotation != null){
				// padding space after notation
				int setNotLength = setNotation.length();
				for(int I = 0; I < col1Width - setNotLength; I++)
					setNotation += " ";
				// padding space after label name
//				System.out.println(col2Width + " " + Col2Data.length());
				int tempCOL2DATALEN = Col2Data.length();
				for(int I = 0; I <col2Width - tempCOL2DATALEN; I++)
					Col2Data += " ";
				System.err.println("|   " + setNotation + " |   " + Col2Data + "  |");
				// print the horiz line to indicate the end of a row
				System.err.println(horizLine);
			}
		}
	}

	
	/*
	 * Adding 1~N DAGs to DAG 0
	 * */
	public void addDAGS(ArrayList<DAGraph2> dags, int numOfDAGs)
	{
		DAGraph2 tempDAG = dags.get(0);
		for(int I = 1; I < numOfDAGs; I++){
			dags.get(0).addGraph(dags.get(I));
			dags.get(0).BFSVisit(dags.get(0).findRoots());
		}
	}
	
	public void makeHierarchy(String directory, String targetDir, String choiceClass) throws IOException
	{
		ArrayList<DAGraph2> dags = new ArrayList<DAGraph2>();
		
		ArrayList<String> dagFiles = fileListFromDir(directory); 
		DagLabelSize = (int) (Math.log(dagFiles.size())/Math.log(26)) + 1;
		int NumOfDAG = getAndConstructDAGs(dags, dagFiles.size(), dagFiles);
		
//		DagLabelSize = (int) (Math.log(NumOfDAG)/Math.log(26)) + 1;
		
		dags.get(0).addDAGS(dags, NumOfDAG);
		
//		System.out.println("Here goes the final DAG (after adding all): \n" + dags.get(0)); 
		
		HashSet<HashSet<String>> labels = dags.get(0).getLabelSet();

		printFileLegends(directory);
		
		/*
		 * If you want a random labeling of the nodes, then open the next two lines 
		 * and close the function "putLabelToNode" call
		 * in HierarchyTree.java in method traverserTree()
		 * */ 
		dags.get(0).performMappingSetLabel(labels);
		
		HierarchyTree2 HT = new HierarchyTree2();
		
		/*
		 * I believe, if there are multiple disjoint trees, then my current implementation (Date: 29/12/2018)
		 * will only consider one of the trees. I came to know about the limitation while constructing WGR 
		 * hierarchy and then regenerated the same with OMD example and a random DAG ... now I will try to fix it
		 * I think this limitation is due to the "constructTree" not properly handling all of the cases
		 * */

		HT.constructForest(labels);// this will construct a set of trees and store in "ArrayList<HierarchyNode> tree" of HT
		
		/*
		 * Now we need a method that will make a list of "ArrayList<HierarchyNode> subTrees" from "ArrayList<HierarchyNode> tree" 
		 * and call "printTreeLevel" for each of them.
		 * 
		 * We use a method "separateTrees" to construct subtrees
		 * 
		 * you must initialize "HashMap<Integer, ArrayList<HierarchyNode>> levelTree" to independently and separately printing 
		 * the subtrees 
		 * */
		
		HierarchyTree2.HierarchyNode2 root = HT.new HierarchyNode2(null, null);

		HT.separateTrees(root, null);
		
		HT.constructBNs(dags.get(0), directory, targetDir, choiceClass);
		
		HT.printTreeLevel();
		
//		System.out.println(dags.get(0));
		
	}
	
	public static void main(String args[]) throws IOException
	{
		DAGraph2 dagLearn = new DAGraph2();
//		String directory = "iOOBNLearning_IO\\GeneratedFiles\\";
//		String directory = "iOOBNLearning_IO\\";
		String directory = "InputOOBNFiles\\Nets\\GeneratedFiles\\";
		String targetDir = "GeneratedFiles\\";
		String choiceClass = "";
		dagLearn.makeHierarchy(directory, targetDir, choiceClass);
	}
}
