package com.onionlee.videoplugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.onionlee.videoplugin.dto.VideoInfoDto;
import com.unity3d.player.UnityPlayer;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Window;

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

	public static void LoadFromMedia() {
		ArrayList<VideoInfoDto> videoInfoDtos = new ArrayList<VideoInfoDto>();
		ContentResolver cr = UnityPlayer.currentActivity.getContentResolver();
		Cursor videoCursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);

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
				dto.thumbPath = GetThumbnailPathForLocalFile(dto.id);
				dto.addedDate = videoCursor.getLong(videoAddedDateCol);
				dto.modifiedDate = videoCursor.getLong(videoModifiedDateCol);
				
				videoInfoDtos.add(dto);
			} while (videoCursor.moveToNext());

			videoCursor.close();
		}

		Gson gson = new Gson();
		String json = gson.toJson(videoInfoDtos);
		UnityPlayer.UnitySendMessage(objectName, "OnLoadFromMediaSucceed", json);
	}
	
	public static void LoadFromDirectory(String rootPath)
	{
		ArrayList<VideoInfoDto> videoInfoDtos = new ArrayList<VideoInfoDto>();
		MediaMetadataRetriever rv = new MediaMetadataRetriever();
		File rootFile = new File(rootPath);
		
		AddVideoInfo(rootFile, videoInfoDtos, rv, 0);
		
		Gson gson = new Gson();
		String json = gson.toJson(videoInfoDtos);
		UnityPlayer.UnitySendMessage(objectName, "OnLoadFromDirectorySucceed", json);
	}
	
	@SuppressLint("SimpleDateFormat")
	private static void AddVideoInfo(File root, ArrayList<VideoInfoDto> dtos, MediaMetadataRetriever rv, int index)
	{
		File[] list = root.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				return filename.endsWith(".mp4"); 
			}
		});
		
		for(File f : list)
		{
			if(f.isDirectory())
			{
				AddVideoInfo(f, dtos, rv, ++index);
			}
			else
			{
				try
				{
					rv.setDataSource(f.getPath());
					String duration = rv.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
					String date = rv.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
					Bitmap bitmap = rv.getFrameAtTime();
					String thumbPath = f.getParent() + "/" + f.getName() + ".png";
					boolean savedBitmap = SaveBitmapFile(thumbPath, bitmap);
					
					VideoInfoDto dto = new VideoInfoDto();
					dto.id = index;
					dto.title = f.getName();
					dto.path = f.getPath();
					if(duration != null)
						dto.duration = Integer.parseInt(duration);
					if(date != null)
					{
						SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS'Z'");
						format.setTimeZone(TimeZone.getTimeZone("UTC"));
						Date dateTime = format.parse(date);
						dto.addedDate = dateTime.getTime() / 1000;
						dto.modifiedDate = dateTime.getTime() / 1000;
					}
					
					if(savedBitmap)
						dto.thumbPath = thumbPath;
					
					dtos.add(dto);
				}
				catch(Exception e)
				{
					Log(e.getMessage());
				}
			}
		}
	}
	
	private static boolean SaveBitmapFile(String path, Bitmap bmp)
	{
		FileOutputStream out = null;
		try {
		    out = new FileOutputStream(path);
		    bmp.compress(Bitmap.CompressFormat.PNG, 100, out); 
		    return true;// bmp is your Bitmap instance
		    // PNG is a lossless format, the compression factor (100) is ignored
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (out != null) {
		            out.close();
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
		 return false;
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
