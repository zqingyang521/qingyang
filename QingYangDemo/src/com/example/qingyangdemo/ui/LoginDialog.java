package com.example.qingyangdemo.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.example.qingyangdemo.R;
import com.example.qingyangdemo.base.AppException;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseActivity;
import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.bean.User;
import com.example.qingyangdemo.common.StringUtil;
import com.example.qingyangdemo.common.UIHelper;

/**
 * 用户登录对话框
 * 
 * @author 赵庆洋
 * 
 */
public class LoginDialog extends BaseActivity implements OnClickListener {

	private ViewSwitcher viewSwitcher;

	private ImageButton btn_close;

	private Button btn_login;

	private AutoCompleteTextView account;

	private EditText password;

	private View loginLoading;

	private CheckBox rememberMe;

	private InputMethodManager imm;

	private AnimationDrawable loadingAnimation;

	private BaseApplication application;

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_dialog);

		application = (BaseApplication) getApplication();

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		initView();
	}

	private void initView() {
		viewSwitcher = (ViewSwitcher) findViewById(R.id.logindialog_view_switcher);

		loginLoading = (View) findViewById(R.id.login_loading);

		account = (AutoCompleteTextView) findViewById(R.id.login_account);

		password = (EditText) findViewById(R.id.login_password);

		rememberMe = (CheckBox) findViewById(R.id.login_checkbox_rememberMe);

		btn_close = (ImageButton) findViewById(R.id.login_close_button);

		btn_login = (Button) findViewById(R.id.login_btn_login);

		btn_login.setOnClickListener(this);

		btn_close.setOnClickListener(this);

		// 是否显示登录信息
		BaseApplication application = (BaseApplication) getApplication();

		User user = application.getLoginInfo();

		if (user == null || !user.isRememberMe()) {
			return;
		}

		if (!StringUtil.isEmpty(user.getName())) {
			account.setText(user.getName());
			account.selectAll();
			rememberMe.setChecked(user.isRememberMe());
		}
		if (!StringUtil.isEmpty(user.getPassword())) {
			password.setText(user.getPassword());
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_close_button:
			finish();
			break;
		case R.id.login_btn_login:
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			String username = account.getText().toString();

			String pwd = password.getText().toString();

			boolean isRememberMe = rememberMe.isChecked();

			if (StringUtil.isEmpty(username)) {
				UIHelper.ToastMessage(v.getContext(),
						R.string.msg_login_email_null);
				return;
			}

			if (StringUtil.isEmpty(pwd)) {
				UIHelper.ToastMessage(v.getContext(),
						R.string.msg_login_pwd_null);
				return;
			}

			btn_close.setVisibility(View.GONE);

			loadingAnimation = (AnimationDrawable) loginLoading.getBackground();

			loadingAnimation.start();

			viewSwitcher.showNext();

			login(username, pwd, isRememberMe);

			break;
		}

	}

	// 登陆验证
	private void login(final String username, final String pwd,
			final boolean isRememberMe) {

		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					user = application.loginVerify(username, pwd);
				} catch (AppException e) {
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					if (!user.isErr()) {
						UIHelper.ToastMessage(LoginDialog.this,
								R.string.msg_login_success);
						user.setRememberMe(isRememberMe);
						application.saveLoginInfo(user);
						finish();
					} else {
						UIHelper.ToastMessage(LoginDialog.this, user.getMsg());
						viewSwitcher.showPrevious();
						btn_close.setVisibility(View.VISIBLE);
						application.cleanLoginInfo();
					}
				} else {
					viewSwitcher.showPrevious();
					btn_close.setVisibility(View.VISIBLE);
					UIHelper.ToastMessage(LoginDialog.this,
							R.string.msg_login_fail);
					application.cleanLoginInfo();
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
