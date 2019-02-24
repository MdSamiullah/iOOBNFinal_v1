package ANTLR_NPP;

import java.util.ArrayList;
/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
***/
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.v4.runtime.misc.NotNull;

import com.mxgraph.util.mxPoint;

import ANTLR_NPP.mxCellStructure.Point;
	
public class NPPvisit extends NETPlusPlusBaseVisitor<String> {
	public ClassDefn classNode; // = new ClassDefn();
	
	public String parentClassList = "";
	public String parentInterfaceList = "";

	public static int IOE = 2; // 0 = input, 1 = output, 2 = embedded
	public static HashMap<String, ArrayList<String>> MainClassNodeToInstanceNodeList = new HashMap<String, ArrayList<String>>();// this is the map that contains all the referential links
	public static int id = 1;
	public static HashMap<String, String> labelMapId = new HashMap<String, String>();
	public static HashMap<String, Integer> IdMapXCoord = new HashMap<String, Integer>();
	public static HashMap<String, Integer> IdMapYCoord = new HashMap<String, Integer>();
	
	// don't re-initialize it in the constructor
	public static HashMap<String, Integer> InstanceIdMapXCoord = new HashMap<String, Integer>();
	public static HashMap<String, Integer> InstanceIdMapYCoord = new HashMap<String, Integer>();

	public static boolean doneNodeAttribute = false;
	public static boolean doneInstanceAttribute = false;
	public static String [] inputNodeList;
	public static String [] outputNodeList;
	
	public static HashMap<String, HashMap<String, mxCellStructure>> mxGraphsMap = new HashMap<String, HashMap<String, mxCellStructure>>(); 
//	public static ArrayList<mxCellStructure> mxGraph = new ArrayList<mxCellStructure>();
	public static HashMap<String, mxCellStructure> mxGraph = new HashMap<String, mxCellStructure>();
	
	Attribute tempAttribute;
	BasicNode tempBasicNode;
	Instance tempInstance;
	Potential tempPotential;
	mxCellStructure mxCell;
	
	public String currentChildNodeLabelInPotential = "";
	
	
	public String ParsedClass; // this is the class name that we are parsing now, it has to be used in getting parent child mapping perfect
	
	
	
	public NPPvisit(){// I have added this constructor to initialize the static variables after each instantiation
		// so that when we create an instance of this class, everything is in its fresh form
		NPPvisit.id = 1;
		NPPvisit.labelMapId = new HashMap<String, String>();
		NPPvisit.IdMapXCoord = new HashMap<String, Integer>();
		NPPvisit.IdMapYCoord = new HashMap<String, Integer>();
		
		NPPvisit.IOE = 2; // 0 = input, 1 = output, 2 = embedded
		
		NPPvisit.doneNodeAttribute = false;
		NPPvisit.doneInstanceAttribute = false;
		NPPvisit.mxGraph = new HashMap<String, mxCellStructure>();
		
		classNode = new ClassDefn(); // = new ClassDefn();
		
		parentClassList = "";
		parentInterfaceList = "";
		
		inputNodeList = null;
		outputNodeList = null;
		
		tempAttribute = new Attribute();
//		tempBasicNode = new BasicNode();// be carefull. Don't activate this line in the constructor to avoid unnecessary garbage node to be created
		tempInstance = new Instance();
		tempPotential = new Potential();
//		mxCell = new mxCellStructure();// be carefull. Don't activate this line in the constructor to avoid unnecessary garbage node to be created
		
		currentChildNodeLabelInPotential = "";
		
		
		ParsedClass = ""; // this is the class name that we are parsing now, it has to be used in getting parent child mapping perfect
	}
	 
	/* 
	 * mxCell = new mxCellStructure(); 
	 * this mxCell is the graphical representation of nodes/edges/instances of a hugin oobn class
	 * to be represented in iOOBN
	 * 
	 * Translations:
 * 1. 	        node V
				{
					label= "V";
					type="Discrete";
					subType="Boolean";
					states=("True" "False" );
				}
	 * 		<mxCell id="2" parent="1" style="ellipse;dashed=1" value="V" vertex="1">
         		<mxGeometry as="geometry" height="55.0" width="80.0" x="120.0" y="90.0" />
      		</mxCell>
	 * 
	 * 2.	 potential ( E | L T  )
			{
				data=
				(
					(
						(0.5 0.5 )
						(0.5 0.5 )
					)
					(
						(0.5 0.5 )
						(0.5 0.5 )
					)
				);
			}
	 * 
	 * 
	 		<mxCell edge="1" id="14" parent="1" source="4" style="" target="7" value="">
		         <mxGeometry as="geometry" relative="1">
		            <mxPoint as="sourcePoint" x="160.0" y="250.0" />
		            <mxPoint as="targetPoint" x="230.0" y="380.0" />
		         </mxGeometry>
      		</mxCell>
      		<mxCell edge="1" id="13" parent="1" source="5" style="" target="7" value="">
		         <mxGeometry as="geometry" relative="1">
		            <mxPoint as="sourcePoint" x="310.0" y="250.0" />
		            <mxPoint as="targetPoint" x="250.0" y="380.0" />
		         </mxGeometry>
	      	</mxCell>
	 */
	
