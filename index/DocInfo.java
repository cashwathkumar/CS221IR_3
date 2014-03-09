package index;

import java.io.Serializable;
import java.util.ArrayList;

public class DocInfo implements Serializable
{
	int docId;
	float freq;
	static final long serialVersionUID=3;
	ArrayList<Integer> pos;
	
	DocInfo(int docId)
	{
		this.docId = docId;
		pos = new ArrayList<Integer>();
		freq = 0;
	}
	
	public ArrayList<Integer> getPos()
	{
		return pos;
		
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
	
	public float getFreq()
	{
		return freq;
	}
}
