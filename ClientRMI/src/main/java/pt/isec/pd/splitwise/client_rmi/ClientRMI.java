package pt.isec.pd.splitwise.client_rmi;

import pt.isec.pd.splitwise.sharedLib.rmi.SplitwiseServiceInterface;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientRMI {
	private static final String RMI_REGISTRY = "localhost";
	private static final int RMI_PORT = Registry.REGISTRY_PORT;
	private static ObserverService observerService;
	private static SplitwiseServiceInterface rmiService;

	public static void main(String[] args) {
		System.out.println("Starting ClientRMI");

		try {
			String registration = "rmi://" + RMI_REGISTRY + ":" + RMI_PORT + "/SplitwiseService";

			Remote remoteService = Naming.lookup(registration);
			rmiService = (SplitwiseServiceInterface) remoteService;

			observerService = new ObserverService();
			rmiService.addObserver(observerService);

			System.out.println("ClientRMI started");
			System.out.println("getUsers: " + rmiService.getUsers());
			System.out.println("getGroups: " + rmiService.getGroups());

			System.out.println("Press enter to stop");
			System.in.read();
			stop();
		} catch ( NotBoundException e ) {
			System.out.println("No 'SplitwiseServiceInterface' service available!");
		} catch ( RemoteException e ) {
			System.out.println("RMI error - " + e);
		} catch ( Exception e ) {
			System.out.println("Error - " + e);
		}
	}

	private static void stop() throws RemoteException {
		rmiService.removeObserver(observerService);
		if (!UnicastRemoteObject.unexportObject(observerService, true)) {
			System.out.println("Failed to unexport ObserverService");
		}

		System.out.println("ClientRMI finished");
	}
}