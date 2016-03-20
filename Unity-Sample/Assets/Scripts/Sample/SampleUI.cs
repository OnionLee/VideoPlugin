using UnityEngine;
using System.Collections;

public class SampleUI : MonoBehaviour {

	private void Awake()
	{
		VideoPluginManager.Instance.VideoInfosLoaded += OnVideoInfosLoaded;
	}

	private void OnVideoInfosLoaded(System.Collections.Generic.List<VideoInfoDto> infos)
	{
		foreach(var info in infos)
		{
			var format = "id : {0} \n title: {1} \n path : {2} \n duration : {3} \n thumbPath :{4}";
			Debug.LogFormat(format, info.Id, info.Title, info.Path, info.Duration, info.ThumbPath);
		}
	}

	public void OnLoadButtonClicked()
	{
		VideoPluginManager.Instance.LoadVideoFileInfos();
	}
}
