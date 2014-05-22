package com.example.qingyangdemo.fragment;

import java.util.List;
import java.util.Stack;

import com.example.qingyangdemo.DownLoadActivity;
import com.example.qingyangdemo.MainActivity;
import com.example.qingyangdemo.R;
import com.example.qingyangdemo.UploadActivity;
import com.example.qingyangdemo.MainActivity.onKeyDownListener;
import com.example.qingyangdemo.adapter.FileAdapter;
import com.example.qingyangdemo.base.AppException;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseFragment;
import com.example.qingyangdemo.bean.PcFile;
import com.example.qingyangdemo.common.UIHelper;
import com.example.qingyangdemo.common.UpLoadManager;
import com.example.qingyangdemo.common.UpdateManager;
import com.example.qingyangdemo.net.SocketClient;
import com.example.qingyangdemo.ui.CustomDialog;
import com.example.qingyangdemo.ui.LocalFileDialog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 中间的布局
 * 
 * @author 赵庆洋
 * 
 */
public class CenterFragment extends BaseFragment implements OnClickListener,
		onKeyDownListener {

	public static final String DISK_PATH = "disk_path";

	private ListView listView;

	private ImageButton returnBtn, upBtn;

	private TextView titleView;

	private FileAdapter fileAdapter;

	private List<PcFile> fileList;

	private String diskPath;

	private MainActivity activity;

	// path的堆栈
	private static Stack<String> pathStack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		diskPath = getArguments().getString(DISK_PATH);
		activity = (MainActivity) getActivity();
		addPath(diskPath);
	}

	/**
	 * 添加路径到堆栈
	 * 
	 * @param path
	 */
	public void addPath(String path) {

		if (pathStack == null) {
			pathStack = new Stack<String>();
		}

		pathStack.add(path);
	}

	/**
	 * 获取堆栈最上层的路径
	 * 
	 * @return
	 */
	public String getLastPath() {
		return pathStack.lastElement();
	}

	/**
	 * 移除堆栈最上层路径
	 */
	public void removeLastPath() {
		pathStack.remove(getLastPath());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.center_fragment, container,
				false);

		listView = (ListView) rootView.findViewById(R.id.center_drawer);

		fileAdapter = new FileAdapter(getActivity());

		listView.setAdapter(fileAdapter);

		listView.setOnItemClickListener(new DrawerItemClickListener());

		returnBtn = (ImageButton) rootView.findViewById(R.id.center_return_btn);

		upBtn = (ImageButton) rootView.findViewById(R.id.center_up_btn);

		returnBtn.setOnClickListener(this);

		upBtn.setOnClickListener(this);

		titleView = (TextView) rootView.findViewById(R.id.center_title);

		titleView.setText(diskPath);

		searchViewData(diskPath);

		return rootView;
	}

	/**
	 * 查询调用方法
	 */
	public void searchData(String path) {
		searchViewData(path);
		titleView.setText(path);
	}

	/**
	 * 查询view的数据
	 */
	public void searchViewData(final String path) {

		activity.putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				try {
					fileList = SocketClient.getFileInfo(getMyApplication(),
							path);
				} catch (AppException e) {
					return false;
				}

				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {

					fileAdapter.setList(fileList);

					fileAdapter.setSelectedPosition(-1);

					fileAdapter.notifyDataSetChanged();

				} else {
					UIHelper.ToastMessage(activity, R.string.msg_search_error);
				}
			}
		});
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			fileAdapter.setSelectedPosition(position);

			selectItem(position);
		}
	}

	private void selectItem(int position) {

		String filePath = fileAdapter.getList().get(position).getFilePath();

		String fileName = fileAdapter.getList().get(position).getFileName();

		if (fileAdapter.getList().get(position).isDirectory()) {
			searchData(filePath);
			addPath(filePath);
		} else if (fileAdapter.getList().get(position).isFile()) {

			Bundle args = new Bundle();

			args.putString(DownLoadActivity.FILE_NAME, fileName);

			args.putString(DownLoadActivity.FILE_PATH, filePath);

			Intent intent = new Intent(getActivity(), DownLoadActivity.class);

			intent.putExtras(args);

			getActivity().startActivity(intent);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.center_return_btn:
			if (getLastPath().equals(diskPath)) {
				return;
			}
			removeLastPath();

			searchData(getLastPath());

			break;

		case R.id.center_up_btn:
			Intent intent = new Intent(activity, LocalFileDialog.class);
			intent.putExtra(UploadActivity.SAVE_PATH, getLastPath());
			activity.startActivity(intent);
			break;
		}
	}

	@Override
	public void fragmentKeyDown(int keyCode, KeyEvent event) {

		if (getLastPath().equals(diskPath)) {
			final CustomDialog outdialog = new CustomDialog(activity,
					activity.getCurrentFocus());

			outdialog.setTitle("提示");
			outdialog.setMessage("确定退出?");
			outdialog.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View v) {
					outdialog.dismiss();
					AppManager.getAppManager().AppExit(activity);
				}
			});
			outdialog.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(View v) {
					outdialog.dismiss();
				}
			});
			outdialog.show();
			return;
		}
		removeLastPath();

		searchData(getLastPath());
	}

}
