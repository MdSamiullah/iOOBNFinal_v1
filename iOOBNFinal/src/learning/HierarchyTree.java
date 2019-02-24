package learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import learning.HierarchyTree.HierarchyNode;

public class HierarchyTree{
	
	public static int labelScale = 1;// to enlarge the tree, increase the value
	public static boolean redTree = false;// to print tree in black, put false
	
	public static boolean doneRootPrint = false;
//	public static int globalSlotSize = 1;// I have replaced this one by a local variable to decouple the tree printing to facilitate mutiple tree printing
	public static HashMap<HashSet<String>, ArrayList<HashSet<String>>> parentChildrenMap = new HashMap<HashSet<String>, ArrayList<HashSet<String>>>(); 

	public static int labelNodeCount = 0;// This will be used to generate string for label nodes. Its max number will be = DA.LabelSize 
	public static int maxSetSize = 0;// this is now required to print the legend table
	public static int slotWidth = 0; // this depends on the DAGraph construction, not on the tree printing or construction
	
	public class HierarchyNode{
		HashSet<String> label;
		HashSet<String> parentNode;
		
		int level;
		int slotSize;
		int offSet;
		int nodeNumberInTheLevel;
		int numOfChild;// how many children this node has
		int childNumber;// what number of child is this node of its parent
		int maxNumOfChildInSameLevel;
		int numOfSiblings; // this is the number of children of its parent
		
		public HierarchyNode()
		{
			this.label = new HashSet<String>();
			this.parentNode = null;
			
			this.level = 0;
			this.slotSize = 1;
			this.offSet = 0;
			this.nodeNumberInTheLevel = 0;
			this.numOfChild = 0;
			this.childNumber = 1;// child number will start from 1
			this.maxNumOfChildInSameLevel = 0;
			this.numOfSiblings = 1;
		}
		
		public HierarchyNode(HashSet<String> label, HashSet<String> parentNode)
		{
			this.label = label;
			this.parentNode = parentNode;
		}
		
		public void setParent(HashSet<String> parentNode){
			this.parentNode = parentNode;
		}
		
		public String toString()
		{	
			String str = "";
			str += "Label " + this.label + " Parent " + this.parentNode + " level " + this.level + " slotSize " + this.slotSize 
					+ " offSet " + this.offSet + " child Number " + this.childNumber
					+ " child number in the level " + this.nodeNumberInTheLevel;

			return str; 
		}
	}
	
	ArrayList<HierarchyNode> tree;
	ArrayList<ArrayList<HierarchyNode>> subTree;
	HashMap<Integer, ArrayList<HierarchyNode>> levelTree;// node level vs nodes mapping
	
	public HierarchyTree()
	{
		this.tree = new ArrayList<HierarchyNode>();
		this.subTree = new ArrayList<ArrayList<HierarchyNode>>(); 
		this.levelTree = new HashMap<Integer, ArrayList<HierarchyNode>>(); 
	}
	
	// this method will traverse the "tree" and find for multiple roots and construct separate trees and store in subTree arrayList
	public void separateTrees(HierarchyNode root, ArrayList<HierarchyNode> tempTree){
		for(HierarchyNode next: this.tree){
			if(next.parentNode == root.label){
				if(root.label == null){
					tempTree = new ArrayList<HierarchyNode>();
					this.subTree.add(tempTree);
				}
				tempTree.add(next);
				separateTrees(next, tempTree);
			}
		}
	}
	
