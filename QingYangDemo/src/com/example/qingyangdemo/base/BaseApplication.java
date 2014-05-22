package com.example.qingyangdemo.base;

import java.io.File;
import java.util.Properties;
import java.util.UUID;
import com.example.qingyangdemo.R;
import com.example.qingyangdemo.bean.User;
import com.example.qingyangdemo.common.FileUtil;
import com.example.qingyangdemo.common.StringUtil;
import com.example.qingyangdemo.net.Constant;
import com.example.qingyangdemo.net.NetClient;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.webkit.CacheManager;

/**
 * 全局应用类：用于保存和调用全局应用配置
 * 
 * @author 赵庆洋
 * 
 */
public class BaseApplication extends Application {

	// wifi状态 联通状态 移动状态
	private static int NETTYPE_WIFI = 0x01;
	private static int NETTYPE_CMWAP = 0x02;
	private static int NETTYPE_CMNET = 0x03;

	// 登陆状态
	private boolean login = false;

	// 登陆的ID
	private int loginUnid;

	@Override
	public void onCreate() {
		super.onCreate();

		// 注册app异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler());
	};

	/**
	 * 现在翻转屏是否是横向的
	 * 
	 * @return
	 */
	public boolean isLandscape() {
		Configuration config = getResources().getConfiguration();

		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			return false;
		} else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是平板登陆
	 * 
	 * @return
	 */
	public boolean isTablet() {

		return getResources().getBoolean(R.bool.isTablet);
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0:没有网络 1:wifi网络 2:wap网络 3:net网络
	 */
	public int getNetworkType() {
		int netType = 0;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo ni = cm.getActiveNetworkInfo();

		if (ni == null) {
			return netType;
		}

		int nType = ni.getType();

		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = ni.getExtraInfo();
			if (!StringUtil.isEmpty(extraInfo)) {
				// 如果小写等于cmnet
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}

		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param versionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int versionCode) {
		int currentVersion = Build.VERSION.SDK_INT;
		return currentVersion >= versionCode;
	}

	/**
	 * 获取app安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;

		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}

		if (info == null)
			info = new PackageInfo();

		return info;
	}

	/**
	 * 是否发出提示音
	 * 
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);

		// 默认是开启提示声音
		if (StringUtil.isEmpty(perf_voice))
			return true;
		else
			return StringUtil.toBool(perf_voice);

	}

	/**
	 * 设置提示音
	 * 
	 * @return
	 */
	public void setConfigVoice(boolean b) {
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}

	/**
	 * 设置ip地址
	 * 
	 * @return
	 */
	public void setIpAdress(String ipAdress) {
		setProperty(AppConfig.CONF_IP_ADDRESS, ipAdress);
	}

	/**
	 * 是否检查更新
	 * 
	 * @return
	 */
	public boolean isCheckUp() {
		String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);

		// 默认是关闭的
		if (StringUtil.isEmpty(perf_checkup))
			return false;
		else
			return StringUtil.toBool(perf_checkup);
	}

	/**
	 * 获取ip地址
	 * 
	 * @return
	 */
	public String getIpAdress() {
		String ip_adress = getProperty(AppConfig.CONF_IP_ADDRESS);

		if (StringUtil.isEmpty(ip_adress))
			return "0.0.0.0";
		else
			return ip_adress;
	}

	/**
	 * 设置是否启动检查更新
	 * 
	 * @param b
	 */
	public void setConfigCheckUp(boolean b) {
		setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
	}

	/**
	 * 获取App的唯一标识并保存到配置中
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueId = getProperty(AppConfig.CON_APP_UNIQUEID);

		if (StringUtil.isEmpty(uniqueId)) {
			uniqueId = UUID.randomUUID().toString();
			setProperty(AppConfig.CON_APP_UNIQUEID, uniqueId);
		}
		return uniqueId;
	}

	/**
	 * 初始化用户信息
	 */
	public void initLoginInfo() {
		User user = getLoginInfo();
		if (user != null && user.getUid() > 0 && user.isRememberMe()) {
			this.login = true;
			this.loginUnid = user.getUid();
		} else {
			this.layout();
		}
	}

	/**
	 * 用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}

	/**
	 * 获取登录用户id
	 * 
	 * @return
	 */
	public int getLoginUid() {
		return this.loginUnid;
	}

	/**
	 * 用户注销
	 */
	public void layout() {
		login = false;
		loginUnid = 0;
	}

	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		layout();
		removeProperty("user.uid", "user.name", "user.password",
				"user.isRememberMe");
	}

	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public User getLoginInfo() {
		User user = new User();
		user.setUid(StringUtil.toInt(getProperty("user.uid"), 0));
		user.setName(getProperty("user.name"));
		user.setRememberMe(StringUtil.toBool(getProperty("user.isRememberMe")));
		user.setPassword(getProperty("user.password"));
		return user;
	}

	/**
	 * 保存用户信息
	 * 
	 * @param user
	 */
	public void saveLoginInfo(final User user) {
		this.loginUnid = user.getUid();
		this.login = true;
		setProperties(new Properties() {
			{
				setProperty("user.uid", String.valueOf(user.getUid()));
				setProperty("user.name", user.getName());
				setProperty("user.password", user.getPassword());
				setProperty("user.isRememberMe",
						String.valueOf(user.isRememberMe()));
			}
		});
	}

	public boolean containsProperty(String key) {
		Properties properties = getProperties();
		return properties.containsKey(key);
	}

	/**
	 * 获取配置集合
	 * 
	 * @return
	 */
	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperties(Properties properties) {
		AppConfig.getAppConfig(this).set(properties);
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	/**
	 * 清除缓存
	 */
	public void clearAppCache() {
		// 清除webview缓存
		File file = CacheManager.getCacheFileBaseDir();
		if (file != null && file.exists() && file.isDirectory()) {
			for (File item : file.listFiles()) {
				item.delete();
			}
			file.delete();
		}

		deleteDatabase("webview.db");
		deleteDatabase("webview.db-shm");
		deleteDatabase("webview.db-wal");
		deleteDatabase("webviewCache.db");
		deleteDatabase("webviewCache.db-shm");
		deleteDatabase("webviewCache.db-wal");

		// 清除数据缓存
		clearCacheFolder(getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(getCacheDir(), System.currentTimeMillis());

		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(getExternalCacheDir(), System.currentTimeMillis());
		}
		// 清楚编辑器保存的临时文件
		Properties props = getProperties();

		for (Object key : props.keySet()) {
			String _key = key.toString();
			if (_key.startsWith("temp"))
				removeProperty(_key);
		}
	}

	/**
	 * 清除下载缓存
	 */
	public void clearDownCache() {
		// 清除下载缓存
		File file = FileUtil.getDirectoryFile(Constant.DOWN_PATH);
		if (file != null && file.exists() && file.isDirectory()) {
			for (File item : file.listFiles()) {
				item.delete();
			}
			file.delete();
		}
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param curTime
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;

		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					// 如果文件的最后修改时间小于当前系统时间
					if (child.lastModified() < curTime) {
						// 删除
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 用户登录验证
	 * 
	 * @param userName
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public User loginVerify(String userName, String pwd) throws AppException {
		return NetClient.login(this, userName, pwd);
	}
}
