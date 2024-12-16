package pt.isec.pd.splitwise.server_api.repository;

import lombok.experimental.Delegate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;

@ConfigurationProperties(prefix = "db")
public class DataWrapper {
	@Delegate private final DataBaseManager dbManager;

	public DataWrapper(String dbPath) {
		dbManager = new DataBaseManager(dbPath, null);
	}
}
