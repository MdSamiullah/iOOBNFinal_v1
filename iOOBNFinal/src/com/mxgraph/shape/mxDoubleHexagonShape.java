package com.mxgraph.shape;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public class mxDoubleHexagonShape extends mxHexagonShape
{

	/**
	 * 
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		super.paintShape(canvas, state);
		
		int inset = (int) Math.round((mxUtils.getFloat(state.getStyle(),
				mxConstants.STYLE_STROKEWIDTH, 1) + 3)
				* canvas.getScale());
		
		Rectangle temp = state.getRectangle();
		int x = temp.x;
		int y = temp.y;
		int w = temp.width;
		int h = temp.height;
		String direction = mxUtils.getString(state.getStyle(),
				mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST);
		Polygon hexagon = new Polygon();

		if (direction.equals(mxConstants.DIRECTION_NORTH)
				|| direction.equals(mxConstants.DIRECTION_SOUTH))
		{	
			hexagon.addPoint(x + (int) (0.5 * w ), y + inset);
			hexagon.addPoint(x + w - inset, y + (int) (0.25 * h));
			hexagon.addPoint(x + w - inset, y + (int) (0.75 * h));
			hexagon.addPoint(x + (int) (0.5 * w), y + h - inset);
			hexagon.addPoint(x + inset, y + (int) (0.75 * h));
			hexagon.addPoint(x + inset, y + (int) (0.25 * h));
			
		}
		else
		{	
			hexagon.addPoint(x + (int) (0.25 * w + inset), y+inset);
			hexagon.addPoint(x + (int) (0.75 * w-inset), y+inset);
			hexagon.addPoint(x + w-inset, y + (int) (0.5 * h));
			hexagon.addPoint(x + (int) (0.75 * w-inset), y + h-inset);
			hexagon.addPoint(x + (int) (0.25 * w+inset), y + h-inset);
			hexagon.addPoint(x+inset, y + (int) (0.5 * h));
			
		}

		canvas.getGraphics().draw(hexagon);
	}

}
