package com.example.qingyangdemo.common;

import com.example.qingyangdemo.R;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.ui.FeedBackDialog;
import com.example.qingyangdemo.ui.LoginDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author 赵庆洋
 * 
 */
public class UIHelper {

	/**
	 * 弹出toast消息
	 * 
	 * @param context
	 * @param msg
	 */
	public static void ToastMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context context, int msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出toast消息(自设显示时间)
	 * 
	 * @param context
	 * @param msg
	 * @param time
	 */
	public static void ToastMessage(Context context, String msg, int time) {
		Toast.makeText(context, msg, time).show();
	}

	/**
	 * 显示登录界面
	 * 
	 * @param context
	 */
	public static void showLoginDialog(Context context) {
		Intent intent = new Intent(context, LoginDialog.class);
		context.startActivity(intent);
	}

	/**
	 * 发送App错误异常报告
	 * 
	 * @param context
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context context,
			final String crashReport) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent intent = new Intent(Intent.ACTION_SEND);
						// intent.setType("text/plain");// 模拟器
						intent.setType("message/rfc822");// 真机
						// 发送邮件 Intent.EXTRA_CC抄送 Intent.EXTRA_BCC密送者
						intent.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "736909686@qq.com" });
						// 标题
						intent.putExtra(Intent.EXTRA_SUBJECT,
								"清扬android客户端-错误报告");
						// 内容
						intent.putExtra(Intent.EXTRA_TEXT, crashReport);

						context.startActivity(Intent.createChooser(intent,
								"发送错误报告"));

						AppManager.getAppManager().AppExit(context);

					}
				});

		builder.setNegativeButton(R.string.sure, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				AppManager.getAppManager().AppExit(context);
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				AppManager.getAppManager().AppExit(context);
			}
		});
		alertDialog.show();

	}

	/**
	 * 用户登录或注销
	 * 
	 * @param activity
	 */
	public static void loginOrLogout(Activity activity) {
		BaseApplication application = (BaseApplication) activity
				.getApplication();

		if (application.isLogin()) {
			application.layout();
			ToastMessage(application, "已退出登陆");
		} else {
			showLoginDialog(activity);
		}
	}

	/**
	 * 清除缓存
	 * 
	 * @param activity
	 */
	public static void clearAppCache(Activity activity) {
		final BaseApplication application = (BaseApplication) activity
				.getApplication();

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					ToastMessage(application, "缓存清除成功");
				} else {
					ToastMessage(application, "缓存清除失败");
				}
			};
		};

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();

				try {
					application.clearAppCache();
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();

					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}).start();

	}

	/**
	 * 清除下载数据
	 * 
	 * @param activity
	 */
	public static void clearDownCache(Activity activity) {
		final BaseApplication application = (BaseApplication) activity
				.getApplication();

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					ToastMessage(application, "清除成功");
				} else {
					ToastMessage(application, "清除失败");
				}
			};
		};

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();

				try {
					application.clearDownCache();
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();

					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}).start();

	}

	/**
	 * 显示检查更新页面
	 * 
	 * @param settingActivity
	 */
	public static void showFeedBack(Context context) {
		Intent intent = new Intent(context, FeedBackDialog.class);
		context.startActivity(intent);
	}

	/**
	 * 点击返回监听事件
	 * 
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}
}
