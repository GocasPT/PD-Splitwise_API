package pt.isec.pd.splitwise.server_api_rmi.rmi;

import pt.isec.pd.splitwise.sharedLib.rmi.SplitwiseServiceInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SplitwiseService extends UnicastRemoteObject implements SplitwiseServiceInterface {
	protected SplitwiseService() throws RemoteException {}

	@Override public void getUsers() throws RemoteException {

	}

	@Override public void getGroups() throws RemoteException {

	}

	@Override public void addObserver() throws RemoteException {

	}

	@Override public void removeObserver() throws RemoteException {

	}
}
