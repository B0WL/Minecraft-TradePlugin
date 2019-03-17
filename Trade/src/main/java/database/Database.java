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
import intenrnal.AuctionRecorder;
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
	    Date creationDate = new Date();
	    String creationStirng = format.format(creationDate);
	    

		Connection conn = null;
		PreparedStatement ps = null;
		String query = 
				"INSERT INTO " + table 
				+ " (owner,item,price,creation_time) VALUES(?,?,?,"+"\""+creationStirng+"\""+")";
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			
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
	public String selectItem(String id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs =null;
		
		String query = 
				"SELECT item FROM "+table
				+" WHERE id = "+id
				+";";
		AuctionRecorder.recordAuction("query", query);
		
		try {
			conn= getSQLConnection();
			ps =conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				return rs.getString("item");
			}
			
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
	
	public int buyItem(String id) {
		Connection conn = null;
		PreparedStatement ps = null;
		
		String query = 
				"UPDATE "+table+
				" SET sold = 1"
				+" WHERE id = "+id
				+";";
		
		AuctionRecorder.recordAuction("query", query);
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			
			ps.executeUpdate();
			
			return 1;
		}catch (SQLException e) {
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
	
	
	public int deleteItem(String id) {
		Connection conn = null;
		PreparedStatement ps = null;
		
		String query = 
				"DELETE FROM "+table
				+" WHERE id = "+id
				+";";
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			
			ps.executeUpdate();
			
			return 1;
		}catch (SQLException e) {
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
	
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
	
	public List<Product> listItemAll(int page) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<Product> productList = new ArrayList<Product>();
		int selectColumn = (page-1)*45-1;
		if(selectColumn<0) selectColumn=0;
		
		Date time = new Date();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.DATE, -3);
		
		Date timePast = cal.getTime();
		
		String query = 
				"SELECT * FROM " + table 
				+" WHERE id NOT IN"
				+" (SELECT id FROM " + table
				+" ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" AND sold = 0"
				+" AND creation_time BETWEEN "
				+"\""+format.format(timePast.getTime())+"\" AND \""+format.format(time.getTime())+"\""
				+" ORDER BY id DESC LIMIT 45;";
				
        try {
    		conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Product product = new Product();
				product.setId(rs.getInt("id"));
				product.setCreation_time(rs.getString("creation_time"));
				product.setItem(rs.getString("item"));
				product.setOwner(rs.getString("owner"));
				product.setPrice(rs.getInt("price"));
				product.setSold(rs.getInt("sold"));
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
	
	
	public List<Product> listItemUser(Player player,int page) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<Product> productList = new ArrayList<Product>();
		int selectColumn = (page-1)*45-1;
		if(selectColumn<0) selectColumn=0;		
		
		String id = player.getUniqueId().toString();
		
		String query = 
				"SELECT * FROM " + table 
				+" WHERE id NOT IN"
				+" (SELECT id FROM " + table
				+" ORDER BY id DESC LIMIT "+ Integer.toString(selectColumn)+")"
				+" AND owner = \""+ id + "\""
				+" ORDER BY id DESC LIMIT 45;";
				
        try {
    		conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Product product = new Product();
				product.setId(rs.getInt("id"));
				product.setCreation_time(rs.getString("creation_time"));
				product.setItem(rs.getString("item"));
				product.setOwner(rs.getString("owner"));
				product.setPrice(rs.getInt("price"));
				product.setSold(rs.getInt("sold"));
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



















