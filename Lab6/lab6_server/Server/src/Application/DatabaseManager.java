package Application;

//STEP 1. Import required packages
        import java.sql.DriverManager;
        import java.sql.Connection;
        import java.sql.SQLException;

public class DatabaseManager {

    //  Database credentials
    static final String DB_URL = "jdbc:postgresql://pg:5432/studs";
    static final String USER = "s311294";
    static final String PASS = "vzm803";

    public static void main(String[] argv) {

        System.out.println("Testing connection to PostgreSQL JDBC");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
    }
}