package org.galeas.reutersdigester;

import java.util.Iterator;
import java.util.Vector;

public class Newsitem {
	private String itemid;	
	private String title;
	private Vector paragraph;


	public Newsitem() {
		paragraph = new Vector();
	}
		

	public void addText(Text paragraph) {
		this.paragraph.addElement(paragraph);
	}


	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getText() {
		StringBuffer buf = new StringBuffer();
		Iterator it = paragraph.iterator();
		while(it.hasNext()) {
			Text p = (Text) it.next();
			buf.append(p.toString());
		}
		return buf.toString();		
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("itemid >> " + this.getItemid()+"\n");
		buf.append("title >> " + this.getTitle()+"\n");
		buf.append("text >> " + this.getText());

		return buf.toString();
	}

	


}
