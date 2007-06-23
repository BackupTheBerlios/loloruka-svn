package org.galeas.xindex;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.galeas.TipsterParser.TipsterDocument;
import org.galeas.TipsterParser.TipsterParser;
import org.galeas.utils.ConfigurationManager;
import org.galeas.utils.Stopwords;
import org.xml.sax.SAXException;

public class XIndexer {

	public Properties properties = new Properties();	
	private List timeCounter = new ArrayList();
	private long startIndexingTime;
	private String propertiesFile = "/home/patricio/TREC_Evaluation/conf/properties.xml";
	
	private XIndexer() throws IOException, SAXException {
		loadProperties();
		startIndexing();		
	}
	
	
	public static void main(String[] args) throws Exception {
		XIndexer start = new XIndexer();
	}		
	
	
	private void startIndexing() throws IOException, SAXException {
		
		File dataDir = new File(properties.getProperty("datadir").trim());
		File indexDir = new File(properties.getProperty("indexdir").trim());

		System.out.println("IndexDir Dir: " + indexDir.getAbsolutePath());
		System.out.println("DataDir Dir: " + dataDir.getAbsolutePath());

		this.startIndexingTime = new Date().getTime();
		
		this.addTimeToList(this.startIndexingTime);
		
		int numIndexed = 0;

		// Number of Indexed Files
		numIndexed = index(indexDir, dataDir);


		System.out.println("TOTAL INDEXING PROCESS with " + numIndexed + " XML-documents");		
	}

	
	
	/** 
	 * Load the properties file
	 * 
	 * @throws IOException
	 */
	private void loadProperties() throws IOException {
		ConfigurationManager configurationManager = new ConfigurationManager(
				this.propertiesFile);
		this.properties = configurationManager.getDefaultConfiguration();
	}	
	
	
	
	public int index(File indexDir, File dataDir) throws IOException,
			SAXException {

		if (!dataDir.exists() || !dataDir.isDirectory()) {
			throw new IOException(dataDir
					+ " does not exist or is not a directory");
		}
	
		
		/* Get the Stopword list */	
		File stopwords = null;		
		String stopwordsListFile = properties.getProperty("stopword_file").trim();		
		System.out.println("stopword_file:"+stopwordsListFile);
		
		if(stopwordsListFile != null) {
			stopwords = new File(stopwordsListFile);
		}
				
		
		/* Get the Index Analyzer */
		String definedIndexAnalyzer = properties.getProperty("analyzer").trim();
		
		/* Initialize the IndexWriter */
		IndexWriter writer = null;
		
		/* If Standard Analyzer is defined */
		if(definedIndexAnalyzer.equalsIgnoreCase("Standard")) {
			StandardAnalyzer standardAnalyzer;
			if(stopwords.isFile()) {
				System.out.println("Applying StandardAnalyzer WITH StopWords");
				standardAnalyzer = new StandardAnalyzer(stopwords);
			}
			else {
				System.out.println("Applying StandardAnalyzer");
				standardAnalyzer = new StandardAnalyzer();
			}
			
			/* Define the Index Directory and the applied Analyzer */
			writer = new IndexWriter(indexDir, standardAnalyzer, true);			
			
		}
		// If Porter Analyzer is defined
		else {

			SnowballAnalyzer snowballAnalyzer;
			if(stopwords.isFile()) {
				
				System.out.println("Applying SnowballAnalyzer -> "+definedIndexAnalyzer+" WITH StopWords");
				String[] stopwordsArray = new Stopwords(stopwords).getStopArray();
				snowballAnalyzer = new SnowballAnalyzer(definedIndexAnalyzer, stopwordsArray);
			}
			else {
				System.out.println("Applying SnowballAnalyzer->"+definedIndexAnalyzer);
				snowballAnalyzer = new SnowballAnalyzer(definedIndexAnalyzer);
			}	
			
			writer = new IndexWriter(indexDir, snowballAnalyzer, true);
		}
	
		//writer.setUseCompoundFile(false);
		
		
		
		
		/* Index the Data Directory */
		indexDirectory(writer, dataDir);

		int numIndexed = writer.docCount();

		writer.optimize();

		writer.close();

		return numIndexed;
	}

	
	
