package com.editor.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/*
 * Class components : name & [domain element, attributes, class instances]
 * 	=> name [list/map of basic nodes, a list/map of potentials, a list/map of attributes, a list/map of instances]
 * 
 * 
 * */

public class ClassDefn {
	String name;
	int type; // 0 for interface, 1 for abstract class, 2 for class/ concrete class
	public LinkedHashMap<String, BasicNode> basicNodes;
	LinkedHashMap<String, Potential> potentials;
	LinkedHashMap<String, String> attributes;
	LinkedHashMap<String, Instance> instances;
	LinkedHashSet<String> inputs;
	LinkedHashSet<String> outputs;
	
	public ClassDefn(){
		this.name = "";
		this.type = 2; // by default the file is a class, not abstract class or interface
		basicNodes = new LinkedHashMap<String, BasicNode>();
		potentials = new LinkedHashMap<String, Potential>();
		attributes = new LinkedHashMap<String, String>();
		instances = new LinkedHashMap<String, Instance>();
		inputs = new LinkedHashSet<String>();
		outputs = new LinkedHashSet<String>();
	}
	
	public ClassDefn(String name, int type){
		this.name = name;
		this.type = type;
		basicNodes = new LinkedHashMap<String, BasicNode>();
		potentials = new LinkedHashMap<String, Potential>();
		attributes = new LinkedHashMap<String, String>();
		instances = new LinkedHashMap<String, Instance>();
		inputs = new LinkedHashSet<String>();
		outputs = new LinkedHashSet<String>();
	}
	
	public void insertInputs(String in){
		String tempStr = in;
		for(String str: tempStr.split("[ ,)(]")){
			inputs.add(str);
		}
	}
	
	public void insertOutputs(String out){
		String tempStr = out;
		for(String str: tempStr.split("[ ,)(]"))
			outputs.add(str);
	}
	
	public void insertAttribute(Attribute attribute){
		this.attributes.put(attribute.name, attribute.value);
	}
	
	public void insertBasicNode(BasicNode basicNode){
		this.basicNodes.put(basicNode.name, basicNode);
	}
	
	
	/* potentials from multiple classes/interfaces may come in the form of
	 *  potential(A) or potential(A|B) or potential(A|C) or potential(A|CD)
	 *  1st case should be recorded as distinct
	 *  others should be merged. 
	 *  My plan is to store 1st case as Pot (A + "no Parent") and others as pot(A + "parent")    
	 */
	public void insertPotential(Potential potential){
		String child = potential.childList;
		String parent = potential.parentList;
		String parentTag = "no parent";
		if(parent == "") {parentTag = "parent";}
		
		Potential tempPot = this.potentials.get(child+parentTag);
		
		if (tempPot != null && parent != ""){
			tempPot.insertParent(parent);
			System.err.println(tempPot.parentList);
			/* do something to update potential attributes*/
			for(String A: potential.attributes.keySet())
				tempPot.insertAttribute (new Attribute(A, potential.attributes.get(A)));
			
			this.potentials.put(child+parentTag, tempPot);
		}
		else this.potentials.put(child+parentTag, potential);
		
	}
	
	public void insertInstance(Instance instance){
		this.instances.put(instance.name, instance);
	}
	
	public String getName(){
		return name;
	}
	
	public int getType(){
		return type;
	}
	
	public HashMap<String, Potential> getPotentials (){
		return potentials;
	}
	
	public HashMap<String, BasicNode> getBasicNodes (){
		return basicNodes;
	}
	
	public HashMap<String, String> getAttributes (){
		return attributes;
	}
	
	public String getInputs(){
		String str = String.join(" ", inputs);
		
		return str;
	}
	
	public String getOutputs(){
		String str = String.join(" ", outputs);

		return str;
	}
	
	public HashMap<String, Instance> getInstances (){
		return instances;
	}
	
	public Attribute getAttribute(String name){
		Attribute retAttr = new Attribute(name, (String) attributes.get(name));
		
		return retAttr;
	}
	
	public BasicNode getBasicNode(String name){
		BasicNode basicNode = (BasicNode) basicNodes.get(name);
		
		return basicNode;
	}
	
	public Potential getPotential(String name){
		Potential potential = (Potential) potentials.get(name);
		
		return potential;
	}
	
	public Potential getPotential(String childList, String parentList){
		Potential potential = (Potential) potentials.get(childList+parentList);
		
		return potential;
	}
	
	public Instance getInstance(String name){
		Instance instance = (Instance) instances.get(name);
		
		return instance;
	}
	
	public StringBuilder printCode(int numOfTab){
		String prefix;
		StringBuilder buffer = new StringBuilder();
		
		if(type == 0) prefix = "interface ";
		else if (type == 1) prefix = "abstract class ";
		else prefix = "class ";
		
		//System.out.print(prefix + name + "\n{\n");
		buffer.append(prefix + name + "\n{\n");
		
		if(inputs.size() > 0){
			for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");	
			buffer.append("\t");}
			//System.out.print("inputs = ( " + getInputs() + ");\n");
			buffer.append("inputs = ( " + getInputs() + ");\n");
		}
		
		if(outputs.size() > 0){
			for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");	
			buffer.append("\t");}
			//System.out.print("outputs = ( " + getOutputs() + ");\n");
			buffer.append("outputs = ( " + getOutputs() + ");\n");
		}
		
		Attribute tempAttribute;
		for (String key : attributes.keySet()){
			tempAttribute = new Attribute(key, attributes.get(key));
			buffer.append(tempAttribute.printAttribute(numOfTab));
		}
		
		System.out.print("\n");
		
		for (String key : instances.keySet()){
			Instance tempInstance = instances.get(key);
			buffer.append(tempInstance.printInstance(numOfTab));
			
			//System.out.print("\n");	
			buffer.append("\n");
		}
		
		//System.out.print("\n");	
		buffer.append("\n");
		
		for (String key : basicNodes.keySet()){
			BasicNode tempBasicNode = basicNodes.get(key);
			buffer.append(tempBasicNode.printBasicNode(numOfTab));
			//System.out.print("\n");	
			buffer.append("\n");
		}
		
		System.out.print("\n");	buffer.append("\n");
		
		for (String key : potentials.keySet()){
			Potential tempPotential = potentials.get(key);
			buffer.append(tempPotential.printPotential(numOfTab));
			//System.out.print("\n");	
			buffer.append("\n");
		}
		
		//System.out.print("\n}\n");	
		buffer.append("}\n");
		
		return buffer;
	}
	
	@Override
	public String toString(){
		String prefix;
		
		if(type == 0) prefix = "interface ";
		else if (type == 1) prefix = "abstract class ";
		else prefix = "class ";
		
		return prefix + ": < " + type + name + " " + inputs + " " + outputs + " " + attributes + " " + basicNodes+ " " + instances + " " + potentials + " >";
	}
	
}
