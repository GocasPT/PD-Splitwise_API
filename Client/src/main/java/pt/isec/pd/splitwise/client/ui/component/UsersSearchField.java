package pt.isec.pd.splitwise.client.ui.component;

import com.dlsc.gemsfx.SearchField;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;

import java.util.Comparator;
import java.util.stream.Collectors;

public class UsersSearchField extends SearchField<User> {
	private final ListProperty<User> users;

	public UsersSearchField(ListProperty<User> users) {
		this.users = users;

		ObservableList<User> observableList = users.get() != null
				? FXCollections.observableArrayList(users.get())
				: FXCollections.observableArrayList();
		users.set(observableList);

		users.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				updateSuggestionProvider();
			}
		});

		users.addListener((ListChangeListener<User>) change -> {
			while (change.next()) {
				if (change.wasAdded() || change.wasRemoved()) {
					updateSuggestionProvider();
				}
			}
		});

		// suggestion provider
		setSuggestionProvider(request -> this.users.stream()
				.filter(user -> user.getEmail().toLowerCase().contains(request.getUserText().toLowerCase()))
				.collect(Collectors.toList()));

		// converter
		setConverter(new StringConverter<>() {
			@Override
			public String toString(User user) {
				if (user != null) {
					return user.getEmail();
				}
				return "";
			}

			@Override
			public User fromString(String string) {
				return User.builder().email(string).build();
			}
		});

		// matcher
		setMatcher((user, searchText) -> user.getEmail().toLowerCase().startsWith(searchText.toLowerCase()));

		// comparator
		setComparator(Comparator.comparing(User::getEmail));

		// prompt text, so that the user knows what to type
		getEditor().setPromptText("Start typing user email...");
	}

	private void updateSuggestionProvider() {
		setSuggestionProvider(request -> users.stream()
				.filter(user -> user.getEmail().toLowerCase().contains(request.getUserText().toLowerCase()))
				.collect(Collectors.toList()));
	}
}
