package index;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.lang.Math;
public class Indexer {
	
	public static HashMap<String, Payload> wordIndex = new HashMap<String, Payload>();
	public static HashMap<Integer, UrlInfo> urlIndex = new HashMap<Integer, UrlInfo>();
	
	public static final String delimiter = "!@#$%^&*()_+";
	public static HashSet<String> stopWords=new HashSet<String>();
	public static void main(String args[]) throws IOException
	{
		readFromFile();
		
		String[] words={"eppstein","computer","words","retrieval","fall","xianghua", "information", "software", "engineering", "machine", "learning"};
		
		
		for(String w:words)
		{
			System.out.println("=====================");
			System.out.println(" looking up word \" "+w+"\"");
			
			Payload p=wordIndex.get(w);
			if(p!=null)
			{
				System.out.println("idf="+p.idf);

				System.out.println("frequency of word="+p.totalFreq);
				System.out.println("some of the urls where word occurs....");
				int count=4;
				for(DocInfo temp:p.docList)
				{
					if(count--==0)break;
					
					System.out.println(" =========== url +++ \""+(urlIndex.get(temp.docId).getUrl())+"\"");
					System.out.println("================ freq of word within url + "+temp.freq);
				}
				
				
				System.out.println("*********************");
			}
			else
				System.out.println("nothin.....");
		}
		
		System.out.println("Size of word index="+wordIndex.size());
		System.out.println("Size of URL index "+urlIndex.size());
		
		serializeToOutput();
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
	
	public static void readFromFile() throws IOException
	{
//		String file="C:\\Users\\SAISUNDAR\\Google Drive\\UCI related folders\\IR CS221_\\new run multithreaded\\IRdata.txt";
//		String file1="C:\\Users\\SAISUNDAR\\Google Drive\\UCI related folders\\IR CS221_\\new run multithreaded\\stopwords.txt";
//		String file2="C:\\Users\\SAISUNDAR\\Google Drive\\UCI related folders\\IR CS221_\\Project 3\\Title.txt";
		String file = "E:\\books\\UCI\\Information Retrieval\\Projects\\project3\\IRdata.txt";
		String file1 = "E:\\books\\UCI\\Information Retrieval\\Projects\\project3\\stopwords.txt";
		String file2 = "E:\\books\\UCI\\Information Retrieval\\Projects\\project3\\Title.txt";
		BufferedReader in = new BufferedReader(new FileReader(file));
		BufferedReader in1 = new BufferedReader(new FileReader(file1));
		String line;
		String[] tokens;
		String url = "";
		String docIdStr = null;
		int docLength = 0;
		int docId = 0;
		int position = 0;
		int totalNoDoc = 0;
		boolean firstExec = true;
		
		while((line = in1.readLine()) != null)
			stopWords.add(line);
			
		while((line = in.readLine()) != null)
		{
			
			if(line.equals(delimiter))
			{
				//System.out.println(line);
				if(!firstExec)
					addToURLIndex(docId, new UrlInfo(url, docLength)); // add doc of prev iteration
				else
					firstExec = false;
				docIdStr = in.readLine();
				docIdStr = docIdStr.substring(6);	// 6 - position to extract docid
				docId = Integer.parseInt(docIdStr);
				url = in.readLine();
				in.readLine();	// unwanted info
				in.readLine();	// unwanted info
				position = 0;
				docLength = 0;
				
				totalNoDoc++;
			}
			else 
			{
				//System.out.println(line);
				
				line=line.toLowerCase();
				line=line.trim();
				tokens=line.split("[^a-z0-9]+");
				docLength += tokens.length;
				
				for(String token : tokens)
				{
					if(!stopWords.contains(token) && token.length()>2)
					{
						addToWordIndex(token, docId, position);
						position++;
					}
				}
			}
		}
		
		addToURLIndex(Integer.parseInt(docIdStr), new UrlInfo(url, docLength)); //add the final read url to index
		
		in.close();
		in = new BufferedReader(new FileReader(file2));
		
		int currDocID=0;
		UrlInfo info;
		// for titles...
		while((line = in.readLine()) != null)
		{
			
			if(line.contains("DOCID"))
			{
				tokens=line.split("[^a-z0-9]+");
				currDocID=Integer.parseInt(tokens[1]);
				
				
			}
			else 
			{
				//System.out.println(line);
				if(line.length()<2)continue;
				info=urlIndex.get(currDocID);
				if(info.getTitle()==null)info.setTitle(line);
				line=line.toLowerCase();
				line=line.trim();
				tokens=line.split("[^a-z0-9]+");
				docLength += tokens.length;
				
				for(String token : tokens)
				{
					if(!stopWords.contains(token) && token.length()>2)
					{
						addToWordIndexTitle(token, currDocID);
					}
				}
			}
		}
			
		/* Calculate idf for all words */
		Set<String> wordSet = wordIndex.keySet();
		
		for(String word : wordSet)
		{
			Payload payload = wordIndex.get(word);
			long noOfDoc = payload.getNumberofDoc();
			
			payload.setIDF((float)Math.log((double)totalNoDoc/noOfDoc));
			payload.idfTitle=(float)Math.log((double)totalNoDoc/payload.titleList.size()-1);
			
			for(int i = 0; i < payload.docList.size(); i++)
			{
				float fr=payload.docList.get(i).freq;
				payload.docList.get(i).freq=(float)(Math.log(1+ fr));
			}
		}
		
		stopWords.clear();
		in.close();
		in1.close();
	}
	private static void addToWordIndexTitle(String token, int docId)
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
		int len=payload.titleList.size()-1;
		if(len>0 && payload.titleList.get(len)==docId)
			return;
		payload.titleList.add(docId);
		wordIndex.put(token, payload);
				
	}
	private static void addToURLIndex(int docId, UrlInfo urlInfo)
	{
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
