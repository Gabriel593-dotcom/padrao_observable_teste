package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Product;
import model.services.ProductService;

public class MainViewController implements Initializable, DataChangeListener {

	private ProductService service = new ProductService();

	public void setService(ProductService service) {
		this.service = service;
	}

	@FXML
	private TableView<Product> tableViewProduct;

	@FXML
	private TableColumn<Product, Integer> tableColumnId;

	@FXML
	private TableColumn<Product, String> tableColumnName;

	@FXML
	private TableColumn<Product, Double> tableColumnPrice;

	@FXML
	private TableColumn<Product, Integer> tableColumnQuantity;

	@FXML
	private TableColumn<Product, Product> tableColumnEDIT;

	@FXML
	private TableColumn<Product, Product> tableColumnDELETE;

	@FXML
	private Button btNew;

	private ObservableList<Product> obsListProduct;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		System.out.println("onBtNewAction");
		Stage parentStage = Utils.currentStage(event);
		Product product = new Product();
		loadRegisterForm(product, "/gui/RegisterForm.fxml", parentStage);

	}

	public TableView<Product> getTableView() {
		return tableViewProduct;
	}

	// Método que carrega o form de registro.
	private void loadRegisterForm(Product product, String absoluteName, Stage parentStage) {

		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			AnchorPane pane = loader.load();

			RegisterFormController controller = loader.getController();
			controller.setProduct(product);
			controller.setService(new ProductService());

			controller.updateRegisterForm();
			controller.subscribeDataChangeListener(this);
			Stage stage = new Stage();
			stage.setScene(new Scene(pane));
			stage.setTitle("Enter with datas to register a new product.");
			stage.setResizable(false);
			stage.initOwner(parentStage);
			stage.initModality(Modality.WINDOW_MODAL);

			stage.showAndWait();
		}

		catch (IOException e) {
			Alerts.showAlert("Error", "Error in try to show Register Form.", e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}

	private void initializeNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		tableColumnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
	}

	public void updateTableView() {

		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		obsListProduct = FXCollections.observableArrayList(service.findAll());
		tableViewProduct.setItems(obsListProduct);
		initEditButtons();
		initDeleteButtons();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Product, Product>() {

			Button button = new Button("edit");

			@Override
			protected void updateItem(Product prod, boolean empty) {
				super.updateItem(prod, empty);
				if (prod == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(
						event -> loadRegisterForm(prod, "/gui/RegisterForm.fxml", Utils.currentStage(event)));
			}

		});
	}

	private void initDeleteButtons() {
		tableColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDELETE.setCellFactory(param -> new TableCell<Product, Product>() {
			Button button = new Button("remove");

			@Override
			protected void updateItem(Product prod, boolean empty) {
				super.updateItem(prod, empty);

				if (prod == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(event -> removeProduct(prod));
			}
		});
	}

	protected void removeProduct(Product prod) {

		Optional<ButtonType> result = Alerts.showConfirmationAlert("Confirmation", "Are you sure to delete?");

		if (result.get() == ButtonType.OK) {

			if (service == null) {
				throw new IllegalStateException("Service was null.");
			}

			service.delete(prod);
			updateTableView();

		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		updateTableView();
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

}
