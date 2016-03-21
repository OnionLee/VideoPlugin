using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;

public class SampleUI : MonoBehaviour {

	[SerializeField]
	private RectTransform parent;

	[SerializeField]
	private ListItem listItemPrefab;

	private List<ListItem> listItems;

	[SerializeField]
	private Button loadButton;

	[SerializeField]
	private Slider volumeBar;

	[SerializeField]
	private Slider brightBar;

	private void Awake()
	{
		listItems = new List<ListItem>();

		VideoPluginManager.Instance.LoadVideoInfosSucceed += OnLoadVideoInfosSucceed;
		VideoPluginManager.Instance.GetVolumeSucceed += OnGetVolumeSucceed;
		VideoPluginManager.Instance.GetBrightSucceed += OnGetBrightSucceed;

		loadButton.onClick.AddListener(OnLoadButtonClicked);
		volumeBar.onValueChanged.AddListener(OnVolumeBarValueChanged);
		brightBar.onValueChanged.AddListener(OnBrightBarValueChanged);
	}

	private void Start()
	{
		VideoPluginManager.Instance.GetVolume();
		VideoPluginManager.Instance.GetBright();
	}

	private void OnDestroy()
	{
		VideoPluginManager.Instance.LoadVideoInfosSucceed += OnLoadVideoInfosSucceed;
		VideoPluginManager.Instance.GetVolumeSucceed += OnGetVolumeSucceed;
		VideoPluginManager.Instance.GetBrightSucceed += OnGetBrightSucceed;

		loadButton.onClick.RemoveListener(OnLoadButtonClicked);
		volumeBar.onValueChanged.RemoveListener(OnVolumeBarValueChanged);
		brightBar.onValueChanged.RemoveListener(OnBrightBarValueChanged);
	}

	public void OnLoadButtonClicked()
	{
		VideoPluginManager.Instance.LoadVideoFileInfos();
	}

	private void OnLoadVideoInfosSucceed(System.Collections.Generic.List<VideoInfoDto> infos)
	{
		ClearItem();

		foreach (var info in infos)
		{
			CreateItem(info);
		}
	}

	private void CreateItem(VideoInfoDto info)
	{
		var listItem = Instantiate<ListItem>(listItemPrefab);
		listItem.transform.SetParent(parent, false);
		listItem.InitItem(info);
		listItems.Add(listItem);
	}

	private void ClearItem()
	{
		foreach (var item in listItems)
		{
			Destroy(item.gameObject);
		}

		listItems.Clear();
	}

	private void OnGetVolumeSucceed(int value)
	{
		volumeBar.value = value;
		volumeBar.maxValue = 15;
	}

	private void OnGetBrightSucceed(int value)
	{
		volumeBar.value = value;
		volumeBar.maxValue = 255;
	}

	public void OnVolumeBarValueChanged(float value)
	{
		VideoPluginManager.Instance.SetVolume((int)value);
		VideoPluginManager.Instance.GetVolume();
	}

	public void OnBrightBarValueChanged(float value)
	{
		VideoPluginManager.Instance.SetBright((int)value);
		VideoPluginManager.Instance.GetBright();
	}
}
