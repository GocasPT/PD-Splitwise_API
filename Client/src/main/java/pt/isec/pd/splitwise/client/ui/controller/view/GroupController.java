package pt.isec.pd.splitwise.client.ui.controller.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.StringUtils;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.component.Card;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Balance.DetailBalanceDTO;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Balance.PreviewBalanceDTO;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Expense.DetailExpenseDTO;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Group.PreviewGroupDTO;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Payment.PreviewPaymentDTO;
import pt.isec.pd.splitwise.sharedLib.network.request.Expense.ExportCSV;
import pt.isec.pd.splitwise.sharedLib.network.request.Expense.GetAllExpenses;
import pt.isec.pd.splitwise.sharedLib.network.request.Expense.GetTotalExpenses;
import pt.isec.pd.splitwise.sharedLib.network.request.Group.GetGroup;
import pt.isec.pd.splitwise.sharedLib.network.request.Payment.GetAllPayments;
import pt.isec.pd.splitwise.sharedLib.network.request.Payment.ViewBalance;
import pt.isec.pd.splitwise.sharedLib.network.response.ListResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupController extends BaseController {
	private final ObjectProperty<EGroupView> groupViewState;

	private final Button btnAdd;
	@FXML public Button btnSettings;
	@FXML private Text txtGroupName;
	@FXML private Button btnExpenses;
	@FXML private Button btnPay;
	@FXML private Button btnBalance;
	@FXML private Button btnTotalSpend;
	@FXML private Button btnExport;
	@FXML private VBox vbInfo;

	public GroupController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
		groupViewState = new SimpleObjectProperty<>(EGroupView.EXPENSES);
		btnAdd = new Button("+");
	}

	@Override
	protected void registerHandlers() {
		btnSettings.setOnAction(e -> {
			try {
				viewManager.showView("settings_view");
			} catch ( Exception ex ) {
				viewManager.showError("Failed to show settings: " + ex.getMessage());
			}
		});

		btnExpenses.setOnAction(e -> groupViewState.set(EGroupView.EXPENSES));
		btnPay.setOnAction(e -> groupViewState.set(EGroupView.PAYMENTS));
		btnBalance.setOnAction(e -> groupViewState.set(EGroupView.BALANCE));
		btnTotalSpend.setOnAction(e -> groupViewState.set(EGroupView.TOTAL_SPEND));
		btnExport.setOnAction(e -> exportPopup());

		groupViewState.addListener((observable, oldValue, newValue) -> {
			if (oldValue == newValue) return;

			//TODO: style buttons
			switch (newValue) {
				case EXPENSES -> fetchExpenses();
				case PAYMENTS -> fetchPayments();
				case BALANCE -> fetchBalance();
				case TOTAL_SPEND -> fetchTotalSpend();
			}
		});
	}

	@Override
	protected void update() {
		viewManager.sendRequestAsync(new GetGroup(modelManager.getGroupInViewId()), (response -> {
			if (!response.isSuccess()) {
				viewManager.showError(response.getErrorDescription());
				viewManager.showView("groups_view");
				return;
			}

			if (response instanceof ValueResponse<?> valueResponse) {
				if (valueResponse.getValue() instanceof PreviewGroupDTO group) {
					txtGroupName.setText(group.getName());
				} else {
					viewManager.showError("Failed to get group value");
				}
			} else {
				viewManager.showError("Failed to cast response to ValueResponse");
			}

			fetchExpenses();
		}));
	}

	@Override
	protected void handleResponse(Response response) {
	}

	private void exportPopup() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		File outputFolder = directoryChooser.showDialog(vbInfo.getScene().getWindow());
		if (outputFolder != null)
			viewManager.sendRequestAsync(new ExportCSV(modelManager.getGroupInViewId()), (response -> {
				if (!response.isSuccess()) {
					viewManager.showError(response.getErrorDescription());
					return;
				}

				if (response instanceof ValueResponse<?> valueResponse) {
					if (valueResponse.getValue() instanceof File file) {
						System.out.println("File: " + file.getAbsolutePath());
						System.out.println("Output: " + outputFolder.getAbsolutePath());
						System.out.println("Conent: " + file.getName());

						File outputFile = new File(outputFolder, file.getName());
						try {
							Files.copy(file.toPath(), outputFile.toPath());
						} catch ( IOException e ) {
							viewManager.showError("Failed to save CSV file: " + e);
						}
					}
				}
			}));

	}

	private void fetchExpenses() {
		viewManager.sendRequestAsync(new GetAllExpenses(modelManager.getGroupInViewId()), (response -> {
			if (!response.isSuccess()) {
				viewManager.showError(response.getErrorDescription());
				viewManager.showView("groups_view");
				return;
			}

			vbInfo.getChildren().clear();

			btnAdd.setOnAction(e -> {
				try {
					viewManager.showView("add_expense_view");
				} catch ( Exception ex ) {
					viewManager.showError("Failed to show add expense: " + ex.getMessage());
				}
			});
			vbInfo.getChildren().add(btnAdd);

			if (response instanceof ListResponse<?> listResponse) {
				if (listResponse.isEmpty()) {
					vbInfo.getChildren().add(new Label("No expenses on this group"));
					return;
				}

				if (listResponse.getList() instanceof DetailExpenseDTO[] expenses) {
					try {
						for (DetailExpenseDTO expense : expenses)
							//TODO: add month separator (new month → new separator)
							vbInfo.getChildren().add(
									new Card.Builder()
											.id("expense-card")
											.title(expense.getAmount() + "€")
											.subtitle(expense.getTitle())
											.description(expense.getDate().toString() + " - " + expense.getPayerUser())
											.onMouseClicked(
													e -> {
														try {
															modelManager.setExpenseInViewId(expense.getId());
															viewManager.showView("expense_view");
														} catch ( Exception ex ) {
															viewManager.showError(
																	"Failed to show expense: " + ex.getMessage());
														}
													}).addStyleClass("expense-card").build());
					} catch ( IOException e ) {
						viewManager.showError("Failed to fetch expenses: " + e.getMessage());
					}
				} else {
					viewManager.showError("Failed to get expenses list");
				}
			} else {
				viewManager.showError("Failed to cast response to ListResponse");
			}
		}));
	}

	private void fetchPayments() {
		viewManager.sendRequestAsync(new GetAllPayments(modelManager.getGroupInViewId()), (response -> {
			if (!response.isSuccess()) {
				viewManager.showError(response.getErrorDescription());
				return;
			}

			vbInfo.getChildren().clear();

			btnAdd.setOnAction(e -> {
				try {
					viewManager.showView("add_payment_view");
				} catch ( Exception ex ) {
					viewManager.showError("Failed to show add payment: " + ex.getMessage());
				}
			});
			vbInfo.getChildren().add(btnAdd);

			if (response instanceof ListResponse<?> listResponse) {
				if (listResponse.isEmpty()) {
					vbInfo.getChildren().add(new Label("No payments on this group"));
					return;
				}

				if (listResponse.getList() instanceof PreviewPaymentDTO[] payments) {
					try {
						for (PreviewPaymentDTO payment : payments)
							vbInfo.getChildren().add(
									new Card.Builder().id("payment-card")
											.title(payment.getAmount() + "€")
											.subtitle(payment.getReceiverUser() + " -> " + payment.getPayerUser())
											.description(payment.getDate().toString())
											.onMouseClicked(
													e -> {
														try {
															modelManager.setExpenseInViewId(payment.getId());
															viewManager.showView("payment_view");
														} catch ( Exception ex ) {
															viewManager.showError(
																	"Failed to show expense: " + ex.getMessage());
														}
													}).addStyleClass("payment-card")
											.build());
					} catch ( IOException e ) {
						viewManager.showError("Failed to create card: " + e.getMessage());
					}
				} else
					viewManager.showError("Failed to get payments list");
			} else
				viewManager.showError("Failed to cast response to ListResponse");
		}));
	}

	private void fetchBalance() {
		viewManager.sendRequestAsync(new GetTotalExpenses(modelManager.getGroupInViewId()), (response -> {
			if (!response.isSuccess()) {
				viewManager.showError(response.getErrorDescription());
				return;
			}

			vbInfo.getChildren().clear();

			PieChart pieChart = new PieChart();
			pieChart.setLabelsVisible(false);
			Label caption = new Label("");

			if (response instanceof ValueResponse<?> valueResponse)
				if (valueResponse.getValue() instanceof PreviewBalanceDTO balance) {
					for (Map.Entry<String, Double> entry : balance.getUsersBalance().entrySet())
						pieChart.getData().add(new PieChart.Data(entry.getKey(),
						                                         entry.getValue())); //TODO: improve this (onHover → percentage?)

					for (PieChart.Data data : pieChart.getData())
						data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
							caption.setTextFill(Color.DARKORANGE);
							caption.setStyle("-fx-font: 24 arial;");
							caption.setTranslateX(e.getSceneX());
							caption.setTranslateY(e.getSceneY());
							caption.setText(data.getPieValue() + "%");
						});

					//TODO: add style to this label
					Label lblTotalExpenses = new Label("Total expenses: " + balance.getTotalBalance() + "€");
					lblTotalExpenses.setStyle("-fx-font: 24 arial;"); //TODO: add class?

					vbInfo.getChildren().addAll(lblTotalExpenses, pieChart, caption);
				} else viewManager.showError("Failed to get balance value");
			else viewManager.showError("Failed to cast response to ValueResponse");
		}));
	}

	private void fetchTotalSpend() {
		viewManager.sendRequestAsync(new ViewBalance(modelManager.getGroupInViewId()), (response -> {
			if (!response.isSuccess()) {
				viewManager.showError(response.getErrorDescription());
				return;
			}

			if (response instanceof ValueResponse<?> valueResponse)
				if (valueResponse.getValue() instanceof Map<?, ?> balance) {
					vbInfo.getChildren().clear();

					for (Map.Entry<String, DetailBalanceDTO> entry : ((Map<String, DetailBalanceDTO>) balance).entrySet()) {
						String userEmail = entry.getKey();
						DetailBalanceDTO userBalance = entry.getValue();
						List<Map<String, Double>> debts = new ArrayList<>();
						List<Map<String, Double>> receive = new ArrayList<>();

						userBalance.getDebtList().forEach((k, v) -> debts.add(Map.of(k, v)));
						userBalance.getReceiveList().forEach((k, v) -> receive.add(Map.of(k, v)));

						try {
							Card userCard = new Card.Builder().id("user-expense-card").title(userEmail).subtitle(
									"Total expense: " + userBalance.getTotalExpended() + "€").addContent(
									//TODO: style this (red)
									new VBox(new Label("Debts"),
									         new Label("Total debts: " + userBalance.getTotalDebt() + "€"), new Label(
											(debts.isEmpty() ? "No debts :)" : StringUtils //TODO: add bullets points (see if can be improved)
													.join(debts.stream().map(Map::entrySet).map(set -> set.stream().map(
															e -> "•" + e.getKey() + ": " + e.getValue() + "€").findFirst().orElse(
															"")).toList(), "\n"))))).addContent(
									//TODO: style this (green)
									new VBox(new Label("Receive"),
									         new Label("Total receive: " + userBalance.getTotalReceive() + "€"),
									         new Label(
											         (receive.isEmpty() ? "No receives :(" : StringUtils //TODO: add bullets points (see if can be improved)
													         .join(receive.stream().map(Map::entrySet).map(
															         set -> set.stream().map(
																	         e -> "•" + e.getKey() + ": " + e.getValue() + "€").findFirst().orElse(
																	         "")).toList(), "\n"))))).build();

							vbInfo.getChildren().add(userCard);
						} catch ( IOException e ) {
							viewManager.showError("Fail to create user card: " + e.getMessage());
						}
					}
				} else
					viewManager.showError("Failed to get balance value");
			else
				viewManager.showError("Failed to cast response to ValueResponse");
		}));
	}

	private enum EGroupView {EXPENSES, PAYMENTS, BALANCE, TOTAL_SPEND}
}
