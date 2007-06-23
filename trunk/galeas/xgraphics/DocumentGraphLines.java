package org.galeas.xgraphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.galeas.xsearch.GraphicData;
import org.galeas.xsearch.XqueryTerm;


class DocumentGraphLines extends JPanel {
	
	private float initial_x = 10;
	private float initial_y = 50;
	private float line_wide = 300;
	private float line_step = 20;

	private Color lineColor = Color.GRAY;
	
	private GraphicData[] graphicData;
	
	private String graphicsTitle;
	
	private  Color[] colorArray =  
		{ 
			new Color(255,0,0), 
			new Color(0,255,0), 
			new Color(0,0,255), 
			new Color(255,255,0), 
			new Color(0,255,255), 
			new Color(255,0,255), 
			new Color(192,192,192),
			new Color(255,0,0), 
			new Color(0,255,0).brighter(), 
			new Color(0,0,255).brighter(), 
			new Color(255,255,0).brighter(), 
			new Color(0,255,255).brighter(), 
			new Color(255,0,255).brighter(), 
			new Color(192,192,192).brighter(),			
		};
	
	
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D docGraphics = (Graphics2D)g;
		
		// set the line color
		docGraphics.setPaint(lineColor);
		
		
		// initialize the graphics positions
		float x1 = initial_x;
		float y1 = initial_y;
		float x2 = x1 + line_wide;
		float y2 = initial_y;

		
		// initialize the leyend
		GraphLeyend leyend = new GraphLeyend();
		
		
		// draw the graph title
		
		Font titleGraphFont = new Font("SansSerif",Font.BOLD,12);
		docGraphics.setFont(titleGraphFont);
		docGraphics.drawString(graphicsTitle, initial_x, initial_y-30);
		
		// draw base-lines and word-positions			
		for(int i=0; i<graphicData.length; i++) {
		
			// read the GraphicData object
			GraphicData documentGraph = graphicData[i];
			
			// get the maximal position of the document (~ document size)
			float maximalDocumentPosition = graphicData[i].getMaximaDocumentPosition();
			
			//define line points
			Point2D p1 = new Point2D.Float(x1,y1);
			Point2D p2 = new Point2D.Float(x2,y2);	
			
			// draw the base line for each document
			Line2D line = new Line2D.Float(p1, p2);
			docGraphics.draw(line);
			
			// define the font
			int fontSize = 10;
			Font positionCharactersFont = new Font("SansSerif",Font.TRUETYPE_FONT,fontSize);
			docGraphics.setFont(positionCharactersFont);			
			
			
			// draw each document Name
			String docName = documentGraph.getDocumentName();
			docGraphics.drawString(docName,x2+10,y2);
						
			
			
			/* --------------------------------------
			 * draw the words positions
			 * --------------------------------------*/
			//get the query-words (XqueryTerm) objects
			List queryTermsObjectList = documentGraph.getQueryTermsList();
			Iterator it = queryTermsObjectList.iterator();
			
			int termColorCounter=0;
			
			while (it.hasNext()) {

				XqueryTerm queryTerm = (XqueryTerm)it.next();
				
				// get the position for the document i
				float[] positionsX = queryTerm.getPositions();
				
				// set the color position character
				Color termColor = colorArray[termColorCounter++];
				
				//scale the maximal maximalDocumentPosition with line_wide
				float scaleFactor = line_wide / maximalDocumentPosition;				
				
				// draw the characters at the positionsX
				for(int p=0; p<positionsX.length;p++) {
					docGraphics.setPaint(termColor);
					docGraphics.drawString("+", initial_x + positionsX[p]*scaleFactor, y1);
				}	
				
				// reset the line color
				docGraphics.setPaint(lineColor);
				
				
				// Add the queryWord to the Leyend, ignore it if already exists
				leyend.addLeyendElement(queryTerm.getQuery(),termColor);
				
			}
			/* --------------------------------------*/
			
			// calculate the next base-line position
			y1 = y2  += line_step;
			
		}
		
		// draw the leyend
		leyend.addLeyendToGraphic(docGraphics,initial_x,y2);
		

	}
	
	
	
	
	/* --------------------------------------
	 * Set the arra with all documents positions 
	 * --------------------------------------*/
	public void setDocumentsBoxPositions (GraphicData[] graphicData) {
		this.graphicData = graphicData;
		
	}
	
		
	public void setGraphicsTitle (String title) {
		this.graphicsTitle = title;
	}
	
	
	
}
