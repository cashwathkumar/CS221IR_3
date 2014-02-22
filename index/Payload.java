package index;

import java.util.ArrayList;

public class Payload {
	
	ArrayList<DocInfo> docList;
	
	long totalFreq;
	
	double idf;
	
	Payload()
	{
		docList = new ArrayList<DocInfo>();
		
		totalFreq = 0;
		
		idf = 0.0;
	}
	
	public void incrementTFreq()
	{
		totalFreq++;
	}
	
	public void setIDF(double idf)
	{
		this.idf = idf;
	}
	
	public void updateDoc(int docId, int pos)
	{
		
		for(DocInfo doc : docList)
		{
			if(doc.docId == docId)
			{
				doc.incrementFreq();
				
				doc.addPos(pos);
				
				return;
			}
		}
		
		DocInfo newDoc = new DocInfo(docId);
		
		newDoc.incrementFreq();
		newDoc.addPos(pos);
		
		docList.add(newDoc);
	}
	
	public int getDocPos(int docId)
	{
		int pos;
		
		for(pos = 0; pos < docList.size(); pos++)
		{
			DocInfo doc = docList.get(pos);
			if(doc.docId == docId)
				break;
		}
		
		return pos;
	}
	
	public long getNumberofDoc()
	{
		return docList.size();
	}
	
	private class DocInfo
	{
		int docId;
		long freq;
		
		ArrayList<Integer> pos;
		
		DocInfo(int docId)
		{
			this.docId = docId;
			pos = new ArrayList<Integer>();
			freq = 0;
		}
		
		public void incrementFreq()
		{
			freq++;
		}
		
		public void addPos(int pos)
		{
			this.pos.add(pos);
		}
	}

}
