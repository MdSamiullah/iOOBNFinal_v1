/*
 * Copyright (c) 2001-2012, JGraph Ltd
 */
package com.mxgraph.examples.swing.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.editor.components.CodeGenerator;
import com.editor.components.XMLGenerator;
import com.editor.components.XMLParserIOOBN;
import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxDistanceCostFunction;
import com.mxgraph.analysis.mxGraphAnalysis;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.examples.swing.editor.EditorActions.FileListExtraction;
import com.mxgraph.examples.swing.editor.EditorActions.SaveAction;
import com.mxgraph.examples.swing.editor.EditorActions.SuperAddingAction;
import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.util.png.mxPngTextDecoder;
import com.mxgraph.view.mxGraph;

import COM.hugin.HAPI.ExceptionHugin;
import huginIntegration.LoadAndPropagateOOBN;
import javax.swing.SwingUtilities;

/**
 *
 */
public class EditorActions
{
	static String currFileName;
	
	/**
	 * 
	 * @param e
	 * @return Returns the graph for the given action event.
	 */
	public static final BasicGraphEditor getEditor(ActionEvent e)
	{
		if (e.getSource() instanceof Component)
		{
			Component component = (Component) e.getSource();

			while (component != null
					&& !(component instanceof BasicGraphEditor))
			{
				component = component.getParent();
			}

			return (BasicGraphEditor) component;
		}

		return null;
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleRulersItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public ToggleRulersItem(final BasicGraphEditor editor, String name)
		{
			super(name);
			setSelected(editor.getGraphComponent().getColumnHeader() != null);

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					mxGraphComponent graphComponent = editor
							.getGraphComponent();

					if (graphComponent.getColumnHeader() != null)
					{
						graphComponent.setColumnHeader(null);
						graphComponent.setRowHeader(null);
					}
					else
					{
						graphComponent.setColumnHeaderView(new EditorRuler(
								graphComponent,
								EditorRuler.ORIENTATION_HORIZONTAL));
						graphComponent.setRowHeaderView(new EditorRuler(
								graphComponent,
								EditorRuler.ORIENTATION_VERTICAL));
					}
				}
			});
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleGridItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public ToggleGridItem(final BasicGraphEditor editor, String name)
		{
			super(name);
			setSelected(true);

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					mxGraphComponent graphComponent = editor
							.getGraphComponent();
					mxGraph graph = graphComponent.getGraph();
					boolean enabled = !graph.isGridEnabled();

					graph.setGridEnabled(enabled);
					graphComponent.setGridVisible(enabled);
					graphComponent.repaint();
					setSelected(enabled);
				}
			});
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleOutlineItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public ToggleOutlineItem(final BasicGraphEditor editor, String name)
		{
			super(name);
			setSelected(true);

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					final mxGraphOutline outline = editor.getGraphOutline();
					outline.setVisible(!outline.isVisible());
					outline.revalidate();

					SwingUtilities.invokeLater(new Runnable()
					{
						/*
						 * (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						public void run()
						{
							if (outline.getParent() instanceof JSplitPane)
							{
								if (outline.isVisible())
								{
									((JSplitPane) outline.getParent())
											.setDividerLocation(editor
													.getHeight() - 300);
									((JSplitPane) outline.getParent())
											.setDividerSize(6);
								}
								else
								{
									((JSplitPane) outline.getParent())
											.setDividerSize(0);
								}
							}
						}
					});
				}
			});
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ExitAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);
			if (editor != null)
			{
				int optionDialog = 0;
				if (!editor.isModified()
						|| (optionDialog = JOptionPane.showConfirmDialog(editor,
								mxResources.get("loseChanges"))) == JOptionPane.YES_OPTION)
				{
					editor.exit();
				}
				else if (editor.isModified()
						&& optionDialog != JOptionPane.YES_OPTION)
				{
					SaveAction sA = new SaveAction(false);
					sA.actionPerformed(e);
					editor.exit();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class StylesheetAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String stylesheet;

		/**
		 * 
		 */
		public StylesheetAction(String stylesheet)
		{
			this.stylesheet = stylesheet;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();
				mxCodec codec = new mxCodec();
				Document doc = mxUtils.loadDocument(EditorActions.class
						.getResource(stylesheet).toString());

				if (doc != null)
				{
					codec.decode(doc.getDocumentElement(),
							graph.getStylesheet());
					graph.refresh();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ZoomPolicyAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected int zoomPolicy;

		/**
		 * 
		 */
		public ZoomPolicyAction(int zoomPolicy)
		{
			this.zoomPolicy = zoomPolicy;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				graphComponent.setPageVisible(true);
				graphComponent.setZoomPolicy(zoomPolicy);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class GridStyleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected int style;

		/**
		 * 
		 */
		public GridStyleAction(int style)
		{
			this.style = style;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				graphComponent.setGridStyle(style);
				graphComponent.repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class GridColorAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Color newColor = JColorChooser.showDialog(graphComponent,
						mxResources.get("gridColor"),
						graphComponent.getGridColor());

				if (newColor != null)
				{
					graphComponent.setGridColor(newColor);
					graphComponent.repaint();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ScaleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected double scale;

		/**
		 * 
		 */
		public ScaleAction(double scale)
		{
			this.scale = scale;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				double scale = this.scale;

				if (scale == 0)
				{
					String value = (String) JOptionPane.showInputDialog(
							graphComponent, mxResources.get("value"),
							mxResources.get("scale") + " (%)",
							JOptionPane.PLAIN_MESSAGE, null, null, "");

					if (value != null)
					{
						scale = Double.parseDouble(value.replace("%", "")) / 100;
					}
				}

				if (scale > 0)
				{
					graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PageSetupAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				PrinterJob pj = PrinterJob.getPrinterJob();
				PageFormat format = pj.pageDialog(graphComponent
						.getPageFormat());
//				Paper paper = new Paper();
//				paper.setSize(1000, 1000);
//				format.setPaper(paper);

				if (format != null)
				{
					graphComponent.setPageFormat(format);
					graphComponent.zoomAndCenter();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PrintAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				PrinterJob pj = PrinterJob.getPrinterJob();

				if (pj.printDialog())
				{
					PageFormat pf = graphComponent.getPageFormat();
					Paper paper = new Paper();
					double margin = 36;
					paper.setImageableArea(margin, margin, paper.getWidth()
							- margin * 2, paper.getHeight() - margin * 2);
					pf.setPaper(paper);
					pj.setPrintable(graphComponent, pf);

					try
					{
						pj.print();
					}
					catch (PrinterException e2)
					{
						System.out.println(e2);
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SaveAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean showDialog;

		/**
		 * 
		 */
		protected String lastDir = null;
		public String type;

		/**
		 * 
		 */
		public SaveAction(){
//			StartingFrame.iOOBNType = ".class";
//			this.type = ".class";
//			this.type = StartingFrame.iOOBNType;
		}
		
		public SaveAction(String type){
//			StartingFrame.iOOBNType = type;
			this.type = type;
		}
		
		public SaveAction(String type, boolean showDialog){
//			StartingFrame.iOOBNType = type;
			this.type = type;
			this.showDialog = showDialog;
		}
		
		public SaveAction(boolean showDialog)
		{
			this.showDialog = showDialog;
//			this.type = StartingFrame.iOOBNType;
		}

		/**
		 * Saves XML+PNG format.
		 */
		protected void saveXmlPng(BasicGraphEditor editor, String filename,
				Color bg) throws IOException
		{
			mxGraphComponent graphComponent = editor.getGraphComponent();
			mxGraph graph = graphComponent.getGraph();
			
			// Creates the image for the PNG file
			BufferedImage image = mxCellRenderer.createBufferedImage(graph,
					null, 1, bg, graphComponent.isAntiAlias(), null,
					graphComponent.getCanvas());

			// Creates the URL-encoded XML data
			mxCodec codec = new mxCodec();
			String xml = URLEncoder.encode(
					mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
			mxPngEncodeParam param = mxPngEncodeParam
					.getDefaultEncodeParam(image);
			param.setCompressedText(new String[] { "mxGraphModel", xml });

			// Saves as a PNG file
			FileOutputStream outputStream = new FileOutputStream(new File(
					filename));
			try
			{
				mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
						param);

				if (image != null)
				{
					encoder.encode(image);

					editor.setModified(false);
					editor.setCurrentFile(new File(filename));
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noImageData"));
				}
			}
			finally
			{
				outputStream.close();
			}
		}

		/**
		 * 
		 */
		public static ArrayList<Object> allVertices = new ArrayList<Object>();
		
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);
//			StartingFrame.iOOBNType = this.type;
			
			if(showDialog == true)
				StartingFrame.iOOBNType = type;
			
			System.out.println("\n\n\n##############################################\n\n");
			System.out.println("Node Table Size and elements (In EditorAction.java) " + StartingFrame.NodeTable.size());
			for(String key: StartingFrame.NodeTable.keySet()){
				if(StartingFrame.NodeTable.get(key).getBNInfo() != null)
				System.out.println(StartingFrame.NodeTable.get(key).getBNInfo().label);
			}
			System.out.println("\n\n\n##############################################\n\n");

			if (editor != null)
			{
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				FileFilter selectedFilter = null;
				
//				DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".iOOBN",
//						"iOOBN Editor " + mxResources.get("file")	+ " (.iOOBN)");
				System.out.println(StartingFrame.iOOBNType);
				DefaultFileFilter xmlPngFilter = new DefaultFileFilter(StartingFrame.iOOBNType,
						"iOOBN Editor " + mxResources.get("file")	+ " (" + StartingFrame.iOOBNType + ")");
				
				FileFilter vmlFileFilter = new DefaultFileFilter(".html",
						"VML " + mxResources.get("file") + " (.html)");
				String filename = null;
				boolean dialogShown = false;

				if (showDialog || editor.getCurrentFile() == null)
				{
					String wd;

					if (lastDir != null)
					{
						wd = lastDir;
					}
					else if (editor.getCurrentFile() != null)
					{
						wd = editor.getCurrentFile().getParent();
					}
					else
					{
						wd = System.getProperty("user.dir");
					}

					JFileChooser fc = new JFileChooser(wd);

					// Adds the default file format
					FileFilter defaultFilter = xmlPngFilter;
					fc.addChoosableFileFilter(defaultFilter);

					// Adds special vector graphics formats and HTML
//					fc.addChoosableFileFilter(new DefaultFileFilter(".interface",
//							"Interface" + mxResources.get("file") + " (.interface)"));

					// Adds a filter for each supported image format
					Object[] imageFormats = ImageIO.getReaderFormatNames();

					// Finds all distinct extensions
					HashSet<String> formats = new HashSet<String>();

					for (int i = 0; i < imageFormats.length; i++)
					{
						String ext = imageFormats[i].toString().toLowerCase();
						formats.add(ext);
					}

					imageFormats = formats.toArray();

					for (int i = 0; i < imageFormats.length; i++)
					{
						String ext = imageFormats[i].toString();
						fc.addChoosableFileFilter(new DefaultFileFilter("."
								+ ext, ext.toUpperCase() + " "
								+ mxResources.get("file") + " (." + ext + ")"));
					}

					// Adds filter that accepts all supported image formats
					fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
							mxResources.get("allImages")));
					fc.setFileFilter(defaultFilter);
					int rc = fc.showDialog(null, mxResources.get("save"));
					dialogShown = true;

					if (rc != JFileChooser.APPROVE_OPTION)
					{
						return;
					}
					else
					{
						lastDir = fc.getSelectedFile().getParent();
					}

					filename = fc.getSelectedFile().getAbsolutePath();
					selectedFilter = fc.getFileFilter();

					if (selectedFilter instanceof DefaultFileFilter)
					{
						String ext = ((DefaultFileFilter) selectedFilter)
								.getExtension();

						if (!filename.toLowerCase().endsWith(ext))
						{
							filename += ext;
						}
					}
					
					String constructTypeName = filename.replace(StartingFrame.iOOBNType, ".ioobn");
					
					if (new File(constructTypeName).exists()
							&& JOptionPane.showConfirmDialog(graphComponent,
									mxResources.get("overwriteExistingConstruct")) != JOptionPane.YES_OPTION)
					{
						return;
					}
				}
				else
				{
					filename = editor.getCurrentFile().getAbsolutePath();
				}

				currFileName = filename;
				try
				{
					String ext = filename
							.substring(filename.lastIndexOf('.') + 1);

					if (ext.equalsIgnoreCase("svg"))
					{
						mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer
								.drawCells(graph, null, 1, null,
										new CanvasFactory()
										{
											public mxICanvas createCanvas(
													int width, int height)
											{
												mxSvgCanvas canvas = new mxSvgCanvas(
														mxDomUtils.createSvgDocument(
																width, height));
												canvas.setEmbedded(true);

												return canvas;
											}

										});

						mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()),
								filename);
					}
					else if (selectedFilter == vmlFileFilter)
					{
						mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
								.createVmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("html"))
					{
						mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
								.createHtmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("iOOBN")
							|| ext.equalsIgnoreCase("xml")
							|| ext.equalsIgnoreCase("class") 
							|| ext.equalsIgnoreCase("absclass") 
							|| ext.equalsIgnoreCase("interface"))
					{
						mxCodec codec = new mxCodec();
						// this is the place where xml conversion from the graph is done
						String xml = mxXmlUtils.getXml(codec.encode(graph
								.getModel()));
						
						System.out.print(xml);
						
						String iOOBNFilename = filename.replace(StartingFrame.iOOBNType, ".ioobn");
						System.out.println("\nsaved file format 1 " + iOOBNFilename + " type " + type + " iOOBNType " + StartingFrame.iOOBNType);
						mxUtils.writeFile(xml, iOOBNFilename);

						editor.setModified(false);
						editor.setCurrentFile(new File(iOOBNFilename));
						
						/////////////////////////////////
						
						mxAnalysisGraph aGraph = new mxAnalysisGraph();
						aGraph.setGraph(graph);
						Object[][] components = mxGraphStructure.getGraphComponents(aGraph);
						mxIGraphModel model = aGraph.getGraph().getModel();

						// mxGraphModel.getChildCells(graph.getModel(), graph.getDefaultParent(), true, true)
						
						/*
						 * In our NodeTable, some deleted and unused node/vertices may exist, so I have delete them
						 * based on the vertices found here. And after saving all the node info in ioobn file format
						 * I will delete everything from the Node Table / reset the NodeTable. 
						*/
						System.out.println("\nThe Node Table Contents: ");
						for(String key: StartingFrame.NodeTable.keySet()){
							System.out.print(key + "  ");
							System.out.print(StartingFrame.NodeTable.get(key).isConnectable() + "  [false means an instance starts] ");
							System.out.println(StartingFrame.NodeTable.get(key).getValue());
						}
						
						Object[] vertices = graph.getChildVertices(graph.getDefaultParent());
						
						System.out.println("\n\n\nA place that can help in finding mapping of IO nodes and external nodes [Editor Action : Save]\n\n");
						
						Object[] edges = graph.getChildEdges(graph.getDefaultParent());
						System.out.println(" Number of top parent vertex & edge : " + vertices.length + " & " + edges.length);
						for(int j = 0; j < edges.length; j++){
							mxCell edge = (mxCell) edges[j];
							System.out.print("\n" + model.getValue(edge.getSource()) + "[" + edge.getSource().getId() + "]"+ " -> ");
							System.out.print(model.getValue(edge.getTarget()) + "[" + edge.getTarget().getId() + "]");
						}
						 
						extractAllVertices(graph, graph.getDefaultParent());
						Object[] allVerticesArray = allVertices.toArray(new Object[0]);
						// hence in the generated XML, we can only find the nodes outside of group
						// to get objects, you can check with number of groups
						
						XMLGenerator xmlGen = new XMLGenerator();
						System.out.println("Total other vertices " + allVertices.size());
						String xmlForNodeTable = xmlGen.generateXML(allVerticesArray, edges);
						System.out.println("\n\n\nXML Output: \n\n" + xmlForNodeTable);
						
//						String XMLfilename = filename.replace(".ioobn", ".xml");
//						System.out.println("\nsaved file format 2 " + filename);
						String XMLfilename = filename.replace(".ioobn", StartingFrame.iOOBNType);
						
						System.out.println("\nsaved file format 2 " + XMLfilename + " type " + type + " iOOBNType " + StartingFrame.iOOBNType);
						mxUtils.writeFile(xmlForNodeTable, XMLfilename);
						
							/* this is wrong place of resetting Node Table. Otherwise, in case of save as, you will lose
							 * the node information. Hence, in case of exit and in case of new, you can refresh it. 
							 * I think in case of New and Open resetting is more appropriate
							 * */ 
//						System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");
//							StartingFrame.NodeTable = new HashMap<String, mxCell>();
							
							
//						if(showDialog == true)
						{
							String constractName = XMLfilename.substring(XMLfilename.lastIndexOf('\\') + 1);
							EditorMenuBar.superSubMenu.add(editor.bind(constractName, new SuperAddingAction("SuperAdd", constractName), "/com/mxgraph/examples/swing/images/group.gif"));
						}
						
					}
					else if (ext.equalsIgnoreCase("txt"))
					{
						String content = mxGdCodec.encode(graph);

						mxUtils.writeFile(content, filename);
					}
					else
					{
						Color bg = null;

						if ((!ext.equalsIgnoreCase("gif") && !ext
								.equalsIgnoreCase("png"))
								|| JOptionPane.showConfirmDialog(
										graphComponent, mxResources
												.get("transparentBackground")) != JOptionPane.YES_OPTION)
						{
							bg = graphComponent.getBackground();
						}

						if (selectedFilter == xmlPngFilter
								|| (editor.getCurrentFile() != null
										&& ext.equalsIgnoreCase("png") && !dialogShown))
						{
							saveXmlPng(editor, filename, bg);
						}
						else
						{
							BufferedImage image = mxCellRenderer
									.createBufferedImage(graph, null, 1, bg,
											graphComponent.isAntiAlias(), null,
											graphComponent.getCanvas());

							if (image != null)
							{
								ImageIO.write(image, ext, new File(filename));
							}
							else
							{
								JOptionPane.showMessageDialog(graphComponent,
										mxResources.get("noImageData"));
							}
						}
					}
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	static void extractAllVertices(mxGraph graph, Object root){
		 Object[] tempVertices = graph.getChildVertices(root);
		 for(int i=0; i < tempVertices.length; i++){
			 SaveAction.allVertices.add(tempVertices[i]);
			 extractAllVertices(graph, tempVertices[i]);// recursive call
		 }
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SelectShortestPathAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean directed;

		/**
		 * 
		 */
		public SelectShortestPathAction(boolean directed)
		{
			this.directed = directed;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();
				mxIGraphModel model = graph.getModel();

				Object source = null;
				Object target = null;

				Object[] cells = graph.getSelectionCells();

				for (int i = 0; i < cells.length; i++)
				{
					if (model.isVertex(cells[i]))
					{
						if (source == null)
						{
							source = cells[i];
						}
						else if (target == null)
						{
							target = cells[i];
						}
					}

					if (source != null && target != null)
					{
						break;
					}
				}

				if (source != null && target != null)
				{
					int steps = graph.getChildEdges(graph.getDefaultParent()).length;
					Object[] path = mxGraphAnalysis.getInstance()
							.getShortestPath(graph, source, target,
									new mxDistanceCostFunction(), steps,
									directed);
					graph.setSelectionCells(path);
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noSourceAndTargetSelected"));
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SelectSpanningTreeAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean directed;

		/**
		 * 
		 */
		public SelectSpanningTreeAction(boolean directed)
		{
			this.directed = directed;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();
				mxIGraphModel model = graph.getModel();

				Object parent = graph.getDefaultParent();
				Object[] cells = graph.getSelectionCells();

				for (int i = 0; i < cells.length; i++)
				{
					if (model.getChildCount(cells[i]) > 0)
					{
						parent = cells[i];
						break;
					}
				}

				Object[] v = graph.getChildVertices(parent);
				Object[] mst = mxGraphAnalysis.getInstance()
						.getMinimumSpanningTree(graph, v,
								new mxDistanceCostFunction(), directed);
				graph.setSelectionCells(mst);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleDirtyAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				graphComponent.showDirtyRectangle = !graphComponent.showDirtyRectangle;
			}
		}

	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleConnectModeAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxConnectionHandler handler = graphComponent
						.getConnectionHandler();
				handler.setHandleEnabled(!handler.isHandleEnabled());
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleCreateTargetItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public ToggleCreateTargetItem(final BasicGraphEditor editor, String name)
		{
			super(name);
			setSelected(true);

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					mxGraphComponent graphComponent = editor
							.getGraphComponent();

					if (graphComponent != null)
					{
						mxConnectionHandler handler = graphComponent
								.getConnectionHandler();
						handler.setCreateTarget(!handler.isCreateTarget());
						setSelected(handler.isCreateTarget());
					}
				}
			});
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PromptPropertyAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected Object target;

		/**
		 * 
		 */
		protected String fieldname, message;

		/**
		 * 
		 */
		public PromptPropertyAction(Object target, String message)
		{
			this(target, message, message);
		}

		/**
		 * 
		 */
		public PromptPropertyAction(Object target, String message,
				String fieldname)
		{
			this.target = target;
			this.message = message;
			this.fieldname = fieldname;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof Component)
			{
				try
				{
					Method getter = target.getClass().getMethod(
							"get" + fieldname);
					Object current = getter.invoke(target);

					// TODO: Support other atomic types
					if (current instanceof Integer)
					{
						Method setter = target.getClass().getMethod(
								"set" + fieldname, new Class[] { int.class });

						String value = (String) JOptionPane.showInputDialog(
								(Component) e.getSource(), "Value", message,
								JOptionPane.PLAIN_MESSAGE, null, null, current);

						if (value != null)
						{
							setter.invoke(target, Integer.parseInt(value));
						}
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			// Repaints the graph component
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				graphComponent.repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class TogglePropertyItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public TogglePropertyItem(Object target, String name, String fieldname)
		{
			this(target, name, fieldname, false);
		}

		/**
		 * 
		 */
		public TogglePropertyItem(Object target, String name, String fieldname,
				boolean refresh)
		{
			this(target, name, fieldname, refresh, null);
		}

		/**
		 * 
		 */
		public TogglePropertyItem(final Object target, String name,
				final String fieldname, final boolean refresh,
				ActionListener listener)
		{
			super(name);

			// Since action listeners are processed last to first we add the given
			// listener here which means it will be processed after the one below
			if (listener != null)
			{
				addActionListener(listener);
			}

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					execute(target, fieldname, refresh);
				}
			});

			PropertyChangeListener propertyChangeListener = new PropertyChangeListener()
			{

				/*
				 * (non-Javadoc)
				 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
				 */
				public void propertyChange(PropertyChangeEvent evt)
				{
					if (evt.getPropertyName().equalsIgnoreCase(fieldname))
					{
						update(target, fieldname);
					}
				}
			};

			if (target instanceof mxGraphComponent)
			{
				((mxGraphComponent) target)
						.addPropertyChangeListener(propertyChangeListener);
			}
			else if (target instanceof mxGraph)
			{
				((mxGraph) target)
						.addPropertyChangeListener(propertyChangeListener);
			}

			update(target, fieldname);
		}

		/**
		 * 
		 */
		public void update(Object target, String fieldname)
		{
			if (target != null && fieldname != null)
			{
				try
				{
					Method getter = target.getClass().getMethod(
							"is" + fieldname);

					if (getter != null)
					{
						Object current = getter.invoke(target);

						if (current instanceof Boolean)
						{
							setSelected(((Boolean) current).booleanValue());
						}
					}
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		}

		/**
		 * 
		 */
		public void execute(Object target, String fieldname, boolean refresh)
		{
			if (target != null && fieldname != null)
			{
				try
				{
					Method getter = target.getClass().getMethod(
							"is" + fieldname);
					Method setter = target.getClass().getMethod(
							"set" + fieldname, new Class[] { boolean.class });

					Object current = getter.invoke(target);

					if (current instanceof Boolean)
					{
						boolean value = !((Boolean) current).booleanValue();
						setter.invoke(target, value);
						setSelected(value);
					}

					if (refresh)
					{
						mxGraph graph = null;

						if (target instanceof mxGraph)
						{
							graph = (mxGraph) target;
						}
						else if (target instanceof mxGraphComponent)
						{
							graph = ((mxGraphComponent) target).getGraph();
						}

						graph.refresh();
					}
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class HistoryAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean undo;

		/**
		 * 
		 */
		public HistoryAction(boolean undo)
		{
			this.undo = undo;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				if (undo)
				{
					editor.getUndoManager().undo();
				}
				else
				{
					editor.getUndoManager().redo();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class FontStyleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean bold;

		/**
		 * 
		 */
		public FontStyleAction(boolean bold)
		{
			this.bold = bold;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Component editorComponent = null;

				if (graphComponent.getCellEditor() instanceof mxCellEditor)
				{
					editorComponent = ((mxCellEditor) graphComponent
							.getCellEditor()).getEditor();
				}

				if (editorComponent instanceof JEditorPane)
				{
					JEditorPane editorPane = (JEditorPane) editorComponent;
					int start = editorPane.getSelectionStart();
					int ende = editorPane.getSelectionEnd();
					String text = editorPane.getSelectedText();

					if (text == null)
					{
						text = "";
					}

					try
					{
						HTMLEditorKit editorKit = new HTMLEditorKit();
						HTMLDocument document = (HTMLDocument) editorPane
								.getDocument();
						document.remove(start, (ende - start));
						editorKit.insertHTML(document, start, ((bold) ? "<b>"
								: "<i>") + text + ((bold) ? "</b>" : "</i>"),
								0, 0, (bold) ? HTML.Tag.B : HTML.Tag.I);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					editorPane.requestFocus();
					editorPane.select(start, ende);
				}
				else
				{
					mxIGraphModel model = graphComponent.getGraph().getModel();
					model.beginUpdate();
					try
					{
						graphComponent.stopEditing(false);
						graphComponent.getGraph().toggleCellStyleFlags(
								mxConstants.STYLE_FONTSTYLE,
								(bold) ? mxConstants.FONT_BOLD
										: mxConstants.FONT_ITALIC);
					}
					finally
					{
						model.endUpdate();
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class WarningAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Object[] cells = graphComponent.getGraph().getSelectionCells();

				if (cells != null && cells.length > 0)
				{
					String warning = JOptionPane.showInputDialog(mxResources
							.get("enterWarningMessage"));

					for (int i = 0; i < cells.length; i++)
					{
						graphComponent.setCellWarning(cells[i], warning);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noCellSelected"));
				}
			}
		}
	}

	public static int parentClassAddingCount = 0;
	
	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class NewAction extends AbstractAction
	{
		/**
		 * 
		 */
		public String type;
		
		public NewAction(){
//			StartingFrame.iOOBNType = ".class";
//			this.type = ".class";
			this.type = StartingFrame.iOOBNType;
		}
		
		public NewAction(String type){
			this.type = type;
			StartingFrame.iOOBNType = type;
			System.out.println(StartingFrame.iOOBNType);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			parentClassAddingCount = 0;
			BasicGraphEditor editor = getEditor(e);
			StartingFrame.iOOBNType = type;
			
			if (editor != null)
			{
				System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");
				StartingFrame.NodeTable = new HashMap<String, mxCell>();
				
				StartingFrame.chanceNodeCount = 0;
				StartingFrame.decisionNodeCount = 0;
				StartingFrame.utilityNodeCount = 0;
				
				int optionDialog = 0;
				if (!editor.isModified()
						|| (optionDialog = JOptionPane.showConfirmDialog(editor,
								mxResources.get("loseChanges"))) == JOptionPane.YES_OPTION)
				{
					mxGraph graph = editor.getGraphComponent().getGraph();

					// Check modified flag and display save dialog
					mxCell root = new mxCell();
					root.insert(new mxCell());
					graph.getModel().setRoot(root);

					editor.setModified(false);
					editor.setCurrentFile(null);
					editor.getGraphComponent().zoomAndCenter();
				}
				else if (editor.isModified()
						&& optionDialog != JOptionPane.YES_OPTION)
				{
//					SaveAction sA = new SaveAction(false);
					SaveAction sA = new SaveAction(StartingFrame.iOOBNType, false);
					sA.actionPerformed(e);
					
					mxGraph graph = editor.getGraphComponent().getGraph();

					// Check modified flag and display save dialog
					mxCell root = new mxCell();
					root.insert(new mxCell());
					graph.getModel().setRoot(root);

					editor.setModified(false);
					editor.setCurrentFile(null);
					editor.getGraphComponent().zoomAndCenter();

				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ImportAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String lastDir;

		/**
		 * Loads and registers the shape as a new shape in mxGraphics2DCanvas and
		 * adds a new entry to use that shape in the specified palette
		 * @param palette The palette to add the shape to.
		 * @param nodeXml The raw XML of the shape
		 * @param path The path to the directory the shape exists in
		 * @return the string name of the shape
		 */
		public static String addStencilShape(EditorPalette palette,
				String nodeXml, String path)
		{

			// Some editors place a 3 byte BOM at the start of files
			// Ensure the first char is a "<"
			int lessthanIndex = nodeXml.indexOf("<");
			nodeXml = nodeXml.substring(lessthanIndex);
			mxStencilShape newShape = new mxStencilShape(nodeXml);
			String name = newShape.getName();
			ImageIcon icon = null;

			if (path != null)
			{
				String iconPath = path + newShape.getIconPath();
				icon = new ImageIcon(iconPath);
			}

			// Registers the shape in the canvas shape registry
			mxGraphics2DCanvas.putShape(name, newShape);

			if (palette != null && icon != null)
			{
				palette.addTemplate(name, icon, "shape=" + name, 80, 80, "", null);//////////////////
			}

			return name;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				String wd = (lastDir != null) ? lastDir : System
						.getProperty("user.dir");

				JFileChooser fc = new JFileChooser(wd);

				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				// Adds file filter for Dia shape import
				fc.addChoosableFileFilter(new DefaultFileFilter(".shape",
						"Dia Shape " + mxResources.get("file") + " (.shape)"));

				int rc = fc.showDialog(null, mxResources.get("importStencil"));

				if (rc == JFileChooser.APPROVE_OPTION)
				{
					lastDir = fc.getSelectedFile().getParent();

					try
					{
						if (fc.getSelectedFile().isDirectory())
						{
							EditorPalette palette = editor.insertPalette(fc
									.getSelectedFile().getName());

							for (File f : fc.getSelectedFile().listFiles(
									new FilenameFilter()
									{
										public boolean accept(File dir,
												String name)
										{
											return name.toLowerCase().endsWith(
													".shape");
										}
									}))
							{
								String nodeXml = mxUtils.readFile(f
										.getAbsolutePath());
								addStencilShape(palette, nodeXml, f.getParent()
										+ File.separator);
							}

							JComponent scrollPane = (JComponent) palette
									.getParent().getParent();
							editor.getLibraryPane().setSelectedComponent(
									scrollPane);

							// FIXME: Need to update the size of the palette to force a layout
							// update. Re/in/validate of palette or parent does not work.
							//editor.getLibraryPane().revalidate();
						}
						else
						{
							String nodeXml = mxUtils.readFile(fc
									.getSelectedFile().getAbsolutePath());
							String name = addStencilShape(null, nodeXml, null);

							JOptionPane.showMessageDialog(editor, mxResources
									.get("stencilImported",
											new String[] { name }));
						}
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 
	   File inputFile = new File("C:\\Users\\msamiull\\workspace\\jgraphx-master\\t.xml");
       DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
       DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
       Document doc = dBuilder.parse(inputFile);
       
	   doc.getDocumentElement().normalize();
       System.out.println("Main element :"+ doc.getDocumentElement().getNodeName());
        
       NodeList nodeList = doc.getElementsByTagName("node");
	 
	 */
	@SuppressWarnings("serial")
	public static class FileListExtraction extends AbstractAction
	{	
		public String directory;
		
		public FileListExtraction(){
			directory = StartingFrame.packageLoc;
		}
		
		public String typeChecking(String fileName) throws ParserConfigurationException, SAXException, IOException
		{
			String type = ".class";
			
			File inputFile = new File(directory+fileName);
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc = dBuilder.parse(inputFile);
		       
		    doc.getDocumentElement().normalize();
		    Node node = doc.getElementsByTagName("ioobn").item(0);
		    
		    Element eElem = (Element) node;
			
			type = eElem.getAttribute("type");

			return type;
		}
		
		// file types mean .class/.absclass/.interface and extension is also same
		// then why duplicate two things? cause I was not sure if different extension 
		// can be used to store different type of construct
		public ArrayList<String> extractListOfFileNames(ArrayList<String> fileTypes, String extension) throws ParserConfigurationException, SAXException, IOException
		{
			ArrayList<String> ioobnFiles = new ArrayList<String>();

			File[] files = new File(directory).listFiles();
			//If this pathname does not denote a directory, then listFiles() returns null. 

			for (File file : files) {
			    if (file.isFile() && file.getName().endsWith(extension)) {
			    	for(String key: fileTypes){
			    		if(typeChecking(file.getName()).equalsIgnoreCase(key))
			    			ioobnFiles.add(file.getName());
			    	}
			    }
			}

			return ioobnFiles;
		}
		
		public ArrayList<String> extractListOfFileNames(ArrayList<String> extensions) throws ParserConfigurationException, SAXException, IOException
		{
			ArrayList<String> ioobnFiles = new ArrayList<String>();

			File[] files = new File(directory).listFiles();
			//If this pathname does not denote a directory, then listFiles() returns null. 

			for(String extension: extensions){
				for (File file : files) {
				    if (file.isFile() && file.getName().endsWith(extension)) {
				    	ioobnFiles.add(file.getName());
				    }
				}
			}

			return ioobnFiles;
		}

		
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			ArrayList<String> fileTypes = new ArrayList<String>();
			// classes
			fileTypes.add(".class");
			try {
				System.out.println("List of Files .xml " + extractListOfFileNames(fileTypes, ".xml"));
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// classes (abstract+concrete)
			fileTypes.add(".absclass");
			try {
				System.out.println("List of Files .xml " + extractListOfFileNames(fileTypes, ".xml"));
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// classes + interfaces
			fileTypes.add(".interface");
			try {
				System.out.println("List of Files .xml " + extractListOfFileNames(fileTypes, ".xml"));
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class OpenAction extends AbstractAction
	{
		/**
		 * 
		 */
		public String type;
		protected String lastDir;
		
		public OpenAction(){
//			StartingFrame.iOOBNType = ".class";
			this.type = ".class";
		}
		
		public OpenAction(String type){
//			StartingFrame.iOOBNType = type;
			this.type = type;
//			System.out.println(StartingFrame.iOOBNType);
		}

		/**
		 * 
		 */
		protected void resetEditor(BasicGraphEditor editor)
		{
			editor.setModified(false);
			editor.getUndoManager().clear();
			editor.getGraphComponent().zoomAndCenter();
		}

		/**
		 * Reads XML+PNG format.
		 */
		protected void openXmlPng(BasicGraphEditor editor, File file)
				throws IOException
		{
			Map<String, String> text = mxPngTextDecoder
					.decodeCompressedText(new FileInputStream(file));

			if (text != null)
			{
				String value = text.get("mxGraphModel");

				if (value != null)
				{
					Document document = mxXmlUtils.parseXml(URLDecoder.decode(
							value, "UTF-8"));
					mxCodec codec = new mxCodec(document);
					codec.decode(document.getDocumentElement(), editor
							.getGraphComponent().getGraph().getModel());
					editor.setCurrentFile(file);
					resetEditor(editor);

					return;
				}
			}

			JOptionPane.showMessageDialog(editor,
					mxResources.get("imageContainsNoDiagramData"));
		}

		/**
		 * @throws IOException
		 *
		 */
		protected void openGD(BasicGraphEditor editor, File file,
				String gdText)
		{
			mxGraph graph = editor.getGraphComponent().getGraph();

			// Replaces file extension with .mxe
			String filename = file.getName();
//			filename = filename.substring(0, filename.length() - 4) + ".iOOBN";
			filename = filename.replace(StartingFrame.iOOBNType, ".ioobn");

			if (new File(filename).exists()
					&& JOptionPane.showConfirmDialog(editor,
							mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
			{
				return;
			}

			((mxGraphModel) graph.getModel()).clear();
			mxGdCodec.decode(gdText, graph);
			editor.getGraphComponent().zoomAndCenter();
			editor.setCurrentFile(new File(lastDir + "/" + filename));
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);
			StartingFrame.iOOBNType = type;

			if (editor != null)
			{
				// it has been done later
//				System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");
//				StartingFrame.NodeTable = new HashMap<String, mxCell>();
				/*
				 * However, doing the following may introduce confusion with existing ones. 
				 * An appropriate would be initialize with n+1 instead of 0 where n is the last node count  
				 * of the opened version
				 * */
				StartingFrame.chanceNodeCount = 0;
				StartingFrame.decisionNodeCount = 0;
				StartingFrame.utilityNodeCount = 0;
				
				if (!editor.isModified()
						|| JOptionPane.showConfirmDialog(editor,
								mxResources.get("loseChanges")) == JOptionPane.YES_OPTION)
				{
					mxGraph graph = editor.getGraphComponent().getGraph();

					if (graph != null)
					{
						String wd = (lastDir != null) ? lastDir : System
								.getProperty("user.dir");

						JFileChooser fc = new JFileChooser(wd);

						// Adds file filter for supported file format

						System.out.println("filter " + StartingFrame.iOOBNType);
						DefaultFileFilter defaultFilter = new DefaultFileFilter(
								StartingFrame.iOOBNType, mxResources.get("allSupportedFormats")
										+ " (" + StartingFrame.iOOBNType + ")")
						{

							public boolean accept(File file)
							{
								String lcase = file.getName().toLowerCase();

								return super.accept(file);
							}
						};
						fc.addChoosableFileFilter(defaultFilter);
						
						fc.addChoosableFileFilter(new DefaultFileFilter(".class",
								"iOOBN Editor " + mxResources.get("file")
										+ " (.class)"));
						fc.addChoosableFileFilter(new DefaultFileFilter(".absclass",
								"iOOBN Editor " + mxResources.get("file")
										+ " (.absclass)"));
						fc.addChoosableFileFilter(new DefaultFileFilter(".interface",
								"iOOBN Editor " + mxResources.get("file")
										+ " (.interface)"));

						fc.setFileFilter(defaultFilter);

						int rc = fc.showDialog(null,
								mxResources.get("openFile"));

						if (rc == JFileChooser.APPROVE_OPTION)
						{
							lastDir = fc.getSelectedFile().getParent();

							try
							{
								if (fc.getSelectedFile().getAbsolutePath()
										.toLowerCase().endsWith(".png"))
								{
									openXmlPng(editor, fc.getSelectedFile());
								}
								else if (fc.getSelectedFile().getAbsolutePath()
										.toLowerCase().endsWith(".txt"))
								{
									openGD(editor, fc.getSelectedFile(),
											mxUtils.readFile(fc
													.getSelectedFile()
													.getAbsolutePath()));
								}
								else
								{	
									/////////////////////////////////////////////////////////////////
									String fileNameXML = fc.getSelectedFile().getPath();
//									fileNameXML = fileNameXML.replace(".ioobn", StartingFrame.iOOBNType);
									File inputFile = new File(fileNameXML);
									System.out.println("opened file name "+ fileNameXML);
 									currFileName = fileNameXML;
 							        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
							        DocumentBuilder dBuilder;
							        
									try {
										dBuilder = dbFactory.newDocumentBuilder();
										Document doc = dBuilder.parse(inputFile);
										doc.getDocumentElement().normalize();
										NodeList nodeList = doc.getElementsByTagName("node");
										
										ArrayList<String> tagList = new ArrayList<String>();
	
	//							        tagList.add("node");
								        tagList.add("state");
								        tagList.add("parent");
								        tagList.add("tuple");
								        System.out.println("Node list length in main " + nodeList.getLength());
								        System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");
								        StartingFrame.NodeTable = new HashMap<String, mxCell>();
								        XMLParserIOOBN xmlParser = new XMLParserIOOBN();
								        xmlParser.recursiveParserXML(nodeList, tagList, null);// initially mxCell is null
									} catch (ParserConfigurationException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (SAXException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
							/////////////////////////////////////////////////////////////////
									
									String filename = fc.getSelectedFile().getAbsolutePath().replace
											(StartingFrame.iOOBNType, ".ioobn");
									System.out.println("opened last file " + filename + " iOOBN type " + StartingFrame.iOOBNType);
									Document document = mxXmlUtils
											.parseXml(mxUtils.readFile(filename));
							
									mxCodec codec = new mxCodec(document);
									codec.decode(
											document.getDocumentElement(),
											graph.getModel());
									editor.setCurrentFile(fc
											.getSelectedFile());

									resetEditor(editor);
								}
							}
							catch (IOException ex)
							{
								ex.printStackTrace();
								JOptionPane.showMessageDialog(
										editor.getGraphComponent(),
										ex.toString(),
										mxResources.get("error"),
										JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String key;

		/**
		 * 
		 */
		protected boolean defaultValue;
		
		/**
		 * 
		 */
		protected boolean defaultForStroke;

		/**
		 * 
		 * @param key
		 */
		public ToggleAction(String key)
		{
			this(key, false, false);
		}

		/**
		 * 
		 * @param key
		 */
		public ToggleAction(String key, boolean defaultValue)
		{
			this.key = key;
			this.defaultValue = defaultValue;
			this.defaultForStroke = false;
		}
		
		/**
		 * 
		 * @param key
		 */
		public ToggleAction(String key, boolean defaultValue, boolean defaultForStroke)
		{
			this.key = key;
			this.defaultValue = defaultValue;
			this.defaultForStroke = defaultForStroke;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);
			System.out.println("Hi I am here to change node type In/Out/Emb");
			if (graph != null)
			{
				mxCell selectedCell = (mxCell) graph.getSelectionCell(); 
				if(!key.equalsIgnoreCase(mxConstants.STYLE_DOUBLED)){
					graph.toggleCellStyles(key, defaultValue);
					// to keep track of node type (EIO) change in NodeTable
					// mark input
					StartingFrame.NodeTable.get(selectedCell.getId()).getBNInfo().setBasicNodeInfo(null, null, null, -1, -1, 
							null, null, null, null, null, null, false, 1);
				}
				else{
					if(!this.defaultForStroke){
						graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "5");
						this.defaultForStroke = true;
						
						// to keep track of node type (EIO) change in NodeTable
						// mark output
						StartingFrame.NodeTable.get(selectedCell.getId()).getBNInfo().setBasicNodeInfo(null, null, null, -1, -1, 
								null, null, null, null, null, null, false, 2);
					}
					else{
						graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "2");
						this.defaultForStroke = false;
						// to keep track of node type (EIO) change in NodeTable
						// mark embedded or 
						StartingFrame.NodeTable.get(selectedCell.getId()).getBNInfo().setBasicNodeInfo(null, null, null, -1, -1, 
								null, null, null, null, null, null, false, 0);
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	
	@SuppressWarnings("serial")
	public static class ToggleSolidAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String key;

		/**
		 * 
		 */
		protected boolean defaultValue;

		/**
		 * 
		 * @param key
		 */
		public ToggleSolidAction(String key)
		{
			this(key, false);
		}

		/**
		 * 
		 * @param key
		 */
		public ToggleSolidAction(String key, boolean defaultValue)
		{
			this.key = key;
			this.defaultValue = defaultValue;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null)
			{
				graph.toggleCellStyles(key, defaultValue);
			}
		}
	}
	
	
	
	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleDoubleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String key;

		/**
		 * 
		 */
		protected boolean defaultValue;

		/**
		 * 
		 * @param key
		 */
		public ToggleDoubleAction(String key)
		{
			this(key, false);
		}

		/**
		 * 
		 * @param key
		 */
		public ToggleDoubleAction(String key, boolean defaultValue)
		{
			this.key = key;
			this.defaultValue = defaultValue;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null)
			{
				
				graph.toggleCellStyles(key, defaultValue);
			}
		}
	}

	
	
	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SetLabelPositionAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String labelPosition, alignment;

		/**
		 * 
		 * @param key
		 */
		public SetLabelPositionAction(String labelPosition, String alignment)
		{
			this.labelPosition = labelPosition;
			this.alignment = alignment;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				graph.getModel().beginUpdate();
				try
				{
					// Checks the orientation of the alignment to use the correct constants
					if (labelPosition.equals(mxConstants.ALIGN_LEFT)
							|| labelPosition.equals(mxConstants.ALIGN_CENTER)
							|| labelPosition.equals(mxConstants.ALIGN_RIGHT))
					{
						graph.setCellStyles(mxConstants.STYLE_LABEL_POSITION,
								labelPosition);
						graph.setCellStyles(mxConstants.STYLE_ALIGN, alignment);
					}
					else
					{
						graph.setCellStyles(
								mxConstants.STYLE_VERTICAL_LABEL_POSITION,
								labelPosition);
						graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN,
								alignment);
					}
				}
				finally
				{
					graph.getModel().endUpdate();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SetStyleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String value;
		protected boolean isDoubled;

		/**
		 * 
		 * @param key
		 */
		public SetStyleAction(String value)
		{
			this.value = value;
			isDoubled = false;
		}
		public SetStyleAction(String value, boolean doubled)
		{
			this.value = value;
			isDoubled = doubled;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				if(!isDoubled)		graph.setCellStyle(value);
				else	graph.setCellStyles(value, "5");
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class KeyValueAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String key, value;

		/**
		 * 
		 * @param key
		 */
		public KeyValueAction(String key)
		{
			this(key, null);
		}

		/**
		 * 
		 * @param key
		 */
		public KeyValueAction(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				graph.setCellStyles(key, value);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PromptValueAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String key, message;

		/**
		 * 
		 * @param key
		 */
		public PromptValueAction(String key, String message)
		{
			this.key = key;
			this.message = message;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof Component)
			{
				mxGraph graph = mxGraphActions.getGraph(e);

				if (graph != null && !graph.isSelectionEmpty())
				{
					String value = (String) JOptionPane.showInputDialog(
							(Component) e.getSource(),
							mxResources.get("value"), message,
							JOptionPane.PLAIN_MESSAGE, null, null, "");

					if (value != null)
					{
						if (value.equals(mxConstants.NONE))
						{
							value = null;
						}

						graph.setCellStyles(key, value);
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class AlignCellsAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String align;

		/**
		 * 
		 * @param key
		 */
		public AlignCellsAction(String align)
		{
			this.align = align;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				graph.alignCells(align);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class AutosizeAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				Object[] cells = graph.getSelectionCells();
				mxIGraphModel model = graph.getModel();

				model.beginUpdate();
				try
				{
					for (int i = 0; i < cells.length; i++)
					{
						graph.updateCellSize(cells[i]);
					}
				}
				finally
				{
					model.endUpdate();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ColorAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String name, key;

		/**
		 * 
		 * @param key
		 */
		public ColorAction(String name, String key)
		{
			this.name = name;
			this.key = key;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();

				if (!graph.isSelectionEmpty())
				{
					Color newColor = JColorChooser.showDialog(graphComponent,
							name, null);

					if (newColor != null)
					{
						graph.setCellStyles(key, mxUtils.hexString(newColor));
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class BackgroundImageAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				String value = (String) JOptionPane.showInputDialog(
						graphComponent, mxResources.get("backgroundImage"),
						"URL", JOptionPane.PLAIN_MESSAGE, null, null,
						"http://www.callatecs.com/images/background2.JPG");

				if (value != null)
				{
					if (value.length() == 0)
					{
						graphComponent.setBackgroundImage(null);
					}
					else
					{
						Image background = mxUtils.loadImage(value);
						// Incorrect URLs will result in no image.
						// TODO provide feedback that the URL is not correct
						if (background != null)
						{
							graphComponent.setBackgroundImage(new ImageIcon(
									background));
						}
					}

					// Forces a repaint of the outline
					graphComponent.getGraph().repaint();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class BackgroundAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Color newColor = JColorChooser.showDialog(graphComponent,
						mxResources.get("background"), null);

				if (newColor != null)
				{
					graphComponent.getViewport().setOpaque(true);
					graphComponent.getViewport().setBackground(newColor);
				}

				// Forces a repaint of the outline
				graphComponent.getGraph().repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PageBackgroundAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Color newColor = JColorChooser.showDialog(graphComponent,
						mxResources.get("pageBackground"), null);

				if (newColor != null)
				{
					graphComponent.setPageBackgroundColor(newColor);
				}

				// Forces a repaint of the component
				graphComponent.repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class StyleAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();
				String initial = graph.getModel().getStyle(
						graph.getSelectionCell());
				String value = (String) JOptionPane.showInputDialog(
						graphComponent, mxResources.get("style"),
						mxResources.get("style"), JOptionPane.PLAIN_MESSAGE,
						null, null, initial);

				if (value != null)
				{
					graph.setCellStyle(value);
				}
			}
		}
	}
	
	
	

	/**
	 * 
	 */
	public static class SuperAddingAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String fileName;
		
		/**
		 * 
		 */
//		private static final long serialVersionUID = -4718086600089409092L;

		/**
		 * 
		 * @param name
		 */
		public SuperAddingAction(String name)
		{
			super(name);
			this.fileName = "";
		}
		
		/**
		 * 
		 * @param name
		 * @param fileName
		 */
		public SuperAddingAction(String name, String fileName)
		{
			super(name);
			this.fileName = fileName;
		}
		
		protected void resetEditor(BasicGraphEditor editor)
		{
			editor.setModified(false);
			editor.getUndoManager().clear();
			editor.getGraphComponent().zoomAndCenter();
		}
		
		public void addParentClass(ActionEvent e) throws IOException {
			
			BasicGraphEditor editor = EditorActions.getEditor(e);

			if (editor != null) {

				mxGraph graph = editor.getGraphComponent().getGraph();
				mxGraph graphBackUp;
				
				if (graph != null) {
					
					// if you parse .class (making NodeTable) after .ioobn (making graphs) then label and property 
					// updating will not take place  
					////////////////////ioobn part begins///////////////////////
					System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");	
				StartingFrame.NodeTable = new HashMap<String, mxCell>();
				
				File inputFile = new File(this.fileName);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder;
				try {
					dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(inputFile);
					doc.getDocumentElement().normalize();
					NodeList nodeList = doc.getElementsByTagName("node");

					ArrayList<String> tagList = new ArrayList<String>();

					// tagList.add("node");
					tagList.add("state");
					tagList.add("parent");
					tagList.add("tuple");
					System.out.println("Node list length in main " + nodeList.getLength());
					System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");
					StartingFrame.NodeTable = new HashMap<String, mxCell>();
					XMLParserIOOBN xmlParser = new XMLParserIOOBN();
					xmlParser.recursiveParserXML(nodeList, tagList, null);// initially mxCell is null
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				////////////////////ioobn part ends///////////////////////


					try {

						
						Object[] cells2 = graph.getChildCells(graph.getDefaultParent());
						
						for(int l = 0; l < cells2.length; l++){
							mxCell tempCell = (mxCell) cells2[l];
							if (tempCell.isVertex()){
								((mxCell)cells2[l]).setId(tempCell.getId());
							}
						}
						
//						String filename = this.fileName.replace(".class", ".ioobn");
						String filename = ""; 
						if(this.fileName.endsWith(".class"))	filename = this.fileName.replace(".class", ".ioobn");
						else if(this.fileName.endsWith(".absclass"))	filename = this.fileName.replace(".absclass", ".ioobn");
						else if(this.fileName.endsWith(".interface"))	filename = this.fileName.replace(".interface", ".ioobn");
						
						Document document = mxXmlUtils.parseXml(mxUtils.readFile(filename));

						mxCodec codec = new mxCodec(document);
						graphBackUp = graph;
						Object[] cells = graph.getChildCells(graph.getDefaultParent());
						
						codec.decode(document.getDocumentElement(), graph.getModel());
						
						graph.addCells(cells);
						
					} catch (IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(editor.getGraphComponent(), ex.toString(),
								mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
					}
				}
				
								resetEditor(editor);
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
			parentClassAddingCount++;
			mxGraph graph = mxGraphActions.getGraph(e);

			if(parentClassAddingCount <= 1){
				if (graph != null)
				{
					graph.setSelectionCell(graph.groupCells(null,
							getGroupBorder(graph)));
				}
				try {
					addParentClass(e);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "Atmost one parent class can be added!", "No more parents", JOptionPane.WARNING_MESSAGE);
			}
			
		}

	}

	/**
	 * 
	 */
	public static class CompileAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
				
		public void generateCodes(ActionEvent e) throws IOException {
			FileListExtraction flistEx = new FileListExtraction();
			ArrayList<String> extensions = new ArrayList<String>();
			extensions.add(".class");//	extensions.add(".absclass");
			try {
				ArrayList<String> classes = flistEx.extractListOfFileNames(extensions);
				System.out.println("List of class files " + classes);
				
				BasicGraphEditor editor = getEditor(e);
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				Object[] vertices = graph.getChildVertices(graph.getDefaultParent());
				Object[] edges = graph.getChildEdges(graph.getDefaultParent());
				
				for(int j = 0; j < edges.length; j++){
					
					mxCell edge = (mxCell) edges[j];
					
					if(edge.getSource().getParent() != edge.getTarget().getParent())
					{
						mxGraphModel.refEdges.add(edge);
					}
				}
				
				CodeGenerator cg = new CodeGenerator();
				
				String code = cg.generateCode(vertices, currFileName);
				mxUtils.writeFile(code, currFileName.replace(".class", ".oobn"));
				
			} catch (ParserConfigurationException | SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			try {
				generateCodes(e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}
	
	/**
	 * 
	 */
	
//	public static String defaultPackage = "C:\\Users\\msamiull\\workspace\\jgraphx-master\\";
	// the above information is already defined in "StartingFrame" class with name "packageLoc"
	
	public static class RunAction extends AbstractAction
	{
		public ArrayList<ArrayList<String>> outcome;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
				
		public void inferenceOutcomeDisplay()
		{
			JFrame frame = new JFrame();

			if (frame != null)
			{
				EditorInferenceOutcomeFrame outcome = new EditorInferenceOutcomeFrame(frame, this.outcome);
				outcome.setModal(true);

				// Shows the modal dialog and waits
				outcome.setVisible(true);
			}
		}
		
		public void runCodes(ActionEvent e) throws IOException, ExceptionHugin, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			
			FileListExtraction flistEx = new FileListExtraction();
			ArrayList<String> extensions = new ArrayList<String>();
			extensions.add(".class");//	extensions.add(".absclass");
			try {
				ArrayList<String> classes = flistEx.extractListOfFileNames(extensions);
				System.out.println("List of class files " + classes);
				
				BasicGraphEditor editor = getEditor(e);
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				
				Object[] vertices = graph.getChildVertices(graph.getDefaultParent());
				LoadAndPropagateOOBN lpOOBN = new LoadAndPropagateOOBN();
					
				ArrayList<String> classNames = new ArrayList<String>();
				ArrayList<String> files = new ArrayList<String>();
				
				// just to remove all extensions from file names to get class names
				// since Hugin maintains "a.oobn" has class "a"
				
//				for(int I = 0; I < classes.size(); I++){
//					classes.set(I, classes.get(I).replace(".class", ""));
//				}
				
				ArrayList<String> fileNames = new ArrayList<String>();
				String fileName = "";
				System.out.println(currFileName);
				// so far, current file only gets the file for which "run code" is called
				// but we also need to load and propagate super class and instantiating classes
				// of course, current class should be the last to be loaded
				
				// however, after using "MyParseListener", I don't need to do anything for instance class loading. 
				// Hence I have blocked next few lines  
				
				
				// do something to load super classes
				
				
				// do something to load instantiating classes. You can find all instances in "CodeGenerator" class 
				// with name "InstanceClassMap" having instance to class mapping. So extract class names from there
//				System.out.println("The instance file names are:::::::: ");
//				for(String str: CodeGenerator.InstanceClassMap.values()){
//					fileNames.add(StartingFrame.packageLoc+"\\"+str+".oobn");
//					classNames.add(str);
//					System.out.println(StartingFrame.packageLoc+"\\"+str+".oobn");
//				}
				
				String[] fileNameParts = currFileName.split("\\\\");
				fileName = fileNameParts[fileNameParts.length-1];
				System.out.println(fileName);
				if(currFileName.endsWith(".class")){
					fileNames.add(currFileName.replace(".class", ".oobn"));
					classNames.add(fileName.replace(".class", ""));
				}
				else if(currFileName.endsWith(".ioobn")){
					fileNames.add(currFileName.replace(".ioobn", ".oobn"));
					classNames.add(fileName.replace(".ioobn", ""));
				}
				System.out.println("Class File names in EditorAction " + fileNames);
				System.out.println("Class names in EditorAction " + classNames);
				ArrayList<String> cases = new ArrayList<String>();
				
				// do something to add case files
				cases.add(null);
				
				System.out.println("fileNames " + fileNames + " classNames " + classNames);
				// the "2" as last parameter in LAP func, is a default number  for number of states to be used in calculating JT cost Kanazawa
				
				this.outcome = lpOOBN.LAP (fileNames, classNames, cases, "", 2);// dir = "";
				
				inferenceOutcomeDisplay();
			} catch (ParserConfigurationException | SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			try {
				runCodes(e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExceptionHugin e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

}
