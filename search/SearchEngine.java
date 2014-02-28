package search;

import index.DocInfo;
import index.Payload;
import index.UrlInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SearchEngine {

	private static HashMap<String, Payload> wordIndex = new HashMap<String, Payload>();
	private static HashMap<Integer, UrlInfo> urlIndex = new HashMap<Integer, UrlInfo>();
	
	private static HashMap<Integer, Integer> docScoreMap = new HashMap<Integer, Integer>();
	
	private static void loadIndex() throws IOException
	{
		FileInputStream wIn = new FileInputStream("Windex");
		FileInputStream uIn = new FileInputStream("Uindex");
		
		ObjectInputStream oIn = new ObjectInputStream(wIn);
		
		try
		{
			wordIndex = (HashMap<String, Payload>)oIn.readObject();
			
			oIn = new ObjectInputStream(uIn);
			
			urlIndex = (HashMap<Integer, UrlInfo>)oIn.readObject();
		} 
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		wIn.close();
		uIn.close();
	}
	
	public static void main(String args[]) throws IOException
	{
		
		loadIndex();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		for(int i = 0; i < 10; i++)
		{
			String query = getQuery(br);
			
			rankDocs(query);
			
			displayResults();
		}
	}
	
	private static String getQuery(BufferedReader br) throws IOException
	{
		System.out.println("Enter Query: ");
		
		return br.readLine();
	}
	
	private static void rankDocs(String input)
	{
		String query = input.toLowerCase();
		
		String[] tokens = query.split("[^a-z0-9]+");
		
		/*calculate score for each document containing the token */
		for(String token : tokens)
		{
			calculateDocScores(token);
		}
	}
	
	private static void calculateDocScores(String token)
	{
		Payload tokenPayload = wordIndex.get(token);
		
		List<DocInfo> docList = tokenPayload.getDocList();
		
		int idf = (int)tokenPayload.getIDF();
		
		for(int i = 0; (i < 100) && (i < docList.size()); i++)
		{
			DocInfo doc = docList.get(i);
			int docId = doc.getDocId();
			int tf = doc.getFreq();
			int score = 0;
			
			if(docScoreMap.containsKey(docId))
			{
				score = docScoreMap.get(docId);
				score += idf * tf;
				
				docScoreMap.put(docId, score);
			}
			else
			{
				score = idf * tf;
				docScoreMap.put(docId, score);
			}
		}
	}
	
	
	private static void displayResults()
	{
		TreeSet<DocScore> topDocs = new TreeSet<DocScore>(new Comparator<DocScore>(){
			public int compare(DocScore a, DocScore b)
			{
				return b.docScore - a.docScore;
			}
		});
		
		Set<Integer> docs = docScoreMap.keySet();
		
		int resultSize = 0;
		
		/* Get the top documents*/
		for(int docId : docs)
		{
			int currentDocScore = docScoreMap.get(docId);
			
			if(resultSize < 10)
			{
				topDocs.add(new DocScore(docId, currentDocScore));
				resultSize++;
			}
			else
			{
				DocScore leastDScoreObj = topDocs.last();
				
				if(leastDScoreObj.docScore < currentDocScore)
				{
					topDocs.remove(leastDScoreObj);
					topDocs.add(new DocScore(docId, currentDocScore));	
				}
			}
		}
		
		/* display the urls*/
		for(int i = 0; i < topDocs.size(); i++)
		{
			DocScore dScore = topDocs.pollFirst();
			
			System.out.println(urlIndex.get(dScore.docId).getUrl());
		}
	}
}
