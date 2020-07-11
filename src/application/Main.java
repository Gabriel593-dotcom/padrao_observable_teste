package application;

import java.sql.Connection;

import db.DB;
import gui.MainViewController;
import gui.util.Alerts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.entities.Product;
import model.services.ProductService;

public class Main extends Application {

	private static Scene mainScene;

	@Override
	public void start(Stage stage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));

			ScrollPane scrollPane = loader.load();
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);

			MainViewController controller = loader.getController();
			TableView<Product> tableView = controller.getTableView();
			tableView.prefHeightProperty().bind(stage.heightProperty());
			controller.setService(new ProductService());
			mainScene = new Scene(scrollPane);
			stage.setScene(mainScene);

			stage.show();
		}

		catch (Exception e) {
			Alerts.showAlert("Error", "Error in try to run application", e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}

	public static Scene getMainScene() {
		return mainScene;
	}

	public static void main(String[] args) {
		launch(args);
		Connection conn = DB.getConnection();
		DB.closeConnection();

	}
}
