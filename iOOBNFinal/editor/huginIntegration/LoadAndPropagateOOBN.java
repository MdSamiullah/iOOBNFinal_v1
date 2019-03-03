package huginIntegration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import javax.management.Query;

import COM.hugin.HAPI.*;
import COM.hugin.HAPI.Class;
import COM.hugin.HAPI.Native.HAPI;
//import COM.hugin.

public class LoadAndPropagateOOBN {
	/**
	 * A nice information I have noticed that .oobn requires file names to be
	 * unique with 1st line but if .net is used, multiple classes can
	 * accommodate in a single file and no restriction of same name of 1st line.
	 * Hence, My problem of file name extension is solved. I can replace .ioobn
	 * with .net in my description so that 1st line can be changed flawlessly
	 * 
	 * This function parses the given NET/OOBN file, compiles the network.
	 * 
	 */
	
	public ArrayList<ArrayList<String>> outcome;
	public ArrayList<ArrayList<String>> LAP(ArrayList<String> classFileNames, ArrayList<String> classNames, ArrayList<String> caseName, String dir, int numOfStates) throws ExceptionHugin, IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		long startTime = System.currentTimeMillis();
		
		outcome = new ArrayList<ArrayList<String>>();
//		ClassParseListener classParseListener = new DefaultClassParseListener();
//		ParseListener parseListener = new DefaultClassParseListener();
		MyParseListener mpl = new MyParseListener();

		if(!SIICompilation.onlyDisplatTime) System.out.println("Class File Name " + classFileNames + "    " + classFileNames.size());
		if(!SIICompilation.onlyDisplatTime) System.out.println("Class Names " + classNames + "    " + classNames.size());

