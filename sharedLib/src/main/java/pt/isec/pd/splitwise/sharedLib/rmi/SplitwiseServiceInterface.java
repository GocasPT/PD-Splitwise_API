package pt.isec.pd.splitwise.sharedLib.rmi;

import pt.isec.pd.splitwise.sharedLib.database.Observer.RMIObserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SplitwiseServiceInterface extends Remote {
	List<String> getUsers()  throws RemoteException;
	List<String> getGroups() throws RemoteException;

	void addObserver(ObserverServiceInterface observer) throws RemoteException;
	void removeObserver(ObserverServiceInterface observer) throws RemoteException;
}