	/* 
	 * The graph for our iOOBN software will be constructed from oobn code. 
	 * Each node and potential (actually a set of edges/a single edge) constitutes an mxCell  
	 * and the instance construct will constitute a series of mxCells having nodes and edges in it. 
	 */
	
	
	/* since attribute is a common component for class, node, potential and class instance
	 *  we should keep track of the current meta component (class = 0, node = 1, pot = 2, class ins = 3)
	 */
	int metaComponentIndex = 0;// 1 for basic node, 2 for potentials/edges, 3 for instances
	
	// state : 'states' '=' '(' STRING* ')' ';';
		@Override public String visitState(  NETPlusPlusParser.StateContext ctx)  
		{ 
			String str = ctx.str;
			
			String statesValues = ctx.STRING().toString().replaceAll("[,]", "");
			statesValues = "( " + statesValues.substring(1,  statesValues.length()-1) + " )"; 
			
			tempAttribute = new Attribute("states", statesValues);
			
			if(metaComponentIndex == 1) tempBasicNode.insertAttribute(tempAttribute);
			else if(metaComponentIndex == 3) tempInstance.insertAttribute(tempAttribute);
			
			return visitChildren(ctx); 
		}
		
		// attribute : attribName '=' attribValue ';' ;
		@Override public String visitAttribute(  NETPlusPlusParser.AttributeContext ctx)  
		{ 
			tempAttribute = new Attribute(ctx.attribName.getText(), ctx.attribValue.str);
			if(metaComponentIndex == 0){
				if(ctx.attribName.getText().equalsIgnoreCase("inputs")) {
					classNode.insertInputs(ctx.attribValue.str);
					inputNodeList = ctx.attribValue.str.replace("(", "").replace(")", "").trim().split(" ");
//					for(String s: inputNodeList)
//						System.out.print(s + " ");
//					System.out.println();
				}
				else if (ctx.attribName.getText().equalsIgnoreCase("outputs")) {
					classNode.insertOutputs(ctx.attribValue.str);
					outputNodeList = ctx.attribValue.str.replace("(", "").replace(")", "").trim().split(" ");
//					for(String s: outputNodeList)
//						System.out.print(s + " ");
//					System.out.println();
				}
				else	classNode.insertAttribute(tempAttribute);
			}	else if(metaComponentIndex == 1){
				tempBasicNode.insertAttribute(tempAttribute);
			}	else if(metaComponentIndex == 2){
				tempPotential.insertAttribute(tempAttribute);
			}	else if(metaComponentIndex == 3){
				tempInstance.insertAttribute(tempAttribute);
			}
			
			return visitChildren(ctx); 
		}
		
		  
		
