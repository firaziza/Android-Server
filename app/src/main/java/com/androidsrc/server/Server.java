package com.androidsrc.server;

import android.icu.text.SymbolTable;
import android.os.HandlerThread;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImplFactory;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.lang.Thread;



public class Server {


	MainActivity activity;
	ServerSocket serverSocket;

	String message = "";
	static final int socketServerPORT = 8080;

	public Server(MainActivity activity) {
		this.activity = activity;
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();

	}


	public int getPort() {
		return socketServerPORT;
	}

	public void onDestroy() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}



	}



	private class SocketServerThread extends Thread {

		int count = 0;

		public void run() { //method to run the program

			try {

				serverSocket = new ServerSocket(socketServerPORT);

				while (true) {
					Socket socket = serverSocket.accept();
					count++;

					{
						try {
							message += "#" + "Hi" + ":" + count + "\n";
						} catch (Exception e) {
							System.out.print("");
						}

					}
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							activity.msg.setText(message);
						}

					});

					SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
							socket, count);

					socketServerReplyThread.run();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		int cnt;

		SocketServerReplyThread(Socket socket, int c) {
			hostThreadSocket = socket;
			cnt = c;
		}

		@Override

		public void run() {
			OutputStream outputStream;
			String msgReply = "HellO !!" + cnt + "\n";

			try {

				outputStream = hostThreadSocket.getOutputStream();

				PrintStream printStream = new PrintStream(outputStream);

				printStream.print(msgReply);

				printStream.close();

				message += "Reply: " + msgReply + "\n";

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						activity.msg.setText(message);

					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong!" + e.toString() + "\n";
			}



			activity.runOnUiThread(new Runnable() {

				@Override

				public void run() {
					activity.msg.setText(message);

				}
			});
		}

	}

	public String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress
							.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "phone running at : "
								+ inetAddress.getHostAddress();

					}
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}
		return ip;
	}


}
