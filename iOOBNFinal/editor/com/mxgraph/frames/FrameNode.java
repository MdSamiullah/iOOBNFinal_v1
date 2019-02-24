package com.mxgraph.frames;

import java.awt.BorderLayout;
import com.mxgraph.model.mxCell;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JLayeredPane;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import javax.swing.JTable;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.editor.EditorActions.ExitAction;
import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import com.editor.components.Attribute;
import com.editor.components.BasicNode;

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
import java.awt.event.ActionListener;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.JInternalFrame;

public class FrameNode extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private int count;
	private int count1;
	
	private JPanel panel3;
	private JPanel panel2;
	private JScrollPane scrollPane_1;
	
	public Attribute attrib;
	public LinkedHashMap <String, String> attributes;
	public BasicNode node;
	
	public ArrayList<String> numdata;
	
	public mxCell cell;
	
	/**
	 * @wbp.nonvisual location=63,14
	 */
//	private final JLabel label = DefaultComponentFactory.getInstance().createTitle("New JGoodies title");
	public JTextField textField;
	public JTextField textField_2;
	public final ButtonGroup buttonGroup = new ButtonGroup();
	public final ButtonGroup buttonGroup_1 = new ButtonGroup();
	public final ButtonGroup buttonGroup_2 = new ButtonGroup();
	public final Action action = new SwingAttribute();
	public final Action action_1 = new SwingBelief();
	public final Action actionShowStateValue = new SwingStateValueDisplay();
	public final Action actionCancel = new SwingCancel();
	
