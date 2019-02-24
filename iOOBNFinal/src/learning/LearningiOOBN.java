package learning;

/***
 * Our learning is targeted to convert a set of BNs to OOBN/iOOBN
 * and a set of OOBN classes to iOOBN. However, in our current prototype implementation,
 * we assume X_A, X_C being two different output nodes of two objects A and C may be of same class
 * It is really rare that two classes that uses object A and object C as embedded object will have same child node
 * for X_A and X_C, but same type of child nodes, which we can check in further stages 
 * This can also be an advancement or future direction of work to come up with some nice solution for this 
 * particular issue
 * 
***/
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ANTLR_NPP.NETPlusPlusLexer;
import ANTLR_NPP.NETPlusPlusParser;
import ANTLR_NPP.NPPCompiler;
import ANTLR_NPP.NPPvisitForDAG;
import ANTLR_NPP.NewClass;
import ANTLR_NPP.ThrowingErrorListener;
import ANTLR_NPP.mxCellStructure;
import ANTLR_NPP.mxCellStructure.Point;

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
import java.util.HashSet;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;


/*
 * We assume, a set of DAG for a BN is given. DAG is a HashMap of list 
 * Each node and edge have a label (i.e. a set of container graphs), each node has a name which is unique throughout the
 * whole graph 
 * */ 
public class LearningiOOBN {

	public static int noOfErrors;
	public static String choice = "";
		
	public static ParseTree performParsing(String inputFile, String directory) throws IOException{
        String tempInputFile = "DAG";
        
        Scanner Sc = new Scanner (new File(directory+inputFile));
        
        FileWriter write = new FileWriter(tempInputFile);
		PrintWriter print_line = new PrintWriter(write);
		
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
	
	public void generateDAG(String directory, String fileName, String extension) throws Exception
	{
		String className;
        
        className = fileName;
        
        ParseTree tree = performParsing(className, directory);
        
        if(noOfErrors > 0)        System.err.println("Total error found: " + noOfErrors);
        else{
        	NPPvisitForDAG traverse = new NPPvisitForDAG();
            traverse.visit(tree); // visit() can be found in "String org.antlr.v4.runtime.tree.AbstractParseTreeVisitor.visit(ParseTree tree)"
        }
	}
	
	public ArrayList<String> fileListFromDir(String path)
	{
		ArrayList<String> classList = new ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String fileName = listOfFiles[i].getName();
				if(fileName.endsWith(".oobn"))
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
	
	
	public ArrayList<String> compile(String directory, String extension, String targetDir, String choiceClass) throws Exception
	{
		Scanner S = new Scanner (System.in);
    	String classToCompile = "";
    	
    	System.out.println("Press X for specific file or D for input from default directory or C for console filename list");
    	
    	choice = S.next();
    	
    	ArrayList<String> classNameList = new ArrayList<String>();
    	
    	if(choice.equalsIgnoreCase("X")){
    		System.out.println("Please provide the class name");
        	
                classToCompile = S.next();
                
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
    	compileAllFiles(directory, extension, classNameList, targetDir, choiceClass);// this is for the classes to be compiled
    	
    	return classNameList;
	}
	
	public void compileAllFiles(String directory, String extension, ArrayList<String> classNameList, String targetDir, String choiceClass) throws Exception
	{
    	File codeGenDir = new File(directory + "GeneratedFiles\\");
    	java.nio.file.Path codeGenDirPath = Paths.get(directory + "GeneratedFiles\\");
    	
    	//Files.deleteIfExists(codeGenDirPath);
    	
    	boolean successful = codeGenDir.mkdir();
    	
    	String fileName = "";
    	
    	for(int I = 0; I < classNameList.size(); I++){
    		fileName = classNameList.get(I);
        	generateDAG(directory, fileName, extension);
        	System.out.println("DAG for oobn : " + fileName);
        	DisplayTheMap(NPPvisitForDAG.dagMAP);
        	System.out.println("The directory is  " + directory);
        	
        	DAGWriterForOOBN(directory, NPPvisitForDAG.dagMAP, fileName, targetDir, "dag_");
        	NPPvisitForDAG.dagMAP = new HashMap<String, HashSet<String>>();
    	}
	}
	
	
	public void DAGWriterForOOBN(String directory, HashMap<String, HashSet<String>> dAG, String classOfDAG, String targetDir, String filePrefix) throws IOException {
		String tempOutputFile = "";
    	
//		System.out.println("directory " + directory + " targetDir " + targetDir + " file prefix " + filePrefix + " class " + classOfDAG);
    	tempOutputFile = directory + targetDir + filePrefix +classOfDAG.replace(".oobn", ".txt");
//    	System.out.println(tempOutputFile);
        FileWriter write = new FileWriter(tempOutputFile);
		PrintWriter print_line = new PrintWriter(write);
		
		for(String node : dAG.keySet())
		{		
			print_line.print(node + " : ");
			for (String child : dAG.get(node))
				print_line.print(child + " ");
			print_line.println();
		}
		
    	print_line.close();
		
	}
	
	public static void DisplayTheMap(HashMap<String, HashSet<String>> dagMAP) {
		
		for(String node : dagMAP.keySet())
		{		
			System.out.print(node + " :");
			for (String child : dagMAP.get(node))
				System.out.print(" " + child);
			System.out.println();
		}
	}
	
    public static void main(String[] args) throws Exception 
    {
    	
    	String extension = ".oobn";
//    	String directory = "iOOBNLearning_IO\\";
    	String directory = "InputOOBNFiles\\Nets\\";
    	
//		String directory1 = "iOOBNLearning_IO\\GeneratedFiles\\";
//		String directory1 = "iOOBNLearning_IO\\";
		String directory1 = "InputOOBNFiles\\Nets\\GeneratedFiles\\";

		String targetDir = "GeneratedFiles\\";
    	
    	LearningiOOBN learn = new LearningiOOBN();
    	
    	Scanner sc = new Scanner (System.in);
    	String choice = "";
    	System.out.println("If you want to generate DAG files (compile) before learning, press <C/c>, or press anything else followed by an Enter: ");
    	
    	choice = sc.nextLine();
    	
    	System.out.println("If you want to generate class files after learning, press <C/c>, or press anything else followed by an Enter: ");
    	
    	String choiceClass = sc.nextLine();
    	
    	if(choice.equalsIgnoreCase("C")){
    		ArrayList<String> classOrder = learn.compile(directory, extension, targetDir, choiceClass);
    		System.out.println("Class loading order: " + classOrder);
    	
    		System.out.println("\n\n\n Compilation done Successfully!!! \n\n\n");
    	}
    	
    	DAGraph2 dagLearner = new DAGraph2();
    	dagLearner.makeHierarchy(directory1, targetDir, choiceClass);
    	
    }

}
