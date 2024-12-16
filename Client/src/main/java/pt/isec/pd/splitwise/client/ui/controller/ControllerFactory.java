package pt.isec.pd.splitwise.client.ui.controller;

import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.controller.view.*;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;

public class ControllerFactory {
	public static BaseController getController(Class<? extends BaseController> controllerType, ViewManager viewManager, ModelManager modelManager) {
		return switch (controllerType.getSimpleName()) {
			case "LoginController" -> new LoginController(viewManager, modelManager);
			case "RegisterController" -> new RegisterController(viewManager, modelManager);
			case "NavBarController" -> new NavBarController(viewManager, modelManager);
			case "GroupsController" -> new GroupsController(viewManager, modelManager);
			case "InvitesController" -> new InvitesController(viewManager, modelManager);
			case "GroupController" -> new GroupController(viewManager, modelManager);
			case "ExpenseController" -> new ExpenseController(viewManager, modelManager);
			case "AddExpenseController" -> new AddExpenseController(viewManager, modelManager);
			case "PaymentController" -> new PaymentController(viewManager, modelManager);
			case "AddPaymentController" -> new AddPaymentController(viewManager, modelManager);
			case "UserController" -> new UserController(viewManager, modelManager);
			case "SettingsController" -> new SettingsController(viewManager, modelManager);
			default -> throw new IllegalArgumentException("Unknown controller type: " + controllerType.getName());
		};
	}
}
