package ANTLR_NPP;

// Generated from NETPlusPlus.g4 by ANTLR 4.4

	//package target.antlr4;
	//System.out.println("Grammar started");
	// what ever you will write here will be placed at the top of the generated files like lexer, visitor, parser

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link NETPlusPlusParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface NETPlusPlusVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#interfaceStructure}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceStructure(@NotNull NETPlusPlusParser.InterfaceStructureContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#data}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData(@NotNull NETPlusPlusParser.DataContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_call(@NotNull NETPlusPlusParser.Function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#bindings}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBindings(@NotNull NETPlusPlusParser.BindingsContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#stateValues}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateValues(@NotNull NETPlusPlusParser.StateValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#inputBinding}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputBinding(@NotNull NETPlusPlusParser.InputBindingContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#attribValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribValue(@NotNull NETPlusPlusParser.AttribValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#instanceAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstanceAttribute(@NotNull NETPlusPlusParser.InstanceAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#actualName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActualName(@NotNull NETPlusPlusParser.ActualNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#outputBinding}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputBinding(@NotNull NETPlusPlusParser.OutputBindingContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#yCoordinate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitYCoordinate(@NotNull NETPlusPlusParser.YCoordinateContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#productExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProductExpr(@NotNull NETPlusPlusParser.ProductExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#potentialAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPotentialAttribute(@NotNull NETPlusPlusParser.PotentialAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#parentClass}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentClass(@NotNull NETPlusPlusParser.ParentClassContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#state}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitState(@NotNull NETPlusPlusParser.StateContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#potential}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPotential(@NotNull NETPlusPlusParser.PotentialContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#interfaceNameList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceNameList(@NotNull NETPlusPlusParser.InterfaceNameListContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#childNodes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChildNodes(@NotNull NETPlusPlusParser.ChildNodesContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#abstractClassStructure}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAbstractClassStructure(@NotNull NETPlusPlusParser.AbstractClassStructureContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#parentNodes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentNodes(@NotNull NETPlusPlusParser.ParentNodesContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#position}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPosition(@NotNull NETPlusPlusParser.PositionContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(@NotNull NETPlusPlusParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#outputBindings}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputBindings(@NotNull NETPlusPlusParser.OutputBindingsContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(@NotNull NETPlusPlusParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#nodeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNodeName(@NotNull NETPlusPlusParser.NodeNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#formalName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalName(@NotNull NETPlusPlusParser.FormalNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#instanceName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstanceName(@NotNull NETPlusPlusParser.InstanceNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#interfaceElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceElement(@NotNull NETPlusPlusParser.InterfaceElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#className}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassName(@NotNull NETPlusPlusParser.ClassNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#parentInterfaces}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentInterfaces(@NotNull NETPlusPlusParser.ParentInterfacesContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#domainElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDomainElement(@NotNull NETPlusPlusParser.DomainElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(@NotNull NETPlusPlusParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#tuple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTuple(@NotNull NETPlusPlusParser.TupleContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#sumExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSumExpr(@NotNull NETPlusPlusParser.SumExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#subtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubtype(@NotNull NETPlusPlusParser.SubtypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(@NotNull NETPlusPlusParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(@NotNull NETPlusPlusParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#interfaceName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceName(@NotNull NETPlusPlusParser.InterfaceNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute(@NotNull NETPlusPlusParser.AttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#classInstance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassInstance(@NotNull NETPlusPlusParser.ClassInstanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#stmt1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt1(@NotNull NETPlusPlusParser.Stmt1Context ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#fileStructureDefn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFileStructureDefn(@NotNull NETPlusPlusParser.FileStructureDefnContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#classElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassElement(@NotNull NETPlusPlusParser.ClassElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#basicNode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasicNode(@NotNull NETPlusPlusParser.BasicNodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#nodeAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNodeAttribute(@NotNull NETPlusPlusParser.NodeAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(@NotNull NETPlusPlusParser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(@NotNull NETPlusPlusParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#superClassName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperClassName(@NotNull NETPlusPlusParser.SuperClassNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#edgeInformation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdgeInformation(@NotNull NETPlusPlusParser.EdgeInformationContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#attribName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribName(@NotNull NETPlusPlusParser.AttribNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#inputBindings}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputBindings(@NotNull NETPlusPlusParser.InputBindingsContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#xCoordinate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXCoordinate(@NotNull NETPlusPlusParser.XCoordinateContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormula(@NotNull NETPlusPlusParser.FormulaContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolExpr(@NotNull NETPlusPlusParser.BoolExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#tuple1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTuple1(@NotNull NETPlusPlusParser.Tuple1Context ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(@NotNull NETPlusPlusParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link NETPlusPlusParser#classStructure}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassStructure(@NotNull NETPlusPlusParser.ClassStructureContext ctx);
}