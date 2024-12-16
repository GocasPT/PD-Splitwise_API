package pt.isec.pd.splitwise.server_api.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.splitwise.server_api.repository.DataWrapper;
import pt.isec.pd.splitwise.server_api.security.TokenService;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;

import java.sql.SQLException;

/**
 * Controller responsible for handling user authentication and registration operations.
 *
 * This controller provides endpoints for user registration and login, including:
 * - Registering new users with validation
 * - Generating authentication tokens for logged-in users
 *
 * The controller uses Spring's RestController annotation and integrates with
 * TokenService for authentication and DataWrapper for database operations.
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
	private final TokenService tokenService;
	private final DataWrapper dataWrapper;

	/**
	 * Registers a new user in the system with comprehensive validation.
	 *
	 * Performs validation checks on:
	 * - Presence of required fields (email, password, phone number)
	 * - Non-empty field values
	 * - Email format (alphanumeric characters, @, ., _, -)
	 * - Phone number format (numeric characters only)
	 *
	 * @param userConfig User object containing registration details
	 * @return ResponseEntity containing:
	 *         - [HTTP Status 201] "User created" if registration is successful
	 *         - [HTTP Status 409] "User already exists" if user is a duplicate
	 *         - [HTTP Status 400] Error messages for:
	 *           * "Missing fields" for null fields
	 *           * "Empty fields" for blank fields
	 *           * "Invalid email" for incorrect email format
	 *           * "Invalid phone number" for non-numeric phone numbers
	 *         - [HTTP Status 500] "Internal server error" for database exceptions
	 */
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User userConfig) {
		try {
			if (userConfig.getEmail() == null || userConfig.getPassword() == null || userConfig.getPhoneNumber() == null)
				return ResponseEntity.badRequest()
						.body("Missing fields");

			String email = userConfig.getEmail();
			String password = userConfig.getPassword();
			String phoneNumber = userConfig.getPhoneNumber();

			if (email.isBlank() || password.isBlank() || phoneNumber.isBlank())
				return ResponseEntity.badRequest()
						.body("Empty fields");

			if (email.matches(".*[^a-zA-Z0-9@._-].*"))
				return ResponseEntity.badRequest()
						.body("Invalid email");

			if (phoneNumber.matches(".*[^0-9].*"))
				return ResponseEntity.badRequest()
						.body("Invalid phone number");

			return dataWrapper.getUserDAO().createUser(email, email, phoneNumber, password) == 1
					? ResponseEntity.status(HttpStatus.CREATED).body("User created")
					: ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
		} catch ( SQLException e ) {
			log.error("AuthController.register: {}", e.getMessage()); //TODO: improve this
			return ResponseEntity.internalServerError()
					.body("Internal server error");
		}
	}

	/**
	 * Generates an authentication token for a successfully logged-in user.
	 *
	 * This method is called after successful authentication and uses the
	 * TokenService to create a unique token for the authenticated user.
	 *
	 * @param authentication The authentication object containing user credentials
	 * @return String representing the generated authentication token
	 */
	@PostMapping("/login")
	public String login(Authentication authentication) {
		return tokenService.generateToken(authentication);
	}
}