		// classInstance : 'instance' instanceName ':' className bindings '{' instanceAttributes '}';
		@Override public String visitClassInstance(  NETPlusPlusParser.ClassInstanceContext ctx)  
		{
			metaComponentIndex = 3;
			
			String className = ctx.className.getText();
			String instanceName = ctx.instanceName.getText();
			
			// process bindings (input bindings to deal with referential links)
			// in hugin they don't allow output node of an instance to input node of another instance 
			String binding = ctx.bindings.getText();
			String inBindings[];
			if(binding != null && !binding.equalsIgnoreCase("")){// neither empty nor null
				binding = binding.split(";")[0];
//				System.out.println(binding);
				inBindings = binding.split(",");
				
				for(String s: inBindings){
					String parts[] = s.split("=");
					String instanceNode = className + "::" + instanceName + "_" + parts[0];// [0] is the input node in an instance
//					String MainClassNode = parts[1];// 0 is the instance node
					
					if(parts.length > 1){// this is actually for making a mapping between main class nodes to the input nodes of the instances that can be used to generate ref links
						if(MainClassNodeToInstanceNodeList.containsKey(parts[1]))
						{
							MainClassNodeToInstanceNodeList.get(parts[1]).add(instanceNode);
						}
						else{
							ArrayList<String> tempListNodes = new ArrayList<String>();
							tempListNodes.add(instanceNode);
							MainClassNodeToInstanceNodeList.put(parts[1], tempListNodes);
						}
					}
				}
			}
			
			
			// assuming instances are defined in hugin oobn before potentials 
			////////////////// still need to consider an oobn without edges/potentials   /////////////////
			if(tempInstance != null){
				classNode.insertInstance(tempInstance);
//				mxCell = formCellFromInstance(tempInstance, tempInstance.name);
				formCellFromInstance(tempInstance, tempInstance.name);
			}
			
			if( ctx.bindings.inputBindings!= null && ctx.bindings.outputBindings != null){
				tempInstance = new Instance(ctx.instanceName.getText(), ctx.className.getText(), ctx.bindings.inputBindings.str + "; ", ctx.bindings.outputBindings.str);
			}
			else if(ctx.bindings.outputBindings == null){			
				tempInstance = new Instance(ctx.instanceName.getText(), ctx.className.getText(), ctx.bindings.inputBindings.str, "");
			}
			else if(ctx.bindings.inputBindings == null){
				tempInstance = new Instance(ctx.instanceName.getText(), ctx.className.getText(), "; ", ctx.bindings.outputBindings.str);
			}
			
//			classNode.insertInstance(tempInstance);// I have replaced this line with 2 segments to make things work (I mean attributes are now properly added to the nodes/instances)
			
			
			ArrayList<String> tempInstanceList;
			
			NPPCompiler.instanceclassMap.put(instanceName, className);// this mapping is instance name to class name mapping
			// and we need both for some faster processing
			if(NPPCompiler.classinstanceListMap.keySet().contains(className)){
				tempInstanceList = NPPCompiler.classinstanceListMap.get(className);
				tempInstanceList.add(instanceName);
				NPPCompiler.classinstanceListMap.put(className, tempInstanceList);
			}
			else{
				tempInstanceList = new ArrayList<String>();
				tempInstanceList.add(instanceName);
				NPPCompiler.classinstanceListMap.put(className, tempInstanceList);
			}
			
			String strTest = ctx.str;
			
			return visitChildren(ctx);
		}
		
		
		/* 2.	 potential ( E | L T  )
		{
			data=
			(
				(
					(0.5 0.5 )
					(0.5 0.5 )
				)
				(
					(0.5 0.5 )
					(0.5 0.5 )
				)
			);
		}
		 * 
		 		<mxCell edge="1" id="14" parent="1" source="4" style="" target="7" value="">
			         <mxGeometry as="geometry" relative="1">
			            <mxPoint as="sourcePoint" x="160.0" y="250.0" />
			            <mxPoint as="targetPoint" x="230.0" y="380.0" />
			         </mxGeometry>
		  		</mxCell>
		  		<mxCell edge="1" id="13" parent="1" source="5" style="" target="7" value="">
			         <mxGeometry as="geometry" relative="1">
			            <mxPoint as="sourcePoint" x="310.0" y="250.0" />
			            <mxPoint as="targetPoint" x="250.0" y="380.0" />
			         </mxGeometry>
		      	</mxCell>
		 */
		// potential : 'potential' '(' edgeInformation ')' '{' (potentialAttribute) '}';
		
		@Override public String visitPotential(  NETPlusPlusParser.PotentialContext ctx)  
		{ 
			// adding previous node attributes, but keep track that it should be done only once 
			// assuming that all potentials are defined after all nodes in oobn files 
			
			if(doneNodeAttribute == false){
				classNode.insertBasicNode(tempBasicNode);
				mxCell = formCellFromNode(tempBasicNode);
//				mxGraph.add(mxCell);
				mxGraph.put(mxCell.properties.id, mxCell);
//				System.out.println(mxCell);
				doneNodeAttribute = true;
			}
			
			// assuming instances are defined in hugin oobn before potentials
			if(doneInstanceAttribute == false && tempInstance != null){
				classNode.insertInstance(tempInstance);
//				mxCell = formCellFromInstance(tempInstance, tempInstance.name);
				formCellFromInstance(tempInstance, tempInstance.name);
				doneInstanceAttribute = true;
			}
			
			metaComponentIndex = 2;
			
			String str = ctx.edgeInformation.str;
			
			String potentialInfo []= str.split("[|]");
			String child = potentialInfo[0].trim();
			String parent = "";
			
			currentChildNodeLabelInPotential = child;
			
			if(potentialInfo.length>1)
				parent = potentialInfo[1];
			

			if(ctx.potentialAttribute != null && ctx.potentialAttribute.data != null){	
				tempPotential = new Potential(ctx.edgeInformation.str, child, parent, ctx.potentialAttribute.data.str);
//				System.err.println("The data " + ctx.potentialAttribute.data.str);
			}
			else tempPotential = new Potential(ctx.edgeInformation.str, child, parent, "");
			
			classNode.insertPotential(tempPotential);
			
			// here goes the code for ioobn conversion
			
			if(potentialInfo.length>1){
				ArrayList<String> parentNodesId = new ArrayList<String>();
//				System.out.println("parent info" + potentialInfo[1] + "child " + child);
				String tempParents [] = parent.trim().split(" ");
				
				
//				System.out.println(tempParents.length);
				for (int i = 0; i < tempParents.length; i++){
					
					mxCell = formCellFromPotential(child, tempParents[i]);
//					mxGraph.add(mxCell);
					mxGraph.put(mxCell.properties.id, mxCell);
				}
			}
			
			return visitChildren(ctx); 
		}
		
