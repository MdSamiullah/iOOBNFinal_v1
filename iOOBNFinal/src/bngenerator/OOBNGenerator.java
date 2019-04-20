package bngenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import ANTLR_NPP.NPPCompiler;
import huginIntegration.LoadAndPropagateOOBN;

public class OOBNGenerator {
	public static int folds = 5;
    int nClasses=0;
    int nObjects=1;// initially this is 0 but if nClasses != 0 then default will be 1
    boolean nClassesWasSet = false;// this will help define nObjs's default value, if true then nObjs = 1
    String structure="multi"; // default structure
    String format="oobn"; // default format
    String baseFileName = "Class"; // default file name
    Integer line = null; // Default value should also be provided
    Integer nbNodes=null;
    int maxValues=2; //  Default is binary nodes.
    boolean fixed_nVal = false;
    int nIterations=0;
    int maxInducedWidth=-1; // this value means that there is no induced width constraint
    int numberNodes=5;
    int numberMaxDegree=3;
    int numberMaxInDegree=3;
    int numberMaxOutDegree=3;
    int numberMaxArcs=6;
    
    String namePrefix = "";// this is going to be the name of the class or prefix of additional classes of the objects
	
    
    // auxiliar variables
    boolean maxDegreeWasSet = false;
    boolean maxInDegreeWasSet = false;
    boolean maxOutDegreeWasSet = false;
    boolean maxArcsWasSet = false;
    boolean generate=true;
    boolean loadNetwork=false; // auxiliar variables to load structure from xml file
    String auxName = "default"; // auxiliar variables to load structure from xml file
	float lowerP=0;
    float upperP=1;

	public static void printHelp(){
        System.out.println();
        System.out.println("Type: [-option value]...");
        System.out.println("Available command options:");
        System.out.println("\tNumber of nodes:[-nNodes value]");
        System.out.println("\tMax degree for each node: [-maxDegree value]");
        System.out.println("\tMaximum number of incoming arcs for each node: [-maxInDegree value]");
        System.out.println("\tMaximum number of outgoing arcs for each node: [-maxOutDegree value]");
        System.out.println("\tMaximum total number of arcs: [-maxArcs value]");
        System.out.println("\tMaximum induced-width (tree-width) allowed: [-maxIW value]");
        System.out.println("\tMaximum number of values (states) for each node: [-maxVal value]");
        System.out.println("\tThis option fix the number of states to maxVal: [-fixed_maxVal]");
        System.out.println("\tNumber of generated graphs/Classes from which objects will be created (default is 0): [-nOOBNs value]");
        System.out.println("\tNumber of copies of graphs/Instances (defualt is 0, but if -nOOBNs defined, default will be 1 ): [-nObj value]");
		System.out.println("\tName of saved file: [-fName name]");
    }
	
