package index;

import java.io.Serializable;

class UrlInfo implements Serializable
{
	private String url;
	private long length;
	static final long serialVersionUID=2;
	
	UrlInfo(String url, long length)
	{
		this.url = url;
		this.length = length;
	}
	
	String getUrl()
	{
		return url;
	}
	
	long getLength()
	{
		
		return length;
	}
}