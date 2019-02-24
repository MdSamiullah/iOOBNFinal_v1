package com.mxgraph.frames;

import java.awt.BorderLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JLayeredPane;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.Component;
import javax.swing.JTable;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.editor.EditorActions.ExitAction;
import com.mxgraph.examples.swing.editor.InstanceAddingAction;
import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;

import com.editor.components.Attribute;
import com.editor.components.BasicNode;
import com.editor.components.Instance;
import com.editor.components.XMLParserIOOBN;

import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JDesktopPane;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.ButtonGroup;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.JInternalFrame;
import java.awt.Cursor;

public class FrameInstance extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private int count;
	private int count1;
	
	private JPanel panel3;
	private JPanel panel2;
	private JScrollPane scrollPane_1;
	
	public Attribute attrib;
	public LinkedHashMap <String, String> attributes;
	public Instance inst;
	
	public ArrayList<String> numdata;
	
	public mxCell cell;
	
	/**
	 * @wbp.nonvisual location=63,14
	 */
//	private final JLabel label = DefaultComponentFactory.getInstance().createTitle("New JGoodies title");
	public JTextField textField;
	public JTextField textField_2;
	public JComboBox<String> classList;
	public final ButtonGroup buttonGroup = new ButtonGroup();
	public final ButtonGroup buttonGroup_1 = new ButtonGroup();
	public final ButtonGroup buttonGroup_2 = new ButtonGroup();
	public final Action action = new SwingAttribute();
	public final Action actionCancel = new SwingCancel();
	public static String classNInstanceName = "";
	
//	public mxGraphComponent fmxGC;
	public Object obj;
	public String className;
	
