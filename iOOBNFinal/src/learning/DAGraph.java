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
import java.util.LinkedList;
import java.util.Queue;

import learning.HierarchyTree.HierarchyNode;

public class DAGraph 
{	
	public static HashMap<HashSet<String>, String> labelSetToNotationMap = new HashMap<HashSet<String>, String>();
	public static int maxNotationLen = 0;
	public static int LabelSize = 0;
	public class Node
	{
		String name;
		HashSet<String> label;
		HashMap<String, HashSet<String>> neighbors;// neighbors HashMap contains a set of neighbor node in keyset and their corresponding graph label in the mapped value (Set)
		
		/*
		 * The 1st hashmap is the neighbor node name
		 * the inner hashset is the graphLabel of the edge
		*/ 
		
		public Node(){
			this.name = "";
			this.label = new HashSet<String>();
			this.neighbors = new HashMap<String, HashSet<String>>(); 
			
		}
		public Node(String name, HashSet<String> label, HashMap<String, HashSet<String>> neighbors)
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
	
	HashMap<String, Node> daGraph;
	HashSet<String> neighborSet;
	
	public DAGraph()
	{
		this.daGraph = new HashMap<String, Node>(); 
		this.neighborSet = new HashSet<String>();
		/*
		 * in construct graph method, and any method which adds a neighbor (e.g. add another graph to construct super-graph)
		 * this neighborSet should be updated accordingly
		*/ 
	}
	
	public DAGraph(DAGraph g)
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
        	System.out.println(readLine);
        	String destinations [] = nodes[1].split(" ");
        	
        	DAGraph.Node tempNode = this.new Node();
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
	
	public void addGraph(DAGraph g)
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
			Node nodeToAdd = g.daGraph.get(nodeToAddKey);
			Node currNode = this.daGraph.get(nodeToAddKey);
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
	
	public static ArrayList<String> fileListFromDir(String path)
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
		
		System.out.println(fileList);
		return fileList;
	}
	
	// upperLowerCase = 65 for Upper, 97 for lower 
	public static String generateString(int i, int upperLowerCase) {
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
		
	public static int getAndConstructDAGs(ArrayList<DAGraph> dags, int numOfDAGs, ArrayList<String> dagFiles) throws IOException {
		int countNumOfDAG = 0;
		for(int I = 0; I < numOfDAGs; I++){
			if(dagFiles.get(I).endsWith(".txt")){
				String graphLabel = generateString(I, 97);// 97 for lower case
				DAGraph tempDAG = new DAGraph();
			
				tempDAG.constructGraph(dagFiles.get(I), graphLabel);
				System.out.println("\nAfter adding : " + dagFiles.get(I));
				tempDAG.BFSVisit(tempDAG.findRoots());
				dags.add(tempDAG);
				countNumOfDAG++;
			}
		}
		return countNumOfDAG;
	}
	
	/*
	 * Adding 1~N DAGs to DAG 0
	 * */
	public void addDAGS(ArrayList<DAGraph> dags, int numOfDAGs)
	{
		DAGraph tempDAG = dags.get(0);
		for(int I = 1; I < numOfDAGs; I++){
			dags.get(0).addGraph(dags.get(I));
			dags.get(0).BFSVisit(dags.get(0).findRoots());
		}
	}
	
	public static void makeHierarchy(String directory) throws IOException
	{
		ArrayList<DAGraph> dags = new ArrayList<DAGraph>();
		
		ArrayList<String> dagFiles = fileListFromDir(directory); 
		int NumOfDAG = getAndConstructDAGs(dags, dagFiles.size(), dagFiles);
		
		dags.get(0).addDAGS(dags, NumOfDAG);
		
		System.out.println("Here goes the final DAG (after adding all): \n" + dags.get(0)); 
		
		HashSet<HashSet<String>> labels = dags.get(0).getLabelSet();
//		System.out.println("\n\nLabels "+labels+"\n\n");
		System.out.println("\n\nLabels ");
		for(HashSet<String> hsL : labels)
			System.out.println(hsL);
		System.out.println("\n\n");
		/*
		 * If you want a random labeling of the nodes, then open the next two lines 
		 * and close the function "putLabelToNode" call
		 * in HierarchyTree.java in method traverserTree()
		 * */ 
		dags.get(0).performMappingSetLabel(labels);
//		System.out.println("\n\nMapping done "+labelSetToNotationMap+"\n\n");
		
		HierarchyTree HT = new HierarchyTree();
//		LabelSize = (int)(Math.log(labels.size()) / Math.log(26)) + 1;// this is done in "performMappingSetLabel" method
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
		
		HierarchyTree.HierarchyNode root = HT.new HierarchyNode(null, null);

		HT.separateTrees(root, null);
		
		HT.printTreeLevel();
		
//		HT.printTreeNormally(root);
//		System.out.println("\n\n\nHere goes the tree node count  with  normal tree print :" + HierarchyTree.countNodesInNormalPrint +  " \n\n\n");

//		System.out.println("\n\n\nHere goes the tree with  nodes in sequential order: \n\n\n");
//		HT.printTreeSequentially();
	}
	
	public static void main(String args[]) throws IOException
	{
//		String directory = "iOOBNLearning_IO\\GeneratedFiles\\";
		String directory = "iOOBNLearning_IO\\";
//		String directory = "InputOOBNFiles\\Nets\\GeneratedFiles\\";
		makeHierarchy(directory);
	}
}
