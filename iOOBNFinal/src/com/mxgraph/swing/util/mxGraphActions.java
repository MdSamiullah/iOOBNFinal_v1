/**
 * Copyright (c) 2008, Gaudenz Alder
 */
package com.mxgraph.swing.util;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.editor.components.XMLParserIOOBN;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.DefaultFileFilter;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;

/**
 *
 */
public class mxGraphActions
{

	/**
	 * 
	 */
	static final Action deleteAction = new DeleteAction("delete");

	/**
	 * 
	 */
	static final Action editAction = new EditAction("edit");

	/**
	 * 
	 */
	static final Action groupAction = new GroupAction("group");
	
	
	/**
	 * 
	 */
//	static final Action superAction = new SuperAddingAction("super");

	/**
	 * 
	 */
	static final Action ungroupAction = new UngroupAction("ungroup");

	/**
	 * 
	 */
	static final Action removeFromParentAction = new RemoveFromParentAction(
			"removeFromParent");

	/**
	 * 
	 */
	static final Action updateGroupBoundsAction = new UpdateGroupBoundsAction(
			"updateGroupBounds");

	/**
	 * 
	 */
	static final Action selectAllAction = new SelectAction("selectAll");

	/**
	 * 
	 */
	static final Action selectVerticesAction = new SelectAction("vertices");

	/**
	 * 
	 */
	static final Action selectEdgesAction = new SelectAction("edges");

	/**
	 * 
	 */
	static final Action selectNoneAction = new SelectAction("selectNone");

	/**
	 *
	 */
	static final Action selectNextAction = new SelectAction("selectNext");

	/**
	 * 
	 */
	static final Action selectPreviousAction = new SelectAction(
			"selectPrevious");

	/**
	 * 
	 */
	static final Action selectParentAction = new SelectAction("selectParent");

	/**
	 * 
	 */
	static final Action selectChildAction = new SelectAction("selectChild");

	/**
	 * 
	 */
	static final Action collapseAction = new FoldAction("collapse");

	/**
	 * 
	 */
	static final Action expandAction = new FoldAction("expand");

	/**
	 * 
	 */
	static final Action enterGroupAction = new DrillAction("enterGroup");

	/**
	 * 
	 */
	static final Action exitGroupAction = new DrillAction("exitGroup");

	/**
	 * 
	 */
	static final Action homeAction = new DrillAction("home");

	/**
	 * 
	 */
	static final Action zoomActualAction = new ZoomAction("actual");

	/**
	 * 
	 */
	static final Action zoomInAction = new ZoomAction("zoomIn");

	/**
	 * 
	 */
	static final Action zoomOutAction = new ZoomAction("zoomOut");

	/**
	 * 
	 */
	static final Action toBackAction = new LayerAction("toBack");

	/**
	 * 
	 */
	static final Action toFrontAction = new LayerAction("toFront");

