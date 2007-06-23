package org.galeas.reutersdigester;


public class Text {
	
   private String p="";

   public Text() {}

   public void setP( String p ) {
	   this.p += p + "\n";
   }
   
   public String toString() {
	   return this.p;
   }

}
