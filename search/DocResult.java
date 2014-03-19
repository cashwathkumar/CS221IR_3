package search;

import java.io.BufferedReader;
import java.util.Scanner;
import java.io.FileReader;


public class DocResult {
	
	String title;
	String url;
	String snippet;
	
	DocResult(String title, String url,int offset,int docid)
	{
		this.title = title;
		this.url = url;
		
		try
		{
		String in=Integer.toString(docid);
		Scanner inp=new Scanner(new FileReader(in));
		int count=0;
		StringBuilder temp=new StringBuilder();
		while(inp.hasNext() && count< offset-5)
		{inp.next();count++;
		}
			
		while(inp.hasNext() && count<offset+10)
		{
			temp.append(inp.next());
			temp.append(" ");
			count++;
		}
		
		snippet=temp.toString();
		inp.close();
		}
		catch(Exception e)
		{
			
		}
		
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getUrl()
	{
		return url;
	}
}
