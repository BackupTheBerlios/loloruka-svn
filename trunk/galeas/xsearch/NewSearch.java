package org.galeas.xsearch;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.galeas.comparable.XtermWordComparator;
import org.galeas.utils.ConfigurationManager;
import org.galeas.utils.Stopwords;
import org.galeas.xgraphics.SearchGraph;

/**
 * @author patricio
 * 
 */
public class NewSearch {

	private String propertiesFile = "/home/patricio/TREC_Evaluation/conf/properties.xml";

	public Properties properties = new Properties();

	public IndexReader indexReader;

	private IndexSearcher indexSearcher;

	private String userQuery;

	private int queryID;

	private Query parsedQuery;

	private QueryParser queryParser;

	private Hits initialHits;

	private List dispersionHits = new ArrayList();
	
	private List dispersionHitsPlus = new ArrayList();

	private List expandedQuery = new ArrayList();

	public NewSearch() throws IOException {

		/* Load the program configuration */
		loadProperties();

		/* Specify the index directory */
		String indexDirectory = this.properties.getProperty("indexdir").trim();

		String queryOperator = this.properties.getProperty("search_query_operator").trim();
		
		System.out.println("indexDirectory:" + indexDirectory);

		Directory fsDir = FSDirectory.getDirectory(new File(indexDirectory),
				false);

		indexReader = IndexReader.open(fsDir);

		/* Set the index searcher */
		indexSearcher = new IndexSearcher(fsDir);

		/* Set the query Analyzer defined in the properties file */
		queryParser = setQueryParser();

		/* Define the standard operator fpr the query words */
		if(queryOperator.equalsIgnoreCase("AND")) {
			queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		}
		
		
	}

	/**
	 * Define the query QueryParser (Analyzer) defined in the properties file
	 * 
	 * @return QueryParser
	 * @throws IOException
	 */
	private QueryParser setQueryParser() throws IOException {

		// Get the analyzer type for parse query
		String stopFile = this.properties.getProperty("stopword_file").trim();
		File stopWordsFile = new File(stopFile);
		String AnalyzerType = this.properties.getProperty("analyzer").trim();

		// Define the document Fields that will be searched
		String[] searchedFields = properties.getProperty("search_fields")
				.split(",");

		// Initialize Objects
		Analyzer StandardQueryAnalyzer;
		SnowballAnalyzer SnowballQueryAnalyzer;
		QueryParser queryParser;

		// If the "Standard" Query Analyzer was defined
		if (AnalyzerType.equalsIgnoreCase("Standard")) {
			// ... with stopwords
			if (stopWordsFile.exists()) {
				StandardQueryAnalyzer = new StandardAnalyzer(stopWordsFile);
			}
			// ... without stopwords
			else {
				StandardQueryAnalyzer = new StandardAnalyzer();
			}
			// Set the query parser
			queryParser = new MultiFieldQueryParser(searchedFields,
					StandardQueryAnalyzer);
		}
		// If the "Stemmed" Query Analyzer was defined
		else {
			// ... with stopwords
			if (stopWordsFile.exists()) {
				SnowballQueryAnalyzer = new SnowballAnalyzer(AnalyzerType);
			}
			// ... without stopwords
			else {

				SnowballQueryAnalyzer = new SnowballAnalyzer(AnalyzerType,
						new Stopwords(stopWordsFile).getStopArray());
			}
			// Set the query parser
			queryParser = new MultiFieldQueryParser(searchedFields,
					SnowballQueryAnalyzer);
		}

		return queryParser;
	}

	/**
	 * Get a String with the stopword defined in a file
	 * 
	 * @param stopFile
	 * @return
	 * @throws IOException
	 */
	/*
	 * private String[] stopFileToArray(File stopFile) throws IOException {
	 * 
	 * BufferedReader in = new BufferedReader(new FileReader(stopFile)); String
	 * str; List stopList = new ArrayList(); while ((str = in.readLine()) !=
	 * null) { stopList.add(str); } in.close(); String[] stopArray = (String[])
	 * stopList.toArray(); return stopArray;
	 *  }
	 */

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

	/**
	 * Generate the standard Lucene (TFIDF) search an save the results in
	 * initialHits
	 * 
	 * @param queryString
	 * @throws IOException
	 * @throws ParseException
	 */
	public void search(String queryString) throws IOException, ParseException {

		System.out.println("\nqueryString:" + queryString);

		this.userQuery = queryString;

		/* Parse the query */
		parsedQuery = queryParser.parse(queryString);

		
		/* Execute the parsed query */
		initialHits = indexSearcher.search(parsedQuery);

		
		/* Reset the dispersionHits lists */
		dispersionHits.clear();
		dispersionHitsPlus.clear();
		
	}