//	public mxGraphComponent fmxGC;
	public Object obj;
	public String className;
	
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
	 */
	public FrameNode() {
		
		attrib = new Attribute();
		node = new BasicNode();
		attributes = new LinkedHashMap<String, String>();
		
		panel2 = new JPanel();
		
		BasicNode tempBN= new BasicNode();
		
		setTitle("Node Properties");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 537, 556);
		
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
		
		JMenuItem mntmNode = new JMenuItem("Belief");
		mntmNode.setAction(action_1);
		mnAdd.add(mntmNode);
		
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
		
		JLabel lblName = new JLabel("Node Name");
		lblName.setBounds(12, 25, 128, 16);
		desktopPane.add(lblName);
		
		textField = new JTextField();
		textField.setBounds(91, 22, 290, 22);
		desktopPane.add(textField);
		textField.setColumns(10);
		textField.setText(tempBN.name);
		
		JLabel lblNodeLabel = new JLabel("Node Label");
		lblNodeLabel.setBounds(12, 60, 128, 16);
		desktopPane.add(lblNodeLabel);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(91, 57, 290, 22);
		desktopPane.add(textField_2);
		textField_2.setText(tempBN.label);
		
		JLabel lblNodeStates = new JLabel("Node States");
		lblNodeStates.setBounds(12, 103, 81, 16);
		desktopPane.add(lblNodeStates);
		
		JLabel lblNodeSubtype = new JLabel("Node Type");
		lblNodeSubtype.setBounds(12, 250, 89, 16);
		desktopPane.add(lblNodeSubtype);
		
		JRadioButton rdbtnBoolean = new JRadioButton("Boolean");
		buttonGroup_2.add(rdbtnBoolean);
		rdbtnBoolean.setBounds(91, 246, 73, 25);
		desktopPane.add(rdbtnBoolean);
		if(tempBN.subType == null || tempBN.subType.equalsIgnoreCase("") || tempBN.subType.equalsIgnoreCase("Boolean")){
			rdbtnBoolean.setSelected(true);
		}
		
		JRadioButton rdbtnLabel = new JRadioButton("Label");
		buttonGroup_2.add(rdbtnLabel);
		rdbtnLabel.setBounds(170, 246, 69, 25);
		desktopPane.add(rdbtnLabel);
		
		if(tempBN.subType.equalsIgnoreCase("Label")){
			rdbtnLabel.setSelected(true);
		}
		
		// don't know whether it is working or not
		rdbtnLabel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
            	if(scrollPane_1 != null){
            		Component[] compList = scrollPane_1.getComponents();
            		for(Component c: compList){
            			scrollPane_1.remove(c);
            		}
            		scrollPane_1.revalidate();
            		scrollPane_1.repaint();
            	}
            }
        });

		
		JRadioButton rdbtnNumber = new JRadioButton("Number");
		buttonGroup_2.add(rdbtnNumber);
		rdbtnNumber.setBounds(243, 246, 73, 25);
		desktopPane.add(rdbtnNumber);
		
		if(tempBN.subType.equalsIgnoreCase("Number")){
			rdbtnNumber.setSelected(true);
			
        	count1 = 0;
        	
        	JLabel lblStateValues = new JLabel("State Values");
    		lblStateValues.setBounds(12, 300, 81, 16);
    		desktopPane.add(lblStateValues);
    		
    		JScrollPane scrollPane_1 = new JScrollPane();
    		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    		scrollPane_1.setBounds(97, 300, 284, 55);
    		desktopPane.add(scrollPane_1);
    		scrollPane_1.setToolTipText("add values");
    		scrollPane_1.setVerticalScrollBar(new JScrollBar());
    		scrollPane_1.setLayout(new ScrollPaneLayout());
    		
    		panel3 = new JPanel();
    		
    		panel3.setBackground(Color.LIGHT_GRAY);
    		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
    		scrollPane_1.setViewportView(panel3);
    		
    		JButton btnNewButton_1 = new JButton("Add Values");
    		btnNewButton_1.setBounds(386, 300, 97, 30);
    		desktopPane.add(btnNewButton_1);
    		
			for(int i=0; i < tempBN.stateValues.size(); i++){
				JTextArea tArea = new JTextArea();
                JTextField tfield = new JTextField();
                
                tArea.setText("Value# " + count1 + " : ");
                tArea.setName("Value" + count1);
                tArea.setBackground(Color.yellow);
                
                tfield.setName("Value " + count1);
                tfield.setText(tempBN.stateValues.get(i));
                
                count1++;
                panel3.add(tArea);
                panel3.add(tfield);
			}

    		btnNewButton_1.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    JTextArea tArea = new JTextArea();
                    JTextField tfield = new JTextField();
                    
                    tArea.setText("Value# " + count1 + " : ");
                    tArea.setName("Value" + count1);
                    tArea.setBackground(Color.yellow);
                    
                    tfield.setName("Value " + count1);
                    tfield.setText("Value" + count1);
                    
                    count1++;
                    panel3.add(tArea);
                    panel3.add(tfield);
                    
                    panel3.revalidate();  // For JDK 1.7 or above.
                    //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
                    panel3.repaint();
                }
            });
		}
		
		rdbtnNumber.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
            	count1 = 0;
            	
            	JLabel lblStateValues = new JLabel("State Values");
        		lblStateValues.setBounds(12, 300, 81, 16);
        		desktopPane.add(lblStateValues);
        		
        		JScrollPane scrollPane_1 = new JScrollPane();
        		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        		scrollPane_1.setBounds(97, 300, 284, 55);
        		desktopPane.add(scrollPane_1);
        		scrollPane_1.setToolTipText("add values");
        		scrollPane_1.setVerticalScrollBar(new JScrollBar());
        		scrollPane_1.setLayout(new ScrollPaneLayout());
        		
        		panel3 = new JPanel();        		
        		
        		panel3.setBackground(Color.LIGHT_GRAY);
        		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
        		scrollPane_1.setViewportView(panel3);
        		
        		JButton btnNewButton_1 = new JButton("Add Values");
        		btnNewButton_1.setBounds(386, 300, 97, 30);
        		desktopPane.add(btnNewButton_1);

        		btnNewButton_1.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent ae)
                    {
                        JTextArea tArea = new JTextArea();
                        JTextField tfield = new JTextField();
                        
                        tArea.setText("Value# " + count1 + " : ");
                        tArea.setName("Value" + count1);
                        tArea.setBackground(Color.yellow);
                        
                        tfield.setName("Value " + count1);
                        tfield.setText("Value" + count1);
                        
                        count1++;
                        panel3.add(tArea);
                        panel3.add(tfield);
                        
                        panel3.revalidate();  // For JDK 1.7 or above.
                        //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
                        panel3.repaint();
                    }
                });
            }
        });

		
		JRadioButton rdbtnInterval = new JRadioButton("Interval");
		buttonGroup_2.add(rdbtnInterval);
		rdbtnInterval.setBounds(320, 246, 73, 25);
		desktopPane.add(rdbtnInterval);
		
		if(tempBN.subType.equalsIgnoreCase("Interval")){
			rdbtnInterval.setSelected(true);
			
        	count1 = 0;
        	
        	JLabel lblStateValues = new JLabel("State Values");
    		lblStateValues.setBounds(12, 300, 81, 16);
    		desktopPane.add(lblStateValues);
    		
    		JScrollPane scrollPane_1 = new JScrollPane();
    		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    		scrollPane_1.setBounds(97, 300, 284, 55);
    		desktopPane.add(scrollPane_1);
    		scrollPane_1.setToolTipText("add values");
    		scrollPane_1.setVerticalScrollBar(new JScrollBar());
    		scrollPane_1.setLayout(new ScrollPaneLayout());
    		
    		panel3 = new JPanel();        		
    		
    		panel3.setBackground(Color.LIGHT_GRAY);
    		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
    		scrollPane_1.setViewportView(panel3);
    		
    		JButton btnNewButton_1 = new JButton("Add Values");
    		btnNewButton_1.setBounds(386, 300, 97, 30);
    		desktopPane.add(btnNewButton_1);
    		
			for(int i=0; i < tempBN.stateValues.size(); i++){
				JTextArea tArea = new JTextArea();
                JTextField tfield = new JTextField();
                
                tArea.setText("Value# " + count1 + " : ");
                tArea.setName("Value" + count1);
                tArea.setBackground(Color.yellow);
                
                tfield.setName("Value " + count1);
                tfield.setText(tempBN.stateValues.get(i));
                
                count1++;
                panel3.add(tArea);
                panel3.add(tfield);
			}

    		btnNewButton_1.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    JTextArea tArea = new JTextArea();
                    JTextField tfield = new JTextField();
                    
                    tArea.setText("Value# " + count1 + " : ");
                    tArea.setName("Value" + count1);
                    tArea.setBackground(Color.yellow);
                    
                    tfield.setName("Value " + count1);
                    tfield.setText("Value" + count1);
                    
                    count1++;
                    panel3.add(tArea);
                    panel3.add(tfield);
                    
                    panel3.revalidate();  // For JDK 1.7 or above.
                    //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
                    panel3.repaint();
                }
            });
		}

		
		rdbtnInterval.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
            	count1 = 0;
            	
            	JLabel lblStateValues = new JLabel("State Values");
        		lblStateValues.setBounds(12, 300, 81, 16);
        		desktopPane.add(lblStateValues);
        		
        		scrollPane_1 = new JScrollPane();
        		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        		scrollPane_1.setBounds(97, 300, 284, 55);
        		desktopPane.add(scrollPane_1);
        		scrollPane_1.setToolTipText("add values");
        		scrollPane_1.setVerticalScrollBar(new JScrollBar());
        		scrollPane_1.setLayout(new ScrollPaneLayout());
        		
