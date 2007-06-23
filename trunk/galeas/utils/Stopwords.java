package org.galeas.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Stopwords {

	private File stopFile;
	
	
	public Stopwords(String stopFileName) {
		this.stopFile = new File(stopFileName);
	}
	
	public Stopwords(File stopFile) throws IOException {
		this.stopFile = stopFile;
	}
	
	public String[] getStopArray() throws IOException {
		
		BufferedReader in = new BufferedReader(new FileReader(stopFile));
		String str;
		List stopList = new ArrayList();
		while ((str = in.readLine()) != null) {
			stopList.add(str);
		}
		in.close();
		
		Iterator it = stopList.iterator();
		String stopArray[] = new String[stopList.size()];
		int counter = 0;
		while(it.hasNext()) {
			stopArray[counter++] = (String)it.next();
		}
		
		return stopArray;
	}
	
	
	
}
