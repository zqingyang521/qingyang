package com.example.qingyangdemo.adapter;

import java.io.File;
import java.util.List;

import com.example.qingyangdemo.R;
import com.example.qingyangdemo.common.FileUtil;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 本地文件和文件夹的adapter
 * 
 * @author 赵庆洋
 * 
 */
public class LocalFileAdapter extends BaseAdapter {

	private List<File> files;

	private Context context;

	private int selectedPosition = -1;

	public LocalFileAdapter(Context context) {
		this.context = context;
	}

	public LocalFileAdapter(Context context, List<File> files) {
		this.context = context;
		this.files = files;
	}

	@Override
	public int getCount() {
		if (files == null) {
			return 0;
		}
		return files.size();
	}

	@Override
	public Object getItem(int position) {
		return files.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.local_file_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.local_file_text);
			viewHolder.textSize = (TextView) convertView
					.findViewById(R.id.local_file_text_size);
			viewHolder.fileIcon = (ImageView) convertView
					.findViewById(R.id.local_file_icon);
			viewHolder.fileImage = (ImageView) convertView
					.findViewById(R.id.local_file_image);
			viewHolder.linearLayout = (LinearLayout) convertView
					.findViewById(R.id.local_file_lin);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (selectedPosition == position) {
			viewHolder.textView.setSelected(true);
			viewHolder.linearLayout.setBackgroundColor(context.getResources()
					.getColor(R.color.skyblue));
		} else {
			viewHolder.textView.setSelected(false);
			viewHolder.linearLayout.setBackgroundColor(Color.TRANSPARENT);
		}

		viewHolder.textView.setText(files.get(position).getName());

		if (files.get(position).isDirectory()) {
			viewHolder.fileIcon.setImageResource(R.drawable.folder);
			viewHolder.fileImage.setImageResource(R.drawable.file_folder);
			viewHolder.textSize.setText("");
		} else {
			viewHolder.fileImage.setImageResource(R.drawable.file_upload);
			FileUtil.setImage(context, files.get(position).getName(),
					viewHolder.fileIcon);
			viewHolder.textSize.setText(FileUtil.formatFileSize(files.get(
					position).length()));
		}

		return convertView;
	}

	class ViewHolder {
		TextView textView;
		TextView textSize;
		ImageView fileIcon;
		ImageView fileImage;
		LinearLayout linearLayout;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
