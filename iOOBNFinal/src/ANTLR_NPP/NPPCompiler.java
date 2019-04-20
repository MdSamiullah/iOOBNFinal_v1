package ANTLR_NPP;

/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
***/
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.editor.components.BasicNode;
import com.editor.components.CodeGenerator;
import com.editor.components.XMLGenerator;
import com.mxgraph.frames.FrameInstance;
import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

import ANTLR_NPP.mxCellStructure.Point;
import bngenerator.OOBNGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/*
 * 	1. overlapping management (node, instance, potential, IO)
	2. parent interfaces and classes must be compiled and with the extension .oobn
	If it is a standalone interface or class then .oobn can be ensured using Hugin i.e. easy and standard
	If it is a derived one (interafce or class) then must be in ioobn format but after compilation a ".oobn" 
	version will be generated for this in our system. So, from all angles it is clear.
	Code changing: for parent component extraction, use .oobn extension
 * 
 * */

public class NPPCompiler {

	public int noOfErrors = 0;
	public static int folds = 5;
	public NPPCompiler(){
		noOfErrors = 0;
		
		classinstanceListMap = new HashMap<String, ArrayList<String>>();
		instanceclassMap = new HashMap<String, String>();
		
		childNodeIdParentNodeIdMap = new HashMap<String, ArrayList<String>>();
		
		instanceClassOrderForCompile = new Stack<String>();
		
		heightMap = new HashMap<String, Double>();
		widthMap = new HashMap<String, Double>();
		
		minXMap = new HashMap<String, Integer>();
		minYMap = new HashMap<String, Integer>();
//		runningNode = new BasicNode();
	}
	
//	public static Object[][] dataTable;
	
	public BasicNode runningNode;
	
	/*
	 * this is for maintaining the order of classes (because of instances need to be loaded first before they are used)
	 * in Hugin code
	 * 
	 * The static stack "instanceClassOrderForCompile" is updated in "NewClass.java" class in method "generateCode" with a new name
	 * classOrder 
	 */  
	static Stack<String> instanceClassOrderForCompile = new Stack<String>();
	
	public static HashMap<String, Double> heightMap = new HashMap<String, Double>();
	public static HashMap<String, Double> widthMap = new HashMap<String, Double>();
	
	public static HashMap<String, Integer> minXMap = new HashMap<String, Integer>();
	public static HashMap<String, Integer> minYMap = new HashMap<String, Integer>();
	
	// the "classinstanceListMap" is updated in NPPvisit class 
	public static HashMap<String, ArrayList<String>> classinstanceListMap = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, String> instanceclassMap = new HashMap<String, String>();
	
	public static HashMap<String, ArrayList<String>> childNodeIdParentNodeIdMap = new HashMap<String, ArrayList<String>>();
	
	/*
	 * this function gets the hugin code file from a directory and generates a replica of the hugin file after removing all
	 * comments.
	 *  
	 * */
	public ParseTree performParsing(String inputFile, String directory) throws IOException{
        String tempInputFile = "temp";
        System.out.println(inputFile);
        Scanner Sc = new Scanner (new File(directory+inputFile));
        
        FileWriter write = new FileWriter(tempInputFile);
		PrintWriter print_line = new PrintWriter(write);
		
		// directory+inputFile file is read and comment removed file is generated with name tempInputFile
		// then this tempInputFile is used in the parsing
		String line;
		while(Sc.hasNext()){
			line = Sc.nextLine();
			line = line.split("%")[0];
			print_line.println(line);
		}
        print_line.close();

        InputStream is = System.in;
        if ( tempInputFile!=null ) is = new FileInputStream(tempInputFile);
        ANTLRInputStream input = new ANTLRInputStream(is);
        NETPlusPlusLexer lexer = new NETPlusPlusLexer(input);
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        NETPlusPlusParser parser = new NETPlusPlusParser(tokens);
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);
        
        System.out.println("\n");
        ParseTree tree = parser.prog(); // parse
        
        noOfErrors = ThrowingErrorListener.errorCount;
        
        ThrowingErrorListener.errorCount = 0; // resetting count for other files
        
