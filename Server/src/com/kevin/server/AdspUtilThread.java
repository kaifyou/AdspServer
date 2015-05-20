package com.kevin.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.text.html.HTML.Tag;

import org.apache.commons.logging.Log;

import com.motorola.adsp.lbs.api.ADSPServer;
import com.motorola.adsp.lbs.api.Bounds;
import com.motorola.adsp.lbs.api.ConnectionException;
import com.motorola.adsp.lbs.api.FloorPlan;
import com.motorola.adsp.lbs.api.FloorPlanRequest;
import com.motorola.adsp.lbs.api.LBSClient;
import com.motorola.adsp.lbs.api.LbsStreamListener;
import com.motorola.adsp.lbs.api.Location;
import com.motorola.adsp.lbs.api.LocationFilter;
import com.motorola.adsp.lbs.api.NetworkPath;
import com.motorola.adsp.lbs.api.PresenceEvent;
import com.motorola.adsp.lbs.api.ProxySettings;
import com.motorola.adsp.lbs.api.RegionEvent;
import com.motorola.adsp.lbs.api.RegionFilter;
import com.motorola.adsp.lbs.api.RssiData;
import com.motorola.adsp.lbs.api.SensorData;
import com.motorola.adsp.lbs.api.ServerRequestException;
import com.motorola.adsp.lbs.api.StreamException;
import com.motorola.adsp.lbs.api.StreamType;
import com.motorola.adsp.lbs.api.RegionEvent.RegionData;

public class AdspUtilThread extends Thread implements LbsStreamListener {

	private static final String CLINEMAC = "e8:99:c4:90:85:08"; // HTC
	// private static final String CLINEMAC = "2c:54:cf:fd:dc:ee"; //LG

	private LBSClient client;
	private ADSPServer server;
	private ProxySettings proxy;
	private NetworkPath mNetworkPath;
	private Bounds mBounds;
	private FloorPlan mFloorPlan;
	private FloorPlanRequest mFloorPlanRequest;
	private Location deviceLocation;

	private String ADDRIP = "192.168.1.241";
	private int PORT = 8543;
	private String USER = "admin";
	private String PASSWD = "motorola";

	private float locationDataX;
	private float locationDataY;
	private String timeComputed;
	private String timeExpires;
	private String deviceNetworkPath;
	private String macAddress;
	private List<String> regionsList;
	private long currentClock = 0;
	private long lastClock = 0;
	private int intervalClock = 0;

	private Date computedDate;
	private Date expiresDate;
	private Date servDate;
	private String timeServ;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	// "D:\work\project\Server\log";
	private FileOutputStream fileOutputStream = null;
	private String tempString = null;
	private Timer mTimer;
	private final String BACKGROUNDIMAGE = "D:/Tools/apache-tomcat-7.0.59/webapps/marketbackground.jpg";

	public AdspUtilThread() {
		File file = new File("D:/Tools/project/log/adsp.txt");
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

		regionsList = new ArrayList<String>();
	}

