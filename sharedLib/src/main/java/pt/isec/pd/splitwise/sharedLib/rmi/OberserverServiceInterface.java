package pt.isec.pd.splitwise.sharedLib.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OberserverServiceInterface extends Remote {
	void onRegiste() throws RemoteException;
	void onLogin() throws RemoteException;
	void onInsertExpense() throws RemoteException;
	void onDeleteExpense() throws RemoteException;
}