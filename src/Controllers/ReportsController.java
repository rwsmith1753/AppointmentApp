package Controllers;

import Model.Appointment;
import Reports.CustomerSum;
import Reports.MonthSum;
import Reports.TypeSum;
import Utils.Helper;
import Utils.JDBC;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ResourceBundle;

/**
 * Reports Controller
 */
public class ReportsController {
    @FXML
    public TableView<Appointment> scheduleReportsTable;
    @FXML
    public TableColumn<String, String> contactReportsCol = new TableColumn<>();
    @FXML
    public TableView contactReportsTable;
    @FXML
    public Tab activityTab;
    @FXML
    public TableView<CustomerSum> activityTable;
    @FXML
    public TableColumn<Object, Object> customerActivityCol;
    @FXML
    public TableColumn<Object, Object> apptActivityCol;
    @FXML
    public TableColumn<String,String> contactActivityCol;
    @FXML
    public TableView contactActivityTable;
    @FXML
    public TableColumn<Object, Object> monthSumCol;
    @FXML
    public TableColumn<Object, Object> appointmentSumCol;
    @FXML
    public TableView<MonthSum> monthSumTable;
    @FXML
    private TableView<CustomerSum> customerSummaryTable = new TableView<>();
    @FXML
    private TableColumn<CustomerSum,String> customerReportCol = new TableColumn<>();
    @FXML
    private TableColumn<CustomerSum,Integer> customerSumReportCol = new TableColumn<>();
    @FXML
    private TableView<TypeSum> typeSummaryTable;
    @FXML
    public TableColumn<Appointment,String> typeScheduleReportCol;
    @FXML
    public TableColumn<Object, Object> typeReportCol;
    @FXML
    public TableColumn<Object, Object> typeSumReportCol;
    @FXML
    public TableColumn<Appointment,Integer> apptIDReportCol;
    @FXML
    public TableColumn<Appointment,Integer> customerIDReportCol;
    @FXML
    public TableColumn<Object, Object> descReportCol;
    @FXML
    public TableColumn<Appointment,String> endDateReportCol;
    @FXML
    public TableColumn<Appointment,String> endTimeReportCol;
    @FXML
    public TableColumn<Object, Object> locReportCol;
    @FXML
    public Tab scheduleTab;
    @FXML
    public TableColumn<Appointment,String> startDateReportCol;
    @FXML
    public TableColumn<Appointment,String> startTimeReportCol;
    @FXML
    public Tab summaryTab;
    @FXML
    public TableColumn<Object, Object> titleReportCol;

     //Static database connection
    private static final Connection conn = JDBC.connection;

    /**
     * Initialize Reports
     *
     * @param location
     * @param resources
     * @throws SQLException
     */
    public void initialize(URL location, ResourceBundle resources) throws SQLException {
    }

    /**
     * Run appointments summary
     *
     * @param actionEvent
     * @throws SQLException SQL exception
     */
    @FXML
    public void runAppointmentsSummary(ActionEvent actionEvent) throws SQLException {
        //Get user's zone offset, converted to string in hours format
        ZoneId userZone = ZoneId.systemDefault();
        ZoneOffset userOffset = userZone.getRules().getOffset(LocalDateTime.now(userZone));
        String offset = userOffset.getTotalSeconds()/60/60 + ":00:00";
        //Initiate list of appointments by month
        ObservableList<MonthSum> monthSums = FXCollections.observableArrayList();
        //Get number of appointments by month
        String query = "SELECT MONTH(ADDTIME(Start, ?)) AS Mo, COUNT(*)\n" +
                "FROM APPOINTMENTS\n" +
                "GROUP BY Mo;";
        PreparedStatement pStmt = conn.prepareStatement(query);
        //Account for user's timezone to determine month
        pStmt.setString(1, offset);
        ResultSet res = pStmt.executeQuery();
        while (res.next()) {
            int monthNum = res.getInt(1);
            SimpleIntegerProperty sum = new SimpleIntegerProperty(res.getInt(2));
            MonthSum monthSum = new MonthSum(monthNum,sum);
            monthSums.add(monthSum);
        }
        //Reset month summary table
        monthSumTable.getItems().clear();
        monthSumTable.setItems(monthSums);
        monthSumCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        appointmentSumCol.setCellValueFactory(new PropertyValueFactory<>("sum"));

        //Reset type summary table
        typeSummaryTable.getItems().clear();
        typeSummaryTable.setItems(getTypeSums());
        typeReportCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeSumReportCol.setCellValueFactory(new PropertyValueFactory<>("sum"));
    }

