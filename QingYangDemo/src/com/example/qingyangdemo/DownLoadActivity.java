package com.example.qingyangdemo;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseActivity;
import com.example.qingyangdemo.common.DownLoadManager;
import com.example.qingyangdemo.common.UIHelper;

/**
 * 下载页面
 * 
 * @author 赵庆洋
 * 
 */
public class DownLoadActivity extends BaseActivity {

	public final static String FILE_NAME = "fileName";

	public final static String FILE_PATH = "filePath";

	private ImageButton returnBtn;

	private ProgressBar progressBar;

	private TextView fileNameText, progressText;

	private String filePath;

	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_activity);

		fileName = getIntent().getExtras().getString(FILE_NAME);

		filePath = getIntent().getExtras().getString(FILE_PATH);

		initView();

		downLoad();
	}

	/**
	 * 下载方法
	 */
	private void downLoad() {
		DownLoadManager.getDownManager().oneDownLoad(this, application,
				filePath, fileName, progressBar, progressText);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {

		returnBtn = (ImageButton) findViewById(R.id.download_return_btn);

		progressBar = (ProgressBar) findViewById(R.id.download_progress);

		fileNameText = (TextView) findViewById(R.id.download_filename);

		progressText = (TextView) findViewById(R.id.download_progress_text);

		fileNameText.setText(fileName);

		returnBtn.setOnClickListener(UIHelper.finish(this));

	}

	@Override
	protected void onDestroy() {
		DownLoadManager.getDownManager().stopDownTread();
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}
}
