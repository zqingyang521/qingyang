package com.example.qingyangdemo;

import java.io.File;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseActivity;
import com.example.qingyangdemo.common.UIHelper;
import com.example.qingyangdemo.common.UpLoadManager;

/**
 * 上传界面
 * 
 * @author 赵庆洋
 * 
 */
public class UploadActivity extends BaseActivity implements OnClickListener {

	public final static String FILE_NAME = "fileName";

	public final static String FILE_PATH = "filePath";

	public final static String SAVE_PATH = "savePath";

	private ImageButton returnBtn;

	private ProgressBar progressBar;

	private TextView fileNameText, progressText;

	// SD卡文件路径
	private String filePath;

	private String fileName;

	// 服务端存储的文件路径
	private String savePath;

	private Button stopButton;

	private File file;

	// 是否是完成上传任务
	private boolean isFinish = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_activity);

		fileName = getIntent().getExtras().getString(FILE_NAME);

		filePath = getIntent().getExtras().getString(FILE_PATH);

		savePath = getIntent().getExtras().getString(SAVE_PATH);

		file = new File(filePath);

		initView();

		upload();
	}

	/**
	 * 上传方法
	 */
	private void upload() {

		UpLoadManager.getUpManager().oneUpLoad(this, application, file,
				progressBar, progressText, stopButton, savePath);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {

		returnBtn = (ImageButton) findViewById(R.id.upload_return_btn);

		progressBar = (ProgressBar) findViewById(R.id.upload_progress);

		fileNameText = (TextView) findViewById(R.id.upload_filename);

		progressText = (TextView) findViewById(R.id.upload_progress_text);

		stopButton = (Button) findViewById(R.id.upload_stop);

		fileNameText.setText(fileName);

		returnBtn.setOnClickListener(UIHelper.finish(this));

		stopButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.upload_stop:
			if (stopButton.getText().toString()
					.equals(getString(R.string.upload_activity_btn_start))) {
				upload();
				stopButton
						.setText(getString(R.string.upload_activity_btn_stop));
			} else if (stopButton.getText().toString()
					.equals(getString(R.string.upload_activity_btn_stop))) {
				UpLoadManager.getUpManager().setStart(false);
				stopButton
						.setText(getString(R.string.upload_activity_btn_start));
			} else if (stopButton.getText().toString()
					.equals(getString(R.string.upload_success))) {
				finish();
				isFinish = true;
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		if (!isFinish) {
			UpLoadManager.getUpManager().setStart(false);
		}
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}
}
