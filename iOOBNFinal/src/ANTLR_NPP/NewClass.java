package ANTLR_NPP;

import java.io.FileWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Stack;

/*
 * Class components : name & [domain element, attributes, class instances]
 * 	=> name [list/map of basic nodes, a list/map of potentials, a list/map of attributes, a list/map of instances]
 * 
 * 
 * */

public class NewClass {
	LinkedHashMap<String, ClassDefn> parentComponents;
	ClassDefn ownComponents;
	ClassDefn combinedClass; // untill the method combinedComponents() has been called, it will not instantiated
	
	public NewClass(){
		parentComponents = new LinkedHashMap<String, ClassDefn>();
		this.ownComponents = new ClassDefn();
	}
	
	public NewClass(String name, int type){
		parentComponents = new LinkedHashMap<String, ClassDefn>();
		this.ownComponents = new ClassDefn(name, type);
	}
	
	public void insertParentComponent(ClassDefn parent){
		this.parentComponents.put(parent.name, parent);
	}
	
	public void insertOwnComponent(ClassDefn own){
		this.ownComponents = own;
	}
	
	public HashMap<String, ClassDefn> getParentComponents (){
		return parentComponents;
	}
	
	public ClassDefn getParent(String name){		
		return parentComponents.get(name);
	}
	
