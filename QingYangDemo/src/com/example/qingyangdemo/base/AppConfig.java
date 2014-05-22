package com.example.qingyangdemo.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.content.Context;

/**
 * 应用配置类:保存用户相关信息和配置信息
 * 
 * @author 赵庆洋
 * 
 */
public class AppConfig {

	private final static String APP_CONFIG = "config";

	public final static String CON_APP_UNIQUEID = "APP_UNIQUEID";

	public final static String CONF_CHECKUP = "perf_checkup";

	public final static String CONF_VOICE = "perf_voice";

	public final static String CONF_IP_ADDRESS = "perf_ip_adress";

	private Context context;

	private static AppConfig appConfig;

	/**
	 * 单例获取appconfig对象
	 * 
	 * @param context
	 * @return
	 */
	public static AppConfig getAppConfig(Context context) {
		if (appConfig == null) {
			appConfig = new AppConfig();
			appConfig.context = context;
		}
		return appConfig;
	}

	/**
	 * 保存到配置集合对象
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		Properties properties = get();
		properties.setProperty(key, value);
		setProps(properties);
	}

	/**
	 * 保存到配置集合对象
	 * 
	 * @param ps
	 */
	public void set(Properties ps) {
		Properties properties = get();
		properties.putAll(ps);
		setProps(properties);
	}

	public void remove(String... key) {
		Properties properties = get();
		for (String k : key) {
			properties.remove(k);
		}
		setProps(properties);
	}

	/**
	 * key取value
	 * 
	 * @param key
	 * @return 如果有配置问价 返回value 否则返回null
	 */
	public String get(String key) {
		Properties props = get();
		return (props != null) ? props.getProperty(key) : null;
	}

	/**
	 * 获取配置集合对象
	 * 
	 * @return
	 */
	public Properties get() {
		FileInputStream fis = null;
		Properties properties = new Properties();
		try {
			// 获取app_config目录下的config
			File dirConf = context.getDir(APP_CONFIG, Context.MODE_PRIVATE);

			fis = new FileInputStream(dirConf.getPath() + File.separator
					+ APP_CONFIG);
			properties.load(fis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return properties;
	}

	/**
	 * 设置配置集合对象
	 * 
	 * @return
	 */
	public Properties setProps(Properties properties) {
		FileOutputStream fos = null;
		try {
			// 把config建在(自定义)app_config的目录下
			File dirConf = context.getDir(APP_CONFIG, Context.MODE_PRIVATE);

			File conf = new File(dirConf, APP_CONFIG);

			fos = new FileOutputStream(conf);

			properties.store(fos, null);

			fos.flush();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return properties;
	}
}
