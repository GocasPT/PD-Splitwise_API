package pt.isec.pd.splitwise.server.Thread;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.server.Runnable.BackupServerHandler;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class BackupServerReceiver extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(BackupServerReceiver.class);
	private final ServerSocket serverSocket;
	private final DataBaseManager context;
	private final boolean isRunning;

	public BackupServerReceiver(boolean isRunning, ServerSocket serverSocket, DataBaseManager context) {
		super("BackupServerReceiver");
		this.isRunning = isRunning;
		this.serverSocket = serverSocket;
		this.context = context;
	}

	@Override
	public void run() {
		try {
			logger.info("Backup server receiver started");

			while (isRunning) {
				Socket backupServerSocket = serverSocket.accept();
				new Thread(
						new BackupServerHandler(backupServerSocket, context),
						backupServerSocket.getInetAddress().getHostAddress()
				).start();
			}
		} catch ( NumberFormatException e ) {
			logger.error("O porto de escuta deve ser um inteiro positivo");
		} catch ( SocketException e ) {
			logger.error("backupServerSocket TCP: {}", e.getMessage());
		} catch ( IOException e ) {
			logger.error("IOException: {}", e.getMessage());
		} finally {
			try {
				serverSocket.close();
			} catch ( IOException e ) {
				logger.error("Fechar o socket: {}", e.getMessage());
			}
		}
	}
}