	public void search(String queryString, int queryID) throws IOException,
			ParseException {
		this.queryID = queryID;
		search(queryString);
	}

	
	/* Calculate the expanded query terms based on the Dispersion Ranking */
	private void setExpandedQuery() {

		this.expandedQuery.clear();
		
		int expandedQuerySize = Integer.valueOf(
				properties.getProperty("expandedquery_size")).intValue();

		
		int expandedQueryTopDocs = Integer.valueOf(
				properties.getProperty("expandedquery_with_top_docs")).intValue();		
		
		
		
		if(dispersionHits.size() == 0) {
			System.out.println("Dispersion Ranking is empty! ...\n" +
					"The ExpandedQuery based on the results of the DispersionRanking.");
		}
		Iterator i = dispersionHits.iterator();
		int counter = 0;
		while ( i.hasNext() && (counter++ < expandedQueryTopDocs)) {

			Xhit actualHit = (Xhit) i.next();

			List actualHitExpandedQuery = actualHit.getExtendedQuery(
					new XqueryUtils().queryWordsToArray(this.userQuery),
					expandedQuerySize);
			
			
			addTermsToExpandedQuery(actualHitExpandedQuery);
			
			//System.out.println("addHitToExpandedQuery :"+counter);
			
		}

		// Sort the Expanded Query Terms
		Comparator comparator = Collections.reverseOrder();
		Collections.sort(this.expandedQuery, comparator);
	}

	public List getExpandedQuery() {

		int expandedQuerySize = Integer.valueOf(
				properties.getProperty("expandedquery_size")).intValue();


		setExpandedQuery();
		
		/*Send a message if the Query Expansion calculations returns null values */
		if(this.expandedQuery.size() == 0) {
			System.out.println("getExpandedQuery() returns 0 terms ...");
		}
		
		
		List limitedList;
		if (this.expandedQuery.size() > expandedQuerySize) {
			limitedList = this.expandedQuery.subList(0, expandedQuerySize);
		} else {
			limitedList = this.expandedQuery;
		}

		return limitedList;
	}
	
	
	public String[] getExpandedQueryArray() {
		List expandedQueryList = getExpandedQuery();
		Iterator it = expandedQueryList.iterator();
		String[] queryWordArray = new String[expandedQueryList.size()];
		
		while(it.hasNext()) {
			Xterm queryTerm = (Xterm)it.next();
			String queryWord = queryTerm.GetTermWord();
			queryWordArray[expandedQueryList.indexOf(queryTerm)] = queryWord;
		}
		return queryWordArray;
	}
	
	
	public String getExpandedQueryString() {
		
		String[] expandedQueryArray = this.getExpandedQueryArray();
		String expandedQueryString = "";
		for(int i=0; i<expandedQueryArray.length; i++) {
			expandedQueryString += expandedQueryArray[i]+ " ";
		}
		
		return expandedQueryString;
	}

	
	public void printExpandedQuery() {

		List expandedQuery = getExpandedQuery();

		Iterator expandedQueryIt = expandedQuery.iterator();
		
		while (expandedQueryIt.hasNext()) {
			Xterm actualExpandedTerm = (Xterm) expandedQueryIt.next();
			System.out.println("ExpQuery:" + actualExpandedTerm.GetTermWord()
					+ " [" + actualExpandedTerm.GetIntersection() + "]");
		}
	}

