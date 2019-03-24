package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import exception.Errors;
import exception.Error;
import main.Trade;
import util.AuctionRecorder;

public abstract class Database {
	Trade plugin;
	Connection connection;
	public int tokens = 0;

	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

	public Database(Trade instance) {
		plugin = instance;
	}

	public abstract Connection getSQLConnection();

	public abstract void load();

	public void initialize() {
		connection = getSQLConnection();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Product WHERE id = ?");
			ps.setString(1, "");
			ResultSet rs = ps.executeQuery();
			close(ps, rs);
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
	}
	
	public String getDisplayName(String uuid) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = 
				"SELECT name FROM User WHERE uuid = ?;";
		AuctionRecorder.recordAuction("query", query);

		try {
			conn = getSQLConnection();
			
			ps = conn.prepareStatement(query);
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			return rs.getString("name");
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
	
	public void registPlayer(Player player) {
		String id = player.getUniqueId().toString();
		String name = player.getDisplayName();

		Connection conn = null;
		PreparedStatement ps = null;
		String query =
				"INSERT OR REPLACE INTO User (uuid,name) VALUES(?,?);";
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);

			ps.setString(1, id);
			ps.setString(2, name);

			ps.executeUpdate();
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
	}
	
	
	// FLAG QUERY_REGI_ITEM
	public int registItem(Player player, String item, String price, String material) {// 1= success 0= fail
		String playerID = player.getUniqueId().toString();
		int priceInt = Integer.parseInt(price);
		Date creationDate = new Date();
		String creationStirng = format.format(creationDate);

		Connection conn = null;
		PreparedStatement ps = null;
		String query = 
				"INSERT INTO Product (uuid,item,price,material,creation_time) VALUES(?,?,?,?,?)";
		AuctionRecorder.recordAuction("query", query);

		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);

			ps.setString(1, playerID);
			ps.setString(2, item);
			ps.setInt(3, priceInt);
			ps.setString(4, material);
			ps.setString(5, creationStirng);

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
	
	// FLAG QUERY_SELE_ITEM
	public String selectItem(String id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = 
				"SELECT item FROM Product WHERE id = ?;";
		AuctionRecorder.recordAuction("query", query);

		try {
			conn = getSQLConnection();
			
			ps = conn.prepareStatement(query);
			ps.setString(1, id);
			
			rs = ps.executeQuery();

			return rs.getString("item");
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

	// FLAG QUERY_SET_STATUS
	public int setStatus(String id, int status) {
		Connection conn = null;
		PreparedStatement ps = null;

		String query = 
				"UPDATE Product SET status = ? WHERE id = ?;";
		AuctionRecorder.recordAuction("query", query);

		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, String.valueOf(status));
			ps.setString(2, String.valueOf(status));

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

	// FLAG QUERY_DELE_ITEM
	public int deleteItem(String id) {
		Connection conn = null;
		PreparedStatement ps = null;

		String query = 
				"DELETE FROM Product WHERE id = ?;";

		AuctionRecorder.recordAuction("query", query);
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, id);

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
	
	
	

	//FLAG QUERY_ITEM_ALL
	public List<Product> listItemAll(int page) {
		int selectColumn = (page-1)*45-1;
		if(selectColumn<0) selectColumn=0;
		
		String query = 
				"SELECT * FROM Product WHERE id NOT IN"
				+" (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" ORDER BY id DESC LIMIT 45;";

		AuctionRecorder.recordAuction("query", query);

		return queryToProduct(query);
	}
	
	//FLAG QUERY_ITEM_ALL_WITHOUT_USER
	public List<Product> listItemAll(int page, Player user) {
		int selectColumn = (page - 1) * 45 - 1;
		if (selectColumn < 0)
			selectColumn = 0;

		Date time = new Date();
		int period = Trade.instance.getConfig().getInt("Regist_period");

		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.HOUR, -period);

		Date timePast = cal.getTime();

		String userid = user.getUniqueId().toString();
		
		String query = 
				"SELECT * FROM Product WHERE id"
				+" NOT IN (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" AND uuid != \""+userid+"\""
				+" AND status = 0"
				+" AND creation_time BETWEEN "
				+"\""+format.format(timePast.getTime())+"\" AND \""+format.format(time.getTime())+"\""
				+" ORDER BY id DESC LIMIT 45;";
		
		AuctionRecorder.recordAuction("query", query);

		return queryToProduct(query);
	}
	
	//FLAG QUERY_ITEM_USER
	public List<Product> listItemUser(Player player,int page) {
		int selectColumn = (page-1)*45-1;
		if(selectColumn<0) selectColumn=0;		
		
		String id = player.getUniqueId().toString();
		
		String query = 
				"SELECT * FROM Product WHERE id NOT IN"
				+" (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" AND uuid = \""+ id + "\""
				+" ORDER BY id DESC LIMIT 45;";

		AuctionRecorder.recordAuction("query", query);
		
		return queryToProduct(query);
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
	}// FLAG QUERY___________________________________________
	
	List<Product> queryToProduct(String query) {
		List<Product> productList = new ArrayList<Product>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {
				Product product = new Product();
				product.setId(rs.getInt("id"));
				product.setCreation_time(rs.getString("creation_time"));
				product.setItem(rs.getString("item"));
				product.setUUID(rs.getString("uuid"));
				product.setPrice(rs.getInt("price"));
				product.setStatus(rs.getInt("status"));
				product.setMaterial(rs.getString("material"));
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
	
}




















