using UnityEngine;
using System.Collections;
using System;
using System.Collections.Generic;
using Newtonsoft.Json;

public class VideoPluginManager : MonoBehaviour
{
	public event Action<List<VideoInfoDto>> LoadVideoInfosSucceed;
	public event Action<int> GetVolumeSucceed;
	public event Action<int> GetBrightSucceed;

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

	#region VideoInfo
	public void LoadVideoFileInfos()
	{
		androidManagerClass.CallStatic("LoadVideoFileInfos");
	}

	public void OnVideoFileInfosLoaded(string json)
	{
		var infoList = JsonConvert.DeserializeObject<List<VideoInfoDto>>(json);
		if (LoadVideoInfosSucceed != null)
		{
			LoadVideoInfosSucceed(infoList);
		}
	}
	#endregion

	#region Volume

	public void SetVolume(int value)
	{
		androidManagerClass.CallStatic("SetVolume", value);
	}

	public void GetVolume()
	{
		androidManagerClass.CallStatic("GetVolume");
	}

	public void OnGetVolumeSucceed(string msg)
	{
		int value = int.Parse(msg);
		if (GetVolumeSucceed != null)
			GetVolumeSucceed(value);
	}

	#endregion

	#region Bright

	public void SetBright(int value)
	{
		androidManagerClass.CallStatic("SetBright", value);
	}

	public void GetBright()
	{
		androidManagerClass.CallStatic("GetBright");
	}

	public void OnGetBrightSucceed(string msg)
	{
		int value = int.Parse(msg);
		if (GetBrightSucceed != null)
			GetBrightSucceed(value);
	}

	#endregion

	public void Log(string msg)
	{
		Debug.Log(msg);
	}

	public void LogError(string msg)
	{
		Debug.LogError(msg);
	}
}
