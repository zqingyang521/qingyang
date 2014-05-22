package com.example.qingyangdemo.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

/**
 * 应用程序Activity的基类
 * 
 * @author 赵庆洋
 * 
 */
public class BaseActivity extends Activity {

	// 是否允许全屏
	private boolean allowFullScreen = true;

	// 是否允许销毁
	private boolean allowDestory = true;

	private View view;

	// 屏幕宽度
	protected int screenWidth;

	// 屏幕高度
	protected int screenHeight;

	// 密度
	protected float density;

	// 所有的异步任务
	protected List<AsyncTask<Void, Void, Boolean>> myAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();

	protected BaseApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		allowFullScreen = true;

		AppManager.getAppManager().addActivity(this);

		application = (BaseApplication) getApplication();

		// DisplayMetrics 类提供了一种关于显示的通用信息，如显示大小，分辨率和字体
		DisplayMetrics metrics = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		screenWidth = metrics.widthPixels;

		screenHeight = metrics.heightPixels;

		density = metrics.density;

	}

	/**
	 * 添加异步任务到数组中
	 * 
	 * @param asyncTask
	 */
	public void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
		myAsyncTasks.add(asyncTask.execute());
	}

	public boolean isAllowFullScreen() {
		return allowFullScreen;
	}

	/**
	 * 设置是否全屏
	 * 
	 * @param allowFullScreen
	 */
	public void setAllowFullScreen(boolean allowFullScreen) {
		this.allowFullScreen = allowFullScreen;
	}

	public void setAllowDestory(boolean allowDestory) {
		this.allowDestory = allowDestory;
	}

	public void setAllowDestory(boolean allowDestory, View view) {
		this.allowDestory = allowDestory;
		this.view = view;
	}

	/**
	 * 清除所有的异步任务
	 */
	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Boolean>> iterator = myAsyncTasks
				.iterator();

		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Boolean> asyncTask = iterator.next();

			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}

		myAsyncTasks.clear();
	}

	/**
	 * 通过Class跳转界面
	 */
	protected void startActivity(Class<?> cls) {

		startActivity(cls, null);
	}

	/**
	 * 含有Bundle通过Class跳转界面
	 */
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();

		intent.setClass(this, cls);

		if (bundle != null) {
			intent.putExtras(bundle);
		}

		startActivity(intent);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		clearAsyncTask();

		// AppManager.getAppManager().finishActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {

			view.onKeyDown(keyCode, event);

			if (!allowDestory) {
				return false;
			}

		}

		return super.onKeyDown(keyCode, event);
	}

}
