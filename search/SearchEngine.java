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
import java.util.PriorityQueue;
import java.util.Set;

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
		
		System.out.println("Enter Query: ");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		for(int i = 0; i < 3; i++)
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
		PriorityQueue<DocScore> topDocs = new PriorityQueue<DocScore>(10, new Comparator<DocScore>(){
			public int compare(DocScore a, DocScore b)
			{
				return a.docScore - b.docScore;
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
				DocScore leastDScoreObj = topDocs.peek();
				
				if(leastDScoreObj.docScore < currentDocScore)
				{
					topDocs.remove(leastDScoreObj);
					topDocs.add(new DocScore(docId, currentDocScore));	
				}
			}
		}
		
		/* display the urls*/
		DocScore[] dScores = new DocScore[topDocs.size()];
		
		for(int i = topDocs.size() - 1; !topDocs.isEmpty(); i--)
		{
			dScores[i] = topDocs.poll();
		}
		
		for(int i = 0; i < dScores.length; i++)
		{	
			System.out.println(urlIndex.get(dScores[i].docId).getUrl());
		}
	}
}
