package com.example.qingyangdemo.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import com.example.qingyangdemo.R;
import com.example.qingyangdemo.base.AppException;
import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.bean.Update;
import com.example.qingyangdemo.net.NetClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 应用程序更新管理类
 * 
 * @author 赵庆洋
 * 
 */
public class UpdateManager {

	// 没有sd卡状态
	private static final int DOWN_NOSDCARD = 0;
	// 更新状态
	private static final int DOWN_UPDATE = 1;
	// 结束状态
	private static final int DOWN_OVER = 2;

	private static final int DIALOG_TYPE_LATEST = 0;

	private static final int DIALOG_TYPE_FAIL = 1;

	private static UpdateManager updateManager;

	private Context context;

	// 终止标记
	private boolean interceptFlag;

	// 当前版本的名称
	private String curVersionName = "";

	// 当前版本号
	private int curVersionCode;

	// 查询动画
	private ProgressDialog progressDialog;

	// 已经是最新或无法获取最新版本的对话框
	private Dialog latestOrFailDialog;

	// 服务端获取的更新信息
	private Update mUpdate;

	// 返回回来的安装包url
	private String apkUrl;

	// 提示语
	private String updateMsg;

	// 通知对话框
	private Dialog noticeDialog;

	// 下载对话框
	private Dialog downloadDialog;

	// 下载进度条
	private ProgressBar mProgressBar;

	// 下载详细文本
	private TextView mProgressText;

	// 下载线程
	private Thread downLoadThread;

	// 下载路径
	private String savePath;

	// apk文件路径
	private String apkFilePath;

	// 临时文件路径
	private String tempFilePath;

	// 进度值
	private int progress;

	// 下载文件的大小
	private String apkFileSize;

	// 已下载文件的大小
	private String tmpFileSize;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgressBar.setProgress(progress);
				mProgressText.setText(tmpFileSize + "/" + apkFileSize);
				break;

			case DOWN_OVER:
				downloadDialog.dismiss();
				downloadDialog = null;
				installApk();
				break;
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				downloadDialog = null;
				UIHelper.ToastMessage(context, "无法下载安装文件，请检查SD卡是否挂载", 3000);
			}
		};
	};

	public static UpdateManager getUpdateManager() {
		if (updateManager == null) {
			updateManager = new UpdateManager();
		}
		updateManager.interceptFlag = false;
		return updateManager;
	}

	/**
	 * 获取当前客户端的版本信息
	 */
	private void getCurrentVersion() {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			curVersionName = info.versionName;
			curVersionCode = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查app更新
	 * 
	 * @param context
	 * @param isShowMsg
	 *            是否显示查询dialog
	 */
	public void checkAppUpdate(final Context context, final boolean isShowMsg) {
		this.context = context;
		getCurrentVersion();
		if (isShowMsg) {
			if (progressDialog == null) {
				progressDialog = ProgressDialog.show(context, null,
						"正在检测,请稍后...", true, true);
			} else if (progressDialog.isShowing()
					|| (latestOrFailDialog != null && latestOrFailDialog
							.isShowing())) {
				return;
			}
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 进度条对话框不显示
				if (progressDialog != null && !progressDialog.isShowing()) {
					return;
				}

				if (isShowMsg && progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}

				if (msg.what == 1) {
					mUpdate = (Update) msg.obj;

					if (mUpdate != null) {
						if (curVersionCode < mUpdate.getVersionCode()) {
							apkUrl = mUpdate.getDownloadUrl();
							updateMsg = mUpdate.getUpdateLog();
							showNoticeDialog();
						} else if (isShowMsg) {
							showLatestOrFailDialog(DIALOG_TYPE_FAIL);
						}
					}
				}
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();

				try {
					Update update = NetClient
							.checkVersion((BaseApplication) context
									.getApplicationContext());

					msg.what = 1;

					msg.obj = update;

					handler.sendMessage(msg);

				} catch (AppException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	/**
	 * 显示已经最新或者无法获取版本信息的对话框
	 * 
	 * @param dialogTypeFail
	 */
	protected void showLatestOrFailDialog(int dialogType) {
		if (latestOrFailDialog != null) {
			latestOrFailDialog.dismiss();
			latestOrFailDialog = null;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle("系统提示");

		if (dialogType == DIALOG_TYPE_FAIL) {
			builder.setMessage("无法获取版本更新信息");
		} else if (dialogType == DIALOG_TYPE_LATEST) {
			builder.setMessage("您已经是最新版本");
		}

		builder.setPositiveButton("确定", null);
		latestOrFailDialog = builder.create();
		latestOrFailDialog.show();
	}

	/**
	 * 显示版本更新通知对话框
	 */
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle("软件版本更新");

		builder.setMessage(updateMsg);

		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});

		builder.setNegativeButton("以后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		noticeDialog = builder.create();

		noticeDialog.show();

	}

	/**
	 * 显示下载对话框
	 */
	protected void showDownloadDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("正在下载新版本");

		final LayoutInflater inflater = LayoutInflater.from(context);

		View v = inflater.inflate(R.layout.update_progress, null);

		mProgressBar = (ProgressBar) v.findViewById(R.id.update_progress);

		mProgressText = (TextView) v.findViewById(R.id.update_progress_text);

		builder.setView(v);

		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});

		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});

		downloadDialog = builder.create();

		downloadDialog.setCanceledOnTouchOutside(false);

		downloadDialog.show();

		downloadApk();
	}

	private Runnable downApkRunnbale = new Runnable() {

		@Override
		public void run() {

			try {

				String apkName = mUpdate.getVersionName() + ".apk";

				String tmpName = mUpdate.getVersionName() + ".tmp";

				// 判断是否挂载了SD卡
				String storageState = Environment.getExternalStorageState();

				if (storageState.equals(Environment.MEDIA_MOUNTED)) {
					savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/qingyang/Update";
					File file = new File(savePath);
					if (!file.exists()) {
						file.mkdir();
					}

					apkFilePath = savePath + "/" + apkName;

					tempFilePath = savePath + "/" + tmpName;
				}

				if (apkFilePath == null || apkFilePath.equals("")) {
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}

				File apkFile = new File(apkFilePath);

				// 如果已经存在更新文件
				if (apkFile.exists()) {
					downloadDialog.dismiss();
					downloadDialog = null;
					installApk();
					return;
				}

				File tmpFile = new File(tempFilePath);

				FileOutputStream fos = new FileOutputStream(tmpFile);

				URL url = new URL(apkUrl);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();

				conn.connect();

				int length = conn.getContentLength();

				InputStream is = conn.getInputStream();

				DecimalFormat df = new DecimalFormat("0.00");

				apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

				int count = 0;

				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);

					count += numread;
					// 当先下载文件大小
					tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";

					// 当前进度值
					progress = (int) (((float) count / length) * 100);

					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);

					if (numread <= 0) {
						// 下载完成-奖临时下载文件装成APK文件
						if (tmpFile.renameTo(apkFile)) {
							// 通知安装
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载

				fos.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	/**
	 * 下载apk
	 */
	private void downloadApk() {
		downLoadThread = new Thread(downApkRunnbale);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 */
	private void installApk() {
		File apkFile = new File(apkFilePath);

		if (!apkFile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkFile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
	}
}
