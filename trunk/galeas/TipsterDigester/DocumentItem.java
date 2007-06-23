package org.galeas.TipsterDigester;


public class DocumentItem {
	private String docno;	
	private String title="";
	private String text="";
	private String documentName="";
	
	
	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocno() {
		return docno;
	}

	public void setDocno(String docno) {
		this.docno = docno;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title += title + " ";;
	}
	
	
	public void setText(String text) {
		this.text += text + " ";;
	}	
	
	public String getText() {
		return this.text;		
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("DOCNAME >> " + this.getDocumentName()+"\n");
		buf.append("DOCNO >> " + this.getDocno()+"\n");
		buf.append("TITLE >> " + this.getTitle()+"\n");
		buf.append("TEXT >> " + this.getText());
		buf.append("\n\n--------------------------------------------------------------------");
		return buf.toString();
	}
	
	public void printDocument() {
		System.out.println(this.toString());
	}

}
