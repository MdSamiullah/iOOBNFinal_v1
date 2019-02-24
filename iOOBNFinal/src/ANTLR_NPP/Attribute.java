package ANTLR_NPP;

/*
 * Attribute : name (string) & value (= number, list of numbers, list of ID, null) = string
 * attribute can be "state", "label", "position" or general attributes. However, all can 
 * be represented as above name string and value string. Becasue,
 * 
 * Attribute "state": name = "state" a string, value = collection of strings = 1 string
 * Attribute "label": name = "label" a string, value = 1 string
 * Attribute "position": name = "position" a string, value = 2 numbers = 1 string
 * */

public class Attribute {
	String name;
	String value;
	
	public Attribute()
	{
		this.name = "";
		this.value = "";
	}
	
	public Attribute(String name, String value)
	{
		this.name = name;
		this.value = value;
	}
	
	public String getName(){
		return name;
	}
	
	public String getValue(){
		return value;
	}
	
	public StringBuilder printAttribute(int numOfTab){
		StringBuilder buffer = new StringBuilder();
		for(int I = 0; I < numOfTab; I++)	{//System.out.print("\t");	
		buffer.append("\t");}
		//System.out.print(name + " = " + value + ";\n");
		buffer.append(name + " = " + value + ";\n");
		
		return buffer;
	}
	
	@Override
	public String toString(){
		return "Attribute: < "+ name + " " + value + " >";
	}
}
