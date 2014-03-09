package index;

import java.io.Serializable;
import java.util.ArrayList;

public class Payload implements Serializable{
	
	ArrayList<DocInfo> docList;
	ArrayList<Integer> titleList;
	int currId;
	int docPos;
	static final long serialVersionUID=1;
	long totalFreq;
	
	double idf;
	float idfTitle;
	
	Payload()
	{
		//docList = new ArrayList<DocInfo>();
		docList= new ArrayList<DocInfo>();
		titleList = new ArrayList<Integer>();
		totalFreq = 0;
		currId=-1;
		docPos=-1;
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
		DocInfo	doc=null;
		if(docId==currId)
		{
			doc=docList.get(docPos);
			doc.incrementFreq();
			doc.addPos(pos);
		//	docList.set(docPos,doc);
			return;
		}
			
		doc = new DocInfo(docId);
		doc.incrementFreq();
		doc.addPos(pos);
		docList.add(doc);
		currId=docId;
		docPos=docList.size()-1;
		
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
	
	public ArrayList<DocInfo> getDocList()
	{
		return docList;
	}
	public ArrayList<Integer> getTitleList()
	{
		return titleList;
	}
	public double getIDF()
	{
		return idf;
	}
	public double getTitleIDF()
	{
		return idfTitle;
	}
}
