package pt.isec.pd.splitwise.sharedLib.database.Observer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIObserver extends Remote {
	void onRegiste(String email) throws RemoteException;
	void onLogin(String email) throws RemoteException;
	void onInsertExpense(String email, double amount) throws RemoteException;
	void onDeleteExpense(int id) throws RemoteException;
}
