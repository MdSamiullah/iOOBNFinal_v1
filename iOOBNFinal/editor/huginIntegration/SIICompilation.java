// final correct version with map structure but neighbor info is redundant and removed
// this version also have clique numbers in string format rather than integer
package huginIntegration;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.editor.components.BasicNode;
import com.editor.components.CodeGenerator;
import com.editor.components.XMLParserIOOBN;
import com.mxgraph.frames.FrameInstance;
import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

import ANTLR_NPP.NPPCompiler;
import COM.hugin.HAPI.Domain;
import COM.hugin.HAPI.ExceptionHugin;

/*
 * instance I1 : C (X=X1, Y=Y1; Z1=Z) {...}
Class C must have (at least) two input nodes: X and Y. For instance I1, X corresponds
to node X1, and Y corresponds to node Y1. Class C must also have (at least)
one output node: Z. The output clone corresponding to Z for instance I1 is given
the name Z1.
 * 
 * */


public class SIICompilation {
	
	timingInfo tInfo;
	public static long huginCompilation;

	public static boolean onlyDisplatTime = true;
	public static int folds = 5;
	
	public static int globalCliqueCount = 1;
	public static HashMap<String, myClique> myGlobalCliques = new HashMap<String, myClique>();
	public static HashMap<Integer, String> myGlobalCliquesKeys = new HashMap<Integer, String>();
	// this will contain the original clique numbers and should be mapped using
    //same key as myJTCliques along with the JT id
	
	public static ArrayList<RefLink> pseudoRefLinks = null;
	
	public static Duration timeCodeGeneration = Duration.ZERO;
	
	public static HashSet<String> instanceList = new HashSet<String>();
	// while making RL, we get the mapping instance Input node name to it's RL's
	// other end variable name
	public static HashMap<String, String> inputNodeList = new HashMap<String, String>();
	
	public static int rowIndex = 0;
	public static int numOfRow = 0;
	public static Object[][] dataTable;
	public static BasicNode runningNode;
	public static ArrayList<HashMap<String, mxCell>> NodesInExternalClasses; 
	// this is an arraylist of Hash containing the
	// info/attributes of super class and classes for instances
	public static HashMap<String, mxCell> TempNodeTable;
	
	public static Stack <String> cliqueStack2BPruned = new Stack<String>();
	
	public static HashMap<String, Integer> jtID2GlobalNumMap = new HashMap<String, Integer>();

	public static ArrayList<JTConnection> sortedConnections;
	
	public static String JTGrabOrCreate = "";
	public static Duration timeToCreateJTs= Duration.ZERO;



	public class myClique {
		public ArrayList<Integer> cliqueNumbers;
		public ArrayList<String> cliques;
		public HashMap<String, ArrayList<String>> myJTSeparators;

		public myClique() {
			this.cliqueNumbers = new ArrayList<Integer>();
			this.cliques = new ArrayList<String>();
			this.myJTSeparators = new HashMap<String, ArrayList<String>>();
		}

		public myClique(ArrayList<Integer> cliqueNumbers, ArrayList<String> clique) {
			this.cliqueNumbers = new ArrayList<Integer>();
			this.cliques = new ArrayList<String>();
			this.myJTSeparators = new HashMap<String, ArrayList<String>>();

			this.cliqueNumbers = cliqueNumbers;
			this.cliques = clique;
		}

		public String toString() {
			String str = "";
			str += this.cliqueNumbers + ":" + this.cliques;
			return str;
		}

		public void appendDeletedCliquesNumbers(myClique anotherClique) {
			this.cliqueNumbers.addAll(anotherClique.cliqueNumbers);
		}
	}

	public class JTStructure {
		/*
		 * the "global number" is a numbering base/start position to efficiently
		 * indexing cliques in a JT during the joining process of JTs in SIIC
		 * algorithm. As an example, say 2 JTs A and B having 5 (1, 2, 3, 4, 5)
		 * and 3 (1, 2, 3) cliques, respectively. While joining A and B, we keep
		 * A's cliques and their numbers intact by putting globalNum of A = 0.
		 * But B's globalNum will be last index of the clqs in A i.e. 5. Hence,
		 * B's clqs numbers will be 6, 7 and 8
		 * 
		 * This technique seems to me efficient because in this way I don't need
		 * to re-number all cliques in the JT (rather only adding base number
		 * i.e. globalNum in the algorithm will give me new numbers) and
		 * renumbering of the cliques in the connections constructed from ref
		 * Link
		 */
		public String keyID;
		public HashMap<String, myClique> myJTCliques;
		public HashMap<String, Integer> componentIndex;

		public JTStructure() {
			this.keyID = "";
			this.myJTCliques = new HashMap<String, myClique>();
			this.componentIndex = new HashMap<String, Integer>();
		}

		public void join(JTStructure jt2, JTConnection jTC) {
//			System.out.println("JT1 " + this + "\nJT2 " + jt2 + "\nThe connection is " + jTC);
			this.keyID = this.keyID + "," + jt2.keyID;
			int FirstJTSize = this.myJTCliques.size();

//			System.out.println("JT1:\n" + this + "\nJT2:\n" + jt2 + "\nAdded by " + jTC);

			// adding all the member clique info and separator info

			this.myJTCliques.putAll(jt2.myJTCliques);
			// this.myJTSeparators.putAll(jt2.myJTSeparators);// we don't need
			// it any more

			// adding the connection's separator to the both cliques in the
			// corresponding terminal JTs cliques

			// this part is for 1st terminal
			if(this.myJTCliques.containsKey(jTC.cliqueTerminal1)) {
				if (this.myJTCliques.get(jTC.cliqueTerminal1).myJTSeparators == null) {
					this.myJTCliques.get(jTC.cliqueTerminal1).myJTSeparators.put(jTC.cliqueTerminal2,
							jTC.connectionSeparators);
				} else {
					this.myJTCliques.get(jTC.cliqueTerminal1).myJTSeparators.put(jTC.cliqueTerminal2,
							jTC.connectionSeparators);
				}
			}
			else {
				System.out.println("\n\n\n something is not going fine in terminal 1 of joining 2 cliques !!!!!!!!!!!!!!!!!!!!!!!! \n\n");
			}

			// this part is for 2nd terminal
			if(this.myJTCliques.containsKey(jTC.cliqueTerminal2))
				if (this.myJTCliques.get(jTC.cliqueTerminal2).myJTSeparators == null) {
					this.myJTCliques.get(jTC.cliqueTerminal2).myJTSeparators.put(jTC.cliqueTerminal1,
							jTC.connectionSeparators);
				} else {
					this.myJTCliques.get(jTC.cliqueTerminal2).myJTSeparators.put(jTC.cliqueTerminal1,
							jTC.connectionSeparators);
				}
			else {
				System.out.println("The terminal 2 clique in the JT connection "+ jTC.cliqueTerminal2 + "\nThe Cliques are \n"+ this.myJTCliques + "\n\n\n something is not going fine in terminal 2 of joining 2 cliques !!!!!!!!!!!!!!!!!!!!!!!! \n\n");
			}

			// this part is for updating the component index
			this.componentIndex.putAll(jt2.componentIndex);

			// System.out.println("JT after Joining \n" + this);

			this.componentIndex = findComponentJTs(this);
		}

		public String toString() {
			String str = "The JT ID = <" + this.keyID + ">\n";
			// for (String n : this.myJTCliques.keySet()) {
			// str += "\n" + n + ": ";
			// for (String S : this.myJTCliques.get(n)) {
			// str += S + " ";
			// }
			// }
			//
			// str += "\n";
			//
			// for (String key : this.myJTSeparators.keySet()) {
			// str += key + " ";
			// str += this.myJTSeparators.get(key) + "\n";
			// }
			//
			// str += "The JT Components " + this.componentIndex + "\n";
			//
			// str += "\nThe JT structure in another form: \n<clq> --- [sep] ---
			// <clq> \n\n";

			// int count = 1;
			//
			// HashMap<String, ArrayList<String>> flagPrintDone = new
			// HashMap<String, ArrayList<String>>();
			//
			// for (String key : this.myJTSeparators.keySet()) {
			// for (String p : this.myJTSeparators.get(key).keySet()) {
			// /*
			// * A <-> B is presented as A - B and B - A
			// * After printing A - B, to stop B - A
			// * in the list of B add A, and always check before adding
			// * whether any key = B contains p = A or not, if doesn't contain
			// * then only add it
			// * */
			// if(!(flagPrintDone.get(key)!= null &&
			// flagPrintDone.get(key).contains(p)))
			// {
			// if(flagPrintDone.get(p) == null) flagPrintDone.put(p, new
			// ArrayList<String>());
			// flagPrintDone.get(p).add(key);
			// flagPrintDone.get(key);
			// str += count++ + ". <" + this.myJTCliques.get(key) + "> --- ";
			// if (this.myJTSeparators.get(key) == null ||
			// this.myJTSeparators.get(key).size() == 0)
			// str += "[ ] --- <";
			// else
			// str += this.myJTSeparators.get(key).get(p) + " ";
			// str += " --- <";
			//
			// str += this.myJTCliques.get(p) + ">\n";
			// }
			// }
			// }

			// BFS based printing of JT info
			int count = 1;
			ArrayList<String> visited = new ArrayList<String>();

			Queue<String> Q = new LinkedList<String>();
			String firstKey = this.myJTCliques.keySet().stream().findFirst().get();
			Set<String> setOfKey = this.myJTCliques.get(firstKey).myJTSeparators.keySet();

			if (setOfKey.size() > 0) {
				String src = setOfKey.toArray()[0].toString();

				visited.add(src);
				Q.add(src);

				while (!Q.isEmpty()) {
					String src1 = Q.remove();
					for (String next : this.myJTCliques.get(src1).myJTSeparators.keySet()) {
						if (!visited.contains(next)) {
							Q.add(next);
							visited.add(next);
							str += count++ + ". <" + this.myJTCliques.get(src1) + "> --- ";
							str += this.myJTCliques.get(src1).myJTSeparators.get(next) + " ";
							str += " --- <";
							str += this.myJTCliques.get(next) + ">\n";
						}
					}
				}
			} else
				str += this.myJTCliques;
			return str;
		}
	}

	public class JTConnection {
		public ArrayList<String> TerminalClique1;// = new ArrayList<String>();
		public ArrayList<String> TerminalClique2;// = new ArrayList<String>();
		public String jtTerminal1;// contains the index of terminal1 JT in the
									// list of JT
		public String jtTerminal2;// contains the index of terminal2 JT in the
									// list of JT
		public String cliqueTerminal1;// contains the index of terminal clique 1
										// in the list cliques in the JT
		public String cliqueTerminal2;// contains the index of terminal clique 2
										// in the list cliques in the JT
		public ArrayList<String> connectionSeparators;
		public boolean done;

		public JTConnection() {
			this.TerminalClique1 = new ArrayList<String>();
			this.TerminalClique2 = new ArrayList<String>();
			this.jtTerminal1 = "";
			this.jtTerminal2 = "";
			this.cliqueTerminal1 = "";
			this.cliqueTerminal2 = "";
			this.connectionSeparators = new ArrayList<String>();
			this.done = false;
		}

		public void setJTConnection(ArrayList<String> TerminalClique1, ArrayList<String> TerminalClique2,
				String jtTerminal1, String jtTerminal2, String clq1, String clq2,
				ArrayList<String> connectionSeparators) {
			this.TerminalClique1 = TerminalClique1;
			this.TerminalClique2 = TerminalClique2;
			this.jtTerminal1 = jtTerminal1;
			this.jtTerminal2 = jtTerminal2;
			this.cliqueTerminal1 = clq1;
			this.cliqueTerminal2 = clq2;
			if (this.connectionSeparators != null) {
				this.connectionSeparators.addAll(connectionSeparators);
			} else {
				this.connectionSeparators = new ArrayList<String>();
				this.connectionSeparators.addAll(connectionSeparators);
			}
			this.done = false;
		}

		public String toString() {
			String str = "";
			str += " Terminal 1 info (i) JT = "+this.jtTerminal1 + ",(ii) Clq = " + this.cliqueTerminal1 + " : " + this.TerminalClique1 + " --- <"
					+" the sep info "+  this.connectionSeparators + "> --- " + " Terminal 2 info (i) JT = "+ this.jtTerminal2 + ", (ii) Clq = " + this.cliqueTerminal2 + " : "
					+ this.TerminalClique2 + " done = " + this.done + "\n";

			return str;
		}

		public void updateJTConnectionWeight(JTConnection connection) {
			this.connectionSeparators.addAll(connection.connectionSeparators);
		}
	}

	public ArrayList<String> CommonItems(ArrayList<String> cliqElem, ArrayList<String> neighborClqElem) {
		ArrayList<String> sep = new ArrayList<String>();
		/* some optimization can be done here */
		for (int i = 0; i < cliqElem.size(); i++) {
			for (int j = 0; j < neighborClqElem.size(); j++) {
				if (cliqElem.get(i).equalsIgnoreCase(neighborClqElem.get(j))) {
					sep.add(cliqElem.get(i));
					break;
				}
			}
		}
		return sep;
	}

	public HashMap<String, ArrayList<String>> extractSeparators(ArrayList<String> cliqElem,
			HashMap<String, ArrayList<String>> neighborsElem) {
		HashMap<String, ArrayList<String>> sepList = new HashMap<String, ArrayList<String>>();

		for (String i : neighborsElem.keySet()) {

			ArrayList<String> sep = new ArrayList<String>();

			sep = CommonItems(cliqElem, neighborsElem.get(i));

			sepList.put(i, sep);
		}

		return sepList;
	}

	// here delimiter is "." and ":" and "space" i.e. "[.: ]+"
	
