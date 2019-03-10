package com.editor.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import com.mxgraph.frames.FrameTable;
import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

public class CodeGenerator {
	
	public static HashMap<String, String> InstanceClassMap = new HashMap<String, String>();
	
	public String generateCode(Object[] vertices, String fileName)
	{
		String[] fileNameParts = fileName.split("\\\\");
		fileName = fileNameParts[fileNameParts.length-1];
//		String generatedCode = "class " + fileName.replace(".class", "\n{\n");
		String generatedCode = "";
		String allPotential = "";
		
	    // format sample for Input and output specification: "inputs = (mare sire);"
		String inputSpec = "\tinputs = (";
		String outputSpec = "\toutputs = (";
		
		for(int i=0; i < vertices.length; i++){
			mxCell cell = (mxCell) vertices[i];
			if(StartingFrame.NodeTable.containsKey(cell.getId()) && !(cell.getId().contains("::") && cell.getId().contains("_"))){
				BasicNode tempNode = StartingFrame.NodeTable.get(cell.getId()).getBNInfo();
				if(tempNode != null){
					ArrayList<Integer> statesCountStatic = new ArrayList<Integer>();
			        
					ArrayList<Integer> parentStates = new ArrayList<Integer>();
					for(int j = 0; j < tempNode.parents.size(); j++){
						BasicNode tempParent = StartingFrame.NodeTable.get(tempNode.parents.get(j)).getBNInfo();
						parentStates.add(tempParent.states.size());
					}
			        int tempStateCount = 1;
			        for(int j = 0; j < parentStates.size(); j++){
			        	tempStateCount *= parentStates.get(j);
			        	statesCountStatic.add((Integer)tempStateCount);
			        }
					
					generatedCode+= ("\tnode " + tempNode.name + "\n\t{\n");
					
					generatedCode+= ("\t\tlabel= \"" + tempNode.label+ "\";\n");
					generatedCode+= ("\t\ttype=\"" + tempNode.type+ "\";\n");
					generatedCode+= ("\t\tsubType=\"" + tempNode.subType+ "\";\n");
					
					if(tempNode.isEIO == 1){
						inputSpec += (" " + tempNode.name); 
					}
					else if(tempNode.isEIO == 2){
						outputSpec += (" " + tempNode.name);
					}
					
					String states = "";
					for(String key: tempNode.states){
						states += ("\"" + key + "\" ");
					}
					generatedCode+= ("\t\tstates=(" + states + ");\n");
					
					if(tempNode.stateValues.size()>0){
						String stateValues = "";
						for(int j = 0; j < tempNode.stateValues.size(); j++)
							stateValues += ("\"" + tempNode.stateValues.get(j) + "\" ");
						
						generatedCode+= ("\t\tstatesValue=(" + stateValues + ");\n");
					}
					
					generatedCode += "\t}\n";
					
					String potential = "\tpotential ( " + tempNode.name;
					
					if(tempNode.parents.size()>0)	 potential += " | ";
					
					String parents = "";
					
					ArrayList<String> parentsList = new ArrayList<String>();
					ArrayList<ArrayList<String>> parentStatesList = new ArrayList<ArrayList<String>>();
					
					for(int j = 0; j < tempNode.parents.size(); j++){
						BasicNode tempParent = StartingFrame.NodeTable.get(tempNode.parents.get(j)).getBNInfo();
						
						// do something to add instance name in case parent is from an instance
						String tempInstance = "";
						if(tempNode.parents.get(j).contains("::") && tempNode.parents.get(j).contains("_")){
							tempInstance = tempNode.parents.get(j);
							String arr1 [] = tempInstance.split("::");
							String tempStr = arr1[arr1.length-1];
							String arr2[] = tempStr.split("_");
							tempInstance = arr2[0]; // temp will contain instance name, since "class::instance_Node" is the format
							for(int I = 1; I < arr2.length-1; I++)	tempInstance += ("_"+arr2[I]);
							tempInstance += "_";
						}
						
						parents += (tempInstance+ tempParent.name + " ");
						parentsList.add(tempInstance+tempParent.name);
						parentStatesList.add(tempParent.states);
					}
					
					potential += (parents + " )\n\t{\n");
					
					String data = "";
					data = "\t\tdata=";
										
					data += showDataParenthesized(tempNode.data, tempNode.states, parentsList, parentStatesList);
					
					potential += data;
					
					potential += ";\n\t}\n";
					
					allPotential += potential;
				}
			}
		}
		
		// adding input and output specifications
		String generatedCodePrefix = "class " + fileName.replace(".class", "\n{\n"); // prefix means the part that should come first
		
		if(generatedCodePrefix.contains(".oobn")){
			generatedCodePrefix = "class " + fileName.replace(".oobn", "\n{\n"); // prefix means the part that should come first
		}
		
		generatedCodePrefix += (inputSpec + " );\n");
		generatedCodePrefix += (outputSpec + " );\n");
		generatedCode = generatedCodePrefix + generatedCode; 
		
		// do something to add instance info before potential info
		// instance names are unique for all classes, (e.g. Int a and Float a not possible)
		// for each referential edge, you will get IO mapping wrt an instance name
		// store them in order of src to target but in I mapping input parameter of the class to source of the edge and 
		//for O-mapping target of the edge to the output parameter
		
		HashMap<String, ArrayList<String>> InstanceTable = new HashMap<String, ArrayList<String>>();

		String className = "";
		if(mxGraphModel.refEdges.size() > 0){
			for(Object e: mxGraphModel.refEdges){
				mxCell cell = (mxCell) e;
				String tempID = "";
				boolean srcOrTarget = true;// false if src is interface node
				
				BasicNode BNsrc = StartingFrame.NodeTable.get(cell.getSource().getId()).getBNInfo();
				BasicNode BNtar = StartingFrame.NodeTable.get(cell.getTarget().getId()).getBNInfo();

				if(BNsrc.isEIO == 2){// if ref edge starts from an output, then source will give us parameters of the instance class
					srcOrTarget = false;// output mapping i.e. "srcInstance_srcNodeName = srcNodeName" 
					tempID = cell.getSource().getId();
				}
				else if(BNsrc.isEIO == 0){// else if ref edge starts from embedded, then target will give us parameter of instance class
					tempID = cell.getTarget().getId();// input mapping i.e. "tarNodeName = srcNodeName", 
					//here srcNodeName = might have leading "instanceName_" if this is from an instance. Hence we don't need to explicitly add it
				}
				else if (BNsrc.isEIO == 1){// else if ref edge starts from an input node, then target will give us parameter of instance class
					tempID = cell.getTarget().getId();// input mapping i.e. "tarNodeName = srcNodeName", 
					//here srcNodeName = might have leading "instanceName_" if this is from an instance. Hence we don't need to explicitly add it
				}
				else{
					System.err.println("The ref link has got unhandled case");
				}
				
				System.out.println("The tempID is: " + tempID);
				String parts1[] = tempID.split("::");
				className = parts1[0].replaceAll(".class", "");
				String parts2[] = parts1[parts1.length-1].split("_");
				String InstanceName = parts2[0];
				for(int I = 1; I < parts2.length-1; I++)
					InstanceName += ("_"+parts2[I]);
				String InterfaceNodeId = parts2[parts2.length-1];
				
				if(InstanceClassMap.containsKey(InstanceName) == false){
					InstanceClassMap.put(InstanceName, className);
				}
				else{
					
					// in case same instance name with different class found, show an error dialogue box
					if(!className.equalsIgnoreCase(InstanceClassMap.get(InstanceName)))
						JOptionPane.showMessageDialog(null , "Eggs are not supposed to be green.");
				}
				
				System.out.println(" Info of instance nodes " + className + " " + InstanceName + " " + InterfaceNodeId + " " + cell.getSource().getId() + " " + cell.getTarget().getId());
				
				ArrayList<String> tempList = new ArrayList<String>();
				if(srcOrTarget == false){// output mapping // false means src is output node not embedded node.
					if(InstanceTable.containsKey(InstanceName) == false){
						tempList.add("O::>" + InstanceName + "_" + BNsrc.name + "=" + BNsrc.name);
						InstanceTable.put(InstanceName, tempList);	
					}
					else InstanceTable.get(InstanceName).add("O::>" + InstanceName + "_" + BNsrc.name + "=" + BNsrc.name);
				}
				else{// true means src is embedded node not output node, hence input mapping.
					if(InstanceTable.containsKey(InstanceName) == false){
						tempList.add("I::>" + BNtar.name + "=" + BNsrc.name);
						InstanceTable.put(InstanceName, tempList);
					}
					else InstanceTable.get(InstanceName).add("I::>" + BNtar.name + "=" + BNsrc.name);
				}
				
			}
			
			for(String key: InstanceTable.keySet()){ 
				if(!key.equalsIgnoreCase("")) {// just to avoid creating an instance with no name
					ArrayList<String> temp2List = InstanceTable.get(key);
					String tempInputMapping = "";
					String tempOutputMapping = " ; ";
					
					for(String str: temp2List){
						if(str.charAt(0) == 'I'){
							if(tempInputMapping != "") tempInputMapping += ", ";{
								String arr1 [] = str.split("::>");
								tempInputMapping += arr1[arr1.length-1];
							}
						}
						else{
							if(tempOutputMapping != " ; ") tempOutputMapping += ", ";{
								String arr1 [] = str.split("::>");
								tempOutputMapping += arr1[arr1.length-1];
							}
						}
					}
					// if there is no output mapping then no ';' required
					if(tempOutputMapping == " ; ")	tempOutputMapping = "";
					
					generatedCode += ("\tinstance " + key + " : " + InstanceClassMap.get(key) + " ( " + tempInputMapping + tempOutputMapping + ")");
					generatedCode += "\n\t{\n\t\tlabel=\"\";";
					generatedCode += "\n\t\tposition=( 100 100);\n\t}\n"; // default value of instance position
				}
			}
		}
		else System.out.println("Ref edges size is 0 ");
		
		// do something to add potential info of some referential but required edge (output of instance to embedded) 
		// to allPotential before adding it to generatedCode 
		
		generatedCode += allPotential;
		
		generatedCode += "\n}";
		
		return generatedCode;
	}
	
