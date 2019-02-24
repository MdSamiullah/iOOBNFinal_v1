package learning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import ANTLR_NPP.NPPvisitForDAG;
import learning.DAGraph2.Node2;
import learning.HierarchyTree.HierarchyNode;
import learning.HierarchyTree2.HierarchyNode2;

public class HierarchyTree2{
	
	public static int labelScale = 2;// to enlarge the tree, increase the value
	public static boolean redTree = false;// to print tree in black, put false
	public static int horizBarSize = 1;
	public static int upVertBarSize = 2;
	public static int lowVertBarSize = 1;
	
	public static HashMap<HashSet<String>, ArrayList<HashSet<String>>> parentChildrenMap = new HashMap<HashSet<String>, ArrayList<HashSet<String>>>(); 

	public static int labelNodeCount = 0;// This will be used to generate string for label nodes. Its max number will be = DA.LabelSize 
	public static int maxSetSize = 0;// this is now required to print the legend table
	public static int slotWidth = 0; // this depends on the DAGraph construction, not on the tree printing or construction
	
	public class HierarchyNode2{
		HashSet<String> label;
		HashSet<String> parentNode;
		
		int level;
		int numOfSlots;// how many slot will this node achieves
		int slotStart;// what is the start index of the slots for this node in the global slot range
		int numOfChild;// how many children this node has
		int childNumber;// what number of child is this node of its parent
		int numOfSiblings; // this is the number of children of its parent
		
		public HierarchyNode2()
		{
			this.label = new HashSet<String>();
			this.parentNode = null;
			
			this.level = 0;
			this.numOfSlots = 0;
			this.slotStart = 0;
			this.numOfChild = 0;
			this.childNumber = 1;// child number will start from 1
			this.numOfSiblings = 1;
		}
		
		public HierarchyNode2(HashSet<String> label, HashSet<String> parentNode)
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
			str += "Label " + this.label + " Parent " + this.parentNode + " level " + this.level + " num of child " + this.numOfChild 
					+ " num of sibling " + this.numOfSiblings + " child Number " + this.childNumber;