	public void dataAquisition(String argv[]){
		System.out.println("length " + argv.length);
        //// Data acquisition ////
        if( argv.length > 0 ) {
            for( int i = 0; i < argv.length; i++ ) {
               String param = argv[i];
               System.out.println("param " + param);
                //******* Generated graph informations *******************************
                // a) Structural informations
                // dag structure type
                
                // number of nodes
                if (param.compareTo("-nNodes") == 0){
                	System.out.println("Got matched in -nNodes");
                    i++;
                    nbNodes = new Integer(argv[i]);
                    this.numberNodes = nbNodes.intValue();
                    if (this.numberNodes < 5)    {
                        System.out.println();
                        System.out.println("Number of nodes must be larger than 3!");
                        System.out.println("-nNodes was set to 5.");
                        this.numberNodes=5;
                    }
                }
                // maximum degree for all nodes
                else if (param.compareTo("-maxDegree") == 0) {
                    i++;
                    line = new Integer(argv[i]);
                    this.numberMaxDegree = line.intValue();
                    if (this.numberMaxDegree < 3)    {
                        System.out.println();
                        System.out.println("Degree of nodes must be larger than 2!");
                        System.out.println("-maxDegree was set to 3.");
                        this.numberMaxDegree=3;
                    }
                    this.maxDegreeWasSet=true;
                }
                // maximum number of incoming arcs
                else if (param.compareTo("-maxInDegree") == 0) {
                    i++;
                    line = new Integer(argv[i]);
                    this.numberMaxInDegree = line.intValue();
                    this.maxInDegreeWasSet=true;
                    if (this.numberMaxInDegree < 1)    {
                        System.out.println();
                        System.out.println("Value of maxInDegree has no sense. At least, one node will have a parent!");
                        System.out.println("-maxInDegree was set to 2.");
                        this.numberMaxInDegree=2;
                    }
                    if (numberMaxInDegree==1)    {
                        System.out.println();
                        System.out.println("Note that incoming arcs equal to 1 (-maxInDegree=1) just has sense in polytrees!");
                    }
                }
                // maximum number of outgoing arcs
                else if (param.compareTo("-maxOutDegree") == 0) {
                    i++;
                    line = new Integer(argv[i]);
                    this.numberMaxOutDegree = line.intValue();
                    this.maxOutDegreeWasSet=true;
                    if (this.numberMaxOutDegree < 1)    {
                        System.out.println();
                        System.out.println("Value of maxOutDegree has no sense. At least, one node will have a children!");
                        System.out.println("-maxOutDegree was set to 2.");
                        this.numberMaxOutDegree=2;
                    }
                    if (this.numberMaxOutDegree==1)    {
                        System.out.println();
                        System.out.println("Note that outgoing arcs equal to 1 (-maxOutDegree=1) has sense in polytrees!");
                    }
                }
                // maximum number of total number of arcs in the network
                else if (param.compareTo("-maxArcs") == 0) {
                    i++;
                    line = new Integer(argv[i]);
                    this.numberMaxArcs = line.intValue();
                    this.maxArcsWasSet=true;
                }
                // maxim allowed induced width
                else if (param.compareTo("-maxIW") == 0){	// for testing Uniformity
                    i++;
                    line = new Integer(argv[i]);
                    this.maxInducedWidth=line.intValue();
                    if (this.maxInducedWidth < 2)  {
                        System.out.println();
                        System.out.println("The minimum permited induced width is 2!");
                        System.out.println("-maxIW was set to 2.");
                        this.maxInducedWidth=2;
                    }
                }// end of if & else´s
                // b) Distributions informations
                // set maximum number of values of each node
                else if (param.compareTo("-maxVal") == 0){
                    i++;
                    line = new Integer(argv[i]);
                    this.maxValues=line.intValue();
                }
                // make the number of values for each node fixed
                else if (param.compareTo("-fixed_maxVal") == 0) {
                    //i++;
                	this.fixed_nVal = true;
                }
                
                //******* Generating process informations *******************************
                // number of graphs to generate
                else if (param.compareTo("-nOOBNs") == 0) {
                    i++;
                    System.out.println("Got matched in -nOOBNs");
                    line = new Integer(argv[i]);
                    this.nClasses = line.intValue();
//                    if(this.nClasses > 0)	nObjects = 1;
                }
                
                else if (param.compareTo("-nObj") == 0) {
                    i++;
                    line = new Integer(argv[i]);
                    this.nObjects = line.intValue();
                }
                // base file name to use
                else if (param.compareTo("-fName") == 0){
                    i++;
                    this.baseFileName = argv[i];
                }
                // help option
                else if (param.compareTo("-help") == 0){
                    i++;
                    this.generate=false;
                    System.out.println();
                    System.out.println("BNGeneratorOwn - a generator for random Bayesian network. Version (0.4)");
                    this.printHelp();
                    System.exit(0);
                } else    {
                    System.out.println("Probably, you have typed a wrong option in OOBN generator! Please, verify your entry.") ;
                    this.printHelp();
                    System.exit(0);
                }
            } // end of for
            System.out.println();
        } // end of if(argv.length>0)
        
        namePrefix += "nClasses_"+nClasses+"#";
        namePrefix += "nObjects_" + nObjects + "#";
        namePrefix += "nStates_" + maxValues + "#";
        
        
        namePrefix += "nNodes_" + numberNodes + "#";
//        namePrefix += "maxDeg_" + numberMaxDegree + "#";
        namePrefix += "maxInDeg_" + numberMaxInDegree + "#";
//        namePrefix += "maxOutDeg_" + numberMaxOutDegree + "#";
        namePrefix += "maxArcs_" + numberMaxArcs;
        
	}// end of data acquisition method
	