	public String generateXMLFromiOOBNGraphics()// this function still requires refLink adding part
	{
		String generatedCode = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<iOOBN>\n";
		for(String key2: StartingFrame.NodeTable.keySet()){
			mxCell cell = StartingFrame.NodeTable.get(key2);
			
				
			BasicNode tempNode = cell.getBNInfo();
			
			ArrayList<Integer> statesCountStatic = new ArrayList<Integer>();
	        
			ArrayList<Integer> parentStates = new ArrayList<Integer>();
			for(int j = 0; j < tempNode.parents.size(); j++){
				BasicNode tempParent = StartingFrame.NodeTable.get(tempNode.parents.get(j)).getBNInfo();
				parentStates.add(tempParent.states.size());
			}
	        int tempStateCount = 1;
	        for(int j = 0; j < parentStates.size(); j++){
	        	tempStateCount *= parentStates.get(j);
	        	statesCountStatic.add((Integer)tempStateCount);
	        }
			
			generatedCode+= "\t<node ";
			
			generatedCode+= (" id=\"" + cell.getId()+ "\"");
			generatedCode+= (" name=\"" + tempNode.name + "\"");
			generatedCode+= (" label=\"" + tempNode.label+ "\"");
			generatedCode+= (" type=\"" + tempNode.type+ "\"");
			generatedCode+= (" subType=\"" + tempNode.subType+ "\"");
			generatedCode+= (" kind=\"" + tempNode.isEIO+ "\"");
			generatedCode+= ">\n";
			
			
			for(int j = 0; j < tempNode.states.size(); j++)
				generatedCode+= ("\t\t<state name=\"" + tempNode.states.get(j) + "\" />\n");
			
			for(int j = 0; j < tempNode.parents.size(); j++)
				generatedCode+= ("\t\t<parent id=\"" + tempNode.parents.get(j) + "\" />\n");
			
			for(int j = 0; j < tempNode.stateValues.size(); j++)
				generatedCode+= ("\t\t<stateValue name=\"" + tempNode.stateValues.get(j) + "\" />\n");
			
			for(String key : tempNode.attributes.keySet())
				generatedCode+= ("\t\t<attribute name=\"" + key + "\""  + " value=\"" + tempNode.attributes.get(key) + "\" />\n");
			
			
			ArrayList<String> table = storeDataTabular(tempNode.data);
			for(int j = 1; j < table.size(); j++)
				generatedCode+= ("\t\t<tuple value=\"" + table.get(j) + "\" />\n");
			
			generatedCode+= "\t</node>\n";
		}
		generatedCode += "</iOOBN>";
		return generatedCode;
	}
	
