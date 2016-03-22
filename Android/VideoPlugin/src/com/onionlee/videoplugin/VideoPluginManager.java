package com.onionlee.videoplugin;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.onionlee.videoplugin.dto.VideoInfoDto;
import com.unity3d.player.UnityPlayer;

import android.app.ActionBar.LayoutParams;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Window;
import android.view.WindowManager;

public class VideoPluginManager {
	final public static String objectName = "__VIDEO_PLUGIN_MANAGER";
	final public static String[] videoProjection = { 
			MediaStore.Video.Media._ID, 
			MediaStore.Video.Media.TITLE,
			MediaStore.Video.Media.DATA, 
			MediaStore.Video.Media.DURATION, 
			MediaStore.Video.Media.DATE_ADDED,
			MediaStore.Video.Media.DATE_MODIFIED };

	final public static String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA };

	public static void LoadVideoFileInfos() {
		ArrayList<VideoInfoDto> videoInfoDtos = new ArrayList<VideoInfoDto>();
		
		LoadVideoFileInfo(videoInfoDtos, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		LoadVideoFileInfo(videoInfoDtos, MediaStore.Video.Media.INTERNAL_CONTENT_URI);

		Gson gson = new Gson();
		String json = gson.toJson(videoInfoDtos);
		UnityPlayer.UnitySendMessage(objectName, "OnVideoFileInfosLoaded", json);
	}
	
	public static void LoadVideoFileInfo(ArrayList<VideoInfoDto> videoInfoDtos, Uri uri)
	{
		ContentResolver cr = UnityPlayer.currentActivity.getContentResolver();
		Cursor videoCursor = cr.query(uri, videoProjection, null, null, null);

		if (videoCursor != null && videoCursor.moveToFirst()) {
			int videoIDCol = videoCursor.getColumnIndex(MediaStore.Video.Media._ID);
			int videoTitleCol = videoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
			int videoPathCol = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
			int videoDurationCol = videoCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
			int videoAddedDateCol = videoCursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED);
			int videoModifiedDateCol = videoCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);
			
			do {
				VideoInfoDto dto = new VideoInfoDto();

				dto.id = videoCursor.getLong(videoIDCol);
				dto.title = videoCursor.getString(videoTitleCol);
				dto.path = videoCursor.getString(videoPathCol);
				dto.duration = videoCursor.getInt(videoDurationCol);
				dto.thumbPath = GetThumbnailPathForLocalFile(dto.id, uri);
				dto.addedDate = videoCursor.getLong(videoAddedDateCol);
				dto.modifiedDate = videoCursor.getLong(videoModifiedDateCol);
				
				videoInfoDtos.add(dto);
			} while (videoCursor.moveToNext());

			videoCursor.close();
		}
	}

	private static String GetThumbnailPathForLocalFile(long fileId, Uri uri) {
		ContentResolver cr = UnityPlayer.currentActivity.getContentResolver();
		MediaStore.Video.Thumbnails.getThumbnail(cr, fileId, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND, null);

		Cursor thumbCursor = null;
		try {

			thumbCursor = cr.query(uri, thumbColumns,
					MediaStore.Video.Thumbnails.VIDEO_ID + " = " + fileId, null, null);

			if (thumbCursor.moveToFirst()) {
				String thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));

				return thumbPath;
			}

		} finally {
		}

		return null;
	}

	public static void SetVolume(int value) {

		AudioManager am = (AudioManager) UnityPlayer.currentActivity.getSystemService(Context.AUDIO_SERVICE);
		int volume = Math.max(Math.min(value, 15), 0);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
	}

	public static void GetVolume() {
		AudioManager am = (AudioManager) UnityPlayer.currentActivity.getSystemService(Context.AUDIO_SERVICE);
		int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		UnityPlayer.UnitySendMessage(objectName, "OnGetVolumeSucceed", String.valueOf(volume));
	}

	public static void SetBright(int value) {
		ContentResolver cr = UnityPlayer.currentActivity.getContentResolver();
		try {
			if (Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE) == 1) {
				Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
			}
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int brightness = Math.max(Math.min(value, 255), 0);
		Window window = UnityPlayer.currentActivity.getWindow();
		Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
		android.view.WindowManager.LayoutParams layoutpars = window.getAttributes();
		layoutpars.screenBrightness = brightness / (float) 255;
		window.setAttributes(layoutpars);
	}

	public static void GetBright() {
		ContentResolver cr = UnityPlayer.currentActivity.getContentResolver();
		try {
			int bright = Settings.System.getInt(cr, "screen_brightness");
			UnityPlayer.UnitySendMessage(objectName, "OnGetBrightSucceed", String.valueOf(bright));
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void Log(String msg) {
		UnityPlayer.UnitySendMessage(objectName, "Log", msg);
	}

	public static void LogError(String msg) {
		UnityPlayer.UnitySendMessage(objectName, "LogError", msg);
	}
}