	private List getStemmedExpandedQuery() {

		if (this.expandedQuery.isEmpty()) {
			setExpandedQuery();
		}

		/* Initialize Objects */
		PorterStemmer stemmer = new PorterStemmer();
		List StemmedExpandedQuery = new ArrayList();
		List addedTermsList = new ArrayList();

		/* First Iteartor over actual expanded query */
		Iterator it1 = this.expandedQuery.iterator();

		while (it1.hasNext()) {
			Xterm actualXterm1 = (Xterm) it1.next();

			Collections.sort(addedTermsList);
			if (Collections.binarySearch(addedTermsList, actualXterm1,
					new XtermWordComparator()) < 0) {

				/* Add a clone of Term1 to StemmedExpandedQuery */
				Xterm newXterm = (Xterm) actualXterm1.clone();

				/* Get the word of the Term1 */
				String word1 = actualXterm1.GetTermWord();

				/* Second Iteartor over actual expanded query */
				Iterator it2 = this.expandedQuery.iterator();
				while (it2.hasNext()) {
					Xterm actualXterm2 = (Xterm) it2.next();
					String word2 = actualXterm2.GetTermWord();

					/* Sort the actual addedTermsList for searching */
					Collections.sort(addedTermsList, new XtermWordComparator());

					/*
					 * If words stems are equal : i) Concatenates the terms
					 * (Strings) ii) Adds their intersection values
					 */
					String word1Stemm = stemmer.stem(word1);
					String word2Stemm = stemmer.stem(word2);

					if ((Collections.binarySearch(addedTermsList, actualXterm1,
							new XtermWordComparator()) < 0)
							&& (word1.compareTo(word2) != 0)
							&& (word1Stemm.compareTo(word2Stemm) == 0)
							&& (word1Stemm.compareTo("Invalid term") < 0)
							&& (word2Stemm.compareTo("Invalid term") < 0)) {

						/* concatenate their words */
						newXterm.AddTermWord(word2);

						/* add their intersection values */
						newXterm.AddIntersectionValue(actualXterm2
								.GetIntersection());

						/* Save the actualized term to addedTermsList */
						addedTermsList.add(actualXterm2);
					}
				}

				/* add the new composed term to the StemmedExpandedQuery list */
				StemmedExpandedQuery.add(newXterm);
			}

		}

		/* sort the StemmedExpandedQuery list */
		Comparator comparator = Collections.reverseOrder();
		Collections.sort(StemmedExpandedQuery, comparator);

		return StemmedExpandedQuery;
	}

	
	public void printStemmedExpandedQuery() {

		List StemmedExpandedQuery = getStemmedExpandedQuery();
		Iterator it = StemmedExpandedQuery.iterator();
		while (it.hasNext()) {
			Xterm actualXterm1 = (Xterm) it.next();
			System.out.println("StemmedQuery : " + actualXterm1.GetTermWord()
					+ "  - " + actualXterm1.GetIntersection());
		}
	}

	
	private void addTermsToExpandedQuery(List newXterms) {

		// Sort the elements in expandedQuery for searching
		if (!expandedQuery.isEmpty()) {
			Collections.sort(this.expandedQuery, new XtermWordComparator());
		}

		if (expandedQuery.isEmpty()) {
			Iterator exIt = newXterms.iterator();
			while (exIt.hasNext()) {
				
				Xterm exTerm = (Xterm) exIt.next();

				if (exTerm.GetIntersection() > 0) {
					this.expandedQuery.add(exTerm);
				}

			}
		} 
		else {

			Iterator newTermsIt = newXterms.iterator();
			while (newTermsIt.hasNext()) {
				Xterm newTerm = (Xterm) newTermsIt.next();
				double newTermIntersection = newTerm.GetIntersection();

				if (newTermIntersection > 0) {
					int newTermPositionInExtendedQuery = Collections
							.binarySearch(this.expandedQuery, newTerm,
									new XtermWordComparator());

					if (newTermPositionInExtendedQuery >= 0) {
						Xterm qTerm = (Xterm) this.expandedQuery
								.get(newTermPositionInExtendedQuery);
						qTerm.AddIntersectionValue(newTermIntersection);
					} else {
						this.expandedQuery.add(newTerm);
					}
				}

			}
		}
	}

	
	
	
	/* ----------------------------------------------------
	 * Return the most frequently query terms in the results 
	 * ----------------------------------------------------*/
	public QueryStatsOnResults getReducedQuery() throws IOException{	
		
		int nrOfHitsToCalculateReducedQuery = Integer.valueOf(
				properties.getProperty("documents_for_calculate_reduced_query")).intValue();		

		int nrInitialHits = this.initialHits.length();

		/* Limits the number of documents To Calculate Extended Ranking */
		int nrOfRankedDocuments = nrInitialHits;
		if (nrOfHitsToCalculateReducedQuery != -1 && nrOfHitsToCalculateReducedQuery < nrInitialHits)
			nrOfRankedDocuments = nrOfHitsToCalculateReducedQuery;

		
		/* Calculate an array with the query Terms */
		int q_counter=0;
		Set queryList = new HashSet();
		parsedQuery.extractTerms(queryList);
		int numberOfQueryTerms = queryList.size();
		String[] queryTermsArray = new String[numberOfQueryTerms];
		Iterator q_it = queryList.iterator();
		    while (q_it.hasNext()) {
		        // Get element
		        Term queryTerm = (Term)q_it.next();
		        queryTermsArray[q_counter++] = queryTerm.text();
		    }
		
		
		/* Create a vector containing the query words frequency 
		 * for the Ranking Documents */
		int[][] queryVectorFreq = new int[nrOfRankedDocuments][numberOfQueryTerms];
		
		/* ----------------------------------------------------------------
		 * Loop over TFIDF ranked documents 
		 * ----------------------------------------------------------------*/
		for (int dcounter = 0; dcounter < nrOfRankedDocuments; dcounter++) {
			int documentID = this.initialHits.id(dcounter);
			
			
			/* Vector with all Terms frequency and Positions in the document */
			TermPositionVector termPositionsVector = (TermPositionVector) this.indexReader
					.getTermFreqVector(documentID, "text");
			
			/* Vector with terms frequency in the document */
			int[] vectorTermFrequency = termPositionsVector.getTermFrequencies();			
			
			
			/* Vector with the position of the query terms in the document */
			int[] queryTermPositionsInFrequencyVector = termPositionsVector.indexesOf(queryTermsArray,0,numberOfQueryTerms);
	
			
			/* Get the frequency of the query terms in the document 
			 * and save them in the vector queryVectorFreq */
			for(int tcounter=0; tcounter<numberOfQueryTerms;tcounter++) {
				int queryWordPositionInFrequencyVector = queryTermPositionsInFrequencyVector[tcounter];
				if (queryWordPositionInFrequencyVector != -1) {
					queryVectorFreq[dcounter][tcounter] = vectorTermFrequency[queryWordPositionInFrequencyVector];	
				}
				else {
					queryVectorFreq[dcounter][tcounter] = 0;
				}
				
			}
		}
		/* ----------------------------------------------------------------*/
		
		
		
		/*--------------------------------------------------------------
		 *  calculate the total query words frequency 
		 *  for the current TFIDF ranking 
		 *--------------------------------------------------------------*/
		
		int[] totalQueryTermsFrequency = new int[numberOfQueryTerms];
	
	    QueryStatsOnResults reducedQuery = new QueryStatsOnResults();
	    
		for(int tcounter=0; tcounter<numberOfQueryTerms;tcounter++) {
			for(int dcounter=0; dcounter<nrOfRankedDocuments;dcounter++) {
				
				totalQueryTermsFrequency[tcounter] += queryVectorFreq[dcounter][tcounter];

				RqueryTerm qt = new RqueryTerm(queryTermsArray[tcounter], totalQueryTermsFrequency[tcounter]);
				
				
				
				reducedQuery.addQueryTerm(qt);
			}			
		}
		/*--------------------------------------------------------------*/
		
		return reducedQuery;
		
	}
	
	
	
	
	
