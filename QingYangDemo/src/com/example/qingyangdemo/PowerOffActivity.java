package com.example.qingyangdemo;

import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qingyangdemo.R;
import com.example.qingyangdemo.base.AppException;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseActivity;
import com.example.qingyangdemo.common.UIHelper;
import com.example.qingyangdemo.net.SocketClient;

/**
 * 控制pc端关机
 * 
 * @author 赵庆洋
 * 
 */
public class PowerOffActivity extends BaseActivity {

	private ImageButton returnBtn;

	private TextView powerText;

	private ImageView powerImage;

	// 是否关机
	private boolean isPowerOff = true;

	// 倒计时的秒数
	private int recLen = 6;

	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pc_power_off_activity);
		initView();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {

		powerText = (TextView) findViewById(R.id.power_text);

		powerImage = (ImageView) findViewById(R.id.power_image);

		returnBtn = (ImageButton) findViewById(R.id.power_return_btn);

		returnBtn.setOnClickListener(UIHelper.finish(this));

		powerImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPowerOff) {
					powerImage.setImageResource(R.drawable.pc_power_off_canel);

					isPowerOff = false;

					timer = new Timer();

					timer.schedule(new TimerTask() {
						@Override
						public void run() {

							runOnUiThread(new Runnable() { // UI thread
								@Override
								public void run() {
									recLen--;
									powerText.setText(recLen
											+ "秒后将关闭电脑! 取消请再次点击按钮!");
									if (recLen == 0) {
										timer.cancel();

										timer = null;

										powerOffPc();
									}
								}
							});
						}
					}, 0, 1000);
				} else {
					powerImage.setImageResource(R.drawable.pc_power_off);

					isPowerOff = true;

					powerText.setText("");

					timer.cancel();

					timer = null;

					recLen = 6;
				}
			}
		});

	}

	/**
	 * 关闭计算机
	 */
	public void powerOffPc() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				Boolean isClose = false;
				try {
					isClose = SocketClient.PowerOffPc(application);

				} catch (AppException e) {
					return false;
				}

				return isClose;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {
					AppManager.getAppManager().AppExit(PowerOffActivity.this);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}

}