		public void formCellFromInstance(Instance tempInstance2, String instanceName) {
//			System.out.println("HIHIHIIH " + instanceName);
			String pos = tempInstance2.attributes.get("position");
			// (100 200) is splitted using the following 3 lines
			if(pos != null){
				String positions[] = pos.replace("(", "").replace(")", "").trim().split(" ");
				InstanceIdMapXCoord.put(instanceName, Integer.parseInt(positions[0]));
				InstanceIdMapYCoord.put(instanceName, Integer.parseInt(positions[1]));
			}
		}
		
		public mxCellStructure formCellFromPotential(String child, String parent) {
			id++;
//			System.out.println("child:" + child + "parent:" + parent);
			mxCellStructure tempMxCell = new mxCellStructure();
			
//			System.out.println(labelMapId + "\n" + IdMapXCoord + "\n" + IdMapYCoord);
			String childId = labelMapId.get(child); // target
			String parentId = labelMapId.get(parent); // source
			
			int srcXCoord = 0;
			int srcYCoord = 0;
			int tarXCoord = 0;
			int tarYCoord = 0;
			
			// assuming instance's' output nodes' can be parent of edges/potentials
			// since parent A_X is a node, name X, in instance A, parentId will be null
//			System.out.println(parent +" " + NPPCompiler.instanceclassMap);			
			
			if(IdMapXCoord.containsKey(parentId)) {// we assume if XCoord exist then Y will also exist
				srcXCoord = IdMapXCoord.get(parentId);
				srcYCoord = IdMapYCoord.get(parentId);
			}
			if(IdMapXCoord.containsKey(childId)){// we assume if XCoord exist then Y will also exist
				tarXCoord = IdMapXCoord.get(childId);
				tarYCoord = IdMapYCoord.get(childId);
			}
			
			tempMxCell.properties.edge = "1";
			tempMxCell.properties.id = Integer.toString(id);
			tempMxCell.properties.parentId = "1";// we have to put a correct value here
			tempMxCell.properties.style.shape = "";
			tempMxCell.properties.value = "";
			if(parentId != null)
				tempMxCell.properties.source = parentId;
			else tempMxCell.properties.source = parent;
			
			tempMxCell.properties.target = childId;
			
			tempMxCell.mxGeometry.as = "geometry";
			tempMxCell.mxGeometry.relative = "1";// it might change, must fix later
//			OuterClass.InnerClass innerObject = outerObject.new InnerClass();
			mxCellStructure.Point pointSrc = tempMxCell.new Point();
			pointSrc.as = "sourcePoint";
			pointSrc.xcoord = srcXCoord;
			pointSrc.ycoord = srcYCoord;
			
			mxCellStructure.Point pointTar = tempMxCell.new Point();
			pointTar.as = "targetPoint";
			pointTar.xcoord = tarXCoord;
			pointTar.ycoord = tarYCoord;
			
			tempMxCell.mxGeometry.mxPoints.add(pointSrc);
			tempMxCell.mxGeometry.mxPoints.add(pointTar);
			
			return tempMxCell;
		}

		// basicNode : 'node' nodeName '{' nodeAttribute* '}' | 'node' nodeType=('discrete' | 'continuous') nodeName '{' nodeAttribute* '}' | 'decision' nodeName '{' nodeAttribute* '}' | 'utility' nodeName '{' nodeAttribute* '}' ;
		@Override public String visitBasicNode(  NETPlusPlusParser.BasicNodeContext ctx)  
		{ 
			metaComponentIndex = 1;
			
			if(ctx.nodeType != null){
//				System.out.println("Hello");
				tempBasicNode = new BasicNode(ctx.nodeName.getText(), ctx.nodeType.getText() + " node");
			}
				
			else {
				// adding previous node attributes 
				if (tempBasicNode != null){
					classNode.insertBasicNode(tempBasicNode);
					mxCell = formCellFromNode(tempBasicNode);
//					mxGraph.add(mxCell);
					mxGraph.put(mxCell.properties.id, mxCell);
//					System.out.println(mxCell);
				}
				
				String tempStr = ctx.str;
				String typeStr;
				
				tempStr = tempStr.split(" ")[0];
//				System.out.println("Hello");
				if(tempStr.equalsIgnoreCase("node")){
					tempBasicNode = new BasicNode(ctx.nodeName.getText(), "discrete node");
				}
				else if(tempStr.equalsIgnoreCase("decision")) tempBasicNode = new BasicNode(ctx.nodeName.getText(), "decision");
				else if (tempStr.equalsIgnoreCase("utility")) tempBasicNode = new BasicNode(ctx.nodeName.getText(), "utility");
			}
//			System.out.println(ctx.nodeAttribute(3).getText());// 0 is for label, 1 for position, 2 for states
//			System.out.println(ctx.nodeAttribute(1).position.getText());
//			System.out.println(ctx.nodeAttribute(1).position.xCoordinate.getText());// 1 = position based on the oobn definition
			
			
			
			
			
			// here goes the code for ioobn conversion
			
			/* Translations:
				 * 1. 	        node V
								{
									label= "V";
									type="Discrete";
									subType="Boolean";
									states=("True" "False" );
								}
					 * 		<mxCell id="2" parent="1" style="ellipse;dashed=1" value="V" vertex="1">
				         		<mxGeometry as="geometry" height="55.0" width="80.0" x="120.0" y="90.0" />
				      		</mxCell>
				      		
			*/
//			mxCell = new mxCellStructure();
			
			
			
			return visitChildren(ctx); 
		}
		
