package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Product;
import model.exceptions.ValidationException;
import model.services.ProductService;

public class RegisterFormController implements Initializable {

	private Product product;

	private ProductService productService;

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setService(ProductService productService) {
		this.productService = productService;
	}

	List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtPrice;

	@FXML
	private TextField txtQuantity;

	@FXML
	private Label labelError;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		System.out.println("onBtSaveAction");

		if (product == null) {
			throw new IllegalStateException("Entitie was null.");
		}

		if (productService == null) {
			throw new IllegalStateException("Service was null.");
		}

		try {
			product = getProduct();
			productService.saveOrUpdate(product);
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		}

		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}

		catch (DBException e) {
			Alerts.showAlert("Error", "Error saving object.", e.getMessage(), AlertType.ERROR);
		}
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		System.out.println("onBtCancelAction");
		Utils.currentStage(event).close();

	}

	// método que captura as informações que estão nos TextFields;
	private Product getProduct() {

		Product prod = new Product();

		ValidationException exception = new ValidationException("Validation error.");

		prod.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "field name can't be empty.");
		}

		prod.setName(txtName.getText());

		if (txtPrice.getText() == null || txtPrice.getText().trim().equals("")) {
			exception.addError("price", "field price can't be empty.");
		}

		else {
			prod.setPrice(Double.parseDouble(txtPrice.getText()));
		}

		if (txtQuantity.getText() == null || txtQuantity.getText().trim().equals("")) {
			exception.addError("quantity", "field quantity can't be empty.");
		}

		else {
			prod.setQuantity(Integer.parseInt(txtQuantity.getText()));
		}

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return prod;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	public void updateRegisterForm() {

		if (product == null) {
			throw new IllegalStateException("Entitie was null.");
		}

		txtId.setText(String.valueOf(product.getId()));
		txtName.setText(product.getName());
		txtPrice.setText(String.valueOf(product.getPrice()));
		txtQuantity.setText(String.valueOf(product.getQuantity()));
	}

	private void setErrorMessages(Map<String, String> errors) {

		Set<String> fields = errors.keySet();

		for (String field : fields) {

			if (fields.contains(field)) {
				labelError.setText(errors.get(field));
			}
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldDouble(txtPrice);
		Constraints.setTextFieldInteger(txtQuantity);

	}
}