   /*
    * in this function the filename = JT_className is used to grab the JT from files if it is already in there
    * otherwise it will create one using "LoadAndPropagateOOBN lap = new LoadAndPropagateOOBN();" the 
    * lap.LAP (fileNames, classNames, null); where null is given for case file and
    * fileNames = fileNames.add("C:\\Users\\msamiull\\workspace\\jgraphx-master\\NoBCGD.oobn") or "NoBCGD.oobn";
	* className = classNames.add("NoBCGD") or "NoBCGD";
    */
	public JTStructure getJTInfoFileString(String dir, String extraDirForSrc, String filename, String delimiter, String instanceName,
			String InstanceNumber) throws IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ExceptionHugin {
		
		
		JTStructure jt = new JTStructure();
		String CliqueKeyPrefix = instanceName + InstanceNumber;

		File file = new File(dir+extraDirForSrc+filename);
		File srcFile = new File(dir+extraDirForSrc+filename.replace("JT_", "").replace(".txt","")+".oobn");
		
		// if the class is not the compiling class itself and the JT for instance class doesn't exist
		// or if the file exist and was last modified before the modification of the srcFile
		// this is to check if srcFile has been changed after last JT creation
		if(file.exists() == false || file.lastModified() < srcFile.lastModified())
		{// && !instanceName.equalsIgnoreCase("OWN")){
			LoadAndPropagateOOBN lap = new LoadAndPropagateOOBN();
			ArrayList<String> fileNames = new ArrayList<String>();
			String className = filename.replace("JT_", "").replace(".txt", "");
			
			if(!onlyDisplatTime)
				System.out.println("Now compiling using hugin " + className+".oobn");
			ArrayList<String> classNames = new ArrayList<String>();
			classNames.add(className);
			className = dir+extraDirForSrc+className;
			fileNames.add(className+".oobn");
			if(!onlyDisplatTime) System.out.println("Now loading to hugin the file and class " + fileNames + " " + classNames);
			lap.LAP(fileNames, classNames, null, dir+extraDirForSrc);
		}
		
		int bufferSize = 4 * 8 * 1024; // just to increase the buffer size (4 times than deafault) to speed up the performance
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader, bufferSize);
		
//		StringBuffer stringBuffer = new StringBuffer();
//		InputStream input = new BufferedInputStream(new FileInputStream(file));
		String line = "";

		HashMap<String, ArrayList<String>> tempJTClqNeighbors = new HashMap<String, ArrayList<String>>();

		while ((line = bufferedReader.readLine()) != null) {
			// System.out.println(line);
			String[] names = line.split(delimiter);
			String[] nodes = names[1].split(",");
			// don't put any space within clique variables

			// if the class is not the current compiling class (I mean an instance class) then
			// adding instance name with the variables as prefix
			if (!instanceName.equalsIgnoreCase("OWN")) {
				for (int i = 0; i < nodes.length; i++) {
					nodes[i] = instanceName + "_" + nodes[i];
					
					if (inputNodeList.keySet().contains(nodes[i]))
						nodes[i] = nodes[i].replace(instanceName + "_", "");
				}
			}
			ArrayList<Integer> tempClqNum = new ArrayList<Integer>();
			tempClqNum.add(globalCliqueCount++);

			myClique tempClq = new myClique(tempClqNum, new ArrayList<String>(Arrays.asList(nodes)));
			jt.myJTCliques.put(CliqueKeyPrefix + "," + names[0], tempClq);

			myClique tClq = new myClique();
			tClq.cliqueNumbers.add(globalCliqueCount - 1);
			tClq.cliques = new ArrayList<String>();
			tClq.cliques.addAll(tempClq.cliques);

			myGlobalCliques.put(CliqueKeyPrefix + "," + names[0], tClq);
			myGlobalCliquesKeys.put(globalCliqueCount-1, CliqueKeyPrefix + "," + names[0]);

			ArrayList<String> tempNeighbors = new ArrayList<String>();

			for (int i = 2; i < names.length; i++) {
				// started from 2, because splitted 0 & 1 contains number and info of
				// cliques where 2~N contains neighbor info
				tempNeighbors.add(CliqueKeyPrefix + "," + names[i]);
			}
			tempJTClqNeighbors.put(CliqueKeyPrefix + "," + names[0], tempNeighbors);
		}

		// adding neighbor info and separators
		for (String i : jt.myJTCliques.keySet()) {
			HashMap<String, ArrayList<String>> separatorsList = new HashMap<String, ArrayList<String>>();
			ArrayList<String> neighbors = tempJTClqNeighbors.get(i);

			HashMap<String, ArrayList<String>> neighborsElem = new HashMap<String, ArrayList<String>>();

			for (String j : neighbors) {
				neighborsElem.put(j, jt.myJTCliques.get(j).cliques);
			}

			separatorsList = extractSeparators(jt.myJTCliques.get(i).cliques, neighborsElem);
			jt.myJTCliques.get(i).myJTSeparators = separatorsList;
		}

		jt.componentIndex = findComponentJTs(jt);

		if(!onlyDisplatTime) System.out.println(jt);

