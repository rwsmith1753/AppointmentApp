import Utils.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Locale;

/**
 * Main
 *
 * @Author Ryan Smith
 * email: rsm1479@wgu.edu
 * student id: 010092517
 */
public class Main extends Application {
    //Global variables
    private static int userID;
    private static String password;

    /**
     * Start Application
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Start at login window
        Parent root = FXMLLoader.load(getClass().getResource("Views/Login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    /**
     * The entry point of application.
     *
     * @param args input arguments
     */
    public static void main(String[] args) {
        //Launch application
        launch();
        //Close database connection on program close
        JDBC.closeConnection();
    }
}