//	public static Object[][] instances;
//	public static mxGraph[] newGraph;
	
	private final Action action_2 = new SwingSave();

	/**
	 * Launch the application.
	 */
	
	public void postAttribValue(){
		if(attributes.get(attrib.name) != null){
			System.err.println("You have updated an existing attribute");
			attributes.put(attrib.name, attrib.value);
//			System.out.println(attributes);
		}
		else attributes.put(attrib.name, attrib.value);
		System.out.println("In FrameNode: Attribute (name, value) = "+attrib.getName() + "  " + attrib.getValue());
	}
	
	public void postBelief(){
		
		System.out.println("In FrameNode: Belief = "+ numdata);
	}
	
	public void setCell(mxCell cell)
	{
		this.cell = cell;
	}
	
	public mxCell getCell()
	{
		return this.cell;
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public FrameInstance() throws ParserConfigurationException, SAXException, IOException {
		
		attrib = new Attribute();
		inst = new Instance();
		attributes = new LinkedHashMap<String, String>();
		
//		instances = new Object[100][];
//		newGraph = new mxGraph[100];
		
		panel2 = new JPanel();
		
		BasicNode tempBN= new BasicNode();
		
		setTitle("Node Properties");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 469, 334);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Options");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAction(action_2);
		mnNewMenu.add(mntmSave);
		
		JMenuItem mntmCancel = new JMenuItem("Exit");
		mnNewMenu.add(mntmCancel);
		mntmCancel.setAction(actionCancel);
		
		JMenu mnAdd = new JMenu("Add");
		menuBar.add(mnAdd);
		
		JMenuItem mntmAttribute = new JMenuItem("Attribute");
		mntmAttribute.setAction(action);
		mnAdd.add(mntmAttribute);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		table = new JTable();
		contentPane.add(table);
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		contentPane.add(desktopPane);
		
		JLabel lblName = new JLabel("Instance Name");
		lblName.setBounds(12, 25, 128, 16);
		desktopPane.add(lblName);
		
		textField = new JTextField();
		textField.setBounds(103, 22, 290, 22);
		desktopPane.add(textField);
		textField.setColumns(10);
		textField.setText(tempBN.name);
		
		JLabel lblNodeLabel = new JLabel("Instance Label");
		lblNodeLabel.setBounds(12, 60, 128, 16);
		desktopPane.add(lblNodeLabel);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(103, 57, 290, 22);
		desktopPane.add(textField_2);
		textField_2.setText(tempBN.label);
		
		JLabel lblNodeStates = new JLabel("Classes");
		lblNodeStates.setBounds(12, 102, 81, 16);
		desktopPane.add(lblNodeStates);
		
		JButton btnNewButton = new JButton("Cancel");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(243, 204, 97, 25);
		desktopPane.add(btnNewButton);
		btnNewButton.setAction(actionCancel);
		
		JButton button = new JButton("Save");
		button.setBounds(103, 204, 97, 25);
		desktopPane.add(button);
		button.setAction(action_2);
		
		JPanel panel = new JPanel();
		panel.setBounds(334, 92, -31, 26);
		desktopPane.add(panel);
		
		panel2.setBounds(97, 300, 284, 55);
		
		EditorActions.FileListExtraction tempFileListExtractor = new EditorActions.FileListExtraction();
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(".class");
		extensions.add(".absclass");
		ArrayList<String> fileNameList = tempFileListExtractor.extractListOfFileNames(extensions);
		System.out.println("Number of instantiating classes " + fileNameList.size());
		String[] strArray = new String [fileNameList.size()];
		strArray = fileNameList.toArray(strArray);
		
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		
		JPanel comboPane = new JPanel();
		comboPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboPane.setBackground(Color.WHITE);
		comboPane.setBounds(103, 102, 290, 32);
		classList = new JComboBox<String>();
		classList.setAlignmentX(Component.LEFT_ALIGNMENT);
		classList.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		for(String key: fileNameList){
			classList.addItem(key);
		}
		classList.setMaximumRowCount(8);
		classList.setAutoscrolls(true);
		classList.setBounds(103, 102, 100, 23);
		comboPane.add(classList);
		panel2.setBounds(105, 113, 250, 20);
		comboPane.add(panel2);
		desktopPane.add(comboPane);		
	}

	
	
	private class SwingAttribute extends AbstractAction {
		public SwingAttribute() {
			putValue(NAME, "Attribute");
			putValue(SHORT_DESCRIPTION, "Click to add Attribute");
		}
		public void actionPerformed(ActionEvent e) {
			FrameAttribute frame = new FrameAttribute();
			frame.setVisible(true);
		}
	}
	
	
	private class SwingSave extends AbstractAction {
		public SwingSave() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Press here to save the node information.");
		}
		public void actionPerformed(ActionEvent e) {
			String name = ""; 
			mxGraphModel.instanceAddingGoingOn = true;
			name = textField.getText();
			String label = "";
			label = textField_2.getText();
			String className = classList.getSelectedItem().toString();
			
			classNInstanceName = className+"::"+name;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Following code will put info of the object to update the cells' name and label as per requirement/saved component
			
			File inputFile = new File(className);
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
				//Do something to keep everything in the Node Table 
				//(It will be done in recursive parser of xml and don't reinitiate the Node Table as done in next commented line)
//				System.out.println("\n\n... ... ... NodeTable is initialized ... ... ...\n");
				//StartingFrame.NodeTable = new HashMap<String, mxCell>();
				XMLParserIOOBN xmlParser = new XMLParserIOOBN();
				
				xmlParser.recursiveParserXML(nodeList, tagList, null);// initially mxCell is null
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String filename = className.replace(".class", ".ioobn");
			
			Document document;
			
			try {
				
				document = mxXmlUtils.parseXml(mxUtils.readFile(filename));
				mxCodec codec = new mxCodec(document);
				
				mxGraph newGraph = new mxGraph();
				codec.decode(document.getDocumentElement(), newGraph.getModel());
				
				Object[] newGroupableCells = newGraph.getChildCells(newGraph.getDefaultParent());

				
				InstanceAddingAction insAdd = new InstanceAddingAction("InstanceAdd", label, className, mxGraphComponent.g, newGroupableCells);
				
				insAdd.actionPerformed(e);
				InstanceAddingAction.instanceCount++;
				
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mxGraphModel.instanceAddingGoingOn = false;
			
			dispose();
		}
	}
	
	private class SwingCancel extends AbstractAction {
		public SwingCancel() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Press here to cancel.");
		}
		public void actionPerformed(ActionEvent e) {
				dispose();
		}
	}
}