	private void indexDirectory(IndexWriter writer, File dir)
			throws IOException, SAXException {
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Directory to index : "+dir.getAbsolutePath());
		File[] files = dir.listFiles();
		long doc_counter = 0;
		
		
		this.addTimeToList();
		
		
		for (int i = 0; i < files.length; i++) {
			File f = files[i];

			if (f.isDirectory()) {
				indexDirectory(writer, f); // recurse				
				doc_counter = 0;
			} else {

				doc_counter++;
				indexFile(writer, f);

			}

		}
		
		if(doc_counter >0) {
			System.out.println();
			System.out.println("* Indexing of directory with "+doc_counter+" files took "+getLastIndexingTimeLap("LAP")+" minutes");
			System.out.println("* Cumulated time : "+getLastIndexingTimeLap("CUMULATED")+" minutes");
			System.out.println();
		}

		
	}

	private void indexFile(IndexWriter writer, File f)
			throws IOException, SAXException {

		if (f.isHidden() || !f.exists() || !f.canRead()) {
			return;
		}

		
		// doc.add(Field.Text("contents", new FileReader(f), true));

		// Create the Parser for the Tister File
		TipsterParser tipsterParser = new TipsterParser();

		// Parse the actual file (f)
		tipsterParser.parseFile(f);
		
		
		//Get All document in the File
		List documentList = tipsterParser.getDocumentList();
		
		Iterator it = documentList.iterator();
		while(it.hasNext()) {
			
			Document doc = new Document();
			
			TipsterDocument tipsterDoc = (TipsterDocument)it.next();
			
			/* Save the document DOCNO in the index */
			Field itemidField = new Field("docno", tipsterDoc.getDocno(),
					Field.Store.YES, Field.Index.NO);

			/* Save the document Title in the index */
			Field titleField = new Field("title", tipsterDoc.getTitle(),
					Field.Store.NO, Field.Index.TOKENIZED);
			// titleField.setBoost((float) 1.2);

			/*
			 * Save the document Text in the index's field
			 * named text
			 */
			Field textField = new Field("text", tipsterDoc.getText()
					, Field.Store.NO,Field.Index.TOKENIZED, Field.TermVector.WITH_POSITIONS);	
			
			doc.add(itemidField);
			doc.add(titleField);
			doc.add(textField);
			
			writer.addDocument(doc);
		}

		
	}
	
	
	private void addTimeToList(long actualTime) {	
		this.timeCounter.add(String.valueOf(actualTime));
	}	
	
	private void addTimeToList() {
		long actualTime = new Date().getTime();		
		this.timeCounter.add(String.valueOf(actualTime));
	}	
	
	
	private String getLastIndexingTimeLap(String type) {
		
		String lastTimeElement = null;
		if(type.equalsIgnoreCase("LAP")) {
			lastTimeElement = (String) this.timeCounter.remove(this.timeCounter.size()-1);
		}
		else if(type.equalsIgnoreCase("CUMULATED")) {
			lastTimeElement = String.valueOf(this.startIndexingTime);
		}
		
		
		long startTime =  Long.valueOf(lastTimeElement).longValue();		
		long finishTime =  new Date().getTime();		
		long LastTimeLapMiliseconds = (finishTime - startTime);		
		double LastTimeLapMinutes = (double)LastTimeLapMiliseconds/60000;
		
		
		// Round und scale the indexing time	
	    int decimalPlace = 2;
	    BigDecimal bd = new BigDecimal(LastTimeLapMinutes);
	    bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
	    LastTimeLapMinutes = bd.doubleValue();		
		
		// Print indexing time
		return String.valueOf(LastTimeLapMinutes);		
		
	}
	
	

}