		return jt;
	}

	public HashMap<String, Integer> findComponentJTs(JTStructure jt) {

		HashMap<String, Integer> componentIndex = new HashMap<String, Integer>(); 
		// the last index will contain the number of
		// trees found +2 becasue 0th index is wasted due to clique numbering
		// starts from 1

		for (String s : jt.myJTCliques.keySet()) {
			componentIndex.put(s, 0);
		}

		// for each of the cliques there will be an entry to trace what tree it
		// belongs to i just put "1" to the set of connected cliques, "2" to
		// another set of connected cliques and so on

		int treeCount = 1;
		int nodeCount = 0; // when this count will be equal to
							// jt.myJTCliques.size() number of cliques in
		// the forest, we need to stop the execution

		Queue<String> clqQueue = null;

		String I = "";

		while (nodeCount < jt.myJTCliques.size()) {
			clqQueue = new LinkedList<>();
			// as the last index will contain the number of trees found, we only
			// need to check up to last-1 elements

			for (String tempI : componentIndex.keySet()) { // this loop finds
															// the min index of
															// the clique node
															// that has not
				// been traversed
				if (componentIndex.get(tempI) == 0) {
					I = tempI;
					break;
				}
			}

			clqQueue.add(I);
			while (!clqQueue.isEmpty()) {
				String frontNode = clqQueue.remove();
				nodeCount++;
				componentIndex.put(frontNode, treeCount);

				// System.out.println("Component info " + componentIndex + " " +
				// jt.myJTSeparators.get(frontNode) + " " + frontNode);

				for (String n : jt.myJTCliques.get(frontNode).myJTSeparators.keySet()) {
					if (componentIndex.get(n) == 0) {
						clqQueue.add(n);
					}
				}

			}

			treeCount++;

		}

		// as the last index will contain the number of trees found
		componentIndex.put("$$##Size##$$", treeCount - 1);
		// +1 because clique numbering starts from 1 instead of 0
		return componentIndex;
	}

	
	/*
	 * This parser will parse an XML file and keep in a list of Basic Node
	 * structures so that class info stored in XML format can be accessed in the
	 * program
	 */

	public void recursiveParserXML(NodeList nodeList, ArrayList<String> tagList, mxCell cell) {

		for (int elem = 0; elem < nodeList.getLength(); elem++) {// for each element
			/*
			 * If the element is a "Node" initiate/create mxCell and BasicNode
			 * and then pass that mxCell in further recursive calls to be
			 * completed. If the element is a "state" add in mxCell and
			 * BasicNode and then pass that mxCell in further recursive calls.
			 * (if prev is null, then create new state List otherwise just add
			 * in List) If the element is a "parent" add in mxCell and BasicNode
			 * and then pass that mxCell in further recursive calls. (if prev is
			 * null, then create new parent List otherwise just add in List) If
			 * the element is a "datarow" add in mxCell and BasicNode and then
			 * pass that mxCell in further recursive calls. (if prev is null,
			 * then create new model otherwise just add in model)
			 */

			Node eNode = nodeList.item(elem); // each element is called as eNode
												// here

			Element eElem = (Element) eNode;

			String[] columnNames;

			NamedNodeMap childAttribList = eElem.getAttributes();
			// System.out.println(eNode.getNodeName() + " " +
			// eNode.getTextContent());
			if (eNode.getNodeName().equalsIgnoreCase("node")) {
				cell = new mxCell();
				runningNode = new BasicNode();
				runningNode.states = new ArrayList<String>(); 
				// this is to remove default constructor's 2 default states

				dataTable = null;
				rowIndex = 0;
				numOfRow = 0;// intentionally given a big number

				for (int attrib = 0; childAttribList != null && attrib < childAttribList.getLength(); attrib++) {
					// for each attrib
					Node aNode = childAttribList.item(attrib); 
					// each element is called as aNode here

					if (aNode.getNodeName().equalsIgnoreCase("id")) {
//						System.out.println("Id of the node is : " + aNode.getTextContent());
						cell.setId(aNode.getTextContent());
					} else if (aNode.getNodeName().equalsIgnoreCase("name")) {
						runningNode.name = aNode.getTextContent();
					} else if (aNode.getNodeName().equalsIgnoreCase("label")) {
						cell.setValue(aNode.getTextContent());
						runningNode.label = aNode.getTextContent();
					} else if (aNode.getNodeName().equalsIgnoreCase("subType")) {
						runningNode.subType = aNode.getTextContent();
					} else if (aNode.getNodeName().equalsIgnoreCase("type")) {
						runningNode.type = aNode.getTextContent();
					} else if (aNode.getNodeName().equalsIgnoreCase("kind")) {
						runningNode.isEIO = Integer.parseInt(aNode.getTextContent());
					}
				}
			}

			else if (eNode.getNodeName().equalsIgnoreCase("state")) {
				for (int attrib = 0; childAttribList != null && attrib < childAttribList.getLength(); attrib++) {
					// for each attrib
					Node aNode = childAttribList.item(attrib); 
					// each element is called as aNode here
					runningNode.states.add(aNode.getTextContent());//
				}
			}

			else if (eNode.getNodeName().equalsIgnoreCase("parent")) {
				for (int attrib = 0; childAttribList != null && attrib < childAttribList.getLength(); attrib++) {
					// for each attrib
					Node aNode = childAttribList.item(attrib); 
					// each element is called as aNode here
					runningNode.parents.add(aNode.getTextContent());
				}
			}

			else if (eNode.getNodeName().equalsIgnoreCase("tuple")) {
				for (int attrib = 0; childAttribList != null && attrib < childAttribList.getLength(); attrib++) {// for
					// each attrib
					Node aNode = childAttribList.item(attrib); 
					// each element is called as aNode here
					String[] tempDataRow = aNode.getTextContent().split("\\$"); 
					// to split with dollar you have to use "\\$"
					
					// for (int l = 0; l < tempDataRow.length; l++)
					// System.out.println(" " + tempDataRow[l]);

					numOfRow = runningNode.parents.size() + runningNode.states.size();
					if (dataTable == null) {
						// columnNames = tempDataRow;
						dataTable = new Object[numOfRow + 1][tempDataRow.length];
					}
					dataTable[rowIndex++] = tempDataRow;

				}
			}

			for (int k = 0; k < tagList.size(); k++) {
				NodeList childNodeList = eElem.getElementsByTagName(tagList.get(k));
				recursiveParserXML(childNodeList, tagList, cell);
			}
			// at this point, mxCell decoration is complete for a particular
			// <Node> tag
			// checking is done or not

			if (eNode.getNodeName().equalsIgnoreCase("node")) {
				if (dataTable != null) {
					columnNames = (String[]) dataTable[0];
					dataTable = Arrays.copyOfRange(dataTable, 1, dataTable.length);
					runningNode.data = new DefaultTableModel(dataTable, columnNames) {
						public boolean isCellEditable(int row, int column) {
							if (row < runningNode.parents.size())
								return false;
							// This causes some cells to be not editable
							else
								return true;
						}

						public void cellBackground(int row, int column) {

						}
					};
				}

				if (mxGraphModel.instanceAddingGoingOn == true) {
					// same might be better if we do for super classes
					cell.setId(FrameInstance.classNInstanceName + "_" + cell.getId());
				}

				cell.setBNInfo(runningNode);

				TempNodeTable.put(cell.getId(), cell);
			}
		}
	}
	
	public String generateCodeHuginCodeAfterRemovingInstance(String fileName, HashMap<String, mxCell> NodeTable) {
		Instant tempCodeGenTime = Instant.now();
		
		String[] fileNameParts = fileName.split("\\\\");
		fileName = fileNameParts[fileNameParts.length - 1];
		String generatedCode = "";
		String allPotential = "";

		// format sample for Input and output specification: "inputs = (mare
		// sire);"
		String inputSpec = "\tinputs = (";
		String outputSpec = "\toutputs = (";

//		System.out.println("Node Table is : " + NodeTable);
		for (String key1 : NodeTable.keySet()) {
			
			mxCell cell = NodeTable.get(key1);
//			System.out.println("key " + key1 + " " + cell.getId());
			if (NodeTable.containsKey(cell.getId()) && !(cell.getId().contains("::") && cell.getId().contains("_"))) {
				BasicNode tempNode = NodeTable.get(cell.getId()).getBNInfo();
				if (tempNode != null) {
					ArrayList<Integer> statesCountStatic = new ArrayList<Integer>();

					ArrayList<Integer> parentStates = new ArrayList<Integer>();
					for (int j = 0; j < tempNode.parents.size(); j++) {
						BasicNode tempParent = NodeTable.get(tempNode.parents.get(j)).getBNInfo();
						parentStates.add(tempParent.states.size());
					}
					int tempStateCount = 1;
					for (int j = 0; j < parentStates.size(); j++) {
						tempStateCount *= parentStates.get(j);
						statesCountStatic.add((Integer) tempStateCount);
					}

					generatedCode += ("\tnode " + tempNode.name + "\n\t{\n");

					generatedCode += ("\t\tlabel= \"" + tempNode.label + "\";\n");
					generatedCode += ("\t\ttype=\"" + tempNode.type + "\";\n");
					generatedCode += ("\t\tsubType=\"" + tempNode.subType + "\";\n");

					if (tempNode.isEIO == 1) {
						inputSpec += (" " + tempNode.name);
					} else if (tempNode.isEIO == 2) {
						outputSpec += (" " + tempNode.name);
					}

					String states = "";
					for (String key : tempNode.states) {
						states += ("\"" + key + "\" ");
					}
					generatedCode += ("\t\tstates=(" + states + ");\n");

					if (tempNode.stateValues.size() > 0) {
						String stateValues = "";
						for (int j = 0; j < tempNode.stateValues.size(); j++)
							stateValues += ("\"" + tempNode.stateValues.get(j) + "\" ");

						generatedCode += ("\t\tstatesValue=(" + stateValues + ");\n");
					}

					generatedCode += "\t}\n";

					String potential = "\tpotential ( " + tempNode.name;

					if (tempNode.parents.size() > 0)
						potential += " | ";

					String parents = "";

					ArrayList<String> parentsList = new ArrayList<String>();
					ArrayList<ArrayList<String>> parentStatesList = new ArrayList<ArrayList<String>>();

					for (int j = 0; j < tempNode.parents.size(); j++) {
						BasicNode tempParent = NodeTable.get(tempNode.parents.get(j)).getBNInfo();

						// do something to add instance name in case parent is
						// from an instance
						String tempInstance = "";
						if (tempNode.parents.get(j).contains("::") && tempNode.parents.get(j).contains("_")) {
							tempInstance = tempNode.parents.get(j);
							tempInstance = (tempInstance.split("::")[1]).split("_")[0];
							// temp will contain instance
							// name, since "class::instance_Node" is the format
							tempInstance += "_";
						}

						parents += (tempInstance + tempParent.name + " ");
						parentsList.add(tempInstance + tempParent.name);
						parentStatesList.add(tempParent.states);
					}

					potential += (parents + " )\n\t{\n");

					String data = "";
					data = "\t\tdata=";

					CodeGenerator cg = new CodeGenerator();

					data += cg.showDataParenthesized(tempNode.data, tempNode.states, parentsList, parentStatesList);

					potential += data;

					potential += ";\n\t}\n";

					allPotential += potential;
				}
			} else {
				/*
				 * add pseudo referential links for output nodes of an
				 * instance to external Output or embedded nodes
				 * this is going to be used in ".class" file 
				 * 
				 * and pseudo ref links are generated only in SIIC compilation 
				 * that means it should be handled from here or after this point
				 * 
				 * split an id of instance node by :: and _ to get the instance name,
				 * then you can create node replica for that particular output node (a
				 * node with kind = 2 is an output node)
				 * 
				 * Along with all these, I also have to create a list of pseudo ref links here 
				 * */ 
				BasicNode tempNode = NodeTable.get(cell.getId()).getBNInfo();
//				System.out.println("node name in instance to be used for psedu ref link " + tempNode.name + " " + tempNode.isEIO);
				if (tempNode != null && tempNode.isEIO == 2) {
						// following split is done to add instance name as
						// prefix to a node of an instance
						String insId = cell.getId();
						String insIdList[] = insId.split("[::_]"); 
						// assuming Asia.class::A_2
						insId = insIdList[insIdList.length - 2];
						// (length - 2) would give me A from  "Asia.class::A_2"
//						System.out.println("Node name used in pseudo ref link " + tempNode.name);
						generatedCode += ("\tnode " + insId + "_" + tempNode.name + "\n\t{\n");

						generatedCode += ("\t\tlabel= \"" + tempNode.label + "\";\n");
						generatedCode += ("\t\ttype=\"" + tempNode.type + "\";\n");
						generatedCode += ("\t\tsubType=\"" + tempNode.subType + "\";\n");

						String states = "";
						for (String key : tempNode.states) {
							states += ("\"" + key + "\" ");
						}
						generatedCode += ("\t\tstates=(" + states + ");\n");

						if (tempNode.stateValues.size() > 0) {
							String stateValues = "";
							for (int j = 0; j < tempNode.stateValues.size(); j++)
								stateValues += ("\"" + tempNode.stateValues.get(j) + "\" ");

							generatedCode += ("\t\tstatesValue=(" + stateValues + ");\n");
						}
						generatedCode += "\n}\n";
						
						/*
						 * Here I have to add info in pseudoRefLinks list to be used later in creating a list of refLinks for
						 * JT connections 
						 * */
						
						RefLink tempRefLink = new RefLink();
						// for pseudo ref links, idSrc and idTarget will be same, as well as nameSrc and nameTarget will be same
						tempRefLink.idSrc = cell.getId();
						tempRefLink.idTarget = cell.getId();
						
						// we added (fileName+"::" +insId+ "_") with the ref links to make sure the format is of the form "<className>::<InstanceId>_<varName>"
						tempRefLink.nameSrc = fileName+"::"+insId + "_" + tempNode.name;
						tempRefLink.nameTarget = fileName+"::"+insId + "_" + tempNode.name;
						
						tempRefLink.isEIOSrc = 2;
						tempRefLink.isEIOTarget = 0;
						
						pseudoRefLinks.add(tempRefLink);
						
						
						/*
						 * we have ignored potential info. Though it should be
						 * added where a calculation needs to be done to convert
						 * potentials from conditional prob (wrt nodes' parent)
						 * to own cpt as the replica nodes will be root node
						 * (nodes with no parents) in the external network.
						 */
				}
			}
		}

		// adding input and output specifications
		String generatedCodePrefix = "class " + fileName.replace(".class", "_own\n{\n");
		// prefix means the part that should come first
		generatedCodePrefix += (inputSpec + " );\n");
		generatedCodePrefix += (outputSpec + " );\n");
		generatedCode = generatedCodePrefix + generatedCode;

		// ------------------------ this time don't add instances
		// -----------------------------
		// hence I have remomed all the codes from original version to add
		// instance info

		// do something to add potential info of some referential but
		// required edge (output of instance to embedded)
		// to allPotential before adding it to generatedCode

		generatedCode += allPotential;

		generatedCode += "\n}";

//		System.out.println(generatedCode);
		Instant tempCodeGenEndTime = Instant.now();
//		System.out.println("Code  (with no instance) generation time individual " + Duration.between(tempCodeGenTime, tempCodeGenEndTime).toMillis());
		timeCodeGeneration = Duration.between(tempCodeGenTime, tempCodeGenEndTime);
		return generatedCode;
	}

	/*
	 * This function works on .class file and check for tag "RefLink" which will be used to extract a list of RL
	 * To avoid the previous bad condition of checking whether an edge is a ref link or a pseudo ref link,
	 * we have to add a new piece of info in the ref-link tag for src and target nodes i.e. whether 
	 * they are input/output or embedded nodes or not 
	 * 
	 * 
	 * */
	public ArrayList<ArrayList<String>> iterativeParserXML4RL(NodeList rLList, ArrayList<String> tagList,
			ArrayList<ArrayList<String>> RefLinkList) {
		
		for (int elem = 0; elem < rLList.getLength(); elem++) {// for each
																// element

			Node eNode = rLList.item(elem); // each element is called as eNode
											// here

			Element eElem = (Element) eNode;

			if (eNode.getNodeName().equalsIgnoreCase("RefLink")) {

				// Assuming Ref Link has only two tags, "source" and "target"
				NodeList sourceList = eElem.getElementsByTagName(tagList.get(0));
				NodeList targetList = eElem.getElementsByTagName(tagList.get(1));

				Element aElem = (Element) sourceList.item(0); 
				// the 1st element of srcList
				Element bElem = (Element) targetList.item(0); 
				// the 1st element of targetList

				NamedNodeMap attribA = aElem.getAttributes();
				NamedNodeMap attribB = bElem.getAttributes();

				Node aNodeId = attribA.item(0); // for id of src node
				Node aNodeName = attribA.item(2);// for name of src node
				Node aNodeKind = attribA.item(1);// for kind of src node

				Node bNodeId = attribB.item(0); // for id of target node
				Node bNodeName = attribB.item(2);// for name of target node
				Node bNodeKind = attribB.item(1);// for kind of target node
				
//				System.out.println(aNodeId + " " + bNodeId + " " + aNodeName + " " + bNodeName);
				
				ArrayList<String> tempRefLinkPair = new ArrayList<String>();

//				if (aNodeName.getTextContent().equalsIgnoreCase(bNodeName.getTextContent())) {
//				System.out.println("The kind of target node in ref link " + bNodeKind.getTextContent() + " " + bNodeName.getTextContent());
				if(bNodeKind.getTextContent().equalsIgnoreCase("1")){// remember in ref link, aNode can be E/I/O
					// a link will be ref link iff they have same name. Atleast
					// in our current implementation, we need to follow this
					// convention. This condition is not correct at all. 
					// If target is an input node i.e. EIO = 1 then it is a pure ref edge
					
					String tempS = aNodeName.getTextContent().split("[\"]")[0];
					tempRefLinkPair.add(tempS);

					String namePartArray[] = bNodeId.getTextContent().split("[::_]");

					String pref = "";
					
					pref = namePartArray[namePartArray.length - 2];
//					
					tempS = bNodeName.getTextContent();// assuming bNodeName format is "main1::Obj0C1_node1" 
					tempRefLinkPair.add(tempS);
					
//					System.out.println("Link pair in iterativeParserXML4RL " + tempRefLinkPair + " " + pref + " " + tempS);

					String instanceName = bNodeId.getTextContent().split("_")[0];

					inputNodeList.put(pref + "_" + bNodeName.getTextContent(), aNodeName.getTextContent());

					instanceList.add(instanceName);
//					System.out.println("Instances added in the list (in if ) " + instanceList);
				}

				else {// the link is not a direct ref link but a causal edge
						// where ref link needs to be added

					/*
					 * previously we assumed that, at a time, src & target both can't belong to instance nodes, this is also wrong indeed
					 * for sure, a psudo ref link can be identified by the kind of src node. If src node kind is 2 and target node kind is 0
					 * then it is a pseudo ref link. Remember, in a pseudo ref link we add a copy of out put node of an instance in the
					 * main class with kind = 0  
					 * */ 

//					if (aNodeId.getTextContent().contains("::") && aNodeId.getTextContent().contains("_")) {
					if(aNodeKind.getTextContent().equalsIgnoreCase("2") && bNodeKind.getTextContent().equalsIgnoreCase("0")){
						String namePartArray[] = aNodeId.getTextContent().split("[::_]");

						String pref = namePartArray[namePartArray.length - 2];
						String tempS = aNodeName.getTextContent();
						tempRefLinkPair.add(pref + "_" + tempS);

						instanceList.add(aNodeId.getTextContent().split("_")[0]);
//						System.out.println("Instances added in the list  (in else ) " + instanceList);
						// tempRefLinkPair.add(tempS);// this line has been
						// replaced by the next one to maintain consistency of
						// node names
						tempRefLinkPair.add(pref + "_" + tempS);
					} else if (bNodeId.getTextContent().contains("::") && bNodeId.getTextContent().contains("_")) {
						// I have done nothing here at the first place assuming that the name
						// difference would take place only in case
						// of output node to external node causal edges and then
						// :: and _ would be in instances which can only be
						// source i.e. aNode
					}
				}

				RefLinkList.add(tempRefLinkPair);
			}
		}
//		System.out.println(inputNodeList);
		return RefLinkList;

	}

	public HashMap<String, JTConnection> createConnections(JTConnection connection,
			HashMap<String, JTConnection> setConnections) {
		// both connectionKeys are same except <A:a, B:b> and <B:b,A:a> i.e.
		// both order are checked to confirm both way direction
		String connectionKey1 = "<$" + connection.jtTerminal1 + ":" + connection.cliqueTerminal1 + "|"
				+ connection.jtTerminal2 + ":" + connection.cliqueTerminal2 + "$>";
		String connectionKey2 = "<$" + connection.jtTerminal2 + ":" + connection.cliqueTerminal2 + "|"
				+ connection.jtTerminal1 + ":" + connection.cliqueTerminal1 + "$>";

		if (setConnections.keySet().contains(connectionKey1)) {
			setConnections.get(connectionKey1).updateJTConnectionWeight(connection);
			// update the existing connections weight

		} else if (setConnections.keySet().contains(connectionKey2)) {
			setConnections.get(connectionKey2).updateJTConnectionWeight(connection);
			// update the existing connections weight
		} else {
			setConnections.put(connectionKey1, connection);
			// even it works for connectionKey2
		}

		// System.out.println("The set of connections " + setConnections);
		return setConnections;
	}

	public String searchClique(JTStructure JT, String var) {
		//System.out.println("Searching for " + var + " in " + JT);
		for (String i : JT.myJTCliques.keySet()) {
			for (String s : JT.myJTCliques.get(i).cliques)
				if (s.equalsIgnoreCase(var)){
//					System.out.println("The index returned is " + i);
					return i;
				}
		}
		System.out.println("The index is null ");
		return null;
	}

	public JTConnection convertRL2Connection(JTConnection connection, ArrayList<String> rL,
			HashMap<String, SIICompilation.JTStructure> JTList) {
		String keyJT = "";
		String keyJTOwn = "";
		JTStructure ownJT;
		/*
		 * If, in a Ref Link, both node name
		 * 		contains "::" then 1st one is the output node of an instance, (we can introduce RefLink structure to efficiently deal with this
		 * 		because, "::" is a vulnerable criteria, better to check for EIO value)
		 * 
		 * 		take the JT of 1st one into consideration to find the clique
		 * 		containing the variable after "_" and take the self JT to find the
		 * 		clique containing the variable after "_" 
		 * else if only 2nd one
		 * contains "::" then 2nd one is the input node of an instance. Find the
		 * variable in JT of the instance 
		 * 
		 * else keep track and take care later
		 */
//		System.out.println("The ref link to be converted now is " + rL);

		SIICompilation.JTStructure instJT = null;
		/*
		 * I think the ref link terminal node format is className::ObjName_variableName or "variableName" , hence the
		 * split with :: will either produce 2 or 1 string. However, the terminal node should be in either of the following
		 * format ObjName_variableName or "variableName" 
		 * */
		String str1[] = rL.get(0).split("::");
		String str2[] = rL.get(1).split("::");
		String class1Name = "";		String instance1Name = "";		String var1Name = "";
		String class2Name = "";		String instance2Name = "";		String var2Name = "";
		
		if(str1.length>1){// i.e. if str1 has :: and _ so lenght will be 2, 1st one is class name 2nd one is "instanceName_varName"
			class1Name = str1[0];
			instance1Name = str1[1].split("_")[0];
			var1Name = str1[1].split("_")[1];
		}
		else{
			var1Name = str1[0];
		}
		if(str2.length>1){// i.e. if str1 has :: and _ so lenght will be 2, 1st one is class name 2nd one is "instanceName_varName"
			class2Name = str2[0];
			instance2Name = str2[1].split("_")[0];
			var2Name = str2[1].split("_")[1];
		}
		else{
			var2Name = str2[0];
		}

		if (rL.get(0).contains("::") && rL.get(1).contains("::")) {
			keyJT = findKeyOfJT(JTList, instance1Name, instance1Name+"_"+var1Name);// format of the function is findKeyOfJT(JTList, JTName, varName)
			instJT = JTList.get(keyJT);
//			System.out.println("The key of the JT is " + keyJT + " hahaha. The class name " + class1Name + " instance name " + instance1Name + " var1Name " + var1Name + "\nfinally the JT is " + instJT );

			keyJTOwn = findKeyOfJT(JTList, "OWN", instance2Name + "_" + var2Name);
			ownJT = JTList.get(keyJTOwn);

			String ind1 = searchClique(instJT, instance1Name + "_" + var1Name);
			String ind2 = searchClique(ownJT, instance2Name + "_" + var2Name);
			// for output to embedded of own, var will be in a format like "A_a"
			// System.out.println("Ind1, Ind2 " + ind1 + ", " + ind2);
			ArrayList<String> tempConnSeparator = new ArrayList<String>();
			tempConnSeparator.add( instance1Name+ "_" + var1Name);// we could add either terminal node name of the RL for the separator without class name, so we choose the 1st one

			connection.setJTConnection(instJT.myJTCliques.get(ind1).cliques, ownJT.myJTCliques.get(ind2).cliques, keyJT,
					keyJTOwn, ind1, ind2, tempConnSeparator);
		} else if (rL.get(1).contains("::")) {// the 2nd terminal node of the RL is from an instance
			keyJT = findKeyOfJT(JTList, instance2Name, var2Name);
//			System.out.println("The key of the JT is " + keyJT + " hahaha. The class name " + class2Name + " var2Name " + var2Name  );
			instJT = JTList.get(keyJT);
//			System.out.println("The JT of the instance is " + instJT);

			
			keyJTOwn = findKeyOfJT(JTList, "OWN", var1Name);
			//System.out.println("Key to find JT in JTList " + str1[0] + " and " + keyJTOwn);
			ownJT = JTList.get(keyJTOwn);

			String ind1 = searchClique(ownJT, var1Name);
			// since str1 will not contain "_" hence split would return an array
			// of size 1
			String ind2 = searchClique(instJT, instance2Name + "_" + var2Name);
			// System.out.println(instJT);
			//System.out.println(str2[0] + "_" + str2[1] + " Ind1, Ind2 " + ind1 + ", " + ind2);
			ArrayList<String> tempConnSeparator = new ArrayList<String>();
			tempConnSeparator.add(var2Name);
			
//			System.out.println("The connection created and to be added is ownJT.myJTCliques.get(ind1).cliques, instJT.myJTCliques.get(ind2).cliques,keyJTOwn, keyJT, ind1, ind2, tempConnSeparator \n" + " = " 
//								+ "ownJT.myJTCliques.get(ind1).cliques" + " " + "instJT.myJTCliques.get(ind2).cliques" + " "
//								+ keyJTOwn + " " + keyJT + " " + ind1 + " " + ind2 + " " +  tempConnSeparator);
			
			connection.setJTConnection(ownJT.myJTCliques.get(ind1).cliques, instJT.myJTCliques.get(ind2).cliques,
					keyJTOwn, keyJT, ind1, ind2, tempConnSeparator);
		} else {
			System.out.println("Some anomalies or inconsistencies occurred in the RL list construction!!!");
		}

		return connection;
	}

	public String findKeyOfJT(HashMap<String, JTStructure> jTList, String JTName, String varName) {
		String keyJT = "";
		ArrayList<String> keys = new ArrayList<String>();
		for (String key : jTList.keySet()) {
			if (key.contains(JTName)) {
				keys.add(key);
			}
		}
		if (keys.size() == 1)
			keyJT = JTName + "#1";
		else {
			for (String key : keys) {
				JTStructure tempJT = jTList.get(key);
				for (String n : tempJT.myJTCliques.keySet()) {
					ArrayList<String> clq = tempJT.myJTCliques.get(n).cliques;
					if (clq.contains(varName)) {
						keyJT = key;
						break;
					}
				}
			}
		}
		return keyJT;
	}

	public ArrayList<JTConnection> unSortConnections(HashMap<String, JTConnection> setConnections) {

		JTConnection connectionsInArray[] = setConnections.values()
				.toArray(new JTConnection[setConnections.values().size()]);

		ArrayList<JTConnection> unSortedConnections = new ArrayList<JTConnection>(Arrays.asList(connectionsInArray));

		return unSortedConnections;
	}

	public int weightOfCliqueLetters(ArrayList<String> clq) throws UnsupportedEncodingException {
		int count1 = 0;

		for (String s : clq) {
			int count = 0;
			byte[] bytes = s.getBytes("US-ASCII");
			for (int i = 0; i < bytes.length; i++)
				count = count * 10 + bytes[i];

			count1 = count1 * 10 + count;
		}

		return count1;
	}

	public ArrayList<JTConnection> sortConnections(HashMap<String, JTConnection> setConnections)
			throws UnsupportedEncodingException {

		JTConnection connectionsInArray[] = setConnections.values()
				.toArray(new JTConnection[setConnections.values().size()]);
		// converting collection of JT connections from hashmap to Array

		for (int i = 0; i < connectionsInArray.length; i++) {
			for (int j = 0; j < connectionsInArray.length; j++) {
				if (connectionsInArray[i].connectionSeparators.size() < connectionsInArray[j].connectionSeparators
						.size()) {
					JTConnection tempCon = connectionsInArray[i];
					connectionsInArray[i] = connectionsInArray[j];
					connectionsInArray[j] = tempCon;
				} 
				else if (connectionsInArray[i].connectionSeparators
						.size() == connectionsInArray[j].connectionSeparators.size()
						&& weightOfCliqueLetters(connectionsInArray[i].connectionSeparators) > weightOfCliqueLetters(
								connectionsInArray[j].connectionSeparators)) {
					JTConnection tempCon = connectionsInArray[i];
					connectionsInArray[i] = connectionsInArray[j];
					connectionsInArray[j] = tempCon;
				}
				
				/* though I planned to check the chronological orders of edge labels, 
				 * but I think edge labels could be more than one letter and need not 
				 * to check to order them. If I realize that we should do it, then 
				 * add the checking here
				 * */
			}
		}

		ArrayList<JTConnection> sortedConnections = new ArrayList<JTConnection>(Arrays.asList(connectionsInArray));

		return sortedConnections;
	}

	public void swap(JTConnection jtConnection, JTConnection jtConnection2) {
		JTConnection tempCon = jtConnection;
		jtConnection = jtConnection2;
		jtConnection = tempCon;
	}

	/*
	 * lastNodeCount is used to keep track of indices that used in the last
	 * iteration Say the JT has 3 junction trees with 7 cliques in total. So the
	 * combined clique indices vs local indices will be like the following
	 * 
	 * 1 - 1 3 - 2 ----- 2 - 1 5 - 2 6 - 3 ----- 4 - 1 7 - 2
	 * 
	 * Already it has in each clique "Own#1" as prefix now we might have to add
	 * more prefix Own and then j i.e. Own#1, Own#2 ... Target is to remove
	 * Previous Own#1 i.e. the instance name
	 * 
	 */
	public JTStructure extractOneOfTheJTs(JTStructure jt, int j, String instanceName) {
		JTStructure tempJT = new JTStructure();
		String CliqueKeyPrefix = instanceName + "#" + j;
		int count = 0;
		HashMap<String, Integer> countHash = new HashMap<String, Integer>();

		for (String I : jt.myJTCliques.keySet()) {
			String CliqueKeySuffix = I.replace(instanceName + "#1", "");
			if (jt.componentIndex.get(I) == j) {// if any clique belongs to a
												// particular tree number i.e. j
												// then put that in tempJT
				HashMap<String, ArrayList<String>> tempSep = new HashMap<String, ArrayList<String>>();

				tempJT.myJTCliques.put(CliqueKeyPrefix + CliqueKeySuffix, jt.myJTCliques.get(I));
				for (String k : jt.myJTCliques.get(I).myJTSeparators.keySet()) {
					String CliqueKeySuffix2 = k.replace(instanceName + "#1", "");
					// ######################################## for a jf having
					// multiple jt with more than one clique, we might have to
					// change this part or check for correctness at least
					// ########################################
					tempSep.put(CliqueKeyPrefix + CliqueKeySuffix2, jt.myJTCliques.get(I).myJTSeparators.get(k));
				}

				tempJT.myJTCliques.get(CliqueKeyPrefix + CliqueKeySuffix).myJTSeparators = tempSep;
				tempJT.componentIndex.put(CliqueKeyPrefix + CliqueKeySuffix, 1);
			}
		}

		tempJT.componentIndex.put("$$##Size##$$", 1); // the number of JT here
														// is 1

		// System.out.println(tempJT);

		return tempJT;
	}

	public HashMap<String, JTStructure> pruning(HashMap<String, JTStructure> jTList) {
		int count = 0;
		for (String key : jTList.keySet()) {
			JTStructure jt = jTList.get(key);

			jt = pruningIndividualJT(jt, count);
		}

		return jTList;
	}

	// I think I need to revise this part (prunning) in a whole because of I
	// have changed the data structure from list to map
	// I have started revising it on Thursday 26-07-2018

	/*
	 * Revisied version of the previous iterative version is to make the
	 * function recursive All ways start from 1st clq to last in the input JT if
	 * any clique has any separator same remove that separator and clique and
	 * make the new JT call it recursively. The base case is if there is no such
	 * same clique-separator
	 */

	// In a sense, we don't need the neighvor list in our JT structure, cause
	// the mapping has got the neighbor info. we will work around this later

	public JTStructure pruningIndividualJT(JTStructure jt, int count) {
		// meaning: parentRemovedClique = parent of the removed clique which is
		// actually the clq connected by a sepqrator
		String parentRemovedClique = "";// just to flag no parent clique is
										// found so far

		String cliqueTobeRemoved = "";

		for (String i : jt.myJTCliques.keySet()) {

			// if say in the current JT we have got a clq which can be removed,
			// and there are more but we don't need to check for the remaining
			// cause that will be taken care of in the next recursion call,
			// another point is that it 1st clq removal may change the other
			// cliques and the potential cliques to be removed might change
			// System.out.println(i + " " + jt.myJTSeparators.get(i));
			for (String p : jt.myJTCliques.get(i).myJTSeparators.keySet()) {
				if (sameClique(jt.myJTCliques.get(i).cliques, jt.myJTCliques.get(i).myJTSeparators.get(p))) {
					parentRemovedClique = p;
					break;
				}
			}
			if (parentRemovedClique != "") {
				cliqueTobeRemoved = i;
				break;
			}
		}

		// the base case
		if (parentRemovedClique == "") {
			return jt;
		}

		for (String j : jt.myJTCliques.get(cliqueTobeRemoved).myJTSeparators.keySet()) {
			if (!parentRemovedClique.contains(j)) {
				ArrayList<String> newSep = CommonItems(jt.myJTCliques.get(j).cliques,
						jt.myJTCliques.get(parentRemovedClique).cliques);

				// updating separator info to p
				jt.myJTCliques.get(parentRemovedClique).myJTSeparators.put(j, newSep);
				// updating separator info to j
				jt.myJTCliques.get(j).myJTSeparators.put(parentRemovedClique, newSep);

				// removing separators of 'i' related to x it's neighbors
				jt.myJTCliques.get(j).myJTSeparators.remove(cliqueTobeRemoved);

			}
		}

		jt.myJTCliques.get(parentRemovedClique).appendDeletedCliquesNumbers(jt.myJTCliques.get(cliqueTobeRemoved));

		// removing i from it's parent's neighbor hood and separator list
		jt.myJTCliques.get(parentRemovedClique).myJTSeparators.remove(cliqueTobeRemoved);

		// removing clique i and it's neighbor and separator info
		jt.myJTCliques.remove(cliqueTobeRemoved);

		// we don't need this anymore
		// jt.myJTSeparators.remove(cliqueTobeRemoved);

//		System.out.println("\nHello there, pruning " + count + "\n" + jt);
		pruningIndividualJT(jt, count + 1);// recursive call to the pruning

		return jt;

	}

	public JTStructure pruningIndividualJTOnEachStep(JTStructure jt, int count) {
		// meaning: parentRemovedClique = parent of the removed clique which is
		// actually the clq connected by a sepqrator
		String parentRemovedClique = "";
		// just to flag no parent clique is found so far

		String cliqueTobeRemoved = "";

		for (String i : jt.myJTCliques.keySet()) {

			// if say in the current JT we have got a clq which can be removed,
			// and there are more but we don't need to check for the remaining
			// cause that will be taken care of in the next recursion call,
			// another point is that it 1st clq removal may change the other
			// cliques and the potential cliques to be removed might change

			for (String p : jt.myJTCliques.get(i).myJTSeparators.keySet()) {
				if (sameClique(jt.myJTCliques.get(i).cliques, jt.myJTCliques.get(i).myJTSeparators.get(p))) {
					parentRemovedClique = p;
					break;
				}
			}
			if (parentRemovedClique != "") {
				cliqueTobeRemoved = i;
				break;
			}
		}

		// the base case
		if (parentRemovedClique == "") {
			return jt;
		}

//		System.out.println("\n\nBefore parent update" + jt + "\n\n");
		// updating clique info of parent clique by adding the removed clique
		jt.myJTCliques.put(cliqueTobeRemoved + "," + parentRemovedClique, jt.myJTCliques.get(parentRemovedClique));

		// updating parent info after a pruning
		for (String str : jt.myJTCliques.get(parentRemovedClique).myJTSeparators.keySet()) {
			ArrayList<String> tempSep = jt.myJTCliques.get(str).myJTSeparators.get(parentRemovedClique);
			jt.myJTCliques.get(str).myJTSeparators.put(cliqueTobeRemoved + "," + parentRemovedClique, tempSep);
			jt.myJTCliques.get(str).myJTSeparators.remove(parentRemovedClique);
		}

		jt.myJTCliques.remove(parentRemovedClique);

		parentRemovedClique = cliqueTobeRemoved + "," + parentRemovedClique;

		for (String j : jt.myJTCliques.get(cliqueTobeRemoved).myJTSeparators.keySet()) {
			if (!parentRemovedClique.contains(j)) {
				ArrayList<String> newSep = CommonItems(jt.myJTCliques.get(j).cliques,
						jt.myJTCliques.get(parentRemovedClique).cliques);

				// updating separator info to p
				jt.myJTCliques.get(parentRemovedClique).myJTSeparators.put(j, newSep);
				// updating separator info to j
				jt.myJTCliques.get(j).myJTSeparators.put(parentRemovedClique, newSep);

				// removing separators of 'i' related to x it's neighbors
				jt.myJTCliques.get(j).myJTSeparators.remove(cliqueTobeRemoved);

			}
		}

		// appending removed clique's clique nunmber to its parent
		jt.myJTCliques.get(parentRemovedClique).appendDeletedCliquesNumbers(jt.myJTCliques.get(cliqueTobeRemoved));

		// removing i from it's parent's neighbor hood and separator list
		jt.myJTCliques.get(parentRemovedClique).myJTSeparators.remove(cliqueTobeRemoved);

		// removing clique i and it's neighbor and separator info
		jt.myJTCliques.remove(cliqueTobeRemoved);

		// we don't need this anymore
		// jt.myJTSeparators.remove(cliqueTobeRemoved);
		// System.out.println("Sorted connections before update " +
		// sortedConnections);

		// updating the RefLinks those are not processed yet

		for (JTConnection con : sortedConnections) {
			if (con.done == false) {
				// System.out.println("Old" + backupParentRemovedClique + " and
				// new " + parentRemovedClique + " and connection is " + con );
				if (con.cliqueTerminal1.equalsIgnoreCase(cliqueTobeRemoved)) {
					con.cliqueTerminal1 = parentRemovedClique;
				}
				if (con.cliqueTerminal2.equalsIgnoreCase(cliqueTobeRemoved)) {
					con.cliqueTerminal2 = parentRemovedClique;
				}
			}
		}
//		System.out.println("\nHello there, pruning " + count + "\n" + jt);
		pruningIndividualJT(jt, count + 1);// recursive call to the pruning

		return jt;

	}

	public boolean sameClique(ArrayList<String> list1, ArrayList<String> list2) {
		boolean same = true;

		if (list1.size() == list2.size()) {
			for (int i = 0; i < list1.size(); i++)
				if (!list2.contains(list1.get(i)))
					return false;
		}

		else
			return false;
		return same;
	}

	public JTStructure augment(JTStructure jt1, ArrayList<String> clqSeq, String src, String dest, JTConnection jTC) {
//		System.out.println("The connection " + jTC);
		ArrayList<String> common = jTC.connectionSeparators;
		// augmenting the cliques
		//System.out.println("Clq Seq " + clqSeq);
		for (int i = 0; i < clqSeq.size(); i++) {

			String p = clqSeq.get(i);
			for (String s : common) {
				if (!jt1.myJTCliques.get(p).cliques.contains(s))
					jt1.myJTCliques.get(p).cliques.add(s);
			}
		}

		// augmenting the separators
		for (int i = 0; i < clqSeq.size() - 1; i++) {
			String p = clqSeq.get(i);
			String q = clqSeq.get(i + 1);

			//System.out.println("P = " + p + " q = " + q);
			for (String s : common) {
				// if p's neighbor at position q doesn't contain s then add it
				// there
				if (!jt1.myJTCliques.get(p).myJTSeparators.get(q).contains(s)) {
					jt1.myJTCliques.get(p).myJTSeparators.get(q).add(s);
				}

				// if q's neighbor at position p doesn't contain s then add it
				// there
				if (!jt1.myJTCliques.get(q).myJTSeparators.get(p).contains(s)) {
					jt1.myJTCliques.get(q).myJTSeparators.get(p).add(s);
				}
			}
		}

		return jt1;
	}

	// public static boolean donePathFinding = false;
	//
	// // find the path to be augmented
	// public void findPath(JTStructure jt1, String src, String dest,
	// ArrayList<String> clqSeq) {
	//
	// if (src == dest) {
	// clqSeq.add(dest);
	// visited.put(src, 1);
	// donePathFinding = true;
	// return;
	// }
	// visited.put(src, 1);
	// for (String next : jt1.myJTSeparators.get(src).keySet()) {
	// if (visited.get(next) == 0 && donePathFinding == false) {
	// System.out.println(src);
	// findPath(jt1, next, dest, clqSeq);
	// if (donePathFinding){
	// clqSeq.add(src);
	// }
	//
	// }
	// }
	
	// }

	public HashMap<String, String> parent;

	public ArrayList<String> path;

	public boolean checkJTProperty(JTStructure jt) {
		boolean JTHolds = true;

		HashMap<String, ArrayList<String>> flagCheckingDone = new HashMap<String, ArrayList<String>>();

		for (String src : jt.myJTCliques.keySet()) {
			for (String dest : jt.myJTCliques.keySet()) {
				if (!(flagCheckingDone.get(src) != null && flagCheckingDone.get(src).contains(dest))) {
					if (flagCheckingDone.get(dest) == null)
						flagCheckingDone.put(dest, new ArrayList<String>());
					flagCheckingDone.get(dest).add(src);
					flagCheckingDone.get(src);
					parent = new HashMap<String, String>();

					ArrayList<String> visited = new ArrayList<String>();
					path = new ArrayList<String>();
					parent.put(src, "");
					findPathBFS(jt, src, dest, visited);
					extractPath(dest);

					ArrayList<String> srcClique = jt.myJTCliques.get(src).cliques;
					ArrayList<String> destClique = jt.myJTCliques.get(dest).cliques;

					ArrayList<String> commonClqElem = CommonItems(srcClique, destClique);
					boolean isContain = doesPathContainsCommonClqElem(jt, commonClqElem, src, dest);
					if (isContain == false) {
						// return isContain;
						JTHolds = false;
					}
				}
			}
		}

		return JTHolds;
	}

	// check in the path for common elements
	public boolean doesPathContainsCommonClqElem(JTStructure jt, ArrayList<String> commonItems, String src,
			String dest) {
		boolean isContain = true;
//		System.out.println("The JT under check " + jt);
		for (String clqInd : path) {
			isContain = jt.myJTCliques.get(clqInd).cliques.containsAll(commonItems);
			if (isContain == false) {
//				System.out.println("Path : " + path);
				if(!onlyDisplatTime) System.out.print("JT fails for Common Items " + commonItems + " between cliques ("
						+ jt.myJTCliques.get(src) + ", " + jt.myJTCliques.get(dest) + ")  in Clique : ");
				if(!onlyDisplatTime) System.out.println(clqInd + " : " + jt.myJTCliques.get(clqInd));
				return isContain;
			}
		}

		return isContain;
	}

	public void findPathBFS(JTStructure jt, String src, String dest, ArrayList<String> visited) {

		Queue<String> Q = new LinkedList<String>();
		visited.add(src);
		Q.add(src);

		while (!Q.isEmpty()) {
			String src1 = Q.remove();
			for (String next : jt.myJTCliques.get(src1).myJTSeparators.keySet()) {
				if (!visited.contains(next)) {
					parent.put(next, src1);
					Q.add(next);
					visited.add(next);
				}
			}
		}
	}

	public void extractPath(String dest) {

		if (parent.get(dest) == "") {
			path.add(dest);
			return;
		}

		extractPath(parent.get(dest));

		path.add(dest);

	}

	// public static HashMap<String, Integer> visited = new HashMap<String,
	// Integer>();

	// public void SIICompilation(ArrayList<JTConnection> sortedConnections,
	// HashMap<String, JTStructure> jTList) {
	public void SIICompilation(HashMap<String, JTStructure> jTList) {
		JTStructure jt1 = null;
		JTStructure jt2 = null;
		boolean f1 = true, f2 = true;
		String key1 = "", key2 = "";

		int ind = 0;

		int stepCount = 0;

		for (JTConnection jTC : sortedConnections) {

			f1 = true;
			f2 = true;
			for (String JTKey : jTList.keySet()) {
				/*becareful, as we might get into trap in a way for some cases like
				 * OWN10 and OWN1; Own2 and Own2,Own3
				 * If we were looking for OWN1, and encounter with OWN10, then we will end up with OWN10, which is actually wrong
				 * However, if we were looking for OWN2 but we have OWN2,OWN3, then we should end up with OWN2,OWN3
				 * One more thing, if we have OWN1,OWN2 and OWN10, then we should end up with OWN1, OWN2
				 * So, we should be able to distinguish between OWN1's supersequence and OWN10
				 * */
//				if (f1 && JTKey.contains(jTC.jtTerminal1)) {
				if (f1 && hasTheTerminal(JTKey, jTC.jtTerminal1)) {
					jt1 = jTList.get(JTKey);
					key1 = JTKey;
					f1 = false;
				}
//				if (f2 && JTKey.contains(jTC.jtTerminal2)) {
				if (f2 && hasTheTerminal(JTKey, jTC.jtTerminal2)) {
					jt2 = jTList.get(JTKey);
					key2 = JTKey;
					f2 = false;
				}
				if (f1 == false && f2 == false)
					break;
			}

			/*
			 * the above if-else makes a bond/mapping between JT and the
			 * connection i.e. JT1 is mapped to jTC.terminal1 and JT2 is mapped
			 * to jTC.terminal2 But if we call JT2.join, the join will consider
			 * JT2 as JT1 which we should take special care. Either don't change
			 * the order by calling JT2.join or something similar
			 */

			// System.out.println("The keys of the two JTs " + key1 + " & " +
			// key2);

			if (key1.equalsIgnoreCase(key2)) {// augmentation step
				String src = jTC.cliqueTerminal1;
				String dest = jTC.cliqueTerminal2;

				parent = new HashMap<String, String>();
				path = new ArrayList<String>();
				ArrayList<String> visited = new ArrayList<String>();

				parent.put(src, "");
				findPathBFS(jt1, src, dest, visited);
				extractPath(dest);

				jt1 = augment(jt1, path, src, dest, jTC);
				// if we prune at each step, then in path finding for
				// augmentation, there might be a chance of dest not found
//				jt1 = pruningIndividualJTOnEachStep(jt1, 0);

//				System.out.println("\n\nJT after augmentation \n" + jt1);

			} else {// joining step
				sortedConnections.get(ind).done = true;
				int jt1Size = 0;
				// System.out.println("Joining " + key1 + " + " + key2 + " using
				// " + jTC);
				jt1Size = jt1.myJTCliques.size();
				jt1.join(jt2, jTC);
				// if we prune at each step, then in path finding for
				// augmentation, there might be a chance of dest not found
//				jt1 = pruningIndividualJTOnEachStep(jt1, 0);
				jTList.put(key1 + "," + key2, jt1);

				/*
				 * assuming, the 2nd JT related connections need to be upgraded
				 * i.e. only the remaining connections with 2nd JT need to be
				 * updated where only clique numbers are updated based on the
				 * size of 1st JT
				 *
				 * Don't swap JT orders to avoid any logical inconsistencies
				 **/
				// sortedConnections = updateConnections(jt1Size, jt2,
				// sortedConnections, key1, key2);

//				System.out.println("\n\nJT after joining \n" + jt1);
				jTList.remove(key1);
				jTList.remove(key2);
			}
			// System.out.println("\nThe resultant JT aftert step " +
			// ++stepCount + "\n" + jt1);
		}
		
		
		
		jTList = pruning(jTList);

		for (String jtID : jTList.keySet()) {
			boolean jtPropertyHolds = checkJTProperty(jTList.get(jtID));
			if (jtPropertyHolds) {
				if(!onlyDisplatTime) System.out.println("JT property holds.");
			}
			else {
				if(!onlyDisplatTime) System.out.println("JT property doesn't hold!!!");
			}
		}
		if(sortedConnections.size() > 0)
			sortedConnections.get(ind++).done = true;
		if(!onlyDisplatTime) System.out.println("The resultatnt JT list after SIIC \n" + jTList);

		for (String key : myGlobalCliques.keySet())
			if(!onlyDisplatTime) System.out.println(
					key + " -> " + myGlobalCliques.get(key).cliqueNumbers + " : " + myGlobalCliques.get(key).cliques);
		if(!onlyDisplatTime) System.out.println(myGlobalCliquesKeys);
	}
	
	public boolean hasTheTerminal(String jTKey, String jtTerminal) {
		String []keyArr = jTKey.split(",");
		for(String key: keyArr) {
			if(key.equalsIgnoreCase(jtTerminal))
				return true;
		}
		return false;
	}

	public boolean subClique(int clqNum1, int clqNum2) {
		String clqKey1 = myGlobalCliquesKeys.get(clqNum1); 
		String clqKey2 = myGlobalCliquesKeys.get(clqNum2);
		
		myClique clq1 = myGlobalCliques.get(clqKey1);
		myClique clq2 = myGlobalCliques.get(clqKey2);
		
//		System.out.println(clq1.cliques + "  " + clq2.cliques + "\n\n");
		
		for(String key: clq1.cliques){
//			System.out.println(key + "  " + clq2.cliques);
			if(!clq2.cliques.contains(key))
				return false;
		}
		
		return true;
	}
	
	public ArrayList<Integer> findOnlySuperCliques(String topCliqueKey, JTStructure jt) {
		ArrayList<Integer> superClqs= new ArrayList<Integer>();
		
		myClique topClq = jt.myJTCliques.get(topCliqueKey); 
		
		superClqs.addAll(topClq.cliqueNumbers);
		
		for(int i = 0; i < topClq.cliqueNumbers.size(); i++){
			for(int j = 0; j < topClq.cliqueNumbers.size(); j++){
				if(i != j && subClique(topClq.cliqueNumbers.get(i), topClq.cliqueNumbers.get(j)) == true){// if i is a subClique of j then break and remove i from superClq's list
					superClqs.remove(topClq.cliqueNumbers.get(i));
					break;
				}
			}
		}
		
		return superClqs;
	}
	
	public void conductElection(myClique curClq, HashMap<String, HashMap<String, Integer>> voting1, Set<String> mpKeys) {
		
		HashMap<String, ArrayList<String>> regionSeparators = curClq.myJTSeparators;
		int count = 1;
//		System.out.println("Voting info: ");
		for(String sep: regionSeparators.keySet()){// for each region
			HashMap<String, Integer> tempVote = new HashMap<String, Integer>(); 
			for(String mpKey: mpKeys){// for each MP whose string ID is now in voting1 as key
				ArrayList<String> common = CommonItems(regionSeparators.get(sep), myGlobalCliques.get(mpKey).cliques);
//				System.out.println(sep +"  " + regionSeparators.get(sep) + "   " + mpKey + "["+ myGlobalCliques.get(mpKey).cliques +"]"+ " : " + common + "  " + common.size());
				tempVote.put(mpKey, common.size());
			}
			voting1.put(sep, tempVote);
		}
		
	}

	public void partitionOnClique(String Clique2BPruned, ArrayList<Integer> superCliques, JTStructure jt, myClique partition1, myClique partition2) {
		/*
		 * 	###############  1st stage of voting  ###############
		 * Perform voting where separators of the clique are voting zone
		 * the variables of the separators are citizen who can vote multiple MPs
		 * However, the MP having most vote in a particular zone will win the zone
		 * 
		 * In case of tie, we will take all winners say 3 MP got same vote then we will 
		 * count all 3 of them as winner, later in coalition, we will count all of them in sum 
		 * This will help us to avoid depriving some winner which will be helpful later 
		 * 
		 * MPs are the original cliques
		 * 
		 * Don't worry about the MP who got 2 out of 3 votes, 
		 * we will propagate the other variable in the path
		 * */
		// map : Clq -> map <Separator, count> 
		// map : Clq -> Separators where this clique won
		
		HashMap<String, HashMap<String, Integer>> voting1 = new HashMap<String, HashMap<String, Integer>>();
		// the above map will contain region wise each MPs vote
//		HashMap<String, Integer> MPsRegion = new HashMap<String, Integer>();
//		// above map will contain which MP 
		
		for(int i = 0; i < superCliques.size(); i++){
			HashMap<String, Integer> tempVoting = new HashMap<String, Integer>();
			String tempStrKey = myGlobalCliquesKeys.get(superCliques.get(i));
			voting1.put(tempStrKey, tempVoting);
		}
//		System.out.println(voting1);
		myClique curClq = jt.myJTCliques.get(Clique2BPruned);
		
		Set<String> mpKeys= new HashSet<String>();
		mpKeys.addAll(voting1.keySet());
		
		conductElection(curClq, voting1, mpKeys);
		
		/*
		 * 	###############  2nd stage of voting  ###############
		 * Now, for each MP, try to assign them into one of the two coalition group so that the difference
		 * become minimal.
		 * 
		 * One of the idea is:
		 *	sum = sum up the #MPs'. 
		 *	Avg = sum/2
		 *	
		 *	So, select from (descending order of MP's numbers in p)
		 *	
		 *	1. put max in 1st group, then the 2nd max in 2nd group
		 *	2. Get the difference d = max - 2nd max
		 *	3. if d > 0:
		 *		keep adding next value from p until d <= 0
		 *	4. if d< 0
		 *		take the next max from P and add it to group 1
		 *		d = sum(group1) - sum(group2)
		 *		jump up to step 3
		 * */
	}
	

	public void pruneTopClique(String topCliqueKey, JTStructure jt) {
		myClique partition1 = new myClique();
		myClique partition2 = new myClique();
		
		ArrayList<Integer> superCliques = findOnlySuperCliques(topCliqueKey, jt);
		
//		System.out.println(superCliques);
		
		partitionOnClique(topCliqueKey, superCliques, jt, partition1, partition2);
		
		
	}

	public void pruneCliquesInStack(Stack<String> cliqueStack2BPruned, JTStructure jt) {
		while(!cliqueStack2BPruned.isEmpty()){
			String topCliqueKey = cliqueStack2BPruned.pop();
			pruneTopClique(topCliqueKey, jt);
		}
	}

	public void pruneJT(JTStructure jt) {
		//System.out.println("Cliques in the Stack: ");
		for (String key: jt.myJTCliques.keySet()){
			myClique curClique = jt.myJTCliques.get(key);
			if(curClique.cliqueNumbers.size() > 1){
				cliqueStack2BPruned.push(key);
//				System.out.println(key);
			}
		}
		pruneCliquesInStack(cliqueStack2BPruned, jt);
	}

	public void PostPrunning(HashMap<String, JTStructure> jTList) {
		for(String jtKey: jTList.keySet()){
			pruneJT(jTList.get(jtKey));
		}
		
	}

	/*
	 * this function can be used to calculate JT cost of Kanazawa where JT is generated by SIIC
	 * 
	 * */
	public int calculateOwnJTCostKanazawa(JTStructure jt) {
		// we don't need to extract nodes from JTs, since we have domain.getNodes() and domain.getNodesbyName()
		
		// myStrClq: myJTCliques
		String costFormula = "", costFormula2 = "";
		int cost = 0;
		
		for(String n: jt.myJTCliques.keySet()){
			int numOfNeighbor = jt.myJTCliques.get(n).myJTSeparators.size();
			int omega = numOfNeighbor;
			costFormula = Integer.toString(omega);
			int stateSpaceSize = 2;
			for(String key: jt.myJTCliques.get(n).cliques){
					omega *= stateSpaceSize;
					costFormula += " * " + Integer.toString(stateSpaceSize);
			}
			cost += omega;
			costFormula2 += " + " + costFormula + "\n";
		}
//		System.out.println(costFormula2);
		return cost;
	}
	
	public void removingInstancesFromCompilingClass(String classFileToCompile, SIICompilation SIIC) throws IOException
	{
		// call recursiveParserXML() here to update TempNodeTable with info of a
		// particular class that needs to be loaded
		

		File inputFile = new File(classFileToCompile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("node");

			ArrayList<String> tagList = new ArrayList<String>();

			tagList.add("state");
			tagList.add("parent");
			tagList.add("tuple");

			if(!onlyDisplatTime) System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");
			TempNodeTable = new HashMap<String, mxCell>();
			SIIC.recursiveParserXML(nodeList, tagList, null);// initially mxCell
																// is null

			NodesInExternalClasses.add(TempNodeTable);
			
			// create the oobn file for the compiling class			
			String gc = SIIC.generateCodeHuginCodeAfterRemovingInstance(classFileToCompile, TempNodeTable);

			
			String FILENAME = classFileToCompile.replace(".class", "")+ "_own.oobn";
			FileWriter fw = new FileWriter(FILENAME);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(gc);
			bw.close();

		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public ArrayList<ArrayList<String>> generateRefLinks(String classFileToCompile, SIICompilation SIIC) throws IOException
	{
		/*
		 * the refLinkList is a list of list of string, where inner list could be replaced by a pair of string but I didn't
		 * write any code for tuple/pair of string. so it is a list of string pairs representing list of 
		 * referential edges
		 * */  

		ArrayList<ArrayList<String>> RefLinkList = new ArrayList<ArrayList<String>>();
		/*
		 * Extract the referential link info from classes
		 */
		File inputFile = new File(classFileToCompile);
//		System.out.println("Class File to Compile " + classFileToCompile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList RLList = doc.getElementsByTagName("RefLink");

			ArrayList<String> tagList = new ArrayList<String>();

			tagList.add("source");
			tagList.add("target");

			/*
			 * Call the iterative parser to get the ref link info
			 */
			
			RefLinkList = SIIC.iterativeParserXML4RL(RLList, tagList, RefLinkList);
//			System.out.println("Ref LL before psudo " + RefLinkList);
			/*
			 * Now you can add pseudo ref link info in RefLinkList
			 * So, call a function that will take the current RefLinkList, add the additional pseudo ref links those
			 * are not in .class file
			 * 
			 * BTW, the additional pseudo ref links are generated in the generateCode function of this java file 
			 * while duplicating a list of output nodes of instances   
			 */ 
			
			RefLinkList = SIIC.addPsudoRefLinksToTheList(RefLinkList, pseudoRefLinks);
			
			
//			System.out.println("\n\nList of referential edges: " + RefLinkList);

		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return RefLinkList;
	}
	
	public ArrayList<ArrayList<String>> addPsudoRefLinksToTheList(ArrayList<ArrayList<String>> refLinkList, ArrayList<RefLink> pseudoRefLinks2) {
		for(RefLink rl: pseudoRefLinks2){
			ArrayList<String> tempRL = new ArrayList<String>();
			
			tempRL.add(rl.nameSrc);
			tempRL.add(rl.nameTarget);
			
			refLinkList.add(tempRL);
		}
		return refLinkList;
	}
	
	public HashMap<String, SIICompilation.JTStructure> generateJunctionForest(String dir, String classFileToCompile, SIICompilation SIIC) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ExceptionHugin
	{
//		System.out.println("Class file to compile " + classFileToCompile);
		String extraDir = "";
		String classFileToCompileOnlyFile = "";
		if(classFileToCompile.contains("GeneratedFiles")){
			extraDir = classFileToCompile.split("\\\\")[0]+"\\";
			classFileToCompile = classFileToCompile.split("\\\\")[1];
		}
		// loading JTs from files
		SIICompilation.JTStructure jt = SIIC.new JTStructure();
		// jt = SIIC.getJTInfoFileString("jt.txt", "[: ]+", "JT");

		HashMap<String, SIICompilation.JTStructure> JTList = new HashMap<String, SIICompilation.JTStructure>();
		// we need to convert set to array or ArrayList to maintain order of
		// instances
		ArrayList<String> arrayListInstances = new ArrayList<String>(instanceList);

		// this for loop is extracting info of JT from files in a per instance
		// basis to optimize it, we can load it for per class and copy the data
		// structure info more efficiently
//		System.out.println("Num of instances " + arrayListInstances.size());
		for (int i = 0; i < arrayListInstances.size(); i++) {
			Instant startInstance = Instant.now();
//			System.out.println("instance # " + (i+1) + " 's JT constructing / finding");
			SIICompilation.JTStructure tempJT = SIIC.new JTStructure();
			String tempStr = arrayListInstances.get(i);
			String tok[] = tempStr.split("[::\\.]");
			String className = tok[0];
			String instanceName = tok[tok.length - 1];
			
			// this function will compile and create JT using Hugin if the JT doesn't exist
			jt = SIIC.getJTInfoFileString(dir, extraDir, "JT_" + className + ".txt", "[: ]+", instanceName, "#1");
			
			int NOJT = jt.componentIndex.get("$$##Size##$$");
			if (NOJT == 1) {// if there is only 1 JT
				jt.keyID = instanceName + "#1";
				JTList.put(instanceName + "#1", jt);
			} else {// assuming more than one JT
				for (int j = 1; j <= NOJT; j++) {
					JTStructure temporaryJT = SIIC.extractOneOfTheJTs(jt, j, instanceName);
					temporaryJT.keyID = instanceName + "#" + j;
					JTList.put(instanceName + "#" + j, temporaryJT);
				}
			}
			Instant instanceEnd = Instant.now();
			JTGrabOrCreate += "\t\t\t\tTime for Instance " + (i+1) + " JT creation " + Duration.between(startInstance, instanceEnd).toMillis() + "\n";
			timeToCreateJTs = timeToCreateJTs.plus(Duration.between(startInstance, instanceEnd));
		}
//		System.out.println("\n\nThe list of Junction Trees: \n" + JTList);

		Instant OwnJTStart = Instant.now();
		
		SIICompilation.JTStructure jtCurrent = SIIC.new JTStructure();
		/*
		 * RefLinkList is provided as argument to rename nodes in instance JT's
		 * those were connected via RL
		 * 
		 */
//		jtCurrent = SIIC.getJTInfoFileString("JT_Diag2.txt", "[: ]+", "OWN", "#1");
//		System.out.println("Own class's JT constructing / finding");
//		jtCurrent = SIIC.getJTInfoFileString("JT_Exp_asia_own.txt", "[: ]+", "OWN", "#1");
		
		// this function will compile and create JT using Hugin if the JT doesn't exist
		jtCurrent = SIIC.getJTInfoFileString(dir, extraDir, "JT_" + classFileToCompile.replace(".class", "") +"_own.txt", "[: ]+", "OWN", "#1");
//		System.out.println("The Current JT: " + jtCurrent);
		int NOJT = jtCurrent.componentIndex.get("$$##Size##$$");
		//System.out.println("NO JT in the compiling class " + NOJT);
		if (NOJT == 1) {// if there is only 1 JT
			jtCurrent.keyID = "OWN" + "#1";
			JTList.put("OWN" + "#1", jtCurrent);
//			System.out.println(JTList);
		} else {// assuming more than one JT
			for (int j = 1; j <= NOJT; j++) {
				JTStructure temporaryJT = SIIC.extractOneOfTheJTs(jtCurrent, j, "OWN");
				temporaryJT.keyID = "OWN" + "#" + j;
				JTList.put("OWN" + "#" + j, temporaryJT);
			}
		}
		Instant OwnJTEnd = Instant.now();
		JTGrabOrCreate += "\t\t\t\tTime for  own JT creation " + Duration.between(OwnJTStart, OwnJTEnd).toMillis() + "\n";
		timeToCreateJTs = timeToCreateJTs.plus(Duration.between(OwnJTStart, OwnJTEnd));
		return JTList;
	}
	
	public HashMap<String, JTConnection> createJTConnections(String classFileToCompile, SIICompilation SIIC, ArrayList<ArrayList<String>> RefLinkList, HashMap<String, SIICompilation.JTStructure> JTList) {
		HashMap<String, SIICompilation.JTConnection> SetConnections = new HashMap<String, SIICompilation.JTConnection>();

		for (ArrayList<String> RL : RefLinkList) {
			SIICompilation.JTConnection Connection = SIIC.new JTConnection();
			Connection = SIIC.convertRL2Connection(Connection, RL, JTList);
			SetConnections = SIIC.createConnections(Connection, SetConnections);
		}
//		System.out.println("\n\nThe set of connections: \n" + SetConnections);
		
		return SetConnections;
	}

	public void calculateShowJTCost(HashMap<String, JTStructure> JTList, SIICompilation SIIC) {
		for (String key: JTList.keySet()){
			int costJTSIIC = SIIC.calculateOwnJTCostKanazawa(JTList.get(key));
			System.out.println("The JT Cost " + costJTSIIC);
		}		
	}
	
	/*
	 * The compilation func here works on the following flow:
	 * 1. removingInstancesFromCompilingClass() which calls generate code to create .oobn file and code from .class
	 * 		this one starts working with .class file and using an XML parser func, it extracts all info from the .class file
	 * 		and then put it in a NodeTable. The table is then used in generateCode() to create .oobn file
	 * 		So, instead of adding pseudo Ref link info to .class file,
	 * 		in the generate .oobn code func, when we create duplicate/replica of output nodes of an instance
	 * 	 	we can have a list of pseudo ref link info that can be used in "generateRefLinks" 
	 * 2. generateRefLinks from .class of main file
	 * 		I don't know wht my NPPCompiler doesn't create psudo ref link info in .class files
	 * 		that means for JT connections, I need pseudo ref links along with ref links that I 
	 * 		somehow have to add before I call generateRefLinks() 
	 * 3. generateJunctionForest
	 * 4. createJTConnections
	 * 5. SIICompilation
	 * 6. PostPruning
	 * 
	 * */

	public String Compilation(String dir, String classFileToCompile) throws IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ExceptionHugin
	{	
//		System.out.println("class file name " + classFileToCompile);
		String timeSting = "";
		
		NodesInExternalClasses = new ArrayList<HashMap<String, mxCell>>();
		SIICompilation SIIC = new SIICompilation();

		Instant startPreprocess = Instant.now();
		pseudoRefLinks = new ArrayList<RefLink>();// this initialization must be done before "removingInstancesFromCompilingClass()" this call 
		removingInstancesFromCompilingClass(dir+classFileToCompile, SIIC);
		
		ArrayList<ArrayList<String>> RefLinkList = new ArrayList<ArrayList<String>>();
		
		RefLinkList = generateRefLinks(dir+classFileToCompile, SIIC);
		/*
		 * Ref Link generated connections: [[V, A_V], [V, B_V], [S, A_S], [S,
		 * B_S], [B_X, X], [B_G, G]] The set of instances used: [Asia.class::A,
		 * Asia2.class::B]
		 * 
		 * We need to extract the JTs for Asia.class and Asia2.class and then
		 * create connections between cliques of JTs using the generated
		 * connections. E.g. [V, A_V] : find the clique containing "V" in the JT
		 * of current class and the clique containing "V" in JT for instance "A"
		 * which is a copy of Asia.class JT
		 */
		
		Instant endPreprocess = Instant.now();
//		System.out.println("Pre-Processing time " + Duration.between(startPreprocess, endPreprocess));
		timeSting += "\t\tPre-Processing time " + Duration.between(startPreprocess, endPreprocess).toMillis() + "\n";
		tInfo.sIICTimingBreakDown.add("Pre-Processing ");
		tInfo.sIICTimingBreakDown.add(Long.toString(Duration.between(startPreprocess, endPreprocess).toMillis()));
		
		Instant startJFCreate = Instant.now();
		HashMap<String, SIICompilation.JTStructure> JTList = new HashMap<String, SIICompilation.JTStructure>();
		JTList = generateJunctionForest(dir, classFileToCompile, SIIC);
		
		Instant endJFCreate = Instant.now();
//		System.out.println("JF creation time " + Duration.between(startJFCreate, endJFCreate).toMillis());
		timeSting += "\t\tJF creation time " + Duration.between(startJFCreate, endJFCreate).toMillis() + "\n";
		timeSting += "\t\t\tBreakdown is:\n" + JTGrabOrCreate;
		tInfo.sIICTimingBreakDown.add("JF Creation");
		tInfo.sIICTimingBreakDown.add(Long.toString(Duration.between(startJFCreate, endJFCreate).toMillis()));
	
		Instant startJTConnection = Instant.now();
		HashMap<String, SIICompilation.JTConnection> SetConnections = new HashMap<String, SIICompilation.JTConnection>();
		SetConnections = createJTConnections(classFileToCompile, SIIC, RefLinkList, JTList);
		
		Instant endJTConnection = Instant.now();
//		System.out.println("JT connection time " + Duration.between(startJTConnection, endJTConnection).toMillis());
		timeSting += "\t\tJT connection time " + Duration.between(startJTConnection, endJTConnection).toMillis() + "\n";
		tInfo.sIICTimingBreakDown.add("JT connection");
		tInfo.sIICTimingBreakDown.add(Long.toString(Duration.between(startJTConnection, endJTConnection).toMillis()));
		
		/*
		 * Both sorted and unsorted connection order gives same result. May be
		 * same weigt different order has no impact
		 */

//		 ArrayList<JTConnection> unSortedConnections = SIIC.unSortConnections(SetConnections);
//		 SIIC.SIICompilation(JTList);
		
		Instant startConnectionSort = Instant.now();
		sortedConnections = SIIC.sortConnections(SetConnections);
//		System.out.println("\n\nThe set of connections in sorted form " + sortedConnections);
		Instant endConnectionSort = Instant.now();
//		System.out.println("Connection sorting time " + Duration.between(startConnectionSort, endConnectionSort).toMillis());
		timeSting += "\t\tConnection sorting time " + Duration.between(startConnectionSort, endConnectionSort).toMillis() + "\n";
		tInfo.sIICTimingBreakDown.add("Connection Sorting");
		tInfo.sIICTimingBreakDown.add(Long.toString(Duration.between(startConnectionSort, endConnectionSort).toMillis()));
		
		Instant startSIIC = Instant.now();
		SIIC.SIICompilation(JTList);
		Instant endSIIC = Instant.now();
//		System.out.println("SIIC time " + Duration.between(startSIIC, endSIIC).toMillis());
		timeSting += "\t\tSIIC time " + Duration.between(startSIIC, endSIIC).toMillis() + "\n";
		tInfo.sIICTimingBreakDown.add("Compilation");
		tInfo.sIICTimingBreakDown.add(Long.toString(Duration.between(startSIIC, endSIIC).toMillis()));
		
		Instant startPostPrunning = Instant.now();
		SIIC.PostPrunning(JTList);
		Instant endPostPrunning = Instant.now();
//		System.out.println("Post Prunning time " + Duration.between(startPostPrunning, endPostPrunning).toMillis());
		timeSting += "\t\tPost Prunning time " + Duration.between(startPostPrunning, endPostPrunning).toMillis() + "\n";
		tInfo.sIICTimingBreakDown.add("Post Prunning");
		tInfo.sIICTimingBreakDown.add(Long.toString(Duration.between(startPostPrunning, endPostPrunning).toMillis()));
		long endTime = System.currentTimeMillis();
//		calculateShowJTCost(JTList, SIIC);
		
		return timeSting;
	}
	
	public void initializeStaticVariables() {
		globalCliqueCount = 1;
		myGlobalCliques = new HashMap<String, myClique>();
		myGlobalCliquesKeys = new HashMap<Integer, String>();
		// this will contain the original clique numbers and should be mapped using
	    //same key as myJTCliques along with the JT id
		
		pseudoRefLinks = null;
		
		timeCodeGeneration = Duration.ZERO;
		
		instanceList = new HashSet<String>();
		// while making RL, we get the mapping instance Input node name to it's RL's
		// other end variable name
		inputNodeList = new HashMap<String, String>();
		
		rowIndex = 0;
		numOfRow = 0;
		 
		cliqueStack2BPruned = new Stack<String>();
		
		jtID2GlobalNumMap = new HashMap<String, Integer>();
		
		JTGrabOrCreate = "";
		timeToCreateJTs= Duration.ZERO;
	}
	
	public void performCompilation(String dir, String additionalDir, String fileName) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ExceptionHugin
	{
		/*
		 * On 13-01-2019, while fixing some issues for compilation, 
		 * I have organized (so far from the beginning of coding SIIC and iOOBN), 
		 * if you want to compile using SIIC, say an oobn xyz.oobn with n objects from m different classes, 
		 * you need to generate xyz.class and xyz.ioobn files for xyz.oobn using/running NPPCompiler 
		 * xyz.ioobn is required to display the oobn in iOOBN software gui 
		 * xyz.class is required to compile xyz.oobn using SIIC
		 * 
		 * 
		 * NB: while creating xyz.class file, NPPCompiler also generates all .oobn files related to the xyz.oobn 
		 * in a separate subfolder name "GeneratedFiles"
		 * by related, I mean it may create 
		 * (1) xyz.oobn again in the subfolder (the difference between the original xyz.oobn and
		 * 		new xyz.oobn is new oobn file may be a hugin oobn file created from xyz.oobn's parent class and interfaces as well as
		 * 		additional info of xyz.oobn itself) 
		 * (2) all embedded objects' associated class .oobn files 
		 * 
		 * 
		 * In SIIC, we will call compilation for xyz.class file which will be used to create temp_xyz.oobn file that purely contains
		 * all nodes related to xyz.oobn, that is, no instances in it, but some duplicate nodes of the output nodes of the instances
		 * */
		
		initializeStaticVariables();
		Instant start = Instant.now();
		
//		String timeSting = SIIC.Compilation("GenerateAutoOOBN\\nClasses_1#nObjects_1#nStates_4#nNodes_5#maxInDeg_15#maxArcs_6\\GeneratedFiles\\temp_main.class");
		
		String timeSting = Compilation(dir, additionalDir+fileName);
//		String timeSting = SIIC.Compilation("Diag.class");
		Instant end = Instant.now();
//		System.out.println("Whole SIIC compilation time " + Duration.between(start, end).toMillis());
		tInfo.totalSIICTime = Long.toString(Duration.between(start, end).toMillis());
//		System.out.println("\tTiming breakdown: \n" + timeSting);
//		System.out.println("Time required to generate code (removing instances) " + timeCodeGeneration.toMillis());
//		System.out.println("Time required to generate JTs (Junction Forest) " + timeToCreateJTs.toMillis() + "\nBreakdown is :\n"+ JTGrabOrCreate);

	}

	public static ArrayList<String> generateDirectoriesNameList()
	{
		ArrayList<String> dirs = new ArrayList<String>();
		String []argv = {"-nNodes", "5", "-maxVal", "2", "-maxInDegree", "5", "-nOOBNs","3", "-nObj", "5", "-fixed_maxVal"};
		
//		int []bnSize = {5, 20, 50};
//		int []arity = {2, 3, 4};
//		int []maxParent = {5, 10, 15};
//		int []additionalClass = {0, 1, 2, 3};
//		int []numObjPerAdditionalClass = {1, 3, 5};
		
		int []additionalClass = {0, 1, 2, 3};
		int []numObjPerAdditionalClass = {1, 2, 3};
		int []arity = {2, 3, 4};
		int []bnSize = {5};
		int []maxParent = {2, 3, 5};
		
//		int []additionalClass = {1};
//		int []numObjPerAdditionalClass = {1};
//		int []arity = {2};
//		int []bnSize = {5};
//		int []maxParent = {2};
		
		for(int a = 0; a < bnSize.length; a++){
			argv[1] = Integer.toString(bnSize[a]);
			for(int b = 0; b < arity.length; b++){
				argv[3] = Integer.toString(arity[b]);
				for(int c = 0; c < maxParent.length; c++){
					argv[5] = Integer.toString(maxParent[c]);
					for(int d = 0; d < additionalClass.length; d++){
						argv[7] = Integer.toString(additionalClass[d]);
						if(additionalClass[d] == 0) {
							argv[9] = "0";
							String namePrefix = "GenerateAutoOOBN\\";
							namePrefix += "nClasses_"+argv[7]+"#" + "nObjects_" + argv[9] + "#" + "nStates_" + argv[3] + "#" + "nNodes_" + argv[1] + "#" + "maxInDeg_" + argv[5] + "#" + "maxArcs_" + "6";
					        dirs.add(namePrefix+"\\");					        
						}
						else{
							for(int e = 0; e < numObjPerAdditionalClass.length; e++){
								argv[9] = Integer.toString(numObjPerAdditionalClass[e]);
								String namePrefix = "GenerateAutoOOBN\\";
								namePrefix += "nClasses_"+argv[7]+"#" + "nObjects_" + argv[9] + "#" + "nStates_" + argv[3] + "#" + "nNodes_" + argv[1] + "#" + "maxInDeg_" + argv[5] + "#" + "maxArcs_" + "6";
						        
								dirs.add(namePrefix+"\\");
							}
						}
					}
				}
			}
		}
		return dirs;
	}
	
	
	@SuppressWarnings("finally")
	public static void main(String args[]) throws Exception 
	{	
		String complexityOutputFileName = "C:\\Users\\msam34\\git\\iOOBNFinal_v1\\iOOBNFinal\\editor\\huginIntegration\\SIIC_Output_tabular.txt";
		PrintWriter pw = null;
		pw =  new PrintWriter(new FileWriter(complexityOutputFileName));
        String line;
		
		ArrayList<String> dirList = generateDirectoriesNameList();
		if(!onlyDisplatTime) System.out.println("The list of dir file names generated " + dirList);
		int count = 0;
		int completedoneCount = 0;
		boolean exception = false;
		String str = "NumOfClass \t int NumOfObject \t NumOfNodes \t NumOfAvgPar \t NumOfStates \t Hugin \t SIIC\n";
		pw.println(str);
//		pw.close();// we are closing this again and again just to atleat save the part that worked well and to avoid loss of data due to abrupt closure of the function
//		String dir = "GenerateAutoOOBN\\nClasses_1#nObjects_1#nStates_3#nNodes_5#maxInDeg_15#maxArcs_6\\";
//		try {
			for(String dir : dirList) {
				timingInfo foldedTime = new timingInfo();
	//			pw =  new PrintWriter(new FileWriter(complexityOutputFileName)); // we are opening-appending-closing this again and again just to atleat save the part that worked well and to avoid loss of data due to abrupt closure of the function
				for(int j = 0; j < folds; j++) 
				{
					exception = false;
					String additionalDir = "GeneratedFiles\\";
					String fileName = "temp_main.class";
					SIICompilation SIIC = new SIICompilation();
					System.out.println("############## The file to be compiled : "+ dir + additionalDir + fileName + " ########################\n");
					count++;
					SIIC.tInfo = new timingInfo();
					try {
					
					/*
					 * Hugin compilation
					 * 
					 * */
					
					SIIC.performHuginCompilation(dir, "main.oobn");
					
					/*
					 * SII compilation
					 * 
					 * */
					SIIC.performCompilation(dir, additionalDir, fileName);
					System.out.println("************************* Compilation done !!! *************************\n\n\n");
					SIIC.tInfo.datasetProp = dir.replace("GenerateAutoOOBN\\", "");
	//				System.out.println(SIIC.tInfo);
					foldedTime.addTimingInfo(SIIC.tInfo);
					completedoneCount++;
					}
					catch(RuntimeException re){
						exception = true;
						System.out.println("Some exception arose!!!");
					}
					finally {
						
						if(exception) {
							System.out.println(dir + additionalDir + fileName+"\n************************* Compilation incomplete !!! *************************\n\n\n");
						}
						continue;
					}
				}
				foldedTime.averageTimingInfo();
				System.out.println(foldedTime);
				foldedTime.printTableInFile(pw);
			}
//		}
//		catch(RuntimeException re) {
//			System.out.println("Some exception arose!!!");
//		}
//		finally {
//			pw.close();
//		}
		System.out.println("All " + count + " cases are tried!!! Among them " + (count-completedoneCount) + " cases were incomplete; " + completedoneCount + " cases were complete!!!");
		pw.close();
	}

	public void performHuginCompilation(String dir, String fileName) throws Exception {
		Instant startHugin = Instant.now();
    	
		LoadAndPropagateOOBN lap = new LoadAndPropagateOOBN();
		ArrayList<String> fileNames = new ArrayList<String>();
		ArrayList<String> classNames = new ArrayList<String>();
		
//		System.out.println("Hugin is compiling now : " +(dir+fileName));

		fileNames.add(dir+"main.oobn");
		copyAllDependantFile(dir);
		classNames.add("main");
    	lap.LAP(fileNames, classNames, null, "");
		Instant endHugin = Instant.now();
//		System.out.println("Hugin Compilation time : " + Duration.between(startHugin, endHugin).toMillis());
		huginCompilation = Duration.between(startHugin, endHugin).toMillis();
		tInfo.huginTimingBreakDown.add("Compilation");
		tInfo.huginTimingBreakDown.add(Long.toString(huginCompilation));
	}

	public static void copyAllDependantFile(String dir) throws IOException {
		NPPCompiler dr = new NPPCompiler();
		ArrayList<String> fileNameList = dr.fileListFromDir(dir);
		for(String fN : fileNameList) {
			String []fileNamePart = fN.split("\\\\");
			String dest = fileNamePart[fileNamePart.length-1];
//			System.out.println("Now copying " + (dir+fN+".oobn") + " to " + (dest+".oobn"));
			File fsrc = new File(dir+fN+".oobn");
			File fdst = new File(dest+".oobn");
			copyFileUsingStream(fsrc, fdst);
		}
		
	}
	
	private static void copyFileUsingStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}

}

