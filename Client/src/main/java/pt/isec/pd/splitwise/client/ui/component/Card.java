package pt.isec.pd.splitwise.client.ui.component;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import pt.isec.pd.splitwise.client.ClientApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Card extends VBox {
	@FXML private HBox headerBox;
	@FXML private Label titleLabel;
	@FXML private Label subtitleLabel;
	@FXML private Label headerDescriptionLabel;
	@FXML private VBox contentBox;
	@FXML private Label descriptionLabel;
	@FXML private HBox footerBox;

	private Card() throws IOException {
		loadFXML();
		titleLabel.setVisible(false);
		titleLabel.setManaged(false);
		subtitleLabel.setVisible(false);
		subtitleLabel.setManaged(false);
		headerDescriptionLabel.setVisible(false);
		headerDescriptionLabel.setManaged(false);
		descriptionLabel.setVisible(false);
		descriptionLabel.setManaged(false);
		footerBox.setVisible(false);
		footerBox.setManaged(false);
	}

	private void loadFXML() throws IOException {
		FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("components/card.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		loader.load();
	}

	public static class Builder {
		private final List<Node> contentNodes = new ArrayList<>();

		private final List<Button> footerButtons = new ArrayList<>();

		private final List<String> styleClasses = new ArrayList<>();

		private String id;

		private String title;

		private String subtitle;

		private String headerDescription;

		private String description;

		private EventHandler<? super MouseEvent> onMouseClicked;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder subtitle(String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public Builder headerDescription(String description) {
			this.headerDescription = description;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder addContent(Node content) {
			this.contentNodes.add(content);
			return this;
		}

		public Builder addButton(Button button) {
			this.footerButtons.add(button);
			return this;
		}

		public Builder addStyleClass(String styleClass) {
			this.styleClasses.add(styleClass);
			return this;
		}

		public Builder onMouseClicked(EventHandler<? super MouseEvent> onMouseClicked) {
			this.onMouseClicked = onMouseClicked;
			return this;
		}

		public Card build() throws IOException {
			Card card = new Card();

			if (id != null) card.setId(id);

			card.getStyleClass().addAll(styleClasses);

			if (title != null) {
				card.titleLabel.setText(title);
				showNode(card.titleLabel);
			}

			if (subtitle != null) {
				card.subtitleLabel.setText(subtitle);
				showNode(card.subtitleLabel);
			}

			if (headerDescription != null) {
				card.headerDescriptionLabel.setText(headerDescription);
				showNode(card.headerDescriptionLabel);
			}

			if (description != null) {
				card.descriptionLabel.setText(description);
				showNode(card.descriptionLabel);
			}

			if (!contentNodes.isEmpty()) card.contentBox.getChildren().addAll(contentNodes);

			if (!footerButtons.isEmpty()) {
				card.footerBox.getChildren().addAll(footerButtons);
				showNode(card.footerBox);
			}

			if (onMouseClicked != null) {
				card.getStyleClass().add("clickable");
				card.setOnMouseClicked(onMouseClicked);
				card.setCursor(Cursor.HAND);
			}

			return card;
		}

		private void showNode(Node node) {
			node.setVisible(true);
			node.setManaged(true);
		}
	}
}
