package org.galeas.xgraphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GraphLeyend {

	private List leyendElements = new ArrayList();
	
	
	
	public void addLeyendElement(String word, Color color) {
		if(!elementExists(word)) {
			LeyendElement newElement = new LeyendElement();
			newElement.setElement(word, color);		
			this.leyendElements.add(newElement);
		}
	}
	
	
	public boolean elementExists(String elementString) {
		
		Iterator it = this.leyendElements.iterator();
		while(it.hasNext()) {
			LeyendElement actualElement = (LeyendElement)it.next();
			if(actualElement.element.equalsIgnoreCase(elementString)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public Graphics2D addLeyendToGraphic(Graphics2D graphic, float x, float y) {
		
		//initial values
		float x1 = x;
		float y1 = y;
		
		// set the the leyend bounds
		int columnWidth = 100;
		int rowHeight = 20;
		int columns = 3;
		
		int nrOfElements = this.leyendElements.size();
		
		for(int i=0;i<nrOfElements;i++) {
			
			// set the vertical position for new lines
			if(remainder(i+1, columns+1)==0) {
				y1 += rowHeight;
				x1 = x;
			}

			LeyendElement element = (LeyendElement) this.leyendElements.get(i); 
			String elementString = element.element;
			Color color = element.getColor();
			
			// draw the string
			graphic.setColor(color);
			graphic.drawString(elementString,x1,y1);
			
			x1 += columnWidth;
			
		}
		return graphic;
	}
	
	
	public static double remainder( double a, double b )
	{
	  return Math.signum(a)*(Math.abs(a)-Math.abs(b)*Math.floor(Math.abs(a)/Math.abs(b)));
	}	
	
}

class LeyendElement {
	
	String element;
	Color color;

	
	public void setElement(String element, Color color) {
		this.element = element;
		this.color = color;		
	}
	
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	
}