		public boolean inInterfaceNodeList(String [] nodeList, String key)
		{
			for (String s: nodeList)
				if (s.equalsIgnoreCase(key))
					return true;
			return false;
		}
		
		public boolean lookInArray(String Key, String [] list)
		{
			for(String s: list)
			{
				if(s.equalsIgnoreCase(Key))
					return true;
			}
			return false;
		}
			
		public mxCellStructure formCellFromNode(BasicNode tempBasicNode2) 
		{
			id++;
			String strId = Integer.toString(id);
			labelMapId.put(tempBasicNode2.name, strId);
			
			mxCellStructure tempMxCell = new mxCellStructure();
			
			tempMxCell.properties.id = Integer.toString(id);
			tempMxCell.properties.parentId = "1";// we have to put a correct value here
			tempMxCell.properties.style.shape = "ellipse";// check the node, for discrete = ellipse, continuous = ?, utility = diamond, decision = rectangle
			tempMxCell.properties.kind = 0;// initially by default it is 0
			if(inInterfaceNodeList(inputNodeList, tempBasicNode2.name) == true){
				for(String in: inputNodeList)	System.out.print(in + " ");
				System.out.println("The input list is above: and we are looking for " + tempBasicNode2.name);
				tempMxCell.properties.style.gesture = "dashed=1";// we have to check the node label in the input/output list to define it
				tempMxCell.properties.kind = 1;
			}
			else if(inInterfaceNodeList(outputNodeList, tempBasicNode2.name) == true){
				for(String in: outputNodeList)	System.out.print(in + " ");
				System.out.println("The output list is above: and we are looking for " + tempBasicNode2.name);
				tempMxCell.properties.style.gesture = "strokeWidth=5";
				tempMxCell.properties.kind = 2;
			}
			
//			tempMxCell.properties.value = tempBasicNode2.attributes.get("label");
			tempMxCell.properties.value = tempBasicNode2.name;
			tempMxCell.properties.vertex = "1";
			tempMxCell.mxGeometry.as = "geometry";
			tempMxCell.mxGeometry.height = 55.0;
			tempMxCell.mxGeometry.width = 80.0;
			
			tempMxCell.properties.type = tempBasicNode2.type;
			if(tempBasicNode2.attributes.containsKey("subType"))
			tempMxCell.properties.sub_type = tempBasicNode2.attributes.get("subType").replace("\"", "").trim();
			tempMxCell.properties.name = tempBasicNode2.name;
			
//			tempMxCell.extraProperties.states = tempBasicNode2.attributes.get("states");
			String [] stateArray = null;
			if(tempBasicNode2.attributes.containsKey("states"))
				stateArray = tempBasicNode2.attributes.get("states").replace("(", "").replace(")", "").trim().split(" ");
	        
			if(stateArray != null)
	        for(String x: stateArray)
	        	tempMxCell.extraProperties.states.add(x.replace("\"", ""));
			
//	        if(lookInArray(tempBasicNode2.name, inputNodeList))
//	        	tempMxCell.properties.kind = 1;// EIO = 0, 1, 2 for Embedded, Input and Output
//	        else if(lookInArray(tempBasicNode2.name, outputNodeList))
//	        	tempMxCell.properties.kind = 2;// EIO = 0, 1, 2 for Embedded, Input and Output
	        
			String pos = tempBasicNode2.attributes.get("position");
			
//			System.out.println(tempBasicNode2.attributes);
			// (100 200) is splitted using the following 3 lines
			if(pos != null){
				String positions[] = pos.replace("(", "").replace(")", "").trim().split(" "); 
//				System.out.println(positions[0]);
				tempMxCell.mxGeometry.xcoord = Integer.parseInt(positions[0]);
				tempMxCell.mxGeometry.ycoord = Integer.parseInt(positions[1]);
				
				IdMapXCoord.put(strId, tempMxCell.mxGeometry.xcoord);
				IdMapYCoord.put(strId, tempMxCell.mxGeometry.ycoord);
			}
			
			return tempMxCell;
		}