	public static void main(String argv[]) throws Exception
	{
//		String OOBNSpecific [] = {"-nOOBNs"};
//		String BNSpecific [] = {"-format", "oobn", "-fixed_maxVal"};
		
		Scanner sc = new Scanner(System.in);
		
        if( argv.length<1)  {
        	System.out.println("Do you want a default OOBN? (Y/y or N/n)");
        	String choice = sc.nextLine();
        	
        	if(choice.equalsIgnoreCase("y")){
        	
	            System.out.println("A default OO-Bayesian network will be generated. Enter with your options!");
	            printHelp();
	            
        	}
        	else{
        		System.out.println("Do you want to generate OOBNs based on predefined parameters (Y/y or N/n)?");
        		choice = sc.nextLine();
        		if(choice.equalsIgnoreCase("y")){
        			generateAllPossibleParameters();
        		}
        		else{
	        		printHelp();
	        		String params = "";
	        		params = sc.nextLine();
	        		argv = params.split(" ");
	        		
	        		OOBNGenerator oobn = new OOBNGenerator();
	                oobn.generateOOBNHugin(argv, folds);
        		}
        		
        		
        	}
        }// end of if for argv initialization
        
        
	}// end of main class

	public static void generateAllPossibleParameters() throws Exception {
		String []argv = {"-nNodes", "5", "-maxVal", "2", "-maxInDegree", "5", "-nOOBNs","3", "-nObj", "5", "-fixed_maxVal"};
		
		
//		int []bnSize = {5, 20, 50};
//		int []arity = {2, 3, 4};
//		int []maxParent = {2, 3, 5};
//		int []additionalClass = {0, 1, 2, 3};
//		int []numObjPerAdditionalClass = {1, 2, 3};
		
		int []bnSize = {5, 10, 15, 20, 25, 30, 50};
		int []arity = {2, 3, 4, 5};
		int []maxParent = {2, 3, 4, 5};
		int []additionalClass = {0, 1, 2, 3};
		int []numObjPerAdditionalClass = {1, 2, 3, 4};

//		int []bnSize = {5};
//		int []arity = {2};
//		int []maxParent = {5};
//		int []additionalClass = {1};
//		int []numObjPerAdditionalClass = {3};
		
		for(int a = 0; a < bnSize.length; a++){
			argv[1] = Integer.toString(bnSize[a]);
			for(int b = 0; b < arity.length; b++){
				argv[3] = Integer.toString(arity[b]);
				for(int c = 0; c < maxParent.length; c++){
					argv[5] = Integer.toString(maxParent[c]);
					for(int d = 0; d < additionalClass.length; d++){
						argv[7] = Integer.toString(additionalClass[d]);
						for(int iter = 0; iter < folds; iter++) {
							if(additionalClass[d] == 0) {
								argv[9] = "0";
								OOBNGenerator oobn = new OOBNGenerator();
								System.out.println("Now generating: " + argv [0] + " " + argv [1] + " "+ argv [2] + " "+ argv [3] + " "+ argv [4] + " "+ argv [5] + " "+ argv [6] + " "+ argv [7] + " "+ argv [8] + " "+ argv [9] + " "+ argv [10]);
						        oobn.generateOOBNHugin(argv, iter+1);
							}
							else{
								for(int e = 0; e < numObjPerAdditionalClass.length; e++){
									argv[9] = Integer.toString(numObjPerAdditionalClass[e]);
									OOBNGenerator oobn = new OOBNGenerator();
									System.out.println("Now generating: " + argv [0] + " " + argv [1] + " "+ argv [2] + " "+ argv [3] + " "+ argv [4] + " "+ argv [5] + " "+ argv [6] + " "+ argv [7] + " "+ argv [8] + " "+ argv [9] + " "+ argv [10]);
							        oobn.generateOOBNHugin(argv, iter+1);
								}
							}
						}
					}
				}
			}
		}
		
	}

