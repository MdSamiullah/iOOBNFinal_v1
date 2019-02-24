package com.editor.components;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.TableModel;

import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

import ANTLR_NPP.NPPvisit;

public class XMLGenerator {
//	public String generateXML(Object[] vertices)
//	{
//		String generatedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><iOOBN>";
//		for(int i=0; i < vertices.length; i++){
//			mxCell cell = (mxCell) vertices[i];
//			if(StartingFrame.NodeTable.containsKey(cell.getId())){
//				
//				BasicNode tempNode = StartingFrame.NodeTable.get(cell.getId()).getBNInfo();
//				
//				ArrayList<Integer> statesCountStatic = new ArrayList<Integer>();
//		        
//				ArrayList<Integer> parentStates = new ArrayList<Integer>();
//				for(int j = 0; j < tempNode.parents.size(); j++){
//					BasicNode tempParent = StartingFrame.NodeTable.get(tempNode.parents.get(j)).getBNInfo();
//					parentStates.add(tempParent.states.size());
//				}
//		        int tempStateCount = 1;
//		        for(int j = 0; j < parentStates.size(); j++){
//		        	tempStateCount *= parentStates.get(j);
//		        	statesCountStatic.add((Integer)tempStateCount);
//		        }
//				
//				generatedXML+= "<node ";
//				
//				generatedXML+= (" id=\"" + cell.getId()+ "\"");
//				generatedXML+= (" name=\"" + tempNode.name + "\"");
//				generatedXML+= (" label=\"" + tempNode.label+ "\"");
//				generatedXML+= (" type=\"" + tempNode.type+ "\"");
//				generatedXML+= (" subType=\"" + tempNode.subType+ "\"");
//				generatedXML+= ">";
//				
//				
//				for(int j = 0; j < tempNode.states.size(); j++)
//					generatedXML+= ("<state name=\"" + tempNode.states.get(j) + "\" />");
//				
//				for(int j = 0; j < tempNode.parents.size(); j++)
//					generatedXML+= ("<parent id=\"" + tempNode.parents.get(j) + "\" />");
//				
//				for(int j = 0; j < tempNode.stateValues.size(); j++)
//					generatedXML+= ("<stateValue name=\"" + tempNode.stateValues.get(j) + "\" />");
//				
//				for(String key : tempNode.attributes.keySet())
//					generatedXML+= ("<attribute name=\"" + key + "\""  + " value=\"" + tempNode.attributes.get(key) + "\" />");
//				
////				String dataValue = "";
////				if(tempNode.data != null)
////					dataValue = showDataParenthesizedNoTab(tempNode.data, tempNode.parents.size(), statesCountStatic); 				
////				generatedXML+= ("\t\t<data value=\"" + dataValue + "\" />\n");
//				
//				ArrayList<String> table = storeDataTabular(tempNode.data);
//				for(int j = 0; j < table.size(); j++)
//					generatedXML+= ("<tuple value=\"" + table.get(j) + "\" />");
//				
//				generatedXML+= "</node>";
//			}
//		}
//		generatedXML += "</iOOBN>";
//		return generatedXML;
//	}
	