	public String showDataParenthesized(TableModel model, ArrayList<String> states, ArrayList<String> parents, ArrayList<ArrayList<String>> parentStates){
		
		ArrayList<Integer> statesCountStatic;
		
		int numOfParents = parents.size();
		
		statesCountStatic = new ArrayList<Integer>();
        
        int tempStateCount = 1;
        for(int i = 0; i < parentStates.size(); i++){
        	tempStateCount *= parentStates.get(i).size();
        	statesCountStatic.add((Integer)tempStateCount);
        }
		
		// row starts from 1 and column from NumOfParents in the beliefTable
//		System.out.println("Parenthesized format");
		
		String parenthesizedData = "";
		// run column major format then start counting of cells. 
		// If counter is divisible by 1, then use all parents starting parenthesis 
		// say, Child has 3, P1 has 2, P2 has 2 and P3 has 4 states, then for each 
		// total cell = 3x4x2x2 = 48, if divisible by 3 then start parenthesis for Child
		// if divisible by 12 and divisible by 24. These numbers can be found in staticStateCounts array
		// statesCountStatic = 4, 8, 16
		String tempDataCol = "";
		String tab = "";
//		System.out.println(statesCountStatic);
		if(model != null){
			for (int I = 1; I < model.getColumnCount(); I++){
				
				for(int k = 0; k < parents.size(); k++){
					tab = new String(new char[k+2]).replace("\0", "\t");// for K = 0, i.e 1st parent will have no braces
					if((I-1)%statesCountStatic.get(parents.size()-k-1) == 0){
						tempDataCol += ("\n"+tab+"(");
					}
				}
				
				tab = new String(new char[parents.size()+2]).replace("\0", "\t");
				if(parents.size() > 0)	tempDataCol += ("\n"+tab+"(");
				else tempDataCol += "(";// to avoid "\n and \t" in case of no parents
				
				for(int J = numOfParents; J < model.getRowCount(); J++){
		              tempDataCol += model.getValueAt(J, I).toString();
		              tempDataCol += " ";
				}
				tempDataCol += ")";
				
				for(int k = 0; k < parents.size(); k++){
					tab = new String(new char[parents.size()-k-1+2]).replace("\0", "\t");// for K = 0, i.e last parent will have braces after (numPar -1) tab
					if((I)%statesCountStatic.get(k) == 0){
						tempDataCol += ("\n"+tab+")");
					}
				}
				
	        }
		}
		else{// this is for default data generation
			int numOfCol = 1;
	        if(parents.size()>0){
		        for(int I = 0; I <parentStates.size(); I++)
		        		numOfCol *= parentStates.get(I).size();
	        }
	        
	        numOfCol++;
	        
	        int numOfRow = states.size()+parents.size();
	        if(numOfRow == 0) numOfRow = 2;// default 2 states	
	        
	        for (int I = 1; I < numOfCol; I++){
				
				for(int k = 0; k < parents.size(); k++){
					tab = new String(new char[k+2]).replace("\0", "\t");// for K = 0, i.e 1st parent will have no tabs
					if((I-1)%statesCountStatic.get(parents.size()-k-1) == 0){
						tempDataCol += ("\n"+tab+"(");
					}
				}
				tab = new String(new char[parents.size()+2]).replace("\0", "\t");
				if(parents.size() > 0)	tempDataCol += ("\n"+tab+"(");
				else tempDataCol += "(";// to avoid "\n and \t" in case of no parents
				
				for(int J = numOfParents; J < numOfRow; J++){
		              tempDataCol += "0.5";// default value
		              tempDataCol += " ";
				}
				tempDataCol += ")";
				
				for(int k = 0; k < parents.size(); k++){
					tab = new String(new char[parents.size()-k-1+2]).replace("\0", "\t");// for K = 0, i.e last parent will have braces after (numPar -1) tab
					if((I)%statesCountStatic.get(k) == 0){
						tempDataCol += ("\n"+tab+")");
					}
				}
				
	        }
		}
		
//		if(tempDataCol.equalsIgnoreCase("")){
//			
//			tempDataCol = "(0.5 0.5)";
//		}
		return tempDataCol;
	}
	
	public ArrayList<String> storeDataTabular(TableModel model) {
		// header row missing
		System.out.println("Tabular format");
		ArrayList<String> Tabular = new ArrayList<String>();
		String tempDataRow = "";
		
		/*
		 * $ is the separator of different cell
		 * ^ is the indicator of null/vacant cell
		 * */
		
		if(model != null){
			for(int I = 0; I < model.getColumnCount(); I++)
			{
				if(model.getColumnName(I) != null && model.getColumnName(I) != "")
					tempDataRow += (model.getColumnName(I) + "$");
				else tempDataRow += "^$";
			}
				
			Tabular.add(tempDataRow);
			for (int I = 0; I < model.getRowCount(); I++) {
				tempDataRow = "";
				for (int J = 0; J < model.getColumnCount(); J++) {
					if(model.getValueAt(I, J) != null)
						tempDataRow += (model.getValueAt(I, J).toString() + "$");
					else tempDataRow += "^$";
					
				}
				Tabular.add(tempDataRow);
			}
		}
		return Tabular;
	}
	
}