	public void generateOOBNHugin(String[] argv, int iter) throws Exception {
		dataAquisition(argv);
        String []argv2 = new String[]{"-nNodes", Integer.toString(numberNodes), "-format", "oobn", "-maxVal", Integer.toString(maxValues), "-fixed_maxVal", "-maxInDegree", Integer.toString(numberMaxInDegree)};
        boolean exception = true;
        while (exception == true) {
	        ArrayList<BNGeneratorOwn> bns = new ArrayList<BNGeneratorOwn>();
	        // +1 in the next loop because 1 OOBN class for the main class that contains n objects from n classes
	        // this is the 1st class that is going to be the base class
	        BNGeneratorOwn tempBN = new BNGeneratorOwn();
	    	tempBN.generateBN(argv2);
	
	    	tempBN.oobn.className = "main";// this should always be placed after generateBN() call
	    	bns.add(tempBN);
	        
	        for(int I = 1; I < nClasses+1; I++){
	        	tempBN = new BNGeneratorOwn();
	        	tempBN.generateBN(argv2);
	
	        	tempBN.oobn.className = "main"+I;// this should always be placed after generateBN() call
	        	bns.add(tempBN);
	        }
	        
	        for(int I = 1; I < nClasses+1; I++){
	        	for(int J = 0; J < nObjects; J++){
	//        		String className = oobn.namePrefix + "#" + I;
	        		String className = "main"+I;
	        		String objName = "Obj" + J + "C" + I;
	        		Binding tempBinding = new Binding();
	        		tempBinding.className = className;
	        		tempBinding.objName = objName;
	//        		
	        		AddObject(bns.get(0), bns.get(I), className, objName, tempBinding);
	        		bns.get(0).oobn.instances.add(tempBinding);
	        	}
	        }
	                
	     // some values required to compute object and other node info in main class
	        double x,y;
	//        int nDegree=tempBN.numberMaxDegree+1;
	        int nDegree=tempBN.numberMaxDegree+ 1 + nClasses * nObjects;
	        int contaEmX[]= new int[nDegree];
	        
	        for (int i=0;i<bns.get(0).oobn.nodes.size() + bns.get(0).oobn.instances.size();i++ ) {
	            // code segment to update node position info
	        	Binding bnd = null;
	        	Node tempNode = bns.get(0).oobn.nodes.get(Integer.toString(i));
	        	int nP  = 0;
	        	if(tempNode != null){
	        		nP=tempNode.parents.size();
	                y=30+120*nP+15*Math.sin(3.1415/2*contaEmX[nP]);
	                x=30+120*contaEmX[nP]+15*Math.cos(3.1415*nP);
	                int xx=(int)x;
	                int yy=(int)y;
	
	                tempNode.posX = xx;
	                tempNode.posY = yy;
	        	}
	        	else{// assuming i > bns.get(0).oobn.nodes.size()
	        		bnd = bns.get(0).oobn.instances.get(i-bns.get(0).oobn.nodes.size());
	        		nP = bnd.inputBinding.size();
	        		
	                y=30+120*nP+15*Math.sin(3.1415/2*contaEmX[nP]);
	                x=30+120*contaEmX[nP]+15*Math.cos(3.1415*nP);
	                int xx=(int)x;
	                int yy=(int)y;
	
	                bnd.posX = xx;
	                bnd.posY = yy;
	        	}
	
	            contaEmX[nP]++;
	        }
	        
	//        System.out.println(bns.get(0).oobn);
	        // now generate codes for each of the oobns generated so far
	        String directory = "GenerateAutoOOBN\\" + namePrefix + "\\";
	       
	        for(int I = 0; I < nClasses+1; I++){
	        	
	        	generateHuginOOBNCode(bns.get(I), directory, iter);
	        }
	        
	        // now we can try compiling the OOBN to check for validity
	        // if it compiles then okay, else try generating a new one
	        
	        try {
				directory += "_"+iter+"\\";
				System.out.println("Now compiling " + directory);
				/*
				 * Hugin compilation
				 */
	        		String huginFileName = "main.oobn";
//	        		Scanner sc = new Scanner(System.in);
//	        		String xx = sc.next();
					performHuginCompilation(directory, huginFileName);
					System.out.println("No exceptions, successfully generated OOBN!!!");
					exception = false;
				}
				catch(Exception e){
//					exception = true;
					System.out.println("Some exceptions arose!!!" + e);
				}
//				finally {
	        	System.out.println(exception);
				if (exception == true) continue;
				else break;
//				}
        }
	}

