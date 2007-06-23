package org.galeas.xgraphics;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import org.galeas.xsearch.GraphicData;

class SearchFrame extends JFrame {
	public SearchFrame(String windowsTitle, String graphTitle, GraphicData[] graphicData){
		
		// get screen dimensions
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		//int screenWidth = screensize.width;
		int screenWidth = 900;
		int screenHeight = screensize.height;
		
		//center the frame in screen
		setSize(screenWidth/2, screenHeight/2);
		setLocation(screenWidth/4, screenHeight/4);
		
		// set window titel
		setTitle(windowsTitle);		
		
		
		
		// define the content pane 
		Container contentPane = getContentPane();
		
		// define the documet base lines
		DocumentGraphLines mylines = new DocumentGraphLines();

		// set the graph title
		mylines.setGraphicsTitle(graphTitle);
		
		
		/* add (to the lines) the boxes with the query-words positions 
		 * on the document*/
		mylines.setDocumentsBoxPositions(graphicData);
		
	
		
		contentPane.add(mylines);		
		
	}
}