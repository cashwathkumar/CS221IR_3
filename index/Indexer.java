package index;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Set;

public class Indexer {
	
	static HashMap<String, Payload> wordIndex = new HashMap<String, Payload>();
	static HashMap<String, UrlInfo> urlIndex = new HashMap<String, UrlInfo>();
	
	public static final String delimiter = "!@#$%^&*()_+";
	
	public static void main(String args[]) throws IOException
	{
		readFromFile();
		
		String[] words={"Eppstein","Computer","words","retrieval","Fall"};
		
		
		for(String w:words)
		{
			System.out.println("=====================");
			System.out.println(" looking up word"+w);
			
			Payload p=wordIndex.get(w);
			if(p!=null)
			{
				System.out.println("idf="+p.idf);

				System.out.println("frequency of word="+p.totalFreq);
				System.out.println("*********************");
			}
			else
				System.out.println("nothin.....");
		}
		
		
		//serializeToOutput();
	}
	
	private static void serializeToOutput() throws IOException
	{
		FileOutputStream wOut = new FileOutputStream("Windex");
		FileOutputStream uOut = new FileOutputStream("Uindex");
		
		ObjectOutputStream oOut = new ObjectOutputStream(wOut);
		
		oOut.writeObject(wordIndex);
		
		oOut = new ObjectOutputStream(uOut);
		
		oOut.writeObject(urlIndex);
		
		oOut.close();
		
		wOut.close();
		uOut.close();	
	}
	
	private static void readFromFile() throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\SAISUNDAR\\Google Drive\\UCI related folders\\IR CS221_\\new run multithreaded\\IRdata.txt"));
		String line;
		String[] tokens;
		String url = "";
		String docIdStr = null;
		long docLength = 0;
		int docId = 0;
		int position = 0;
		int totalNoDoc = 0;
		
		while((line = in.readLine()) != null)
		{
			
			if(line.equals(delimiter))
			{
				//System.out.println(line);
				addToURLIndex(docIdStr, new UrlInfo(url, docLength));
				docLength = 0;
				docIdStr = in.readLine();
				docIdStr = docIdStr.substring(6);	// 6 - position to extract docid
				docId = Integer.parseInt(docIdStr);
				url = in.readLine();
				in.readLine();	// unwanted info
				in.readLine();	// unwanted info
				position = 0;
				
				totalNoDoc++;
			}
			else 
			{
				//System.out.println(line);
				tokens = line.split("[ ]+");
				docLength += tokens.length;
				
				for(String token : tokens)
				{
					addToWordIndex(token, docId, position);
					
					position++;
				}
			}
			
		}
		
		addToURLIndex(docIdStr, new UrlInfo(url, docLength)); //add the final read url to index
		
		/* Calculate idf for all words */
		Set<String> wordSet = wordIndex.keySet();
		
		for(String word : wordSet)
		{
			Payload payload = wordIndex.get(word);
			long noOfDoc = payload.getNumberofDoc();
			
			payload.setIDF((double)noOfDoc/totalNoDoc);
		}
		
		in.close();
	}
	
	private static void addToURLIndex(String docId, UrlInfo urlInfo)
	{
		if(docId != null)
			urlIndex.put(docId, urlInfo);
	}
	
	private static void addToWordIndex(String token, int docId, int pos)
	{
		Payload payload;
		
		if(wordIndex.containsKey(token))
		{
			payload = wordIndex.get(token);
		}
		else
		{
			payload = new Payload();
		}
		
		payload.incrementTFreq();
		
		payload.updateDoc(docId, pos);
		
		wordIndex.put(token, payload);
	}
}
