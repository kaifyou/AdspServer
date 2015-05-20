package com.kevin.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.motorola.adsp.lbs.api.Location;

//ÿ���ͻ��˵�������һ�������߳�
public class ForClientThread implements Runnable {

	private static Socket mSocket;
	private BufferedReader mBufferedReader;
	private static PrintWriter mPrintWriter;
	private String mStrMsg;
	public static String macStr;
	public static String acountStr;

	private static Date clientDate;
	private static String clientTime;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss");

	private static FileOutputStream fileOutputStream;
	private static String acount = null;
	
	public ForClientThread(Socket socket) {
		File file = new File("D:/Tools/project/log/send.txt");
		try {
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			fileOutputStream = new FileOutputStream(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.mSocket = socket;
		try {
			// ���ܿͻ��˷��͹���������
			mBufferedReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// �������ݵ��ͻ���
			mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// // mStrMsg = "user:" + this.mSocket.getInetAddress() +
		// "client total:"
		// + mClientList.size();
		// // sendMessage(mStrMsg);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("for client thread run");

		// try {
		// // ��ȡ�ͻ��˷��͹���������
		// while (mSocket.isConnected()) {
		//
		// if ((mStrMsg = mBufferedReader.readLine()) != null && mStrMsg != "")
		// {
		//
		// // if (mStrMsg.contains("user:")){
		// // acount = mStrMsg.substring(mStrMsg.indexOf(":")+1);
		// // }
		//
		// if (mStrMsg.length() > 25) {
		// acountStr = mStrMsg;
		// System.out.println("==========acountStr:"+acountStr);
		//
		// acount =
		// acountStr.substring(acountStr.indexOf("=")+1,acountStr.indexOf(" "));
		// File file = new File("D:/Tools/project/log/"+acount+".txt");
		// if(!file.exists()){
		// file.createNewFile();
		// }
		//
		// } else if (mStrMsg.length() > 10) {
		// macStr = mStrMsg;
		// System.out.println(macStr);
		// }
		// }
		//
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	// ��ͻ��˷�������
	public static void sendMessage(String msgStr) {
		// System.out.println(msgStr);
		// ���������е�һ���ͻ��˷�������
		if (mSocket.isConnected()) {
			mPrintWriter.println(msgStr);
		//	mPrintWriter.write(msgStr);
		
			try {
				clientDate = new Date(System.currentTimeMillis());
				clientTime = dateFormat.format(clientDate);
				String tempString = "time=" + clientTime + msgStr;
				
				fileOutputStream.write(tempString.getBytes());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			for (Socket clientSocket : AdspServer.mClientList) {
				if (clientSocket.equals(mSocket)) {
					try {
						mSocket.close();
						AdspServer.mClientList.remove(mSocket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		// �����пͻ���������
		// for(Socket client : mClientList){
		// try {
		// mPrintWriter = new PrintWriter(client.getOutputStream(), true);
		// mPrintWriter.println(msgStr);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}
	
	//hsl update 2015-04-23
	public static void getLoactionInfo(final String mac,final String string){
		if(macStr != null && mac.equals(macStr)){
//		if(macStr != null){
//			System.out.println("=====macStr:"+macStr);
//			System.out.println("=====mac:"+mac);
//			System.out.println("========acount:"+acount);
//		}
			File file = new File("D:/Tools/project/log/"+acount+".txt");
			if(file.exists()){
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(file,true);
					String tempStr = "MAC: "+ mac + " " + string + "\r\n";
					fileOutputStream.write(tempStr.getBytes());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IOException e){
					
				}
				
			}
		}
	}
}
