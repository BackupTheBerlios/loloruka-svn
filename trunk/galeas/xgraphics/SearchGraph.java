package org.galeas.xgraphics;
import javax.swing.JFrame;

import org.galeas.xsearch.GraphicData;

public class SearchGraph {
	
	public SearchGraph(String windowsTitle, String graphTitle, GraphicData[] graphicData) {
		SearchFrame frame = new SearchFrame(windowsTitle, graphTitle, graphicData);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);		
	}
		
}