    /**
     * Gets type sums.
     *
     * @return the type sums
     * @throws SQLException the sql exception
     */
    public static ObservableList<TypeSum> getTypeSums() throws SQLException {
        try {
            ObservableList<TypeSum> typeSums = FXCollections.observableArrayList();

            String query = "SELECT DISTINCT(Type), COUNT(Appointment_ID)\n" +
                    "FROM APPOINTMENTS\n" +
                    "\tGROUP BY Type\n" +
                    "ORDER BY COUNT(Appointment_ID) DESC";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                String type = res.getString(1);
                int sum = res.getInt(2);
                TypeSum typeSum = new TypeSum(type, sum);
                typeSums.add(typeSum);
            }
            return typeSums;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Run schedule
     * [LAMBDA: Listener -
     *  Triggered by contact selection. Does not require a "Run" button
     *  Show all appointments scheduled with selected contact]
     *
     * @param actionEvent the action event
     * @throws SQLException the sql exception
     */
    public void runSchedule(ActionEvent actionEvent) throws SQLException {
        contactReportsTable.getItems().clear();
        //Get list of contacts
        contactReportsTable.setItems(getContacts());
        contactReportsCol.setCellValueFactory(list-> new SimpleStringProperty(list.getValue()));
        //LAMBDA
        contactReportsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String contact = String.valueOf(contactReportsTable.getSelectionModel().getSelectedItem());
                try {
                    showSchedule(contact);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    /**
     * Show Schedule
     *
     * [LAMBDA: converts database UTC times to local times as rows are added to columns]
     *
     * @param contact name
     * @throws SQLException
     */
    private void showSchedule(String contact) throws SQLException {
        scheduleReportsTable.getItems().clear();
        //Get appointments from list
        scheduleReportsTable.setItems(getContactSchedule(contact));
        apptIDReportCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        titleReportCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descReportCol.setCellValueFactory(new PropertyValueFactory<>("desc"));
        locReportCol.setCellValueFactory(new PropertyValueFactory<>("loc"));
        typeScheduleReportCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        //LAMBDAS to fill table columns with local dates/times
        startDateReportCol.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        startTimeReportCol.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        endDateReportCol.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        endTimeReportCol.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());

        customerIDReportCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    /**
     * Gets contact schedule.
     *
     * @param contact contact name
     * @return the user schedule
     * @throws SQLException the sql exception
     */
    public ObservableList<Appointment> getContactSchedule(String contact) throws SQLException {
        try {
            ObservableList<Appointment> appointments = FXCollections.observableArrayList();
            //Get all appointments from database for selected contact
            String query = "SELECT * FROM APPOINTMENTS\n" +
                    "\tINNER JOIN CONTACTS USING (Contact_ID)\n" +
                    "WHERE Contact_Name = ?\n" +
                    "ORDER BY Start ASC;";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1,contact);
            ResultSet res = pStmt.executeQuery();
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
                //Convert start & end times to local time zone
                LocalDateTime startLocal = Helper.toLDT(start);
                LocalDateTime endLocal = Helper.toLDT(end);
                //Cast database rows to Appointments
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
                //Add appointment to appointments list
                appointments.add(appointment);
            }
            return appointments;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets contacts.
     *
     * @return the contacts
     * @throws SQLException the sql exception
     */
    public ObservableList<String> getContacts() throws SQLException {
        ObservableList<String> contacts = FXCollections.observableArrayList();
        try {
            String query = "SELECT DISTINCT(Contact_Name) FROM CONTACTS";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                String contact = res.getString(1);
                contacts.add(contact);
            }
            return contacts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Run Activity Summary Report
     *
     * [LAMBDA: Listener -
     *      Triggered by contact selection. Does not require "Run" button
     *      Shows sum of appointments the contact has with each customer]
     * @param actionEvent
     * @throws SQLException
     *
     */
    private void runActivity(ActionEvent actionEvent) throws SQLException {
        contactActivityTable.getItems().clear();
        contactActivityTable.setItems(getContacts());
        contactActivityCol.setCellValueFactory(list-> new SimpleStringProperty(list.getValue()));
        //LAMBDA
        contactActivityTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String contact = String.valueOf(contactActivityTable.getSelectionModel().getSelectedItem());
                try {
                    showActivity(contact);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    /**
     * Show Activity
     * @param contact
     * @throws SQLException
     */
    private void showActivity(String contact) throws SQLException{
        //Reset activity table
        activityTable.getItems().clear();
        //Get CustomerSum and populate columns
        activityTable.setItems(getContactActivity(contact));
        customerActivityCol.setCellValueFactory(new PropertyValueFactory<>("customer"));
        apptActivityCol.setCellValueFactory(new PropertyValueFactory<>("sum"));
    }

    /**
     * Gets contact activity.
     *
     * @param contact the contact
     * @return the contact activity
     * @throws SQLException the sql exception
     */
    public static ObservableList<CustomerSum> getContactActivity(String contact) throws SQLException {
        try {
            ObservableList<CustomerSum> customerSums = FXCollections.observableArrayList();
            //Get contact, customer, and appointment sums from database
            String query = "SELECT Contact_Name, Customer_Name, COUNT(Appointment_ID)\n" +
                    "FROM CONTACTS\n" +
                    "\tINNER JOIN APPOINTMENTS USING(Contact_ID)\n" +
                    "\tINNER JOIN CUSTOMERS USING(Customer_ID)\n" +
                    "WHERE Contact_Name = ?\n" +
                    "GROUP BY Customer_Name\n" +
                    "ORDER BY COUNT(Appointment_ID) DESC";
            PreparedStatement pSmt = conn.prepareStatement(query);
            pSmt.setString(1,contact);
            ResultSet res = pSmt.executeQuery();
            while (res.next()) {
                String customer = res.getString(2);
                int sum = res.getInt(3);
                //Cast database rows to CustomerSum class
                CustomerSum customerSum = new CustomerSum(customer, sum);
                customerSums.add(customerSum);
            }
            return customerSums;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reports tab listener.
     * Populate report when tab is selected
     * @param event
     */
    public void reportsTabListener(Event event) {
        //Summary tab selected
        if (summaryTab.isSelected()) {
            try {
                runAppointmentsSummary(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        //Schedule tab selected
        } else if (scheduleTab.isSelected()) {
            try {
                runSchedule(null);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        //Activity tab selected
        } else if (activityTab.isSelected()) {
            try {
                runActivity(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

