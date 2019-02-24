package ANTLR_NPP;

import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 * class instance : name (2 parts: own name and class name) 
 * 					& [bindings (2 parts: input (string) & output (string)), a list/map of attribute]
 * => own name, class name, input bindings, output bindings, list of attributes (can be a map of attributes)
 * the map is between <attribute name, attribute> or <attribute name, attribute value>. Currently,
 * I prefer the later one. Because, attribute contains only two strings. one will be in key another
 * will be in value of the map. Hence full info can be retrieved. If attribute is more than this two 
 * strings, then we can think of the former one.
 * 
 * */

public class Instance {
	String name;
	String className;
	String inputBindings;
	String outputBindings;
	LinkedHashMap <String, String> attributes;
	
	public Instance(){
		this.name = "";
		this.className = "";
		this.inputBindings = "";
		this.outputBindings = "";
		attributes = new LinkedHashMap<String, String>();
	}
	
	/* if any of the bindings is not provided, then use "" for that parameter */
	public Instance(String name, String className, String inputBindings, String outputBindings){
		this.name = name;
		this.className = className;
		this.inputBindings = inputBindings;
		this.outputBindings = outputBindings;
		attributes = new LinkedHashMap<String, String>();
	}
	
	public void insertAttribute(Attribute attribute){
		this.attributes.put(attribute.name, attribute.value);
	}
	
	public String getName (){
		return name;
	}
	
	public String getClassName (){
		return className;
	}
	
	public String getInputBindings (){
		return inputBindings;
	}
	
	public String getOutputBindings (){
		return outputBindings;
	}
	
	public HashMap<String, String> getAttributes (){
		return attributes;
	}
	
	public Attribute getAttribute(String name){
		Attribute retAttr = new Attribute(name, (String) attributes.get(name));
		
		return retAttr;
	}
	
	public StringBuilder printInstance(int numOfTab){
		Attribute tempAttribute;
		
		StringBuilder buffer = new StringBuilder();
		
		String bindings;
		if(inputBindings == null && outputBindings == null) {bindings = "( )"; }
		else if(inputBindings == null && outputBindings != null) {bindings = "( " + outputBindings + " )"; }
		else if(inputBindings != null && outputBindings == null) {bindings = "( "+ inputBindings + " )"; }
		else  {bindings = "( " + inputBindings + outputBindings + " )"; }
		
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");
		buffer.append("\t");}
		//System.out.print("instance " + name + " : " + className + bindings + "\n");
		buffer.append("instance " + name + " : " + className + bindings + "\n");
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");
		buffer.append("\t");}
		//System.out.println("{");	
		buffer.append("{");
		
		for (String key : attributes.keySet()){
			tempAttribute = new Attribute(key, attributes.get(key));
			buffer.append(tempAttribute.printAttribute(numOfTab+1));
		}
		
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");
		buffer.append("\t");}
		//System.out.println("}");	
		buffer.append("}");
		
		return buffer;
	}
	
	@Override
	public String toString(){
		if(inputBindings == null) inputBindings = "";
		if(outputBindings == null) outputBindings = "";
		
		return "Instance: < " + name + " " + className + " "+ inputBindings + " "+ outputBindings + " " + attributes + " >";
	}
}
