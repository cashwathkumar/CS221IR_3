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
import java.lang.Math;
public class SearchEngine {

	private static HashMap<String, Payload> wordIndex = new HashMap<String, Payload>();
	private static HashMap<Integer, UrlInfo> urlIndex = new HashMap<Integer, UrlInfo>();
	
	private static HashMap<Integer, Float> docScoreMap = new HashMap<Integer, Float>();
	
	private static void loadIndex() throws IOException
	{
		FileInputStream wIn = new FileInputStream("Windex");
		FileInputStream uIn = new FileInputStream("Uindex");
		
		ObjectInputStream oIn = new ObjectInputStream(wIn);
		System.out.println("about to read indices");
		try
		{
			wordIndex = (HashMap<String, Payload>)oIn.readObject();
			
			System.out.println("word index done");
			oIn = new ObjectInputStream(uIn);
			
			urlIndex = (HashMap<Integer, UrlInfo>)oIn.readObject();
			System.out.println("URL index done");
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
		
		System.out.println("Enter Query:  ( enter \"exit\" to leave)");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String query = getQuery(br);
		for(int i = 0;query.equals("exit");query = getQuery(br), i++)
		{
					
			rankDocs(query);
			
			displayResults();
			
			clearScores();
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
		if(tokenPayload!=null)
		{
			List<DocInfo> docList = tokenPayload.getDocList();
	
			float idf = (float)tokenPayload.getIDF();
	
			for(int i = 0; i < docList.size(); i++)
			{
				DocInfo doc = docList.get(i);
				int docId = doc.getDocId();
				int tf = doc.getFreq();
				float score = 0;
	
				if(docScoreMap.containsKey(docId))
				{
					score = docScoreMap.get(docId);
					score += idf *((float)Math.log(1+ tf));
	
					docScoreMap.put(docId, score);
				}
				else
				{
					score = idf *((float)(Math.log(1+ tf)));
					docScoreMap.put(docId, score);
				}
			}
		}
	}
	
	
	private static void displayResults()
	{
		PriorityQueue<DocScore> topDocs = new PriorityQueue<DocScore>(10, new Comparator<DocScore>(){
			public int compare(DocScore a, DocScore b)
			{
				if(a.docScore - b.docScore>0)
				return 1;
				if(a.docScore - b.docScore==0)
					return 0;
				return -1;
			}
		});
		
		Set<Integer> docs = docScoreMap.keySet();
		
		int resultSize = 0;
		
		/* Get the top documents*/
		for(int docId : docs)
		{
			float currentDocScore = docScoreMap.get(docId);
			
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
	
	private static void clearScores()
	{
		docScoreMap.clear();
		//docScoreMap = new HashMap<Integer, Float>();
	}
}