	public void constructForest(HashSet<HashSet<String>> labelSet)
	{
		HashMap<Integer, ArrayList<HierarchyNode>> partitionLabelSet = new HashMap<Integer, ArrayList<HierarchyNode>>();
		
		int maxSize = 0;
		int minSize = 0;
		
		// separating the label set 
		for(HashSet<String> labSet: labelSet){
			int size = labSet.size();
			
			if(size > maxSize)	maxSize = size;
			else if(size < minSize) minSize = size;
			
			HierarchyNode hNode = new HierarchyNode(labSet, null);// initially parent is null
			if(partitionLabelSet.containsKey(size)){
				partitionLabelSet.get(size).add(hNode);
			}
			else{
				ArrayList<HierarchyNode> tempList = new ArrayList<HierarchyNode>();
				tempList.add(hNode);
				partitionLabelSet.put(size, tempList);
			}
		}
		
//		System.out.println("The map that has size based partition: \n" + partitionLabelSet + "\n\n");
		
		maxSetSize = maxSize;
		
		
//		DAGraph.LabelSize += 1;
		slotWidth = DAGraph.LabelSize * labelScale;
		
		/*
		 * based on the size based partition, now we will find immediate parents (with smallest size difference)
		 * e.g. for "AG", "ACG" and "ACFG" both being it's ancestor in the tree, 
		 * but immediate parent will be "ACG" 
		 * */
		int n = minSize;
		while(n < maxSize){// because we don't need to find parent of max size set
			if(partitionLabelSet.containsKey(n)){
				ArrayList<HierarchyNode> tempChildLabelList = partitionLabelSet.get(n);
				for(HierarchyNode potenChild: tempChildLabelList){// for each of the label set of size n
					boolean done = false;
					int m = n + 1;
					
					while(m <= maxSize){// check n+1, n+2 ... maxSize labels until we find a superset of the child
						if(partitionLabelSet.containsKey(m)){
							ArrayList<HierarchyNode> tempParentLabelList = partitionLabelSet.get(m);
							
							/*
							 * for each of the label of size > n, check for superset
							 * if found then break for that particular child label
							 * */
							for(HierarchyNode potenParent: tempParentLabelList){  
								if(potenParent.label.containsAll(potenChild.label))// subsumes
								{
									potenChild.setParent(potenParent.label);
									// done with parent info adding, now add the child info to the tree
									this.tree.add(potenChild);
									done = true;
									break;
								}
								/*
								 * else the parent for some setLabels will be still null indicating that
								 * indicates that it wil be a child of root node
								 * */ 
							}
						}
						// if parent for a label is found of size m, no need to check size m+1 or further
						if(done == true) break;
						else m++;
					}
					if(done == false)// if there is any node which belongs to a disjoint tree/ or a different tree in the forest then we need to take care of that here
					{
						System.out.println("A node with no matching parent in the tree " + potenChild);
						this.tree.add(potenChild);
					}
				}
			}
			n++;
		}
		// add the max size nodes in the tree
		this.tree.addAll(partitionLabelSet.get(maxSize));
	}
	
	
	public void putLabelToNode(HashSet<String> label, HashMap<HashSet<String>, String> labelSetToNotationMap, int labelWidth, int labelNodeCount){
		String notation = DAGraph.generateString(labelNodeCount, 65);// 65 for upper case, 97 for lower
		for(int I = 0; I < labelWidth - notation.length(); I++)
			notation = "0" + notation;// just for padding 0 with the notation
		
		labelSetToNotationMap.put(label, notation);
	}	
	
	/*
	 * Tree traversing will traverse the tree in DFS order to put some information in the nodes of the tree (or trees)
	 * It will not change any global variables
	 * */
	public void traverseTree(HierarchyNode root, ArrayList<HierarchyNode> tree, int level, int childNumberOwn)
	{ 			
		if(parentChildrenMap.containsKey(root.parentNode)){
			putLabelToNode(root.label, DAGraph.labelSetToNotationMap, DAGraph.LabelSize, labelNodeCount++);
			parentChildrenMap.get(root.parentNode).add(root.label);
		}
		else if(root.label != null)
		{
			ArrayList<HashSet<String>> tempChildren = new ArrayList<HashSet<String>>();
			tempChildren.add(root.label);
			putLabelToNode(root.label, DAGraph.labelSetToNotationMap, DAGraph.LabelSize, labelNodeCount++);
			parentChildrenMap.put(root.parentNode, tempChildren);
		}
		
		if(this.levelTree.containsKey(level)){
			HierarchyNode tempNode = new HierarchyNode();
			// fill-in the information of tempNode
			tempNode.label = root.label;
			tempNode.level = level;
			tempNode.childNumber = childNumberOwn;
			
			this.levelTree.get(level).add(tempNode);
		}
		else if(root.label != null){
			HierarchyNode tempNode = new HierarchyNode();
			// fill-in the information of tempNode
			tempNode.level = level;
			tempNode.label = root.label;
			tempNode.childNumber = childNumberOwn;
			
			ArrayList<HierarchyNode> tempChildren = new ArrayList<HierarchyNode>();
			tempChildren.add(tempNode);
			this.levelTree.put(level, tempChildren);
		}

		int childNumber = 0;
		for(HierarchyNode next: tree){
			if(next.parentNode == root.label){
				childNumber++;
				traverseTree(next, tree, level+1, childNumber);
			}
		}
	}
	
