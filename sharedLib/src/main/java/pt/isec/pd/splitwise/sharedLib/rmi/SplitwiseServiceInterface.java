package pt.isec.pd.splitwise.sharedLib.rmi;

import java.rmi.RemoteException;
import java.util.List;

public interface SplitwiseServiceInterface {
	List<String> getUsers()  throws RemoteException;
	List<String> getGroups() throws RemoteException;

	void addObserver(OberserverServiceInterface observer) throws RemoteException;
	void removeObserver(OberserverServiceInterface observer) throws RemoteException;
}
