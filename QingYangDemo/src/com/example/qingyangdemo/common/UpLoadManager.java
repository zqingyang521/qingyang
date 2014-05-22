package com.example.qingyangdemo.common;

import java.io.File;
import com.example.qingyangdemo.R;
import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.thread.UploadThread;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 上传管理
 * 
 * @author 赵庆洋
 * 
 */
public class UpLoadManager {
	// 上传失败状态
	public final static int UPLOAD_FAILL = -1;
	// 上传成功状态
	public final static int UPLOAD_SUCCESS = 1;
	// 上传更新状态
	public final static int UPLOAD_UPDATE = 2;

	private Context context;

	// 是否上传
	private boolean start = true;

	private static UpLoadManager uploadManager;

	private UploadThread runnable;

	public static UpLoadManager getUpManager() {

		if (uploadManager == null) {
			uploadManager = new UpLoadManager();
		}
		return uploadManager;
	}

	/**
	 * 上传方法方法
	 * 
	 * @param context
	 * @param application
	 * @param progressBar
	 * @param progressText
	 * @param stopButton
	 * @param savePath
	 */
	public void oneUpLoad(final Context context, BaseApplication application,
			final File file, final ProgressBar progressBar,
			final TextView progressText, final Button stopButton,
			String savePath) {
		this.context = context;

		DBManager dbManager = new DBManager(context);

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == UPLOAD_FAILL) {
					UIHelper.ToastMessage(context, R.string.upload_error);
				} else if (msg.what == UPLOAD_SUCCESS) {

					stopButton.setText(R.string.upload_success);

				} else if (msg.what == UPLOAD_UPDATE) {

					Long length = (Long) msg.obj;

					// 当前进度值
					int progress = (int) (((float) length / file.length()) * 100);

					progressBar.setProgress(progress);

					progressText.setText(FileUtil.formatFileSize(length) + "/"
							+ FileUtil.formatFileSize(file.length()));
				}
			}
		};

		runnable = new UploadThread(application, dbManager, handler, file,
				savePath);

		runnable.start();

	}

	public void setStart(boolean start) {
		this.start = start;
		runnable.setStart(start);
	}

	public boolean isStart() {
		return start;
	}
}
