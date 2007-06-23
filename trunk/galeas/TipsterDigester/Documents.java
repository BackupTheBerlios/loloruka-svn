package org.galeas.TipsterDigester;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Documents {

	private List documentList = new ArrayList();
	
	
	public Documents() {
		
	}
	
	public void addDocument(DocumentItem docItem) {
		documentList.add(docItem);
	}
	
	public void printDocuments() {
		Iterator it = documentList.iterator();
		while (it.hasNext()) {
			DocumentItem actualDoc = (DocumentItem) it.next();
			actualDoc.printDocument();
		}
	}
	
}
