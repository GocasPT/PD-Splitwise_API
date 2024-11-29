package pt.isec.pd.splitwise.sharedLib.rmi;

import java.rmi.RemoteException;

public interface SplitwiseServiceInterface {
	void getUsers()  throws RemoteException;
	void getGroups() throws RemoteException;

	void addObserver() throws RemoteException;
	void removeObserver() throws RemoteException;
}
