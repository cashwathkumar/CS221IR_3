package index;

import java.io.Serializable;

public class UrlInfo implements Serializable
{
	private String url;
	private int length;
	static final long serialVersionUID=2;
	
	UrlInfo(String url, int length)
	{
		this.url = url;
		this.length = length;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public int getLength()
	{
		
		return length;
	}
}