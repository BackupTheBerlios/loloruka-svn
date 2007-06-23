package org.galeas.reutersdigester;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class ReutersDigester {
	
	static Newsitem news;
   
	
	public ReutersDigester(String reuterDoc) throws IOException, SAXException {
		File inputFile = new File( reuterDoc );
		digest(inputFile);
	}
	
	public ReutersDigester(File reuterFile) throws IOException, SAXException {
		digest(reuterFile);
	}
	
	
	public void digest( File reuterFile ) throws IOException, SAXException {

         Digester digester = new Digester();
         digester.setValidating( false );

         digester.addObjectCreate( "newsitem", Newsitem.class );
         digester.addSetProperties( "newsitem", "itemid", "itemid" );                  
         digester.addBeanPropertySetter( "newsitem/title", "title" );
                  
         digester.addObjectCreate( "newsitem/text", Text.class );
         digester.addBeanPropertySetter( "newsitem/text/p", "p" );
         digester.addSetNext( "newsitem/text", "addText" );
                  
         news = (Newsitem)digester.parse( reuterFile );
         
         //System.out.println(news.toString());
   }
   
   
   public String getTitle() {
	   return news.getTitle();
   }
   
   public String getItemid() {
	   return news.getItemid();
   }
   
   public String getText() {
	   return news.getText();
   }
   
   

}