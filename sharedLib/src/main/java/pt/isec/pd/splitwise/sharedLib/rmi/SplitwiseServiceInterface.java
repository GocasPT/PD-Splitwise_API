package pt.isec.pd.splitwise.sharedLib.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SplitwiseServiceInterface extends Remote {
	List<String> getUsers() throws RemoteException;

	List<String> getGroups() throws RemoteException;

	void addObserver(ObserverServiceInterface observer) throws RemoteException;

	void removeObserver(ObserverServiceInterface observer) throws RemoteException;
}
