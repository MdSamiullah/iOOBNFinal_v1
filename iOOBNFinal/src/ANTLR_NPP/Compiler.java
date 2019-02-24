package ANTLR_NPP;

import java.util.ArrayList;
import java.util.Scanner;

import COM.hugin.HAPI.ClassCollection;
import COM.hugin.HAPI.ExceptionHugin;

public class Compiler {
	public static void displayBelief(HuginClassLoader hCL, ClassCollection cc, String className) throws ExceptionHugin{
		System.out.print("Press A for all belief, S for specific node's belief, END for exit: ");
    	Scanner Sc = new Scanner(System.in);
    	String choice = Sc.nextLine(); 
    	if(choice.equalsIgnoreCase("A")){
    		System.out.println("Displaying beliefs of the whole domain: " + className);
	    	hCL.showAllBelief(cc, className);
    	} else if(choice.equalsIgnoreCase("S")){
    		String instanceName, nodeName;
    		int attributeNumber;
    		while(true){
    			System.out.print("Provide instance name/ type END to exit: ");
	    		instanceName = Sc.next();
	    		if(instanceName.equalsIgnoreCase("END")) break;
	    		System.out.print("Provide node name: ");
	    		nodeName = Sc.next();
	    		System.out.print("Provide Attribute Number: ");
	    		attributeNumber = Sc.nextInt();
	    		hCL.showBelief(cc, className, instanceName, nodeName, attributeNumber);
    		}
    		// "John", "offspring", 0
    		
    	}
    	else if(choice.equalsIgnoreCase("END")){
    		// do nothing here to exit
    	}
	}
	
	 public static void main(String[] args) throws Exception 
	    {
		 	Scanner S = new Scanner (System.in);
	    	
	    	System.out.println("Press X for specific file or D for input from default directory or C for console filename list");
	    	System.out.println("No instance and variable names should contain \"_\" and press X to see the conversion from hugin oobn to iOOBN");
	    	String choice = S.next();
	    	
	    	String extension = ".ioobn";
	    	String directory = "InputIOOBNFiles\\";
	    	NPPCompiler dr = new NPPCompiler();
	    	String classToCompile = "";
	    	if(choice.equalsIgnoreCase("X")){
	    		System.out.println("Please provide the class name");
	    		 classToCompile = S.next();
	    	}
	    	
//	    	dr.compile(directory, extension, choice);
	    	dr.compile(directory, extension, choice, classToCompile);
	    	System.out.println("\n Code Generated Successfully!!! \n");
	    	
	    	ArrayList<String> classOrder = dr.stackToArrayList();
	    	System.err.println("Class loading order: " + classOrder);
	    	
	    	
	    	ClassCollection cc = new ClassCollection();
	    	extension = ".oobn";
	    	directory = directory + "GeneratedFiles\\";
	    	
	    	String className = "";
	    	String beliefNodeName = "John.offspring";
	    	HuginClassLoader hCL = new HuginClassLoader();
			
	    	for(int I = 0; I <classOrder.size(); I++){
	    		className = classOrder.get(I);
	    		cc = hCL.LAP (className, directory, extension, cc);
//	    		int attributeNumber = 0;
//	    		hCL.showBelief(cc, className, beliefNodeName, attributeNumber);
	    	}
	    	
			System.out.println("\nBelief Propagation is done successfully\n");
			
			for(int I = 0; I <classOrder.size(); I++){
				System.out.println("Displaying information of class: " + classOrder.get(I));
				displayBelief(hCL, cc, classOrder.get(I));
			}
	    }
}
