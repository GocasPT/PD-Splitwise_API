package pt.isec.pd.splitwise.sharedLib.database.Observer;

public interface DatabaseChangeObserver {
	void onDBChange(String query, Object... params);
}