	public void calculateDispersionRankingPlus() throws IOException, Exception {
		
		/* --------------------------------------------------
		 * Check if DispersionRanking not yet calculated
		 * If NOT, --> the DispersionRanking will be calculated 
		 * --------------------------------------------------*/		
		if(this.dispersionHits.size()==0) {
			this.calculateDispersionRanking();
		}
		/* --------------------------------------------------*/
		
		
		/* --------------------------------------------------
		 * Initialize Search Parameters 
		 * --------------------------------------------------*/
		/* Get the terms for the expanden query */
		String[] expandedQueryArray = getExpandedQueryArray();
		
		/* Calculate an array with the user query */
		String[] userQueryArray = new XqueryUtils().queryWordsToArray(this.userQuery);

		/* Get the comparation criterium for dispersion rank */
		String dispersion_rank_comparator = properties.getProperty("dispersion_rank_comparator");
		
		/* Get the dispersion Model value */
		String dispersion_model = properties.getProperty("dispersion_model");
		
		/* Get the dispersion Weighting value */
		double dispersion_rank_weight = Double.valueOf(	properties.getProperty("dispersion_rank_weight")).doubleValue();		
		/* --------------------------------------------------*/
		
		
		/* --------------------------------------------------
		 * Calculate the extended Query (user query + expanded words)
		 * --------------------------------------------------*/
		String[] wordsSet = new String[userQueryArray.length + expandedQueryArray.length];
		System.arraycopy(userQueryArray, 0, wordsSet, 0, userQueryArray.length);
		System.arraycopy(expandedQueryArray, 0, wordsSet, userQueryArray.length, expandedQueryArray.length);
		
		/* --------------------------------------------------*/	
	
		
		
		/* --------------------------------------------------
		 * Cycle over all Dispersion Hits and
		 * calculate the ranking with the new query 
		 * --------------------------------------------------*/
		Iterator it = dispersionHits.iterator();
		while(it.hasNext()) {
			Xhit actualXhit = (Xhit)it.next();
			
			actualXhit.applyRanking(wordsSet, dispersion_rank_comparator,	
					dispersion_model, dispersion_rank_weight);	
			
			/* ---------------------------------------------------
			 * Add the actual Hit to extendedHitsPlus
			 * ---------------------------------------------------*/
			this.dispersionHitsPlus.add(actualXhit);
			/* ---------------------------------------------------*/			
		}
		/* --------------------------------------------------*/
	
	}
	
	
	
	
	
	
	public void calculateDispersionRanking() throws IOException,
			java.text.ParseException {

		/* Read the file with the Xterms stopwords 
		 * and create a sorted array with them */
		Stopwords stopwordsXT = new Stopwords(properties.getProperty("stopword_for_xterms"));
		String[] stopwordForXterms = stopwordsXT.getStopArray();
		Arrays.sort(stopwordForXterms);
		
		int nrOfExtendedHits = Integer.valueOf(
				properties.getProperty("nr_of_extendedhits")).intValue();

		int nrInitialHits = this.initialHits.length();

		/* Limits the number of documents To Calculate Extended Ranking */
		int nrOfRankedDocuments = nrInitialHits;
		if (nrOfExtendedHits != -1 && nrOfExtendedHits < nrInitialHits)
			nrOfRankedDocuments = nrOfExtendedHits;

		System.out.println("nrOfRankedDocuments : "+nrOfRankedDocuments);
		
		for (int i = 0; i < nrOfRankedDocuments; i++) {
			int documentID = this.initialHits.id(i);
			
			/*
			 * Get vector with all Terms frequency and Positions in actual
			 * document (docID)
			 */
			TermPositionVector termPositionsVector = (TermPositionVector) this.indexReader
					.getTermFreqVector(documentID, "text");

			String[] vectorTermString = termPositionsVector.getTerms();

			int[] vectorTermFrequency = termPositionsVector
					.getTermFrequencies();

			List documentXterms = new ArrayList();

			/* CHECK THE OUTPUT */
			/*
			String doc = this.initialHits.doc(i).getField("docno").toString();
			System.out.println("---------------------------------------");
			System.out.println("doc:" + doc);			
			*/
			
			for (int j = 0; j < vectorTermString.length; j++) {

				/* Word */
				String Word = vectorTermString[j];

				
				/* STOWORDS CONDITION :
				 * if the word is not in the stopwords_expanded_query.txt 
				 * define it as Xterm */
				if(Arrays.binarySearch(stopwordForXterms, Word)<0)
				{	
					/* Term Positions */
					int[] TermPositions = termPositionsVector.getTermPositions(j);

					/* TermFrequency */
					String Freq = String.valueOf(vectorTermFrequency[j]);

					/* Term Maximal Position */
					Arrays.sort(TermPositions);
					String maximalPosition = String
							.valueOf(TermPositions[TermPositions.length - 1]);

					/* Get the initialStatValues (Center and Spread) */
					String[] initialStatValues = getStatisticsArray(TermPositions);

					/* Create an array with the TermFrequency and maximalPosition */
					String[] addedStatValues = { Freq, maximalPosition };

					/* Define the array with all statistical values */
					String[] StatValues = new String[initialStatValues.length
							+ addedStatValues.length];

					/* Set the array StatValues (add two arrays in StatValues) */
					System.arraycopy(initialStatValues, 0, StatValues, 0,
							initialStatValues.length);
					System.arraycopy(addedStatValues, 0, StatValues,
							initialStatValues.length, addedStatValues.length);

					/* Set the Xterm */
					Xterm newXterm = new Xterm(Word, StatValues);

					/* Add newXterm to documentXterms */
					//documentXterms.add(j, newXterm);
					documentXterms.add(newXterm);
					
					/* CHECK THE OUTPUT */
					/*String stringT=""; 
					for(int t=0;t<TermPositions.length;t++) {
					stringT += TermPositions[t]+","; } String stringT2="";
					for(int t=0;t<StatValues.length;t++) { stringT2 =
					"[C:"+StatValues[0]+"]"+ "[S:"+StatValues[1]+"]"+
					"[F:"+StatValues[2]+"]"+ "[M:"+StatValues[3]+"]"; }
					//System.out.println("Word:"+Word+" "+stringT2+" ("+stringT+")");				
					System.out.println(Word+" : "+stringT2);*/
					
				}

			}
			
			
			

			/* Create the Xhit for the corresponding document */
			Xhit actualXhit = new Xhit(documentID, documentXterms,
					this.initialHits.score(i));

			
			/* ---------------------------------------------------
			 * Set the Ranking for actualXhit 
			 * ---------------------------------------------------*/
			String[] userQueryArray = new XqueryUtils()
					.queryWordsToArray(this.userQuery);
			
			/* Get the comparation criterium for dispersion rank */
			String dispersion_rank_comparator = properties
					.getProperty("dispersion_rank_comparator");
			
			/* Get the dispersion Weighting value */
			double dispersion_rank_weight = Double.valueOf(
					properties.getProperty("dispersion_rank_weight"))
					.doubleValue();
			
			/* Get the dispersion Model value */
			String dispersion_model = properties.getProperty("dispersion_model");
//			String dispersion_wordset = properties.getProperty("dispersion_wordset");
			
			
			/* ----------------------------------------------------
			 * Define the Terms used for the Dispersion Ranking
			 * and 
			 * Calculate the dispersion ranking for the actual Hit
			 * ---------------------------------------------------- */
			actualXhit.applyRanking(userQueryArray, dispersion_rank_comparator,	
					dispersion_model, dispersion_rank_weight);	
			
//			if(dispersion_wordset.equalsIgnoreCase("only_query")) {
//				actualXhit.applyRanking(userQueryArray, dispersion_rank_comparator,	
//						dispersion_model, dispersion_rank_weight);
//			}
//			else {
//				/* Get the terms for the expanden query */
//				String[] expandedQueryArray = getExpandedQueryArray();
//				if(dispersion_wordset.equalsIgnoreCase("expanded_query")) {
//					String[] wordsSet = new String[userQueryArray.length + expandedQueryArray.length];
//					System.arraycopy(userQueryArray, 0, wordsSet, 0, userQueryArray.length);
//					System.arraycopy(expandedQueryArray, 0, wordsSet, userQueryArray.length, expandedQueryArray.length);
//					actualXhit.applyRanking(wordsSet, dispersion_rank_comparator,	
//							dispersion_model, dispersion_rank_weight);				
//				}
//			}
			/* ---------------------------------------------------- */
			
			

			/* ---------------------------------------------------
			 * Add the actual Hit to extendedHits 
			 * ---------------------------------------------------*/
			this.dispersionHits.add(actualXhit);
			/* ---------------------------------------------------*/

		}
	}

	
	
