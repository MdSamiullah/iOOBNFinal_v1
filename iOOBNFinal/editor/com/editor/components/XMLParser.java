package com.editor.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.swing.text.html.parser.AttributeList;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class XMLParser {
	
	public static void recursiveParserXML(NodeList nodeList, ArrayList<String> tagList){
		for (int elem = 0; elem < nodeList.getLength(); elem++) {// for each element
			Node eNode = nodeList.item(elem); // each element is called as nNode here 
			System.out.println(eNode.getNodeName());
			
			Element eElem = (Element) eNode;
			
			NamedNodeMap childAttribList = eElem.getAttributes();
	        for (int attrib = 0; childAttribList!= null && attrib < childAttribList.getLength(); attrib++) {// for each attrib
				Node aNode = childAttribList.item(attrib); // each element is called as nNode here
				System.out.println("Attribute: " + aNode.getNodeName() + " -> " + aNode.getTextContent());
			}
			
			for(int k = 1; k < tagList.size(); k++){
				NodeList childNodeList =  eElem.getElementsByTagName(tagList.get(k));
				recursiveParserXML(childNodeList, tagList);
			}
		}
	}
	
	// this is not complete. Yet to be added : mxPoint tag
	public static void nonRecursiveParserXML(NodeList rList, Document doc){
	      try {	
	          System.out.println("===========================");
	          for (int temp1 = 0; temp1 < rList.getLength(); temp1++) {
	         	 NodeList nList = doc.getElementsByTagName("mxCell");
	              System.out.println("++++++++++++++   mxCell   +++++++++++++++++");
	              for (int temp = 0; temp < nList.getLength(); temp++) {// for each cell
	                 Node nNode = nList.item(temp); // a cell is called in as nNode
	                 System.out.println("\nCurrent Element :" + nNode.getNodeName());
	                 if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                    Element eElement = (Element) nNode;
	                    NamedNodeMap attList = eElement.getAttributes(); 
	                    
	                    for(int ind = 0; ind < attList.getLength(); ind++){
	                 	   System.out.println("Attribute :" + attList.item(ind));
	                    } 
	                    NodeList subNodes = eElement.getElementsByTagName("mxGeometry");
	                    System.out.println("\t\t------------    mxGeometry    --------------");
	                    for(int subInd = 0; subInd < subNodes.getLength(); subInd++){
	                 	   NamedNodeMap elemAtList = subNodes.item(subInd).getAttributes(); 
	                        
	                        for(int ind2 = 0; ind2 < elemAtList.getLength(); ind2++){
	                     	   System.out.println("Attribute :" + elemAtList.item(ind2));
	                        }
	                    }
	                  //System.out.println("First Name : "      + eElement.getElementsByTagName("mxGeometry").item(0).getTextContent());
	                 }
	              } 
	          }
	       } catch (Exception e) {
	          e.printStackTrace();
	       }
		}
	
   public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
//	   File inputFile = new File("C:\\Users\\msamiull\\workspace\\jgraphx-master\\examples\\com\\mxgraph\\examples\\swing\\xxxxxx.mxe");
	   File inputFile = new File("C:\\Users\\msamiull\\workspace\\jgraphx-master\\t.xml");
       DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
       DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
       Document doc = dBuilder.parse(inputFile);
       
		doc.getDocumentElement().normalize();
        System.out.println("Main element :"+ doc.getDocumentElement().getNodeName());
//        NodeList nodeList = doc.getElementsByTagName("root");
        
        NodeList nodeList = doc.getElementsByTagName("iOOBN");
//      NamedNodeMap attribList = doc.getAttributes();
        
//       nonRecursiveParserXML(nList, doc);

        ArrayList<String> tagList = new ArrayList<String>();
//        tagList.add("mxCell");
//        tagList.add("mxGeometry");
//        tagList.add("mxPoint");
        tagList.add("Node");
        tagList.add("state");
        tagList.add("tuple"); // purposely i raplced "datarow" by "tuple" so that my parser can consider state first before tuple/data
        tagList.add("parent");
        
        recursiveParserXML(nodeList, tagList);

   }
}