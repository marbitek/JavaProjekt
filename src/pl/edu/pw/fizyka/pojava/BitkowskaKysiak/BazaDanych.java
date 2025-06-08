package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class BazaDanych implements AutoCloseable {
	
	protected String nick = "";
	private static final String url    = "jdbc:mysql://db4free.net/marbitka";
    private static final String user   = "marbitka";
    private static final String pass   = "%D_gZ8cm3MU@9G.";
    private Connection conn = null;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	public BazaDanych() throws SQLException {
		this.connect();
		this.initialize();
		
		
	}
	
	public void connect() throws SQLException {
		
			try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	        	throw new SQLException("Brak sterownika JDBC MySQL w classpath!", e);
	        }
			conn = DriverManager.getConnection(url, user, pass);
				  
	}
	
	public void initialize() {  
		try {
			Statement statement = conn.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS`users` ("+
					  "`Id` int(6) unsigned NOT NULL auto_increment,"+
					  "`Last_usage` date default NULL,"+
					  "`Nickname` varchar(100) default NULL,"+
					  "PRIMARY KEY  (`Id`)"+
					") ;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public StringBuilder listOfUsers() {
		StringBuilder out = new StringBuilder();
        try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM users");) { //w miejsce ? zostanie wstsawiona moja liczba, cześciowo skompilowane
        	//PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE usd < ?")) { //w miejsce ? zostanie wstsawiona moja liczba, cześciowo skompilowane
                //pst.setDouble(1, tHold); //pierwszy znak zapytania - tHold, jeśli by był drugi to 2-coś
                try (ResultSet rs = pst.executeQuery()) {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();

                    // Nagłówki kolumn
                    for (int i = 1; i <= cols; i++) {
                        String name = md.getColumnName(i);
                        out.append(i == 2
                                ? String.format("%-20s", name) //jeśli data to rozszerzyć 
                                : String.format("%-8s", name)); //jeśli nie to wszystko tak samo
                    }
                    out.append("\n");
                    out.append("-".repeat(15 + (cols - 1) * 15)).append("\n");

                    // Wiersze danych
                    while (rs.next()) {
                        for (int i = 1; i <= cols; i++) {
                            String cell = rs.getString(i);
                            out.append(i == 2
                                    ? String.format("%-20s", cell)
                                    : String.format("%-8s", cell));
                        }
                        out.append("\n");
                    }
                
            }
        } catch (SQLException sqle) {
            out.append("Błąd SQL: ")
               .append(sqle.getMessage()).append("\n");
        }
		return out;
	}
		
	 public void addUser(String nickname) throws SQLException {
	        String sql = "INSERT INTO users (last_usage, nickname) VALUES (?, ?)";
	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setTimestamp(1, date());
	            ps.setString(2, nickname);
	            ps.executeUpdate();
	        }
	    }
		
		public Timestamp date() {
			String formattedDate = LocalDateTime.now().format(FORMATTER);
	        LocalDateTime parsedDate = LocalDateTime.parse(formattedDate, FORMATTER);
	        return Timestamp.valueOf(parsedDate);
		}

		@Override
		public void close() throws SQLException {
			if (conn != null && !conn.isClosed()) {
	            conn.close();
	        }
		}
		




}