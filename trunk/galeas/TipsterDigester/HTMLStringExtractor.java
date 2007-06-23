
package org.galeas.TipsterDigester;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

/**
* Extract plaintext strings from a web page.
* Illustrative program to gather the textual contents of a web page.
* Uses a {@link org.htmlparser.beans.StringBean StringBean} to accumulate
* the user visible text (what a browser would display) into a single string.
*/
public class HTMLStringExtractor
{
 private String resource;

 /**
  * Construct a StringExtractor to read from the given resource.
  * @param resource Either a URL or a file name.
  */
 public HTMLStringExtractor (String resource)
 {
     this.resource = resource;
 }

 /**
  * Extract the text from a page.
  * @return The textual contents of the page.
  * @param links if <code>true</code> include hyperlinks in output.
  * @exception ParserException If a parse error occurs.
  */
 public String extractStrings (boolean links)
     throws
         ParserException
 {
     StringBean sb;

     sb = new StringBean ();
     sb.setLinks (links);
     sb.setURL (resource);

     return (sb.getStrings ());
 }

 /**
  * Mainline.
  * @param args The command line arguments.
  */
 public static void main (String[] args)
 {
     boolean links;
     String url;
     HTMLStringExtractor se;

     links = false;
     url = null;
     for (int i = 0; i < args.length; i++)
         if (args[i].equalsIgnoreCase ("-links"))
             links = true;
         else
             url = args[i];
     if (null != url)
     {
         se = new HTMLStringExtractor (url);
         try
         {
             System.out.println (se.extractStrings (links));
         }
         catch (ParserException e)
         {
             e.printStackTrace ();
         }
     }
     else
         System.out.println ("Usage: java -classpath htmlparser.jar org.htmlparser.parserapplications.StringExtractor [-links] url");
 }
}
