package index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Indexer {
	
	static HashMap<String, Payload> wordIndex = new HashMap<String, Payload>();
	static HashMap<String, UrlInfo> urlIndex = new HashMap<String, UrlInfo>();
	
	public static final String delimiter = "!@#$%^&*()_+";
	
	public static void main(String args[]) throws IOException
	{
		ReadFromFile();
		
		
	}
	
	public static void ReadFromFile() throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader("IRdata.txt"));
		String line;
		String[] tokens;
		String url = "";
		String docIdStr = null;
		long docLength = 0;
		int docId = 0;
		
		while((line = in.readLine()) != null)
		{
			if(line.equals(delimiter))
			{
				addToURLIndex(docIdStr, new UrlInfo(url, docLength));
				docLength = 0;
				docIdStr = in.readLine();
				docIdStr = docIdStr.substring(6);	// 6 - position to extract docid
				docId = Integer.parseInt(docIdStr);
				url = in.readLine();
				in.readLine();	// unwanted info
				in.readLine();	// unwanted info
			}
			else 
			{
				tokens = line.split("[ ]+");
				docLength += tokens.length;
				
				int position = 0;
				
				for(String token : tokens)
				{
					addToWordIndex(token, docId, position);
					
					position++;
				}
			}
			
		}
		
		addToURLIndex(docIdStr, new UrlInfo(url, docLength)); //add the final read url to index
		
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
	}
}