class RefLink{
	String idSrc = "";
	String nameSrc = "";
	String idTarget = "";
	String nameTarget = "";
	int isEIOSrc = 2;// default 2 means Output for pseudo ref link
	int isEIOTarget = 0;// default 0 means Embedded for pseudo ref link
	
	public String toString(){
		String generatedXML = "";
		generatedXML+= "\t<RefLink>\n";
		generatedXML+= ("\t\t<source id=\"" + idSrc + "\"" + " name = \"" + nameSrc +  "\"" + " kind = \"" + isEIOSrc +  "\" />\n");
		generatedXML+= ("\t\t<target id=\"" + idTarget + "\"" + " name = \"" + nameTarget +  "\"" + " kind = \"" + isEIOTarget +  "\" />\n");
		generatedXML+= "\t</RefLink>\n";
		
		return generatedXML;
	}
}

class timingInfo{
//	HashMap<String, Long> sIICTimingBreakDown;
//	HashMap<String, Long> huginTimingBreakDown;

	ArrayList<String> sIICTimingBreakDown;
	ArrayList<String> huginTimingBreakDown;
	String totalSIICTime;
	String datasetProp;
	int nNodes = 0;
	int nObjects = 0;
	int nClasses = 0;
	int nStates = 0;
	int avgPar = 0;
	