		// label : 'label' '=' STRING ';';
		@Override public String visitLabel(  NETPlusPlusParser.LabelContext ctx)  
		{ 
			tempAttribute = new Attribute("label", ctx.STRING.getText());
			
			if(metaComponentIndex == 1) tempBasicNode.insertAttribute(tempAttribute);
			else if(metaComponentIndex == 3) tempInstance.insertAttribute(tempAttribute);
			
			return visitChildren(ctx); 
		}
				
		// position : 'position' '=' '(' xCoordinate yCoordinate ')' ';';
		@Override public String visitPosition(  NETPlusPlusParser.PositionContext ctx)  
		{ 
//			System.out.println("Hi");
			String str = ctx.str;
			
			tempAttribute = new Attribute("position", "( "+ ctx.xCoordinate.getText() + " " + ctx.yCoordinate.getText() + " )");
			
			if(metaComponentIndex == 1) tempBasicNode.insertAttribute(tempAttribute);
			else if(metaComponentIndex == 3) tempInstance.insertAttribute(tempAttribute);
			
			return visitChildren(ctx); 
		}
		
		//fileStructureDefn : classStructure | abstractClassStructure | interfaceStructure;
		@Override public String visitFileStructureDefn(  NETPlusPlusParser.FileStructureDefnContext ctx)  
		{ 	
			return visitChildren(ctx);
		}
		
		// classStructure: 'class' className parentClass parentInterfaces '{' (classElement )* '}'
		@Override public String visitClassStructure( NETPlusPlusParser.ClassStructureContext ctx) 
		{ 
			this.ParsedClass = ctx.className.getText();
			metaComponentIndex = 0; // this will be 0 for class, abstract class and interface. Since
									// for a signle file no two of them will occur simultaneously
			
			String className = ctx.className.getText();
			String strTest = ctx.str;
			
			classNode = new ClassDefn(className, 2);
			
			//		my plan is to update the components of classNode on the fly. If it does not work,
			//      then same type of global components can be created and later can be assigned here.
			
			return visitChildren(ctx); 
		}
		
		
		// abstractClassStructure: 'abstract' 'class' className parentClass parentInterfaces '{' (classElement )* '}'
		@Override public String visitAbstractClassStructure( NETPlusPlusParser.AbstractClassStructureContext ctx) 
		{ 
			metaComponentIndex = 0; // this will be 0 for class, abstract class and interface. Since
			// for a signle file no two of them will occur simultaneously

			String abstractClassName = ctx.className.getText();
			String strTest = ctx.str;
			
			classNode = new ClassDefn(abstractClassName, 1);
			
			return visitChildren(ctx); 
		}
		
		
		@Override public String visitInterfaceStructure( NETPlusPlusParser.InterfaceStructureContext ctx) 
		{ 
			metaComponentIndex = 0; // this will be 0 for class, abstract class and interface. Since
			// for a signle file no two of them will occur simultaneously

			String interfaceName = ctx.interfaceName.getText();
			String strTest = ctx.str;
			
			classNode = new ClassDefn(interfaceName, 0);
			
			return visitChildren(ctx); 
		}
		
		
		@Override public String visitInterfaceNameList( NETPlusPlusParser.InterfaceNameListContext ctx) 
		{ 
			return visitChildren(ctx); 
		}
		
		
		@Override public String visitInterfaceElement( NETPlusPlusParser.InterfaceElementContext ctx) 
		{ 
			return visitChildren(ctx); 
		}
		
		// parentClass : 'extends' superClassName
		@Override public String visitParentClass( NETPlusPlusParser.ParentClassContext ctx) 
		{ 
			if(ctx.superClassName != null)
				parentClassList = ctx.superClassName().getText();
			
			return visitChildren(ctx); 
		}
		
		// parentClass : 'implements' interfaceNameList
		@Override public String visitParentInterfaces( NETPlusPlusParser.ParentInterfacesContext ctx) 
		{ 
			if(ctx.interfaceNameList != null)
				parentInterfaceList = ctx.interfaceNameList.getText();
			return visitChildren(ctx); 
		}
		
		// stateValues : 'state_values' '=' '(' NUMBER* ')' ';';
		@Override public String visitStateValues(  NETPlusPlusParser.StateValuesContext ctx)  
		{ 
			String str = ctx.str;
			
			String statesValues = ctx.NUMBER().toString();
			statesValues = "( " + statesValues.substring(1,  statesValues.length()-1) + " )";
			
			statesValues = statesValues.replaceAll("[,]", "");
			
			tempAttribute = new Attribute("state_values", statesValues);
			
			if(metaComponentIndex == 1) tempBasicNode.insertAttribute(tempAttribute);
			else if(metaComponentIndex == 3) tempInstance.insertAttribute(tempAttribute);
			
			return visitChildren(ctx); 
		}
		
