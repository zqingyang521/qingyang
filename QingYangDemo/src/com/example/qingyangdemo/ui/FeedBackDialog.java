package com.example.qingyangdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.qingyangdemo.R;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseActivity;
import com.example.qingyangdemo.common.UIHelper;

/**
 * 意见反馈界面
 * 
 * @author 赵庆洋
 * 
 */
public class FeedBackDialog extends BaseActivity {

	private ImageButton closeButton;

	private EditText contentText;

	private Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_dialog);

		initView();
	}

	private void initView() {
		closeButton = (ImageButton) findViewById(R.id.feedback_close_button);

		contentText = (EditText) findViewById(R.id.feedback_content);

		submitButton = (Button) findViewById(R.id.feedback_submit);

		closeButton.setOnClickListener(UIHelper.finish(this));

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String content = contentText.getText().toString();

				// 发送反馈报告
				Intent intent = new Intent(Intent.ACTION_SEND);
				// intent.setType("text/plain");// 模拟器
				intent.setType("message/rfc822");// 真机
				// 发送邮件 Intent.EXTRA_CC抄送 Intent.EXTRA_BCC密送者
				intent.putExtra(Intent.EXTRA_EMAIL,
						new String[] { "736909686@qq.com" });
				// 标题
				intent.putExtra(Intent.EXTRA_SUBJECT, "清扬android客户端-用户意见反馈");
				// 内容
				intent.putExtra(Intent.EXTRA_TEXT, content);

				startActivity(Intent.createChooser(intent, "发送用户意见反馈"));

				finish();

			}
		});
	}

	@Override
	protected void onDestroy() {
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}

}