	// countFold is required to calculate average running time of multiple fold
	int countFold = 1;// for no call to addTimingInfo, there is only one fold of execution done, we assumed
	
	public timingInfo() {
		this.sIICTimingBreakDown = new ArrayList<String>();
		this.huginTimingBreakDown = new ArrayList<String>();
		this.huginTimingBreakDown.add("Pre-Processing");
		this.huginTimingBreakDown.add("unknown");
		this.datasetProp = "";
		this.totalSIICTime = "";
	}
	
	public void printTableInFile(PrintWriter pw) {
		int hugSize = 0;
		if(this.huginTimingBreakDown != null && this.huginTimingBreakDown.size()!=2)
		{
			hugSize = this.huginTimingBreakDown.size()-1;
			System.out.println("hugSize = " + hugSize+" "+this.huginTimingBreakDown);
			pw.print("(){}<>{}() " + this.nNodes + "\t\t" + this.avgPar + "\t\t\t" + this.nStates + "\t\t" + this.nClasses + "\t\t" + this.nObjects + "\t\t" + this.huginTimingBreakDown.get(hugSize) + "\t\t" + this.totalSIICTime+"\t\t"+this.sIICTimingBreakDown.get(1)+ "\n");
			System.out.println("(){}<>{}()" + this.nNodes + "\t\t" + this.avgPar + "\t\t\t" + this.nStates + "\t\t" + this.nClasses + "\t\t" + this.nObjects + "\t\t" + this.huginTimingBreakDown.get(this.huginTimingBreakDown.size()-1) + "\t\t" + this.totalSIICTime+"\t\t"+this.sIICTimingBreakDown.get(1)+ "\n");
		}
		
//		pw.close();
	}

