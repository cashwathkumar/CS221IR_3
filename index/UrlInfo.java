package index;

import java.io.Serializable;

public class UrlInfo implements Serializable
{
	private String url;
	private int length;
	private String title;
	static final long serialVersionUID=2;
	
	UrlInfo(String url, int length)
	{
		this.url = url;
		this.length = length;
		title=null;
	}
	
	public void setTitle(String tit)
	{
		
		title=tit;
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
}