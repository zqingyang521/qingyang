package com.example.qingyangdemo.base;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * 
 * @author 赵庆洋
 * 
 */
public class AppManager {

	// activity的堆栈
	private static Stack<Activity> activityStack;

	private static AppManager instance;

	/**
	 * 单例
	 */
	public static AppManager getAppManager() {

		if (instance == null) {
			instance = new AppManager();
		}
		return instance;

	}

	/**
	 * 添加activity到堆栈
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {

		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前activity(堆栈中最后一个压入的activity)
	 * 
	 * @return
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前的activity(堆栈中最后一个压入的activity)
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();

		finishActivity(activity);
	}

	/**
	 * 结束指定的activity
	 */
	public void finishActivity(Activity activity) {

		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束所有的activity
	 */
	public void finishAllActivity() {

		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (activityStack.get(i) != null) {
				activityStack.get(i).finish();
			}
		}

		activityStack.clear();

	}

	/**
	 * 退出应用程序
	 * 
	 * @param context
	 */
	public void AppExit(Context context) {

		finishAllActivity();

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// // 2.2之前的rom就用restartPackage之后的就用killBackgroundProcesses
		if (BaseApplication.isMethodsCompat(Build.VERSION_CODES.FROYO)) {
			activityManager.killBackgroundProcesses(context.getPackageName());
		} else {

			activityManager.restartPackage(context.getPackageName());
		}

		System.exit(0);
	}
}
