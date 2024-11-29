package pt.isec.pd.splitwise.sharedLib.network.request.Payment;

import org.sqlite.util.StringUtils;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Balance.DetailBalanceDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Expense;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Payment;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ViewBalance(int groupID) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting balance for group {}", groupID);

		Map<String, DetailBalanceDTO> groupBalance = new HashMap<>();
		try {
			List<User> userList = context.getGroupUserDAO().getAllUsersFromGroup(groupID);
			logger.debug("Users:[\n{}]",
			             StringUtils.join(userList.stream()
					                              .map(user -> "\t" + user.toString())
					                              .collect(Collectors.toList()),
			                              "\n"
			             )); //TODO: better format
			List<Expense> expensesList = context.getExpenseDAO().getAllExpensesFromGroup(groupID);
			logger.debug("Expenses:[\n{}]",
			             StringUtils.join(expensesList.stream()
					                              .map(expense -> "\t" + expense.toString())
					                              .collect(Collectors.toList()),
			                              "\n"
			             )); //TODO: better format
			List<Payment> paymentsList = context.getPaymentDAO().getAllPaymentsFromGroup(groupID);
			logger.debug("Payments:[\n{}]",
			             StringUtils.join(paymentsList.stream()
					                              .map(payment -> "\t" + payment.toString())
					                              .collect(Collectors.toList()),
			                              "\n"
			             )); //TODO: better format

			for (User user : userList)
				groupBalance.put(user.getEmail(), new DetailBalanceDTO());

			logger.debug("Group balance:[\n{}]",
			             StringUtils.join(groupBalance.entrySet().stream()
					                              .map(entry ->
							                                   String.format("\t%s = %s", entry.getKey(),
							                                                 entry.getValue()))
					                              .collect(Collectors.toList()),
			                              "\n"
			             ));

			//TODO: round doubles to 2 decimal places
			for (Expense expense : expensesList) {
				String buyerEmail = expense.getPayerUser();
				double amount = expense.getAmount();
				List<String> associateUsersEmail = expense.getAssocietedUsersList();

				logger.debug("Expense {} bought by {} for {}", expense.getId(), buyerEmail, amount);
				if (buyerEmail == null || associateUsersEmail.isEmpty())
					continue;

				// Total expense
				DetailBalanceDTO buyerBalance = groupBalance.get(buyerEmail);

				logger.debug("Buyer balance: {}", buyerBalance);

				buyerBalance.setTotalExpended(buyerBalance.getTotalExpended() + amount);

				double share = amount / (associateUsersEmail.size());
				for (String associatedUserEmail : associateUsersEmail) {
					if (associatedUserEmail.equals(buyerEmail))
						continue;

					DetailBalanceDTO userBalance = groupBalance.get(associatedUserEmail);

					logger.debug("User balance: {}", userBalance);

					// Update associated user debts → add the user part
					userBalance.setTotalDebt(userBalance.getTotalDebt() + share);
					userBalance.getDebtList().merge(buyerEmail, share, Double::sum);

					logger.debug("User balance updated: {}", userBalance);

					// Update buyer receive → add the user part
					buyerBalance.setTotalReceive(buyerBalance.getTotalReceive() + share);
					buyerBalance.getReceiveList().merge(associatedUserEmail, share, Double::sum);
				}

				logger.debug("Buyer balance updated: {}", buyerBalance);
			}

			logger.debug("Group balance after expenses:\n{}", groupBalance.entrySet().stream()
					.map(entry -> String.format("\t%s = %s", entry.getKey(), entry.getValue()))
					.collect(Collectors.joining("\n")));

			for (Payment payment : paymentsList) {
				String fromUser = payment.getFromUser();
				String toUser = payment.getToUser();
				double amount = payment.getAmount();

				logger.debug("Payment from {} to {} of {}", fromUser, toUser, amount);
				if (fromUser == null || toUser == null)
					continue;

				DetailBalanceDTO fromUserBalance = groupBalance.get(fromUser);
				DetailBalanceDTO toUserBalance = groupBalance.get(toUser);

				// Update from user debts → remove the user part
				fromUserBalance.setTotalDebt(fromUserBalance.getTotalDebt() - amount);
				fromUserBalance.getDebtList().merge(toUser, -amount, Double::sum);

				// Update to user receive → remove the user part
				toUserBalance.setTotalReceive(toUserBalance.getTotalReceive() - amount);
				toUserBalance.getReceiveList().merge(fromUser, -amount, Double::sum);
			}

			logger.debug("Group balance after payments:\n{}", groupBalance.entrySet().stream()
					.map(entry -> String.format("\t%s = %s", entry.getKey(), entry.getValue()))
					.collect(Collectors.joining("\n")));

			// Remove debts/receives with 0 value → if expense "have been paid", doesn't show in balance
			for (DetailBalanceDTO balance : groupBalance.values()) {
				balance.getDebtList().values().removeIf(amount -> amount == 0);
				balance.getReceiveList().values().removeIf(amount -> amount == 0);
			}

			logger.debug("Group balance after cleaning:\n{}", groupBalance.entrySet().stream()
					.map(entry -> String.format("\t%s = %s", entry.getKey(), entry.getValue()))
					.collect(Collectors.joining("\n")));
		} catch ( Exception e ) {
			logger.error("ViewBalance: {}", e.getMessage());
			return new ValueResponse<>("Failed to get balance");
		}

		return new ValueResponse<>(groupBalance);
	}

	@Override
	public String toString() {
		return "VIEW_BALANCE " + groupID;
	}
}
