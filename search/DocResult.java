package search;


public class DocResult {
	
	String title;
	String url;
	
	DocResult(String title, String url)
	{
		this.title = title;
		this.url = url;
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
