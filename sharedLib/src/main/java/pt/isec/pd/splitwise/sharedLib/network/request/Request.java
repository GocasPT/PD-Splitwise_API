package pt.isec.pd.splitwise.sharedLib.network.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.io.Serializable;

public interface Request extends Serializable {
	Logger logger = LoggerFactory.getLogger(Request.class);

	Response execute(DataBaseManager context);

	@Override
	String toString();
}
