package com.example.qingyangdemo.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.qingyangdemo.MainActivity;
import com.example.qingyangdemo.R;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseActivity;
import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.common.StringUtil;
import com.example.qingyangdemo.common.UIHelper;

/**
 * 设置Ip地址界面
 * 
 * @author 赵庆洋
 * 
 */
public class IpSetDialog extends BaseActivity {

	public final static String IS_STRAT_MAIN = "isStartMain";

	private ImageButton closeButton;

	private EditText edit1, edit2, edit3, edit4;

	private Button submitButton;

	private BaseApplication application;

	private boolean isStartMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ipset_dialog);

		application = (BaseApplication) getApplication();

		isStartMain = getIntent().getExtras().getBoolean(IS_STRAT_MAIN);

		initView();
	}

	private void initView() {
		closeButton = (ImageButton) findViewById(R.id.ipset_close_button);

		edit1 = (EditText) findViewById(R.id.local_gw_edit_1);

		edit2 = (EditText) findViewById(R.id.local_gw_edit_2);

		edit3 = (EditText) findViewById(R.id.local_gw_edit_3);

		edit4 = (EditText) findViewById(R.id.local_gw_edit_4);

		submitButton = (Button) findViewById(R.id.ipset_submit);

		closeButton.setOnClickListener(UIHelper.finish(this));

		String[] ip = application.getIpAdress().toString().replace(".", ",")
				.split(",");

		edit1.setText(ip[0]);

		edit2.setText(ip[1]);

		edit3.setText(ip[2]);

		edit4.setText(ip[3]);

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (StringUtil.isEmpty(edit1.getText().toString())
						|| StringUtil.isEmpty(edit2.getText().toString())
						|| StringUtil.isEmpty(edit3.getText().toString())
						|| StringUtil.isEmpty(edit4.getText().toString())) {
					UIHelper.ToastMessage(IpSetDialog.this,
							R.string.ip_set_error);
				}

				String ipadress = edit1.getText().toString() + "."
						+ edit2.getText().toString() + "."
						+ edit3.getText().toString() + "."
						+ edit4.getText().toString();

				application.setIpAdress(ipadress);

				if (isStartMain) {
					startActivity(MainActivity.class);
				}

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
