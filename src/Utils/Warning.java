package Utils;

import Controllers.LoginController;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.control.Alert.AlertType.WARNING;

import java.time.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The type Warning.
 */
public class Warning {

    /**
     * Overlap warning.
     *
     * @param conflicts the conflicts
     */
    public static void overlapWarning(ObservableList<Integer> conflicts) {
        Alert alert = new Alert(WARNING);
        alert.setTitle("CONFLICTING APPOINTMENTS");
        alert.setHeaderText("Appointment times overlap with the following appointment(s)");
        alert.setContentText(String.valueOf(conflicts));
        alert.showAndWait();
    }

    /**
     * Appt soon.
     *
     * Alert if appointment starts within 15 minutes after login
     *
     * @param upcomingAppt the upcoming appointment ID
     * @param dateTime  LocalDateTime
     */
    public static void apptSoon(int upcomingAppt,LocalDateTime dateTime) {
//        Locale locale = new Locale("fr");   //uncomment line to change locale. Comment line below
        Locale locale = LoginController.getLocale();
        //Set ResourceBundle to language properties
        ResourceBundle rb = ResourceBundle.getBundle("Properties/rb", locale);
        //Get text from language properties
        String title = null;
        String header = null;
        String titleTrue = rb.getString("TitleTrue");
        String headerTrue = rb.getString("HeaderTrue");
        String text = null;
        String titleFalse = rb.getString("TitleFalse");
        String headerFalse = rb.getString("HeaderFalse");
        //Initiate alert types
        Alert.AlertType warn = WARNING;
        Alert.AlertType info = INFORMATION;
        Alert.AlertType type = null;
        //Text if appointment starting soon
        if (upcomingAppt != 0) {
            //Get warning variables
            type = warn;
            title = titleTrue;
            header = headerTrue;
            text = "ID: " + upcomingAppt + " @ " + dateTime;
        } else { //Text if no upcoming appointment
            //Get info variables
            type = info;
            title = titleFalse;
            header = headerFalse;
        }
        //Show alert
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }


    /**
     * Delete customer warning.
     */
    public static void deleteCustomerWarning() {
        Alert alert = new Alert(WARNING);
        alert.setTitle("CANNOT DELETE CUSTOMER");
        alert.setHeaderText("Customer has active appointments scheduled");
        alert.setContentText("Unable to delete customers with active appointments");
        alert.showAndWait();
    }

    /**
     * Delete customer confirm.
     */
    public static void deleteCustomerConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("DELETE CUSTOMER");
        alert.setHeaderText("You are about to delete a customer");
        alert.setContentText("This action cannot be undone");
        alert.showAndWait();
    }

    /**
     * Delete appointment confirm.
     */
    public static void deleteAppointmentConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("DELETE APPOINTMENT");
        alert.setHeaderText("You are about to delete an appointment");
        alert.setContentText("This action cannot be undone");
        alert.showAndWait();
    }

    /**
     * Save warning.
     */
    public static void saveWarning() {
        Alert alert = new Alert(WARNING);
        alert.setTitle("CANNOT SAVE");
        alert.setHeaderText("Missing or invalid information");
        alert.setContentText("All data fields are required. Please ensure data is correct");
        alert.showAndWait();
    }
}
