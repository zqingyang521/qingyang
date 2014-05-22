package server;

import java.awt.Label;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import model.FileLog;

import thread.DownThread;
import thread.MouseThread;
import thread.ServerThread;
import thread.UpThread;

/**
 * 与客户端连接的查询server
 * 
 * @author 赵庆洋
 * 
 */
public class ConnectServer {

	private static ServerSocket serverSocket;

	private static ServerSocket downSocket;

	private static ServerSocket upSocket;

	private static ServerSocket mouseSocket;

	private static final int port = 8624;

	private static final int down_port = 8625;

	private static final int up_port = 8626;

	public final static int PC_MOUSE_CON = 8627;

	private Label label;

	// 存放断点数据，最好改为数据库存放
	private Map<Long, FileLog> datas = new HashMap<Long, FileLog>();

	public ConnectServer() throws IOException {
		serverSocket = new ServerSocket(port);
		downSocket = new ServerSocket(down_port);
		upSocket = new ServerSocket(up_port);
		mouseSocket = new ServerSocket(PC_MOUSE_CON);
		datas = new HashMap<Long, FileLog>();
		System.out.println("服务器连接启动.");
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	/**
	 * 开始服务
	 * 
	 * @throws IOException
	 */
	public void service() throws IOException {

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("查询服务器正在等待连接...");
					Socket socket;
					try {
						socket = serverSocket.accept();
						label.setText("The user connection is successful.");
						new Thread(new ServerThread(socket)).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("下载服务器正在等待连接...");
					Socket socket;
					try {
						socket = downSocket.accept();
						new DownThread(socket).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("上传服务器正在等待连接...");
					Socket socket;
					try {
						socket = upSocket.accept();
						new UpThread(socket, datas).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("鼠标控制服务器正在等待连接...");
					Socket socket;
					try {
						socket = mouseSocket.accept();
						new MouseThread(socket).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

}
