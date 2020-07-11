package model.dao;

import db.DB;
import model.dao.impl.ProductDaoJDBC;

public class DaoFactory {

	// Método que injeta uma depência de ProductDaoJDBC.
	public static ProductDao createProduct() {
		return new ProductDaoJDBC(DB.getConnection());
	}

}
