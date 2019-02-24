package com.editor.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/*
 * Basic Node : name & [type (discrete (by default or by choice)/continuous/decision/utility) 
 * 				(a string), set of attributes (or map of attributes)]
 * 
 * */

public class BasicNode implements Serializable{
//	public static boolean parentChanged = false;
	public String name;
	public String label;
	public String type;
	
	public ArrayList<String> dataTuple;// this contain a list of string where each string is a row (True-False value for a binary discrete variable, A, B, C for a ternary discrete variable)
	/*
	 * [0.1, 0.9, 0.2, 0.8, 0.3, 0.7, 0.4, 0.6] is actually [0.1, 0.9,     
	 *                                                       0.2, 0.8, 
	 *                                                       0.3, 0.7, 
	 *                                                       0.4, 0.6]
	 *
	 *   You might be wondering how do I know that we have to take 2 in each row, actually we don't need to 
	 *   bother about that here because this has been handled in NPPvisit and each row is actually string of 
	 *   two numbers here  
	 *                   
	 * which represent a table 
	 * True   0.1 0.2 0.3 0.4
	 * False  0.9 0.8 0.7 0.6
	 * 
	 * */ 
	
	public LinkedHashMap <String, String> attributes;
	
	/*In text version following are stored as attributes. In gui we have to store them as both attributes and separately*/
	public ArrayList<String> states;
	public int posX, posY;
	public String subType;
	public ArrayList<String> stateValues;
	public ArrayList<String> parents;// name of the parent nodes, that can be traced from a map of nodes
//	public String data;// In row major format the 2D representation of the CPT
	public TableModel data;// In row major format the 2D representation of the CPT
	public boolean parentChanged = false;
	public int isEIO = 0; // is embedded = 0, Input = 1, output = 2
	
	public BasicNode(){
		this.name = "";
		this.label = "";
		this.type = "discrete";
		this.posX = 0;
		this.posY = 0;
		this.attributes = new LinkedHashMap<String, String>();
		this.states = new ArrayList<String>();
		this.states.add("True");
		this.states.add("False");
		this.stateValues = new ArrayList<String>();
		this.subType = "Boolean";
		this.parents = new ArrayList<String>();
		this.parentChanged = false;
		this.dataTuple = new ArrayList<String>();
		this.isEIO = 0;
	}

	
	public BasicNode(String name, String label, String type, int posX, int posY, LinkedHashMap<String, String> attributes, 
						ArrayList<String> states, String subType, ArrayList<String> parents, ArrayList<String> stateValues, 
						TableModel data, int EIO){
		this.name = name;
		this.label = label;
		this.type = type;
		this.posX = posX;
		this.posY = posY;
		if(attributes != null)	this.attributes = attributes;
								else this.attributes = new LinkedHashMap<String, String>();
		if(states != null)		this.states = states;
								else{
									this.states = new ArrayList<String>();
									this.states.add("True");
									this.states.add("False");
								}
		this.subType = subType;
		if(parents != null)		this.parents = parents;
								else this.parents = new ArrayList<String>();
		if(stateValues != null) this.stateValues = stateValues;
								else this.stateValues = new ArrayList<String>(); 
		if(data != null)		this.data = data;
		this.parentChanged = false;
		this.isEIO = EIO;
		this.dataTuple = new ArrayList<String>();
	}
	
	public void setBasicNodeInfo(String name, String label, String type, int posX, int posY, LinkedHashMap<String, String> attributes, 
			ArrayList<String> states, String subType, String parent, ArrayList<String> stateValues, 
			TableModel data, boolean parentChanged, int EIO)
	{
		if(name != null)	this.name = name;
		if(label != null)	this.label = label;
		if(type != null)	this.type = type;
		if(posX >= 0)	this.posX = posX;
		if(posY >= 0)	this.posY = posY;
		if(attributes != null)	this.attributes = attributes;
		if(states != null)		this.states = states;
		if(subType != null)	this.subType = subType;
//		if(parent != null && !this.parents.contains(parent) )		this.parents.add(parent);
		if(parent != null && !parents.contains(parent))		this.parents.add(parent);
		if(stateValues != null)		this.stateValues = stateValues;
		if(data != null)		this.data = data;
		if(parentChanged == true) this.parentChanged = parentChanged;
		if(EIO != -1) this.isEIO = EIO;
	}
	