	public String getDispersionHitsPlus() throws Exception {

		String results = "";

		/* Calculate Extended Search */
		this.calculateDispersionRankingPlus();

		/* Sort Extended Hits */
		sortDispersionHitsPlus();

		int numberOfHits = this.dispersionHitsPlus.size();

		/* initialize the word-distribution graph */
		int max_docs_to_graph = Integer.valueOf(properties.getProperty("documents_to_graph")).intValue();
		
		int graph_dispersion_rank = Integer.valueOf(properties.getProperty("graph_dispersion_rank")).intValue();
		
		String graph_title = properties.getProperty("graph_title");
		
		int documents_to_graph = numberOfHits;
		
		if (max_docs_to_graph < numberOfHits) {
			documents_to_graph = max_docs_to_graph;
		}
		
		GraphicData[] documentsGraphicData = new GraphicData[documents_to_graph];

		Iterator it = this.dispersionHitsPlus.iterator();
		int counter = 0;
		while (it.hasNext()) {

			Xhit actualHit = (Xhit) it.next();

			Document actualDocument = indexSearcher.doc(actualHit
					.getDocumentID());

			/*
			 * Standard Format for TREC results <qid> <iter> <docno> <rank>
			 * <sim> <run_id>
			 */
			int qid = this.queryID;
			String iter = "qEXTplus";
			String docno = actualDocument.getField("docno").stringValue();
			int rank = 0;
			double sim = actualHit.getRanking();
			String run_id = "galeas1";

			/*
			 * get the graphical data of actual document, to be used in the
			 * query-terms distribution graphic
			 */
			if (counter < documents_to_graph && graph_dispersion_rank == 1) {
				documentsGraphicData[counter++] = this.getDocumentGraphData(
						actualHit.getDocumentID(), docno);
			}

			/* Print the results */
			results += qid + " " + iter + " " + docno + " " + rank + " " + sim
					+ " " + run_id + "\n";

		}

		/*
		 * ------------------------------------------- create the query-words
		 * distribution graphic -------------------------------------------
		 */
		if (graph_dispersion_rank == 1) {
			String windowTitle = graph_title;
			String graphTitle = "Query-Terms Distribution: DISPERSION-Plus Ranking";
			SearchGraph frame = new SearchGraph(windowTitle, graphTitle,
					documentsGraphicData);
		}
		/* ------------------------------------------- */

		/* Return the strout results */
		return results;

	}	

	
	
	
	
	
	
	
	
	
	
	
	public String getDispersionHits() throws IOException,
			java.text.ParseException {

		String results = "";
		
		/* Calculate Extended Search */
		this.calculateDispersionRanking();		

		/* Sort Extended Hits */
		sortDispersionHits();

		int numberOfHits = this.dispersionHits.size();

		/* initialize the word-distribution graph */
		int max_docs_to_graph = Integer.valueOf(
				properties.getProperty("documents_to_graph")).intValue();
		int graph_dispersion_rank = Integer.valueOf(
				properties.getProperty("graph_dispersion_rank")).intValue();
		String graph_title = properties.getProperty("graph_title");
		int documents_to_graph = numberOfHits;
		;
		if (max_docs_to_graph < numberOfHits) {
			documents_to_graph = max_docs_to_graph;
		}
		GraphicData[] documentsGraphicData = new GraphicData[documents_to_graph];

		Iterator it = this.dispersionHits.iterator();
		int counter = 0;
		while (it.hasNext()) {

			Xhit actualHit = (Xhit) it.next();

			Document actualDocument = indexSearcher.doc(actualHit
					.getDocumentID());

			/*
			 * Standard Format for TREC results <qid> <iter> <docno> <rank>
			 * <sim> <run_id>
			 */
			int qid = this.queryID;
			String iter = "qEXT";
			String docno = actualDocument.getField("docno").stringValue();
			int rank = 0;
			double sim = actualHit.getRanking();
			String run_id = "galeas1";

			/*
			 * get the graphical data of actual document, to be used in the
			 * query-terms distribution graphic
			 */
			if (counter < documents_to_graph && graph_dispersion_rank == 1) {
				documentsGraphicData[counter++] = this.getDocumentGraphData(
						actualHit.getDocumentID(), docno);
			}

			/* Print the results */
			results += qid + " " + iter + " " + docno + " " + rank + " " + sim
					+ " " + run_id + "\n";

		}

		/* ------------------------------------------- 
		 * create the query-words distribution graphic 
		 * ------------------------------------------- */
		if(graph_dispersion_rank == 1) {
			String windowTitle = graph_title;
			String graphTitle = "Query-Terms Distribution: DISPERSION Ranking";
			SearchGraph frame = new SearchGraph(windowTitle, graphTitle,
					documentsGraphicData);			
		}
		/* ------------------------------------------- */

		/* Return the strout results */
		return results;

	}
	
	
	
	
	
	
	