	/** Creates parent directories if necessary. Then returns file */
	private static File fileWithDirectoryAssurance(String directory, String filename) {
	    File dir = new File(directory);
	    if (!dir.exists()) dir.mkdirs();
	    return new File(directory + "\\" + filename);
	}
	
	public void generateHuginOOBNCode(BNGeneratorOwn bn, String directory, int iter) throws IOException {
		
//		String tempInputFile = directory + bn.oobn.className+".oobn";
		String tempInputFile = bn.oobn.className+".oobn";
		
        //create the directory if doesn't exist
		File file = fileWithDirectoryAssurance(directory+"_"+iter, tempInputFile);
		
		FileWriter write = new FileWriter(file);
		PrintWriter print_line = new PrintWriter(write);
		System.out.println(bn.oobn);
		print_line.println(bn.oobn);
		print_line.close();
	}

	public void AddObject(BNGeneratorOwn bn1, BNGeneratorOwn bn2, String className, String objName, Binding tempBinding) {
		int numIn = bn2.oobn.inputs.size();
		int numOut = bn2.oobn.outputs.size();
		
		int inputBindLimit = (numIn * bn1.oobn.nodes.size())/ (numIn + numOut);// the nodes that can be used for input binding
		
		int maxLevelToUseForInputBind = 0;
		for(int I = 0; I < bn1.oobn.cumNodeCountLevelWise.size(); I++){
			if(bn1.oobn.cumNodeCountLevelWise.get(I) >= inputBindLimit)	{maxLevelToUseForInputBind = I; break;}
		}
		
		bindInputNodes(bn1, bn2, 0, maxLevelToUseForInputBind, className, objName, tempBinding);
		System.out.println("MAX Limit " + maxLevelToUseForInputBind);
//		System.out.println(bn1.oobn);
		bindOutputNodes(bn1, bn2, maxLevelToUseForInputBind+1, bn1.oobn.cumNodeCountLevelWise.size(), className, objName, tempBinding);
//		System.out.println(bn1.oobn);
		
	}

	public void bindInputNodes(BNGeneratorOwn bn1, BNGeneratorOwn bn2, int startLevel, int endLevel, String className, String objName, Binding tempBinding) {
		
		int n = bn2.oobn.inputs.size();
		Node smallestDegreeNodes[] = new Node[n];
		
		ArrayList<Node> nodesInTheRange = new ArrayList<Node>();
		
		for(int i = startLevel; i <= endLevel; i++){
			for(String tempNode : bn1.oobn.levelToNode.get(i)){
				Node tNode = bn1.oobn.nodes.get(tempNode);
				nodesInTheRange.add(tNode);
			}
		}
		Node potentialNodesToBind [] = nodesInTheRange.toArray(new Node[nodesInTheRange.size()]);
		
		if(potentialNodesToBind.length < n) System.err.println("Partition for IO binding is not good enough!!!");
		else{
			potentialNodesToBind = sortOutDegree(potentialNodesToBind);
			smallestDegreeNodes = findNSmallestDegreeNode(potentialNodesToBind, n);
			for(Node x: smallestDegreeNodes)	System.out.print(x.name + " ");
			System.out.println();
		
		
			// adding input nodes of bn2 with some nodes in bn1 will add parents to the input nodes of bn2 and children to those nodes of bn1
			int i = 0;
			for(String inp: bn2.oobn.inputs){
	//			System.out.println("smallest nodes " + smallestDegreeNodes[i].name + " End ");
				
				String newName = objName + "_node" + inp;
				bn1.oobn.nodes.get(smallestDegreeNodes[i].name).children.add(newName);
				
				BindPair bp = new BindPair();
				bp.formalParam = "node"+inp;
				bp.actualParam = "node"+smallestDegreeNodes[i].name;
				tempBinding.inputBinding.add(bp);
				
				i++;
				// we are not adding parents of bn2's input nodes, since in OOBN of hugin, mapping/binding is done in embedding class only
			}
		}
	}
	
