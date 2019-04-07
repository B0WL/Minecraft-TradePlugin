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
import util.RecordManager;

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

	
	//FLAG QURTY_AVERAGE_FROM_MAT
	public Float getAverageTrading(String material) {
		String query =  
		String.format("SELECT AVG(price) FROM Record WHERE material = \"%s\";", material);
		
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);			
			rs = ps.executeQuery();
			
			return rs.getFloat("AVG(price)");
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
		return 0f;
	}
	
	//FLAG QUERY_LOWEST_FROM_MAT
	public Float getLowestPrice(String material) {
		String query =  
		String.format("SELECT MIN(price) FROM Product WHERE material = \"%s\";", material);
		
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);			
			rs = ps.executeQuery();
			
			return rs.getFloat("MIN(price)");
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
		return 0f;
	}
	//FLAG QUERY_GET_COUNT_FROM_MAT
	public int getProductCountMaterial(String material) {
		String query =
						"SELECT COUNT(*) FROM Record WHERE material = ?;";
		RecordManager.record("query", query);
		
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);			
			ps.setString(1, material);
			rs = ps.executeQuery();
			
			return rs.getInt("COUNT(*)");
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
		return 0;
	}

	//FLAG QUERY_GET_COUNT_FROM_PLAYER
	public int getProductCount(Player player) {
		String uuid = player.getUniqueId().toString();
		String query =
				String.format(
						"SELECT COUNT(*) FROM Product WHERE status = 0 AND uuid = \"%s\";",uuid);
		RecordManager.record("query", query);
		
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);			
			rs = ps.executeQuery();
			
			return rs.getInt("COUNT(*)");
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
		return 0;
	}
	//FLAG QUERY_GET_SELLER
	public String getSeller(String productID) {
		String query =
						"SELECT uuid FROM Product WHERE id = ?;";
		RecordManager.record("query", query);
		
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);			
			ps.setString(1, productID);
			rs = ps.executeQuery();
			
			
			return rs.getString("uuid");
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

	//FLAG QUERY_GET_PRICE
	public Float getPrice(String productID) {
		String query =
						"SELECT price FROM Product WHERE id = ?;";
		RecordManager.record("query", query);
		
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);			
			ps.setString(1, productID);
			rs = ps.executeQuery();
			
			
			return rs.getFloat("price");
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
	
	//FLAG QUERY_GET_SOLDOUT
	public int getSoldOut(String uuid) {
		String query =
						"SELECT COUNT(*) FROM Product WHERE uuid = ? AND status = 1;";
		RecordManager.record("query", query);
		
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, uuid);
			rs = ps.executeQuery();
			
			return rs.getInt("COUNT(*)");
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
		return 0;
	}
	
	//FLAG QUERY_REGI_RECORD
	public int registRecord(Player buyer,String sellerID, String item, Float eachPrice, String material) {
		String buyerID = buyer.getUniqueId().toString();
		Date creationDate = new Date();
		String creationStirng = format.format(creationDate);

		String query = 
				"INSERT INTO Record (buyer,seller,item,price,material,trading_time) VALUES(?,?,?,?,?,?)";
		RecordManager.record("query", query);

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);

			ps.setString(1, buyerID);
			ps.setString(2, sellerID);
			ps.setString(3, item);
			ps.setFloat(4, eachPrice);
			ps.setString(5, material);
			ps.setString(6, creationStirng);

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
	
	
	// FLAG QUERY_REGI_ITEM
	public int registItem(String playerID, String item, Float eachPrice, String material, int status) {// 1= success 0= fail
		
		Date creationDate = new Date();
		String creationStirng = format.format(creationDate);


		String query = 
				"INSERT INTO Product (uuid,item,price,material,creation_time,status) VALUES(?,?,?,?,?,?)";
		RecordManager.record("query", query);

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);

			ps.setString(1, playerID);
			ps.setString(2, item);
			ps.setFloat(3, eachPrice);
			ps.setString(4, material);
			ps.setString(5, creationStirng);
			ps.setInt(6, status);

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
		String query = 
				"SELECT item FROM Product WHERE id = ?;";
		RecordManager.record("query", query);
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
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

		String query = 
				"UPDATE Product SET status = ? WHERE id = ?;";
		RecordManager.record("query", query);

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, String.valueOf(status));
			ps.setString(2, String.valueOf(id));

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
		String query = 
				"DELETE FROM Product WHERE id = ?;";

		RecordManager.record("query", query);
		
		Connection conn = null;
		PreparedStatement ps = null;
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
	
	//FLAG QUERY_ITEM_GROUP_MAT
	public List<Product> listItemGroupMaterial(int page, Player user){
		int selectColumn=0;
		if(page >1)
			selectColumn = (page-1)*45-1;
		
		Date time = new Date();

		int wait = Trade.instance.getConfig().getInt("wait_time");
		int period = Trade.instance.getConfig().getInt("regist_period")*60+wait;

		Calendar calBefore = Calendar.getInstance();
		calBefore.setTime(time);
		calBefore.add(Calendar.MINUTE, -period);
		
		Calendar calAfter = Calendar.getInstance();
		calAfter.setTime(time);
		calAfter.add(Calendar.MINUTE, +wait);

		Date timeBefore = calBefore.getTime();
		Date timeAfter = calAfter.getTime();
		
		String userid = user.getUniqueId().toString();
		
		String query = "SELECT * FROM Product WHERE id"
				+" NOT IN (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" AND uuid != \""+userid+"\""
				+" AND status = 0"
				+" AND creation_time BETWEEN "
				+"\""+format.format(timeBefore.getTime())+"\" AND \""+format.format(timeAfter.getTime())+"\""
				+" GROUP BY material;"
				+" ORDER BY id DESC LIMIT 45";
		RecordManager.record("query", query);
		
		return queryToProduct(query);
	}
	
	//FLAG QUERY_ITEM_GROUP_MAT_ALL
	public List<Product> listItemGroupMaterial(int page){
		int selectColumn=0;
		if(page >1)
			selectColumn = (page-1)*45-1;
		
		String query = "SELECT * FROM Product WHERE id"
				+" NOT IN (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" GROUP BY material;"
				+" ORDER BY id DESC LIMIT 45";
		RecordManager.record("query", query);
		
		return queryToProduct(query);
	}
	
	//FLAG QUERY_TRADING_INFO_ALL
	public List<Product> listRecordGroupMaterial(int page){
		int selectColumn=0;
		if(page >1)
			selectColumn = (page-1)*45-1;
		
		String query = "SELECT * FROM Record WHERE id"
				+" NOT IN (SELECT id FROM Record ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" GROUP BY material;"
				+" ORDER BY id DESC LIMIT 45";
		RecordManager.record("query", query);
		
		return queryToRecord(query);
	}
	

	//FLAG QUERY_ITEM_MANGERLIST
	public List<Product> listItemAll(int page) {
		int selectColumn=0;
		if(page >1)
			selectColumn = (page-1)*45-1;
		
		String query = 
				"SELECT * FROM Product"
				+" WHERE id NOT IN"
				+" (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" ORDER BY id DESC LIMIT 45;";

		RecordManager.record("query", query);

		return queryToProduct(query);
	}
	
	//FLAG QUERY_ITEM_BUYLIST
	public List<Product> listItemAll(int page, Player user) {
		int selectColumn=0;
		if(page >1)
			selectColumn = (page-1)*45-1;

		Date time = new Date();

		int wait = Trade.instance.getConfig().getInt("wait_time");
		int period = Trade.instance.getConfig().getInt("regist_period")*60+wait;

		Calendar calBefore = Calendar.getInstance();
		calBefore.setTime(time);
		calBefore.add(Calendar.MINUTE, -period);
		
		Calendar calAfter = Calendar.getInstance();
		calAfter.setTime(time);
		calAfter.add(Calendar.MINUTE, -wait);

		Date timeBefore = calBefore.getTime();
		Date timeAfter = calAfter.getTime();

		String userid = user.getUniqueId().toString();
		
		String query = 
				"SELECT * FROM Product WHERE id"
				+" NOT IN (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" AND uuid != \""+userid+"\""
				+" AND status = 0"
				+" AND creation_time BETWEEN "
				+"\""+format.format(timeBefore.getTime())+"\" AND \""+format.format(timeAfter.getTime())+"\""
				+" ORDER BY id DESC LIMIT 45;";
		
		RecordManager.record("query", query);

		return queryToProduct(query);
	}
	

	
	
	//FLAG QUERY_ITEM_BUYLIST_BY_MAT
	public List<Product> listItemAll(int page, Player user, String material) {
		int selectColumn=0;
		if(page >1)
			selectColumn = (page-1)*45-1;

		Date time = new Date();

		int wait = Trade.instance.getConfig().getInt("wait_time");
		int period = Trade.instance.getConfig().getInt("regist_period")*60+wait;

		Calendar calBefore = Calendar.getInstance();
		calBefore.setTime(time);
		calBefore.add(Calendar.MINUTE, -period);
		
		Calendar calAfter = Calendar.getInstance();
		calAfter.setTime(time);
		calAfter.add(Calendar.MINUTE, +wait);

		Date timeBefore = calBefore.getTime();
		Date timeAfter = calAfter.getTime();

		String userid = user.getUniqueId().toString();
		
		String query = 
				"SELECT * FROM Product WHERE id"
				+" NOT IN (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" AND uuid != \""+userid+"\""
				+" AND status = 0"
				+" AND material = \""+material+"\""
				+" AND creation_time BETWEEN "
				+"\""+format.format(timeBefore.getTime())+"\" AND \""+format.format(timeAfter.getTime())+"\""
				+" ORDER BY id DESC LIMIT 45;";
		
		RecordManager.record("query", query);

		return queryToProduct(query);
	}
	
	//FLAG QUERY_ITEM_BUYLIST_BY_MAT_ALL
	public List<Product> listItemAll(int page, String material) {
		int selectColumn=0;
		if(page >1)
			selectColumn = (page-1)*45-1;
		
		String query = 
				"SELECT * FROM Product WHERE id"
				+" NOT IN (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" AND material = \""+material+"\""
				+" ORDER BY id DESC LIMIT 45;";
		
		RecordManager.record("query", query);

		return queryToProduct(query);
	}
	
	//FLAG QUERY_ITEM_SELFLIST
	public List<Product> listItemUser(Player player,int page) {
		int selectColumn=0;
		if(page >1)
			selectColumn = (page-1)*45-1;
		
		String id = player.getUniqueId().toString();
		
		String query = 
				"SELECT * FROM Product WHERE id NOT IN"
				+" (SELECT id FROM Product ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" AND uuid = \""+ id + "\""
				+" ORDER BY status DESC LIMIT 45;";

		RecordManager.record("query", query);
		
		return queryToProduct(query);
	}
	
	//FLAG QUERY_RECORD_LIST	
	public List<Product> listRecordALL(int page) {
	int selectColumn=0;
	if(page >1)
		selectColumn = (page-1)*45-1;
	
	String query = 
			"SELECT * FROM Record WHERE id NOT IN"
			+" (SELECT id FROM Record ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
			+" ORDER BY id DESC LIMIT 45;";

	RecordManager.record("query", query);
	
	return queryToRecord(query);
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
	

	List<Product> queryToRecord(String query) {
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
				product.setCreation_time(rs.getString("trading_time"));
				product.setItem(rs.getString("item"));
				product.setSeller(rs.getString("seller"));
				product.setBuyer(rs.getString("buyer"));
				product.setPrice(rs.getFloat("price"));
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
				product.setSeller(rs.getString("uuid"));
				product.setPrice(rs.getFloat("price"));
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




