		/*
		 * The following segment is for loading classes where each class is in a
		 * single .oobn file
		 */
		ArrayList<ClassCollection> cc = new ArrayList<ClassCollection>();
		ArrayList<Class> c = new ArrayList<Class>();
		ArrayList<Domain> dom = new ArrayList<Domain>();
//		System.out.println("Class file names " + classFileNames);
//		System.out.println("Class names " + classNames);
		for (int I = 0; I < classFileNames.size(); I++) {
			ClassCollection ccTemp = new ClassCollection();
			if(!SIICompilation.onlyDisplatTime)  System.out.println("Class file name: " + classFileNames.get(I));
			ccTemp.parseClasses(classFileNames.get(I), mpl);
			
			cc.add(ccTemp);
			c.add(I, cc.get(I).getClassByName(classNames.get(I)));
			dom.add(I, c.get(I).createDomain());
			
			/* from the documentation of Hugin, Sets an initial triangulation for this Domain. 
			 * This triangulation will be used by the H_TM_TOTAL_WEIGHT triangulation method. 
			 * The purpose is to (1) improve the generated triangulation, and (2) to reduce 
			 * the run-time of the algorithm. The triangulation must be specified in the form 
			 * of an elimination sequence.
			 * 
			 * That means it can be used to feed/create our JT but we need do some reverse engineering
			 * Since, elimination order can give us fill-in edges
			 * fill-in edges can give us triangulation
			 *  triangulation can make JT
			 *  
			 *  And we have JT > triangulate > fill-in > elimination order
			 *  Though I need to check whether fill-in to elimination order is possible/straight forward or not
			 * 
			*/
			
			dom.get(I).triangulate(Domain.H_TM_BEST_GREEDY);
			
//			NodeList nl = new NodeList();
			
//			ArrayList<String> order = eliminationOrder("jt.txt");
			
//			System.out.println("Node elimination order : " + order);
			
//			for(String node : order)
//				nl.add(dom.get(I).getNodeByName(node));
//			
//			dom.get(I).triangulate(nl);
			
			JunctionTreeList jT = dom.get(I).getJunctionTrees();
			String JTFileName = "";
			if(!SIICompilation.onlyDisplatTime)
				printJunctionTree(jT);
			printJunctionTreeFile(jT, classNames.get(I), dir);
			JTFileName = dir + "JT_" + classNames.get(I).replace(".oobn", "")+ ".txt";
//			constructJTOwn(dom.get(I));
			
			boolean isJTHold = false;
			boolean isMarkovBlanketforJT = false;
			
//			int cost = calculateOwnJTCost(dom.get(I), "jt.txt");// from string representation
//			int costK = calculateOwnJTCostKanazawa(dom.get(I), "jt.txt");// from string representation 
//			isJTHold = checkJTProperty();
//			isMarkovBlanketforJT = checkMarkovBlanketforJT(dom.get(I));
			
//			if(isMarkovBlanketforJT == false) {
//				if(!SIICompilation.onlyDisplatTime) 
//					System.out.println("JT.TXT It's not a JT due to markov blanket!!!");
//			}
//			
//			if(isJTHold == false) {
//				if(!SIICompilation.onlyDisplatTime) 
//					System.out.println("JT.TXT  It's not a JT");
//			}
//			else {
//				if(!SIICompilation.onlyDisplatTime) 
//					System.out.println("JT.TXT  It's a JT");
//			}
//			System.out.println("Own JT (Not thin) Cost "+ cost);
//			System.out.println();
			
//			if(!SIICompilation.onlyDisplatTime) 
//			{
//				int cost = calculateOwnJTCost(dom.get(I), "jt1.txt");// from string representation
//				int costK = calculateOwnJTCostKanazawa(dom.get(I), "jt1.txt");
//				isJTHold = checkJTProperty();
//				isMarkovBlanketforJT = checkMarkovBlanketforJT(dom.get(I));
//				if(isJTHold == false)	System.out.println("JT1.TXT It's not a JT");
//				else System.out.println("JT1.TXT It's a JT");
//	//			System.out.println("Own JT (thin) Cost "+ cost + "  Kanazawa " + costK);
//	//			System.out.println();
//				
//				cost = calculateOwnJTCost(dom.get(I), "jt2.txt");// from string representation
//				costK = calculateOwnJTCostKanazawa(dom.get(I), "jt2.txt");
//				isJTHold = checkJTProperty();
//				isMarkovBlanketforJT = checkMarkovBlanketforJT(dom.get(I));
//				if(isJTHold == false)	System.out.println("JT2.TXT It's not a JT");
//				else System.out.println("JT2.TXT It's a JT");
//	//			System.out.println("Hugin JT Cost "+ cost + "  Kanazawa " + costK);
//	//			System.out.println();
//				
//				cost = calculateOwnJTCost(dom.get(I), "jt3.txt");// from string representation
//				costK = calculateOwnJTCostKanazawa(dom.get(I), "jt3.txt");
//				isJTHold = checkJTProperty();
//				isMarkovBlanketforJT = checkMarkovBlanketforJT(dom.get(I));
//				if(isJTHold == false)	System.out.println("JT3.TXT It's not a JT");
//				else System.out.println("JT3.TXT It's a JT");
//			}
//			System.out.println("Thinning JT Cost "+ cost + "  Kanazawa " + costK);
//			System.out.println();
			
//			cost = calculateOwnJTCost(dom.get(I), "jt4.txt");// from string representation
//			costK = calculateOwnJTCostKanazawa(dom.get(I), "jt4.txt");
//			isJTHold = checkJTProperty();
//			isMarkovBlanketforJT = checkMarkovBlanketforJT(dom.get(I));
////			if(isJTHold == false)	System.out.println("JT4.TXT It's not a JT");
////			else System.out.println("JT4.TXT It's a JT");
////			System.out.println("Prev Optimization (Separator based) JT Cost "+ cost + "  Kanazawa " + costK);
////			System.out.println();
//			
//			cost = calculateOwnJTCost(dom.get(I), "jt5.txt");// from string representation
//			costK = calculateOwnJTCostKanazawa(dom.get(I), "jt5.txt");
//			isJTHold = checkJTProperty();
//			isMarkovBlanketforJT = checkMarkovBlanketforJT(dom.get(I));
//			if(isJTHold == false)	System.out.println("JT5.TXT It's not a JT");
////			else System.out.println("JT5.TXT It's a JT");
////			System.out.println("Optimization (Separator based + Thinning) JT Cost "+ cost + "  Kanazawa " + costK);
////			System.out.println();
//			
//			cost = calculateOwnJTCost(dom.get(I), "jt6.txt");// from string representation
//			costK = calculateOwnJTCostKanazawa(dom.get(I), "jt6.txt");
//			isJTHold = checkJTProperty();
//			isMarkovBlanketforJT = checkMarkovBlanketforJT(dom.get(I));
////			if(isJTHold == false)	System.out.println("JT6.TXT It's not a JT");
////			else System.out.println("JT6.TXT It's a JT");
////			System.out.println("Optimization (Separator based + Thinning) JT Cost [different order]"+ cost + "  Kanazawa " + costK);
////			System.out.println();
//			
//			cost = calculateOwnJTCost(dom.get(I), "jt7.txt");// from string representation
//			costK = calculateOwnJTCostKanazawa(dom.get(I), "jt7.txt");
//			isJTHold = checkJTProperty();
//			isMarkovBlanketforJT = checkMarkovBlanketforJT(dom.get(I));
////			if(isJTHold == false)	System.out.println("JT7.TXT It's not a JT");
////			else System.out.println("JT7.TXT It's a JT");
////			System.out.println("Optimization (Separator based + Thinning) JT Cost [different order]"+ cost + "  Kanazawa " + costK);
////			System.out.println();
			
			dom.get(I).compile();
			int jtcost = calculateOwnJTCostKanazawaWRTNumOfStates(dom.get(I), JTFileName, numOfStates);
			System.out.println("JTCost Hugin " + jtcost);
//			printBeliefsAndUtilities(dom.get(I));
		}
		long endTime = System.currentTimeMillis();

//		System.out.println("LAP Compile and JT writing time took : " + (endTime - startTime) + " milliseconds");
		
		
		/*
		 * The following segment is for loading classes where multiple classes
		 * in a single .net file
		 */
		
