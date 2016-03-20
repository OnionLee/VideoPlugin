using Newtonsoft.Json;

public class VideoInfoDto
{
	[JsonProperty("id")]
	public long Id { get; private set; }

	[JsonProperty("title")]
	public string Title { get; private set; }

	[JsonProperty("path")]
	public string Path { get; private set; }

	//밀리세컨드 단위입니다.
	[JsonProperty("duration")]
	public int Duration { get; private set; }

	[JsonProperty("thumbPath")]
	public string ThumbPath { get; private set; }
}
