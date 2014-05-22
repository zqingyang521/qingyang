package com.example.qingyangdemo.ui;

import java.io.File;
import java.util.List;
import java.util.Stack;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.qingyangdemo.R;
import com.example.qingyangdemo.UploadActivity;
import com.example.qingyangdemo.adapter.LocalFileAdapter;
import com.example.qingyangdemo.base.AppManager;
import com.example.qingyangdemo.base.BaseActivity;
import com.example.qingyangdemo.common.FileUtil;
import com.example.qingyangdemo.common.UIHelper;

/**
 * 选择本地文件的视图
 * 
 * @author 赵庆洋
 * 
 */
public class LocalFileDialog extends BaseActivity implements OnClickListener {

	private ListView listView;

	private ImageButton returnBtn;

	private TextView titleView;

	private LocalFileAdapter adapter;

	private File root;

	private List<File> list;

	private LinearLayout layout;

	// path的堆栈
	private static Stack<String> pathStack;

	// 服务端要保存文件的路径
	private String savePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.local_file_dialog);

		setTitle("从SD卡选择文件上传");

		savePath = getIntent().getExtras().getString(UploadActivity.SAVE_PATH);

		String storageState = Environment.getExternalStorageState();

		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			root = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
		} else {
			finish();
		}

		initView();

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

		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				try {
					list = FileUtil.getFileListByPath(path);
				} catch (Exception e) {
					return false;
				}

				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {

					adapter.setFiles(list);

					adapter.setSelectedPosition(-1);

					adapter.notifyDataSetChanged();

				} else {
					UIHelper.ToastMessage(LocalFileDialog.this,
							R.string.msg_search_error);
				}
			}
		});
	}

	/**
	 * 初始化视图
	 */
	private void initView() {

		layout = (LinearLayout) findViewById(R.id.local_File_lin);

		layout.setLayoutParams(new FrameLayout.LayoutParams(screenWidth - 100,
				screenHeight - 200));

		listView = (ListView) findViewById(R.id.local_File_drawer);

		adapter = new LocalFileAdapter(this, list);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new DrawerItemClickListener());

		returnBtn = (ImageButton) findViewById(R.id.local_File_return_btn);

		returnBtn.setOnClickListener(this);

		titleView = (TextView) findViewById(R.id.local_File_title);

		searchData(root.getAbsolutePath());

		addPath(root.getAbsolutePath());

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

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			adapter.setSelectedPosition(position);

			selectItem(position);
		}
	}

	private void selectItem(int position) {

		String filePath = adapter.getFiles().get(position).getAbsolutePath();

		String fileName = adapter.getFiles().get(position).getName();

		if (adapter.getFiles().get(position).isDirectory()) {
			searchData(filePath);
			addPath(filePath);
		} else if (adapter.getFiles().get(position).isFile()) {

			Bundle args = new Bundle();

			args.putString(UploadActivity.FILE_NAME, fileName);

			args.putString(UploadActivity.FILE_PATH, filePath);

			args.putString(UploadActivity.SAVE_PATH, savePath);

			Intent intent = new Intent(LocalFileDialog.this,
					UploadActivity.class);

			intent.putExtras(args);

			startActivity(intent);

			finish();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.local_File_return_btn:
			if (getLastPath().equals(root.getAbsolutePath())) {
				return;
			}
			removeLastPath();

			searchData(getLastPath());

			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}
}
