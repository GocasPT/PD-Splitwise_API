package pt.isec.pd.splitwise.client.ui.manager;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pt.isec.pd.splitwise.client.ClientApp;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.controller.ControllerFactory;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.io.IOException;

public class ViewManager {
	private final Stage primaryStage;

	private final ModelManager modelManager;

	private final ProgressIndicator loadingIndicator;

	public ViewManager(Stage primaryStage, ModelManager modelManager) {
		this.primaryStage = primaryStage;
		this.primaryStage.getIcons().add(new Image(ClientApp.class.getResource("img/logo.png").toString()));
		this.modelManager = modelManager;

		loadingIndicator = new ProgressIndicator();
		loadingIndicator.setVisible(false);
	}

	public void showView(String viewName) {
		try {
			Parent view = loadView(viewName);
			Scene scene = primaryStage.getScene();
			if (scene == null) {
				scene = new Scene(new StackPane(view, loadingIndicator));
				primaryStage.setScene(scene);
			} else scene.setRoot(new StackPane(view, loadingIndicator));
			primaryStage.show();
		} catch ( Exception e ) {
			loadingIndicator.setVisible(false);
			showError("Failed to show " + viewName + ": " + e);
		}
	}

	private Parent loadView(String viewName) throws IOException {
		String fxmlPath = "views/" + viewName + ".fxml";
		FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource(fxmlPath));
		loader.setControllerFactory(this::createController);
		return loader.load();
	}

	public void showError(String message) {
		//TODO: Implement error dialog
		System.out.println("Error: " + message);
	}

	private BaseController createController(Class<?> type) {
		return ControllerFactory.getController(type.asSubclass(BaseController.class), this, modelManager);
	}

	public void sendRequestAsync(Request request, HandlerResponseInterface handleResponse) {
		loadingIndicator.setVisible(true);

		Task<Response> requestTask = new Task<>() {
			@Override
			protected Response call() {
				return modelManager.sendRequest(request);
			}
		};

		requestTask.setOnSucceeded(e -> {
			Response response = requestTask.getValue();
			handleResponse.func(response);
			loadingIndicator.setVisible(false);
		});

		requestTask.setOnFailed(e -> {
			loadingIndicator.setVisible(false);
			showError("Request failed: " + requestTask.getException().getMessage());
		});

		Thread requestThread = new Thread(requestTask);
		requestThread.setDaemon(true);
		requestThread.start();
	}

	public interface HandlerResponseInterface {
		void func(Response response);
	}
}
