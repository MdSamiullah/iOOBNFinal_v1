package com.mxgraph.frames;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class ColorRenderer2 extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected)
            setBackground( table.getSelectionBackground() );
        else
        {
            setBackground( table.getBackground() );

            try
            {
                double number = Double.parseDouble( value.toString() );

                if (number > 0.0)
                setBackground( Color.LIGHT_GRAY );
            }
            catch(Exception e) {}
        }

        return this;
    }
}

/*	after i put the jtable.setDefaultRenderer(Object.class, new ColorRenderer()); 
 *  into the constructor it worked, thanks, but why did it use to lag the table out??
 */
