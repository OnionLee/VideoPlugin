using UnityEngine;
using System.Collections;
using System;
using System.Collections.Generic;
using Newtonsoft.Json;

public class VideoPluginManager : MonoBehaviour
{
	public event Action<List<VideoInfoDto>> VideoInfosLoaded;

	public const string objectName = "__VIDEO_PLUGIN_MANAGER";
	public const string classPath = "com.onionlee.videoplugin.VideoPluginManager";

	private static VideoPluginManager instance;
	public static VideoPluginManager Instance
	{
		get
		{
			var instance = FindObjectOfType<VideoPluginManager>();
			if (instance != null)
			{
				if(instance.name != objectName)
					instance.name = objectName;
			}
			else
			{
				var go = new GameObject();
				go.name = objectName;
				DontDestroyOnLoad(go);

				instance = go.AddComponent<VideoPluginManager>();
			}

			return instance;
		}
	}

	private AndroidJavaClass androidManagerClass;

	private void Awake()
	{
		androidManagerClass = new AndroidJavaClass(classPath);
	}

	//ToAndroid
	public void LoadVideoFileInfos()
	{
		androidManagerClass.CallStatic("LoadVideoFileInfos");
	}

	public void OnVideoFileInfosLoaded(string json)
	{
		var infoList = JsonConvert.DeserializeObject<List<VideoInfoDto>>(json);
		if (VideoInfosLoaded != null)
		{
			VideoInfosLoaded(infoList);
		}
	}

	public void Log(string msg)
	{
		Debug.Log(msg);
	}

	public void LogError(string msg)
	{
		Debug.LogError(msg);
	}
}
