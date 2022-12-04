package Controllers;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import Utils.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * Login controller.
 */
public class LoginController implements Initializable {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    public Button cancelBtn;
    @FXML
    public Text Zone;
    @FXML
    public Button signInBtn;
    @FXML
    public Text LoginText;
    @FXML
    private TextField Password;
    @FXML
    private TextField Username;
    @FXML
    private Text loginError;

    //Static database connection
    private static final Connection conn = JDBC.connection;
    private static final String query = "SELECT User_ID FROM USERS WHERE User_Name = ? AND Password = ?";
    //Static username for use throughout application
    private static int userID;
    private static String username = null;
    private static String password = null;

    /**
     * Initialize Login Window
     *
     * @param location
     * @param resources
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        //get user's locale
        Locale locale = getLocale();
        //Point ResourceBundle to languages resources
        ResourceBundle rb = ResourceBundle.getBundle("Properties/rb", locale);
        LoginText.setText(rb.getString("Login"));
        Username.setPromptText(rb.getString("Username"));
        Password.setPromptText(rb.getString("Password"));
        loginError.setText(rb.getString("Error"));
        signInBtn.setText(rb.getString("SignIn"));
        cancelBtn.setText(rb.getString("Cancel"));
        showZoneID();
        //Open database connection
        JDBC.openConnection();
    }

    /**
     * Sign In to application
     *
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    private void signIn(ActionEvent actionEvent) throws IOException, SQLException {
        //Get username and password from input fields
        username = Username.getText();
        password = Password.getText();
        //Connect to database
        Connection conn = JDBC.connection;

        //Load Main Window on successful login
        try {
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, username);
            pStmt.setString(2, password);

            ResultSet res = pStmt.executeQuery();
            res.next();
            //Set static userID
            userID = res.getInt(1);
            //Log successful login
            logActivity(username, true);
            //Hide login error
            loginError.setVisible(false);
            //Load main window
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Views/Main.fxml"));
            Scene scene = new Scene(root, 1150, 550);
            scene.getStylesheets().add("style.css"); //add styling to application
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Appointment Scheduler");
            //Set main window location on screen
            stage.setX(200);
            stage.setY(100);
            //Show main window
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            //Show login error
            loginError.setVisible(true);
            //Log failed login
            logActivity(username,true);
        }
    }

    /**
     * Handle Sign In
     * query database for valid username/password
     * @param conn JDBC Connection
     * @throws SQLException
     * @throws FileNotFoundException
     */

    /**
     * Gets username.
     *
     * @return the username
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Log activity.
     *
     * @param username the username
     * @param valid    the valid
     */
    public static void logActivity(String username, Boolean valid) throws FileNotFoundException {
        try {
            ZonedDateTime logZDT = ZonedDateTime.now();
            //Open stream to login_activity.txt
            PrintWriter pw = new PrintWriter(new FileOutputStream(new File("login_activity.txt"),true));
            //Add successful/failed login to log
            if (valid.equals(true)) {
                pw.println("__successful login__" + logZDT + "__" + username);
            } else {
                pw.println("__FAILED LOGIN__" + logZDT + "__" + username);
            }
            //Close stream
            pw.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("login_activity.txt not found");
        }
    }

    /**
     * Show user's time zone in login window
     */
    private void showZoneID() {
        ZoneId zone = ZoneId.systemDefault();
        Zone.setText(String.valueOf(zone));
    }

    /**
     * Get user's system locale
     *
     * @return
     */
    public static Locale getLocale() {
        Locale locale = Locale.getDefault();
        return locale;
    }

    /**
     * Exit application
     *
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    private void exit(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
