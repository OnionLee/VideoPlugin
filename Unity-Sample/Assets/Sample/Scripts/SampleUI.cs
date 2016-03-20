using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class SampleUI : MonoBehaviour {

	[SerializeField]
	private RectTransform parent;

	[SerializeField]
	private ListItem listItemPrefab;

	private List<ListItem> listItems;

	private void Awake()
	{
		listItems = new List<ListItem>();

		VideoPluginManager.Instance.VideoInfosLoaded += OnVideoInfosLoaded;
	}

	private void OnDestroy()
	{
		VideoPluginManager.Instance.VideoInfosLoaded -= OnVideoInfosLoaded;
	}

	private void OnVideoInfosLoaded(System.Collections.Generic.List<VideoInfoDto> infos)
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
		foreach(var item in listItems)
		{
			Destroy(item.gameObject);
		}

		listItems.Clear();
	}

	public void OnLoadButtonClicked()
	{
		VideoPluginManager.Instance.LoadVideoFileInfos();
	}
}
