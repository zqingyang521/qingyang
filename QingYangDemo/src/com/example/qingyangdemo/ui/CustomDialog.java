package com.example.qingyangdemo.ui;

import com.example.qingyangdemo.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 提示框
 * 
 * @author 赵庆洋
 * 
 */
public class CustomDialog {

	private View mParent;

	private PopupWindow popupWindow;

	private LinearLayout mRootLayout;

	private LayoutParams layoutParams;

	// popupwindow 必须有一个parentView,所以必须加这个参数
	public CustomDialog(Context context, View parent) {
		this.mParent = parent;

		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 加载布局文件
		mRootLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.custom_dialog, null);

		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				layoutParams.WRAP_CONTENT);
	}

	/**
	 * 设置dialog的标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		TextView mTitle = (TextView) mRootLayout
				.findViewById(R.id.custom_dialog_title);

		mTitle.setText(title);
	}

	/**
	 * 设置dialog的主体内容
	 * 
	 * @param title
	 */
	public void setMessage(String message) {
		TextView mMessage = (TextView) mRootLayout
				.findViewById(R.id.custom_dialog_content_text);

		mMessage.setText(message);
	}

	/**
	 * 设置dialog的确定按钮
	 */
	public void setPositiveButton(String text, OnClickListener onClickListener) {
		final Button buttonOk = (Button) mRootLayout
				.findViewById(R.id.custom_dialog_btn_ok);
		buttonOk.setText(text);
		buttonOk.setOnClickListener(onClickListener);
		buttonOk.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置dialog的取消按钮
	 */
	public void setNegativeButton(String text, OnClickListener onClickListener) {
		final Button buttonCancel = (Button) mRootLayout
				.findViewById(R.id.custom_dialog_btn_cancel);
		buttonCancel.setText(text);
		buttonCancel.setOnClickListener(onClickListener);
		buttonCancel.setVisibility(View.VISIBLE);
	}

	/**
	 * 替换dialog的主体布局
	 * 
	 * @param layout
	 */
	public void setContentLayout(View layout) {
		TextView message = (TextView) mRootLayout
				.findViewById(R.id.custom_dialog_content_text);
		message.setVisibility(View.GONE);
		LinearLayout contentLayout = (LinearLayout) mRootLayout
				.findViewById(R.id.custom_dialog_contentview);
		contentLayout.addView(layout);
	}

	/**
	 * 设置dialog的长宽
	 * 
	 * @param width
	 * @param height
	 */
	public void setLayoutParams(int width, int height) {
		layoutParams.width = width;
		layoutParams.height = height;
	}

	/**
	 * 显示dialog
	 */
	public void show() {
		if (popupWindow == null) {
			popupWindow = new PopupWindow(mRootLayout, layoutParams.width,
					layoutParams.height);
			popupWindow.setFocusable(true);
		}
		popupWindow.showAtLocation(mParent, Gravity.CENTER, Gravity.CENTER,
				Gravity.CENTER);
	}

	/**
	 * 取消dialog的显示
	 */
	public void dismiss() {
		if (popupWindow == null) {
			return;
		}
		popupWindow.dismiss();
	}
}