			return str; 
		}
	}
	
	ArrayList<HierarchyNode2> tree;
	ArrayList<ArrayList<HierarchyNode2>> subTree;
	HashMap<Integer, ArrayList<HierarchyNode2>> levelTree;// node level vs nodes mapping
	int totalSlots = 0;
	
	public HierarchyTree2()
	{
		this.tree = new ArrayList<HierarchyNode2>();
		this.subTree = new ArrayList<ArrayList<HierarchyNode2>>(); 
		this.levelTree = new HashMap<Integer, ArrayList<HierarchyNode2>>();
		this.totalSlots = 0;
	}
	
	// this method will traverse the "tree" and find for multiple roots and construct separate trees and store in subTree arrayList
	public void separateTrees(HierarchyNode2 root, ArrayList<HierarchyNode2> tempTree){
		for(HierarchyNode2 next: this.tree){
			if(next.parentNode == root.label){
				if(root.label == null){
					tempTree = new ArrayList<HierarchyNode2>();
					this.subTree.add(tempTree);
				}
				tempTree.add(next);
				separateTrees(next, tempTree);
			}
		}
	}
	
	public void constructForest(HashSet<HashSet<String>> labelSet)
	{
		HashMap<Integer, ArrayList<HierarchyNode2>> partitionLabelSet = new HashMap<Integer, ArrayList<HierarchyNode2>>();
		
		int maxSize = 0;
		int minSize = 0;
		
		// separating the label set 
		for(HashSet<String> labSet: labelSet){
			int size = labSet.toString().length();
			
			if(size > maxSize)	maxSize = size;
			else if(size < minSize) minSize = size;
			
			HierarchyNode2 hNode = new HierarchyNode2(labSet, null);// initially parent is null
			if(partitionLabelSet.containsKey(size)){
				partitionLabelSet.get(size).add(hNode);
			}
			else{
				ArrayList<HierarchyNode2> tempList = new ArrayList<HierarchyNode2>();
				tempList.add(hNode);
				partitionLabelSet.put(size, tempList);
			}
		}
		
		maxSetSize = maxSize;
		
		/*
		 * remember this is how many space/character will require for each slot to be represented
		 * which is not same as num of slots for a node. This value is fixed for any node in the tree
		 * however, the num of slot per node is different and normally upper level nodes will have more slots in each node
		 * of course if a node has no children then its number of slot in this implementation will be 1 no matter
		 * what level it belongs to
		 * */ 
		slotWidth = DAGraph2.LabelSize * labelScale;
		
		/*
		 * based on the size based partition, now we will find immediate parents (with smallest size difference)
		 * e.g. for "AG", "ACG" and "ACFG" both being it's ancestor in the tree, 
		 * but immediate parent will be "ACG" 
		 * */
		int n = minSize;
		while(n < maxSize){// because we don't need to find parent of max size set
			if(partitionLabelSet.containsKey(n)){
				ArrayList<HierarchyNode2> tempChildLabelList = partitionLabelSet.get(n);
				for(HierarchyNode2 potenChild: tempChildLabelList){// for each of the label set of size n
					boolean done = false;
					int m = n + 1;
					
					while(m <= maxSize){// check n+1, n+2 ... maxSize labels until we find a superset of the child
						if(partitionLabelSet.containsKey(m)){
							ArrayList<HierarchyNode2> tempParentLabelList = partitionLabelSet.get(m);
							
							/*
							 * for each of the label of size > n, check for superset
							 * if found then break for that particular child label
							 * */
							for(HierarchyNode2 potenParent: tempParentLabelList){  
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
//						System.out.println("A node with no matching parent in the tree " + potenChild);
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
		DAGraph2 temp = new DAGraph2();
		String notation = temp.generateString(labelNodeCount, 65);// 65 for upper case, 97 for lower
		for(int I = 0; I < labelWidth - notation.length(); I++)
			notation = "0" + notation;// just for padding 0 with the notation
		
		labelSetToNotationMap.put(label, notation);
	}	
	
	/*
	 * Tree traversing will traverse the tree in DFS order to put some information in the nodes of the tree (or trees)
	 * It will not change any global variables
	 * */
	public void traverseTree(HierarchyNode2 root, ArrayList<HierarchyNode2> tree, int level, int childNumberOwn)
	{ 			
		if(parentChildrenMap.containsKey(root.parentNode)){
			putLabelToNode(root.label, DAGraph2.labelSetToNotationMap, DAGraph2.LabelSize, labelNodeCount++);
			parentChildrenMap.get(root.parentNode).add(root.label);
		}
		else if(root.label != null)
		{
			ArrayList<HashSet<String>> tempChildren = new ArrayList<HashSet<String>>();
			tempChildren.add(root.label);
			putLabelToNode(root.label, DAGraph2.labelSetToNotationMap, DAGraph2.LabelSize, labelNodeCount++);
			parentChildrenMap.put(root.parentNode, tempChildren);
		}
		
		if(this.levelTree.containsKey(level)){
			HierarchyNode2 tempNode = new HierarchyNode2();
			// fill-in the information of tempNode
			tempNode.label = root.label;
			tempNode.level = level;
			tempNode.childNumber = childNumberOwn;
			tempNode.parentNode = root.parentNode;
			
			this.levelTree.get(level).add(tempNode);
		}
		else if(root.label != null){
			HierarchyNode2 tempNode = new HierarchyNode2();
			// fill-in the information of tempNode
			tempNode.level = level;
			tempNode.label = root.label;
			tempNode.childNumber = childNumberOwn;
			tempNode.parentNode = root.parentNode;
			
			ArrayList<HierarchyNode2> tempChildren = new ArrayList<HierarchyNode2>();
			tempChildren.add(tempNode);
			this.levelTree.put(level, tempChildren);
		}

		int childNumber = 0;
		for(HierarchyNode2 next: tree){
			if(next.parentNode == root.label){
				childNumber++;
				traverseTree(next, tree, level+1, childNumber);
			}
		}
	}
	
	public static ArrayList<Integer> levelsInTheTree; 
	
	public void topDownTraversal() {// this will count the number of leaf nodes
		
		levelsInTheTree = new ArrayList<Integer>(); 
		for(int lev: this.levelTree.keySet()){
			levelsInTheTree.add(lev);
			ArrayList<HierarchyNode2> tempNodeList = this.levelTree.get(lev);
			
			for(HierarchyNode2 n: tempNodeList){
				if(parentChildrenMap.containsKey(n.label)){
					ArrayList<HashSet<String>> childrenLabel = parentChildrenMap.get(n.label);  
					n.numOfChild = childrenLabel.size();
					// update the number of sibling info
					if(this.levelTree.containsKey(lev+1)){
						ArrayList<HierarchyNode2> tempPotentialChildrenNodeList = this.levelTree.get(lev+1);
						int childCount = 1;
						for(HashSet<String> ch : childrenLabel){// for each of the child node label, add number of sibling info
							for(HierarchyNode2 potCh : tempPotentialChildrenNodeList){
								if(potCh.label == ch){
									potCh.numOfSiblings = n.numOfChild;// num of sibling = parents num of children
									potCh.childNumber = childCount++;
									break;
								}
							}
						}
					}
				}
				else{
					n.numOfChild = 0;
					
					if(this.levelTree.containsKey(lev+1)){// just to make sure that no leaf nodes will be added to the last level 
						HierarchyNode2 sudoChild = new HierarchyNode2();
						sudoChild.label.add("psudo");
						if(n.label.contains("psudo"))	n.label = n.parentNode;
						sudoChild.parentNode = n.label;
						
						this.levelTree.get(lev+1).add(sudoChild);
					}
					
					this.totalSlots++;// for each leaf nodes there will be a slot in the display
				}
			}
		}
		
		
		
	}
	
	public void bottomUpTraversal() {// this will count the number slots per node
		// sorting the "levelsInTheTree" list in descending order
		Collections.sort(levelsInTheTree);
		Collections.reverse(levelsInTheTree);
		for(int lev: levelsInTheTree){// level 4, 3, 2, 1
			ArrayList<HierarchyNode2> tempNodeList = this.levelTree.get(lev);
			ArrayList<HierarchyNode2> tempChildNodeList = null;
			if(lev != levelsInTheTree.get(0))// if the level is not the bottom most level which is the 1st level in the descending order sorted list
				tempChildNodeList = this.levelTree.get(lev+1);// this will be required for counting children slots in the next level
			for(HierarchyNode2 n: tempNodeList){
				if(parentChildrenMap.containsKey(n.label)){
					int countChildSlots = 0;
					
					for(HashSet<String> c: parentChildrenMap.get(n.label)){
						for(HierarchyNode2 tc : tempChildNodeList){
							if(tc.label == c)
								countChildSlots += tc.numOfSlots;
						}
					}
					n.numOfSlots = countChildSlots;
				}
				else{// if the node is not found in the map, i.e. it has no children, assign 1 slot to it
					n.numOfSlots = 1;
				}
			}
		}
	}
	
	public MultiRowString makeSlot(String content, HierarchyNode2 node)
	{
		int numOfSlot = node.numOfSlots;
		MultiRowString slot = new MultiRowString();
		String emptySlot = "";
		String nodeContent = "";
		String upVertBar = "";
		String lowVertBar = "";
		String horizBar = "";
		
		for(int I = 0; I < slotWidth; I++)	emptySlot += " ";
		// we will check if printIndex is available to print. if not then keep printing empty slots and increase the printIndex
		while(printDoneIndex[printIndex] == 1){
			nodeContent += emptySlot;
			upVertBar += emptySlot;
			lowVertBar += emptySlot;
			horizBar += emptySlot;
			
			printIndex += 1;
		}

		int spaceAllocated = numOfSlot * slotWidth;
		
		int extraSpace = spaceAllocated - content.length();
		int extraSpaceForBars = spaceAllocated - 1;// length of a slot is 1
		
		// content slot formation
		for(int I = 0; I < (extraSpace+1)/2; I++)// if odd number of extraSpaces, then add bigger half 1st
			nodeContent += " ";
		nodeContent += content;
		for(int I = 0; I < extraSpace/2; I++)// if odd number of extraSpaces, then add bigger half 1st
			nodeContent += " ";
		
		// upper vertical bar slot formation
		for(int I = 0; I < (extraSpaceForBars+1)/2; I++)// if odd number of extraSpaces, then add bigger half 1st
			upVertBar += " ";
		upVertBar += "|";
		for(int I = 0; I < extraSpaceForBars/2; I++)// if odd number of extraSpaces, then add bigger half 1st
			upVertBar += " ";
		
		/*
		 * horiz bar slot formation. 
		 * 	If num of sib > 1, child Num 1 : start "-" when "|" should start and go with "-" for the remaining of the slots
		 *  If num of sib > 1, child Num == num of sib : go from the begining with "-" and stop when "|" should print, and put spaces for the remaining
		 *  if num of sib > 1, else (i.e. 1 < childNum < num of sib)
		 *  
		 *  if num of sib <= 1, put " " for slots
		 * */ 
		
		if(node.numOfSiblings == 0){// all spaces
			for(int I = 0; I < spaceAllocated; I++)
				horizBar += " ";
		}
		else if(node.numOfSiblings == 1){// all spaces
			horizBar = upVertBar;
		}
		else{// if have more than 1 siblings
			if(node.childNumber == 1){
				for(int I = 0; I < spaceAllocated/2; I++)
					horizBar += " ";
				for(int I = 0; I < (spaceAllocated+1)/2; I++)
					horizBar += "-";
			}
			else if(node.childNumber == node.numOfSiblings){
				for(int I = 0; I <= (spaceAllocated+1)/2; I++)
					horizBar += "-";
				for(int I = 0; I < spaceAllocated/2-1; I++)
					horizBar += " ";
			}
			else{
				for(int I = 0; I < spaceAllocated; I++)
					horizBar += "-";
			}
		}

		// lower vertical bar slot formation
		for(int I = 0; I < (extraSpaceForBars+1)/2; I++)// if odd number of extraSpaces, then add bigger half 1st
			lowVertBar += " ";
		if(node.numOfChild >= 1)
			lowVertBar += "|";
		else lowVertBar += " ";
		for(int I = 0; I < extraSpaceForBars/2; I++)// if odd number of extraSpaces, then add bigger half 1st
			lowVertBar += " ";
		

		// forming the multirow-slot
		slot.content = nodeContent;
		
		for(int I = 0; I < upVertBarSize; I++){
			slot.upperVertBar.add(upVertBar);
		}
		
		slot.horizBar.add(horizBar);
		
		for(int I = 0; I < lowVertBarSize; I++){
			slot.lowerVertBar.add(lowVertBar);
		}
		
		return slot;
	}
	
	public class MultiRowString{
		String content;
		ArrayList<String> upperVertBar;// if the static variable "vetrBarSize == 1", there will be only 1 string in the list
		ArrayList<String> lowerVertBar;// if the static variable "horizBarSize == 1", there will be only 1 string in the list
		ArrayList<String> horizBar;
		
		public MultiRowString(){
			this.content = "";
			this.upperVertBar = new ArrayList<String>();
			this.lowerVertBar = new ArrayList<String>();
			this.horizBar = new ArrayList<String>();
		}
		
		public void add(MultiRowString another){
//			System.out.println("this started \n" + this + "this ended ");
//			System.out.println("another started \n" + another + "another ended ");
			if(this.upperVertBar.size() == 0)	this.upperVertBar = another.upperVertBar;
			else{
				ArrayList<String> tempupperVertBar = new ArrayList<String>();
//				System.out.println(this);
				for(int I = 0; I < another.upperVertBar.size(); I++){
					tempupperVertBar.add(this.upperVertBar.get(I).concat(another.upperVertBar.get(I)));
				}
				this.upperVertBar = tempupperVertBar;
			}
			
			this.content += another.content;
			
			if(this.lowerVertBar.size() == 0) this.lowerVertBar = another.lowerVertBar;
			else{
				ArrayList<String> templowerVertBar = new ArrayList<String>();
				for(int I = 0; I < this.lowerVertBar.size(); I++){
					templowerVertBar.add(this.lowerVertBar.get(I).concat(another.lowerVertBar.get(I)));
				}
				this.lowerVertBar = templowerVertBar;
			}
			
			if(this.horizBar.size() == 0) this.horizBar = another.horizBar;
			else{
				ArrayList<String> temphorizBar = new ArrayList<String>();
				for(int I = 0; I < this.horizBar.size(); I++){
					temphorizBar.add(this.horizBar.get(I).concat(another.horizBar.get(I)));
				}
				this.horizBar = temphorizBar;
			}
		}
		
		public String toString(){
			String str = "";
			
			for(String horizB : this.horizBar){
				str += horizB+"\n";
			}
			
			for(String upB : this.upperVertBar){
				str += upB+"\n";
//				System.out.println("up bar " + upB);
			}
			str += this.content + "\n";
			for(String lowB : this.lowerVertBar){
				str += lowB+"";
//				System.out.println("low bar " + lowB);
			}
			
			return str;
		}
	}
	
	public static int printDoneIndex [] = null;// don't start using it unless "firstLevDone" is true i.e. "printHierarchyLevelWise" 
	public static int printIndex = 0;
	public void printHierarchyLevelWise()
	{	
		boolean indexInitialized = false;
		for(int lev: this.levelTree.keySet()){
			
			printIndex = 0; // in each level, the printing should start from 0
//			String row = "";
			MultiRowString row = new MultiRowString();
			ArrayList<HierarchyNode2> nodesInTheLevel = this.levelTree.get(lev);
			
			if(indexInitialized == false){
				printDoneIndex = new int[nodesInTheLevel.get(0).numOfSlots];// the 1st level node will contain the max num of required slots
				indexInitialized = true;
				// root printing
				
				int SlotSize = nodesInTheLevel.get(0).numOfSlots * slotWidth;
				String root = "";
				for(int I = 0; I <(SlotSize-1)/2; I++)
					root += " ";
				System.out.println(root+"[X]");
			}
						
			for(HierarchyNode2 node : nodesInTheLevel){
				
//				System.out.print(node.label +" " + Display(printDoneIndex));
				
				String notation = DAGraph2.labelSetToNotationMap.get(node.label);
				if(parentChildrenMap.containsKey(node.label)){// non-leaf
//					System.out.print(makeSlot(notation, node.numOfSlots));
					row.add(makeSlot(notation, node));
					printIndex += node.numOfSlots;
				}
				else{// index is printed, keep track of the index
					 // "null" because, "psudo nodes will not be in the map"
					if(!node.label.contains("psudo") && node.label != node.parentNode){// we will not print psudo nodes
						if(notation == null) {
							notation = " ";
						}
						
//						System.out.print(makeSlot(notation, node.numOfSlots));
						row.add(makeSlot(notation, node));
						printDoneIndex[printIndex++] = 1;
					}
				}
				
			}
			System.out.println(row);
			
		}
	}
	
	
	
	
	/*
	 * Our 2nd way of printing the hierarchy is simple. We will traverse the tree twice level-wise. in the first traversal, 
	 * from level 1 down to nth level, we will check how many leaf nodes are there in the tree
	 * 
	 * In the second phase, i.e., bottom up traversal, we will count how many slots per node we will be allocated
	 * as an example, for the below tree 
	 * 
Distribution	#Nodes	Description
Level 4	4	All leaf nodes
		one slot one leaf
		1, 1, 1, 1
Level 3	7	5 leaves and 2 non leaf both with two child
		5 slots for 5 leaves 2x2 = 4 slots
		1, 2, 1, 1, 1, 2, 1
Level 2	3	1 leaf and 2 non leaf (having 3 and 4 children)
		1 for 1 leaf, 3 (4 slots required as of the level 3's 2 children oriented
		one of the nodes are here) anf 4 for non leaf nodes
		(5 slots required as of the level 3's 2 children oriented
		one of the nodes are here)
		1, 4, 5
Level 1	1	1 non leaf node having 3 child
		(10 slots will require as of from level 2, 1st one req 1,
		2nd one requires 4 and 3rd one require 5 slots)

  
	 * 
	 * The last tricks, whenever we will get a leaf node, we will add a psudo child leaf with " " node label in it 
	 * having number of slots = 1
	 * */
	
	public String Display(int[] printDoneIndex2) {
		String setStr = "";
		
		for(int n : printDoneIndex2)
			setStr += n + " ";
		
		return setStr;
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
		HierarchyNode2 root = new HierarchyNode2(null, null);
		/*
		 * I have replaced the above two lines with the following for loop
		 * */
		printLegends();
//		printExtendedLegends();
//		HashMap<HashSet<String>, String> sorted = new HashMap<HashSet<String>, String>();
//		sorted = sortHashMap(sorted, DAGraph2.labelSetToNotationMap);
		
//		if(sorted == null)	System.out.println("Sorted map is empty !!!");
		printExtendedLegendsParticular(100, DAGraph2.labelSetToNotationMap.keySet());
		labelNodeCount = 1;
		for(ArrayList<HierarchyNode2> subT : this.subTree){
			this.levelTree = new HashMap<Integer, ArrayList<HierarchyNode2>>(); // resetting of this one is required for each of the (sub)tree in the forest
			labelNodeCount--;
			/*
				this is added on 04-02-2019 to avoid unnecessary increment of node label for next subtree 
				like if one tree ends in EA, next was starting from EC 
				instead of EB. Thus initializing the variable by 1, and decreasing once per call fixed the problem
			*/  
			traverseTree(root, subT, 0, 1);
			topDownTraversal();
			bottomUpTraversal();
			
			/*preparing a set of notations for printing legend before the tree is printed*/
			Set<HashSet<String>> setOfNotationsLegend = makeASetofNotationsForLegend(subT);
			printExtendedLegendsParticular(100, setOfNotationsLegend);
			printHierarchyLevelWise();
		}
		
	}
	
	private Set<HashSet<String>> makeASetofNotationsForLegend(ArrayList<HierarchyNode2> subT) {
		Set<HashSet<String>> setOfNotations = new HashSet<HashSet<String>>();
		for(HierarchyNode2 node : subT) {
			setOfNotations.add(node.label);
		}
		
		return setOfNotations;
	}

	/*
	 * this will print the mapping between hashset of labels and set notations we used to print in the tree
	 * This one can split the content into several lines as per requirement and the screen size as provided
	 * we need max set notation size = LabelSize and max label set size = maxSetSize (the static value)
	 * */
	
	public void printExtendedLegendsParticular(int windowSize, Set<HashSet<String>> setOfNotations) {
//		windowSize = 100;
		/*This number can be tuned as per the width of the page or screen. 
							   It must be greater than col1Width*/ 
		int col1Width = DAGraph2.LabelSize;
		int col2Width = maxSetSize;
		String headerCol1 = "Notation";
		String headerCol2 = "Label Name";
		String setNotation = "";
		
		int extraLen = 12;
		String horizLine = "";
		/*
		 * Col1 is fixed so only the effect of window size will be in col 2
		 * */
		if( col1Width < headerCol1.length() ) col1Width = headerCol1.length();
		if( col2Width < headerCol2.length() ) col2Width = headerCol2.length();
		if(windowSize > 0) col2Width = windowSize-col1Width;
		
		int horizLineLength = 0; //windowSize;
		if(windowSize > 0) horizLineLength = windowSize;
		else horizLineLength = col1Width + col2Width + extraLen;
		
		for(int I = 0; I < horizLineLength+10; I++)
			horizLine += "-";
		
		/* now print header*/
		// print 1st line
		System.out.println(horizLine);
		// padding extra space after headerCol1 and headerCol2
		for(int I = 0; I <col1Width - headerCol1.length(); I++)
			headerCol1 += " ";
		// padding space after label name
		int hdcl2W = headerCol2.length();
		for(int I = 0; I < col2Width - hdcl2W; I++){
			headerCol2 += " ";
		}
		
		System.out.println("    " + headerCol1 + " |   " + headerCol2 + "  ");
		// print header end line and data entry 1st line
		System.out.println(horizLine);
		
//		System.out.println("The sorted map is " + sorted);
		
		/*
		 * Following two ArrayList of ArrayLists are actually ArrayList of tuples
		 * that means nested arraylists contain only 2 items in them.
		 * 1st one is the notation and 2nd one is the col2data of the legend table
		 * this is done just to avoid the complexity of map and map sorting
		 * */
		
		ArrayList<ArrayList<String>> tempMapOfLegends = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> tempMapOfExtendedLegends = new ArrayList<ArrayList<String>>();
		
		for(HashSet<String> labset: setOfNotations)
		{
			String extendedCol2Data = "";
			String Col2Data = "";
			if(labset != null) {
				Col2Data = labset.toString();
				String arr[] = labset.toString().replace("[", "").replace("]", "").split(",");
				String a = arr[0].trim();
				extendedCol2Data = DAGraph2.labelSetToDAGFileNamesMap.get(a).replace("dag_", "");
				for(int I = 1; I < arr.length; I++) {
					a = arr[I].trim();
					extendedCol2Data += ", " + DAGraph2.labelSetToDAGFileNamesMap.get(a).replace("dag_", "");
				}
			}
			else{
				labset = new HashSet<String>();
			}
			int offsetNextLines = 3 + col1Width;
			Col2Data = breakIntoLinesOfPreferedWidth(Col2Data, col2Width, offsetNextLines);
			extendedCol2Data = breakIntoLinesOfPreferedWidth(extendedCol2Data, col2Width, offsetNextLines);

			setNotation = DAGraph2.labelSetToNotationMap.get(labset);
			if(setNotation != null) {
				ArrayList<String> temp = new ArrayList<String>();
				ArrayList<String> temp2 = new ArrayList<String>();
				temp.add(setNotation);	temp2.add(setNotation);
				temp.add(Col2Data);
				temp2.add(extendedCol2Data);
				
				tempMapOfLegends.add(temp);
				tempMapOfExtendedLegends.add(temp2);
			}
			
		}
		
		ArrayList<ArrayList<String>> sorted1;
		ArrayList<ArrayList<String>> sorted2;
		
		sorted1 = sortArrayListOn1stIndex(tempMapOfLegends);
		sorted2 = sortArrayListOn1stIndex(tempMapOfExtendedLegends);
		
		int indSorted2 = 0;
		for(ArrayList<String> key: sorted1)
		{
			
			setNotation = key.get(0);
			String Col2Data = key.get(1);
			String extendedCol2Data = sorted2.get(indSorted2++).get(1);
			if(setNotation != null){
				// padding space after notation
				int setNotLength = setNotation.length();
				for(int I = 0; I < col1Width - setNotLength; I++)
					setNotation += " ";
				// padding space after label name
				int COLDATALEN = Col2Data.length();// if somehow coldatalen is bigger than col2Width, no space will be added
				for(int I = 0; I <col2Width - COLDATALEN; I++)
					Col2Data += " ";
//				Col2Data = breakIntoLinesOfPreferedWidth(Col2Data, col2Width, offsetNextLines);
				System.out.println("    " + setNotation + " |   " + Col2Data + "  ");
				System.out.println("\t     |   " + extendedCol2Data);
				// print the horiz line to indicate the end of a row
				System.out.println(horizLine);
			}
		}
	}


	/*
	 * this will print the mapping between hashset of labels and set notations we used to print in the tree
	 * This one can split the content into several lines as per requirement and the screen size as provided
	 * we need max set notation size = LabelSize and max label set size = maxSetSize (the static value)
	 * */
	
	public void printExtendedLegends() {
		int windowSize = 100;/*This number can be tuned as per the width of the page or screen. 
							   It must be greater than col1Width*/ 
		int col1Width = DAGraph2.LabelSize;
		int col2Width = maxSetSize;
		String headerCol1 = "Notation";
		String headerCol2 = "Label Name";
		String setNotation = "";
		String extendedCol2Data = "";
		int extraLen = 12;
		String horizLine = "";
		/*
		 * Col1 is fixed so only the effect of window size will be in col 2
		 * */
		if( col1Width < headerCol1.length() ) col1Width = headerCol1.length();
		if( col2Width < headerCol2.length() ) col2Width = headerCol2.length();
		if(windowSize > 0) col2Width = windowSize-col1Width;
		
		int horizLineLength = 0; //windowSize;
		if(windowSize > 0) horizLineLength = windowSize;
		else horizLineLength = col1Width + col2Width + extraLen;
		
		for(int I = 0; I < horizLineLength+10; I++)
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
		
		System.err.println("    " + headerCol1 + " |   " + headerCol2 + "  ");
		// print header end line and data entry 1st line
		System.err.println(horizLine);
		
		HashMap<HashSet<String>, String> sorted = new HashMap<HashSet<String>, String>();
		sorted = sortHashMap(sorted, DAGraph2.labelSetToNotationMap);
		
		if(sorted == null)	System.out.println("Sorted map is empty !!!");
//		System.out.println("The sorted map is " + sorted);
		
		for(HashSet<String> labset : sorted.keySet())
		{
			String Col2Data = "";
			if(labset != null) {
				Col2Data = labset.toString();
				String arr[] = labset.toString().replace("[", "").replace("]", "").split(",");
				String a = arr[0].trim();
				extendedCol2Data = DAGraph2.labelSetToDAGFileNamesMap.get(a).replace("dag_", "");
				for(int I = 1; I < arr.length; I++) {
					a = arr[I].trim();
					extendedCol2Data += ", " + DAGraph2.labelSetToDAGFileNamesMap.get(a).replace("dag_", "");
				}
			}
			else{
				labset = new HashSet<String>();
			}
			int offsetNextLines = 3 + col1Width;
			Col2Data = breakIntoLinesOfPreferedWidth(Col2Data, col2Width, offsetNextLines);
			extendedCol2Data = breakIntoLinesOfPreferedWidth(extendedCol2Data, col2Width, offsetNextLines);
			setNotation = sorted.get(labset);
			if(setNotation != null){
				// padding space after notation
				int setNotLength = setNotation.length();
				for(int I = 0; I < col1Width - setNotLength; I++)
					setNotation += " ";
				// padding space after label name
				int COLDATALEN = Col2Data.length();// if somehow coldatalen is bigger than col2Width, no space will be added
				for(int I = 0; I <col2Width - COLDATALEN; I++)
					Col2Data += " ";
//				Col2Data = breakIntoLinesOfPreferedWidth(Col2Data, col2Width, offsetNextLines);
				System.err.println("    " + setNotation + " |   " + Col2Data + "  ");
				System.err.println("\t     |   " + extendedCol2Data);
				// print the horiz line to indicate the end of a row
				System.err.println(horizLine);
			}
		}
	}
	
	private String breakIntoLinesOfPreferedWidth(String OldString, int prefWidth, int offsetNextLines) {
		String newStr = "";
		char arr[] = OldString.toCharArray();
		String nextLineOffsetStr = "";
		for(int J = 0; J < offsetNextLines; J++)	nextLineOffsetStr += " ";
		
		for(int I = 0; I < arr.length; I++) {
			newStr += arr[I];
			if((I+1)%prefWidth==0) {
				if(arr[I] != ',' && arr[I] != ' ')	newStr += "";
				newStr += "\n";
				newStr += nextLineOffsetStr+"  |   ";
			}
		}
		
		return newStr;
	}

	public void printLegends() {
		
		int col1Width = DAGraph2.LabelSize;
//		int col2Width = maxSetSize*DAGraph2.LabelSize*3;
		int col2Width = maxSetSize;
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
		sorted = sortHashMap(sorted, DAGraph2.labelSetToNotationMap);
		
		if(sorted == null)	System.out.println("Sorted map is empty !!!");
//		System.out.println("The sorted map is " + sorted);
		
		for(HashSet<String> labset : sorted.keySet())
		{
			String Col2Data = "";
			if(labset != null) {
				Col2Data = labset.toString();
			}
			else{
				labset = new HashSet<String>();
			}
			setNotation = sorted.get(labset);
			if(setNotation != null){
				// padding space after notation
				int setNotLength = setNotation.length();
				for(int I = 0; I < col1Width - setNotLength; I++)
					setNotation += " ";
				// padding space after label name
				int COLDATALEN = Col2Data.length();
				for(int I = 0; I <col2Width - COLDATALEN; I++)
					Col2Data += " ";
				System.err.println("|   " + setNotation + " |   " + Col2Data + "  |");
				// print the horiz line to indicate the end of a row
				System.err.println(horizLine);
			}
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
	
	public ArrayList<ArrayList<String>> sortArrayListOn1stIndex(ArrayList<ArrayList<String>> unsorted) {
		Collections.sort(unsorted, new Comparator<ArrayList<String>>() {    
	        @Override
	        public int compare(ArrayList<String> o1, ArrayList<String> o2) {
	            return o1.get(0).compareTo(o2.get(0));
	        }               
	});
		return unsorted;
	}

	
	public static int countNodesInNormalPrint = 0;
	public void printTreeNormally(HierarchyNode2 root)
	{
		System.out.println(root.label + " <--- " + root.parentNode);
		countNodesInNormalPrint++;
		for(HierarchyNode2 next: this.tree){
			if((next.parentNode == null && root.label == null) || (next.parentNode == root.label))
				printTreeNormally(next);
		}
	}
	
	public void printTreeSequentially()
	{
		int countNodes = 0;
		for(HierarchyNode2 node: this.tree){
			System.out.println(node.label + " ----> " + node.parentNode + "\n");
			countNodes++;
		}
//		System.out.println("Total num of node : " + countNodes);
	}
	
	
	/*a DAGraph has following info to represent the DAG
	 * HashMap<String, Node2> daGraph;
	 * HashSet<String> neighborSet;// this is required to find roots. So we are not going to add this info for the sake of simplicity
	 * 								  may be later we will deal with it
	 * 
	 * Node2 elements are
		String name;
		HashSet<String> label;
		HashMap<String, HashSet<String>> neighbors;
	*/
//	public static int dagCount = 0;
	public String makeDAGName(HashSet<String> label)
	{
		String name = "";
		boolean doneFirst = false;
		
		for(String namePart : label){
			if(doneFirst == false){
				doneFirst = true;
				name += DAGraph2.labelSetToDAGFileNamesMap.get(namePart);
			}
			else{
				name += "_" + DAGraph2.labelSetToDAGFileNamesMap.get(namePart);
			}
		}
		
		return name;
	}
	
	
	public void constructBNForNode(HashSet<String> labelToCheck, DAGraph2 dag, String directory, String targetDir, String choiceClass) throws IOException {
		DAGraph2 bn = new DAGraph2();
		
		HashMap<String, HashSet<String>> dagmap = new HashMap<String, HashSet<String>>();// node name to the set of child node name which is known as neighbor node 
		
		for(String nodeKey : dag.daGraph.keySet()){
			Node2 tempNode = dag.daGraph.get(nodeKey); 
			DAGraph2.Node2 newNode = bn.new Node2();
			if(tempNode.label.containsAll(labelToCheck)){
				// now remove those edges that don't contain all of the elements/graph labels in "labelToCheck"
				dagmap.put(nodeKey, new HashSet<String>());
				newNode.label.addAll(tempNode.label);
				newNode.name = tempNode.name;
				for(String edgeKey : tempNode.neighbors.keySet())
				{
					HashSet<String> tempEdgeLabels = tempNode.neighbors.get(edgeKey);
					if(tempEdgeLabels.containsAll(labelToCheck)) {
//						System.out.println( "node " + nodeKey + " edge "+ edgeKey + " edge labels " + tempEdgeLabels + " labels to check " + labelToCheck);
						newNode.neighbors.put(edgeKey, tempEdgeLabels);
						dagmap.get(nodeKey).add(edgeKey);
					}
					
				}
				bn.daGraph.put(nodeKey, newNode);
			}
		}
		String fileName = makeDAGName(labelToCheck) + ".txt";
//		System.out.println("The DAG : " + fileName + " : \n" + bn + "\n\n");
		LearningiOOBN lioobn = new LearningiOOBN();
		if(choiceClass.equalsIgnoreCase("C")){
			lioobn.DAGWriterForOOBN(directory, dagmap, fileName, targetDir, "");
		}
	}
	
	public void constructBNsOfTree(ArrayList<HierarchyNode2> subT, DAGraph2 dag, String directory, String targetDir, String choiceClass) throws IOException {
		for(HierarchyNode2 node : subT){
			constructBNForNode(node.label, dag, directory, targetDir, choiceClass);
		}
	}

	public void constructBNs(DAGraph2 dag, String directory, String targetDir, String choiceClass) throws IOException {
		/*
		 * for each of the "subTree" stored in ArrayList of ArrayList of HierarchyNode2 (which contains HashSet<String>),
		 * we will traverse the trees (i.e iterate through the arrayLists) in its nodes and check in the super most DAG for 
		 * nodes and edges having label same or superset of the hierarchy node's label name
		 * */
		
		for(ArrayList<HierarchyNode2> subT : this.subTree){
			constructBNsOfTree(subT, dag, directory, targetDir, choiceClass);
			
		}
	}
	
	
	public String toString()
	{
		String str = "<Child> -> <Parent>\n";
			   str += "----------------------\n\n"; 
		
	   
		for(Integer n: this.levelTree.keySet()){
			ArrayList<HierarchyNode2> nodeList = this.levelTree.get(n);
			for(HierarchyNode2 ln: nodeList)
				str += ln + "\n";
			str += "\n";
		}
		return str;
	}

	
	
}

	