	public String generateXML(Object[] vertices, Object[] edges)
	{
		String generatedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<iOOBN type=\"" + StartingFrame.iOOBNType + "\">\n";
		System.out.println(" Length " +vertices.length);
		for(int i=0; i < vertices.length; i++){
			mxCell cell = (mxCell) vertices[i];
			if(StartingFrame.NodeTable.containsKey(cell.getId())){
				
				BasicNode tempNode = StartingFrame.NodeTable.get(cell.getId()).getBNInfo();
				if(tempNode != null){
				
					ArrayList<Integer> statesCountStatic = new ArrayList<Integer>();
			        
					ArrayList<Integer> parentStates = new ArrayList<Integer>();
					for(int j = 0; j < tempNode.parents.size(); j++){
						System.out.println("Parent Size in XML generator " + tempNode.parents.size());
						if(StartingFrame.NodeTable.containsKey(tempNode.parents.get(j))){
							BasicNode tempParent = StartingFrame.NodeTable.get(tempNode.parents.get(j)).getBNInfo();
							parentStates.add(tempParent.states.size());
						}
					}
			        int tempStateCount = 1;
			        for(int j = 0; j < parentStates.size(); j++){
			        	tempStateCount *= parentStates.get(j);
			        	statesCountStatic.add((Integer)tempStateCount);
			        }
					
					generatedXML+= "\t<node ";
					
					generatedXML+= (" id=\"" + cell.getId()+ "\"");
					generatedXML+= (" name=\"" + tempNode.name + "\"");
					generatedXML+= (" label=\"" + tempNode.label+ "\"");
					generatedXML+= (" type=\"" + tempNode.type+ "\"");
					generatedXML+= (" subType=\"" + tempNode.subType+ "\"");
					generatedXML+= (" kind=\"" + tempNode.isEIO+ "\"");
					generatedXML+= ">\n";
					
					
					for(int j = 0; j < tempNode.states.size(); j++)
						generatedXML+= ("\t\t<state name=\"" + tempNode.states.get(j) + "\" />\n");
					
					for(int j = 0; j < tempNode.parents.size(); j++)
						generatedXML+= ("\t\t<parent id=\"" + tempNode.parents.get(j) + "\" />\n");
					
					for(int j = 0; j < tempNode.stateValues.size(); j++)
						generatedXML+= ("\t\t<stateValue name=\"" + tempNode.stateValues.get(j) + "\" />\n");
					
					for(String key : tempNode.attributes.keySet())
						generatedXML+= ("\t\t<attribute name=\"" + key + "\""  + " value=\"" + tempNode.attributes.get(key) + "\" />\n");
					
					ArrayList<String> table = storeDataTabular(tempNode.data);
					for(int j = 0; j < table.size(); j++)
						generatedXML+= ("\t\t<tuple value=\"" + table.get(j) + "\" />\n");
					
					generatedXML+= "\t</node>\n";
				}
			}
		}
		
		for(int j = 0; j < edges.length; j++){
			
			mxCell edge = (mxCell) edges[j];
			
			if(edge.getSource().getParent() != edge.getTarget().getParent())
			{
				BasicNode tempNode1 = StartingFrame.NodeTable.get(edge.getSource().getId()).getBNInfo();
				BasicNode tempNode2 = StartingFrame.NodeTable.get(edge.getTarget().getId()).getBNInfo();
				generatedXML+= "\t<RefLink>\n";
				generatedXML+= ("\t\t<source id=\"" + edge.getSource().getId() + "\"" + " name = \"" + tempNode1.name +  "\"" + " kind = \"" + tempNode1.isEIO +  "\" />\n");
				generatedXML+= ("\t\t<target id=\"" + edge.getTarget().getId() + "\"" + " name = \"" + tempNode2.name +  "\"" + " kind = \"" + tempNode2.isEIO +  "\" />\n");
				generatedXML+= "\t</RefLink>\n";
//				mxGraphModel.refEdges.add(edge);
			}
		}
		
		generatedXML += "</iOOBN>";
		return generatedXML;
	}
	
