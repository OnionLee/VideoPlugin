using System;
using UnityEngine;
using UnityEngine.UI;

public class ListItem : MonoBehaviour
{
	[SerializeField]
	private RawImage icon;

	[SerializeField]
	private Text title;

	[SerializeField]
	private Text duration;

	[SerializeField]
	private Text addedTime;

	[SerializeField]
	private Text modifiedTime;

	public void InitItem(VideoInfoDto info)
	{
		//IOS에서는 이 코드를 쓸 수 없습니다. (샘플 프로그램)
		Texture2D texture = null;
		if (System.IO.File.Exists(info.ThumbPath))
		{
			byte[] byteTexture = System.IO.File.ReadAllBytes(info.ThumbPath);
			if (byteTexture.Length > 0)
			{
				texture = new Texture2D(0, 0);
				texture.LoadImage(byteTexture);
			}
		}

		UpdateData(texture, info.Title, info.Duration, info.AddedDate, info.ModifiedDate);
	}

	private void UpdateData(Texture2D texture, string title, int duration, long addedDate, long modifiedDate)
	{
		var addedDateTime = TimeUtility.ToDateTime(addedDate);
		var modifiedDateTime = TimeUtility.ToDateTime(modifiedDate);

		this.icon.texture = texture;
		this.title.text = title;
		this.addedTime.text = addedDateTime.ToString("MM'/'dd'/'yyyy HH':'mm':'ss");
		this.modifiedTime.text = modifiedDateTime.ToString("MM'/'dd'/'yyyy HH':'mm':'ss");

		TimeSpan durationTime = new TimeSpan(0, 0, duration / 1000);
		this.duration.text = string.Format("{0:HH:mm:ss}", durationTime);
	}
}

