package org.galeas.index.handlingtypes.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.galeas.index.handlingtypes.framework.DocumentHandler;
import org.galeas.index.handlingtypes.framework.DocumentHandlerException;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.encryption.DocumentEncryption;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

public class PDFBoxPDFHandler implements DocumentHandler {

	static Logger logger = Logger.getLogger(PDFBoxPDFHandler.class);

	public static String password = "";

	public Document getDocument(InputStream is) throws DocumentHandlerException {
		
		//Document nulldocument = null;
		COSDocument cosDoc = null;
		try {
			cosDoc = parseDocument(is);
		} catch (IOException e) {
			
			closeCOSDocument(cosDoc);
			throw new DocumentHandlerException("Cannot parse PDF document", e);
		}

		// decrypt the PDF document, if it is encrypted
		try {
			if (cosDoc.isEncrypted()) {				
				DocumentEncryption decryptor = new DocumentEncryption(cosDoc);
				decryptor.decryptDocument(password);				
			}
		} 
		catch (CryptographyException e) {
			
			closeCOSDocument(cosDoc);
			throw new DocumentHandlerException("Cannot decrypt PDF document", e);			
		}
		catch (InvalidPasswordException e) {
			
			closeCOSDocument(cosDoc);
			throw new DocumentHandlerException("Cannot decrypt PDF document: invalid password", e);			
		} 
		catch (IOException e) {
			
			closeCOSDocument(cosDoc);
			throw new DocumentHandlerException("Cannot decrypt PDF document", e);
		}

		
		
		// extract PDF document's textual content
		String docText = null;
		try {
			
			PDFTextStripper stripper = new PDFTextStripper();
			docText = stripper.getText(new PDDocument(cosDoc));
		} catch (IOException e) {
			
			closeCOSDocument(cosDoc);
			throw new DocumentHandlerException("Cannot parse PDF document", e);
			// String errS = e.toString();
			// if (errS.toLowerCase().indexOf("font") != -1) {
			// }
		}

		Document doc = new Document();
		if (docText != null) {
			
			// PG:Commented
			// doc.add(Field.UnStored("contents", docText));

			// Add document content
			doc.add(new Field("contents", docText, Field.Store.NO,
					Field.Index.TOKENIZED, Field.TermVector.WITH_POSITIONS));
		}

		// extract PDF document's meta-data
		PDDocument pdDoc = null;
		try {
			
			pdDoc = new PDDocument(cosDoc);
			PDDocumentInformation docInfo = pdDoc.getDocumentInformation();
			String author = docInfo.getAuthor();
			String title = docInfo.getTitle();
			String keywords = docInfo.getKeywords();
			String summary = docInfo.getSubject();

			if ((author != null) && (!author.equals(""))) {
				//doc.add(Field.Text("author", author));
				doc.add(new Field("author", author, Field.Store.YES, Field.Index.TOKENIZED));
			}
			if ((title != null) && (!title.equals(""))) {
				//doc.add(Field.Text("title", title));
				doc.add(new Field("title", title, Field.Store.YES, Field.Index.TOKENIZED));
			}
			if ((keywords != null) && (!keywords.equals(""))) {
				//doc.add(Field.Text("keywords", keywords));
				doc.add(new Field("keywords", keywords, Field.Store.YES, Field.Index.TOKENIZED));
			}
			if ((summary != null) && (!summary.equals(""))) {
				//doc.add(Field.Text("summary", summary));
				doc.add(new Field("summary", summary, Field.Store.YES, Field.Index.TOKENIZED));
			}
		} catch (Exception e) {
			closeCOSDocument(cosDoc);
			closePDDocument(pdDoc);
			System.err.println("Cannot get PDF document meta-data: "
					+ e.getMessage());
		}
		
		closeCOSDocument(cosDoc);
		
		return doc;
	}

	private static COSDocument parseDocument(InputStream is) throws IOException {
		PDFParser parser = new PDFParser(is);
		parser.parse();
		return parser.getDocument();
	}

	private void closeCOSDocument(COSDocument cosDoc) {
		if (cosDoc != null) {
			try {
				cosDoc.close();
			} catch (IOException e) {
				// eat it, what else can we do?
			}
		}
	}

	private void closePDDocument(PDDocument pdDoc) {
		if (pdDoc != null) {
			try {
				pdDoc.close();
			} catch (IOException e) {
				// eat it, what else can we do?
			}
		}
	}

	public static void main(String[] args) throws Exception {
		PDFBoxPDFHandler handler = new PDFBoxPDFHandler();
		Document doc = handler.getDocument(new FileInputStream(
				new File(args[0])));
		System.out.println(doc);
	}
}
