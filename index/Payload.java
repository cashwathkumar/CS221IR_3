package index;

import java.util.ArrayList;

public class Payload {
	
	ArrayList<DocInfo> docList;
	
	long totalFreq;
	
	double idf;
	
	private class DocInfo
	{
		int docId;
		long freq;
		
		ArrayList<Integer> pos;
		long docLength;
		
		DocInfo(int docId, long docLength)
		{
			this.docId = docId;
			this.docLength = docLength;
			pos = new ArrayList<Integer>();
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