	public void combineComponents(){
		combinedClass = new ClassDefn(ownComponents.name, ownComponents.type);
		
		combinedClass.insertInputs(ownComponents.getInputs());
		combinedClass.insertOutputs(ownComponents.getOutputs());
		
		// for parents
		for(ClassDefn parent : parentComponents.values()){
			/* there is a chance of overriding/over writing components with same name in the map of
			 * combined class
			 * So, parent classes components should be added first, to ensure overriding 
			 * prioritize own components
			 */
			combinedClass.insertInputs(parent.getInputs());
			combinedClass.insertOutputs(parent.getOutputs());
			
			for (String key : parent.attributes.keySet()){
				combinedClass.insertAttribute(new Attribute(key, parent.attributes.get(key)));
			}
			
			for (String key : parent.basicNodes.keySet()){
				combinedClass.insertBasicNode(parent.basicNodes.get(key));
			}
			
			for (String key : parent.instances.keySet()){
				combinedClass.insertInstance(parent.instances.get(key));
			}
			
			for (String key : parent.potentials.keySet()){
				combinedClass.insertPotential(parent.potentials.get(key));
			}
		}
		
		// for own components		
		for (String key : ownComponents.attributes.keySet()){
			combinedClass.insertAttribute(new Attribute(key, ownComponents.attributes.get(key)));
		}
		
		for (String key : ownComponents.basicNodes.keySet()){
			combinedClass.insertBasicNode(ownComponents.basicNodes.get(key));
		}
		
		for (String key : ownComponents.instances.keySet()){
			combinedClass.insertInstance(ownComponents.instances.get(key));
		}
		
		for (String key : ownComponents.potentials.keySet()){
			combinedClass.insertPotential(ownComponents.potentials.get(key));
		}
		
	}
	
	
	public void generateHuginCodeForCombinedClass(String targetFileName, int numOfTab, Stack<String> classOrder) throws IOException{
		String prefix;
		String extension = ".oobn";
		StringBuilder buffer = new StringBuilder();
		
		if(this.ownComponents.type == 2){// if it is a class definition file
			extension = ".oobn"; // extension of generated code will be compilable by Hugin
			
			classOrder.add(this.ownComponents.name);
		}
		else if(this.ownComponents.type == 1){// if interface or abstract class
			extension = ".abstractClass";
		}
		else extension = ".interface";
		
		FileWriter write1 = new FileWriter(targetFileName+extension);
		PrintWriter print_line = new PrintWriter(write1);
		
		if(combinedClass.type == 0) prefix = "interface ";
		else if (combinedClass.type == 1) prefix = "abstract class ";
		else prefix = "class ";
		
		print_line.print(prefix + combinedClass.name + "\n{\n");
		
		if(combinedClass.inputs.size() > 0){
			for(int I = 0; I < numOfTab; I++)	{System.out.print("\t");	buffer.append("\t");}
			//System.out.print("inputs = ( " + combinedClass.getInputs() + ");\n");
			buffer.append("inputs = ( " + combinedClass.getInputs() + ");\n");
		}
		
		if(combinedClass.outputs.size() > 0){
			for(int I = 0; I < numOfTab; I++)	{System.out.print("\t");	buffer.append("\t");}
			//System.out.print("outputs = ( " + combinedClass.getOutputs() + ");\n");
			buffer.append("outputs = ( " + combinedClass.getOutputs() + ");\n");
		}
		
		Attribute tempAttribute;
		for (String key : combinedClass.attributes.keySet()){
			tempAttribute = new Attribute(key, combinedClass.attributes.get(key));
			buffer.append(tempAttribute.printAttribute(numOfTab));
		}
		
		buffer.append("\n");
		
		for (String key : combinedClass.instances.keySet()){
			Instance tempInstance = combinedClass.instances.get(key);
			
			if(!tempInstance.className.equalsIgnoreCase("")){
				classOrder.add(tempInstance.className);
				buffer.append(tempInstance.printInstance(numOfTab));
				buffer.append("\n");
			}
		}
		
		System.out.println("The class order of the instances in the class NewClass.java " + classOrder);
		
		buffer.append("\n");
		
		for (String key : combinedClass.basicNodes.keySet()){
			BasicNode tempBasicNode = combinedClass.basicNodes.get(key);
			buffer.append(tempBasicNode.printBasicNode(numOfTab));
			buffer.append("\n");
		}
		
		buffer.append("\n");
		
		for (String key : combinedClass.potentials.keySet()){
			Potential tempPotential = combinedClass.potentials.get(key);
			buffer.append(tempPotential.printPotential(numOfTab));
			buffer.append("\n");
		}
		
		buffer.append("}\n");
		
		print_line.print(buffer);
		
		print_line.close();
	}
	
	
	public void printCode(int numOfTab){
		String prefix;
		
		if(combinedClass.type == 0) prefix = "interface ";
		else if (combinedClass.type == 1) prefix = "abstract class ";
		else prefix = "class ";
		
		//System.out.print(prefix + combinedClass.name + "\n{\n");
		
		if(combinedClass.inputs.size() > 0){
			for(int I = 0; I < numOfTab; I++)	{System.out.print("\t");}
			System.out.print("inputs = ( " + combinedClass.getInputs() + ");");
		}
		
		if(combinedClass.outputs.size() > 0){
			for(int I = 0; I < numOfTab; I++)	{System.out.print("\t");}
			System.out.print("outputs = ( " + combinedClass.getOutputs() + ");");
		}
		
		Attribute tempAttribute;
		for (String key : combinedClass.attributes.keySet()){
			tempAttribute = new Attribute(key, combinedClass.attributes.get(key));
			tempAttribute.printAttribute(numOfTab);
		}
		
		System.out.print("\n");
		
		for (String key : combinedClass.instances.keySet()){
			Instance tempInstance = combinedClass.instances.get(key);
			tempInstance.printInstance(numOfTab);
			System.out.print("\n");
		}
		
		System.out.print("\n");
		
		for (String key : combinedClass.basicNodes.keySet()){
			BasicNode tempBasicNode = combinedClass.basicNodes.get(key);
			tempBasicNode.printBasicNode(numOfTab);
			System.out.print("\n");
		}
		
		System.out.print("\n");
		
		for (String key : combinedClass.potentials.keySet()){
			Potential tempPotential = combinedClass.potentials.get(key);
			tempPotential.printPotential(numOfTab);
			System.out.print("\n");
		}
		
		System.out.print("\n}\n");
	}
	
	@Override
	public String toString(){
		return "Class: < Own: " + ownComponents + " \n Parents:" + parentComponents + " >";
	}
	
}
