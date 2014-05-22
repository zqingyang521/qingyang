package com.example.qingyangdemo.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.qingyangdemo.base.AppException;
import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.bean.Update;
import com.example.qingyangdemo.bean.User;

/**
 * 访问网络公共类
 * 
 * @author 赵庆洋
 * 
 */
public class NetClient {

	public static final String UTF_8 = "UTF-8";

	// 连接超时时间
	private static final int TIMEOUT_CONNECTION = 2000;

	// 重新操作次数
	private static final int RETRY_TIME = 3;

	private static String userAgent;

	private static String getUserAgent(BaseApplication application) {
		if (userAgent == null || userAgent.equals("")) {
			StringBuffer sb = new StringBuffer("qingyang");
			// app版本
			sb.append('/' + application.getPackageInfo().versionName + '_'
					+ application.getPackageInfo().versionCode);
			// andorid 平台
			sb.append("/Android");
			// 手机系统版本
			sb.append("/" + android.os.Build.VERSION.RELEASE);
			// 手机型号
			sb.append("/" + android.os.Build.MODEL);
			// 客户端唯一标识
			sb.append("/" + application.getAppId());

			userAgent = sb.toString();
		}
		return userAgent;
	}

	private static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();

		// 设置默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// 设置连接超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(TIMEOUT_CONNECTION);
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}

	/**
	 * get方法url
	 * 
	 * @param p_url
	 * @param params
	 * @return
	 */
	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder();
		if (url.indexOf("?") < 0)
			url.append("?");

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			// 不做URLEncoder处理
			// url.append(URLEncoder.encode(String.valueOf(params.get(name)),
			// UTF_8));
		}
		return url.toString().replace("?&", "?");
	}

	private static GetMethod getHttpGet(String url, String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_CONNECTION);
		httpGet.setRequestHeader("Host", URL.HOST);
		httpGet.setRequestHeader("Connection", "Keep_Alive");
		httpGet.setRequestHeader("User_Agent", userAgent);
		return httpGet;
	}

	private static PostMethod getHttpPost(String url, String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 设置请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_CONNECTION);
		httpPost.setRequestHeader("Host", URL.HOST);
		httpPost.setRequestHeader("Connection", "Keep_Alive");
		httpPost.setRequestHeader("User_Agent", userAgent);
		return httpPost;
	}

	/**
	 * 公共用的get方法
	 * 
	 * @param application
	 * @param url
	 * @return
	 * @throws AppException
	 */
	private static String http_get(BaseApplication application, String url)
			throws AppException {
		String userAgent = getUserAgent(application);

		HttpClient httpClient = null;

		GetMethod httpGet = null;

		String responseBody = "";

		int time = 0;

		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, userAgent);
				int statusCode = httpClient.executeMethod(httpGet);

				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}

				responseBody = httpGet.getResponseBodyAsString();

				break;

			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		return responseBody;
	}

	/**
	 * 公用的post方法
	 * 
	 * @param application
	 * @param url
	 * @param params
	 * @param files
	 * @return
	 * @throws AppException
	 */
	private static String http_post(BaseApplication application, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException {

		String userAgent = getUserAgent(application);

		HttpClient httpClient = null;

		PostMethod httpPost = null;

		// post表单参数处理
		int length = (params == null ? 0 : params.size())
				+ (files == null ? 0 : files.size());

		Part[] parts = new Part[length];

		int i = 0;

		if (params != null) {
			for (String name : params.keySet()) {
				parts[i++] = new StringPart(name, String.valueOf(params
						.get(name)), UTF_8);
			}
		}

		if (files != null) {
			for (String fileName : files.keySet()) {
				try {
					parts[i++] = new FilePart(fileName, files.get(fileName));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		String responseBody = "";

		int time = 0;

		do {
			try {
				httpClient = getHttpClient();

				httpPost = getHttpPost(url, userAgent);

				httpPost.setRequestEntity(new MultipartRequestEntity(parts,
						httpPost.getParams()));

				int statusCode = httpClient.executeMethod(httpPost);

				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				responseBody = httpPost.getResponseBodyAsString();

				// 做临时数据
				if (responseBody.equals("登录成功")) {
					responseBody = "{\"response\":{\"isErr\":false,\"uid\":1,\"username\":\"xgxx\",\"password\":1234}}";
				} else {
					responseBody = "{\"response\":{\"isErr\":true,\"msg\":\"用户名密码错误！\"}}";
				}

				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		return responseBody;
	}

	public static Bitmap getNetBitmap(String url) throws AppException {
		HttpClient httpClient = null;

		GetMethod httpGet = null;

		Bitmap bitmap = null;

		int time = 0;

		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				InputStream inStream = httpGet.getResponseBodyAsStream();
				bitmap = BitmapFactory.decodeStream(inStream);
				inStream.close();
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return bitmap;
	}

	/**
	 * 登陆验证
	 * 
	 * @param application
	 * @param account
	 * @param password
	 * @return
	 * @throws AppException
	 */
	public static User login(BaseApplication application, String account,
			String password) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", account);
		params.put("password", password);
		return User.parse(http_post(application, URL.LOGIN_URL, params, null));
	}

	/**
	 * 检查版本更新(做的假数据)
	 * 
	 * @param application
	 * @return
	 * @throws AppException
	 */
	public static Update checkVersion(BaseApplication application)
			throws AppException {
		// try {
		// return Update.parse(http_get(application, URL.UPDATE_VERSOIN));
		// } catch (Exception e) {
		// if (e instanceof AppException) {
		// throw (AppException) e;
		// }
		// throw AppException.network(e);
		// }

		Update update = new Update();

		update.setVersionName("qingyang");

		update.setVersionCode(2);

		update.setUpdateLog("版本信息：qingyang for android 2.0版更新日志：1.增加了新版本检测功能。2.修复了网络连接的问题。如果是升级失败，请到http://www.xxx.com/app下载最新版本。");

		update.setDownloadUrl("http://down.angeeks.com/c/d2/d10100/10100520.apk");

		return update;
	}
}