	public void fillInNodeInfo() {
		 int globalSlotSize = 1;
		// traversing the tree for updating "num of children" information
		for(int lev: this.levelTree.keySet()){
			int maxNumOfChildInThisLevel = 0;
			ArrayList<HierarchyNode> tempNodeList = this.levelTree.get(lev);
			
			// calculate MC for each level
			for(HierarchyNode n: tempNodeList){
				if(parentChildrenMap.containsKey(n.label)){
					n.numOfChild = parentChildrenMap.get(n.label).size();
					if(n.numOfChild > maxNumOfChildInThisLevel)	maxNumOfChildInThisLevel = n.numOfChild;
				}
				else{
					n.numOfChild = 0;
				}
			}
			// finding MC for the level lev, now set MC for each node of that level
			for(HierarchyNode n: tempNodeList){
				n.maxNumOfChildInSameLevel = maxNumOfChildInThisLevel;
			}
			if (maxNumOfChildInThisLevel % 2 == 0)
				globalSlotSize *= (maxNumOfChildInThisLevel+1);
			else globalSlotSize *= maxNumOfChildInThisLevel;
		}
		
		System.out.println("Global Slot Size : " + globalSlotSize);
		
		/*
		 * calculating slot size for each node in the tree, 
		 * parent node can find it's childrens' ss by dividing it's own SS by it's num of children
		 * */ 
		int level = 0;
		if(this.levelTree.containsKey(level))// setting root node info
			this.levelTree.get(level).get(0).slotSize = globalSlotSize;
		else{
			level = 1;
			this.levelTree.get(level).get(0).slotSize = globalSlotSize;
		}
		for(int lev: this.levelTree.keySet()){
			ArrayList<HierarchyNode> tempNodeList = this.levelTree.get(lev);// nodes of level "lev" = 1, 2, ..
			
			for(HierarchyNode n: tempNodeList){
				if(parentChildrenMap.containsKey(n.label)){
					int ownSlotSize = n.slotSize;
					ArrayList<HashSet<String>> tempChildLabelList = parentChildrenMap.get(n.label);
					if(this.levelTree.containsKey(lev+1))
					{
						ArrayList<HierarchyNode> tempChildList = this.levelTree.get(lev+1);
						for(HierarchyNode childNode: tempChildList){
							if(tempChildLabelList.contains(childNode.label))
							{
								childNode.numOfSiblings = n.numOfChild;
								if(n.numOfChild % 2 == 0)
									childNode.slotSize = ownSlotSize / (n.numOfChild+1);
								else childNode.slotSize = ownSlotSize / n.numOfChild;
							}
						}
					}
					
				}
			}
		}
		
		/*
		 * calculating offset = slot size/2 and "index = (childNum -1 ) x slot size + offset" 
		 * for each node in the tree, 
		 * */ 
		for(int lev: this.levelTree.keySet()){
			ArrayList<HierarchyNode> tempNodeList = this.levelTree.get(lev);// nodes of level "lev" = 1, 2, ..
			
			for(HierarchyNode n: tempNodeList){
//				n.slotSize *= slotWidth;
				n.slotSize *= (slotWidth);
				n.offSet = n.slotSize / 2;
			}
		}
		
		/*
		 * calculating child/node number in the corresponding level = parent node number in parent's level + SS (child) x (CN(child)-1)
		 *  
		 * if MC > Num of Sib, child Number should be (CN-1)x(MC/Num of Sib) 
		 * 
		 * */ 
		for(int lev: this.levelTree.keySet()){
			ArrayList<HierarchyNode> tempNodeList = this.levelTree.get(lev);// nodes of level "lev" = 1, 2, ..
			
			for(HierarchyNode par: tempNodeList){
				if(parentChildrenMap.containsKey(par.label)){
					int parNodeNumber = par.nodeNumberInTheLevel;// for the 1st level, initially it is 0, which doesn't require any special treatment
					ArrayList<HashSet<String>> tempChildNodeList = parentChildrenMap.get(par.label);
					if(this.levelTree.containsKey(lev+1))
					{
						ArrayList<HierarchyNode> tempChildList = this.levelTree.get(lev+1);
						for(HierarchyNode childNode: tempChildList){
							if(tempChildNodeList.contains(childNode.label))
							{
								int CN = childNode.childNumber-1;
								
								if(childNode.maxNumOfChildInSameLevel > childNode.numOfSiblings)
									CN = (int) (CN*((double)childNode.maxNumOfChildInSameLevel/(double)childNode.numOfSiblings));
								
								if(childNode.numOfSiblings % 2 == 0 && childNode.childNumber > childNode.numOfSiblings/2)
									CN += 1;
																	
								childNode.nodeNumberInTheLevel = (int) (parNodeNumber + childNode.slotSize/slotWidth * (CN));
							}
						}
					}
					
				}
			}
		}
		
	}
	
