package index;

import java.io.Serializable;
import java.util.ArrayList;

public class DocInfo implements Serializable
{
	int docId;
	int freq;
	static final long serialVersionUID=3;
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
	
	public int getDocId()
	{
		return docId;
	}
	
	public int getFreq()
	{
		return freq;
	}
}
