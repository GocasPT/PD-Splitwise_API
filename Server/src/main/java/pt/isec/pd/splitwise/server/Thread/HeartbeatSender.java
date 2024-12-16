package pt.isec.pd.splitwise.server.Thread;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Observer.DatabaseChangeObserver;
import pt.isec.pd.splitwise.sharedLib.network.Heartbeat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;

public class HeartbeatSender extends Thread implements DatabaseChangeObserver {
	private static final Logger logger = LoggerFactory.getLogger(HeartbeatSender.class);

	private final int HEARTBEAT_INTERVAL = 10;

	private final MulticastSocket multicastSocket;

	private final InetAddress group;

	private final ServerSocket backupServerSocket;

	private final DataBaseManager dbManager;

	private final boolean isRunning;

	public HeartbeatSender(boolean isRunning, MulticastSocket multicastSocket, InetAddress group, ServerSocket backupServerSocket, DataBaseManager dbManager) {
		super("HeartbeatSender");
		this.isRunning = isRunning;
		this.multicastSocket = multicastSocket;
		this.group = group;
		this.backupServerSocket = backupServerSocket;
		this.dbManager = dbManager;
	}

	@Override
	public void run() {
		try {
			logger.info("Heartbeat sender started");

			while (isRunning) {
				Thread.sleep(HEARTBEAT_INTERVAL * 1000);
				sendHeartbeat(null);
			}
		} catch ( InterruptedException e ) {
			logger.error("Heartbeat sender stopped");
		} catch ( RuntimeException e ) {
			logger.error("RuntimeException: {}", e.getMessage());
		} finally {
			multicastSocket.close();
			logger.info("Heartbeat sender closed");
		}
	}

	private void sendHeartbeat(String query, Object... params) {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(bOut)
		) {
			Heartbeat heartbeat = new Heartbeat(dbManager.getVersion(), backupServerSocket.getLocalPort(), query,
			                                    params);
			out.writeObject(heartbeat);
			out.flush();
			logger.info("Sending heartbeat: {}", heartbeat);
			DatagramPacket packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), group,
			                                           multicastSocket.getLocalPort());
			multicastSocket.send(packet);
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onDBChange(String query, Object... params) {
		sendHeartbeat(query, params);
	}
}
