package ANTLR_NPP;

import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 * Potential : name (child and parent info) & 
 * [child List (string), parent list (string), data (string), set of attributes (or map of attributes)]
 * 
 * the map is between <attribute name, attribute> or <attribute name, attribute value>. Currently,
 * I prefer the later one. Because, attribute contains only two strings. one will be in key another
 * will be in value of the map. Hence full info can be retrieved. If attribute is more than this two 
 * strings, then we can think of the former one.
 * 
 *  
 * Note 1: to pertain the data format, be careful about '(' and ')' we are losing them in the tree.
 * So try to retrieve them manually
 * 
 * Note 2: whoever uses potential in a map, should make it able to access by both name and 
 * child+parent info (like list of child and parent) 
 * */

public class Potential {
	String name; // concatenation of child name and parent name i.e. ege information
	String childList;
	String parentList;
	String data;
	LinkedHashMap <String, String> attributes; 
	
	public Potential(){
		this.name = "";
		this.childList = "";
		this.parentList = "";
		this.data = "";
		attributes = new LinkedHashMap<String, String>();
	}
	
	public Potential(String edgeInformation, String data){
		this.name = edgeInformation;
		this.data = data;
		attributes = new LinkedHashMap<String, String>();
	}
	
	public Potential(String edgeInfo, String childList, String parentList, String data){
		this.childList = childList;
		this.parentList = parentList;
		if(parentList == "")	edgeInfo = childList + "no parent";
		else edgeInfo = childList + "parent";
		this.data = data;
		attributes = new LinkedHashMap<String, String>();
	}
	
	public void insertParent(String parent){
		this.parentList = this.parentList + " " + parent; 
	}
	
	public void insertAttribute(Attribute attribute){
		this.attributes.put(attribute.name, attribute.value);
	}
	
	public String getName(){
		return name;
	}
	
	public String getChildList(){
		return childList;
	}
	
	public String getParentList(){
		return parentList;
	}
	
	public String getData(){
		return data;
	}
	
	public HashMap<String, String> getAttributes (){
		return attributes;
	}
	
	public Attribute getAttribute(String name){
		Attribute retAttr = new Attribute(name, (String) attributes.get(name));
		
		return retAttr;
	}
	
	public StringBuilder printPotential(int numOfTab){
		Attribute tempAttribute;
		
		StringBuilder buffer = new StringBuilder();
		
		String edgeInfo;
		
		if(childList != "" && parentList == "") {edgeInfo = childList; }
		else if( childList != "" && parentList != "") {edgeInfo = childList + " | " + parentList; }
		else edgeInfo = name;
		
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");
		buffer.append("\t");
		}
		//System.out.print("potential ( " + edgeInfo + " )\n");
		buffer.append("potential ( " + edgeInfo + " )\n");
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");
		buffer.append("\t");
		}
		//System.out.println("{");
		buffer.append("{");
		
		for (String key : attributes.keySet()){
			tempAttribute = new Attribute(key, attributes.get(key));
			buffer.append(tempAttribute.printAttribute(numOfTab+1));
		}
		
		// do something for data
		for(int I = 0; I <= numOfTab; I++)	{//System.out.print("\t");	
		buffer.append("\t");} // (1 tab extra for data)
		//System.out.println(data);
		buffer.append(data);
		
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");
		buffer.append("\t");}
		//System.out.println("}");
		buffer.append("}");
		
		return buffer;
	}
	
	@Override
	public String toString(){
		if(childList == null)	childList = "";
		if(parentList == null)	parentList = "";
		if(data == null) data = "";
		return "Potential: < " + name + " " + childList + " "+ parentList + " "+ data + " " + attributes + " >";
	}
	
}
