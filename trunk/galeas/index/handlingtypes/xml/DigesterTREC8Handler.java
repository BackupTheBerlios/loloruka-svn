package org.galeas.index.handlingtypes.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.galeas.index.handlingtypes.framework.DocumentHandler;
import org.galeas.index.handlingtypes.framework.DocumentHandlerException;
import org.xml.sax.SAXException;

public class DigesterTREC8Handler implements DocumentHandler{
	  private Digester dig;
	  private static Document doc;

	  public DigesterTREC8Handler() {

	    // instantiate Digester and disable XML validation
	    dig = new Digester();
	    dig.setValidating(false);

	    // instantiate DigesterXMLHandler class
	    dig.addObjectCreate("document-collection", DigesterXMLHandler.class);
	   
	    // instantiate Contact class
	    dig.addObjectCreate("document-collection/document", TRECdocument.class);


	    // set different properties of document instance using
	    // specified methods
	    dig.addCallMethod("document-collection/document/docno","setDocno", 0);
	    dig.addCallMethod("document-collection/document/text","setText", 0);

	    // call 'populateDocument' method when the next
	    // 'document-collection/document' pattern is seen
	    dig.addSetNext("document-collection/document", "populateDocument");
	  }

	  public synchronized Document getDocument(InputStream is)
	    throws DocumentHandlerException {

	    try {
	      dig.parse(is);
	    }
	    catch (IOException e) {
	      throw new DocumentHandlerException(
	        "Cannot parse XML document", e);
	    }
	    catch (SAXException e) {
	      throw new DocumentHandlerException(
	        "Cannot parse XML document", e);
	    }

	    return doc;
	  }

	  public void populateDocument(TRECdocument document) {

	    // create a blank Lucene Document
	    doc = new Document();
	    doc.add(new Field("docno", document.getDocno(),Field.Store.YES, Field.Index.UN_TOKENIZED));
	    doc.add(new Field("content", document.getText(),Field.Store.YES, Field.Index.UN_TOKENIZED));
	  }
	  

	  /**
	   * JavaBean class that holds properties of each document
	   * entry.   It is important that this class be public and
	   * static, in order for Digester to be able to instantiate
	   * it.
	   */
	  public static class TRECdocument {
	    private String docno;
	    private String text;
	    
		public String getDocno() {
			return docno;
		}
		public void setDocno(String docno) {
			this.docno = docno;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}

	  }

	  
	  
	  public static void main(String[] args) throws Exception {
	    DigesterXMLHandler handler = new DigesterXMLHandler();
	    Document doc =
	      handler.getDocument(new FileInputStream(new File(args[0])));
	    System.out.println(doc);
	  }
}
