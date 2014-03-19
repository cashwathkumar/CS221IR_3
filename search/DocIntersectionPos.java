package search;
import search.SearchEngine;
import index.UrlInfo;
import java.util.ArrayList;
import java.util.HashMap;


public class DocIntersectionPos {
	
	int docId;
	ArrayList<Integer> posWord1;
	ArrayList<Integer> posWord2;
	
	static int MAX_DISTANCE=10; // maximu distance between two words , if greater than 20 is being ignored
	static float SCORE_INC_FACTOR=(float)(0.5);
	
	int checkIncreaseScore()
	{
		int pos1=0; int pos2=0,position1,position2,diff;
		
		int  len1=posWord1.size();
		int  len2=posWord2.size();
		
		while(pos1<len1 && pos2 <len2)
		{
			position1=posWord1.get(pos1);
			position2=posWord2.get(pos2);
			diff=position1-position2;
			
			if(diff>=0 && diff <MAX_DISTANCE||diff<=0 && diff >(-MAX_DISTANCE))
				{
				int offset=0;
				offset=(diff>0)?position1:position2;
				return offset;
				}
			else if(diff > 0)pos2++;
			else pos1++;
			
		}
	return -1;	
	}
	
	void incScore(HashMap<Integer, Float> docScoreInputMap)
	{
		HashMap<Integer, Float> docScoreMap= docScoreInputMap;
		if(docScoreMap.containsKey(docId))
		{
			
			float score = 0;
			score = docScoreMap.get(docId)*(1+SCORE_INC_FACTOR);
			docScoreMap.put(docId, score);
		}
	}

}
