package com.example.qingyangdemo.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.http.HttpException;
import com.example.qingyangdemo.R;
import com.example.qingyangdemo.common.FileUtil;
import com.example.qingyangdemo.common.UIHelper;
import com.example.qingyangdemo.net.Constant;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

/**
 * 应用程序异常类：用于捕获异常和提示错误信息
 * 
 * @author 赵庆洋
 * 
 */
public class AppException extends Exception implements UncaughtExceptionHandler {

	public final static String LOG_TAG = "qingyang_log";

	// 错误异常类型
	public final static byte TYPE_NETWORK = 0x01;
	public final static byte TYPE_SOCKET = 0x02;
	public final static byte TYPE_HTTP_CODE = 0x03;
	public final static byte TYPE_HTTP_ERROR = 0x04;
	public final static byte TYPE_XML = 0x05;
	public final static byte TYPE_IO = 0x06;
	public final static byte TYPE_RUN = 0x07;

	private byte type;

	private int code;

	// 系统默认的UncaughtExceptionHandler处理类
	private UncaughtExceptionHandler mDefaultHandler;

	private AppException() {
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	private AppException(byte type, int code, Exception excp) {
		super(excp);
		this.type = type;
		this.code = code;
		this.saveErrorLog(excp);
	}

	/**
	 * 获取APP异常崩溃处理对象
	 * 
	 * @param context
	 * @return
	 */
	public static AppException getAppExceptionHandler() {
		return new AppException();
	}

	/**
	 * 友好的错误提示
	 * 
	 * @param context
	 */
	public void makeToast(Context context) {

		switch (this.getType()) {
		case TYPE_HTTP_CODE:
			String err = context.getString(R.string.http_status_code_error,
					this.getCode());
			Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_HTTP_ERROR:
			Toast.makeText(context,
					context.getString(R.string.http_exception_error),
					Toast.LENGTH_SHORT).show();
		case TYPE_SOCKET:
			Toast.makeText(context,
					context.getString(R.string.socket_exception_error),
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_NETWORK:
			Toast.makeText(context,
					context.getString(R.string.network_not_connected),
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_XML:
			Toast.makeText(context,
					context.getString(R.string.xml_parser_failed),
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_IO:
			Toast.makeText(context,
					context.getString(R.string.io_exception_error),
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_RUN:
			Toast.makeText(context,
					context.getString(R.string.app_run_code_error),
					Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	/**
	 * 保存错误日志
	 * 
	 * @param excp
	 */
	public void saveErrorLog(Exception excp) {
		String errorlog = Constant.LOG_NAME;
		String logFilePath = "";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {

			logFilePath = FileUtil.fileDirectory(Constant.LOG_PATH, errorlog);

			// 没有挂载SD卡，无法写文件
			if (logFilePath.equals("")) {
				return;
			}
			File logFile = new File(logFilePath);

			if (!logFile.exists()) {
				logFile.createNewFile();
			}

			fw = new FileWriter(logFile, true);

			pw = new PrintWriter(fw);

			excp.printStackTrace(pw);

			pw.close();

			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO
		// 异常捕获处理
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	public static AppException http(int code) {
		return new AppException(TYPE_HTTP_CODE, code, null);
	}

	public static AppException http(Exception e) {
		return new AppException(TYPE_HTTP_ERROR, 0, e);
	}

	public static AppException socket(Exception e) {
		return new AppException(TYPE_SOCKET, 0, e);
	}

	public static AppException io(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(TYPE_NETWORK, 0, e);
		} else if (e instanceof IOException) {
			return new AppException(TYPE_IO, 0, e);
		}
		return run(e);
	}

	public static AppException network(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(TYPE_NETWORK, 0, e);
		} else if (e instanceof HttpException) {
			return http(e);
		} else if (e instanceof SocketException) {
			return socket(e);
		}
		return http(e);
	}

	public static AppException xml(Exception e) {
		return new AppException(TYPE_XML, 0, e);
	}

	public static AppException run(Exception e) {
		return new AppException(TYPE_RUN, 0, e);
	}

	public byte getType() {
		return type;
	}

	public int getCode() {
		return code;
	}

	/**
	 * 自定义异常处理:收集错误信息并发送错误报告
	 * 
	 * @param ex
	 * @return 处理异常返回true否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		final Context context = AppManager.getAppManager().currentActivity();

		if (context == null) {
			return false;
		}

		final String crashReport = getCrashReport(context, ex);

		// 保存错误日志
		saveErrorLog((Exception) ex);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				UIHelper.sendAppCrashReport(context, crashReport);
				Looper.loop();
			}
		}).start();
		return true;
	}

	/**
	 * 获取app崩溃异常报告
	 * 
	 * @param context
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Context context, Throwable ex) {
		PackageInfo packageInfo = ((BaseApplication) context
				.getApplicationContext()).getPackageInfo();

		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("Version: " + packageInfo.versionName + "("
				+ packageInfo.versionCode + ")\n");

		stringBuffer.append("Android: " + Build.VERSION.RELEASE + "("
				+ Build.MODEL + ")\n");

		stringBuffer.append("Exception: " + ex.getMessage() + "\n");

		// 异常元素集合
		StackTraceElement[] elements = ex.getStackTrace();

		for (int i = 0; i < elements.length; i++) {
			stringBuffer.append(elements[i].toString() + "\n");
		}

		return stringBuffer.toString();
	}
}
