package com.editor.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.html.parser.AttributeList;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mxgraph.frames.FrameInstance;
import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;

public class XMLParserIOOBN {
	public static int rowIndex = 0;
	public static int numOfRow = 0;// intentionally given a big number
	public static Object[][] dataTable;
	public static BasicNode runningNode;
	
	public void recursiveParserXML(NodeList nodeList, ArrayList<String> tagList, mxCell cell){
		
		for (int elem = 0; elem < nodeList.getLength(); elem++) {// for each element
			/* If the element is a "Node" initiate/create mxCell and BasicNode and then pass that mxCell 
			 * 				in further recursive calls to be completed. 
			 * If the element is a "state" add in mxCell and BasicNode and then pass that mxCell 
			 * 				in further recursive calls. (if prev is null, then create new state List otherwise just add in List)
			 * If the element is a "parent" add in mxCell and BasicNode and then pass that mxCell 
			 * 				in further recursive calls.	(if prev is null, then create new parent List otherwise just add in List)
			 * If the element is a "datarow" add in mxCell and BasicNode and then pass that mxCell 
			 * 				in further recursive calls. (if prev is null, then create new model otherwise just add in model)
			*/

			Node eNode = nodeList.item(elem); // each element is called as eNode here 
			
			Element eElem = (Element) eNode;
			
			String[] columnNames;
			
			NamedNodeMap childAttribList = eElem.getAttributes();
			System.out.println(eNode.getNodeName() + " " + eNode.getTextContent());
			if(eNode.getNodeName().equalsIgnoreCase("node")){
				cell = new mxCell();
				runningNode = new BasicNode();
				runningNode.states = new ArrayList<String>(); // this is to remove default constructor's 2 default states 
				
				dataTable = null;
				rowIndex = 0;
				numOfRow = 0;// intentionally given a big number
				
				for (int attrib = 0; childAttribList!= null && attrib < childAttribList.getLength(); attrib++) {// for each attrib
					Node aNode = childAttribList.item(attrib); // each element is called as aNode here
					
					if(aNode.getNodeName().equalsIgnoreCase("id")){
//						cell.setId(instanceCount+"_"+aNode.getTextContent());
						cell.setId(aNode.getTextContent());
					}
					else if(aNode.getNodeName().equalsIgnoreCase("name")){
						runningNode.name = aNode.getTextContent();
					}
					else if(aNode.getNodeName().equalsIgnoreCase("label")){
						cell.setValue(aNode.getTextContent());
						runningNode.label = aNode.getTextContent();
					}
					else if(aNode.getNodeName().equalsIgnoreCase("subType")){
						runningNode.subType = aNode.getTextContent();
					}
					else if(aNode.getNodeName().equalsIgnoreCase("type")){
						runningNode.type = aNode.getTextContent();
					}
					else if(aNode.getNodeName().equalsIgnoreCase("kind")){
						runningNode.isEIO = Integer.parseInt(aNode.getTextContent());
					}
				}
			}
			
			else if(eNode.getNodeName().equalsIgnoreCase("state")){
				for (int attrib = 0; childAttribList!= null && attrib < childAttribList.getLength(); attrib++) {// for each attrib
					Node aNode = childAttribList.item(attrib); // each element is called as aNode here
					runningNode.states.add(aNode.getTextContent());//
					System.out.println(aNode.getTextContent() + " Size " + runningNode.states.size());
				}
			}
			
			else if(eNode.getNodeName().equalsIgnoreCase("parent")){
				for (int attrib = 0; childAttribList!= null && attrib < childAttribList.getLength(); attrib++) {// for each attrib
					Node aNode = childAttribList.item(attrib); // each element is called as aNode here
					runningNode.parents.add(aNode.getTextContent());
					System.out.println(aNode.getTextContent() + " Size " + runningNode.parents.size());
				}
			}
			
			else if(eNode.getNodeName().equalsIgnoreCase("tuple")){
				for (int attrib = 0; childAttribList!= null && attrib < childAttribList.getLength(); attrib++) {// for each attrib
					Node aNode = childAttribList.item(attrib); // each element is called as aNode here
					String[] tempDataRow = aNode.getTextContent().split("\\$"); // to split with dollar you have to use "\\$"
					for(int l = 0; l < tempDataRow.length; l++)
						System.out.println("   " + tempDataRow[l]);
					
					numOfRow = runningNode.parents.size() + runningNode.states.size();
					if(dataTable == null){
//						columnNames = tempDataRow;
						dataTable = new Object[numOfRow+1][tempDataRow.length];
					}
					dataTable[rowIndex++] = tempDataRow;
					
				}
			}
			
			// this will recursively call the parser method for sub elements "parent", "States" and "tuple"
			for(int k = 0; k < tagList.size(); k++){
				NodeList childNodeList =  eElem.getElementsByTagName(tagList.get(k));
				recursiveParserXML(childNodeList, tagList, cell);
			}
			// at this point, mxCell decoration is complete for a particular <Node> tag
			System.out.println("num of row " + numOfRow + " num of row index " + rowIndex);
//			if(rowIndex == numOfRow-1){// this is for ensuring whether tuple checking is done or not
//			if(dataTable != null && rowIndex == numOfRow-1){
			if(eNode.getNodeName().equalsIgnoreCase("node"))
			{	
				if(dataTable != null){
					columnNames = (String[]) dataTable[0];
					dataTable = Arrays.copyOfRange(dataTable, 1, dataTable.length);
					runningNode.data = new DefaultTableModel(dataTable, columnNames)
					{
				          public boolean isCellEditable(int row, int column)
				          {
				        	  if(row < runningNode.parents.size())
				        		  return false;//This causes some cells to be not editable
				        	  else return true;
				          }
				          public void cellBackground(int row, int column){
				        	  
				          }
				        };
				}
				
				if (mxGraphModel.instanceAddingGoingOn == true){// same might be better if we do for super classes
					cell.setId(FrameInstance.classNInstanceName + "_" + cell.getId());
				}
				
				cell.setBNInfo(runningNode);
				
				StartingFrame.NodeTable.put(cell.getId(), cell);
				System.out.println("Component added: in Parsing XMLOwn " + cell.getId() + "Label " + cell.getValue());
			}
		}
	}
	
   public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
	   File inputFile = new File("C:\\Users\\msamiull\\workspace\\jgraphx-master\\t.xml");
       DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
       DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
       Document doc = dBuilder.parse(inputFile);
       
		doc.getDocumentElement().normalize();
        System.out.println("Main element :"+ doc.getDocumentElement().getNodeName());
        
        NodeList nodeList = doc.getElementsByTagName("node");

        ArrayList<String> tagList = new ArrayList<String>();

//        tagList.add("node");
        tagList.add("state");
        tagList.add("parent");
        tagList.add("tuple");
        System.out.println("Node list length in main " + nodeList.getLength());
        System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");
        StartingFrame.NodeTable = new HashMap<String, mxCell>();
        XMLParserIOOBN xmlParser = new XMLParserIOOBN();
        xmlParser.recursiveParserXML(nodeList, tagList, null);// initially mxCell is null
        XMLGenerator parser = new XMLGenerator();
        System.out.println(parser.generateXML());
        
   }
   
   
   
}