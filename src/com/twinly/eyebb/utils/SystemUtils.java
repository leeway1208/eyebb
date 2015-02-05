package com.twinly.eyebb.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.database.DBActivityInfo;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.database.DBNotifications;
import com.twinly.eyebb.database.DBPerformance;

public class SystemUtils {

	public static int getLocale(Context context) {
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();

		System.out.println("--->>" + config.locale);
		if (config.locale.toString().equals("zh_TW")
				|| config.locale.toString().equals("zh")) {
			return Constants.LOCALE_TW;
		} else if (config.locale.toString().equals("zh_HK")
				|| config.locale.toString().equals("zh")) {
			return Constants.LOCALE_HK;
		} else if (config.locale.toString().equals("zh_CN")
				|| config.locale.toString().equals("zh")) {
			return Constants.LOCALE_CN;
		} else {
			return Constants.LOCALE_EN;
		}
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public static void clearData(Context context) {
		DBActivityInfo.clear(context);
		DBChildren.clear(context);
		DBPerformance.clear(context);
		DBNotifications.clear(context);

		SharePrefsUtils.clear(context);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static boolean isRunning(Context ctx) {
		ActivityManager activityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager
				.getRunningTasks(Integer.MAX_VALUE);

		for (RunningTaskInfo task : tasks) {
			if (ctx.getPackageName().equalsIgnoreCase(
					task.baseActivity.getPackageName()))
				return true;
		}
		return false;
	}
}
