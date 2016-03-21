using Newtonsoft.Json;

public class VideoInfoDto
{
	[JsonProperty("id")]
	public long Id { get; private set; }

	[JsonProperty("title")]
	public string Title { get; private set; }

	[JsonProperty("path")]
	public string Path { get; private set; }

	//Millisecond
	[JsonProperty("duration")]
	public int Duration { get; private set; }

	[JsonProperty("thumbPath")]
	public string ThumbPath { get; private set; }

	//TimeStamp
	[JsonProperty("addedDate")]
	public long AddedDate { get; private set; }

	//TimeStamp
	[JsonProperty("modifiedDate")]
	public long ModifiedDate { get; private set; }
}
