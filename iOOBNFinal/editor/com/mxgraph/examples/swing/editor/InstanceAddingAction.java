package com.mxgraph.examples.swing.editor;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.view.mxGraph;

/**
 * 
 */
public class InstanceAddingAction extends AbstractAction
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String className;
	public String instanceName;
	public static int instanceCount = 1;
	public mxGraph graph;
	public Object[] newCells;
	
	/**
	 * 
	 */
//	private static final long serialVersionUID = -4718086600089409092L;

	/**
	 * 
	 * @param name
	 */
	public InstanceAddingAction(String name)
	{
		super(name);
		this.className = "concrete.class";
		this.instanceName = "";
		this.newCells = new Object[1];
	}
	
	/**
	 * 
	 * @param name
	 * @param fileName
	 */
	public InstanceAddingAction(String name, String instanceName, String className, mxGraph graph, Object[] newCellList)
//	public InstanceAddingAction(String name, String fileName, mxGraph graph, ArrayList<Object> newCellList)
	{
		super(name);
		this.className = className;
		this.instanceName = instanceName;
		this.graph = graph;
		
		this.newCells = Arrays.copyOf(newCellList, newCellList.length);
	}
	
	public void display(Object cell){
		mxCell tempCell = (mxCell) cell;
		System.out.println("ID "+ tempCell.getId());
		System.out.println("label "+ tempCell.getValue());
		System.out.println("style" + tempCell.getStyle());
		System.out.println("geometry "+ tempCell.getGeometry().toString());
		System.out.println("parent ID "+ tempCell.getParent().getId());
	}
	
	public void makeGroup(ActionEvent e) {
		if (graph != null) 
		{
			String filename = this.className.replace(".class", ".ioobn");
			
			mxCell doubleClickedCell = (mxCell) mxGraphComponent.cell;
			mxGeometry doubleClickedCellGeometry = doubleClickedCell.getGeometry();
			Object [] cellsTobeRemoved = new Object[1];
			cellsTobeRemoved[0] = doubleClickedCell;
			graph.removeCells(cellsTobeRemoved);
			
			Object group = new Object();
			group = graph.groupCells(null, getGroupBorder(graph), this.newCells);
			((mxCell)group).setValue(this.className+"_"+this.instanceName);
			((mxCell)group).getGeometry().setX(doubleClickedCellGeometry.getCenterX());
			((mxCell)group).getGeometry().setY(doubleClickedCellGeometry.getCenterY());
			
			graph.addCell(group);
			
			graph.repaint();
		}
	}


	/**
	 * 
	 */
	protected int getGroupBorder(mxGraph graph)
	{
		return 2 * graph.getGridSize();

	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e)
	{
		mxGraph graph = mxGraphActions.getGraph(e);

		if (graph != null)
		{
			System.out.println("I am not null in action performed");
			Object grpCell = graph.groupCells(null, getGroupBorder(graph));
			
			String value = this.className + "_" + this.instanceName;
			((mxCell) grpCell).setValue(value);
			
			graph.setSelectionCell(grpCell);
		}
		makeGroup(e);
	}
}