	public void printTreeLevel()
	{
		
		/*
		 * in our tree implementation, we know how many levels will be there and how many nodes 
		 * will reside in each of the level
		 * 
		 * 1. Total #Slot = Prod(Max(child node in each label))
		 * 2. Length slot = Max size of a label
		 ****** have a little bit of lackings:  3. Individual slot size of each "label character" is Total #Slot / (number of node in that level + 1)
		 ****** have a little bit of lackings: 4. Index = (child number -1) * slot size for that level : child number is 1, 2, 3 if there are 3 children
		 * L = level number, N = number of node in that level for that particular branch, NC = Number of child in that level, C = child number, GSS = global slot size (default for root : 1)
		 * MC : Maximum number of child for the nodes in a particular level (default is 1), and always an odd number, if even then add 1 always with it
		 *                          Root [L:0, NC:1, C:1, MC:1, GSS:1] {1} 
		 *                           |
		 *                           A [L:1, NC:3, C:1, MC:3, GSS : GSSxMC = 3 {1*3}]
		 *                           |
		 *    ---------------------------------------------
		 *    |                      |                     |
		 *    B [L:2, N:0, C : 1]    C[L:2, N:3, C : 2]    G [L:2, NC:4, C : 3, MC: 4+1, GSS = 15 {1*3*5}]
		 *                           |                     |
		 *                     ---------------       ---------------
		 *                     |      |      |		|     |    |	|
		 *                     D      E      F      H     I    J    K  
		 *                            |   N=3 for D, E, F      |       [L:3, NC:2, C : 3, MC: 3, GSS = 45 {1*3*5*3}] 
		 *                           ---                      ---      
		 *                     		|   |                    |   |
		 *                          N   O                    L   M    [L:4, NC:0, C:2, MC:1, GSS=] {1*3*5*3*1}
		 *                     
		 *                                                            
		 *                                                            
		 * Now we know GSS = 45, in the second run, SS = GSS/N initially
		 * WS = white space                                 
		 *                                                           
		 *                          Root [ws=0, SS = GSS = 45, Offset = SS/2 = 22.5, next level SS = SS/NC = 45/1 = 45, Index of R = 0x45+22.5 = (C-1)xSS+Offset]  
		 *                           |
		 *                           A   [ws=0[parent ws+(c-1)ss], SS = 45, Offset = SS/2 = 22.5, Next level SS = SS/NC = 45/3 = 15, Index of A = 0x45+22.5 = (C-1)xSS+Offset]
		 *                           |
		 *    ---------------------------------------------
		 *    |                      |                     |
		 *    B                      C                     G [ws=0+2x15, SS = 15, Offset = SS/2 = 7.5, Index of G = 2x15+7.5 = (C-1)xSS+Offset, for this node, next level Next level SS = SS/NC(+1 for even NC) = 15/5=3]
		 *                           |                     |
		 *                     ---------------       ---------------
		 *                     |      |      |		|     |    |	|
		 *                     D      E      F      H     I    J    K  [ws=30+3x3,SS = 3, Offset = SS/2 = 1.5, Index of J = 3x3+1.5 = CxSS+Offset]
		 *                            |                        |        [Since NC was even, index calculation has a catch, for 1st half children Index = (C-1)xSS+Offset, for next half Index = CxSS+Offset]
		 *                           ---                      ---       childNumberInTheLevel of k is 15, J is 14, I is 12, H is 11      
		 *                     		|   |                    |   |		childNumberInTheLevel of O is 15 x (2-1) + 3 x (2-1) + 1 x (3-1) = 15 + 3 + 1
		 *                          N   O                    L   M      why: O, E, C are 2nd child and A is 1st = SS (A) x (CN(A)-1) + SS (C) x (CN(C)-1) + SS (E) x (CN(E)-1) + SS (O) x (CN(O)-1) = parent Number + SS (child) x (CN(child)-1)   
		 *																   = 45 x 0 + 15 + 3 + 1 = 18, though it should be 21, in case of MC > Num of Sib, child Number should be (CN-1)x(MC/Num of Sib) 
		 *                      											Therefore, = 45 x 0 + 15 x 1 + 3 x ((2-1)x5/3) + 1 x ((2-1)x3/2) = 0 + 15 + 5 + 1 = 21 or 21.5
		 * */ 
		HierarchyNode root = new HierarchyNode(null, null);
//		traverseTree(root, tree, 0, 1);
//		fillInNodeInfo(tree);
		/*
		 * I have replaced the above two lines with the following for loop
		 * */
		for(ArrayList<HierarchyNode> subT : this.subTree){
			this.levelTree = new HashMap<Integer, ArrayList<HierarchyNode>>(); // resetting of this one is required for each of the (sub)tree in the forest
//			labelNodeCount = 0;// resetting of this one is required for each of the (sub)tree in the forest 
			traverseTree(root, subT, 0, 1);
			fillInNodeInfo();
			System.out.println("Slot width, Label size in DAG, label Scale " + slotWidth + " " + DAGraph.LabelSize + " " + labelScale );
//			printLegends();
			printAllLevels();
		}
		
		
//		System.out.println(this);
	}

