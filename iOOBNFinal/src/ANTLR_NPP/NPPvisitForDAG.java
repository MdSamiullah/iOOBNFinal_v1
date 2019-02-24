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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.v4.runtime.misc.NotNull;

import com.mxgraph.util.mxPoint;

import ANTLR_NPP.mxCellStructure.Point;
import learning.LearningiOOBN;
	
public class NPPvisitForDAG extends NETPlusPlusBaseVisitor<String> {
	
	public static HashMap<String, HashSet<String>> dagMAP = new HashMap<String, HashSet<String>>(); // parent node to a list of child nodes 
	
	// state : 'states' '=' '(' STRING* ')' ';';
		@Override public String visitState(  NETPlusPlusParser.StateContext ctx)  
		{ 
			String str = ctx.str;
			
			return visitChildren(ctx); 
		}
		
		// attribute : attribName '=' attribValue ';' ;
		@Override public String visitAttribute(  NETPlusPlusParser.AttributeContext ctx)  
		{ 
			
			return visitChildren(ctx); 
		}  
		
		// classInstance : 'instance' instanceName ':' className bindings '{' instanceAttributes '}';
		@Override public String visitClassInstance(  NETPlusPlusParser.ClassInstanceContext ctx)  
		{
				
			return visitChildren(ctx);
		}
		
		
		// potential : 'potential' '(' edgeInformation ')' '{' (potentialAttribute) '}';
		
		@Override public String visitPotential(  NETPlusPlusParser.PotentialContext ctx)  
		{ 
			String str = ctx.edgeInformation.str;
			
			String potentialInfo []= str.split("[|]");
			String child = potentialInfo[0].trim();
			String parent = "";
			String parents [] = null;
			
			if(potentialInfo.length>1){
				parent = potentialInfo[1];
				parents = parent.trim().split(" ");
			}
			
//			System.out.println("child "+ child + " parents "+ parent);
			
			if(parents != null){// if the potential is of a child from 1/more parents 
				
				for(String par : parents){
					if(dagMAP.containsKey(par)){
						HashSet<String> children = dagMAP.get(par); 
						children.add(child);
						dagMAP.put(par, children);
					}
					else{
						HashSet<String> children = new HashSet<String>();
						children.add(child);
						dagMAP.put(par, children);
					}
				}
				HashSet<String> childrenSet = new HashSet<String>();
				dagMAP.put(child, childrenSet);// child node is added in the map with no children
			}
			else{// no parent info is given in the potential, may be it is an isolated node or a node with no parent, e.g. root node/input node
				if(!dagMAP.containsKey(child)){
					HashSet<String> childrenSet = new HashSet<String>();
					dagMAP.put(child, childrenSet);// child node is added in the map with no children
				}
			}

			// here goes the code for ioobn conversion
			
			
		
			return visitChildren(ctx); 
		}

		// basicNode : 'node' nodeName '{' nodeAttribute* '}' | 'node' nodeType=('discrete' | 'continuous') nodeName '{' nodeAttribute* '}' | 'decision' nodeName '{' nodeAttribute* '}' | 'utility' nodeName '{' nodeAttribute* '}' ;
		@Override public String visitBasicNode(  NETPlusPlusParser.BasicNodeContext ctx)  
		{ 
			return visitChildren(ctx);
		}

		// label : 'label' '=' STRING ';';
		@Override public String visitLabel(  NETPlusPlusParser.LabelContext ctx)  
		{ 
			return visitChildren(ctx);
		}
				
		// position : 'position' '=' '(' xCoordinate yCoordinate ')' ';';
		@Override public String visitPosition(  NETPlusPlusParser.PositionContext ctx)  
		{ 
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
			return visitChildren(ctx);
		}
		
		
		// abstractClassStructure: 'abstract' 'class' className parentClass parentInterfaces '{' (classElement )* '}'
		@Override public String visitAbstractClassStructure( NETPlusPlusParser.AbstractClassStructureContext ctx) 
		{ 
			return visitChildren(ctx);
		}
		
		
		@Override public String visitInterfaceStructure( NETPlusPlusParser.InterfaceStructureContext ctx) 
		{
			
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
			
			return visitChildren(ctx); 
		}
		
		// parentClass : 'implements' interfaceNameList
		@Override public String visitParentInterfaces( NETPlusPlusParser.ParentInterfacesContext ctx) 
		{ 
			return visitChildren(ctx); 
		}
		
		// stateValues : 'state_values' '=' '(' NUMBER* ')' ';';
		@Override public String visitStateValues(  NETPlusPlusParser.StateValuesContext ctx)  
		{ 
			String str = ctx.str;
			
			return visitChildren(ctx); 
		}
		
		// subtype : 'subtype' '=' 'boolean' ';' | 'subtype' '=' 'label' ';' | 'subtype' '=' 'number' ';' stateValues | 'subtype' '=' 'interval' ';' stateValues ;
		@Override public String visitSubtype(  NETPlusPlusParser.SubtypeContext ctx)  
		{ 
			String str = ctx.str;
			
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
		return visitChildren(ctx); 
	}
	
	// instanceAttribute : 	label |   position |	attribute ;
	@Override public String visitInstanceAttribute(  NETPlusPlusParser.InstanceAttributeContext ctx) 
	{ 
		
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
		return "";
	}
	
}

 