		return outcome;
	}
	
	class MyParseListener implements ClassParseListener
	{
	    public void parseError (int line, String msg)
	    {
	        System.out.println ("Parse error in line " + line + ": " + msg);
	    }
	    public void insertClass (String className, ClassCollection cc)
	    {
	        try {
	            cc.parseClasses (className + ".oobn", this);
	        } catch (ExceptionHugin e) {
	            System.out.println ("Parsing failed: " + e.getMessage());
	        }
	    }
	}

	public void printJunctionTree(JunctionTreeList tree) throws ExceptionHugin, IOException{
		
		System.out.println("The junction tree "+tree);
		for(int i=0; i < tree.size(); i++){
			System.out.println(tree.get(i));
			JunctionTree jt = tree.get(i);
			
			CliqueList cList = jt.getCliques();
			for(Clique c: cList){
				System.out.println(c.getMembers());
			}
			
			System.out.println("Now tree representation: ");
			Clique root = jt.getRoot();
			
			// following commented 2 lines can add/delete neighbor to a clique (only root is shown though), thus jt can be extended/shrinked
//			jt.getRoot().getNeighbors().add(arg0);
//			jt.getRoot().getNeighbors().remove(o)
			
			// following commented 2 lines can add/delete member nodes to a clique (only root is shown though), thus cliques in jt can be extended/thin
//			jt.getRoot().getMembers().add(e)
//			jt.getRoot().getMembers().remove(o);
			
			setA = new HashSet();
			recursiveTreeDisplay(root, "");
			int cost = calculateJTCost(jt);
			System.out.println("The cost of the JT = " + cost);
			
		}
	}
	
	
	public void printJunctionTreeFile(JunctionTreeList tree, String className, String dir) throws ExceptionHugin, IOException{
		String FILENAME = dir + "JT_" + className.replace(".oobn", "")+ ".txt";
		FileWriter fw = new FileWriter(FILENAME);
		BufferedWriter bw = new BufferedWriter(fw);
		
		ArrayList<String> JTInfo = new ArrayList<String>();
		int cliqueNumber = 0;
		HashMap<Clique, Integer> mapCliqueNumber = new HashMap<Clique, Integer>();
		
		for(int i=0; i < tree.size(); i++){
			
			JunctionTree jt = tree.get(i);
			
			CliqueList cList = jt.getCliques();
			for(Clique c: cList){
				mapCliqueNumber.put(c, ++cliqueNumber);
			}
			
			for(Clique c: cList){
				String tempStr = mapCliqueNumber.get(c) + ": ";
				
				String temp2str = c.getMembers().toString();
				temp2str = temp2str.replace("{", "");
				temp2str = temp2str.replace("}", "");
				temp2str = temp2str.replace(" ", "");
				
				tempStr += temp2str + " : "; 
				
				for (Clique neighbor: c.getNeighbors()){
					tempStr += mapCliqueNumber.get(neighbor) + " ";
				}
				bw.write(tempStr+"\n");
			}
			
			
		}
		if(!SIICompilation.onlyDisplatTime)
			System.out.println("File writing of the JT done in : " + FILENAME);
		bw.close();
	}
	
	public int calculateJTCost(JunctionTree jt) throws ExceptionHugin
	{
		int cost = 0;
		
		CliqueList cList = jt.getCliques();
		for(Clique clq: cList){
			int Omega = 1;
			int numOfNeighbor = clq.getNeighbors().size();
			Omega = numOfNeighbor;
			for(Node n: clq.getMembers()){
				Omega *= n.getTable().getSize();
//				System.out.println(n.getName() + "   Size: " + n.getTable().getSize());
			}
			cost += Omega;
		}
		
		return cost;
	}
	
	
	Set<Clique> setA;
	
	public void recursiveTreeDisplay(Clique root, String tab) throws ExceptionHugin
	{
		if(setA.contains(root))
			return;
		System.out.println(tab+ root.getMembers());
		setA.add(root);
		CliqueList neighbors = root.getNeighbors();
		tab+= "\t";
		for(Clique clq: neighbors){
			recursiveTreeDisplay(clq, tab);
			//System.out.println(clq.getMembers());
		}
	}
	
	
	// constructing JT as your choice by modifying JT cliques and their neighbors where JT is specified by a list of ordinary nodes' set
		// as an example, the JT we found using SI can be represented as follows
		/*
		 * 1. D,E,B   : 2
		 * 2. L,S,B,E  : 1 3
		 * 3. S,X,T,L,E : 2 4
		 * 4. V,T,S,X  : 3 5
		 * 5. V,C,S,X  : 4 6
		 * 6. C,M,R,S,X,Y: 5 7
		 * 7. R,G,X,Y  : 6 8
		 * 8. X,Y,N,G  : 7
		 * 
		 * */
		
		public ArrayList<ArrayList<String>> myJTCliques;// = new ArrayList<ArrayList<String>>();
		public HashMap<Integer, ArrayList<Integer>> myJTClqNeighbors;// = new HashMap<Integer, ArrayList<Integer>>();
		
		
		// here delimiter is "." and ":" and "space" i.e. "[.: ]+"
		
		public void getJTInfoFileString(String filename, String delimiter) throws IOException{
			
			myJTCliques = new ArrayList<ArrayList<String>>();
			myJTClqNeighbors = new HashMap<Integer, ArrayList<Integer>>();
			
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line = "";
	        while ((line = bufferedReader.readLine()) != null) {
	        		//System.out.println(line);
	            String[] names = line.split(delimiter);
	            String[] nodes = names[1].split(",");// don't put any space within clique variables
	            
	            myJTCliques.add(Integer.parseInt(names[0])-1, new ArrayList<String> (Arrays.asList(nodes)));
	            
	            ArrayList<Integer> tempNeighbors = new ArrayList<Integer>();
	            
	            for(int i=2; i < names.length; i++){// startd from 2, because splitted 0 & 1 contains number and info of cliques
	            	// where 2~N contains neighbor info
	            	tempNeighbors.add(Integer.parseInt(names[i]));
	            }
	            myJTClqNeighbors.put(Integer.parseInt(names[0])-1, tempNeighbors);
	        }
		}
	
		
		/*
		 * this function can be used to calculate JT cost where JT is in string form and extracted from file
		 * 
		 * */
		public int calculateOwnJTCost(Domain dom, String fileJT) throws IOException, ExceptionHugin, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			getJTInfoFileString(fileJT, "[: ]+");
			// we don't need to extract nodes from JTs, since we have domain.getNodes() and domain.getNodesbyName()
			
			// myStrClq: myJTCliques
			int cost = 0;
			for(Integer n: myJTClqNeighbors.keySet()){
				int numOfNeighbor = myJTClqNeighbors.get(n).size();
				int omega = numOfNeighbor;
				for(String key: myJTCliques.get(n)){
					if (dom.getNodeByName(key) != null)
						omega *= dom.getNodeByName(key).getTable().getSize();
					else omega *= 1;
				}
				cost += omega;
			}
			
			return cost;
		}

		/*
		 * this function can be used to calculate JT cost of Kanazawa where JT is in string form and extracted from file
		 * 
		 * */
		public int calculateOwnJTCostKanazawa(Domain dom, String fileJT) throws IOException, ExceptionHugin, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			getJTInfoFileString(fileJT, "[: ]+");
			// we don't need to extract nodes from JTs, since we have domain.getNodes() and domain.getNodesbyName()
			
			// myStrClq: myJTCliques
			int cost = 0;
			for(Integer n: myJTClqNeighbors.keySet()){
				int numOfNeighbor = myJTClqNeighbors.get(n).size();
				int omega = numOfNeighbor;
				int stateSpaceSize = 2;
				for(String key: myJTCliques.get(n)){
					if (dom.getNodeByName(key) != null)
						omega *= stateSpaceSize;
					else omega *= 1;
				}
				cost += omega;
			}
			
			return cost;
		}
		
		/*
		 * this function can be used to calculate JT cost of Kanazawa where JT is in string form and extracted from file
		 * 
		 * */
		public int calculateOwnJTCostKanazawaWRTNumOfStates(Domain dom, String fileJT, int numOfStates) throws IOException, ExceptionHugin, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			getJTInfoFileString(fileJT, "[: ]+");
			// we don't need to extract nodes from JTs, since we have domain.getNodes() and domain.getNodesbyName()
			
			// myStrClq: myJTCliques
			int cost = 0;
			for(Integer n: myJTClqNeighbors.keySet()){
				int numOfNeighbor = myJTClqNeighbors.get(n).size();
				int omega = numOfNeighbor;
				int stateSpaceSize = numOfStates;
				for(String key: myJTCliques.get(n)){
					if (dom.getNodeByName(key) != null)
						omega *= stateSpaceSize;
					else omega *= 1;
				}
				cost += omega;
			}
			
			return cost;
		}
		public void displayGraph(HashMap<String, HashSet<String>> graph){
			System.out.println("Current graph: ");
			for(String key: graph.keySet()){
				System.out.println(graph.get(key));
			}
		}
		
		/*
		 * this function generates elimination order for a JT where JT is in string form and extracted from file
		 * 
		 * */
		public ArrayList<String> eliminationOrder(String fileJT) throws IOException, ExceptionHugin, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			ArrayList<String> nodeOrder = new ArrayList<String>();
			
			getJTInfoFileString(fileJT, "[: ]+");
			
			formGraphFromJT();
			
			while(graphJT.size() > 0){
//				displayGraph(graphJT);
				String minimumDegreeNode = "";
				int minimumDeg = graphJT.size();
				
				// finding minimum degree node
				for(String key: graphJT.keySet()){
					if(graphJT.get(key).size() < minimumDeg){
						minimumDeg = graphJT.get(key).size();
						minimumDegreeNode = key;
					}
				}
				
				nodeOrder.add(minimumDegreeNode);
				
				// removing links
				for(String neighbors: graphJT.get(minimumDegreeNode))
					graphJT.get(neighbors).remove(minimumDegreeNode);
				graphJT.remove(minimumDegreeNode);
			}
			
			return nodeOrder;
		}
		
