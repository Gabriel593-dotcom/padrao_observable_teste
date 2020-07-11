package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.ProductDao;
import model.entities.Product;

public class ProductService {
	private ProductDao dao = DaoFactory.createProduct();

	public List<Product> findAll() {

//      MOCK		
//		List<Product> list = new ArrayList<>();
//		list.add(new Product(1, "Celular", 1250.0, 2));
//
//		return list;

		return dao.findAll();
	}

	public void saveOrUpdate(Product product) {

		if (product.getId() == null) {
			dao.insert(product);
		}

		else {
			dao.update(product);
		}

	}

	public void delete(Product prod) {

		dao.delete(prod.getId());
	}
}