	public String generateXML()
	{
		HashMap<String, String> NameIdOfInstanceNodeInRefLinks = new HashMap<String, String>();
		String generatedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<iOOBN>\n";
		for(String key2: StartingFrame.NodeTable.keySet())
		{
			mxCell cell = StartingFrame.NodeTable.get(key2);
			
				
			BasicNode tempNode = cell.getBNInfo();
			
			ArrayList<Integer> statesCountStatic = new ArrayList<Integer>();
	        
			ArrayList<Integer> parentStates = new ArrayList<Integer>();
			System.out.println("Parent size in XMLGenerator generateXML " + tempNode.parents.size() + tempNode);
			for(int j = 0; j < tempNode.parents.size(); j++){
				System.out.println("Parent in generateXML" + tempNode.parents.get(j));
				BasicNode tempParent = StartingFrame.NodeTable.get(tempNode.parents.get(j)).getBNInfo();
				parentStates.add(tempParent.states.size());
			}
	        int tempStateCount = 1;
	        for(int j = 0; j < parentStates.size(); j++){
	        	tempStateCount *= parentStates.get(j);
	        	statesCountStatic.add((Integer)tempStateCount);
	        }
			
			generatedXML+= "\t<node ";
			
			generatedXML+= (" id=\"" + cell.getId()+ "\"");
			generatedXML+= (" name=\"" + tempNode.name + "\"");
			generatedXML+= (" label=\"" + tempNode.label+ "\"");
			generatedXML+= (" type=\"" + tempNode.type+ "\"");
			generatedXML+= (" subType=\"" + tempNode.subType+ "\"");
			generatedXML+= (" kind=\"" + tempNode.isEIO+ "\"");
			generatedXML+= ">\n";
			
			if(cell.getId().contains("::")){// the node Id has "::" means it is from an instance. So do the map
				NameIdOfInstanceNodeInRefLinks.put(tempNode.name, cell.getId());
//				System.out.println("The cell id is " + cell.getId());
			}
			
			
			for(int j = 0; j < tempNode.states.size(); j++)
				generatedXML+= ("\t\t<state name=\"" + tempNode.states.get(j) + "\" />\n");
			
			for(int j = 0; j < tempNode.parents.size(); j++)
				generatedXML+= ("\t\t<parent id=\"" + tempNode.parents.get(j) + "\" />\n");
			
			for(int j = 0; j < tempNode.stateValues.size(); j++)
				generatedXML+= ("\t\t<stateValue name=\"" + tempNode.stateValues.get(j) + "\" />\n");
			
			for(String key : tempNode.attributes.keySet())
				generatedXML+= ("\t\t<attribute name=\"" + key + "\""  + " value=\"" + tempNode.attributes.get(key) + "\" />\n");
			
//				String dataValue = "";
//				if(tempNode.data != null)
//					dataValue = showDataParenthesizedNoTab(tempNode.data, tempNode.parents.size(), statesCountStatic); 
			
//				generatedXML+= ("\t\t<data value=\"" + dataValue + "\" />\n");
			
			ArrayList<String> table = storeDataTabular(tempNode.data);
			for(int j = 1; j < table.size(); j++)
				generatedXML+= ("\t\t<tuple value=\"" + table.get(j) + "\" />\n");
			
			generatedXML+= "\t</node>\n";
		}
		System.out.println(" The map of id and name "+ NameIdOfInstanceNodeInRefLinks);
		/*
		 * I somehow missed the Ref Link part while generating the iOOBN .class code. It was later added
		 * during experimentation
		 * on 12/01/2019
		 * 
		 * Before I proceed here, I need to add the info in StartingFrame.NodeTable to get that I need to go back to the java code that called this function i.e. NPPCompiler
		 * */
//		BasicNode tempNode1 = StartingFrame.NodeTable.get(edge.getSource().getId()).getBNInfo();
//		BasicNode tempNode2 = StartingFrame.NodeTable.get(edge.getTarget().getId()).getBNInfo();
//		generatedXML+= "\t<RefLink>\n";
//		generatedXML+= ("\t\t<source id=\"" + edge.getSource().getId() + "\"" + " name = \"" + tempNode1.name +  "\" />\n");
//		generatedXML+= ("\t\t<target id=\"" + edge.getTarget().getId() + "\"" + " name = \"" + tempNode2.name +  "\" />\n");
//		generatedXML+= "\t</RefLink>\n";
		
		for(String key : NPPvisit.MainClassNodeToInstanceNodeList.keySet()){// MainClassNodeToInstanceNodeList is a list of ref links
			ArrayList<String> refEdgeTargetTerminals = NPPvisit.MainClassNodeToInstanceNodeList.get(key);
			for(String key1: refEdgeTargetTerminals){
				String keyId = NPPvisit.labelMapId.get(key);// for sure this is going to be a node in main class and it's kind can be found from NodeTable
				BasicNode tempNodeMain = StartingFrame.NodeTable.get(keyId).getBNInfo();
				String tempArr [] = key1.split("_");// assuming node names will be of the form "main1::Obj0C1_node1" i.e. <class::Obj_node> or only node1.
//				String key1Id = NameIdOfInstanceNodeInRefLinks.get(tempArr[tempArr.length-1]);// for sure, this is going to be the input node of an instance hence kind is 1
				/*
				 * For multiple ref links from same node, the map "NameIdOfInstanceNodeInRefLinks" fails to find all ref link correct
				 * */
				// assuming key1 = main1::Obj2C1_node0 and key1Id needs to be like "main1.class::Obj2C1_node0"
				String key1Id = key1;
				key1Id = key1Id.replace("::", ".class::");
			
				if(key1Id != null && keyId != null){
					generatedXML+= "\t<RefLink>\n";
					generatedXML+= ("\t\t<source id=\"" + keyId + "\"" + " name = \"" + key +  "\"" + " kind = \"" + tempNodeMain.isEIO +  "\" />\n");
					generatedXML+= ("\t\t<target id=\"" + key1Id + "\"" + " name = \"" + key1 + "\"" + " kind = \"" + "1" +  "\" />\n");
					generatedXML+= "\t</RefLink>\n";
				}
			}
		}
		
		generatedXML += "</iOOBN>";
		return generatedXML;
	}
	