	public String getExpandedHits() {
		
		String[] expandedQueryArray = this.getExpandedQueryArray();
		
		return null;
	}
	

	
	
	

	private void sortDispersionHits() {

		Comparator comparator = Collections.reverseOrder();
		Collections.sort(this.dispersionHits, comparator);
	}
	
	private void sortDispersionHitsPlus() {

		Comparator comparator = Collections.reverseOrder();
		Collections.sort(this.dispersionHitsPlus, comparator);
	}	
	

	public void printDispersionHits() throws IOException,
			java.text.ParseException {
		String results = getDispersionHits();
		/* Print the strout results */
		System.out.print(results);
	}

	/**
	 * @throws Exception
	 *             Get the Initial Document Hits
	 */
	public String getInitialHits() throws Exception {

		String results = "";
		int numberOfHits = this.initialHits.length();

		/* initialize the word-distribution graph */
		int max_docs_to_graph = Integer.valueOf(
				properties.getProperty("documents_to_graph")).intValue();
		int graph_tfidf_rank = Integer.valueOf(
				properties.getProperty("graph_tfidf_rank")).intValue();
		String graph_title = properties.getProperty("graph_title");
		int documents_to_graph = numberOfHits;
		;
		if (max_docs_to_graph < numberOfHits) {
			documents_to_graph = max_docs_to_graph;
		}
		GraphicData[] documentsGraphicData = new GraphicData[documents_to_graph];

		// Print out the results
		for (int i = 0; i < numberOfHits; i++) {

			Document actualDocument = this.initialHits.doc(i);

			/*
			 * Standard Format for TREC results <qid> <iter> <docno> <rank>
			 * <sim> <run_id>
			 */
			int qid = this.queryID;
			String iter = "qSTD";
			String docno = actualDocument.getField("docno").stringValue();
			int rank = 0;
			double sim = initialHits.score(i);
			String run_id = "galeas1";

			/*
			 * get the graphical data of actual document, to be used in the
			 * query-terms distribution graphic
			 */
			if (i < documents_to_graph && graph_tfidf_rank == 1) {
				documentsGraphicData[i] = this.getDocumentGraphData(
						this.initialHits.id(i), docno);
			}

			/* Print the results */
			results += qid + " " + iter + " " + docno + " " + rank + " " + sim
					+ " " + run_id + "\n";

		}

		/* ------------------------------------------- 
		 * create the query-words distribution graphic 
		 * ------------------------------------------- */
		if(graph_tfidf_rank == 1) {
			String windowTitle = graph_title;
			String graphTitle = "Query-Terms Distribution: TFIDF-Ranking";
			SearchGraph frame = new SearchGraph(windowTitle, graphTitle,
					documentsGraphicData);			
		}
		/* ------------------------------------------- */

		/* Return the strout results */
		return results;
	}

