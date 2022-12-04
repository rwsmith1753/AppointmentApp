package Controllers;

import Model.Appointment;
import Utils.Helper;
import Utils.JDBC;
import Utils.Warning;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.sql.*;
import java.time.*;

/**
 * The type Appointment controller.
 */
public class AppointmentController {

    private static final Connection conn = JDBC.connection;

    private static ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    /**
     * New appt.
     *
     * @param appointment the appointment
     */
    public static void newAppt(Appointment appointment) {
        appointments.add(appointment);
    }

    /**
     * Gets appointments.
     *
     * @param conn the conn
     * @return the appointments
     * @throws SQLException the sql exception
     */
    public static ObservableList<Appointment> getAppointments(Connection conn) throws SQLException {
        String query = "SELECT * FROM APPOINTMENTS";
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query);
        while (res.next()) {
            int appointment_ID = res.getInt("Appointment_ID");
            String title = res.getString("Title");
            String desc = res.getString("Description");
            String loc = res.getString("Location");
            String type = res.getString("Type");
            int contactID = res.getInt("Contact_ID");
            LocalDateTime start = res.getObject("Start", LocalDateTime.class);
            LocalDateTime end = res.getObject("End", LocalDateTime.class);
            int customerID = res.getInt("Customer_ID");
            int userID = res.getInt("User_ID");

            LocalDateTime startLocal = Helper.toLDT(start);
            LocalDateTime endLocal = Helper.toLDT(end);

            Appointment appointment = new Appointment(
                    appointment_ID,
                    title,
                    desc,
                    loc,
                    type,
                    startLocal,
                    endLocal,
                    customerID,
                    userID,
                    contactID);
            newAppt(appointment);
        }
        return appointments;
    }

    /**
     * Gets contact id
     *
     * @param contact contact name
     * @return contact id
     * @throws SQLException the sql exception
     */
    public static int getContactID(String contact) throws SQLException {
        int id = 0;
        try {
            String query = "SELECT Contact_ID FROM CONTACTS " +
                    "WHERE Contact_Name = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, contact);
            ResultSet res = pStmt.executeQuery();
            while (res.next()) {
                id = res.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets contact name.
     *
     * @param id contact id
     * @return contact name
     * @throws SQLException the sql exception
     */
    public static String getContactName(int id) throws SQLException {
        String contactName = null;
        try {
            String query = "SELECT Contact_Name from CONTACTS " +
                    "WHERE Contact_ID = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);
            ResultSet res = pStmt.executeQuery();
            while (res.next()) {
                contactName = res.getString(1);
            }
            return contactName;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets customer id.
     *
     * @param customer customer name
     * @return customer id
     * @throws SQLException the sql exception
     */
    public static int getCustomerID(String customer) throws SQLException {
        int id = 0;
        try {
            String query = "SELECT Customer_ID FROM CUSTOMERS " +
                    "WHERE Customer_Name = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, customer);
            ResultSet res = pStmt.executeQuery();
            while (res.next()) {
                id = res.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets customer name.
     *
     * @param id customer id
     * @return customer name
     * @throws SQLException the sql exception
     */
    public static String getCustomerName(int id) throws SQLException {
        String name = null;
        try {
            String query = "SELECT Customer_Name from CUSTOMERS " +
                    "WHERE Customer_ID = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, id);
            ResultSet res = pStmt.executeQuery();
            while (res.next()) {
                name = res.getString(1);
            }
            return name;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets user id.
     *
     * @param username username
     * @return user id
     * @throws SQLException the sql exception
     */
    public static int getUserID(String username) throws SQLException {
        int userID = 0;
        try {
            String query = "SELECT User_ID from USERS " +
                    "WHERE User_Name = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1,username);
            ResultSet res = pStmt.executeQuery();
            while (res.next()) {
                userID = res.getInt(1);
            }
            return userID;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Gets user name.
     *
     * @param userID  user id
     * @return  user name
     * @throws SQLException the sql exception
     */
    public static String getUserName(int userID) throws SQLException {
        String userName = null;
        try {
            String query = "SELECT User_Name from USERS " +
                    "WHERE User_ID = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1,userID);
            ResultSet res = pStmt.executeQuery();
            while (res.next()) {
                userName = res.getString(1);
            }
            return userName;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Get appointment starting within 15 minutes of login
     *
     * @param apptTable appt table
     */
    public static void upcomingAppt(TableView<Appointment> apptTable) {
        int upcomingAppt = 0;
        String time = null;
        LocalDateTime dateTime = null;

        //Get time (now) plus 15 minutes
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime upcoming = current.plusMinutes(15);

        //Get appointment if within 15 minutes from now
        for (Appointment appointment: apptTable.getItems()) {
            if (
                    appointment.getStart().isBefore(upcoming) && appointment.getStart().isAfter(current)
                            || appointment.getStart().equals(upcoming))
            {
                upcomingAppt = appointment.getApptID();
                dateTime = appointment.getStart();
            }
        }
        //Warning: appointment starting within 15 minutes
        Warning.apptSoon(upcomingAppt,dateTime);
    }
}