	public void addTimingInfo(timingInfo ti2) {
		if(this.huginTimingBreakDown.size() > 2) {// for hugin time, there is always 2 entries added  by default
			for(int i = 2; i < this.huginTimingBreakDown.size();  i+=2) {// we don't need to add the 1st two info
				String s1 = this.huginTimingBreakDown.get(i+1);
				String s2 = ti2.huginTimingBreakDown.get(i+1);
				long sum = Long.parseLong(s1) + Long.parseLong(s2);
				this.huginTimingBreakDown.set(i+1, Long.toString(sum));
			}
		}
		else {
			for(int i = 2; i < ti2.huginTimingBreakDown.size();  i+=2) {// we don't need to add the 1st two info
				this.huginTimingBreakDown.add(ti2.huginTimingBreakDown.get(i));
				this.huginTimingBreakDown.add(ti2.huginTimingBreakDown.get(i+1));
			}
		}
		
		if(this.sIICTimingBreakDown.size() == 0) {
			this.sIICTimingBreakDown.addAll(ti2.sIICTimingBreakDown);
			this.totalSIICTime = ti2.totalSIICTime;
			this.datasetProp = ti2.datasetProp;
		}
		else {
			for(int i = 0; i < this.sIICTimingBreakDown.size();  i+=2) {
				String s1 = this.sIICTimingBreakDown.get(i+1);
				String s2 = ti2.sIICTimingBreakDown.get(i+1);
				long sum = Long.parseLong(s1) + Long.parseLong(s2);
				this.sIICTimingBreakDown.set(i+1, Long.toString(sum));
			}
			this.totalSIICTime = Integer.toString(Integer.parseInt(this.totalSIICTime) + Integer.parseInt(ti2.totalSIICTime));
			this.countFold++;
		}
		
		
	}
	
