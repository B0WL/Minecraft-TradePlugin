package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import exception.Errors;
import exception.Error;
import main.Trade;
import util.ItemSerializer;




public abstract class Database {
    Trade plugin;
    Connection connection;
    // The name of the table we created back in SQLite class.
    public String table = "Product";
    public int tokens = 0;
    public Database(Trade instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE id = ?");
            ps.setString(1, "");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally {
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
    
    public int listItem() {
    	Connection conn = null;
        PreparedStatement ps = null;
    	
    	
    	
    	
    	return 0;
    }
    

    public void close(PreparedStatement ps,ResultSet rs){
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