        return tree;
	}
	
	
	/*
	 * Generate code calls "performParsing()" method for the class to compile and generate parse tree after removing comments
	 * 
	 * Then for all parent classes and interfaces, it generated code in the same way by calling "performParsing()" and get a parse 
	 * tree and combine all component of parent interfaces and classes to the compiling class and generate a hugin code for them using
	 * 
	 * generateCode of the class "NewClass"
	 * */
	
	public void generateCode(String directory, String fileName, String extension) throws Exception
	{
		String className;
        
        className = fileName;
        
        NewClass classFileStructure;
        
        ParseTree tree = performParsing(className+extension, directory);
        
        if(noOfErrors > 0)        System.err.println("Total error found: " + noOfErrors);
        else{
        	NPPvisit traverse = new NPPvisit();
            traverse.visit(tree); // visit() can be found in "String org.antlr.v4.runtime.tree.AbstractParseTreeVisitor.visit(ParseTree tree)"
            
            classFileStructure = new NewClass(className, traverse.classNode.getType());
            classFileStructure.insertOwnComponent(traverse.classNode);
            
            if(traverse.parentClassList != ""){
            	String str = traverse.parentClassList.trim();
            	
            	if((new File(directory + str + extension)).exists() == false){
            		System.err.println("parent class: " + str + "not found");
            	}
            	else{
	            	tree = performParsing(str+extension, directory);
	            	if(noOfErrors > 0)	System.err.println("Total error found: " + noOfErrors);
	            	else{
	            		NPPvisit traverse1 = new NPPvisit();
	                    traverse1.visit(tree);
	                    classFileStructure.insertParentComponent(traverse1.classNode);
	            	}
            	}
            }
            
            String parentInterfaces = traverse.parentInterfaceList;
            
            if(parentInterfaces != ""){
            	String strArray[] = parentInterfaces.split("[ ,]");
            	for( int I = 0; I < strArray.length; I++){
            		if((new File(directory + strArray[I] + extension)).exists() == false){
                		System.err.println("parent interface: " + strArray[I] + " not found");
                	}
                	else{
	            		tree = performParsing(strArray[I]+extension, directory);
	                	if(noOfErrors > 0)	System.err.println("Total error found: " + noOfErrors);
	                	else{
	                		NPPvisit traverse1 = new NPPvisit();
	                        traverse1.visit(tree);
	                        classFileStructure.insertParentComponent(traverse1.classNode);
	                	}
                	}
            	}
            }
            
            /* Overriding is handled here in the following line in combineComponents() method
             * there is a chance of overriding/over writing components with same name in the map of
			 * combined class. So, parent classes components should be added first, to ensure overriding 
			 * prioritize own components. 
			 * 
			 */
            classFileStructure.combineComponents();
            //classFileStructure.printCode(1);
            
            classFileStructure.generateHuginCodeForCombinedClass(directory+"GeneratedFiles\\"+className, 1, instanceClassOrderForCompile); 
            // the generateCode method is in "NewClass.java" class which inserts instances in the stack "instanceClassOrderForCompile"
        }
	}
	
	public ArrayList<String> fileListFromDir(String path)
	{
		ArrayList<String> classList = new ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".oobn")) {
				//System.out.println("File " + listOfFiles[i].getName());
//				String fileName = listOfFiles[i].getName().split(".")[0];
				String fileName = listOfFiles[i].getName().replace(".ioobn", "").replace(".oobn", "");
				classList.add(fileName);
			} 
			else if (listOfFiles[i].isDirectory()) {
				//System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
		
		return classList;
	}
	
	public ArrayList<String> fileListFromConsole()
	{
		ArrayList<String> classList = new ArrayList<String>();
		
		String fileName = "";
    	
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Please provide the class names one by one and indicate end by END");
    	while(true){
            fileName = sc.next();
            if(fileName.equalsIgnoreCase("END"))
            	break;
            else{
            	classList.add(fileName);
            	System.out.print("Please provide next class name: ");
            }
    	}
		
		return classList;
	}
	
	/*
	 * this function generates hugin codes for all classes and embedded instance classes
	 * 
	 * */
	public ArrayList<String> compile(String directory, String extension, String choice, String classToCompile) throws Exception
	{
    	ArrayList<String> classNameList = new ArrayList<String>();
    	
    	if(choice.equalsIgnoreCase("X")){
    		classNameList.add(classToCompile);
    	}
    	else if(choice.equalsIgnoreCase("D")){
    		classNameList = fileListFromDir(directory);
    	}
    	else if(choice.equalsIgnoreCase("C")){
    		classNameList = fileListFromConsole();
    	}
    	/*
    	 * After the following call, all classes in the directory will be compiled and their code will be stored in
    	 * the hashmap table, so that we can generate iOOBN code for the class in consideration
    	 * */
    	ArrayList<String> classOrderofInstances = compileAllFiles(directory, extension, classNameList);// this is for the classes to be compiled
    	if(classOrderofInstances != null){
    		System.out.println("My list of instances are : " + classOrderofInstances);
    		compileForAllInstances(directory, extension, classOrderofInstances, classNameList);// this is for the classes of the embedded instances in the classes to be compiled
    	}
    	
    	return classOrderofInstances;
	}
	
	public void compileForAllInstances(String directory, String extension, ArrayList<String> instanceNameList, ArrayList<String> classNameList) throws Exception {
		
    	String fileName = "";
    	for(int I = 0; I < instanceNameList.size(); I++){// just to avoid the last class in the list which is actually the own class
    		fileName = instanceNameList.get(I);

    		if(!classNameList.contains(fileName)){
	    		NPPvisit.doneNodeAttribute = false;// if you miss this line then for the oobn files other than the 1st one, it will miss last basic node to add in node list
	    		NPPvisit.doneInstanceAttribute = false;
	    		System.out.println("I am in compile for all instances ");
	        	generateCode(directory, fileName, extension);
	        	NPPvisit.mxGraphsMap.put(fileName.replace(".oobn", ""), NPPvisit.mxGraph);
    		}
    	}
	}
	

	// this should be called after "formMxCellTuple", "formMxCellParent" and "formMxCellState"
	public void formMxCellNode(mxCellStructure ourMxCell)
	{
		mxCell cell = new mxCell();
		
		cell = new mxCell();
		runningNode = new BasicNode();
		runningNode.states = new ArrayList<String>(); // this is to remove default constructor's 2 default states 
		
		//cell.setId(ourMxCell.properties.id);
		String cellId = "";
		if(!ourMxCell.className.equalsIgnoreCase("")){
			cellId = ourMxCell.className + ".class::" + ourMxCell.instanceName + "_" + ourMxCell.properties.id;
		}
		else cellId = ourMxCell.properties.id;
		
		cell.setId(cellId);
		
		runningNode.name = ourMxCell.properties.name;// this should be name, for the time being, I put here value
		
		cell.setValue(ourMxCell.properties.value);// though I believe it should be name instead of value .........
		runningNode.label = ourMxCell.properties.value;
		
		runningNode.subType = ourMxCell.properties.sub_type;// // this should be a sub-type, for the time being, I put here value
		
		runningNode.type = ourMxCell.properties.type;// this should be a type, for the time being, I put here value
		
		runningNode.isEIO = ourMxCell.properties.kind;// this should be a kind (E/O/I), for the time being, I put here 0;
		
		runningNode.states = ourMxCell.extraProperties.states;
		
		runningNode.dataTuple = ourMxCell.tuples;// this contain a list of string where each string is a row (True-False value for a binary discrete variable, A, B, C for a ternary discrete variable)
		/*
		 * [0.1, 0.9, 0.2, 0.8, 0.3, 0.7, 0.4, 0.6] is actually [0.1, 0.9,     
		 *                                                       0.2, 0.8, 
		 *                                                       0.3, 0.7, 
		 *                                                       0.4, 0.6]
		 *   You might be wondering how do I know that we have to take 2 in each row, actually we don't need to 
		 *   bother about that here because this has been handled in NPPvisit and each row is actually string of 
		 *   two numbers here  
		 *                                                        
		 * which represent a table 
		 * True   0.1 0.2 0.3 0.4
		 * False  0.9 0.8 0.7 0.6
		 * 
		 * this need to be converted to .class representation:
			 <tuple value="C$True$^$False$^$" />
			<tuple value="L$True$False$True$False$" />
			<tuple value="^$^$^$^$^$" />
			<tuple value="True$0.5$0.5$0.5$0.5$" />
			<tuple value="False$0.5$0.5$0.5$0.5$" />
		 * 
		 * */
		
		System.err.println(runningNode.dataTuple);
		
		cell.setBNInfo(runningNode);
		
		StartingFrame.NodeTable.put(cell.getId(), cell);
		
		System.out.println("Component added: in NPPCompiler " + cell.getId() + "Label " + cell.getValue());
	}
	
	public void generateiOOBNCode(String directory, String extension, ArrayList<String> classNameList, String classToCompile, HashMap<String, mxCellStructure> mxGraphMain) throws Exception {
		// "mxGraphMain" might help me in generating ref edges
		
		System.out.println(classinstanceListMap);
		
		System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");
		StartingFrame.NodeTable = new HashMap<String, mxCell>();
		mxCell cell = new mxCell();
		BasicNode runningNode = new BasicNode();
		
		String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<mxGraphModel>\n" + "\t<root>\n" + "\t\t<mxCell id=\"0\" />\n" + "\t\t<mxCell id=\"1\" parent=\"0\" />\n";
    	String suffix = "\n\t</root>\n" + "</mxGraphModel>\n";
    	
    	String tempOutputFile = "";
    	
    	tempOutputFile = directory + "GeneratedFiles\\" + "temp_"+classToCompile.replace(".oobn", "")+".ioobn";	            
        FileWriter write = new FileWriter(tempOutputFile);
		PrintWriter print_line = new PrintWriter(write);
		
    	System.out.println(prefix);
    	print_line.print(prefix);
    	
    	int instanceId = 0;
    	
    	String fileName = "";
    	for(int I = 0; I < classNameList.size(); I++){
    		fileName = classNameList.get(I);
    		
        	String className =  fileName.replace(".oobn", ""); 
        	
//        	if(!className.equalsIgnoreCase(classToCompile) && classinstanceListMap.containsKey(className)){
        	if(!className.equalsIgnoreCase(classToCompile)){
        		
        		boolean doneCoordAdjust = false; // for n instances of a class, we just need to adjust them only once
        		if(classinstanceListMap.containsKey(className)){
	        		for(String instanceName: classinstanceListMap.get(className)){
	        			double width = 350.0;
	        			double height = 450.0;
	        			
	        			width = widthMap.get(className);
	        			height = heightMap.get(className);
	        			
	//        			System.out.println("Height and width " + width + "  " + height);
	        			
	//        			System.out.println(instanceName + "   " + NPPvisit.IdMapXCoord);
	        			int xcoord = 0;
	        			int ycoord = 0;
	        			if(NPPvisit.InstanceIdMapXCoord.containsKey(instanceName))
	        				xcoord = NPPvisit.InstanceIdMapXCoord.get(instanceName);
	        			else System.err.println("I couldn't find any key with name " + instanceName + " in the map \"NPPvisit.InstanceIdMapXCoord\" " + " !!!");
	        			
	        			if(NPPvisit.InstanceIdMapYCoord.containsKey(instanceName))
	        				ycoord = NPPvisit.InstanceIdMapYCoord.get(instanceName);
	        			else System.err.println("I couldn't find any key with name " + instanceName + " in the map \"NPPvisit.InstanceIdMapYCoord\" " + " !!!");
	        			
	        			instanceId = NPPvisit.id++;
	                	String instancePrefix = "\t\t<mxCell connectable=\"0\" id=\""+instanceId+"\" parent=\"1\" value=\"\" vertex=\"1\">\n" + 
	                    "\t\t\t<mxGeometry as=\"geometry\" height=\""+ height +"\" width=\""+ width +"\" x=\""+ xcoord +"\" y=\""+ ycoord +"\">\n" +
	                       "\t\t\t\t<mxRectangle as=\"alternateBounds\" height=\"40.0\" width=\"40.0\" x=\""+ xcoord +"\" y=\""+ ycoord +"\" />\n" + 
	                    "\t\t\t</mxGeometry>\n" + "\t\t</mxCell>\n";
	        			System.out.print(instancePrefix);
	            		print_line.print(instancePrefix);
	        			for(String key: NPPvisit.mxGraphsMap.get(className).keySet()){
	        				mxCellStructure c = NPPvisit.mxGraphsMap.get(className).get(key);
	            			c.className = className;// this will add prefix with node ID and source/target of edge
	            			c.instanceName = instanceName;
	            			c.properties.parentId = Integer.toString(instanceId);
//	            			c.properties.kind = ;// I need to figure out where to get the proper value for this line here
	            			if(doneCoordAdjust == false){
		            			c.mxGeometry.xcoord -= minXMap.get(className); 
		            			c.mxGeometry.ycoord -= minYMap.get(className);
	            			}
	            			
	                		System.out.print(c);
	                		print_line.print(c);
	                		
	                		// NodeTable updating by adding cell info in the table 
	                		if(c.properties.vertex.equalsIgnoreCase("1")){
	                			formMxCellNode(c);// this part is for iOOBN code
	                		}
	                		else{// if it is an edge
	                			if(!c.properties.id.startsWith("RefLink")){// not a ref edge
	//                				System.err.println("with prefix added: " + className + ".class::" + instanceName + "_" + c.properties.source);
	//                				System.err.println("with prefix added: " + className + ".class::" + instanceName + "_" + c.properties.target);
	                				String prefixSrcTarget = className + ".class::" + instanceName + "_" ;
	                				fillMxCellNodeParents(prefixSrcTarget, c);
	                			}
	                			
	                		}
	                		
	                	}
	        			doneCoordAdjust = true;
	        		}
        		}
    		}
        	else{// for the toBeCompiled class
        		for(String key: NPPvisit.mxGraphsMap.get(className).keySet()){
        			mxCellStructure c = NPPvisit.mxGraphsMap.get(className).get(key);
        			if(c.properties.vertex.equalsIgnoreCase("1")){
            			formMxCellNode(c);// this part is for iOOBN code
            		}
        			else{// if it is an edge
            			if(!c.properties.id.startsWith("RefLink")){// not a ref edge
//            				System.err.println(c.properties.source);
//            				System.err.println(c.properties.target);
            				String prefixSrcTarget = "";
            				fillMxCellNodeParents(prefixSrcTarget, c);
            			}
            		}
            		System.out.print(c);
            		print_line.print(c);
            	}
        	}
    	}
    	
    	System.out.println(suffix);
    	print_line.print(suffix);
    	
    	print_line.close();
    	
	}

	public void addParentAndDataToiOOBNCells() {
		
		for (String childNode : childNodeIdParentNodeIdMap.keySet()){
			if(StartingFrame.NodeTable.containsKey(childNode)){
				ArrayList<String> parentsNames = new ArrayList<String>();
				System.out.println(childNode);
				StartingFrame.NodeTable.get(childNode).getBNInfo().parents = childNodeIdParentNodeIdMap.get(childNode);
				ArrayList<ArrayList<String>> parentStates = new ArrayList<ArrayList<String>>();
				
				for(String par: childNodeIdParentNodeIdMap.get(childNode)){
					if(StartingFrame.NodeTable.containsKey(par)){
						BasicNode tempNode = StartingFrame.NodeTable.get(par).getBNInfo(); 
						parentStates.add(tempNode.states);
						
						String parName = "";
						if(par.contains("::") && par.contains("_")){// assuming x.class::A_8 is par
		//						String prefix = par.split("_")[0].split("::")[1]; // this line is replaced by the following segment
							// "_" exist multiple times
							String arr [] = par.split("_");// assume it returns n strings
							String tempPrefix = arr[0];
							for(int I = 1; I < arr.length-1; I++ )
								tempPrefix += ("_"+arr[I]);
							String arr1 [] = tempPrefix.split("::"); 
							String prefix = arr1[arr1.length-1];
							parName = prefix + "_" + tempNode.name;// getting intstance name
						}
						else{
							parName = tempNode.name;
						}
						
						parentsNames.add(parName);
					}
					else
						System.err.println("I could not find any key with name " + par + " in the map \"StartingFrame.NodeTable\" !!!");
				}
				boolean experimentGoingOn = true;
				StartingFrame.NodeTable.get(childNode).getBNInfo().convertDataTupleToTable(parentStates, parentsNames, experimentGoingOn);
			}
		}
		
	}
	
	public void fillMxCellNodeParents(String prefixSrcTarget, mxCellStructure c) {
		String parId = prefixSrcTarget+c.properties.source;
		String childId = prefixSrcTarget+c.properties.target;
		if(childNodeIdParentNodeIdMap.containsKey(childId)){
			childNodeIdParentNodeIdMap.get(childId).add(parId);
		}
		else{
			ArrayList<String> parentsIds = new ArrayList<String>();
			parentsIds.add(parId);
			childNodeIdParentNodeIdMap.put(childId, parentsIds);
		}
	}
	
	public ArrayList<String> compileAllFiles(String directory, String extension, ArrayList<String> classNameList) throws Exception
	{
    	File codeGenDir = new File(directory + "GeneratedFiles\\");
    	java.nio.file.Path codeGenDirPath = Paths.get(directory + "GeneratedFiles\\");
    	
    	//Files.deleteIfExists(codeGenDirPath);
    	
    	boolean successful = codeGenDir.mkdir();
    	ArrayList<String> classOrder = null;
    	String fileName = "";
    	boolean dependantAbsent = false;
    	for(int I = 0; I < classNameList.size(); I++){
    		fileName = classNameList.get(I);
    		File f = new File(fileName);
    		if(!f.exists())
    		{
    			System.err.println("One of the dependant classes is not found.!!! Hence I am not going to compile it further");
    			dependantAbsent = true;
    		}
    		else{
	    		NPPvisit.doneNodeAttribute = false;// if you miss this line then for the oobn files other than the 1st one, it will miss last basic node to add in node list
	    		NPPvisit.doneInstanceAttribute = false;
	        	generateCode(directory, fileName, extension);
	        	NPPvisit.mxGraphsMap.put(fileName.replace(".oobn", ""), NPPvisit.mxGraph);
    		}
    	}
    	if(dependantAbsent == false){
    		classOrder = stackToArrayList();
    		System.out.println(classOrder);
    	}
    	return classOrder;
	}
	
	public ArrayList<String> stackToArrayList()
	{
		System.out.println("Hi " + instanceClassOrderForCompile);
		ArrayList<String> classOrderList = new ArrayList<String>(instanceClassOrderForCompile);
		ArrayList<String> classOrderListShort = new ArrayList<String>();
		
		while(!classOrderList.isEmpty()){
			String temp = classOrderList.get(classOrderList.size()-1);
			if(temp != null && temp != "")
				classOrderListShort.add(temp);
			while(classOrderList.remove(temp));// this will remove all occurrences of temp in the list [may be removing duplicates]
		}
		
		return classOrderListShort;
	}
	
	
	public void calculateHeightWidthOfClassInstances(ArrayList<String> classOrder, String classToCompile) {
		String fileName = "";
    	for(int I = 0; I < classOrder.size(); I++){
    		
    		fileName = classOrder.get(I);
    		
        	String className =  fileName.replace(".oobn", ""); 
        	
        	if(!className.equalsIgnoreCase(classToCompile)){
        		
        		double width = 350.0;
        		double height = 450.0;

    			int minXcoord = 5000;// any big number
    			int minYcoord = 5000;// any big number
    			int maxXcoord = 0;
    			int maxYcoord = 0;
    			
    			
    			for(String key: NPPvisit.mxGraphsMap.get(className).keySet()){
    				mxCellStructure c = NPPvisit.mxGraphsMap.get(className).get(key);
        			if(c.properties.vertex.equalsIgnoreCase("1")){
        				// finding min and max X coordinate
        				if(c.mxGeometry.xcoord < minXcoord)	minXcoord = c.mxGeometry.xcoord; 
        				if(c.mxGeometry.xcoord > maxXcoord)	maxXcoord = c.mxGeometry.xcoord;
        				
        				// finding min and max Y coordinate
        				if(c.mxGeometry.ycoord < minYcoord)	minYcoord = c.mxGeometry.ycoord; 
        				if(c.mxGeometry.ycoord > maxYcoord)	maxYcoord = c.mxGeometry.ycoord;
        			}
            	}
    			width = maxXcoord - minXcoord + 50 + 50;// 50 + 50 are offsets
    			height = maxYcoord - minYcoord + 50 + 50;// 50 + 50 are offsets
    			
    			minXMap.put(className, minXcoord);
    			minYMap.put(className, minYcoord);
    			
    			widthMap.put(className, width);
    			heightMap.put(className, height);
    			
        	}
    		
    	}
	}
	
	public void resolveReferentialEdges(String classToCompile) {
		/*
		 * from NPPvisit we get nodes of main class's and which class-instance-node we need to join
		 * then using the map of mxgraphs to look for nodes to make mxCellStructures for edges 
		 * */
		mxCellStructure mainNodeCell = null;
		mxCellStructure instanceNodeCell = null;
		HashMap<String, mxCellStructure> mainNodeMX = null;
//		ArrayList<mxCellStructure> mainNodeMX = NPPvisit.mxGraphsMap.get(classToCompile);
		if(NPPvisit.mxGraphsMap.containsKey(classToCompile)){
			mainNodeMX = NPPvisit.mxGraphsMap.get(classToCompile);
		}
		else{
			System.err.println("I couldn't find any key with name " + classToCompile + " in the map \"NPPvisit.mxGraphsMap\" " + " !!!");
		}
//		ArrayList<mxCellStructure> instanceNodeMX;
		HashMap<String, mxCellStructure> instanceNodeMX;
		mxCellStructure refEdgeMxCell;
		// for each of the referential edges' 1st terminal node i.e. the node in main class/ the class to compile
		for(String mainNode : NPPvisit.MainClassNodeToInstanceNodeList.keySet()){
			mainNodeCell = findingMxGraph(mainNodeMX, mainNode);
			for(String instanceNode: NPPvisit.MainClassNodeToInstanceNodeList.get(mainNode)){// for each of the instance nodes connected to the main class node
				String parts [] = instanceNode.split("::"); 
				String instanceClass = parts[0];
				String parts2 [] = parts[1].split("_");
//				String instanceName = parts2[0];// what if "_" exists multiple times in parts[1], so I have replaced this with the following couple of lines
				String instanceName = parts2[0];
				for(int I = 1; I < parts2.length-1; I++)
					instanceName += ("_"+parts2[I]);
				
//				String instanceNodeName = parts2[1]; // what if "_" exists multiple times in parts[1], so I have replaced this with the following line
				String instanceNodeName = parts2[parts2.length-1];
				
				if(NPPvisit.mxGraphsMap.containsKey(instanceClass)){
					instanceNodeMX = NPPvisit.mxGraphsMap.get(instanceClass);
					instanceNodeCell = findingMxGraph(instanceNodeMX, instanceNodeName);
				}
				else{
					System.err.println("I couldn't find any key with name " + instanceClass + " in the map \"NPPvisit.mxGraphsMap\" " + " !!!");
				}
				
//				System.out.println(instanceNodeName+ " " + instanceNodeMX);
				if(mainNodeCell != null){
					if(instanceNodeCell != null){
						// add the referential edge to the mxGraph and in the graphMap
						refEdgeMxCell = formMxCellForRefEdge(mainNodeCell, instanceNodeCell, instanceClass, instanceName);
	//					NPPvisit.mxGraphsMap.get(classToCompile).add(refEdgeMxCell);// adding the ref edge to the main class i.e. to be compiled
						/*
						 * adding the ref edge to the main class i.e. to be compiled
						 * */ 
						NPPvisit.mxGraphsMap.get(classToCompile).put(refEdgeMxCell.properties.id, refEdgeMxCell);
					}
					else{
						System.err.println("I couldn't find any instance node cell in the \"findingMxGraph\" method for instance node " + instanceNodeName + " !!!");
					}
				}
				else{
					System.err.println("I couldn't find any instance node cell in the \"findingMxGraph\" method for main node " + mainNode + " !!!");
				}
			}
		}
		
	}
	
	
	
	public mxCellStructure formMxCellForRefEdge(mxCellStructure mainNodeCell, mxCellStructure instanceNodeCell, String instanceClass, String instanceName) 
	{
		NPPvisit.id++;

		mxCellStructure tempMxCell = new mxCellStructure();
		
		String childId = instanceClass+".class::"+instanceName+"_"+instanceNodeCell.properties.id; // target
		String parentId = mainNodeCell.properties.id; // source
		
		int srcXCoord = 0;
		int srcYCoord = 0;
		int tarXCoord = 0;
		int tarYCoord = 0;
		
		srcXCoord = mainNodeCell.mxGeometry.xcoord;
		srcYCoord = mainNodeCell.mxGeometry.ycoord;
	
		tarXCoord = instanceNodeCell.mxGeometry.xcoord;
		tarYCoord = instanceNodeCell.mxGeometry.ycoord;
		
		tempMxCell.properties.edge = "1";
		tempMxCell.properties.id = "RefLink" + Integer.toString(NPPvisit.id);
		tempMxCell.properties.parentId = "1";// we have to put a correct value here
		tempMxCell.properties.style.shape = "";
		tempMxCell.properties.value = "";
		
		tempMxCell.properties.target = childId;
		tempMxCell.properties.source = parentId;
		
		tempMxCell.mxGeometry.as = "geometry";
		tempMxCell.mxGeometry.relative = "1";// it might change, must fix later
//		OuterClass.InnerClass innerObject = outerObject.new InnerClass();
		mxCellStructure.Point pointSrc = tempMxCell.new Point();
		pointSrc.as = "sourcePoint";
		pointSrc.xcoord = srcXCoord;
		pointSrc.ycoord = srcYCoord;
		
		mxCellStructure.Point pointTar = tempMxCell.new Point();
		pointTar.as = "targetPoint";
		pointTar.xcoord = tarXCoord;
		pointTar.ycoord = tarYCoord;
		
		tempMxCell.mxGeometry.mxPoints.add(pointSrc);
		tempMxCell.mxGeometry.mxPoints.add(pointTar);
		
		return tempMxCell;
	}

	public void resolveCompilingClassEdges(HashMap<String, mxCellStructure> mxGraphMain) {
		
		for(String key: mxGraphMain.keySet()){
			mxCellStructure c = mxGraphMain.get(key);
			if(c.properties.edge.equalsIgnoreCase("1") &&  c.properties.source.contains("_")){
				String ParentIdInInstance[] = c.properties.source.split("_");
				
//				String instanceName = ParentIdInInstance[0]; // what if "_" exists multiple times in parts[1], so I have replaced these 2 lines with the following couple of lines
//				String nodeName = ParentIdInInstance[1];
//				String instanceName = parts2[0];
				String instanceName = ParentIdInInstance[0];
				for(int I = 1; I < ParentIdInInstance.length-1; I++)
					instanceName += ("_" + ParentIdInInstance[I]);
				
				String nodeName = ParentIdInInstance[ParentIdInInstance.length-1];
				
				String className = NPPCompiler.instanceclassMap.get(instanceName);
				
//				System.out.println(instanceName + " " + nodeName + "  " + className);
				HashMap<String, mxCellStructure> mxGraphTemp = NPPvisit.mxGraphsMap.get(className);
				
//				System.out.println(" Graphs: " + mxGraphTemp);
				
				mxCellStructure parentMxCell = findingMxGraph(mxGraphTemp, nodeName);
				
				c.properties.source = className + ".class::" + instanceName + "_" + parentMxCell.properties.id;
				
				c.mxGeometry.mxPoints.get(0).xcoord = parentMxCell.mxGeometry.xcoord;// 0 is for source
				c.mxGeometry.mxPoints.get(0).ycoord = parentMxCell.mxGeometry.ycoord;
				
			}
		}
	}
	
	
	// this searching could be avoided since we are using HashMap .... let's work around it later ....
	public mxCellStructure findingMxGraph(HashMap<String, mxCellStructure> mainNodeMX, String nodeName)
	{
		for(String key: mainNodeMX.keySet()){
			mxCellStructure c = mainNodeMX.get(key); 
			if(c.properties.value.equalsIgnoreCase(nodeName))
				return c;
		}
		return null;
	}
	
	public void XMLGeneratorForiOOBN(String directory, String extension, ArrayList<String> classOrder, String classToCompile) throws IOException {
		String tempOutputFile = "";
    	
    	tempOutputFile = directory + "GeneratedFiles\\" + "temp_"+classToCompile.replace(".oobn", "")+".class";	            
        FileWriter write = new FileWriter(tempOutputFile);
		PrintWriter print_line = new PrintWriter(write);
		
		XMLGenerator xmlg = new XMLGenerator(); 
		String xmliOOBNCode = xmlg.generateXML();
    	System.out.println(xmliOOBNCode);
    	
    	print_line.print(xmliOOBNCode);
    	
    	print_line.close();
		
	}
	
	public static void performCompilation(File f, String directory, String extension, String choice, String classToCompile) throws Exception
	{
		// don't re-initialize it in the constructor
		NPPvisit.InstanceIdMapXCoord = new HashMap<String, Integer>();
		NPPvisit.InstanceIdMapYCoord = new HashMap<String, Integer>();
		NPPvisit.MainClassNodeToInstanceNodeList = new HashMap<String, ArrayList<String>>();
		NPPvisit.mxGraphsMap = new HashMap<String, HashMap<String, mxCellStructure>>();
		System.out.println(" Now compiling : " + f.getName());
    	NPPCompiler dr = new NPPCompiler();
    	ArrayList<String> classOrder = dr.compile(directory, extension, choice, classToCompile);
    	
    	if(classOrder != null){
        	System.out.println("Class loading order: " + classOrder);
        	
        	System.out.println("\n\n\n Compilation done Successfully!!! \n\n\n");
        	
        	HashMap<String, mxCellStructure> mxGraphMain = NPPvisit.mxGraphsMap.get(classToCompile);
    		dr.resolveCompilingClassEdges(mxGraphMain);// this will fix the tobeCompiled class's edges from output nodes of an instance
    		dr.resolveReferentialEdges(classToCompile);
    		dr.calculateHeightWidthOfClassInstances(classOrder, classToCompile);
    		dr.generateiOOBNCode(directory, extension, classOrder, classToCompile, mxGraphMain);
    		dr.addParentAndDataToiOOBNCells();
    		
    		dr.XMLGeneratorForiOOBN(directory, extension, classOrder, classToCompile);// .class file generating
    	}
	}
	
    public static void main(String[] args) throws Exception 
    {
    	Scanner S = new Scanner (System.in);
    	
    	
    	System.out.println("Press X for specific file or D for input from default directory or C for console filename list");
    	System.out.println("No instance and variable names should contain \"_\" and press X to see the conversion from hugin oobn to iOOBN");
    	String choice = S.next();
    	
//    	String extension = ".ioobn";
//    	String directory = "InputIOOBNFiles\\";
    	// next 2 are replacing the previous 2 lines to check mxCell formation
    	String extension = ".oobn";
//    	String directory = "InputOOBNFiles\\Nets\\";
//    	String directory = "InputOOBNFiles\\";
//    	String directory = "GenerateAutoOOBN\\nClasses_5#nObjects_5#nStates_2#nNodes_5#maxInDeg_8#maxArcs_6\\";
//    	String directory = "GenerateAutoOOBN\\nClasses_1#nObjects_3#nStates_3#nNodes_5#maxInDeg_10#maxArcs_6\\";
//    	String directory = "GenerateAutoOOBN\\nClasses_3#nObjects_5#nStates_3#nNodes_20#maxInDeg_5#maxArcs_6\\";
    	String directoryMain = "GenerateAutoOOBN\\";
    	
    	/*
    	 * The following takes long in Hugin to compile, However, iOOBN and .class has been generated. Hope SII can do it faster
    	 * nClasses_3#nObjects_5#nStates_3#nNodes_20#maxInDeg_5#maxArcs_6
    	 *  
    	 * */
    	
    	if(choice.equalsIgnoreCase("D")){
	    	String []argv = {"-nNodes", "5", "-maxVal", "2", "-maxInDegree", "5", "-nOOBNs","3", "-nObj", "5", "-fixed_maxVal"};
			
//			int []bnSize = {5, 20, 50};
//			int []arity = {2, 3, 4};
//			int []maxParent = {5, 10, 15};
//			int []additionalClass = {0, 1, 2, 3};
//			int []numObjPerAdditionalClass = {1, 3, 5};
	    	
//			int []bnSize = {5, 20, 50};
//			int []arity = {2, 3, 4};
//			int []maxParent = {2, 3, 5};
//			int []additionalClass = {0, 1, 2, 3};
//			int []numObjPerAdditionalClass = {1, 2, 3};
	    	
	    	int []bnSize = {5, 10, 15, 20, 25, 30, 50};
			int []arity = {2, 3, 4, 5};
			int []maxParent = {2, 3, 4, 5};
			int []additionalClass = {0, 1, 2, 3};
			int []numObjPerAdditionalClass = {1, 2, 3, 4};
	    	
			
//	    	int []bnSize = {5};
//			int []arity = {2};
//			int []maxParent = {5};
//			int []additionalClass = {1};
//			int []numObjPerAdditionalClass = {3};
			
			for(int a = 0; a < bnSize.length; a++){
				argv[1] = Integer.toString(bnSize[a]);
				for(int b = 0; b < arity.length; b++){
					argv[3] = Integer.toString(arity[b]);
					for(int c = 0; c < maxParent.length; c++){
						argv[5] = Integer.toString(maxParent[c]);
						for(int d = 0; d < additionalClass.length; d++){
							argv[7] = Integer.toString(additionalClass[d]);
							for(int iter = 0; iter < folds; iter++)
							{
								if(additionalClass[d] == 0) {
									argv[9] = "0";
									String namePrefix = "";
									namePrefix += "nClasses_"+argv[7]+"#" + "nObjects_" + argv[9] + "#" + "nStates_" + argv[3] + "#" + "nNodes_" + argv[1] + "#" + "maxInDeg_" + argv[5] + "#" + "maxArcs_" + "6";
							        String directory = directoryMain + namePrefix + "\\"+"_"+(iter+1)+"\\";
							        
							        File f = new File(directory);
							        
							        if (f.exists() && f.isDirectory()) {
							        	
							        	String classToCompile = "main";
							        	performCompilation(f, directory, extension, choice, classToCompile);
							        }
								}
								else{
									for(int e = 0; e < numObjPerAdditionalClass.length; e++){
										argv[9] = Integer.toString(numObjPerAdditionalClass[e]);
										String namePrefix = "";
										namePrefix += "nClasses_"+argv[7]+"#" + "nObjects_" + argv[9] + "#" + "nStates_" + argv[3] + "#" + "nNodes_" + argv[1] + "#" + "maxInDeg_" + argv[5] + "#" + "maxArcs_" + "6";
								        
								        String directory = directoryMain + namePrefix + "\\_" + (iter+1) + "\\";
								        
								        File f = new File(directory);
								        
								        if (f.exists() && f.isDirectory()) {
								        	
								        	String classToCompile = "main";
								        	performCompilation(f, directory, extension, choice, classToCompile);
								        }
									}
								}
							}
						}
					}
				}
			}
			System.out.println("I have compiled all files");
    	}
    	else if(choice.equalsIgnoreCase("X")){
    		System.out.println("Please provide the class name");
            String classToCompile = S.next();
            
            NPPCompiler dr = new NPPCompiler();
        	
        	ArrayList<String> classOrder = dr.compile(directoryMain, extension, choice, classToCompile);
        	
        	System.out.println("Class loading order: " + classOrder);
        	
        	System.out.println("\n\n\n Compilation done Successfully!!! \n\n\n");
        	
        	HashMap<String, mxCellStructure> mxGraphMain = NPPvisit.mxGraphsMap.get(classToCompile);
    		dr.resolveCompilingClassEdges(mxGraphMain);// this will fix the tobeCompiled class's edges from output nodes of an instance
    		dr.resolveReferentialEdges(classToCompile);
    		dr.calculateHeightWidthOfClassInstances(classOrder, classToCompile);
    		dr.generateiOOBNCode(directoryMain, extension, classOrder, classToCompile, mxGraphMain);
    		dr.addParentAndDataToiOOBNCells();
    		
    		dr.XMLGeneratorForiOOBN(directoryMain, extension, classOrder, classToCompile);
    	}
    	
    	else System.out.println("I am not printing or compiling any iOOBN code because of the option not being \"X\" or \"D\"");
    }

}
