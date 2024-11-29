package pt.isec.pd.splitwise.sharedLib.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DatabaseSyncManager {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseSyncManager.class);
	private final Object syncLock = new Object();
	private final Set<Thread> waitingThreads = new HashSet<>();
	private int activeBackupTransfers = 0;

	public void startBackupTransfer() {
		logger.debug("Starting backup transfer");

		synchronized (syncLock) {
			activeBackupTransfers++;
			logger.debug("Backup transfer started. Active transfers: {}", activeBackupTransfers);
		}

		logger.debug("Backup transfer started");
	}

	public void endBackupTransfer() {
		synchronized (syncLock) {
			if (activeBackupTransfers > 0) {
				activeBackupTransfers--;
			}
			if (activeBackupTransfers == 0) {
				syncLock.notifyAll();
				logger.debug("All backup transfers completed. Notifying waiting threads.");
			}
		}
	}

	public <T> T executeOperation(DatabaseOperation<T> operation) throws InterruptedException, SQLException {
		Thread currentThread = Thread.currentThread();
		synchronized (syncLock) {
			waitingThreads.add(currentThread);
			try {
				while (activeBackupTransfers > 0) {
					logger.debug("Thread {} waiting for {} backup transfers to complete",
					             currentThread.getName(), activeBackupTransfers);
					syncLock.wait();
				}
				if (operation != null)
					return operation.execute();
				else
					return null;
			} finally {
				waitingThreads.remove(currentThread);
			}
		}
	}

	public interface DatabaseOperation<T> {
		T execute() throws SQLException;
	}
}