	/**
	 * 
	 * @return the delete action
	 */
	public static Action getDeleteAction()
	{
		return deleteAction;
	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getEditAction()
	{
		return editAction;
	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getGroupAction()
	{
		return groupAction;
	}
	
	/**
	 * 
	 * @return the edit action
	 */
//	public static String classFileName = "";
//	public static Action getSuperAction(String className)
//	{
//		classFileName = className;
//		return superAction;
//	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getUngroupAction()
	{
		return ungroupAction;
	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getRemoveFromParentAction()
	{
		return removeFromParentAction;
	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getUpdateGroupBoundsAction()
	{
		return updateGroupBoundsAction;
	}

	/**
	 * 
	 * @return the select all action
	 */
	public static Action getSelectAllAction()
	{
		return selectAllAction;
	}

	/**
	 * 
	 * @return the select vertices action
	 */
	public static Action getSelectVerticesAction()
	{
		return selectVerticesAction;
	}

	/**
	 * 
	 * @return the select edges action
	 */
	public static Action getSelectEdgesAction()
	{
		return selectEdgesAction;
	}

	/**
	 * 
	 * @return the select none action
	 */
	public static Action getSelectNoneAction()
	{
		return selectNoneAction;
	}

	/**
	 * 
	 * @return the select next action
	 */
	public static Action getSelectNextAction()
	{
		return selectNextAction;
	}

	/**
	 * 
	 * @return the select previous action
	 */
	public static Action getSelectPreviousAction()
	{
		return selectPreviousAction;
	}

	/**
	 * 
	 * @return the select parent action
	 */
	public static Action getSelectParentAction()
	{
		return selectParentAction;
	}

	/**
	 * 
	 * @return the select child action
	 */
	public static Action getSelectChildAction()
	{
		return selectChildAction;
	}

	/**
	 * 
	 * @return the go into action
	 */
	public static Action getEnterGroupAction()
	{
		return enterGroupAction;
	}

	/**
	 * 
	 * @return the go up action
	 */
	public static Action getExitGroupAction()
	{
		return exitGroupAction;
	}

	/**
	 * 
	 * @return the home action
	 */
	public static Action getHomeAction()
	{
		return homeAction;
	}

	/**
	 * 
	 * @return the collapse action
	 */
	public static Action getCollapseAction()
	{
		return collapseAction;
	}

	/**
	 * 
	 * @return the expand action
	 */
	public static Action getExpandAction()
	{
		return expandAction;
	}

	/**
	 * 
	 * @return the zoom actual action
	 */
	public static Action getZoomActualAction()
	{
		return zoomActualAction;
	}

	/**
	 * 
	 * @return the zoom in action
	 */
	public static Action getZoomInAction()
	{
		return zoomInAction;
	}

	/**
	 * 
	 * @return the zoom out action
	 */
	public static Action getZoomOutAction()
	{
		return zoomOutAction;
	}

	/**
	 * 
	 * @return the action that moves cell(s) to the backmost layer
	 */
	public static Action getToBackAction()
	{
		return toBackAction;
	}

	/**
	 * 
	 * @return the action that moves cell(s) to the frontmost layer
	 */
	public static Action getToFrontAction()
	{
		return toFrontAction;
	}

	/**
	 * 
	 * @param e
	 * @return Returns the graph for the given action event.
	 */
	public static final mxGraph getGraph(ActionEvent e)
	{
		Object source = e.getSource();

		if (source instanceof mxGraphComponent)
		{
			return ((mxGraphComponent) source).getGraph();
		}

		return null;
	}

	/**
	 * 
	 */
	public static class EditAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 4610112721356742702L;

		/**
		 * 
		 * @param name
		 */
		public EditAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				((mxGraphComponent) e.getSource()).startEditing();
			}
		}

	}

	/**
	 * 
	 */
	public static class DeleteAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -8212339796803275529L;

		/**
		 * 
		 * @param name
		 */
		public DeleteAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.removeCells();
			}
		}

	}

	/**
	 * 
	 */
	public static class GroupAction extends AbstractAction
	{

		public String fileName; 
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4718086600089409092L;

		/**
		 * 
		 * @param name
		 */
		public GroupAction(String name)
		{
			super(name);
//			this.fileName = "";
			this.fileName = "concrete.class";
		}
		
		/**
		 * 
		 * @param name
		 * @param fileName
		 */
		public GroupAction(String name, String fileName)
		{
			super(name);
			this.fileName = fileName;
		}
				
		public void makeGroup(ActionEvent e) {

			
			BasicGraphEditor editor = EditorActions.getEditor(e);

			if (editor != null) {

				mxGraph graph = editor.getGraphComponent().getGraph();
				mxGraph graphBackUp;
				
				if (graph != null) {

					try {
						String filename = StartingFrame.packageLoc + this.fileName.replace(".class", ".ioobn");
						
						System.out.println(filename);
						Document document = mxXmlUtils.parseXml(mxUtils.readFile(filename));

						
						mxCodec codec = new mxCodec(document);
						graphBackUp = graph;
						Object[] cells = graph.getChildCells(graph.getDefaultParent());
						for(int l = 0; l < cells.length; l++){
							mxCell tempCell = (mxCell) cells[l];
							if (tempCell.isVertex()){
								((mxCell)cells[l]).setId(tempCell.getId()+"n");
							}
						}
						
						codec.decode(document.getDocumentElement(), graph.getModel());
						Object[] cells2 = graph.getChildCells(graph.getDefaultParent());
						
						
						Object group = graph.groupCells(null, getGroupBorder(graph), cells2);
						((mxCell) group).setStyle("rounded=1");
						
						graph.addCell(group);
						graph.addCells(cells);
						
					} catch (IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(editor.getGraphComponent(), ex.toString(),
								mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
					}
				}
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
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.setSelectionCell(graph.groupCells(null,
						getGroupBorder(graph)));
			}
			makeGroup(e);
		}

	}

	/**
	 * 
	 */
	public static class UngroupAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2247770767961318251L;

		/**
		 * 
		 * @param name
		 */
		public UngroupAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.setSelectionCells(graph.ungroupCells());
			}
		}

	}

	/**
	 * 
	 */
	public static class RemoveFromParentAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7169443038859140811L;

		/**
		 * 
		 * @param name
		 */
		public RemoveFromParentAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.removeCellsFromParent();
			}
		}

	}

	/**
	 * 
	 */
	public static class UpdateGroupBoundsAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -4718086600089409092L;

		/**
		 * 
		 * @param name
		 */
		public UpdateGroupBoundsAction(String name)
		{
			super(name);
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
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.updateGroupBounds(null, getGroupBorder(graph));
			}
		}

	}

	/**
	 * 
	 */
	public static class LayerAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 562519299806253741L;

		/**
		 * 
		 * @param name
		 */
		public LayerAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				boolean toBack = getValue(Action.NAME).toString()
						.equalsIgnoreCase("toBack");
				graph.orderCells(toBack);
			}
		}

	}

	/**
	 * 
	 */
	public static class FoldAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 4078517503905239901L;

		/**
		 * 
		 * @param name
		 */
		public FoldAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				boolean collapse = getValue(Action.NAME).toString()
						.equalsIgnoreCase("collapse");
				graph.foldCells(collapse);
			}
		}

	}

	/**
	 * 
	 */
	public static class DrillAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 5464382323663870291L;

		/**
		 * 
		 * @param name
		 */
		public DrillAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				String name = getValue(Action.NAME).toString();

				if (name.equalsIgnoreCase("enterGroup"))
				{
					graph.enterGroup();
				}
				else if (name.equalsIgnoreCase("exitGroup"))
				{
					graph.exitGroup();
				}
				else
				{
					graph.home();
				}
			}
		}

	}

	/**
	 * 
	 */
	public static class ZoomAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -7500195051313272384L;

		/**
		 * 
		 * @param name
		 */
		public ZoomAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();

			if (source instanceof mxGraphComponent)
			{
				String name = getValue(Action.NAME).toString();
				mxGraphComponent graphComponent = (mxGraphComponent) source;

				if (name.equalsIgnoreCase("zoomIn"))
				{
					graphComponent.zoomIn();
				}
				else if (name.equalsIgnoreCase("zoomOut"))
				{
					graphComponent.zoomOut();
				}
				else
				{
					graphComponent.zoomActual();
				}
			}
		}

	}

	/**
	 * 
	 */
	public static class SelectAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6501585024845668187L;

		/**
		 * 
		 * @param name
		 */
		public SelectAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				String name = getValue(Action.NAME).toString();

				if (name.equalsIgnoreCase("selectAll"))
				{
					graph.selectAll();
				}
				else if (name.equalsIgnoreCase("selectNone"))
				{
					graph.clearSelection();
				}
				else if (name.equalsIgnoreCase("selectNext"))
				{
					graph.selectNextCell();
				}
				else if (name.equalsIgnoreCase("selectPrevious"))
				{
					graph.selectPreviousCell();
				}
				else if (name.equalsIgnoreCase("selectParent"))
				{
					graph.selectParentCell();
				}
				else if (name.equalsIgnoreCase("vertices"))
				{
					graph.selectVertices();
				}
				else if (name.equalsIgnoreCase("edges"))
				{
					graph.selectEdges();
				}
				else
				{
					graph.selectChildCell();
				}
			}
		}

	}
	
}
