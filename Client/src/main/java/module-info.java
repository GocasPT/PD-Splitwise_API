module pt.isec.pd.client {
	requires static lombok;
	requires Splitwsie.sharedLib.main;
	requires javafx.fxml;
	requires javafx.web;
	requires com.dlsc.formsfx;
	requires net.synedra.validatorfx;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.bootstrapfx.core;
	requires eu.hansolo.tilesfx;
	requires com.dlsc.gemsfx;
	requires com.dlsc.phonenumberfx;
	requires org.controlsfx.controls;
	requires org.kordamp.ikonli.materialdesign;
	requires java.desktop;
	requires org.apache.commons.lang3;

	opens pt.isec.pd.splitwise.client to javafx.fxml;
	opens pt.isec.pd.splitwise.client.model to javafx.fxml;
	opens pt.isec.pd.splitwise.client.ui to javafx.graphics;
	opens pt.isec.pd.splitwise.client.ui.component to javafx.fxml;
	opens pt.isec.pd.splitwise.client.ui.component.dialog to javafx.fxml;
	opens pt.isec.pd.splitwise.client.ui.controller to javafx.fxml;
	opens pt.isec.pd.splitwise.client.ui.controller.view to javafx.fxml;

	exports pt.isec.pd.splitwise.client;
}