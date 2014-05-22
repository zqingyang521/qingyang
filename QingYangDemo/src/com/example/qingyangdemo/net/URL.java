package com.example.qingyangdemo.net;

/**
 * 访问网络的地址
 * 
 * @author 赵庆洋
 * 
 */
public class URL {

	// 网络
	public final static String HOST = "http://www.fs365.com.cn";

	// public final static String HOST = "http://10.0.0.113:9006";

	public final static String URL_SPLITTER = "/";

	public final static String URL_HOST = HOST + URL_SPLITTER;

	public final static String UPDATE_VERSOIN = URL_HOST + "";

	public final static String LOGIN_URL = URL_HOST + "fortel/login";

	public final static int PC_PORT = 8624;

	public final static int PC_DOWN_PORT = 8625;

	public final static int PC_UP_PORT = 8626;

	// 鼠标控制端口
	public final static int PC_MOUSE_CON = 8627;

	// 获取盘符
	public static final String GET_DISK = "getDisk";

	// 关闭计算机
	public static final String POWER_PC = "powerPc";

	// 鼠标左键
	public static final String MOUSE_LEFT = "mouseLeft";

	// 鼠标右键
	public static final String MOUSE_RIGHT = "mouseRight";

}