	@SuppressWarnings("deprecation")
	public void run() {
		System.out.println("adsp server thread run ...");

		proxy = new ProxySettings();
		server = new ADSPServer(ADDRIP, PORT, USER, PASSWD);

		try {
			client = new LBSClient(server, proxy);
			client.addLbsStreamListener(this);
			client.enableStreamingEventCache(true);

			client.openStream(StreamType.OUTPUT_LOCATION, new LocationFilter());
			// client.openStream(StreamType.OUTPUT_RSSI , new Filter());
			// client.openStream(StreamType.OUTPUT_PRESENCE , new
			// PresenceFilter());
			// client.openStream(StreamType.OUTPUT_REGION, new RegionFilter());

			// 下载ADSP服务器地图
//			deviceLocation = client.getDeviceLocation(CLINEMAC);
//			deviceNetworkPath = deviceLocation.getAdspNetworkPath();
//			System.out.println("deviceNetworkPath : " + deviceNetworkPath);
//			mBounds = client.getDesignBounds(deviceNetworkPath);
//			System.out.println("height:" + mBounds.getHeight() + " width:"
//					+ mBounds.getWidth() + " lowerLeftX:"
//					+ mBounds.getLowerLeftX() + " lowerLeftY:"
//					+ mBounds.getLowerLeftY() + " rightX:"
//					+ mBounds.getRightX() + " topY:" + mBounds.getTopY());
//
//			mFloorPlanRequest = new FloorPlanRequest(deviceNetworkPath);
//			mFloorPlanRequest.setPhysicalBounds(mBounds);
//			mFloorPlanRequest.setMaxImageHeight(1200);
//			mFloorPlanRequest.setMaxImageWidth(1600);
//			System.out.println("maxImageHeight:"
//					+ mFloorPlanRequest.getMaxImageHeight() + " maxImageWidth:"
//					+ mFloorPlanRequest.getMaxImageWidth());
//
//			mFloorPlan = client.getFloorPlan(mFloorPlanRequest);
//			BufferedImage backgoundImage = mFloorPlan.getImage();
//
//			try {
//				File backgroundFile = new File(BACKGROUNDIMAGE);
//				if (!backgroundFile.exists()) {
//					backgroundFile.createNewFile();
//				} else {
//					backgroundFile.delete();
//					backgroundFile.createNewFile();
//				}
//				ImageIO.write(backgoundImage, "jpg", backgroundFile);
//				System.out.println("finish download background picture...");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} catch (ConnectionException e) {
			e.printStackTrace();
		} catch (ServerRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 测试定时器调用getDeviceLocation获取点坐标数据
		// mTimer = new Timer();
		// DataTimerTask dataTask = new DataTimerTask();
		// mTimer.schedule(dataTask, 1000, 1000);
	}

	// 定时调用getDeviceLocation接口
	public class DataTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Location mLocation = client.getDeviceLocation(CLINEMAC);
				float x = mLocation.getX();
				float y = mLocation.getY();
				String msg = "x=" + x + " y=" + y;
				System.out.println("deviceLocation: " + msg);

				// Location lastLocation =
				// client.getLastKnownLocation(CLINEMAC);
				// System.out.println("lastLocation: x=" + lastLocation.getX() +
				// " y=" + lastLocation.getY());

				if (AdspServer.isConnClient) {
					ForClientThread.sendMessage(msg);
				}
			} catch (ServerRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void newLocationData(List<Location> arg0) {
		// TODO Auto-generated method stub
		for (Location dataLocation : arg0) {
			if (dataLocation.getClientMac().equals(CLINEMAC)) {
				macAddress = dataLocation.getClientMac();

				locationDataX = (float) (dataLocation.getX());
				locationDataY = (float) (dataLocation.getY());
				System.out.println("locationDataX = " + locationDataX
						+ " localDataY = " + locationDataY);

				// 测试regions
				// regionsList = dataLocation.getRegions();
				// for (String region : regionsList) {
				// System.out.println("region : " + region);
				// }

				// currentClock = dataLocation.getTimeComputed();
				// if (lastClock != 0) {
				// intervalClock = (int) (currentClock - lastClock);
				// }
				// lastClock = currentClock;

				// 当前时间点的转换
				servDate = new Date(System.currentTimeMillis());
				timeServ = dateFormat.format(servDate);
				// System.out.println("timeServ = " + timeServ);

				// try {
				// // 时间间隔大于2秒，重新获取Location数据，待测试
				// if (intervalClock > 2000) {
				// Location deviceLocation = client
				// .getDeviceLocation(macAddress);
				//
				// locationDataX = deviceLocation.getX();
				// locationDataY = deviceLocation.getY();
				//
				// }
				// } catch (ServerRequestException e1) {
				// e1.printStackTrace();
				// }

				// 定义消息发送格式
				String msg = "x=" + locationDataX + " y=" + locationDataY;
				// 客户端连接成功，发送消息
				if (AdspServer.isConnClient) {
					ForClientThread.sendMessage(msg);
				}

				// 测试写文件
				try {
					tempString = " time=" + timeServ + " " + msg;
					fileOutputStream.write(tempString.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 保存时间、点坐标于TXT文本中
				ForClientThread.getLoactionInfo(macAddress, tempString);
				// System.out.println(tempString);
			}
		}
	}

	@Override
	public void newPresenceData(List<PresenceEvent> arg0) {
		// TODO Auto-generated method stub
		for (PresenceEvent presenceEvent : arg0) {
			// if (presenceEvent.getClientMac().equals(CLINEMAC)) {
			System.out.println("presenceData rssi data : "
					+ presenceEvent.getRssiValue() + " event type : "
					+ presenceEvent.getEventType());
			// }
		}
	}

	@Override
	public void newRegionData(List<RegionEvent> arg0) {
		// TODO Auto-generated method stub
		for (RegionEvent regionEvent : arg0) {
			if (regionEvent.getClientMac().equals(CLINEMAC)) {
				List<RegionData> events = regionEvent.getEvents();

				for (RegionData data : events) {

					System.out.println("eventType : " + data.getEventType()
							+ " regionName : " + data.getRegionName());
				}
			}
		}
	}

	@Override
	public void newRssiData(List<RssiData> arg0) {
		// TODO Auto-generated method stub
		for (RssiData rssiData : arg0) {
			List<SensorData> sensorDatas = rssiData.getSensorData();
			for (SensorData data : sensorDatas) {
				int value = data.getDataFramesRssi();
				System.out.println("rssi : " + value + "dBm");
			}
		}
	}

	@Override
	public void streamError(StreamException arg0) {
		// TODO Auto-generated method stub

	}

}