//		ArrayList<ArrayList<String>> graphJT = new ArrayList<ArrayList<String>>();
		
		HashMap<String, HashSet<String>> graphJT = new HashMap<String, HashSet<String>>(); 
		
		/*
		 * this function generates graph for a JT where JT is in string form and extracted from file
		 * 
		 * */
		public void formGraphFromJT() {
						
			for(ArrayList<String> clq : myJTCliques){
				for(String src : clq){
					for(String end : clq){
						if(src != end){
							if(graphJT.containsKey(src)){
								graphJT.get(src).add(end);
							}
							else{
								HashSet<String> tempSet = new HashSet<String>();
								tempSet.add(end);
								graphJT.put(src, tempSet);
							}
						}
					}
				}
			}
		}
		
		
//		public ArrayList<ArrayList<String>> myJTCliques;// = new ArrayList<ArrayList<String>>();
//		public HashMap<Integer, ArrayList<Integer>> myJTClqNeighbors;// = new HashMap<Integer, ArrayList<Integer>>();
		
		public Integer parent[];
		
		public void showParents(){
			for(int i = 0; i < parent.length; i++){
				if(parent[i]!= null)
					System.out.print(parent[i] + " ");
			}
			System.out.println();
		}
		
		public ArrayList<Integer> path;
		
		
		public boolean checkMarkovBlanketforJT(Domain dom) throws ExceptionHugin
		{
			boolean blanketHolds = true;
			NodeList nodes = dom.getNodes();
			
			for(Node n: nodes){
				NodeList parentNodes = n.getParents();
				ArrayList<String> parentNames = new ArrayList<String>();
				
				for(Node p: parentNodes){
					parentNames.add(p.getName());
				}
				parentNames.add(n.getName());// a node itself and its parents should be in at least 1 clique 
				
				
				boolean found = false;
				for(ArrayList<String> clq: myJTCliques){
					if(clq.containsAll(parentNames))	{	found = true;	break;}
				}
				
				if(!found)	{
//					System.out.println(" It's not a JT!!! Because " + parentNames + " is not found in any of the cliques.");
					return false;
				}
				
			}
				
			return blanketHolds;
		}
		
		public boolean checkJTProperty()
		{
			boolean JTHolds = true;
			
			for(Integer src: myJTClqNeighbors.keySet()){
				for(Integer dest: myJTClqNeighbors.keySet()){
					if(src < dest){
						parent = new Integer[myJTCliques.size()+1];
						
						ArrayList<Integer> visited = new ArrayList<Integer>();
						path = new ArrayList<Integer>();
//						System.out.print("Path " + (src+1) + " to " + (dest+1) + " : ");
						parent[src] = -1;
						findPathBFS(src, dest, visited);
//						showParents();
//						System.out.print("The path : ");
						extractPath(dest);
//						System.out.println();
//						System.out.println(path);
						
						ArrayList<String> srcClique = myJTCliques.get(src);
						ArrayList<String> destClique = myJTCliques.get(dest);
						
						ArrayList<String> commonClqElem = findIntersection(srcClique, destClique);
						boolean isContain = doesPathContainsCommonClqElem(commonClqElem, src, dest);
						if(isContain == false){
							return isContain;
						}
					}
				}
			}
				
			return JTHolds;
		}
		
		
		// check in the path for common elements
		public boolean doesPathContainsCommonClqElem(ArrayList<String> commonItems, Integer src, Integer dest)
		{
			boolean isContain = true;
			
			for(Integer clqInd : path){
				isContain = myJTCliques.get(clqInd).containsAll(commonItems);
				if(isContain == false){
					System.out.println(path);
					System.out.print("(In LAP) JT fails for Common Items " +  commonItems + " between cliques [" + (src+1) + ", " + (dest+1) + "] => ( " + myJTCliques.get(src) + ", " + myJTCliques.get(dest) + " ) : in Clique : ");
					System.out.println((clqInd+1) + " : " + myJTCliques.get(clqInd));
					return isContain;
				}
			}
			
			return isContain;
		}
		
		public ArrayList<String> findIntersection(ArrayList<String> src, ArrayList<String> dest)
		{
			ArrayList<String> common = new ArrayList<String>();
			
			for(String node: src){
				if(dest.contains(node))
					common.add(node);
			}
			
			return common;
		}
		
		public void extractPath(Integer dest){
			
			if(parent[dest] == -1) {	path.add(dest);	return;}
			
			extractPath(parent[dest]);
			
			path.add(dest);
			
		}
		
		// this is DFS based, which may lead to longest path instead of shortest. Use BFS based then. Although, in tree, there is only a single
		// path between any two nodes. So, you can also use this one.
		public void findPath(Integer src, Integer dest, ArrayList<Integer> visited){
			
			visited.add(src);
			
			if(src == dest)	return;
			
			for(Integer next : myJTClqNeighbors.get(src)){
				// next - 1, since we stored neighbors without starting from 0
				if(!visited.contains(next-1)){
					parent[next-1] = src;
					findPath(next-1, dest, visited);
				}
			}
		}
		
		public void findPathBFS(Integer src, Integer dest, ArrayList<Integer> visited){
			
			Queue<Integer> Q = new LinkedList<Integer>();
			visited.add(src);
			Q.add(src);
			
			while(!Q.isEmpty()){
				Integer src1 = Q.remove();
				for(Integer next : myJTClqNeighbors.get(src1)){
					// next - 1, since we stored neighbors without starting from 0
					if(!visited.contains(next-1)){
						parent[next-1] = src1;
						Q.add(next-1);
						visited.add(next-1);
					}
				}
			}	
		}
		
		 
	public void constructJTOwn(Domain dom) throws IOException, ExceptionHugin, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		getJTInfoFileString("jt.txt", "[: ]+");
		// we don't need to extract nodes from JTs, since we have domain.getNodes() and domain.getNodesbyName()  

		// Hugin does not allow creating a clique. Hence, need to make an alternate way to do so as follows
		
		JunctionTree JT = dom.getJunctionTrees().get(0);
		
		JunctionTreeList JTL = new JunctionTreeList(); 
		
		CliqueList Clqs = JT.getCliques();
		
		
		
		for(ArrayList<String> myStrClq: myJTCliques){
			Clique clq = Clqs.get(0);
			for(String nodeName : myStrClq){
				
//				clq.getMembers().add(dom.getNodeByName(nodeName));
				boolean done = clq.getMembers().add(dom.getNodeByName(nodeName));
				System.out.println("Status of adding " + done);
				System.out.println("Hello the clique members after appending " + clq.getMembers() + "  " + nodeName);
			}
			
			
		}
		
	}
	/**
	 * Print the beliefs and expected utilities of all nodes in the domain.
	 */
	public void printBeliefsAndUtilities(Domain domain) throws ExceptionHugin {
		NodeList nodes = domain.getNodes();
		boolean hasUtilities = containsUtilities(nodes);
		java.util.ListIterator it = nodes.listIterator();

		while (it.hasNext()) {
			Node node = (Node) it.next();
			ArrayList<String> tempList = new ArrayList<String>();
			System.out.println();
			System.out.println(node.getLabel() + " (" + node.getName() + ")");
			
			tempList.add(node.getLabel() + " (" + node.getName() + ")");

			if (node instanceof DiscreteNode) {
				DiscreteNode dNode = (DiscreteNode) node;

				for (int i = 0, n = (int) dNode.getNumberOfStates(); i < n; i++) {
					System.out.print("  - " + dNode.getStateLabel(i) + " " + dNode.getBelief(i));
					tempList.add("  - " + dNode.getStateLabel(i) + " " + dNode.getBelief(i));
					if (hasUtilities){
						System.out.println(" (" + dNode.getExpectedUtility(i) + ")");
						tempList.add(" (" + dNode.getExpectedUtility(i) + ")");
					}
					else
						System.out.println();
				}
			} else if (node instanceof ContinuousChanceNode) {
				ContinuousChanceNode ccNode = (ContinuousChanceNode) node;

				System.out.println("  - Mean : " + ccNode.getMean());
				tempList.add("  - Mean : " + ccNode.getMean());
				System.out.println("  - SD   : " + Math.sqrt(ccNode.getVariance()));
				tempList.add("  - SD   : " + Math.sqrt(ccNode.getVariance()));
			} else if (node instanceof UtilityNode){
				System.out.println("  - Expected utility: " + ((UtilityNode) node).getExpectedUtility());
				tempList.add("  - Expected utility: " + ((UtilityNode) node).getExpectedUtility());
			}
			else /* "node" is a (real-valued) function node */
				try {
					System.out.println("  - Value: " + ((FunctionNode) node).getValue());
					tempList.add("  - Value: " + ((FunctionNode) node).getValue());
				} catch (ExceptionHugin e) {
					System.out.println("  - Value: N/A");
					tempList.add("  - Value: N/A");
				}
			this.outcome.add(tempList);
		}
	}

	/**
	 * Are there utility nodes in the list?
	 */
	public static boolean containsUtilities(NodeList list) {
		java.util.ListIterator it = list.listIterator();

		while (it.hasNext())
			if (it.next() instanceof UtilityNode)
				return true;

		return false;
	}

	public static void main(String args[]) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ExceptionHugin, IOException{
		LoadAndPropagateOOBN lap = new LoadAndPropagateOOBN(); 
		ArrayList<String> fileNames = new ArrayList<String>();
		ArrayList<String> classNames = new ArrayList<String>();
//		fileNames.add("C:\\Users\\msamiull\\workspace\\jgraphx-master\\BasalCoverGrowthDeath.oobn");
//		classNames.add("BasalCoverGrowthDeath");
//		fileNames.add("C:\\Users\\msamiull\\workspace\\jgraphx-master\\WallabyNatural.oobn");
//		classNames.add("WallabyNatural");
		fileNames.add("C:\\Users\\msamiull\\workspace\\jgraphx-master\\NoBCGD.oobn");
		classNames.add("NoBCGD");
//		fileNames.add("C:\\Users\\msamiull\\workspace\\jgraphx-master\\Diag.oobn");
//		classNames.add("Diag");
		lap.LAP (fileNames, classNames, null, "", 2);// dir = "";
	}
	
}
