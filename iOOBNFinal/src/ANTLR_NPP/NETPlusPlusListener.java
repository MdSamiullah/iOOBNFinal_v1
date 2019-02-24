package ANTLR_NPP;

// Generated from NETPlusPlus.g4 by ANTLR 4.4

	//package target.antlr4;
	//System.out.println("Grammar started");
	// what ever you will write here will be placed at the top of the generated files like lexer, visitor, parser

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link NETPlusPlusParser}.
 */
public interface NETPlusPlusListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#interfaceStructure}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceStructure(@NotNull NETPlusPlusParser.InterfaceStructureContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#interfaceStructure}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceStructure(@NotNull NETPlusPlusParser.InterfaceStructureContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#data}.
	 * @param ctx the parse tree
	 */
	void enterData(@NotNull NETPlusPlusParser.DataContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#data}.
	 * @param ctx the parse tree
	 */
	void exitData(@NotNull NETPlusPlusParser.DataContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#function_call}.
	 * @param ctx the parse tree
	 */
	void enterFunction_call(@NotNull NETPlusPlusParser.Function_callContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#function_call}.
	 * @param ctx the parse tree
	 */
	void exitFunction_call(@NotNull NETPlusPlusParser.Function_callContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#bindings}.
	 * @param ctx the parse tree
	 */
	void enterBindings(@NotNull NETPlusPlusParser.BindingsContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#bindings}.
	 * @param ctx the parse tree
	 */
	void exitBindings(@NotNull NETPlusPlusParser.BindingsContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#stateValues}.
	 * @param ctx the parse tree
	 */
	void enterStateValues(@NotNull NETPlusPlusParser.StateValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#stateValues}.
	 * @param ctx the parse tree
	 */
	void exitStateValues(@NotNull NETPlusPlusParser.StateValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#inputBinding}.
	 * @param ctx the parse tree
	 */
	void enterInputBinding(@NotNull NETPlusPlusParser.InputBindingContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#inputBinding}.
	 * @param ctx the parse tree
	 */
	void exitInputBinding(@NotNull NETPlusPlusParser.InputBindingContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#attribValue}.
	 * @param ctx the parse tree
	 */
	void enterAttribValue(@NotNull NETPlusPlusParser.AttribValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#attribValue}.
	 * @param ctx the parse tree
	 */
	void exitAttribValue(@NotNull NETPlusPlusParser.AttribValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#instanceAttribute}.
	 * @param ctx the parse tree
	 */
	void enterInstanceAttribute(@NotNull NETPlusPlusParser.InstanceAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#instanceAttribute}.
	 * @param ctx the parse tree
	 */
	void exitInstanceAttribute(@NotNull NETPlusPlusParser.InstanceAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#actualName}.
	 * @param ctx the parse tree
	 */
	void enterActualName(@NotNull NETPlusPlusParser.ActualNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#actualName}.
	 * @param ctx the parse tree
	 */
	void exitActualName(@NotNull NETPlusPlusParser.ActualNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#outputBinding}.
	 * @param ctx the parse tree
	 */
	void enterOutputBinding(@NotNull NETPlusPlusParser.OutputBindingContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#outputBinding}.
	 * @param ctx the parse tree
	 */
	void exitOutputBinding(@NotNull NETPlusPlusParser.OutputBindingContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#yCoordinate}.
	 * @param ctx the parse tree
	 */
	void enterYCoordinate(@NotNull NETPlusPlusParser.YCoordinateContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#yCoordinate}.
	 * @param ctx the parse tree
	 */
	void exitYCoordinate(@NotNull NETPlusPlusParser.YCoordinateContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#productExpr}.
	 * @param ctx the parse tree
	 */
	void enterProductExpr(@NotNull NETPlusPlusParser.ProductExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#productExpr}.
	 * @param ctx the parse tree
	 */
	void exitProductExpr(@NotNull NETPlusPlusParser.ProductExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#potentialAttribute}.
	 * @param ctx the parse tree
	 */
	void enterPotentialAttribute(@NotNull NETPlusPlusParser.PotentialAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#potentialAttribute}.
	 * @param ctx the parse tree
	 */
	void exitPotentialAttribute(@NotNull NETPlusPlusParser.PotentialAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#parentClass}.
	 * @param ctx the parse tree
	 */
	void enterParentClass(@NotNull NETPlusPlusParser.ParentClassContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#parentClass}.
	 * @param ctx the parse tree
	 */
	void exitParentClass(@NotNull NETPlusPlusParser.ParentClassContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#state}.
	 * @param ctx the parse tree
	 */
	void enterState(@NotNull NETPlusPlusParser.StateContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#state}.
	 * @param ctx the parse tree
	 */
	void exitState(@NotNull NETPlusPlusParser.StateContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#potential}.
	 * @param ctx the parse tree
	 */
	void enterPotential(@NotNull NETPlusPlusParser.PotentialContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#potential}.
	 * @param ctx the parse tree
	 */
	void exitPotential(@NotNull NETPlusPlusParser.PotentialContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#interfaceNameList}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceNameList(@NotNull NETPlusPlusParser.InterfaceNameListContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#interfaceNameList}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceNameList(@NotNull NETPlusPlusParser.InterfaceNameListContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#childNodes}.
	 * @param ctx the parse tree
	 */
	void enterChildNodes(@NotNull NETPlusPlusParser.ChildNodesContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#childNodes}.
	 * @param ctx the parse tree
	 */
	void exitChildNodes(@NotNull NETPlusPlusParser.ChildNodesContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#abstractClassStructure}.
	 * @param ctx the parse tree
	 */
	void enterAbstractClassStructure(@NotNull NETPlusPlusParser.AbstractClassStructureContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#abstractClassStructure}.
	 * @param ctx the parse tree
	 */
	void exitAbstractClassStructure(@NotNull NETPlusPlusParser.AbstractClassStructureContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#parentNodes}.
	 * @param ctx the parse tree
	 */
	void enterParentNodes(@NotNull NETPlusPlusParser.ParentNodesContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#parentNodes}.
	 * @param ctx the parse tree
	 */
	void exitParentNodes(@NotNull NETPlusPlusParser.ParentNodesContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#position}.
	 * @param ctx the parse tree
	 */
	void enterPosition(@NotNull NETPlusPlusParser.PositionContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#position}.
	 * @param ctx the parse tree
	 */
	void exitPosition(@NotNull NETPlusPlusParser.PositionContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(@NotNull NETPlusPlusParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(@NotNull NETPlusPlusParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#outputBindings}.
	 * @param ctx the parse tree
	 */
	void enterOutputBindings(@NotNull NETPlusPlusParser.OutputBindingsContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#outputBindings}.
	 * @param ctx the parse tree
	 */
	void exitOutputBindings(@NotNull NETPlusPlusParser.OutputBindingsContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(@NotNull NETPlusPlusParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(@NotNull NETPlusPlusParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#nodeName}.
	 * @param ctx the parse tree
	 */
	void enterNodeName(@NotNull NETPlusPlusParser.NodeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#nodeName}.
	 * @param ctx the parse tree
	 */
	void exitNodeName(@NotNull NETPlusPlusParser.NodeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#formalName}.
	 * @param ctx the parse tree
	 */
	void enterFormalName(@NotNull NETPlusPlusParser.FormalNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#formalName}.
	 * @param ctx the parse tree
	 */
	void exitFormalName(@NotNull NETPlusPlusParser.FormalNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#instanceName}.
	 * @param ctx the parse tree
	 */
	void enterInstanceName(@NotNull NETPlusPlusParser.InstanceNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#instanceName}.
	 * @param ctx the parse tree
	 */
	void exitInstanceName(@NotNull NETPlusPlusParser.InstanceNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#interfaceElement}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceElement(@NotNull NETPlusPlusParser.InterfaceElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#interfaceElement}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceElement(@NotNull NETPlusPlusParser.InterfaceElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#className}.
	 * @param ctx the parse tree
	 */
	void enterClassName(@NotNull NETPlusPlusParser.ClassNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#className}.
	 * @param ctx the parse tree
	 */
	void exitClassName(@NotNull NETPlusPlusParser.ClassNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#parentInterfaces}.
	 * @param ctx the parse tree
	 */
	void enterParentInterfaces(@NotNull NETPlusPlusParser.ParentInterfacesContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#parentInterfaces}.
	 * @param ctx the parse tree
	 */
	void exitParentInterfaces(@NotNull NETPlusPlusParser.ParentInterfacesContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#domainElement}.
	 * @param ctx the parse tree
	 */
	void enterDomainElement(@NotNull NETPlusPlusParser.DomainElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#domainElement}.
	 * @param ctx the parse tree
	 */
	void exitDomainElement(@NotNull NETPlusPlusParser.DomainElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(@NotNull NETPlusPlusParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(@NotNull NETPlusPlusParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#tuple}.
	 * @param ctx the parse tree
	 */
	void enterTuple(@NotNull NETPlusPlusParser.TupleContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#tuple}.
	 * @param ctx the parse tree
	 */
	void exitTuple(@NotNull NETPlusPlusParser.TupleContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#sumExpr}.
	 * @param ctx the parse tree
	 */
	void enterSumExpr(@NotNull NETPlusPlusParser.SumExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#sumExpr}.
	 * @param ctx the parse tree
	 */
	void exitSumExpr(@NotNull NETPlusPlusParser.SumExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#subtype}.
	 * @param ctx the parse tree
	 */
	void enterSubtype(@NotNull NETPlusPlusParser.SubtypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#subtype}.
	 * @param ctx the parse tree
	 */
	void exitSubtype(@NotNull NETPlusPlusParser.SubtypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(@NotNull NETPlusPlusParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(@NotNull NETPlusPlusParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(@NotNull NETPlusPlusParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(@NotNull NETPlusPlusParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#interfaceName}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceName(@NotNull NETPlusPlusParser.InterfaceNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#interfaceName}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceName(@NotNull NETPlusPlusParser.InterfaceNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(@NotNull NETPlusPlusParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(@NotNull NETPlusPlusParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#classInstance}.
	 * @param ctx the parse tree
	 */
	void enterClassInstance(@NotNull NETPlusPlusParser.ClassInstanceContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#classInstance}.
	 * @param ctx the parse tree
	 */
	void exitClassInstance(@NotNull NETPlusPlusParser.ClassInstanceContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#stmt1}.
	 * @param ctx the parse tree
	 */
	void enterStmt1(@NotNull NETPlusPlusParser.Stmt1Context ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#stmt1}.
	 * @param ctx the parse tree
	 */
	void exitStmt1(@NotNull NETPlusPlusParser.Stmt1Context ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#fileStructureDefn}.
	 * @param ctx the parse tree
	 */
	void enterFileStructureDefn(@NotNull NETPlusPlusParser.FileStructureDefnContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#fileStructureDefn}.
	 * @param ctx the parse tree
	 */
	void exitFileStructureDefn(@NotNull NETPlusPlusParser.FileStructureDefnContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#classElement}.
	 * @param ctx the parse tree
	 */
	void enterClassElement(@NotNull NETPlusPlusParser.ClassElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#classElement}.
	 * @param ctx the parse tree
	 */
	void exitClassElement(@NotNull NETPlusPlusParser.ClassElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#basicNode}.
	 * @param ctx the parse tree
	 */
	void enterBasicNode(@NotNull NETPlusPlusParser.BasicNodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#basicNode}.
	 * @param ctx the parse tree
	 */
	void exitBasicNode(@NotNull NETPlusPlusParser.BasicNodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#nodeAttribute}.
	 * @param ctx the parse tree
	 */
	void enterNodeAttribute(@NotNull NETPlusPlusParser.NodeAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#nodeAttribute}.
	 * @param ctx the parse tree
	 */
	void exitNodeAttribute(@NotNull NETPlusPlusParser.NodeAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(@NotNull NETPlusPlusParser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(@NotNull NETPlusPlusParser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(@NotNull NETPlusPlusParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(@NotNull NETPlusPlusParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#superClassName}.
	 * @param ctx the parse tree
	 */
	void enterSuperClassName(@NotNull NETPlusPlusParser.SuperClassNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#superClassName}.
	 * @param ctx the parse tree
	 */
	void exitSuperClassName(@NotNull NETPlusPlusParser.SuperClassNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#edgeInformation}.
	 * @param ctx the parse tree
	 */
	void enterEdgeInformation(@NotNull NETPlusPlusParser.EdgeInformationContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#edgeInformation}.
	 * @param ctx the parse tree
	 */
	void exitEdgeInformation(@NotNull NETPlusPlusParser.EdgeInformationContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#attribName}.
	 * @param ctx the parse tree
	 */
	void enterAttribName(@NotNull NETPlusPlusParser.AttribNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#attribName}.
	 * @param ctx the parse tree
	 */
	void exitAttribName(@NotNull NETPlusPlusParser.AttribNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#inputBindings}.
	 * @param ctx the parse tree
	 */
	void enterInputBindings(@NotNull NETPlusPlusParser.InputBindingsContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#inputBindings}.
	 * @param ctx the parse tree
	 */
	void exitInputBindings(@NotNull NETPlusPlusParser.InputBindingsContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#xCoordinate}.
	 * @param ctx the parse tree
	 */
	void enterXCoordinate(@NotNull NETPlusPlusParser.XCoordinateContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#xCoordinate}.
	 * @param ctx the parse tree
	 */
	void exitXCoordinate(@NotNull NETPlusPlusParser.XCoordinateContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormula(@NotNull NETPlusPlusParser.FormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormula(@NotNull NETPlusPlusParser.FormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterBoolExpr(@NotNull NETPlusPlusParser.BoolExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitBoolExpr(@NotNull NETPlusPlusParser.BoolExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#tuple1}.
	 * @param ctx the parse tree
	 */
	void enterTuple1(@NotNull NETPlusPlusParser.Tuple1Context ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#tuple1}.
	 * @param ctx the parse tree
	 */
	void exitTuple1(@NotNull NETPlusPlusParser.Tuple1Context ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(@NotNull NETPlusPlusParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(@NotNull NETPlusPlusParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link NETPlusPlusParser#classStructure}.
	 * @param ctx the parse tree
	 */
	void enterClassStructure(@NotNull NETPlusPlusParser.ClassStructureContext ctx);
	/**
	 * Exit a parse tree produced by {@link NETPlusPlusParser#classStructure}.
	 * @param ctx the parse tree
	 */
	void exitClassStructure(@NotNull NETPlusPlusParser.ClassStructureContext ctx);
}