	public String showDataParenthesizedNoTab(TableModel model, int NumOfParents, 
															ArrayList<Integer> statesCountStatic){
		// row starts from 1 and column from NumOfParents in the beliefTable
		System.out.println("Parenthesized format");
		String tempDataCol = "";
			
		for (int I = 1; I < model.getColumnCount(); I++){
			
			for(int k = 0; k < NumOfParents; k++){
				if((I-1)%statesCountStatic.get(k) == 0){
					tempDataCol += "{";
				}
			}
			
			tempDataCol += "{";
			
			for(int J = NumOfParents; J < model.getRowCount(); J++){
	              tempDataCol += model.getValueAt(J, I).toString();
	              tempDataCol += " ";
			}
			tempDataCol += "}";
			
			for(int k = 0; k < NumOfParents; k++){
				
				if((I)%statesCountStatic.get(k) == 0){
					tempDataCol += "}";
				}
			}
			
        }
		
		return tempDataCol;
	}
	
	public ArrayList<String> storeDataTabular(TableModel model) {
		// header row missing
		System.out.println("Tabular format");
		ArrayList<String> Tabular = new ArrayList<String>();
		String tempDataRow = "";
//		System.out.println();
		
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

/*
 * Tags : 
 * 	<iOOBN></iOOBN>
 * 	<Node id="" name="" label="" type="" subType=""  parentsId="">
 * 		<state name="" />
 * 			.
 * 			.
 * 			.
 * 		<state name="" />
 * 
 * 		<parents id=""/>
 * 			.
 * 			.
 * 			.
 * 		<parents id=""/>
 * 
 * 
 * 		<stateValues value=""/>
 * 			.
 * 			.
 * 			.
 * 		<stateValues value=""/>
 * 		
 * 		<attribute name = "" value = ""/>
 * 				.
 * 				.
 * 				.
 * 		<attribute name = "" value = ""/>
 * 		<data CPT="">
 * 			<attribute name = "" value = ""/>
 * 					.
 * 					.
 * 					.
 * 			<attribute name = "" value = ""/>
 * 		</data>
 * 	</Node>
 * 		.
 * 		.
 * 		.
 * 	<Node>
 * 		
 * 	</Node>
 */

/*
<?xml version="1.0" encoding="UTF-8"?>
<mxGraphModel>
   <root>
      <mxCell id="0" />
      <mxCell id="1" parent="0" />
      <mxCell id="2" parent="1" style="ellipse" value="C_9" vertex="1">
         <mxGeometry as="geometry" height="55.0" width="80.0" x="200.0" y="200.0" />
      </mxCell>
   </root>
</mxGraphModel>
*/