package org.galeas.index.handlingtypes.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * A File Indexer capable of recursively indexing a directory tree.
 */
public class FileIndexer {
	protected FileHandler fileHandler;

	public FileIndexer(Properties props) throws IOException {
		fileHandler = new ExtensionFileHandler(props);
	}
	
	int file_counter = 0;
	
	public void index(IndexWriter writer, File file)
	throws FileHandlerException {

		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						index(writer, new File(file, files[i]));
					}
				}
			} else {

				System.out.println("["+ ++file_counter +"]" + "Indexing " + file);
				try {
					
					Document doc = fileHandler.getDocument(file);
					//System.out.println("Document:"+doc);
					
					if (doc != null) {
												
						// Add Document Path : pgaleas
						doc.add(new Field("filename", file.getCanonicalPath(), Field.Store.YES, null));
						
						
						writer.addDocument(doc);
					} 
					else {
						
						System.err.println("Cannot handle "	+ file.getAbsolutePath() + "; skipping");
					}
				} 
				
				catch (FileHandlerException e) {
					System.err.println("unsere exc. + " + e);
				}
				catch (IOException e) {
					System.err.println("Cannot index " + file.getAbsolutePath()
							+ "; skipping (" + e.getMessage() + ")");
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			usage();
			System.exit(0);
		}

		// PG: Set a basic configuration for log4j
		// Eliminate the log4j messages
		org.apache.log4j.BasicConfigurator.configure();
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);

		Properties props = new Properties();
		//String properties_path = "./handler.properties";
		String properties_path = "E:\\lucene\\repository\\src\\galeas\\org\\galeas\\index\\handlingtypes\\framework\\handler.properties";
		// props.load(new FileInputStream(args[0]));
		props.load(new FileInputStream(properties_path));

		Directory dir = FSDirectory.getDirectory(args[2], true);

		
		IndexWriter writer;
		if (args[0].equalsIgnoreCase("st")) {
			StandardAnalyzer stAnalyzer = new StandardAnalyzer();
			writer = new IndexWriter(dir, stAnalyzer, true);
		} 
		else {
			String analyzer_typ = null;
			if (args[0].equalsIgnoreCase("de"))
				analyzer_typ = "German";
			else if (args[0].equalsIgnoreCase("en"))
				analyzer_typ = "English";
			SnowballAnalyzer SBanalyzer = new SnowballAnalyzer(analyzer_typ);
			 writer = new IndexWriter(dir, SBanalyzer, true);
		}

		

		FileIndexer indexer = new FileIndexer(props);

		long start = new Date().getTime();
		indexer.index(writer, new File(args[1]));
		writer.optimize();
		writer.close();
		long end = new Date().getTime();

		System.out.println();
		IndexReader reader = IndexReader.open(dir);
		System.out.println("Documents indexed: " + reader.numDocs());
		System.out.println("Total time: " + (end - start) + " ms");
		reader.close();
	}

	private static void usage() {
		System.err.println("USAGE: java " + FileIndexer.class.getName()
				+ " /path/to/properties /path/to/file/or/directory"
				+ " /path/to/index");
	}
}
