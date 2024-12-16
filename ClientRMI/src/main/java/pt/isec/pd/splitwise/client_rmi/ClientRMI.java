package pt.isec.pd.splitwise.client_rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.sharedLib.rmi.SplitwiseServiceInterface;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

public class ClientRMI {
	private static final Logger logger = LoggerFactory.getLogger(ClientRMI.class);
	private static final String RMI_REGISTRY = "localhost";
	private static final int RMI_PORT = Registry.REGISTRY_PORT;
	private static SplitwiseServiceInterface rmiService;
	private static ObserverService observerService;
	private static Scanner scanner;

	public static void main(String[] args) {
		logger.info("Starting ClientRMI");
		scanner = new Scanner(System.in);

		try {
			String registration = "rmi://" + RMI_REGISTRY + ":" + RMI_PORT + "/SplitwiseService";

			Remote remoteService = Naming.lookup(registration);
			rmiService = (SplitwiseServiceInterface) remoteService;

			observerService = new ObserverService();
			rmiService.addObserver(observerService);

			showMenu();
		} catch ( NotBoundException e ) {
			logger.error("No 'SplitwiseServiceInterface' service available!");
		} catch ( RemoteException e ) {
			logger.error("RMI error - {}", String.valueOf(e));
		} catch ( Exception e ) {
			logger.error("Error - {}", String.valueOf(e));
		}
	}

	private static void showMenu() throws RemoteException {
		while (true) {
			printMenuOptions();

			try {
				int choice = Integer.parseInt(scanner.nextLine().trim());

				switch (choice) {
					case 1:
						listUsers();
						break;
					case 2:
						listGroups();
						break;
					case 3:
						stop();
						return;
					default:
						System.out.println("Invalid option. Please try again.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number.");
			} catch (Exception e) {
				logger.error("Error in menu operation: {}", e.getMessage());
			}
		}
	}

	private static void printMenuOptions() {
		System.out.println("\n--- Splitwise RMI Client ---");
		System.out.println("1. List Users");
		System.out.println("2. List Groups");
		System.out.println("3. Exit");
		System.out.print("Enter your choice: ");
	}

	private static void listUsers() throws RemoteException {
		List<?> users = rmiService.getUsers();
		System.out.println("--- Users ---");
		if (users.isEmpty()) {
			System.out.println("No users found.");
		} else {
			users.forEach(System.out::println);
		}
	}

	private static void listGroups() throws RemoteException {
		List<?> groups = rmiService.getGroups();
		System.out.println("--- Groups ---");
		if (groups.isEmpty()) {
			System.out.println("No groups found.");
		} else {
			groups.forEach(System.out::println);
		}
	}

	private static void stop() throws RemoteException {
		try {
			// Remove observer
			rmiService.removeObserver(observerService);

			// Unexport the observer service
			if (!UnicastRemoteObject.unexportObject(observerService, true)) {
				logger.warn("Failed to unexport ObserverService");
			}

			System.out.println("ClientRMI is shutting down...");
			logger.info("ClientRMI finished");
		} catch (Exception e) {
			logger.error("Error during shutdown: {}", e.getMessage());
		}
	}
}