	public void convertDataTupleToTable(ArrayList<ArrayList<String>> parentStates, ArrayList<String> parentsNames, boolean experimentGoing){
		/*
		 * we have to convert the datatuples into a table
		 * to do that we need two arrays, 1) 1D array of strings representing the header row
		 * 								  2) 2D Object array representing data, i.e. an array of arrays  
		 * 									where each array represents a data row in the table
		 * #Row = #parent+#states of the node (+ 1 header row which)
		 * #Col = 1 (for states of the nodes) + Product(Each parent's #State) 
		 *  
		 * */
		if(experimentGoing == false){
			int row = this.states.size() + this.parents.size()+1;// 1 for gap row
			int col = 1 + product(parentStates);
			int gapRowIndex = this.parents.size();
			int dataStartsFrom = this.parents.size()+1;
			
			Object [][] table = new Object[row][col];
			String Header [] = new String [col];
			
			/*
			 * Header row formation
			 * */
	//		System.err.println(parentsNames);
			Header[0] = parentsNames.get(0);
			int next = 0;
			int gap = (col-1) / parentStates.get(0).size(); // if col = 9, state size = 2, gap = 4 
			System.out.println("col = " + col + " states" + parentStates.get(0));
			for(int i = 1; i < col; i++){// i represents col here
				if ((i-1)% gap == 0){
					Header[i] = parentStates.get(0).get(next);
					next++;
				}
				else
					Header[i] = "^";
			}
	
			/*
			 * Remaining Header rows formation
			 * */
			for (int i = 1; i < parentStates.size(); i++){// i represents row, for remaining parents' states
				table[i][0] = parentsNames.get(i);// next parent name should be in 1st cell of the (i-1)th row
				int nextInd = 0;
				int gapDis = (col-1)/product(parentStates, i);// if col = 9, state sizes = 2 and 2, gap = 2 
				for(int j = 1; j < col; j++){// j represents col here
					if ((j-1)%gapDis == 0){
						table[i][j] = parentStates.get(i).get(nextInd%parentStates.get(i).size());
						nextInd++;
					}
					else
						table[i][j] = "^";
				}
			}
			
			// padding a row with ^ to indicate end of header
			if(gapRowIndex > 0)// for potentials/cpt with no parents
			for (int j = 0; j < col; j++)
				table[gapRowIndex][j] = "^"; 
			
			
			// setting 1st columns of each row for data part of the table
			for(int i = 0; i < this.states.size(); i++){
	//			System.out.println(i + " " + dataStartsFrom);
				table[dataStartsFrom+i][0] = this.states.get(i);// 1st col of each row should be a state of the node
			}
			
			int colIndex = 1;
			
			for(String tup: this.dataTuple){
				System.out.println(tup);
				String [] tupArray = tup.replace(" ", "").split(",");  // 0.1 0.2 0.3 should be assigned to row dataStartsFrom, dataStartsFrom+1, dataStartsFrom+2
				
				for(int i = 0; i < tupArray.length; i++){
					table[dataStartsFrom+(i%this.states.size())][colIndex] = tupArray[i];// the % is very important here to automatically convert a data tuple without any format and in 1 single string
					if(i>0 && i % this.states.size() == 0)
						colIndex += 1;
				}
			}
			
			 table[0] = Header;
			
			this.data = new DefaultTableModel(table, Header)
	        {
	          public boolean isCellEditable(int row, int column)
	          {
	        	  if(row < parents.size())
	        		  return false;//This causes all cells to be not editable
	        	  else return true;
	          }
	          public void cellBackground(int row, int column){
	        	  
	          }
	        };
		}
	}
	
	public int product(ArrayList<ArrayList<String>> parentStates, int x) {
		int prod = 1;
		for(int i = 0; i <= x; i++)
			prod *= parentStates.get(i).size();
		return prod;
	}


	public int product(ArrayList<ArrayList<String>> parentStates) {
		int prod = 1;
		for(ArrayList<String> states: parentStates)
			prod *= states.size();
		return prod;
	}


	public boolean removeParent(String nodeKey){
		if(this.parents.contains(nodeKey)){
			this.parents.remove(nodeKey);
			return true;
		}
		return false;
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
		return "Basic Node: < " + type + " " + name + " " + attributes + " " + label + " " + posX + " " + posY + " " + subType + " " + stateValues + " >";
	}
	
}
