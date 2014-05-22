package main;

import java.awt.Color;

public class Main {

	public static void main(String[] args) {

		ServerWindow window = new ServerWindow("清扬服务端");
		window.setSize(300, 100);
		window.setBackground(Color.white);
		window.setVisible(true);

		// try {
		// ConnectServer connectServer = new ConnectServer();
		// connectServer.service();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
	}
}
