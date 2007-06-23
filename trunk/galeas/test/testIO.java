package org.galeas.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class testIO {

	public static void main(String[] args) throws IOException {
		convert(new File("C:\\TEMP\\testTipster\\CR93E29"), new File("C:\\TEMP\\testTipster\\chao.txt"));
	}
	
	
	private static void convert(File input, File output) throws IOException {
	
		BufferedReader in = new BufferedReader(new FileReader(input));
		BufferedWriter out = new BufferedWriter(new FileWriter(output));
		
        
		// Add the initial XML-Root TAG
		out.write("<DOCUMENTS>\n");
        
		String str="";
        while ((str = in.readLine()) != null) {
        	//Write the original content in the outputFile 
        	out.write(str+"\n");
        }
        
        // Add the final XML-Root TAG
        out.write("</DOCUMENTS>\n");
        
        in.close();
        out.close();
        
	}

}