//        		panel3.removeAll();
//        		panel3.revalidate();
//        		panel3.repaint();
        		panel3 = new JPanel();
        		
        		panel3.setBackground(Color.LIGHT_GRAY);
        		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
        		scrollPane_1.setViewportView(panel3);
        		
        		JButton btnNewButton_1 = new JButton("Add Values");
        		btnNewButton_1.setBounds(386, 300, 97, 30);
        		desktopPane.add(btnNewButton_1);

        		btnNewButton_1.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent ae)
                    {
                        JTextArea tArea = new JTextArea();
                        JTextField tfield = new JTextField();
                        
                        tArea.setText("Value# " + count1 + " : ");
                        tArea.setName("Value" + count1);
                        tArea.setBackground(Color.yellow);
                        
                        tfield.setName("Value " + count1);
                        tfield.setText("Value" + count1);
                        
                        count1++;
                        panel3.add(tArea);
                        panel3.add(tfield);
                        
                        panel3.revalidate();  // For JDK 1.7 or above.
                        //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
                        panel3.repaint();
                    }
                });
            }
        });

		
		
		JButton btnNewButton = new JButton("Cancel");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(294, 400, 97, 25);
		desktopPane.add(btnNewButton);
		btnNewButton.setAction(actionCancel);
		
		JButton button = new JButton("Save");
		button.setBounds(113, 400, 97, 25);
		desktopPane.add(button);
		button.setAction(action_2);
		
		JPanel panel = new JPanel();
		panel.setBounds(334, 92, -31, 26);
		desktopPane.add(panel);
		
		JScrollPane scrollPane = new JScrollPane();
		desktopPane.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(91, 103, 290, 70);
		scrollPane.setToolTipText("add states");
		scrollPane.setVerticalScrollBar(new JScrollBar());
		scrollPane.setLayout(new ScrollPaneLayout());
		
		panel2.setBackground(Color.LIGHT_GRAY);
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(panel2);
		
		JButton addButton = new JButton("Add States");
		desktopPane.add(addButton);
		addButton.setBounds(91, 186, 106, 30);
		
		for(int j = 0; j < tempBN.states.size(); j++){
			JTextArea tArea = new JTextArea();
            JTextField tfield = new JTextField();
            
            tArea.setText("State# " + count + " : ");
            tArea.setName("Label" + count);
            tArea.setBackground(Color.yellow);
            
            tfield.setName("State " + count);
            tfield.setText(tempBN.states.get(j));
            
            count++;
            panel2.add(tArea);
            panel2.add(tfield);
		}
						
		addButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                JTextArea tArea = new JTextArea();
                JTextField tfield = new JTextField();
                
                tArea.setText("State# " + count + " : ");
                tArea.setName("Label" + count);
                tArea.setBackground(Color.yellow);
                
                tfield.setName("State " + count);
                tfield.setText("State" + count);
                
                count++;
                panel2.add(tArea);
                panel2.add(tfield);
                
                panel2.revalidate();  // For JDK 1.7 or above.
                //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
                panel2.repaint();
            }
        });	
	}

	public FrameNode(mxCell currCell) 
	{	
		attrib = new Attribute();
		node = new BasicNode();
		attributes = new LinkedHashMap<String, String>();
		
		this.cell = currCell;
		
		if(this.cell != null)
		{
			BasicNode currNode = this.cell.getBNInfo();
			
			panel2 = new JPanel();
			
			setTitle("Node Properties");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 537, 556);
			
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
			
			JMenuItem mntmNode = new JMenuItem("Belief");
			mntmNode.setAction(action_1);
			mnAdd.add(mntmNode);
			
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
			
			JLabel lblName = new JLabel("Node Name");
			lblName.setBounds(12, 25, 128, 16);
			desktopPane.add(lblName);
			
			textField = new JTextField();
			textField.setBounds(91, 22, 290, 22);
			desktopPane.add(textField);
			textField.setColumns(10);
			textField.setText(currNode.name);
			
			JLabel lblNodeLabel = new JLabel("Node Label");
			lblNodeLabel.setBounds(12, 60, 128, 16);
			desktopPane.add(lblNodeLabel);
			
			textField_2 = new JTextField();
			textField_2.setColumns(10);
			textField_2.setBounds(91, 57, 290, 22);
			desktopPane.add(textField_2);
			textField_2.setText(currNode.label);
			
			JLabel lblNodeStates = new JLabel("Node States");
			lblNodeStates.setBounds(12, 103, 81, 16);
			desktopPane.add(lblNodeStates);
			
			JLabel lblNodeSubtype = new JLabel("Node Type");
			lblNodeSubtype.setBounds(12, 250, 89, 16);
			desktopPane.add(lblNodeSubtype);
			
			JRadioButton rdbtnBoolean = new JRadioButton("Boolean");
			buttonGroup_2.add(rdbtnBoolean);
			rdbtnBoolean.setBounds(91, 246, 73, 25);
			desktopPane.add(rdbtnBoolean);
			if(currNode.subType == null || currNode.subType.equalsIgnoreCase("") || currNode.subType.equalsIgnoreCase("Boolean")){
				rdbtnBoolean.setSelected(true);
			}
			
			JRadioButton rdbtnLabel = new JRadioButton("Label");
			buttonGroup_2.add(rdbtnLabel);
			rdbtnLabel.setBounds(170, 246, 69, 25);
			desktopPane.add(rdbtnLabel);
			
			if(currNode.subType.equalsIgnoreCase("Label")){
				rdbtnLabel.setSelected(true);
			}
			
			// don't know whether it is working or not
			rdbtnLabel.addActionListener(new ActionListener()
	        {
	            @Override
	            public void actionPerformed(ActionEvent ae)
	            {
	            	if(scrollPane_1 != null){
	            		Component[] compList = scrollPane_1.getComponents();
	            		for(Component c: compList){
	            			scrollPane_1.remove(c);
	            		}
	            		scrollPane_1.revalidate();
	            		scrollPane_1.repaint();
	            	}
	            }
	        });
	
			
			JRadioButton rdbtnNumber = new JRadioButton("Number");
			buttonGroup_2.add(rdbtnNumber);
			rdbtnNumber.setBounds(243, 246, 73, 25);
			desktopPane.add(rdbtnNumber);
			
			if(currNode.subType.equalsIgnoreCase("Number")){
				rdbtnNumber.setSelected(true);
				
	        	count1 = 0;
	        	
	        	JLabel lblStateValues = new JLabel("State Values");
	    		lblStateValues.setBounds(12, 300, 81, 16);
	    		desktopPane.add(lblStateValues);
	    		
	    		JScrollPane scrollPane_1 = new JScrollPane();
	    		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    		scrollPane_1.setBounds(97, 300, 284, 55);
	    		desktopPane.add(scrollPane_1);
	    		scrollPane_1.setToolTipText("add values");
	    		scrollPane_1.setVerticalScrollBar(new JScrollBar());
	    		scrollPane_1.setLayout(new ScrollPaneLayout());
	    		
	    		panel3 = new JPanel();
	    		
	    		panel3.setBackground(Color.LIGHT_GRAY);
	    		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
	    		scrollPane_1.setViewportView(panel3);
	    		
	    		JButton btnNewButton_1 = new JButton("Add Values");
	    		btnNewButton_1.setBounds(386, 300, 97, 30);
	    		desktopPane.add(btnNewButton_1);
	    		
				for(int i=0; i < currNode.stateValues.size(); i++){
					JTextArea tArea = new JTextArea();
	                JTextField tfield = new JTextField();
	                
	                tArea.setText("Value# " + count1 + " : ");
	                tArea.setName("Value" + count1);
	                tArea.setBackground(Color.yellow);
	                
	                tfield.setName("Value " + count1);
	                tfield.setText(currNode.stateValues.get(i));
	                
	                count1++;
	                panel3.add(tArea);
	                panel3.add(tfield);
				}
	
	    		btnNewButton_1.addActionListener(new ActionListener()
	            {
	                @Override
	                public void actionPerformed(ActionEvent ae)
	                {
	                    JTextArea tArea = new JTextArea();
	                    JTextField tfield = new JTextField();
	                    
	                    tArea.setText("Value# " + count1 + " : ");
	                    tArea.setName("Value" + count1);
	                    tArea.setBackground(Color.yellow);
	                    
	                    tfield.setName("Value " + count1);
	                    tfield.setText("Value" + count1);
	                    
	                    count1++;
	                    panel3.add(tArea);
	                    panel3.add(tfield);
	                    
	                    panel3.revalidate();  // For JDK 1.7 or above.
	                    //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
	                    panel3.repaint();
	                }
	            });
			}
			
			rdbtnNumber.addActionListener(new ActionListener()
	        {
	            @Override
	            public void actionPerformed(ActionEvent ae)
	            {
	            	count1 = 0;
	            	
	            	JLabel lblStateValues = new JLabel("State Values");
	        		lblStateValues.setBounds(12, 300, 81, 16);
	        		desktopPane.add(lblStateValues);
	        		
	        		JScrollPane scrollPane_1 = new JScrollPane();
	        		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	        		scrollPane_1.setBounds(97, 300, 284, 55);
	        		desktopPane.add(scrollPane_1);
	        		scrollPane_1.setToolTipText("add values");
	        		scrollPane_1.setVerticalScrollBar(new JScrollBar());
	        		scrollPane_1.setLayout(new ScrollPaneLayout());
	        		
	        		panel3 = new JPanel();        		
	        		
	        		panel3.setBackground(Color.LIGHT_GRAY);
	        		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
	        		scrollPane_1.setViewportView(panel3);
	        		
	        		JButton btnNewButton_1 = new JButton("Add Values");
	        		btnNewButton_1.setBounds(386, 300, 97, 30);
	        		desktopPane.add(btnNewButton_1);
	
	        		btnNewButton_1.addActionListener(new ActionListener()
	                {
	                    @Override
	                    public void actionPerformed(ActionEvent ae)
	                    {
	                        JTextArea tArea = new JTextArea();
	                        JTextField tfield = new JTextField();
	                        
	                        tArea.setText("Value# " + count1 + " : ");
	                        tArea.setName("Value" + count1);
	                        tArea.setBackground(Color.yellow);
	                        
	                        tfield.setName("Value " + count1);
	                        tfield.setText("Value" + count1);
	                        
	                        count1++;
	                        panel3.add(tArea);
	                        panel3.add(tfield);
	                        
	                        panel3.revalidate();  // For JDK 1.7 or above.
	                        //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
	                        panel3.repaint();
	                    }
	                });
	            }
	        });
	
			
			JRadioButton rdbtnInterval = new JRadioButton("Interval");
			buttonGroup_2.add(rdbtnInterval);
			rdbtnInterval.setBounds(320, 246, 73, 25);
			desktopPane.add(rdbtnInterval);
			
			if(currNode.subType.equalsIgnoreCase("Interval")){
				rdbtnInterval.setSelected(true);
				
	        	count1 = 0;
	        	
	        	JLabel lblStateValues = new JLabel("State Values");
	    		lblStateValues.setBounds(12, 300, 81, 16);
	    		desktopPane.add(lblStateValues);
	    		
	    		JScrollPane scrollPane_1 = new JScrollPane();
	    		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    		scrollPane_1.setBounds(97, 300, 284, 55);
	    		desktopPane.add(scrollPane_1);
	    		scrollPane_1.setToolTipText("add values");
	    		scrollPane_1.setVerticalScrollBar(new JScrollBar());
	    		scrollPane_1.setLayout(new ScrollPaneLayout());
	    		
	    		panel3 = new JPanel();        		
	    		
	    		panel3.setBackground(Color.LIGHT_GRAY);
	    		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
	    		scrollPane_1.setViewportView(panel3);
	    		
	    		JButton btnNewButton_1 = new JButton("Add Values");
	    		btnNewButton_1.setBounds(386, 300, 97, 30);
	    		desktopPane.add(btnNewButton_1);
	    		
				for(int i=0; i < currNode.stateValues.size(); i++){
					JTextArea tArea = new JTextArea();
	                JTextField tfield = new JTextField();
	                
	                tArea.setText("Value# " + count1 + " : ");
	                tArea.setName("Value" + count1);
	                tArea.setBackground(Color.yellow);
	                
	                tfield.setName("Value " + count1);
	                tfield.setText(currNode.stateValues.get(i));
	                
	                count1++;
	                panel3.add(tArea);
	                panel3.add(tfield);
				}
	
	    		btnNewButton_1.addActionListener(new ActionListener()
	            {
	                @Override
	                public void actionPerformed(ActionEvent ae)
	                {
	                    JTextArea tArea = new JTextArea();
	                    JTextField tfield = new JTextField();
	                    
	                    tArea.setText("Value# " + count1 + " : ");
	                    tArea.setName("Value" + count1);
	                    tArea.setBackground(Color.yellow);
	                    
	                    tfield.setName("Value " + count1);
	                    tfield.setText("Value" + count1);
	                    
	                    count1++;
	                    panel3.add(tArea);
	                    panel3.add(tfield);
	                    
	                    panel3.revalidate();  // For JDK 1.7 or above.
	                    //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
	                    panel3.repaint();
	                }
	            });
			}
	
			
			rdbtnInterval.addActionListener(new ActionListener()
	        {
	            @Override
	            public void actionPerformed(ActionEvent ae)
	            {
	            	count1 = 0;
	            	
	            	JLabel lblStateValues = new JLabel("State Values");
	        		lblStateValues.setBounds(12, 300, 81, 16);
	        		desktopPane.add(lblStateValues);
	        		
	        		scrollPane_1 = new JScrollPane();
	        		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	        		scrollPane_1.setBounds(97, 300, 284, 55);
	        		desktopPane.add(scrollPane_1);
	        		scrollPane_1.setToolTipText("add values");
	        		scrollPane_1.setVerticalScrollBar(new JScrollBar());
	        		scrollPane_1.setLayout(new ScrollPaneLayout());
	        		
	//        		panel3.removeAll();
	//        		panel3.revalidate();
	//        		panel3.repaint();
	        		panel3 = new JPanel();
	        		
	        		panel3.setBackground(Color.LIGHT_GRAY);
	        		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
	        		scrollPane_1.setViewportView(panel3);
	        		
	        		JButton btnNewButton_1 = new JButton("Add Values");
	        		btnNewButton_1.setBounds(386, 300, 97, 30);
	        		desktopPane.add(btnNewButton_1);
	
	        		btnNewButton_1.addActionListener(new ActionListener()
	                {
	                    @Override
	                    public void actionPerformed(ActionEvent ae)
	                    {
	                        JTextArea tArea = new JTextArea();
	                        JTextField tfield = new JTextField();
	                        
	                        tArea.setText("Value# " + count1 + " : ");
	                        tArea.setName("Value" + count1);
	                        tArea.setBackground(Color.yellow);
	                        
	                        tfield.setName("Value " + count1);
	                        tfield.setText("Value" + count1);
	                        
	                        count1++;
	                        panel3.add(tArea);
	                        panel3.add(tfield);
	                        
	                        panel3.revalidate();  // For JDK 1.7 or above.
	                        //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
	                        panel3.repaint();
	                    }
	                });
	            }
	        });
	
			
			
			JButton btnNewButton = new JButton("Cancel");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
				}
			});
			btnNewButton.setBounds(294, 400, 97, 25);
			desktopPane.add(btnNewButton);
			btnNewButton.setAction(actionCancel);
			
			JButton button = new JButton("Save");
			button.setBounds(113, 400, 97, 25);
			desktopPane.add(button);
			button.setAction(action_2);
			
			JPanel panel = new JPanel();
			panel.setBounds(334, 92, -31, 26);
			desktopPane.add(panel);
			
			JScrollPane scrollPane = new JScrollPane();
			desktopPane.add(scrollPane);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setBounds(91, 103, 290, 70);
			scrollPane.setToolTipText("add states");
			scrollPane.setVerticalScrollBar(new JScrollBar());
			scrollPane.setLayout(new ScrollPaneLayout());
			
			panel2.setBackground(Color.LIGHT_GRAY);
			panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
			scrollPane.setViewportView(panel2);
			
			JButton addButton = new JButton("Add States");
			desktopPane.add(addButton);
			addButton.setBounds(91, 186, 106, 30);
			
			for(int j = 0; j < currNode.states.size(); j++){
				JTextArea tArea = new JTextArea();
	            JTextField tfield = new JTextField();
	            
	            tArea.setText("State# " + count + " : ");
	            tArea.setName("Label" + count);
	            tArea.setBackground(Color.yellow);
	            
	            tfield.setName("State " + count);
	            tfield.setText(currNode.states.get(j));
	            
	            count++;
	            panel2.add(tArea);
	            panel2.add(tfield);
			}
							
			addButton.addActionListener(new ActionListener()
	        {
	            @Override
	            public void actionPerformed(ActionEvent ae)
	            {
	                JTextArea tArea = new JTextArea();
	                JTextField tfield = new JTextField();
	                
	                tArea.setText("State# " + count + " : ");
	                tArea.setName("Label" + count);
	                tArea.setBackground(Color.yellow);
	                
	                tfield.setName("State " + count);
	                tfield.setText("State" + count);
	                
	                count++;
	                panel2.add(tArea);
	                panel2.add(tfield);
	                
	                panel2.revalidate();  // For JDK 1.7 or above.
	                //frame.getContentPane().revalidate(); // For JDK 1.6 or below.
	                panel2.repaint();
	            }
	        });	
		}
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
	
	private class SwingStateValueDisplay extends AbstractAction {
		public SwingStateValueDisplay() {
			putValue(NAME, "Display STate Information");
			putValue(SHORT_DESCRIPTION, "");
		}
		public void actionPerformed(ActionEvent e) {
			
		}
			
	}
	
	private class SwingBelief extends AbstractAction {
		public SwingBelief() {
			putValue(NAME, "Belief");
			putValue(SHORT_DESCRIPTION, "Click to add belief");
		}
		public void actionPerformed(ActionEvent e) {
			// make here array list for making data table
			SwingSave swSave = new SwingSave();
			swSave.actionPerformed(e);
			
	        ArrayList<ArrayList<String>> parentStates = new ArrayList<ArrayList<String>>();
//	        String selectedCellId = ((mxCell) cell).getId();
//	        BasicNode currNode = StartingFrame.NodeTable.get(selectedCellId);
//	        BasicNode currNode = ((mxCell) mxGraphComponent.cell).getBNInfo();
	        BasicNode currNode = getCell().getBNInfo();
	        ArrayList<String> currNodeStates = currNode.states;
	        
	        ArrayList<String> parentNodes = new ArrayList<String>();
	        
//	        BasicGraphEditor editor = EditorActions.getEditor(e);
//	        if(editor != null){
//	        	System.out.println("\nEditor found");
//	        	
//	        }else System.out.println("Null editor");
	        
	        System.out.println("Parents in FrameNode.java ");
	        for(String key: currNode.parents){
	        	System.out.println(key);
	        }
	        
	        for(int I = 0; I < currNode.parents.size(); I++){
	        	// though I have to check why self node ID is added as parent id but for now I have used the below checking
//	        	String selfId = ((mxCell) mxGraphComponent.cell).getId();
	        	String selfId = getCell().getId();
	        	String parKey = currNode.parents.get(I);
//	        	if(!selfId.equalsIgnoreCase(parKey))
//	        	{
	        	System.out.println("ID of parents in FrameNode.java" + parKey + "Containing keys in Node Table ");
	        	for(String key: StartingFrame.NodeTable.keySet())
	        		System.out.println(key);
	        	if(StartingFrame.NodeTable.containsKey(parKey)){
	        		BasicNode tempParentNode = StartingFrame.NodeTable.get(parKey).getBNInfo();
	        	
		        	parentStates.add(tempParentNode.states);
		        	parentNodes.add(tempParentNode.label);
	        	}
	        	
	//	        	else{// this will delete a parent node that doesn't exist anymore (may be deleted)
	//	        		// since I have taken action of deleted parents in mxGraphModel.java
	//	        		StartingFrame.NodeTable.get(mxGraphComponent.selectedCellID).parents.remove(currNode.parents.get(I));
	//	        		
	//	        	}
//	        	}
	        }
	        System.out.println(parentNodes);
			FrameTable frame = new FrameTable();
			frame.setTitle("CPT of " + currNode.label);
//			frame.createAndShowGUI(frame, ((mxCell) mxGraphComponent.cell).getId(), currNodeStates, parentNodes, parentStates, currNode.data);
			frame.createAndShowGUI(frame, getCell(), getCell().getId(), currNodeStates, parentNodes, 
					parentStates, currNode.data);
		}
	}
	private class SwingSave extends AbstractAction {
		public SwingSave() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Press here to save the node information.");
		}
		public void actionPerformed(ActionEvent e) {
			//fmxGC = new mxGraphComponent(new mxGraph());
			String name = textField.getText();
			String label = textField_2.getText();
//			String type = buttonGroup_2.getSelection().toString();// Boolean/Label/Number/Interval
			String subType = null;
			  
		    for (Enumeration<AbstractButton> buttons = buttonGroup_2.getElements(); buttons.hasMoreElements();) {
		            AbstractButton button = buttons.nextElement();
		            if (button.isSelected()) {
		            	subType = button.getText();
		            }
		        }

			ArrayList<String> states = new ArrayList<String>();
			for(Component c: panel2.getComponents()){
				if(c instanceof JTextField){
					states.add(((JTextField) c).getText());
				}
			}
			
			ArrayList<String> stateValues = null;
			if(subType.equalsIgnoreCase("Number") || subType.equalsIgnoreCase("Interval")){
				stateValues = new ArrayList<String>();
				for(Component c: panel3.getComponents()){
					if(c instanceof JTextField){
						stateValues.add(((JTextField) c).getText());
					}
				}
			}
			
//			String prevLabel = getCell().getValue().toString();
			// before the following line cell contains the previous
			// label for a node hence this can be used to update node name in the hash map
			// however, we have to change the name not label. So I used static name string in mxGraphComponent  
			((mxCell) mxGraphComponent.cell).setValue(label);// if you block or delete this line then update of label will not be performed in GUI
			getCell().setValue(label);
			
			// at the moment the type is only discrete in consideration
			String type = "Discrete";
			
			//updating node table as per the changes done in frame node
			String selectedCellID = getCell().getId();
			getCell().getBNInfo().setBasicNodeInfo(name, label, type, -1, -1, null, states, subType, null, stateValues, null, false, -1);
//			StartingFrame.NodeTable.get(selectedCellID).getBNInfo().setBasicNodeInfo(name, label, type, -1, -1, null, states, subType, null, stateValues, null, false);
			StartingFrame.NodeTable.put(selectedCellID, getCell());
			
			// update in the display of node as per changes done in frame node
			mxGraphComponent.postNodeInfo(name, label, type, states, stateValues);
			mxGraphComponent.g.repaint();
			mxGraphComponent.g.refresh();
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
