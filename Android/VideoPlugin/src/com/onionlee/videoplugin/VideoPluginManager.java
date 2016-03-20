package com.onionlee.videoplugin;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.onionlee.videoplugin.dto.VideoInfoDto;
import com.unity3d.player.UnityPlayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;

public class VideoPluginManager {
	final public static String objectName = "__VIDEO_PLUGIN_MANAGER";
	final public static String[] videoProjection = { MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE,
			MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION };
	final public static String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA };

	public static void LoadVideoFileInfos() {
		ArrayList<VideoInfoDto> videoInfoDtos = new ArrayList<VideoInfoDto>();
		ContentResolver cr = UnityPlayer.currentActivity.getContentResolver();
		Cursor videoCursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);

		if (videoCursor != null && videoCursor.moveToFirst()) {
			int videoIDCol = videoCursor.getColumnIndex(MediaStore.Video.Media._ID);
			int videoTitleCol = videoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
			int videoPathCol = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
			int videoDurationCol = videoCursor.getColumnIndex(MediaStore.Video.Media.DURATION);

			do {
				VideoInfoDto dto = new VideoInfoDto();

				dto.id = videoCursor.getLong(videoIDCol);
				dto.title = videoCursor.getString(videoTitleCol);
				dto.path = videoCursor.getString(videoPathCol);
				dto.duration = videoCursor.getInt(videoDurationCol);
				dto.thumbPath = GetThumbnailPathForLocalFile(dto.id);

				videoInfoDtos.add(dto);
			} while (videoCursor.moveToNext());

			videoCursor.close();
		}

		Gson gson = new Gson();
		String json = gson.toJson(videoInfoDtos);
		UnityPlayer.UnitySendMessage(objectName, "OnVideoFileInfosLoaded", json);
	}

	private static String GetThumbnailPathForLocalFile(long fileId) {
		ContentResolver cr = UnityPlayer.currentActivity.getContentResolver();
		MediaStore.Video.Thumbnails.getThumbnail(cr, fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

		Cursor thumbCursor = null;
		try {

			thumbCursor = cr.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns,
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

		Settings.System.putInt(cr, "screen_brightness", value);

		WindowManager.LayoutParams myLayoutParameter = UnityPlayer.currentActivity.getWindow().getAttributes();
		myLayoutParameter.screenBrightness = 1;
		UnityPlayer.currentActivity.getWindow().setAttributes(myLayoutParameter);
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
