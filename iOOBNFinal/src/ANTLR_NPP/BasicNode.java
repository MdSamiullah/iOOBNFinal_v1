package ANTLR_NPP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 * Basic Node : name & [type (discrete (by default or by choice)/continuous/decision/utility) 
 * 				(a string), set of attributes (or map of attributes)]
 * 
 * */

public class BasicNode {
	String name;
	String type;
	LinkedHashMap <String, String> attributes;
	ArrayList<String> dataTuple;
	
	public BasicNode(){
		this.name = "";
		this.type = "discrete";
		attributes = new LinkedHashMap<String, String>();
		this.dataTuple = new ArrayList<String>();
	}
	
	public BasicNode(String name){
		this.name = name;
		this.type = "discrete";
		attributes = new LinkedHashMap<String, String>();
		this.dataTuple = new ArrayList<String>();
	}
	
	public BasicNode(String name, String type){
		this.name = name;
		this.type = type;
		attributes = new LinkedHashMap<String, String>();
	}
	
	public void insertAttribute(Attribute attribute){
		this.attributes.put(attribute.name, attribute.value);
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public HashMap<String, String> getAttributes (){
		return attributes;
	}
	
	public Attribute getAttribute(String name){
		Attribute retAttr = new Attribute(name, (String) attributes.get(name));
		
		return retAttr;
	}
	
	public StringBuilder printBasicNode(int numOfTab){
		
		StringBuilder buffer = new StringBuilder();
		
		Attribute tempAttribute;
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");	
		buffer.append("\t");}
		//System.out.print(type + " " + name + "\n");
		buffer.append(type + " " + name + "\n");
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");	
		buffer.append("\t");}
		//System.out.print("{\n");
		buffer.append("{\n");
		
		for (String key : attributes.keySet()){
			tempAttribute = new Attribute(key, attributes.get(key));
			buffer.append(tempAttribute.printAttribute(numOfTab+1));
		}
		
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");	
		buffer.append("\t");}
		//System.out.print("}\n");
		buffer.append("}\n");
		
		
		return buffer;
	}
	
	@Override
	public String toString(){
		return "Basic Node: < " + type + " " + name + " " + attributes + " >";
	}
	
}
