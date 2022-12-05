package Utils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * JDBC Class
 */
public class JDBC {
    //Database location and credentials
    private static final UN = "[Username]";
    private static final PW = "[Password]";
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static String userName = UN;
    private static String password = PW;

    /**
     * The constant connection.
     */
    public static Connection connection;

    /**
     * Open connection.
     */
    public static void openConnection()
    {
        try {
            //Database driver
            Class.forName(driver);
            //Set connection to database
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            System.out.println("Connection successful!");

        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * Close connection.
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }

}
