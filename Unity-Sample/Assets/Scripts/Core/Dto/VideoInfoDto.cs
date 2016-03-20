using Newtonsoft.Json;

public class VideoInfoDto
{
	[JsonProperty("id")]
	public long Id { get; private set; }

	[JsonProperty("title")]
	public string Title { get; private set; }

	[JsonProperty("path")]
	public string Path { get; private set; }

	[JsonProperty("duration")]
	public int Duration { get; private set; }

	[JsonProperty("thumbPath")]
	public string ThumbPath { get; private set; }
}
