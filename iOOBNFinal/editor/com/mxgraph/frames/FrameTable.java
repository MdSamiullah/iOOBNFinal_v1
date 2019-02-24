package com.mxgraph.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.mxgraph.iOOBNEditor.StartingFrame;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;

import com.editor.components.BasicNode;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class FrameTable extends JFrame {

	private JPanel contentPane;
	
    public int NumOfParents = 0;
	private boolean DEBUG = false;
	
	public Object[][] data;
	
	public ArrayList<String> numdata;
	public TableModel model;
	
	public FrameNode fNd;
	public Object obj;
	public String className;
	
	public String selectedCellId;
	public mxCell cell;
	
//	public static ArrayList<String> selfStates;
	public static ArrayList<String> parentsStatic;
	public static ArrayList<ArrayList<String>> parentStatesStatic;
	public static ArrayList<Integer> statesCountStatic;
	
	private final Action action = new SwingSave();
	
	
	public void setCell(mxCell cell)
	{
		this.cell = cell;
	}
	
	public mxCell getCell()
	{
		return this.cell;
	}
	
    /**
     * @wbp.parser.constructor
     */
    public void FrameTableBuilder(String[] columnNames, mxCell childNode, String childNodeId, ArrayList<String> childStates, 
    				ArrayList<String> parents,	ArrayList<ArrayList<String>> parentStates, TableModel prevModel) 
    {	
//    	System.out.println("Prev. Model " + prevModel + " parentChanged? " + childNode.getBNInfo().parentChanged);
    	if(prevModel == null || StartingFrame.NodeTable.get(childNodeId).getBNInfo().parentChanged || childNode.getBNInfo().parentChanged)////////////////
    	{
    		StartingFrame.NodeTable.get(childNodeId).getBNInfo().parentChanged = false;
    		childNode.getBNInfo().parentChanged = false;
    		
	        NumOfParents = parents.size();
	        parentsStatic = parents;
	        parentStatesStatic = parentStates;
	        
	        statesCountStatic = new ArrayList<Integer>();
	        
	        int tempStateCount = 1;
	        for(int i = 0; i < parentStates.size(); i++){
	        	tempStateCount *= parentStates.get(i).size();
	        	statesCountStatic.add((Integer)tempStateCount);
	        }
	        	
	        int numOfRow = childStates.size()+parents.size();
	        int numOfCol = 1;
	        if(parents.size()>0){
		        for(int I = 0; I <parentStates.size(); I++)
		        		numOfCol *= parentStates.get(I).size();
	        }
	        numOfCol++;
	        data = new Object[numOfRow][numOfCol];
	        //System.out.println(numOfRow + " " +numOfCol );
	        // make rows for parents other than 1st parent, which is already added in header row by "columnNames"
	        int K = 0;
	        for(K = 1; K < parents.size(); K++){
	        	data[K-1][0] = parents.get(K);// name of first (any) parent
	        	int step = 1;
	        	for(int I = K+1; I< parents.size(); I++)	step *= parentStates.get(I).size();
	        	int J = -1;
	            for(int I = 1; I < numOfCol; I += step){
	            	J++;
	            // this loop adds in 1st row/header the name of 1st parent and its states with required spaces
	            	data[K-1][I] = parentStates.get(K).get((J%parentStates.get(K).size()));// remaining will be blank in 1st header row  
	            }
	        }
	        
	        // now make rows for each state of child node with 1st cell as state name and others as blank for data
	        if(parents.size()<1) K = 0;
	        for(int J = 0; J <childStates.size(); J++){
	        	data[K+J][0] = childStates.get(J);
	        }
	        
	        //System.out.println(parents.size());
	        
	        model = new DefaultTableModel(data, columnNames)
	        {
	          public boolean isCellEditable(int row, int column)
	          {
	        	  if(row < parents.size())
	        		  return false;//This causes all cells to be not editable
	        	  else return true;
	          }
	          public void cellBackground(int row, int column){
	        	  
	          }
	        };
	        
	        for (int I = 1; I < numOfCol; I++){
		        for(int J = NumOfParents; J < numOfRow; J++){
						model.setValueAt(Double.toString(1.0/childStates.size()), J, I);
				}
	        }
	        
    	}
    	else if (prevModel != null)
    	{ 
    		NumOfParents = parents.size();
	        parentsStatic = parents;
	        parentStatesStatic = parentStates;
    		model = prevModel;
    		NumOfParents = parents.size();
    		statesCountStatic = new ArrayList<Integer>();
	        
	        int tempStateCount = 1;
	        for(int i = 0; i < parentStates.size(); i++){
	        	tempStateCount *= parentStates.get(i).size();
	        	statesCountStatic.add((Integer)tempStateCount);
	        }
    		System.out.println("I got prev data");
    	}
        
        final JTable table = new JTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
//        table.setEditingRow(0);
        table.setFillsViewportHeight(true);
        ColorRenderer bgRenderer = new ColorRenderer(parents.size());
        table.setDefaultRenderer(Object.class, bgRenderer);
 
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
 
        //Add the scroll pane to this panel.
        getContentPane().add(scrollPane);
        
        //System.out.println("Table " + table + "\nModel " + model);
    }

	/**
	 * Launch the application.
	 */
	public void createAndShowGUI(JFrame frame, mxCell cell, String childId, ArrayList<String> childStates, 
			ArrayList<String> parents, ArrayList<ArrayList<String>> parentStates, TableModel prevModel) {
        //Create and set up the window.
        
		setCell(cell);
		
        int numOfRow = childStates.size()+parents.size();
        int numOfCol = 1;
        for(int I = 0; I <parentStates.size(); I++)
        		numOfCol *= parentStates.get(I).size();
        
        numOfCol++;
        String[] columnNames = new String[numOfCol];
        Arrays.fill(columnNames, "");
        
        int step;
        // making first column
        if(parents.size()>0){
	        columnNames[0] = parents.get(0);// name of first (any) parent
	        step = numOfCol / parentStates.get(0).size();
	        
	        int J = -1;
	        for(int I = 1; I < numOfCol; I += step){
	        	J++; 
	        	columnNames[I] = parentStates.get(0).get(J%parentStates.get(0).size());// remaining will be blank in 1st header row
	        }
        }
        //Create and set up the content pane.
        FrameTableBuilder(columnNames, cell, childId, childStates, parents, parentStates, prevModel);

        frame.setOpacity((float) 1.0);
        frame.pack();
        frame.setVisible(true);
    }
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ArrayList<String> childStates = new ArrayList<String>();
			        ArrayList<String> parents = new ArrayList<String>();
			        ArrayList<ArrayList<String>> parentStates = new ArrayList<ArrayList<String>>();
			        String child = "P";
			        
			        childStates.add("P1");	childStates.add("P2");
			        parents.add("M");	parents.add("N");	parents.add("O");
			        ArrayList<String> Par1 = new ArrayList<String>();
			        ArrayList<String> Par2 = new ArrayList<String>();
			        ArrayList<String> Par3 = new ArrayList<String>();
			        Par1.add("M1"); 	Par1.add("M2");		Par1.add("M3");
			        Par2.add("N1"); 	Par2.add("N2");
			        Par3.add("O1"); 	Par3.add("O2");
			        parentStates.add(Par1);
			        parentStates.add(Par2);
			        parentStates.add(Par3);
			        
					FrameTable frame = new FrameTable();
					frame.createAndShowGUI(frame, null, child, childStates, parents, parentStates, null);
//					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FrameTable() {
//		setTitle("CPT");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 470, 486);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Options");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnNewMenu.add(mntmSave);
		mntmSave.setAction(action);
	}
	
	public String showDataParenthesized(TableModel model){
		// row starts from 1 and column from NumOfParents in the beliefTable
		System.out.println("Parenthesized format");
		
		String parenthesizedData = "";
		// run column major format then start counting of cells. 
		// If counter is divisible by 1, then use all parents starting parenthesis 
		// say, Child has 3, P1 has 2, P2 has 2 and P3 has 4 states, then for each 
		// total cell = 3x4x2x2 = 48, if divisible by 3 then start parenthesis for Child
		// if divisible by 12 and divisible by 24. These numbers can be found in staticStateCounts array
		// statesCountStatic = 4, 8, 16
		String tempDataCol = "";
		String tab = "";
//		System.out.println(statesCountStatic);
		for (int I = 1; I < model.getColumnCount(); I++){
			
			for(int k = 0; k < parentsStatic.size(); k++){
				tab = new String(new char[k]).replace("\0", "\t");// for K = 0, i.e 1st parent will have no braces
				if((I-1)%statesCountStatic.get(parentsStatic.size()-k-1) == 0){
					tempDataCol += (tab+"{\n");
				}
			}
			tab = new String(new char[parentsStatic.size()]).replace("\0", "\t");
			tempDataCol += (tab+"{");
			
			for(int J = NumOfParents; J < model.getRowCount(); J++){
	              tempDataCol += model.getValueAt(J, I).toString();
	              tempDataCol += " ";
			}
			tempDataCol += "}\n";
			
			for(int k = 0; k < parentsStatic.size(); k++){
				tab = new String(new char[parentsStatic.size()-k-1]).replace("\0", "\t");// for K = 0, i.e last parent will have braces after (numPar -1) tab
				if((I)%statesCountStatic.get(k) == 0){
					tempDataCol += (tab+"}\n");
				}
			}
			
        }
		
		return tempDataCol;
	}
	
	public String showDataColumnMajor(TableModel model){
		System.out.println("Column major:");
		String tempDataCol = "";
		for (int I = 1; I < model.getColumnCount(); I++){
			tempDataCol = "{";
			
			for(int J = NumOfParents; J < model.getRowCount(); J++){
				if(model.getValueAt(J, I) != null){
	              numdata.add(model.getValueAt(J, I).toString());
	              tempDataCol += model.getValueAt(J, I).toString();
	              tempDataCol += " ";
				}
				else{ 
					numdata.add("0.5");
					tempDataCol += "0.5";
					tempDataCol += " ";
				}
			}
			tempDataCol += "}";
			System.out.println(tempDataCol);
        }
		return tempDataCol;
	}
	
	public String showDataRowMajor(TableModel model){
		String tempDataRow = "";
		System.out.println("Row major:");
		for (int I = NumOfParents; I < model.getRowCount(); I++){
			for(int J = 1; J < model.getColumnCount(); J++){
				if(model.getValueAt(I, J) != null){
	              numdata.add(model.getValueAt(I, J).toString());
	              tempDataRow += model.getValueAt(I, J).toString();
	              tempDataRow += " ";
				}
				else{ 
					numdata.add("0.5");
					tempDataRow += "0.5";
		            tempDataRow += " ";
				}
				tempDataRow += "\n";
			}
			System.out.println(tempDataRow);
        }
		return tempDataRow;
	}
	
	private class SwingSave extends AbstractAction {
		public SwingSave() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Press here to save the node information.");
		}
		public void actionPerformed(ActionEvent e) {
			
			getCell().getBNInfo().data = model;
			String key = getCell().getId();
			if(StartingFrame.NodeTable.containsKey(key))
			{
				StartingFrame.NodeTable.get(key).getBNInfo().parentChanged = false;
				StartingFrame.NodeTable.get(key).getBNInfo().data = model;
			}

			String tempDataCol = showDataParenthesized(getCell().getBNInfo().data);
			
			System.out.println(tempDataCol);
			dispose();
		}
	}

}
