package ANTLR_NPP;


import java.util.Scanner;

import COM.hugin.HAPI.*;
import COM.hugin.HAPI.Class;
import COM.hugin.HAPI.Native.HAPI;

public class HuginClassLoader 
{
    /**
     * A nice information I have noticed that .oobn requires file names to be unique with 1st line
     * but if .net is used, multiple classes can accommodate in a single file and no restriction of
     * same name of 1st line. Hence, My problem of file name extension is solved. I can replace
     * .ioobn with .net in my description so that 1st line can be changed flawlessly 
     * 
     * This function parses the given NET/OOBN file, compiles the network.
     * 
     */
    public ClassCollection LAP (String className, String directory, String extension, ClassCollection cc) throws ExceptionHugin 
    {
    	ClassParseListener classParseListener = new DefaultClassParseListener();
    	ParseListener parseListener = new DefaultClassParseListener();
    	
    	/*
    	 * The following segment is for loading classes where 
    	 * each class is in a single .oobn file
    	 * */
    	ClassCollection cc1 = new ClassCollection();
    	
    	Class c;
    	
    	
    	String file1 = directory + className + extension;
    	
    	cc1.addClasses(cc);
    	cc1.parseClasses (file1, classParseListener);

    	
    	
    	return cc1;
    }
    
    public void showBelief1(ClassCollection cc, String className, String nodeName, int attributeNum) throws ExceptionHugin
    {
    	Domain d;
    	Class c = cc.getClassByName (className);
    	System.out.println(c.getNumberOfChanceNodes());
    	
    	d = c.createDomain ();
    	d.compile ();
    	if(nodeName != null){
    		if(d.getNodeByName (nodeName)!= null){
    			System.out.println(((DiscreteNode) d.getNodeByName (nodeName)).getBelief (attributeNum));
    		}
    		else System.err.println("No discrete node found with name " + nodeName);
    	}
    }
    
    public void showBelief(ClassCollection cc, String className, String instanceName, String nodeName, int attributeNum) throws ExceptionHugin
    {
    	Domain d;
    	Class c = cc.getClassByName (className);
    	System.out.println(c.getNumberOfChanceNodes());
    	nodeName = instanceName +"."+nodeName;
    	d = c.createDomain ();
    	d.compile ();
    	if(nodeName != null){
    		if(d.getNodeByName (nodeName)!= null){
    			System.out.println(((DiscreteNode) d.getNodeByName (nodeName)).getBelief (attributeNum));
    		}
    		else System.err.println("No discrete node found with name " + nodeName);
    	}
    }
    
    public void showAllBelief(ClassCollection cc, String className) throws ExceptionHugin
    {
    	Domain d;
    	Class c = cc.getClassByName (className);
    	System.out.println(c.getNumberOfChanceNodes());
    	
    	d = c.createDomain ();
    	d.compile ();
    	
    	printBeliefsAndUtilities (d);
    }
    
    /**
     * Print the beliefs and expected utilities of all nodes in the domain.
     */
    public static void printBeliefsAndUtilities (Domain domain)
	throws ExceptionHugin
    {
		NodeList nodes = domain.getNodes();
		boolean hasUtilities = containsUtilities (nodes);
		java.util.ListIterator it = nodes.listIterator();
	
		while (it.hasNext()) {
		    Node node = (Node) it.next();
	
		    System.out.println ();
		    System.out.println (node.getLabel() + " (" + node.getName() + ")");
	
		    if (node instanceof DiscreteNode) {
			DiscreteNode dNode = (DiscreteNode) node;
	
			for (int i = 0, n = (int) dNode.getNumberOfStates(); i < n; i++) {
			    System.out.print ("  - " + dNode.getStateLabel (i)
					      + " " + dNode.getBelief (i));
			    if (hasUtilities)
				System.out.println (" (" + dNode.getExpectedUtility (i) + ")");
			    else
				System.out.println();
			}
		    } else if (node instanceof ContinuousChanceNode) {
			ContinuousChanceNode ccNode = (ContinuousChanceNode) node;
	
			System.out.println ("  - Mean : " + ccNode.getMean());
			System.out.println ("  - SD   : " + Math.sqrt (ccNode.getVariance()));
		    } else if (node instanceof UtilityNode)
			System.out.println ("  - Expected utility: "
					    + ((UtilityNode) node).getExpectedUtility());
		    else  /* "node" is a (real-valued) function node */
			try {
			    System.out.println ("  - Value: "
						+ ((FunctionNode) node).getValue());
			} catch (ExceptionHugin e) {
			    System.out.println ("  - Value: N/A");
			}
		}
    }
    
    /**
     * Are there utility nodes in the list?
     */
    public static boolean containsUtilities (NodeList list)
    {
	java.util.ListIterator it = list.listIterator();

	while (it.hasNext())
	    if (it.next() instanceof UtilityNode)
		return true;

	return false;
    }

   /**
     * Load a Hugin NET file, compile the network, and print the
     * results.  If a case file is specified, load it, propagate the
     * evidence, and print the results.
     * 
     * Instead of using the batch file use run>run configuration > arguments > Program Arguments: ChestClinic ChestClinic.hcs
     * and
     * 
     * add hapi84-64.jar
     * 
     * and
     * 
     * Create a path with name  = java.library.path, value =  C:\Program Files\Hugin Expert\Hugin Dist 8.4 (x64)\Bin\ in path variable 
     * 
     * if not working then try rebooting eclipse and if any cmd screen open then reboot that as well
     * @throws Exception 
     * 
     */
    public static void main (String args[]) throws Exception
    {
    	ClassCollection cc = new ClassCollection();
    	String extension = ".oobn";
//    	String directory = "C:\\Users\\msam34\\Desktop\\oobn1\\";
    	String directory = "C:\\Users\\msamiull\\workspace\\NETPlusPlus\\InputIOOBNFiles\\GeneratedFiles\\";
//    	String className1 = "Horse";
//    	String className2 = "StudFarm";
    	String className1 = "MeatCow";
    	HuginClassLoader hCL = new HuginClassLoader() ;
		cc = hCL.LAP (className1, directory, extension, cc);
		hCL.showAllBelief(cc, className1);
		
		 className1 = "MilkMeatCow";
		cc = hCL.LAP (className1, directory, extension, cc);
		hCL.showAllBelief(cc, className1);
		
		className1 = "DraftingCow";
		cc = hCL.LAP (className1, directory, extension, cc);
		hCL.showAllBelief(cc, className1);
		
		className1 = "CalvingCow";
		cc = hCL.LAP (className1, directory, extension, cc);
		hCL.showAllBelief(cc, className1);
    }
}
