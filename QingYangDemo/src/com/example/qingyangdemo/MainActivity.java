package com.example.qingyangdemo;

import java.util.List;

import com.example.qingyangdemo.adapter.DiskAdapter;
import com.example.qingyangdemo.base.AppException;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseActivity;
import com.example.qingyangdemo.bean.Disk;
import com.example.qingyangdemo.common.UIHelper;
import com.example.qingyangdemo.common.UpdateManager;
import com.example.qingyangdemo.fragment.CenterFragment;
import com.example.qingyangdemo.net.SocketClient;
import com.example.qingyangdemo.ui.CustomDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends BaseActivity implements OnClickListener {

	private onKeyDownListener keyDownListener;

	// 抽屉布局
	private DrawerLayout mDrawerLayout;

	// 抽屉list
	private ListView mDrawerList;

	private LinearLayout left_drawer_LinearLayout;

	private LinearLayout open_llt;

	// 菜单按钮
	private ImageButton fragment_menu;

	private DiskAdapter diskAdapter;

	private String title[] = { "用户登陆", "注销登陆", "系统设置", "控制鼠标", "电脑关机", "退出程序" };

	private PopupWindow popupWindow;

	private View view;

	private List<Disk> diskList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (application.isTablet()) {
			view = View.inflate(this, R.layout.main_activity_tablet, null);
		} else {
			view = View.inflate(this, R.layout.main_activity, null);
		}

		setContentView(view);

		// 网络连接判断
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(this, R.string.network_not_connected);
			finish();
		} else {
			if (application.isCheckUp()) {
				UpdateManager.getUpdateManager().checkAppUpdate(this, false);
			}
		}

		application.initLoginInfo();

		if (!application.isTablet()) {
			initPortpaitView();
		}

		initView();

		searchViewData();

	}

	/**
	 * 查询view的数据
	 */
	public void searchViewData() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				try {
					diskList = SocketClient.getDiskInfo(application);
				} catch (AppException e) {
					return false;
				}

				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {

					diskAdapter.setList(diskList);

					diskAdapter.notifyDataSetChanged();

					if (diskAdapter.getSelectedPosition() == -1) {
						diskAdapter.setSelectedPosition(0);
						selectItem(0);
					}

				} else {
					UIHelper.ToastMessage(MainActivity.this,
							R.string.msg_search_error);
				}
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// 横竖屏拦截，我测试总是不好使。不拦截了 竖屏就一直竖屏， 横屏就一直横屏
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (application.isLandscape()) {
			Log.v(AppException.LOG_TAG, "现在是横屏状态");
		} else {
			Log.v(AppException.LOG_TAG, "现在是竖屏状态");
		}
	}

	/**
	 * 初始化视图
	 * 
	 * @param view
	 */
	public void initView() {
		fragment_menu = (ImageButton) findViewById(R.id.fragment_menu);

		fragment_menu.setOnClickListener(this);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		diskAdapter = new DiskAdapter(this);

		mDrawerList.setAdapter(diskAdapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	}

	/**
	 * 初始化竖向视图
	 * 
	 * @param view
	 * @return
	 */
	public View initPortpaitView() {

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// 遮盖主要内容的阴影
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		left_drawer_LinearLayout = (LinearLayout) view
				.findViewById(R.id.left_drawer_LinearLayout);

		open_llt = (LinearLayout) findViewById(R.id.open_llt);

		open_llt.setOnClickListener(this);

		mDrawerLayout.setDrawerListener(new MyDrawerListener());

		return view;
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			diskAdapter.setSelectedPosition(position);
			selectItem(position);

		}
	}

	private void selectItem(int position) {

		Bundle args = new Bundle();

		args.putString(CenterFragment.DISK_PATH,
				diskAdapter.getList().get(position).getPath());

		Fragment fragment = new CenterFragment();

		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();

		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		mDrawerList.setItemChecked(position, true);

		if (!application.isTablet()) {

			mDrawerLayout.closeDrawer(left_drawer_LinearLayout);
		}
	}

	/**
	 * 抽屉监听
	 */
	private class MyDrawerListener implements DrawerListener {

		@Override
		public void onDrawerClosed(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDrawerOpened(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDrawerSlide(View arg0, float arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDrawerStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// 打开/关闭抽屉
		case R.id.open_llt:
			if (mDrawerLayout.isDrawerOpen(left_drawer_LinearLayout)) {
				mDrawerLayout.closeDrawer(left_drawer_LinearLayout);
			} else {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
			break;

		// 菜单按钮
		case R.id.fragment_menu:
			int x = 0;
			int y = 0;

			if (!application.isTablet()) {
				x = screenWidth - v.getRight() + 20;
				y = v.getBottom() + 60;
			} else {
				x = screenWidth - v.getRight() + 10;
				y = v.getBottom() + 10;
			}

			showPopMenu(x, y);
			break;
		}
	}

	/**
	 * 显示菜单
	 * 
	 * @param x
	 * @param y
	 */
	public void showPopMenu(int x, int y) {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.pop_menu, null);
		ListView listView = (ListView) layout.findViewById(R.id.menu_list);
		listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				R.layout.pop_menu_item, R.id.tv_text, title));

		popupWindow = new PopupWindow(MainActivity.this);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		if (application.isTablet()) {
			popupWindow.setWidth(screenWidth / 4);
		} else {
			popupWindow.setWidth(screenWidth / 3);
		}
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		// 需要指定Gravity，默认情况是center
		popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, x, y);
		listView.setOnItemClickListener(new MenuItemClick());
	}

	/**
	 * 菜单每项点击事件
	 * 
	 * @author R21
	 * 
	 */
	private class MenuItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				UIHelper.showLoginDialog(MainActivity.this);
				break;
			case 1:
				final CustomDialog dialog = new CustomDialog(MainActivity.this,
						getCurrentFocus());

				dialog.setTitle("提示");
				dialog.setMessage("确定注销登录?");
				dialog.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
				break;
			case 2:
				startActivity(SettingActivity.class);
				break;
			case 3:
				startActivity(ControlMouseActivity.class);
				break;
			case 4:
				startActivity(PowerOffActivity.class);
				break;
			case 5:
				final CustomDialog outdialog = new CustomDialog(
						MainActivity.this, getCurrentFocus());

				outdialog.setTitle("提示");
				outdialog.setMessage("确定退出?");
				outdialog.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						outdialog.dismiss();
						AppManager.getAppManager().AppExit(MainActivity.this);
					}
				});
				outdialog.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
						outdialog.dismiss();
					}
				});
				outdialog.show();
				break;
			}

			popupWindow.dismiss();

			popupWindow = null;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			keyDownListener.fragmentKeyDown(keyCode, event);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {

		keyDownListener = (onKeyDownListener) fragment;
		super.onAttachFragment(fragment);
	}

	/**
	 * fragment keyDown回调
	 * 
	 * @author 赵庆洋
	 * 
	 */
	public interface onKeyDownListener {
		public void fragmentKeyDown(int keyCode, KeyEvent event);
	}

}
