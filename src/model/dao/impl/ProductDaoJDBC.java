package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mysql.jdbc.Statement;

import db.DB;
import db.DBException;
import model.dao.ProductDao;
import model.entities.Product;

public class ProductDaoJDBC implements ProductDao {

	private Connection conn;
	private int row = 0;

	public ProductDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Product product) {

		PreparedStatement st = null;

		try {

			conn.setAutoCommit(false);
			st = conn.prepareStatement("INSERT INTO products (name, price, quantity)  VALUES (?,?,?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, product.getName());
			st.setDouble(2, product.getPrice());
			st.setInt(3, product.getQuantity());

			List<Product> list = findAll().stream().filter(x -> x.equals(product)).collect(Collectors.toList());

			if (list.isEmpty()) {
				row = st.executeUpdate();

				if (row > 0) {
					ResultSet rs = st.getGeneratedKeys();
					if (rs.next()) {
						product.setId(rs.getInt(1));
					}

					DB.closeResultSet(rs);
				}

			}

			else {
				throw new DBException("Product is already registered in database.");
			}

			conn.commit();

		}

		catch (

		SQLException e) {
			try {
				conn.rollback();
				throw new DBException("Error: " + e.getMessage());
			} catch (SQLException e1) {

				throw new DBException("Error in try to rollback. Caused by: " + e.getMessage());
			}
		}

		finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void update(Product product) {

		PreparedStatement st = null;

		try {

			conn.setAutoCommit(false);
			st = conn.prepareStatement("UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?");

			st.setString(1, product.getName());
			st.setDouble(2, product.getPrice());
			st.setInt(3, product.getQuantity());
			st.setInt(4, product.getId());

			row = st.executeUpdate();
			System.out.println("rows affected: " + row);
			conn.commit();
		}

		catch (SQLException e) {
			try {
				conn.rollback();
				throw new DBException("Error: " + e.getMessage());
			}

			catch (SQLException e1) {
				throw new DBException("Error in try to rollback. Caused by: " + e1.getMessage());
			}

		}

		finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void delete(Integer id) {

		PreparedStatement st = null;

		try {

			conn.setAutoCommit(false);
			st = conn.prepareStatement("DELETE FROM products WHERE id = ?");
			st.setInt(1, id);
			row = st.executeUpdate();
			conn.commit();
		}

		catch (SQLException e) {
			try {
				conn.rollback();
				throw new DBException("Error: " + e.getMessage());
			}

			catch (SQLException e1) {
				throw new DBException("Error in try to rollback. Caused by: " + e1.getMessage());
			}

		}
	}

	@Override
	public List<Product> findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;
		List<Product> list = new ArrayList<>();

		try {

			st = conn.prepareStatement("SELECT * FROM  products WHERE id = ?");
			st.setInt(1, id);

			rs = st.executeQuery();

			while (rs.next()) {

				list.add(new Product(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"),
						rs.getInt("quantity")));
			}

			return list;
		}

		catch (SQLException e) {
			throw new DBException("Error" + e.getMessage());
		}

		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Product> findAll() {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			List<Product> list = new ArrayList<>();
			st = conn.prepareStatement("SELECT * FROM products");
			rs = st.executeQuery();

			while (rs.next()) {
				list.add(new Product(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"),
						rs.getInt("quantity")));
			}

			return list;
		}

		catch (SQLException e) {
			throw new DBException("Error: " + e.getMessage());
		}

		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
