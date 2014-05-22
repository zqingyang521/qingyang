package com.example.qingyangdemo.base;

import android.app.Fragment;
import android.os.Bundle;

/**
 * fragment 的基类
 * 
 * @author 赵庆洋
 * 
 */
public class BaseFragment extends Fragment {

	private BaseApplication myApplication;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		myApplication = (BaseApplication) getActivity().getApplication();
		super.onCreate(savedInstanceState);
	}

	public BaseApplication getMyApplication() {
		return myApplication;
	}
}
