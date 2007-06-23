package org.galeas.test;

import java.io.IOException;

import org.galeas.reutersdigester.ReutersDigester;
import org.xml.sax.SAXException;

public class TestingDigester {

	public static void main(String[] args) throws IOException, SAXException {
		
		ReutersDigester r = new ReutersDigester("C:\\TEMP\\data\\21123newsML.xml");
		r.toString();
	}
}
