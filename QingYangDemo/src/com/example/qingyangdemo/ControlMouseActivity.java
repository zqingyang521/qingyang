package com.example.qingyangdemo;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.qingyangdemo.R;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseActivity;
import com.example.qingyangdemo.common.UIHelper;
import com.example.qingyangdemo.net.URL;
import com.example.qingyangdemo.thread.MouseThread;

/**
 * 控制pc端鼠标
 * 
 * @author 赵庆洋
 * 
 */
public class ControlMouseActivity extends BaseActivity implements
		OnTouchListener, OnClickListener {

	private ImageButton returnBtn;

	private TextView mouseControl;

	private Button leftBtn;

	private Button rightBtn;

	// 初始值的x，y坐标
	private float initX, initY;

	// 移动后的x，y坐标
	private float disX, disY;

	// 抬起的x，y坐标
	private float upX, upY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_mouse_activity);

		initView();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {

		returnBtn = (ImageButton) findViewById(R.id.mouse_return_btn);

		returnBtn.setOnClickListener(UIHelper.finish(this));

		mouseControl = (TextView) findViewById(R.id.mouse_control);

		mouseControl.setOnTouchListener(this);

		leftBtn = (Button) findViewById(R.id.mouse_left_btn);

		rightBtn = (Button) findViewById(R.id.mouse_right_btn);

		leftBtn.setOnClickListener(this);

		rightBtn.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		// 按下动作
		case MotionEvent.ACTION_DOWN:
			initX = event.getX();
			initY = event.getY();

			// 抬起记录按下的x,y
			upX = event.getX();
			upY = event.getY();
			break;
		// 移动动作
		case MotionEvent.ACTION_MOVE:
			disX = event.getX() - initX;
			disY = event.getY() - initY;

			// 如果移动了
			if (disX != 0 || disY != 0) {
				String msg = "<" + disX + "," + disY + ">";

				new MouseThread(application, msg).start();
			}
			initX = event.getX();
			initY = event.getY();
			break;
		// 抬起动作
		case MotionEvent.ACTION_UP:

			// 如果没有移动过
			if ((event.getX() - upX) == 0 && (event.getY() - upY) == 0) {

				new MouseThread(application, URL.MOUSE_LEFT).start();
			}

			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 鼠标左键
		case R.id.mouse_left_btn:
			new MouseThread(application, URL.MOUSE_LEFT).start();
			break;

		// 鼠标右键
		case R.id.mouse_right_btn:
			new MouseThread(application, URL.MOUSE_RIGHT).start();
			break;
		}

	}

}
