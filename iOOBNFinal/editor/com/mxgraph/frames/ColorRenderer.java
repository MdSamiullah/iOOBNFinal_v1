package com.mxgraph.frames;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class ColorRenderer extends DefaultTableCellRenderer
{
	public int parentSize;
	
	public ColorRenderer(int parentSize){
		this.parentSize = parentSize;
	}
	
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

//        if (row < TableFormat.NumOfParents || column == 0){
        if (row < parentSize || column == 0){
        	setBackground( Color.WHITE );
//        	System.out.println(row + " " + column + " " + TableFormat.NumOfParents);
        }
        else{
        	setBackground( table.getSelectionBackground() );
        }
        
        try
        {
        	double number = Double.parseDouble( value.toString() );
        	if (number < 0.0 || number > 1.0) setBackground( Color.RED );
        }
        catch(Exception e) {}

        return this;
    }
}

/*	after i put the jtable.setDefaultRenderer(Object.class, new ColorRenderer()); 
 *  into the constructor it worked, thanks, but why did it use to lag the table out??
 */