	public void averageTimingInfo() {
		for(int i = 2; i < this.huginTimingBreakDown.size();  i+=2) {
			String s1 = this.huginTimingBreakDown.get(i+1);
			long avg = Long.parseLong(s1) / this.countFold;
			this.huginTimingBreakDown.set(i+1, Long.toString(avg));
		}
		for(int i = 0; i < this.sIICTimingBreakDown.size();  i+=2) {
			String s1 = this.sIICTimingBreakDown.get(i+1);
			long avg = Long.parseLong(s1) / this.countFold;
			this.sIICTimingBreakDown.set(i+1, Long.toString(avg));
		}
		
		if(this.totalSIICTime.equalsIgnoreCase("")) this.totalSIICTime = "0";
		this.totalSIICTime = Integer.toString(Integer.parseInt(this.totalSIICTime) / this.countFold);
	}
	
	public void parseProperty() {
		String info[] = this.datasetProp.split("#");
		for(String s : info) {
			String d[] = s.split("_");
			if(d[0].equalsIgnoreCase("nClasses")) {
				this.nClasses = Integer.parseInt(d[1]);
			}
			else if(d[0].equalsIgnoreCase("nObjects")) {
				this.nObjects = Integer.parseInt(d[1]);
			}
			else if(d[0].equalsIgnoreCase("nStates")) {
				this.nStates = Integer.parseInt(d[1]);
			}
			else if(d[0].equalsIgnoreCase("nNodes")) {
				this.nNodes = Integer.parseInt(d[1]);
			}
			else if(d[0].equalsIgnoreCase("maxInDeg")) {
				this.avgPar = Integer.parseInt(d[1]);
			}
		}
	}
	
