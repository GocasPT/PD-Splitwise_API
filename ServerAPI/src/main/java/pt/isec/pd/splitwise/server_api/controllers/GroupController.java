package pt.isec.pd.splitwise.server_api.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.splitwise.server_api.repository.DataWrapper;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Group.PreviewGroupDTO;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Expense;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Group;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Controller responsible for handling group-related operations in the Splitwise application.
 *
 * This controller provides RESTful endpoints for managing groups and expenses, including:
 * - Retrieving user's groups
 * - Creating new groups
 * - Updating group information
 * - Fetching group expenses
 * - Creating and deleting expenses within groups
 *
 * The controller uses Spring's RestController annotation and requires authentication
 * for all operations. It interacts with the DataWrapper to perform database operations.
 *
 */
@Slf4j
@RestController
@RequestMapping("groups")
@RequiredArgsConstructor
public class GroupController {
	private final DataWrapper dataWrapper;

	/**
	 * Retrieves all groups for the authenticated user with optional filtering.
	 *
	 * @param authentication The authentication object containing user details
	 * @param name Optional group name filter
	 * @param numUsers Optional number of users filter
	 * @return ResponseEntity containing:
	 *         - [HTTP Status 200] List of preview group DTOs if groups found
	 *         - [HTTP Status 404] "No groups found" message if no groups exist
	 *         - [HTTP Status 500] Error response for database or server errors
	 */
	@GetMapping
	public ResponseEntity<?> getGroups(
			Authentication authentication,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "numUsers", required = false) Integer numUsers
	) {
		try {
			User authUSer = dataWrapper.getUserDAO().getUserByEmail(authentication.getName());
			List<Group> groupsList = dataWrapper.getGroupUserDAO().getAllGroupsFromUser(authUSer.getId());

			if (name != null)
				groupsList = groupsList.stream()
						.filter(group -> group.getName().contains(name))
						.toList();

			if (numUsers != null)
				groupsList = groupsList.stream()
						.filter(group -> group.getNumUsers() == numUsers)
						.toList();

			if (groupsList.isEmpty())
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body("No groups found");

			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(groupsList.stream()
							      .map(g -> new PreviewGroupDTO(g.getId(), g.getName(), g.getNumUsers()))
							      .toList()
					);
		} catch ( SQLException e ) {
			log.error("GroupController.getGroups: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error getting groups");
		}
	}

	/**
	 * Creates a new group for the authenticated user.
	 *
	 * @param authentication The authentication object containing user details
	 * @param group The group object to be created
	 * @return ResponseEntity containing:
	 *         - [HTTP Status 200] Created group preview if successful
	 *         - [HTTP Status 500] Error message if group creation fails
	 */
	@PostMapping
	public ResponseEntity<?> createGroup(
			Authentication authentication,
			@RequestBody Group group
	) {
		try {
			User authUSer = dataWrapper.getUserDAO().getUserByEmail(authentication.getName());
			int groupId = dataWrapper.getGroupDAO().createGroup(group.getName(), authUSer.getId());

			return ResponseEntity.ok()
					.body(new PreviewGroupDTO(groupId, group.getName(), 1));
		} catch ( SQLException e ) {
			log.error("GroupController.createGroup: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating group");
		}
	}

	/**
	 * Retrieves a specific group by its ID for the authenticated user.
	 *
	 * @param authentication The authentication object containing user details
	 * @param groupId The ID of the group to retrieve
	 * @return ResponseEntity containing:
	 *         - [HTTP Status 200] Group details if found
	 *         - [HTTP Status 404] "Group not found" message if group doesn't exist
	 *         - [HTTP Status 500] Error response for invalid ID or server errors
	 */
	@GetMapping("/{groupId}")
	public ResponseEntity<?> getGroup(
			Authentication authentication,
			@PathVariable String groupId
	) {
		try {
			User authUSer = dataWrapper.getUserDAO().getUserByEmail(authentication.getName());
			Group groupSelected = dataWrapper.getGroupUserDAO().getAllGroupsFromUser(authUSer.getId()).stream()
					.filter(group -> group.getId() == Integer.parseInt(groupId))
					.findFirst()
					.orElse(null);

			//TODO: get member, expenses, etc and put on the group

			if (groupSelected == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Group not found");

			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(groupSelected);
		} catch ( NumberFormatException e ) {
			return ResponseEntity.badRequest()
					.body("ID inválido.");
		} catch ( SQLException e ) {
			log.error("GroupController.getGroup: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao obter evento.");
		}
	}

	/**
	 * Updates an existing group's information.
	 *
	 * @param authentication The authentication object containing user details
	 * @param groupId The ID of the group to update
	 * @param group The updated group information
	 * @return ResponseEntity containing:
	 *         - [HTTP Status 200] Updated group preview if successful
	 *         - [HTTP Status 404] "Group not found" message if group doesn't exist
	 *         - [HTTP Status 500] Error response for invalid ID or server errors
	 */
	@PutMapping("/{groupId}")
	public ResponseEntity<?> updateGroup(
			Authentication authentication,
			@PathVariable String groupId,
			@RequestBody Group group
	) {
		try {
			User authUSer = dataWrapper.getUserDAO().getUserByEmail(authentication.getName());
			Group groupSelected = dataWrapper.getGroupUserDAO().getAllGroupsFromUser(authUSer.getId()).stream()
					.filter(g -> g.getId() == Integer.parseInt(groupId))
					.findFirst()
					.orElse(null);

			if (groupSelected == null)
				return ResponseEntity.badRequest()
						.body("Group not found");

			dataWrapper.getGroupDAO().editGroup(groupSelected.getId(), group.getName());

			return ResponseEntity.ok()
					.body(dataWrapper.getGroupUserDAO().getAllGroupsFromUser(authUSer.getId()));
		} catch ( NumberFormatException e ) {
			return ResponseEntity.badRequest()
					.body("ID inválido.");
		} catch ( SQLException e ) {
			log.error("GroupController.updateGroup: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves expenses for a specific group with optional filtering.
	 *
	 * @param authentication The authentication object containing user details
	 * @param groupId The ID of the group to retrieve expenses from
	 * @param title Optional expense title filter
	 * @param amount Optional expense amount filter
	 * @param date Optional expense date filter
	 * @param register Optional expense registrar filter
	 * @param payer Optional expense payer filter
	 * @param associated Optional list of associated users filter
	 * @return ResponseEntity containing:
	 *         - [HTTP Status 200] List of expenses if found
	 *         - [HTTP Status 404] "No expenses found" message if no expenses exist
	 *         - [HTTP Status 500] Error response for group not found or server errors
	 */
	@GetMapping("/{groupId}/expenses")
	public ResponseEntity<?> getExpenses(
			Authentication authentication,
			@PathVariable int groupId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "amount", required = false) Double amount,
			@RequestParam(value = "date", required = false) String date,
			@RequestParam(value = "register", required = false) String register,
			@RequestParam(value = "payer", required = false) String payer,
			@RequestParam(value = "associated", required = false) List<String> associated
	) {
		try {
			User useAuth = dataWrapper.getUserDAO().getUserByEmail(authentication.getName());
			Group groupSelected = dataWrapper.getGroupUserDAO().getAllGroupsFromUser(useAuth.getId()).stream()
					.filter(g -> g.getId() == groupId)
					.findFirst()
					.orElse(null);

			//TODO: get register user and put on the expense

			if (groupSelected == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Group not found");

			List<Expense> expenseList = dataWrapper.getExpenseDAO().getAllExpensesFromGroup(groupSelected.getId());

			if (title != null)
				expenseList = expenseList.stream()
						.filter(expense -> expense.getTitle().contains(title))
						.toList();

			if (amount != null)
				expenseList = expenseList.stream()
						.filter(expense -> expense.getAmount() == amount)
						.toList();

			if (date != null)
				expenseList = expenseList.stream()
						.filter(expense -> expense.getDate().toString().contains(date))
						.toList();

			if (register != null)
				expenseList = expenseList.stream()
						.filter(expense -> expense.getRegisterByUser().contains(register))
						.toList();

			if (payer != null)
				expenseList = expenseList.stream()
						.filter(expense -> expense.getPayerUser().contains(payer))
						.toList();

			if (associated != null) {
				expenseList = expenseList.stream()
						.filter(expense -> !Collections.disjoint(expense.getAssocietedUsersList(), associated))
						.toList();
			}

			if (expenseList.isEmpty())
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("No expenses found");

			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(expenseList);
		} catch ( SQLException e ) {
			log.error("GroupController.getExpenses: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Creates a new expense for a specific group.
	 *
	 * @param authentication The authentication object containing user details
	 * @param groupId The ID of the group to add the expense to
	 * @param expense The expense object to be created
	 * @return ResponseEntity containing:
	 *         - [HTTP Status 200] Created expense details if successful
	 *         - [HTTP Status 500] Error message if expense creation fails
	 */
	@PostMapping("/{groupId}/expenses")
	public ResponseEntity<?> createExpense(
			Authentication authentication,
			@PathVariable int groupId,
			@RequestBody Expense expense
	) {
		try {
			User userAuth = dataWrapper.getUserDAO().getUserByEmail(authentication.getName());
			Group groupSelected = dataWrapper.getGroupUserDAO().getAllGroupsFromUser(userAuth.getId()).stream()
					.filter(g -> g.getId() == groupId)
					.findFirst()
					.orElse(null);

			if (groupSelected == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Group not found");

			//TODO: validadete expense

			int expenseId = dataWrapper.getExpenseDAO()
					.createExpense(groupId, expense.getAmount(), expense.getTitle(), expense.getDate(),
					               userAuth.getId(),
					               dataWrapper.getUserDAO().getUserByEmail(expense.getPayerUser()).getId(),
					               expense.getAssocietedUsersList().stream().mapToInt(userEmail -> {
						               try {
							               return dataWrapper.getUserDAO().getUserByEmail(
									               userEmail).getId();
						               } catch ( SQLException e ) {
							               throw new RuntimeException(e);
						               }
					               }).toArray());

			return ResponseEntity.ok()
					.body(dataWrapper.getExpenseDAO().getExpenseById(expenseId));
		} catch ( SQLException e ) {
			log.error("GroupController.createExpense: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating expense");
		}
	}

	/**
	 * Deletes a specific expense from a group.
	 *
	 * @param authentication The authentication object containing user details
	 * @param groupId The ID of the group containing the expense
	 * @param expenseId The ID of the expense to delete
	 * @return ResponseEntity containing:
	 *         - [HTTP Status 200] "Expense deleted" message if successful
	 *         - [HTTP Status 204] "Group not found" or "Expense not found" if either doesn't exist
	 *         - [HTTP Status 500] Error response for server errors
	 */
	@DeleteMapping("/{groupId}/expenses/{expenseId}")
	public ResponseEntity<?> deleteExpense(
			Authentication authentication,
			@PathVariable int groupId,
			@PathVariable int expenseId
	) {
		try {
			User userAuth = dataWrapper.getUserDAO().getUserByEmail(authentication.getName());
			Group groupSelected = dataWrapper.getGroupUserDAO().getAllGroupsFromUser(userAuth.getId()).stream()
					.filter(g -> g.getId() == groupId)
					.findFirst()
					.orElse(null);

			if (groupSelected == null)
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body("Group not found");

			Expense expense = dataWrapper.getExpenseDAO().getAllExpensesFromGroup(groupId).stream()
					.filter(e -> e.getId() == expenseId)
					.findFirst()
					.orElse(null);
			if (expense == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Expense not found in group");

			dataWrapper.getExpenseDAO().deleteExpense(expenseId);

			return ResponseEntity.ok()
					.body("Expense deleted");
		} catch ( SQLException e ) {
			log.error("GroupController.deleteExpense: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error deleting expense");
		}
	}
}