		// subtype : 'subtype' '=' 'boolean' ';' | 'subtype' '=' 'label' ';' | 'subtype' '=' 'number' ';' stateValues | 'subtype' '=' 'interval' ';' stateValues ;
		@Override public String visitSubtype(  NETPlusPlusParser.SubtypeContext ctx)  
		{ 
			String str = ctx.str;
			
			str = str.split(" ")[2];
			
			tempAttribute = new Attribute("subtype", str);
			
			if(metaComponentIndex == 1) tempBasicNode.insertAttribute(tempAttribute);
			else if(metaComponentIndex == 3) tempInstance.insertAttribute(tempAttribute);
			
			return visitChildren(ctx); 
		}
	
	// nodeName : IDENTIFIER ;
	@Override public String visitNodeName(  NETPlusPlusParser.NodeNameContext ctx) 
	{ 
		return visitChildren(ctx); 
	}
	
	// formalName : IDENTIFIER;
	@Override public String visitFormalName(  NETPlusPlusParser.FormalNameContext ctx) 
	{ 
		return visitChildren(ctx); 
	}
	
	// data : 'data' '=' '(' tuple ')' ';';
	@Override public String visitData(  NETPlusPlusParser.DataContext ctx)  
	{  
		String str = ctx.str;
		
		// it is kind of attribute for potential. So it data insertion can also be managed from here
		return visitChildren(ctx); 
	}
	