	public void printInitialHits() throws Exception {
		String results = getInitialHits();
		/* Print the strout results */
		System.out.print(results);
	}

	public void printAllStatTermsOfDocumentID(int docID) throws IOException {

		// Get vector with all Terms frequency and Positions in actual document
		// (docID)
		TermPositionVector termPositionsVector = (TermPositionVector) indexReader
				.getTermFreqVector(docID, "text");

		// Get an String Array with all Terms (Term-Stats-String) of docID
		// The Term-Stats-String contain three values separated with blank
		// spaces:
		// "[word] [word_center] [word_deviation]"
		String[] vectorTermString = termPositionsVector.getTerms();

		// LOOP over all Term-Stats-String in actual document (docID)
		for (int k = 0; k < vectorTermString.length; k++) {

			int[] termPositions = termPositionsVector.getTermPositions(k);
			String actualTermString = vectorTermString[k];
			System.out.println("");
			System.out.print(actualTermString);

			for (int i = 0; i < termPositions.length; i++) {
				System.out.print(" [" + termPositions[i] + "]");
			}
		}
	}

	private String[] getStatisticsArray(int[] word_positons) {

		DescriptiveStatistics stats = DescriptiveStatistics.newInstance();

		for (int positions_counter = 0; positions_counter < word_positons.length; positions_counter++) {
			Integer actualPosition = new Integer(
					word_positons[positions_counter]);
			double newvalue = actualPosition.doubleValue();
			stats.addValue(newvalue);
		}

		/* First approximation for Center and Spread */
		// double center = stats.getMean();
		// double spread = stats.getStandardDeviation();
		// String statsString = ","+formatter.format(mean)+"
		// "+formatter.format(std);
		/* Second approximation for Center and Spread */
		double p25 = stats.getPercentile(25);
		double p75 = stats.getPercentile(75);
		double delta = p75 - p25;
		double center = p25 + (delta / 2);
		double spread = delta / 2;

		NumberFormat formatter = new DecimalFormat("#.##");
		String[] statisticalValues = { formatter.format(center),
				formatter.format(spread) };

		return statisticalValues;
	}

