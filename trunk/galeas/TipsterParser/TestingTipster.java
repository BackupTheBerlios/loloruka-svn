package org.galeas.TipsterParser;

import java.io.File;
import java.io.IOException;

import org.htmlparser.util.ParserException;
import org.xml.sax.SAXException;

public class TestingTipster {

	static File outputFile = new File("C:\\TEMP\\TestFileTipster.txt");
	
	public static void main(String args[]) throws IOException, SAXException, ParserException  {
		
		outputFile.delete();
		File dir = new File(args[0]);
		readDirectory(dir);
	}

	private static void readDirectory(File dir) throws IOException, SAXException, ParserException {

		File[] files = dir.listFiles();
		long doc_counter = 0;


		for (int i = 0; i < files.length; i++) {
			File f = files[i];

			if (f.isDirectory()) {
				System.out.println("Indexing Directory : " + f.getName());
				readDirectory(f); // recurse
				doc_counter = 0;
			} else {

				doc_counter++;
				if ((doc_counter % 100) == 0)
					System.out.println("." + doc_counter);
				else
					System.out.print(".");

				readFile(f);

			}
		}

	}
	
	private static void readFile(File f) throws IOException, SAXException, ParserException {

		//System.out.println("readFile:"+f.getAbsolutePath());
		
		// Create the Parser for the Reuters document
		TipsterParser tipsterParser = new TipsterParser();
		
		tipsterParser.parseFile(f);
		
		
		//tipsterParser.printDocumentList();
		tipsterParser.documentListToFile(outputFile);
		
	}

}
