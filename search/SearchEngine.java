package search;

import index.DocInfo;
import index.Payload;
import index.UrlInfo;
import index.Indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.lang.Math;
import java.util.ArrayList;
public class SearchEngine extends Indexer{

	private HashMap<String, Payload> wordIndex = new HashMap<String, Payload>();
	private HashMap<Integer, UrlInfo> urlIndex = new HashMap<Integer, UrlInfo>();
	
	public HashMap<Integer, Float> docScoreMap = new HashMap<Integer, Float>();
	
	public void loadIndex()
	{
		try
		{
			File f = new File("Test");
			System.out.println(f.getAbsolutePath());
			
			
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
		catch(FileNotFoundException e)
		{
			System.out.println("File not found");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
//		readFromFile();
	}
	
	public static void main(String args[]) throws IOException
	{
		SearchEngine s = new SearchEngine();
		Indexer in = new Indexer();
		in.readFromFile();
		
		s.wordIndex = in.wordIndex;
		s.urlIndex = in.urlIndex;
		
		System.out.println("length of word index"+s.wordIndex.size());
		System.out.println("length of word index"+s.urlIndex.size());
		
		System.out.println("Enter Query:  ( enter \"exit\" to leave)");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String query = s.getQuery(br);
	
		for(int i = 0;!query.equals("exit");query = s.getQuery(br), i++)
		{
			
			if(query.length()<2)continue;		
			s.rankDocs(query);
			
			DocResult[] docResults = s.getResults();
			
			s.printResults(docResults);
			
			s.clearScores();
		}
		System.out.println("End of search......");
	}
	
	public DocResult[] processQuery(String query)
	{
		if(query.length() < 2)
			return null;
		
		rankDocs(query);
		
		DocResult[] docResults = getResults();
		
		this.clearScores();
		
		return docResults;
	}
	
	private String getQuery(BufferedReader br) throws IOException
	{
		System.out.println("Enter Query: ");
		
		return br.readLine();
	}
	private String[] removeStopWords(String[] token)
	{
		ArrayList<String> ProcTokens=new ArrayList<String>();
		
		for(String t:token)
		{
			if(!stopWords.contains(t))
			{
				ProcTokens.add(t);
			}
			
		}

		String[] newArr= new String[ProcTokens.size()];
		newArr=ProcTokens.toArray(newArr);
		return newArr;
	}
	
	private void incScorePos(String t1,String t2)
	{
		
		Payload t1P = wordIndex.get(t1);
		Payload t2P = wordIndex.get(t2);
		
		if(t1P==null||t2P==null)return;
		
		ArrayList<DocInfo> t1L = t1P.getDocList();
		ArrayList<DocInfo> t2L = t2P.getDocList();
		
		int p1=0,p2=0,docID1,docID2;
		
		DocIntersectionPos temp= new DocIntersectionPos();
		for(;p1<t1L.size() && p2 < t2L.size();)
		{
			docID1=t1L.get(p1).getDocId();
			docID2=t2L.get(p2).getDocId();
			
			if(docID1<docID2)p1++;
			else if(docID2<docID1)p2++;
			else
			{
				temp.docId=docID1;
				temp.posWord1=t1L.get(p1).getPos();
				temp.posWord2=t2L.get(p2).getPos();
				if(temp.checkIncreaseScore())
					temp.incScore(this.docScoreMap);
				
				p1++;p2++;
		
			}
		}
	}
		
	private void rankDocs(String input)
	{
		String query = input.toLowerCase();
		
		String[] tokens = query.split("[^a-z0-9]+");
		
		tokens=removeStopWords(tokens);
		
		/*calculate score for each document containing the token */
		for(String token : tokens)
		{
			calculateDocScores(token);
		}
		
		for(int i=0;i<tokens.length;i++)
		{
			for(int j=i+1;j<tokens.length;j++)
			{
				incScorePos(tokens[i],tokens[j]);
			}
			
		}
		
	}
	
	private void calculateDocScores(String token)
	{
		Payload tokenPayload = wordIndex.get(token);
		
		if(tokenPayload!=null)
		{
			List<DocInfo> docList = tokenPayload.getDocList();
	
			float idf = (float)tokenPayload.getIDF();
			//drop document if idf score is very low... <1 ==> the word occurs atleast once  in almost every 2.5 docs ..
			if(idf<1)
				return;
		
			for(int i = 0; i < docList.size(); i++)
			{
				DocInfo doc = docList.get(i);
				int docId = doc.getDocId();
				
				float score = 0;
	
				if(docScoreMap.containsKey(docId))
				{
					score = docScoreMap.get(docId);
					score += idf*doc.getFreq();
	
					docScoreMap.put(docId, score);
				}
				else
				{
					score = idf*doc.getFreq();
					docScoreMap.put(docId, score);
				}
			}
			
			List<Integer> titleList = tokenPayload.getTitleList();
			int docId;
			idf = (float)tokenPayload.getTitleIDF();
			//drop document if idf score is very low... <1 ==> the word occurs atleast once  in almost every 2.5 docs ..
			if(idf<1)
				return;
		
			for(int i = 0; i < titleList.size(); i++)
			{
				docId = titleList.get(i);
				
				float score = 0;
	
				if(docScoreMap.containsKey(docId))
				{
					score = docScoreMap.get(docId);
					score *= idf*(1+(10/urlIndex.get(docId).getTitle().length()));
					score *= (1+(10/urlIndex.get(docId).getUrl().length()));
					docScoreMap.put(docId, score);
				}
				//no need for else part , if no doc is tere it means none of the words are there, but the title is there==>dubious link
			}
			
			
		}
		
	}
	
	private DocResult[] getResults()
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
		DocResult[] dResults = new DocResult[topDocs.size()];
		
		for(int i = topDocs.size() - 1; !topDocs.isEmpty(); i--)
		{
			DocScore dscore = topDocs.poll();
			
			dResults[i] = new DocResult(urlIndex.get(dscore.docId).getTitle(),
					urlIndex.get(dscore.docId).getUrl());
		}
		
		return dResults;
	}
	
	private void printResults(DocResult[] dResults)
	{
		for(int i = 0; i < dResults.length; i++)
		{	
			System.out.println(dResults[i].getTitle());
			System.out.println(dResults[i].getUrl());
		}
	}
	
	private void clearScores()
	{
		docScoreMap.clear();
		//docScoreMap = new HashMap<Integer, Float>();
	}
}