	/*
	 * this will print the mapping between hashset of labels and set notations we used to print in the tree
	 * we need max set notation size = LabelSize and max label set size = maxSetSize (the static value)
	 * */ 
	
	public void printLegends() {
		int col1Width = DAGraph.LabelSize;
		int col2Width = maxSetSize*3;
		String headerCol1 = "Notation";
		String headerCol2 = "Label Name";
		String setNotation = "";
		int extraLen = 12;
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
		
		HashMap<HashSet<String>, String> sorted = new HashMap<HashSet<String>, String>();
		sorted = sortHashMap(sorted, DAGraph.labelSetToNotationMap);
		
		for(HashSet<String> labset : sorted.keySet())
		{
			String Col2Data = labset.toString();
			setNotation = sorted.get(labset);
			// padding space after notation
			int setNotLength = setNotation.length();
			for(int I = 0; I < col1Width - setNotLength; I++)
				setNotation += " ";
			// padding space after label name
			for(int I = 0; I <col2Width - labset.size()*3; I++)
				Col2Data += " ";
			System.err.println("|   " + setNotation + " |   " + Col2Data + "  |");
			// print the horiz line to indicate the end of a row
			System.err.println(horizLine);
		}
	}

	public HashMap<HashSet<String>, String> sortHashMap(HashMap<HashSet<String>, String> sorted, HashMap<HashSet<String>, String> labelSetToNotationMap) {
		sorted = 
				labelSetToNotationMap.entrySet().stream()
			    .sorted(Entry.comparingByValue())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));
		
		return sorted;
	}

	public void printAllLevels(){
		
		for(int lev: this.levelTree.keySet()){
			ArrayList<HierarchyNode> tempNodeList = this.levelTree.get(lev);// nodes of level "lev" = 1, 2, ..
			
			printHorizBar(tempNodeList, lev);
			if(lev == 1)
				printBar(tempNodeList, lev, 0, 1);// upper bar, last 1 for root printing
			printBar(tempNodeList, lev, 0, 0);// upper bar, last 0 for no root printing
			printBar(tempNodeList, lev, 0, 0);// upper bar, last 0 for no root printing
			
			int printedSpaceSoFar = 0;
			
			for(HierarchyNode node : tempNodeList){

				for(int I = printedSpaceSoFar; I < slotWidth*node.nodeNumberInTheLevel; I++ ){
					if(redTree) System.err.print(" ");
					else System.out.print(" ");
					printedSpaceSoFar++;
				}

				for(int I = 0; I < node.offSet; I++){
					if(redTree) System.err.print(" ");
					else
						System.out.print(" ");
				}
				
				printedSpaceSoFar += node.offSet;
//				System.out.print(printedSpaceSoFar);
				
				if(redTree) System.err.print(DAGraph.labelSetToNotationMap.get(node.label));
				else
					System.out.print(DAGraph.labelSetToNotationMap.get(node.label));
				
				// print extra spaces if node.label length is less than slotWidth
				for(int I = 0; I <slotWidth-DAGraph.LabelSize; I++)
					if(redTree) System.err.print(" ");
					else System.out.print(" ");
				
				printedSpaceSoFar += slotWidth;
			}
			// after each level, print a new line i.e. go to next line
			if(redTree) System.err.println();
			else System.out.println();

			printBar(tempNodeList, lev, 1, 0);// lower bar, last 0 for no root printing
		}
	}
	
	// if bar is lower and number of child is > 1, then only print the bar otherwise avoid by replacing with a " "
	public void printBar(ArrayList<HierarchyNode> tempNodeList, int level, int upperLower, int rootPrint){ // upper = 0, lower = 1
		
		boolean NoVerBar = true; 
			
		int printedSpaceSoFar = 0;
		
		for(HierarchyNode node : tempNodeList){
			for(int I = printedSpaceSoFar; I < slotWidth*node.nodeNumberInTheLevel; I++ ){
				if(redTree) System.err.print(" ");
				else System.out.print(" ");
				
				printedSpaceSoFar++;
			}
			for(int I = 0; I < node.offSet; I++){
				if(redTree) System.err.print(" ");
				else System.out.print(" ");
			}
			
			printedSpaceSoFar += node.offSet;
			
			String verBar1 = "";
			String verBar2 = "";
			int nodeLabelLength = DAGraph.LabelSize;

			for(int V = 0; V < (nodeLabelLength-1)/2; V++){
				verBar1 += " ";
				verBar2 += " ";
			}
			
			
			String verBar = "";
			
			if(rootPrint == 1) 	verBar = verBar1 + "{}" + verBar2;
			else{
				if(upperLower == 0){
					verBar = verBar1 + "|" + verBar2;
					NoVerBar = false;
				}
				else{// if lower bar
					if(node.numOfChild < 1)
					{
						verBar = verBar1 + " " + verBar2;
					}
					else{
						verBar = verBar1 + "|" + verBar2;
						NoVerBar = false;
					}
				}
			}
			
			if(nodeLabelLength % 2 == 0)
				verBar += " ";////////////  this is added because we used "(nodeLabelLength-1)/2" instead of "(nodeLabelLength)/2" for the bars
			
			// print extra spaces if node.label length is less than slotWidth
			for(int I = 0; I <slotWidth-nodeLabelLength ; I++){
				verBar += " ";
			}
			
			printedSpaceSoFar += slotWidth;
			
			/*
			 * if for any level, there is no multiple child oriented node, 
			 * then no vertical bar will be printed and to avoid printing only a blank line
			 * */
			
			if(upperLower == 0 || (upperLower == 1 && NoVerBar == false)){
				if(redTree) System.err.print(verBar);
				else System.out.print(verBar);
			}
			else{
				verBar = verBar1 + " " + verBar2;
				if(nodeLabelLength % 2 == 0)
					verBar += " ";////////////  this is added because we used "(nodeLabelLength-1)/2" instead of "(nodeLabelLength)/2" for the bars
				if(redTree) System.err.print(verBar);
				else System.out.print(verBar);
			}
		}
		// after each level, print a new line i.e. go to next line
		if(redTree) System.err.println();
		else System.out.println();

	}
		
		
	public void printHorizBar(ArrayList<HierarchyNode> tempNodeList, int level){
		String horizBar = "";
		boolean doneOffset = false;// this is required to avoid multiple offsets of white space in 1 level
		int nextNodeToPrint = 0;
		boolean printHorizBar = false;
		int nodeNum = 0;
		while(nodeNum < tempNodeList.size())
		{
			HierarchyNode node = tempNodeList.get(nodeNum);
			
			if(doneOffset == false)
			{
				for(int I = 0; I < node.offSet; I++){
					horizBar += " ";
					doneOffset = true;
				}
			}

			// if we have some children missing in the left side, so print space for them
			for(int I = nextNodeToPrint; I < node.nodeNumberInTheLevel; I++)// for each empty slot
			{
					for(int J = 0; J < slotWidth; J++)// print " " of "slot size" times
						horizBar += " ";
			}

			if(node.numOfSiblings > 1 && node.childNumber == 1)// for multiple child of a node, for it's 1st child start printing "-" upto all child
			{	
				nodeNum += (node.numOfSiblings-1);// if for node num = 0, there were 3 siblings, the last sibling is 2 (3-1)
				HierarchyNode lastSiblingNode = tempNodeList.get(nodeNum);// get that sibling to get its childnumber in the level
//				System.out.println(lastSiblingNode);
				/*
				 * print some space = half of the length of the node 
				 * */				

				for(int I = 0; I < (DAGraph.LabelSize-1)/2; I++)// just to start from the middle of the slot
					horizBar += " ";
				
				
				/*
				 * if there are 6 siblings and if node childNum in the level is 0 and last siblings child number in 
				 * the level is 5, then 0-5 i.e. 6 slots to be filled with "-"
				 * 
				 * */
				for(int I = node.nodeNumberInTheLevel; I < lastSiblingNode.nodeNumberInTheLevel; I++) 
				{
					for(int J = 0; J < slotWidth; J++)// print " " of "slot size" times
						horizBar += "-";
				}
				if(node.numOfSiblings % 2 != 0)// for odd num of siblings we need 1 extra slot of "-" print
					for(int J = 0; J < slotWidth; J++)// print " " of "slot size" times
						horizBar += "-";
				// The following two for loops 1 for slotWidth/2 num of "-" and 2nd for " ", we get a slot that way we avoid 1 iteration in the above loop which should be up to I <= lastSiblingNode.nodeNumberInTheLevel 
				for(int J = 0; J < (slotWidth+1)/2; J++)// print " " of "slot size" times
					horizBar += "-";
				for(int J = 0; J < slotWidth/2; J++)// print " " of "slot size" times
					horizBar += " ";

				printHorizBar = true;
//				System.out.print(node.nodeNumberInTheLevel + " " +lastSiblingNode.nodeNumberInTheLevel);
			}
//			else if(node.numOfSiblings == 1){// the node is the only child of its parent so print space for it
//				for(int J = 0; J < slotWidth; J++)// print " " of "slot size" times
//					horizBar += " ";
//			}
			
			nodeNum++;
			
			nextNodeToPrint = tempNodeList.get(nodeNum-1).nodeNumberInTheLevel + 1;
		}
		// after each level, print a new line i.e. go to next line
		if(printHorizBar == true){
			
			if(redTree) System.err.println(horizBar);
			else System.out.println(horizBar);
		}
	}


	public static int countNodesInNormalPrint = 0;
	public void printTreeNormally(HierarchyNode root)
	{
		System.out.println(root.label + " <--- " + root.parentNode);
		countNodesInNormalPrint++;
		for(HierarchyNode next: this.tree){
			if((next.parentNode == null && root.label == null) || (next.parentNode == root.label))
				printTreeNormally(next);
		}
	}
	
	public void printTreeSequentially()
	{
		int countNodes = 0;
		for(HierarchyNode node: this.tree){
			System.out.println(node.label + " ----> " + node.parentNode + "\n");
			countNodes++;
		}
		System.out.println("Total num of node : " + countNodes);
	}

	public String toString()
	{
		String str = "<Child> -> <Parent>\n";
			   str += "----------------------\n\n"; 
		
	   
		for(Integer n: this.levelTree.keySet()){
			ArrayList<HierarchyNode> nodeList = this.levelTree.get(n);
			for(HierarchyNode ln: nodeList)
				str += ln + "\n";
			str += "\n";
		}
		return str;
	}
	
}

	


