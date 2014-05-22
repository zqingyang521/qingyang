package com.example.qingyangdemo;

import java.io.File;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.common.FileUtil;
import com.example.qingyangdemo.common.UIHelper;
import com.example.qingyangdemo.common.UpdateManager;
import com.example.qingyangdemo.net.Constant;
import com.example.qingyangdemo.ui.IpSetDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 系统设置类
 * 
 * @author 赵庆洋
 * 
 */
public class SettingActivity extends PreferenceActivity {

	private SharedPreferences mPreferences;

	private BaseApplication myApplication;

	// 设置Ip
	private Preference ipset;

	// 账户
	private Preference account;

	// 清除缓存
	private Preference cache;

	// 清除已下载数据
	private Preference down;

	// 是否启动应用检查更新
	private CheckBoxPreference checkup;

	// 是否开启提示声音
	private CheckBoxPreference voice;

	// 检查更新
	private Preference update;

	// 意见反馈
	private Preference feedback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);

		myApplication = (BaseApplication) getApplication();

		// 设置显示Preferences
		addPreferencesFromResource(R.xml.preferences);

		// 获得SharedPreferences
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		ListView localListView = getListView();
		localListView.setBackgroundColor(0);
		localListView.setCacheColorHint(0);
		((ViewGroup) localListView.getParent()).removeView(localListView);
		ViewGroup localViewGroup = (ViewGroup) getLayoutInflater().inflate(
				R.layout.setting_activity, null);
		((ViewGroup) localViewGroup.findViewById(R.id.setting_content))
				.addView(localListView, -1, -1);
		setContentView(localViewGroup);

		initView();

	}

	private void initView() {

		// 登陆/注销
		account = findPreference("account");

		if (myApplication.isLogin()) {
			account.setTitle(R.string.main_menu_logout);
		} else {
			account.setTitle(R.string.main_menu_login);
		}
		account.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.loginOrLogout(SettingActivity.this);

				if (myApplication.isLogin()) {
					account.setTitle(R.string.main_menu_logout);
				} else {
					account.setTitle(R.string.main_menu_login);
				}

				return true;
			}
		});

		// 设置Ip
		ipset = findPreference("ipset");

		ipset.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingActivity.this,
						IpSetDialog.class);

				intent.putExtra(IpSetDialog.IS_STRAT_MAIN, false);

				startActivity(intent);
				return true;
			}
		});

		// 提示声音
		voice = (CheckBoxPreference) findPreference("voice");

		voice.setChecked(myApplication.isVoice());

		if (myApplication.isVoice()) {
			voice.setSummary("已开启声音提示");
		} else {
			voice.setSummary("已关闭声音提示");
		}

		voice.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				myApplication.setConfigVoice(voice.isChecked());
				if (voice.isChecked()) {
					voice.setSummary("已开启声音提示");
				} else {
					voice.setSummary("已关闭声音提示");
				}
				return true;
			}
		});

		// 启动检查更新
		checkup = (CheckBoxPreference) findPreference("checkup");

		checkup.setChecked(myApplication.isCheckUp());

		checkup.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				myApplication.setConfigCheckUp(checkup.isChecked());
				return true;
			}
		});

		// 计算缓存大小
		long fileSize = 0;
		String cacheSize = "0KB";
		File filesDir = getFilesDir();
		File cacheDir = getCacheDir();

		fileSize += FileUtil.getDirSize(filesDir);
		fileSize += FileUtil.getDirSize(cacheDir);

		// 2.2版本才有将应用转移到sd卡的功能(当前版本大于2.2的版本)
		if (BaseApplication
				.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			File externalCache = getExternalCacheDir();
			fileSize += FileUtil.getDirSize(externalCache);
		}
		if (fileSize > 0) {
			cacheSize = FileUtil.formatFileSize(fileSize);
		}

		// 清除缓存
		cache = findPreference("cache");

		cache.setSummary(cacheSize);

		cache.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.clearAppCache(SettingActivity.this);
				cache.setSummary("0KB");
				return true;
			}
		});

		// 计算下载大小
		long downFileSize = 0;

		String downSize = "0KB";

		downFileSize = FileUtil.getDirSize(FileUtil
				.getDirectoryFile(Constant.DOWN_PATH));

		if (downFileSize > 0) {
			downSize = FileUtil.formatFileSize(downFileSize);
		}

		down = findPreference("down");

		down.setSummary(downSize);

		down.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.clearDownCache(SettingActivity.this);
				down.setSummary("0KB");
				return true;
			}
		});

		feedback = findPreference("feedback");

		feedback.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.showFeedBack(SettingActivity.this);
				return true;
			}
		});

		// 版本更新
		update = findPreference("update");

		update.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				UpdateManager.getUpdateManager().checkAppUpdate(
						SettingActivity.this, true);
				return true;
			}
		});
	}

	public void back(View paramView) {
		finish();
	}

	@Override
	protected void onDestroy() {
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}

}
