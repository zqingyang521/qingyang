package com.example.qingyangdemo.thread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.net.URL;

/**
 * 控制pc鼠标的线程
 * 
 * @author 赵庆洋
 * 
 */
public class MouseThread extends Thread {

	private Socket socket;

	private DataOutputStream out;

	private BaseApplication application;

	private String msg;

	public MouseThread(BaseApplication application, String msg) {
		this.application = application;
		this.msg = msg;
	}

	@Override
	public void run() {
		try {
			socket = new Socket(application.getIpAdress(), URL.PC_MOUSE_CON);
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(msg);
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		super.run();
	}

}