	public String toString() 
	{
		parseProperty();
		
		String timeString = "";
		
		int eventLength = 44;
		int timeLength = 12;
		
		String nNodeStr = Integer.toString(this.nNodes);
		String nclassStr = Integer.toString(this.nClasses);
		String nobjStr = Integer.toString(this.nObjects);
		String nStatesStr = Integer.toString(this.nStates);
		String avgParStr = Integer.toString(this.avgPar);
		
		nNodeStr = paddLeadingTrailingSpace(timeLength, nNodeStr);
		nclassStr = paddLeadingTrailingSpace(timeLength, nclassStr);
		nobjStr = paddLeadingTrailingSpace(timeLength, nobjStr);
		nStatesStr = paddLeadingTrailingSpace(timeLength, nStatesStr);
		avgParStr = paddLeadingTrailingSpace(timeLength, avgParStr);
		
		String OOBNProperty = "\t\t-------------------------------------------------------------\n";
		OOBNProperty       += "\t\t|                      OOBN Property                        |\n";
		OOBNProperty       += "\t\t-------------------------------------------------------------\n";
		OOBNProperty       += "\t\t|                    Property                   |    Info   |\n";
		OOBNProperty       += "\t\t-------------------------------------------------------------\n";
		OOBNProperty       += "\t\t|           Number of Nodes (each class)        |"+ nNodeStr +"|\n";
		OOBNProperty       += "\t\t|           Number of Instanciated Class        |"+ nclassStr+"|\n";
		OOBNProperty       += "\t\t|           Number of Objects (per class)       |"+ nobjStr  +"|\n";
		OOBNProperty       += "\t\t|           Number of states (per node)         |"+ nStatesStr+"|\n";
		OOBNProperty       += "\t\t|           Average parent (per node)           |"+ avgParStr+"|\n";
		OOBNProperty       += "\t\t-------------------------------------------------------------\n";
		
		
		String timeStringHugin = "\t\t-----------------------------------------------------------\n";
		timeStringHugin       += "\t\t|                   Hugin Time Analysis                   |\n";
		timeStringHugin       += "\t\t-----------------------------------------------------------\n";
		timeStringHugin       += "\t\t|                    Event                   |  Time (ms) |\n";
		timeStringHugin       += "\t\t-----------------------------------------------------------\n";
		
		for(int i = 0; i < this.huginTimingBreakDown.size();  i+=2) {
			String event = "\t\t|";
			event += this.huginTimingBreakDown.get(i);
			for(int j = 0; j < eventLength-this.huginTimingBreakDown.get(i).length(); j++)
				event += " ";
			event += "|";
			
			String time = "";
			for(int j = 0; j < timeLength-this.huginTimingBreakDown.get(i+1).length(); j++)
				time += " ";
			time += this.huginTimingBreakDown.get(i+1);
			time += "|";
			
			timeStringHugin += event + time + "\n";
		}
		timeStringHugin       += "\t\t-----------------------------------------------------------\n";
		
		String wsTotalH = "";
		int x = 3;
		if(this.huginTimingBreakDown.size() < 4)
			x = 1;
		
		for(int j = 0; j < 12-this.huginTimingBreakDown.get(x).length()-1; j++)
			wsTotalH += " ";
		
		timeStringHugin       += "\t\t|Total Hugin";
		for(int j = 0; j < 44-"Total Hugin".length(); j++)
			timeStringHugin += " ";
		timeStringHugin += "|"+wsTotalH+this.huginTimingBreakDown.get(x)+"+|\n";
		timeStringHugin       += "\t\t-----------------------------------------------------------\n";
		
		String timeStringSIIC  = "\t\t-----------------------------------------------------------\n";
		timeStringSIIC       += "\t\t|                   SIIC Time Analysis                    |\n";
		timeStringSIIC       += "\t\t-----------------------------------------------------------\n";
		timeStringSIIC       += "\t\t|                    Event                   |  Time (ms) |\n";
		timeStringSIIC       += "\t\t-----------------------------------------------------------\n";
		
		for(int i = 0; i < this.sIICTimingBreakDown.size();  i+=2) {
			String event = "\t\t|";
			event += this.sIICTimingBreakDown.get(i);
			for(int j = 0; j < eventLength-this.sIICTimingBreakDown.get(i).length(); j++)
				event += " ";
			event += "|";
			
			String time = "";
			for(int j = 0; j < timeLength-this.sIICTimingBreakDown.get(i+1).length(); j++)
				time += " ";
			time += this.sIICTimingBreakDown.get(i+1);
			time += "|";
			
			timeStringSIIC += event + time + "\n";
		}
		timeStringSIIC       += "\t\t-----------------------------------------------------------\n";
		String wsTotal = "";
		for(int j = 0; j < 12-totalSIICTime.length(); j++)
			wsTotal += " ";
		timeStringSIIC       += "\t\t|Total SIIC";
		for(int j = 0; j < 44-"Total SIIC".length(); j++)
			timeStringSIIC += " ";
		timeStringSIIC += "|"+wsTotal+totalSIICTime+"|\n";
		timeStringSIIC       += "\t\t-----------------------------------------------------------\n";
		
		timeString = OOBNProperty + "\n\n" + timeStringHugin + "\n\n" + timeStringSIIC;
		
		return timeString;
	}

	public String paddLeadingTrailingSpace(int timeLength, String mainStr) {
		String newStr = "";
		
		int reqSpace = timeLength-mainStr.length();
		for(int i = 0; i < reqSpace/2; i++)
			newStr += " ";
		newStr += mainStr;
		for(int i = reqSpace/2+1; i < reqSpace; i++)
			newStr += " ";
		
		return newStr;
	}
	
}
