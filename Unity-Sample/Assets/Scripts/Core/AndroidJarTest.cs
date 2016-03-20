using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class AndroidJarTest : MonoBehaviour
{
	[SerializeField]
	private Text logText;

	private AndroidJavaClass firstPluginJc;

	void Awake()
	{
		firstPluginJc = new AndroidJavaClass("com.onionlee.videoplugin.Test");
	}

	public void CallAndroid(string strMsg)
	{
		firstPluginJc.CallStatic("CallAndroid_U", strMsg);
	}

	public void SetLog(string logMsg)
	{
		logText.text = logMsg;
	}
}
