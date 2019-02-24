package com.mxgraph.frames;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ListSelectionModel;
import javax.swing.JMenuBar;
import java.awt.Color;

public class TableFormat extends JPanel {
    public static int NumOfParents = 0;
	private boolean DEBUG = false;
    /**
     * @wbp.parser.constructor
     */
    public TableFormat(String[] columnNames, String childNodeName, ArrayList<String> childStates, ArrayList<String> parents, ArrayList<ArrayList<String>> parentStates) {
        super(new GridLayout(1,0));
 
        NumOfParents = parents.size();
        int numOfRow = childStates.size()+parents.size();
        int numOfCol = 1;
        for(int I = 0; I <parentStates.size(); I++)
        		numOfCol *= parentStates.get(I).size();
        numOfCol++;
        Object[][] data = new Object[numOfRow][numOfCol];
        System.out.println(numOfRow + " " +numOfCol );
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
        for(int J = 0; J <childStates.size(); J++){
        	data[K+J][0] = childStates.get(J);
        }
        
        TableModel model = new DefaultTableModel(data, columnNames)
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
        
//        final JTable table = new JTable(data, columnNames);
        final JTable table = new JTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
//        table.setEditingRow(0);
        table.setFillsViewportHeight(true);
        ColorRenderer bgRenderer = new ColorRenderer(NumOfParents);
        table.setDefaultRenderer(Object.class, bgRenderer);
 
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
 
        //Add the scroll pane to this panel.
        add(scrollPane);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        scrollPane.setColumnHeaderView(menuBar);
    }
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("SimpleTableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
//        String[] columnNames = {"First Name", "Last Name", "Sport", "# of Years", "Vegetarian"};
        
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
        
        
        int numOfRow = childStates.size()+parents.size();
        int numOfCol = 1;
        for(int I = 0; I <parentStates.size(); I++)
        		numOfCol *= parentStates.get(I).size();
        
        numOfCol++;
        String[] columnNames = new String[numOfCol];
        Arrays.fill(columnNames, "");
        
        // making first column
        columnNames[0] = parents.get(0);// name of first (any) parent
//        columnNames[1] = parentStates.get(0).get(0);// 1st state of 1st parent
        int J = 1;
        // this loop adds in 1st row/header the name of 1st parent and its states with required spaces
//        for(int I = 1; I < parentStates.get(0).size(); I++){
        int step = numOfCol / parentStates.get(0).size();
        for(int I = 1; I < numOfCol; I += step){
//        	J += numOfCol / parentStates.get(0).size(); 
        	columnNames[I] = parentStates.get(0).get((I-1)%parentStates.get(0).size());// remaining will be blank in 1st header row
        }
        //System.out.println(columnNames[J+1]);
        //Create and set up the content pane.
        TableFormat newContentPane = new TableFormat(columnNames, child, childStates, parents, parentStates);
    
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}