package pt.isec.pd.splitwise.sharedLib.database.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;

public abstract class DAO {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final DataBaseManager dbManager;

	public DAO(DataBaseManager dbManager) {
		this.dbManager = dbManager;
	}
}
