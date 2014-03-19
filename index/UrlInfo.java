package index;

import java.io.Serializable;

public class UrlInfo implements Serializable
{
	private String url;
	private int length;
	private int titleLength;
	private String title;
	int offset;
	static final long serialVersionUID=2;
	
	UrlInfo(String url, int length)
	{
		this.url = url;
		this.length = length;
		title=null;
		offset=0;
	}
	
	public void setTitle(String tit)
	{
		
		title=tit;
		titleLength=title.length();
	}
	public String getUrl()
	{
		return url;
	}
	public String getTitle()
	{
		
		return title;
	}
	public int getLength()
	{
		
		return length;
	}
	public void setOffset(int off)
	{
		
		offset=off;
		System.out.println("offst set");
	}
	public int getOffSet()
	{
		
		return offset;
	}
	
}