	/**
	 * @return the parsed query
	 */
	public Query getParsedQuery() {
		return parsedQuery;
	}

	public String getUserQuery() {
		return this.userQuery;
	}

	public int getInitialHitsLenght() {
		return this.initialHits.length();
	}

	public String printParsedQuery() {
		return this.parsedQuery.toString();
	}

	public GraphicData getDocumentGraphData(int docID, String documentName)
			throws IOException {

		/*
		 * Get vector with all Terms frequency and Positions in actual document
		 * (docID)
		 */
		TermPositionVector termPositionsVector = (TermPositionVector) indexReader
				.getTermFreqVector(docID, "text");

		/* Get an String Array with all Terms of docID */
		String[] vectorTermString = termPositionsVector.getTerms();

		/* create a set with all query terms */
		HashSet queryTermsSet = new HashSet();
		parsedQuery.extractTerms(queryTermsSet);
		String[] queryTermsArray = new String[queryTermsSet.size()];
		Iterator tempIt = queryTermsSet.iterator();
		int c = 0;
		while (tempIt.hasNext()) {
			String[] temporalArray = tempIt.next().toString().split(":");
			queryTermsArray[c++] = temporalArray[1];
		}

		/* create the graphical data object */
		GraphicData graphData = new GraphicData();

		/* set document name for the graphic */
		graphData.setDocumentName(documentName);

		/* initialize document maximal positions variable */
		int document_max_positions = 0;

		// LOOP over all Terms in actual document
		for (int k = 0; k < vectorTermString.length; k++) {

			// term string
			String termString = vectorTermString[k];

			// term positions
			int[] termPositions = termPositionsVector.getTermPositions(k);

			// get the maximal position
			Arrays.sort(termPositions);
			int document_max_positions_temp = termPositions[termPositions.length - 1];
			if (document_max_positions_temp > document_max_positions) {
				document_max_positions = document_max_positions_temp;
			}

			/*
			 * if term is a query-term, get its positions from the document
			 */
			Arrays.sort(queryTermsArray);
			if (Arrays.binarySearch(queryTermsArray, termString) >= 0) {

				// add the queryTerm object to graphData
				XqueryTerm queryTerm = new XqueryTerm(termString, termPositions);
				graphData.addQueryTerm(queryTerm);
				/*
				 * System.out.print("ADD GRAPH-Data:"+queryTerm.getQuery()+" ->
				 * "); float[] tempo = queryTerm.getPositions(); for(int h=0;h<tempo.length;h++) {
				 * System.out.print("["+tempo[h]+"]"); } System.out.println("");
				 */
			}
		}

		/*
		 * add the maximal document position (document lenght) as last values of
		 * the results array
		 */
		graphData.setMaximaDocumentPosition(document_max_positions);

		return graphData;
	}
	

}
