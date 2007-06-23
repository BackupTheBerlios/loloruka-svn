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

public class DigesterXMLReuterHandler implements DocumentHandler {

	  private Digester dig;
	  private static Document doc;

	  public DigesterXMLReuterHandler() {

	    // instantiate Digester and disable XML validation
	    dig = new Digester();
	    dig.setValidating(false);

	    // instantiate DigesterXMLReuterHandler class
	    dig.addObjectCreate("news-collection", DigesterXMLReuterHandler.class);
	   
	    System.out.println("hallo .............");
	    
	    // instantiate ReuterDocument class
	    dig.addObjectCreate("news-collection/newsitem", ReuterDocument.class);

	    
	    // set type property of ReuterDocument instance when
	    // attribute is found
	    dig.addSetProperties("news-collection/newsitem", "itemid", "itemid");
	    dig.addSetProperties("news-collection/newsitem", "date", "date");
	    
	    
	    // set different properties of ReuterDocument instance using
	    // specified methods
	    dig.addCallMethod("news-collection/newsitem/title", "setTitle", 0);
	    dig.addCallMethod("news-collection/newsitem/headline", "setHeadline", 0);
	    dig.addCallMethod("news-collection/newsitem/dateline", "setDateline", 0);
	    dig.addCallMethod("news-collection/newsitem/text", "setText", 0);
	    

	    // call 'populateDocument' method when the next
	    // 'news-collection/contact' pattern is seen
	    dig.addSetNext("news-collection/newsitem", "populateDocument");
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

	  public void populateDocument(ReuterDocument contact) {

	    // create a blank Lucene Document
	    doc = new Document();
	    
	    System.out.println("itemid"+contact.getItemid()+" - title:"+contact.getTitle());
	    
	    doc.add(new Field("itemid", contact.getItemid(),Field.Store.YES, Field.Index.UN_TOKENIZED));
	    doc.add(new Field("title", contact.getTitle(),Field.Store.YES, Field.Index.UN_TOKENIZED));
	    doc.add(new Field("text", contact.getText(),Field.Store.YES, Field.Index.UN_TOKENIZED));
	  }
	  

	  /**
	   * JavaBean class that holds properties of each ReuterDocument
	   * entry.   It is important that this class be public and
	   * static, in order for Digester to be able to instantiate
	   * it.
	   */
	  public static class ReuterDocument {
	    private String itemid;
	    private String date;
	    private String title;
	    private String dateline;
	    private String headline;
	    private String text;
	    
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getDateline() {
			return dateline;
		}
		public void setDateline(String dateline) {
			this.dateline = dateline;
		}
		public String getHeadline() {
			return headline;
		}
		public void setHeadline(String headline) {
			this.headline = headline;
		}
		public String getItemid() {
			return itemid;
		}
		public void setItemid(String itemid) {
			this.itemid = itemid;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}


	  }

	  public static void main(String[] args) throws Exception {
	    DigesterXMLHandler handler = new DigesterXMLHandler();
	    Document doc = handler.getDocument(new FileInputStream(new File(args[0])));
	    System.out.println(doc);
	  }
	}