	@Override public String visitInstanceName(  NETPlusPlusParser.InstanceNameContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitFunction_call(  NETPlusPlusParser.Function_callContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	// bindings : '(' ')' | '(' inputBindings ')' | '(' inputBindings ';' outputBindings')' | '(' ';' outputBindings')'	 ;
	@Override public String visitBindings(  NETPlusPlusParser.BindingsContext ctx)  
	{ 
//		System.out.println(ctx.inputBindings.getText());
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitClassName(  NETPlusPlusParser.ClassNameContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	// attribValue : STRING |	NUMBER | '(' NUMBER+ ')' | '(' IDENTIFIER+')' | '(' ')'  ;
	@Override public String visitAttribValue(  NETPlusPlusParser.AttribValueContext ctx)  
	{ 
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}
	
	// domainElement : basicNode |   potential ;
	@Override public String visitDomainElement(  NETPlusPlusParser.DomainElementContext ctx)  
	{ 
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}

	@Override public String visitLiteral(  NETPlusPlusParser.LiteralContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitActualName(  NETPlusPlusParser.ActualNameContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	// tuple : (NUMBER)+ | (tuple1)+  ;
	@Override public String visitTuple(  NETPlusPlusParser.TupleContext ctx)  
	{ 
		if(ctx.NUMBER != null){
//			System.err.println(ctx.NUMBER()); // List of Terminal Node
			
			// assuming that nodes are added in mxGraph before potentials and their ids are already present in 
			String dataTuple = ctx.NUMBER().toString().replace("[","").replace("]", "");
			String nodeId = labelMapId.get(currentChildNodeLabelInPotential);
			if(mxGraph.containsKey(nodeId))	mxGraph.get(nodeId).tuples.add(dataTuple);
			else {
				System.err.println("The node ID " + nodeId + " is not found");
			}
		}
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}
	
	// tuple1 : '(' tuple ')' ;
	@Override public String visitTuple1(  NETPlusPlusParser.Tuple1Context ctx)  
	{ 
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}
	
	@Override public String visitYCoordinate(  NETPlusPlusParser.YCoordinateContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitSumExpr(  NETPlusPlusParser.SumExprContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitProductExpr(  NETPlusPlusParser.ProductExprContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	// potentialAttribute : data |	modelAttributes ;
	@Override public String visitPotentialAttribute(  NETPlusPlusParser.PotentialAttributeContext ctx)  
	{
		metaComponentIndex = 2;
		
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitParameter(  NETPlusPlusParser.ParameterContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitExpr(  NETPlusPlusParser.ExprContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitStmt1(  NETPlusPlusParser.Stmt1Context ctx)  
	{ 
		
		return visitChildren(ctx); 
	}
	
	// classElement : domainElement |  attribute+ |  classInstance ;
	@Override public String visitClassElement(  NETPlusPlusParser.ClassElementContext ctx)  
	{ 
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}

	
	// nodeAttribute : 	state |	label |   position |	attribute |   subtype ;
	@Override public String visitNodeAttribute(  NETPlusPlusParser.NodeAttributeContext ctx) 
	{ 
		metaComponentIndex = 1;
		return visitChildren(ctx); 
	}
	
	// instanceAttribute : 	label |   position |	attribute ;
	@Override public String visitInstanceAttribute(  NETPlusPlusParser.InstanceAttributeContext ctx) 
	{ 
		metaComponentIndex = 3;
		
		return visitChildren(ctx); 
	}
	
	// childNodes : IDENTIFIER+;
	@Override public String visitChildNodes(  NETPlusPlusParser.ChildNodesContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitProg(  NETPlusPlusParser.ProgContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	// edgeInformation : (childNodes) | (childNodes '|' parentNodes) ;
	@Override public String visitEdgeInformation(  NETPlusPlusParser.EdgeInformationContext ctx)  
	{ 
		
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitAttribName(  NETPlusPlusParser.AttribNameContext ctx)  
	{ 
		
		return visitChildren(ctx); 
	}
	
	// inputBindings : inputBinding (',' inputBinding )*			
	@Override public String visitInputBindings(  NETPlusPlusParser.InputBindingsContext ctx)  
	{ 
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}
	
	// inputBinding : formalName '=' actualName ;
		@Override public String visitInputBinding(  NETPlusPlusParser.InputBindingContext ctx)  
		{ 
			String str = ctx.str;
			
			return visitChildren(ctx); 
		}
	
	
	@Override public String visitXCoordinate(  NETPlusPlusParser.XCoordinateContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	// parentNodes : IDENTIFIER+;
	@Override public String visitParentNodes(  NETPlusPlusParser.ParentNodesContext ctx)  
	{ 
		
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitFormula(  NETPlusPlusParser.FormulaContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitBoolExpr(  NETPlusPlusParser.BoolExprContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitParameters(  NETPlusPlusParser.ParametersContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	
	@Override public String visitStmt(  NETPlusPlusParser.StmtContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	// outputBindings : outputBinding (',' outputBinding )*
	@Override public String visitOutputBindings(  NETPlusPlusParser.OutputBindingsContext ctx)  
	{ 
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}
	
	// outputBinding : actualName '=' formalName ;
	@Override public String visitOutputBinding(  NETPlusPlusParser.OutputBindingContext ctx)  
	{ 
		String str = ctx.str;
		
		return visitChildren(ctx); 
	}
	
	@Override public String visitPrimary(  NETPlusPlusParser.PrimaryContext ctx)  
	{ 
		return visitChildren(ctx); 
	}
	
	@Override
	public String toString(){
		int numOfTab = 1;
		classNode.printCode(numOfTab);
		return classNode.toString();
	}
	
}


/*
 Each class has a unique name, so make a map from class/interface/abs_class name to its components
  component analysis of a class:
  class consists of some elements 
  element are all string convertible or store-able in a string
  Make a hash map from string to string (component name to component value i.e representing string)
  Component names could be overlapping, so name should be unique to avoid map collision
  
  Class components are: [domain element, attributes, class instances]
  Each domain element : [a list of basic nodes, a list of potentials]
  						basic node [unique name, type, nodeAttributes => same as class instance]
  							type [discrete node, continuous node, decision, utility => any one, convertible to string]
  							
  						potential [edge information, potentialAttribute]
  							edge: [child nodes, parent nodes]
  								child nodes : nullable (might be empty) list of IDs convertible to string
  								parent nodes : nullable (might be empty) list of IDs convertible to string
  							potentialAttribute:   [1 (or more is supported) data, attributes (a set of attributes) same as instance and basic nodes]
  								data: tuple : may be convertible to a single string
  								modelAttrib: [either stmt1, list of identifiers]
  
  Each domain attribute
  
  Each domain class instance: [unique names, bindings, instance attributes]
  							unique names
  							bindings
  									input bindings = might be an empty string
  									output bindings = might be an empty string
  							instance attributes: [might be more than one. and I think each needs to have separate access. hence an array needs to be prepared for this]
  												state = collection of strings -> 1 string possible
  												label = a string
  												position = 2 numbers -> 1 string possible
  												attribute [name, value]
  														name [uniq for the instance]
  														value [string, list of number, list of ID or empty to be convertible to string]
  												subtype [type value like boolean/label, number/interval]
  														stateValues: list of numbers, to be converted to string
  												
  	Some global strings can track class name, instance name, node name and etc while 
  	updating elements/components during traversing the tree. and at the end of such traversal, 
  	these can be used to store in the corresponding mapping entry
  
  For displaying:
  
  a file starts with class with no space
  { and } with no space
  in the class there are some nodes and/or instances and/or potentials
  Their definition starts with 1 tab
  
  need to take care about data only. May be 2 tabs
  
  
 * */
 