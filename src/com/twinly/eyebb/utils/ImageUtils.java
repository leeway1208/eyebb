package com.twinly.eyebb.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.twinly.eyebb.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {
	public static DisplayImageOptions avatarOpitons = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_avatar_dark)
			.showImageForEmptyUri(R.drawable.icon_avatar_dark)
			.showImageOnFail(R.drawable.icon_avatar_dark).cacheInMemory(true)
			.cacheOnDisk(true).considerExifParams(true).build();

	public static DisplayImageOptions locationIconOpitons = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_location_default)
			.showImageForEmptyUri(R.drawable.ic_location_default)
			.showImageOnFail(R.drawable.ic_location_default)
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
			.build();

	public static boolean saveBitmap(Bitmap bitmap, String path) {
		File file = new File(path);
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				System.out.println("saveBitmap ==>>" + e.getMessage());
				return false;
			}
		} catch (FileNotFoundException e) {
			System.out.println("saveBitmap ==>>" + e.getMessage());
			return false;
		}
		return true;
	}

	public static Bitmap getBitmapFromLocal(String path) {
		return BitmapFactory.decodeFile(path);
	}

	public static boolean isLocalImage(String path) {
		if (path.contains("http")) {
			return false;
		} else {
			return true;
		}
	}
}
