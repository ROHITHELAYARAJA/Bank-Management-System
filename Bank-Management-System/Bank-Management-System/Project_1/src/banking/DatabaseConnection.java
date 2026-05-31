package banking;


import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String url = "jdbc:mysql://localhost:3306/bank";
    private static final String user = "root";
    private static final String password = "root";
    public static Connection getConnection() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);

        }
        catch(Exception e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
}
