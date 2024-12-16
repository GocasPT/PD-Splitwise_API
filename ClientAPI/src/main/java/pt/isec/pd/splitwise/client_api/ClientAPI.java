package pt.isec.pd.splitwise.client_api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Scanner;

public class ClientAPI {
	private static final String BASE_URL = "http://localhost:8080";
	private final Scanner scanner;
	private String authToken;

	public ClientAPI() {
		this.scanner = new Scanner(System.in);
	}

	public static void main(String[] args) {
		ClientAPI client = new ClientAPI();
		client.showInitialMenu();
	}

	/**
	 * Send HTTP requests with JWT token authentication
	 *
	 * @param url    Target URL
	 * @param method HTTP method
	 * @param body   Request body (can be null)
	 * @return Server response
	 * @throws Exception If request fails
	 */
	private String sendRequest(String url, String method, String body) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URI(BASE_URL + url).toURL().openConnection();
		conn.setRequestMethod(method);

		if (authToken != null) {
			conn.setRequestProperty("Authorization", "Bearer " + authToken);
		}

		if (body != null) {
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			try ( OutputStream os = conn.getOutputStream() ) {
				byte[] input = body.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}
		}

		int responseCode = conn.getResponseCode();

		if (responseCode == 401) {
			System.out.println("Authentication failed. Please log in or register.");
			authToken = null;
			showMainMenu();
			return null;
		}

		BufferedReader reader = responseCode >= 200 && responseCode <= 299
				? new BufferedReader(new InputStreamReader(conn.getInputStream()))
				: new BufferedReader(new InputStreamReader(conn.getErrorStream()));

		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();

		if (responseCode >= 400) {
			System.out.println("Error: " + response);
			return null;
		}

		return response.toString();
	}

	private void registerUser() {
		try {
			System.out.print("Enter email: ");
			String email = scanner.nextLine();

			System.out.print("Enter password: ");
			String password = scanner.nextLine();

			System.out.print("Enter phone number: ");
			String phoneNumber = scanner.nextLine();

			String body = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"phoneNumber\":\"%s\"}",
			                            email, password, phoneNumber);

			String response = sendRequest("/register", "POST", body);
			if (response != null) {
				System.out.println("Registration successful!");
				loginUser(email, password);
			}
		} catch ( Exception e ) {
			System.out.println("Registration failed: " + e.getMessage());
		}
	}

	private void loginUser() {
		try {
			System.out.print("Enter email: ");
			String email = scanner.nextLine();

			System.out.print("Enter password: ");
			String password = scanner.nextLine();

			loginUser(email, password);
		} catch ( Exception e ) {
			System.out.println("Login failed: " + e.getMessage());
		}
	}

	private void loginUser(String email, String password) throws Exception {
		URL url = new URL(BASE_URL + "/login");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		String auth = email + ":" + password;
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", authHeaderValue);

		int responseCode = conn.getResponseCode();
		if (responseCode == 200) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			authToken = reader.readLine();
			reader.close();

			System.out.println("Login successful!");
			showMainMenu();
		} else {
			System.out.println("Login failed.");
			showInitialMenu();
		}
	}

	private void createGroup() {
		try {
			System.out.print("Enter group name: ");
			String groupName = scanner.nextLine();

			String body = String.format("{\"name\":\"%s\"}", groupName);
			String response = sendRequest("/groups", "POST", body);

			if (response != null) {
				System.out.println("Group created: " + response);
			}
		} catch ( Exception e ) {
			System.out.println("Group creation failed: " + e.getMessage());
		}
	}

	private void listGroups() {
		try {
			String response = sendRequest("/groups", "GET", null);
			if (response != null) {
				System.out.println("Your groups:");
				System.out.println(response);
			}
		} catch ( Exception e ) {
			System.out.println("Failed to retrieve groups: " + e.getMessage());
		}
	}

	private void createExpense() {
		try {
			System.out.print("Enter group ID: ");
			int groupId = Integer.parseInt(scanner.nextLine());

			System.out.print("Enter expense title: ");
			String title = scanner.nextLine();

			System.out.print("Enter expense amount: ");
			double amount = Double.parseDouble(scanner.nextLine());

			System.out.print("Enter payer email: ");
			String payerEmail = scanner.nextLine();

			System.out.print("Enter associated user emails (comma-separated): ");
			String associatedUsers = scanner.nextLine();

			String body = String.format("{" +
			                            "\"amount\": %.0f," +
			                            "\"title\": \"%s\"," +
			                            "\"date\": \"%s\"," +
			                            "\"payerUser\": \"%s\"," +
			                            "\"associetedUsersList\": [%s]" +
			                            "}",
			                            amount,
			                            title,
			                            LocalDate.now(),
			                            payerEmail,
			                            String.join(",",
			                                        associatedUsers.split(","))
					                            .lines()
					                            .map(email -> "\"" + email.trim() + "\"")
					                            .toList());

			System.out.println("Request body: " + body);

			String response = sendRequest("/groups/" + groupId + "/expenses", "POST", body);

			if (response != null) {
				System.out.println("Expense created: " + response);
			}
		} catch ( Exception e ) {
			System.out.println("Expense creation failed: " + e.getMessage());
		}
	}

	private void listExpenses() {
		try {
			System.out.print("Enter group ID: ");
			int groupId = Integer.parseInt(scanner.nextLine());

			String response = sendRequest("/groups/" + groupId + "/expenses", "GET", null);
			if (response != null) {
				System.out.println("Group expenses:");
				System.out.println(response);
			}
		} catch ( Exception e ) {
			System.out.println("Failed to retrieve expenses: " + e.getMessage());
		}
	}

	private void showInitialMenu() {
		while (true) {
			System.out.println("\n--- Splitwise REST Client ---");
			System.out.println("1. Register");
			System.out.println("2. Login");
			System.out.println("3. Exit");
			System.out.print("Choose an option: ");

			String choice = scanner.nextLine();
			switch (choice) {
				case "1" -> registerUser();
				case "2" -> loginUser();
				case "3" -> {
					System.out.println("Exiting...");
					return;
				}
				default -> System.out.println("Invalid option. Try again.");
			}
		}
	}

	private void showMainMenu() {
		while (authToken != null) {
			System.out.println("\n--- Splitwise Dashboard ---");
			System.out.println("1. Create Group");
			System.out.println("2. List Groups");
			System.out.println("3. Create Expense");
			System.out.println("4. List Expenses");
			System.out.println("5. Logout");
			System.out.print("Choose an option: ");

			String choice = scanner.nextLine();
			switch (choice) {
				case "1" -> createGroup();
				case "2" -> listGroups();
				case "3" -> createExpense();
				case "4" -> listExpenses();
				case "5" -> {
					authToken = null;
					showInitialMenu();
					return;
				}
				default -> System.out.println("Invalid option. Try again.");
			}
		}
	}
}