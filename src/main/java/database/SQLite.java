package database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import main.Trade;

public class SQLite extends Database{
    String dbname;
    public SQLite(Trade instance){
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "Auction"); // Set the table name here e.g player_kills
    }

    public String ProductTable = 
    		"CREATE TABLE IF NOT EXISTS Product (" +
            "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
            ",`creation_time` DATETIME NOT NULL"+
            ",`status` INTEGER NOT NULL"+//0 = 판매중/판매실패(기간이 지났을경우) , 1 = 판매완료, 2 = 판매중지
            ",`uuid` TEXT NOT NULL"+
            ",`item` TEXT NOT NULL"+
            ",`price` REAL NOT NULL"+
            ",`material` TEXT NOT NULL"+
            ");";
    public String RecordTable = 
    		"CREATE TABLE IF NOT EXISTS Record ("+
    				"`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
    				",`buyer` TEXT NOT NULL"+
    				",`seller` TEXT NOT NULL"+
    				",`price` REAL NOT NULL"+
            ",`item` TEXT NOT NULL"+
            ",`material` TEXT NOT NULL"+
            ",`trading_time` DATETIME NOT NULL"+
    				");";
    
    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();

            s.executeUpdate(ProductTable);
            s.executeUpdate(RecordTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}