	public void bindOutputNodes(BNGeneratorOwn bn1, BNGeneratorOwn bn2, int startLevel, int endLevel, String className, String objName, Binding tempBinding) {
		
		int n = bn2.oobn.outputs.size();
		Node smallestDegreeNodes[] = new Node[n];
		
		ArrayList<Node> nodesInTheRange = new ArrayList<Node>();
		
		for(int i = startLevel; i < endLevel; i++){
			for(String tempNode : bn1.oobn.levelToNode.get(i)){
				Node tNode = bn1.oobn.nodes.get(tempNode);
				nodesInTheRange.add(tNode);
			}
		}
		Node potentialNodesToBind [] = nodesInTheRange.toArray(new Node[nodesInTheRange.size()]);
		
		if(potentialNodesToBind.length < n) System.err.println("Partition for IO binding is not good enough!!!");
		else{
			potentialNodesToBind = sortInDegree(potentialNodesToBind);
			smallestDegreeNodes = findNSmallestDegreeNode(potentialNodesToBind, n);
			for(Node x: smallestDegreeNodes)	System.out.print(x.name + " ");
			System.out.println();
		
		
			// adding output nodes of bn2 with some nodes in bn1 will add children to the output nodes and children to those nodes 
			int i = 0;
			for(String outp: bn2.oobn.outputs){
				
				String newName = objName + "_node" + outp;
				bn1.oobn.nodes.get(smallestDegreeNodes[i].name).parents.add(newName);
				
				BindPair bp = new BindPair();
				bp.formalParam = newName;
				bp.actualParam = "node"+outp;// If I am not wrong then in hugin formal and actual both are same
				tempBinding.outputBinding.add(bp);
				
				i++;
				// we are not adding children of bn1's output nodes, since in OOBN of hugin, mapping/binding is done in embedding class only
			}
		}
	}
	
	public Node[] sortInDegree( Node [] nodes){
		System.out.println("Before sorting inDeg ");
		for(Node x: nodes)	System.out.print(x.name + " ");
		System.out.println();
		
		for(int i = 0; i < nodes.length; i++){
			for(int j = 0; j < nodes.length; j++){
				if(nodes[i].parents.size() < nodes[j].parents.size()){
					Node temp = nodes[i];
					nodes[i] = nodes[j];
					nodes[j] = temp;
				}
			}
		}
		
		System.out.println("After sorting inDeg ");
		for(Node x: nodes)	System.out.print(x.name + " ");
		System.out.println();
		
		return nodes;
	}
	
	public Node[] sortOutDegree(Node [] nodes){
		
		System.out.println("Before sorting OutDeg ");
		for(Node x: nodes)	System.out.print(x.name + " ");
		System.out.println();
		
		for(int i = 0; i < nodes.length; i++){
			for(int j = 0; j < nodes.length; j++){
				if(nodes[i].children.size() < nodes[j].children.size()){
					Node temp = nodes[i];
					nodes[i] = nodes[j];
					nodes[j] = temp;
				}
			}
		}
		System.out.println("After sorting OutDeg ");
		for(Node x: nodes)	System.out.print(x.name + " ");
		System.out.println();
		
		return nodes;
	}

	public Node[] findNSmallestDegreeNode(Node[] potentialNodesToBind, int n) {
		Node smallestDegreeNodes[] = new Node[n];
		
		for(int i = 0; i < n; i++)
			smallestDegreeNodes[i] = potentialNodesToBind[i];
		
		return smallestDegreeNodes;
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
	
	public void performHuginCompilation(String dir, String fileName) throws Exception {
    	
		LoadAndPropagateOOBN lap = new LoadAndPropagateOOBN();
		ArrayList<String> fileNames = new ArrayList<String>();
		ArrayList<String> classNames = new ArrayList<String>();
		System.out.println("Now compiling " + dir+"main.oobn");
		fileNames.add(dir+"main.oobn");
		copyAllDependantFile(dir);
		classNames.add("main");
			lap.LAP(fileNames, classNames, null, "", 2);// for NOS, I have given 2 cause here i don't care about the NOS or complexity
		}

}
