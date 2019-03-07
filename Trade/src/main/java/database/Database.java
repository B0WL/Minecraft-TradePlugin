package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import exception.Errors;
import exception.Error;
import main.Trade;

public abstract class Database {
	Trade plugin;
	Connection connection;
	// The name of the table we created back in SQLite class.
	public String table = "Product";
	public int tokens = 0;

	public Database(Trade instance) {
		plugin = instance;
	}

	public abstract Connection getSQLConnection();

	public abstract void load();

	public void initialize() {
		connection = getSQLConnection();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE id = ?");
			ps.setString(1, "");
			ResultSet rs = ps.executeQuery();
			close(ps, rs);

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
	}

	public int registItem(Player player, String item, String price) {// 1= success 0= fail
		String playerID = player.getUniqueId().toString();
		int priceInt = Integer.parseInt(price);

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("INSERT INTO " + table + " (owner,item,price) VALUES(?,?,?)");
			ps.setString(1, playerID);
			ps.setString(2, item);
			ps.setInt(3, priceInt);
			ps.executeUpdate();
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return 0;
	}

	public List<Product> listItemAll(int page) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<Product> productList = new ArrayList<Product>();
		int selectColumn = (page-1)*45-1;
		if(selectColumn<0) selectColumn=0;
		
        try {
    		conn = getSQLConnection();
			ps = conn.prepareStatement(
					"SELECT * FROM " + table 
					+" WHERE id NOT IN"
					+" (SELECT id FROM " + table
					+" ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
					+" ORDER BY id DESC LIMIT 45;");
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				
				Product product = new Product();
				product.setId(rs.getInt("id"));
				product.setCreation_time(rs.getString("creation_time"));
				product.setItem(rs.getString("item"));
				product.setOwner(rs.getString("owner"));
				product.setPrice(rs.getInt("price"));
				
				productList.add(product);
				
			}
			return productList;			
		} catch (SQLException ex) {
			 plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }

		

		return null;
	}

	public void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			Error.close(plugin, ex);
		}
	}
	
	
	
}



















