package model.dao;

import db.DB;
import model.dao.impl.ProductDaoJDBC;

public class DaoFactory {

	// M�todo que injeta uma dep�ncia de ProductDaoJDBC.
	public static ProductDao createProduct() {
		return new ProductDaoJDBC(DB.getConnection());
	}

}
