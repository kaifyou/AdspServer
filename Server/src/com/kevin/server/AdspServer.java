package com.kevin.server;

import java.awt.EventQueue;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;

import com.motorola.adsp.lbs.api.ADSPServer;
import com.motorola.adsp.lbs.api.ConnectionException;
import com.motorola.adsp.lbs.api.Filter;
import com.motorola.adsp.lbs.api.LBSClient;
import com.motorola.adsp.lbs.api.LbsStreamListener;
import com.motorola.adsp.lbs.api.Location;
import com.motorola.adsp.lbs.api.LocationFilter;
import com.motorola.adsp.lbs.api.PresenceEvent;
import com.motorola.adsp.lbs.api.PresenceFilter;
import com.motorola.adsp.lbs.api.ProxySettings;
import com.motorola.adsp.lbs.api.RegionEvent;
import com.motorola.adsp.lbs.api.RegionEvent.RegionData;
import com.motorola.adsp.lbs.api.RegionFilter;
import com.motorola.adsp.lbs.api.RssiData;
import com.motorola.adsp.lbs.api.SensorData;
import com.motorola.adsp.lbs.api.ServerRequestException;
import com.motorola.adsp.lbs.api.StreamException;
import com.motorola.adsp.lbs.api.StreamType;

public class AdspServer {

	// 服务器端口
	private static final int SERVERPORT = 8882;
	// 客户端连接
	public static List<Socket> mClientList;
	// 线程池
	private ExecutorService mExecutorService;
	// ServerSocket对象
	private ServerSocket mServerSocket;

	public static boolean isConnClient = false;

	public AdspServer() {
		mClientList = new ArrayList<Socket>();
	}

	public void startServer() {
		System.out.println("adsp server run ......");

		try {
			// 设置服务器端口
			mServerSocket = new ServerSocket(SERVERPORT);
			// 创建一个线程池
			mExecutorService = Executors.newCachedThreadPool();
			mServerSocket.setReuseAddress(true);

			while (true) {
				// 用来临时保存客户端连接的Socket对象
				Socket client = null;
				// 接收客户连接并添加到list中
				client = mServerSocket.accept();
				if (client != null) {
					mClientList.add(client);
					// 开启一个客户端线程
					ForClientThread mForClientThread = new ForClientThread(
							client);
					mExecutorService.execute(mForClientThread);
					isConnClient = true;
				}
				if (mServerSocket.isClosed()) {
					mServerSocket = null;
					mServerSocket = new ServerSocket(SERVERPORT);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				mServerSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();

		}
	}

	public static void main(String[] args) {
		System.out.println("start...");

		AdspUtilThread mAdspUtil = new AdspUtilThread();
		mAdspUtil.start();

		AdspServer mAdspServer = new AdspServer();
		mAdspServer.startServer();
	}
}
