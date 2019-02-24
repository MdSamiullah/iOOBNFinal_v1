package com.mxgraph.shape;

import java.awt.Polygon;
import java.awt.Rectangle;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public class mxDoubleRhombusShape extends mxRhombusShape
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

		Rectangle rect = state.getRectangle();
		int x = rect.x + inset;
		int y = rect.y + inset;
		int w = rect.width - 2 * inset;
		int h = rect.height - 2 * inset;
		int halfWidth = w / 2;
		int halfHeight = h / 2;
		
		Polygon rhombus = new Polygon();
		rhombus.addPoint(x + halfWidth, y);
		rhombus.addPoint(x + w, y + halfHeight);
		rhombus.addPoint(x + halfWidth, y + h);
		rhombus.addPoint(x, y + halfHeight);
		
		canvas.getGraphics().draw(rhombus);
//		canvas.getGraphics().drawOval(x, y, w, h);
	}

}
