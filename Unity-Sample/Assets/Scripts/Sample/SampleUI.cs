using UnityEngine;
using System.Collections;

public class SampleUI : MonoBehaviour {

	private void Awake()
	{
		VideoPluginManager.Instance.VideoInfosLoaded += OnVideoInfosLoaded;
	}

	private void OnVideoInfosLoaded(System.Collections.Generic.List<VideoInfoDto> infos)
	{
		Debug.Log("ok!!!");
	}

	public void OnLoadButtonClicked()
	{
		VideoPluginManager.Instance.LoadVideoFileInfos();
	}
}
