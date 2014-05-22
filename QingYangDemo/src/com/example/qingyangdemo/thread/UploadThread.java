package com.example.qingyangdemo.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.qingyangdemo.base.AppException;
import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.bean.Upload;
import com.example.qingyangdemo.common.DBManager;
import com.example.qingyangdemo.common.StringUtil;
import com.example.qingyangdemo.common.UpLoadManager;
import com.example.qingyangdemo.net.URL;

/**
 * 上传的线程
 * 
 * @author 赵庆洋
 * 
 */
public class UploadThread extends Thread {

	private Socket socket;

	private File file;

	public final static int BUFFER = 1024;

	private DBManager dbManager;

	private BaseApplication application;

	private boolean start = true;

	private Handler handler;

	private String savePath;

	public UploadThread(BaseApplication application, DBManager dbManager,
			Handler handler, File file, String savePath) {
		this.application = application;
		this.dbManager = dbManager;
		this.file = file;
		this.handler = handler;
		this.savePath = savePath;
	}

	/*
	 * 服务器提出上传请求
	 */
	private String request(String sourceid) throws IOException {

		DataInputStream in = new DataInputStream(socket.getInputStream());

		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		String params = "length=" + file.length() + ";filename="
				+ file.getName() + ";sourceid=" + sourceid + ";filePath="
				+ savePath;
		;

		// 发出上传请求
		out.writeUTF(params);

		out.flush();

		// 返回文件字符
		return in.readUTF();

	}

	@Override
	public void run() {
		try {
			String sourceid = dbManager.getBindId(file.getAbsolutePath());
			socket = new Socket(application.getIpAdress(), URL.PC_UP_PORT);
			String response = request(sourceid);
			String[] items = response.split(";");

			// 服务端断点记录标示符
			String responseid = items[0].substring(items[0].indexOf("=") + 1);

			// 服务端断点位置
			String position = items[1].substring(items[1].indexOf("=") + 1);

			// 没上传过此文件，添加到数据库
			if (StringUtil.isEmpty(sourceid)) {

				Upload upload = new Upload();

				upload.setSourceid(responseid);

				upload.setUploadfilepath(file.getAbsolutePath());

				dbManager.saveUpload(upload);
			}

			RandomAccessFile fileOutStream = new RandomAccessFile(file, "r");

			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());

			// 移动到断点处继续读取
			fileOutStream.seek(Long.valueOf(position));

			byte[] buffer = new byte[BUFFER];

			int len = -1;

			long length = Long.valueOf(position);

			while (start && (len = fileOutStream.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				out.flush();
				length += len;
				Message msg = new Message();
				msg.what = UpLoadManager.UPLOAD_UPDATE;
				msg.obj = length;
				handler.sendMessage(msg);
			}

			fileOutStream.close();

			out.close();

			if (file.length() == length) {
				dbManager.delUpload(file.getAbsolutePath());
				Log.v(AppException.LOG_TAG, "上传完毕");
				handler.sendEmptyMessage(UpLoadManager.UPLOAD_SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.v(AppException.LOG_TAG, "上传出错");
			handler.sendEmptyMessage(UpLoadManager.UPLOAD_FAILL);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}
}
