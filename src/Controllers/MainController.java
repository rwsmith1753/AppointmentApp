package Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.Objects;
import java.util.ResourceBundle;

import Model.Appointment;
import Model.Customer;
import Utils.Helper;
import Utils.JDBC;
import Utils.Warning;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.*;

/**
 * Main Controller
 */
public class MainController implements Initializable {
    //JavaFX components
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public ComboBox<String> endCombo;
    public ComboBox<String> startCombo;
    public TableColumn<Object, Object> customerIDCol;
    public TextField typeBox;
    public TextField locBox;
    public TextField descBox;
    public TextField titleBox;
    public TextField apptIDBox;
    public ComboBox<String> contactCombo;
    public ComboBox<String> customerCombo;
    public Button saveCustomerBtn;
    public TextField custIDBox;
    public TextField custNameBox;
    public TextField addressBox;
    public TextField postalCodeBox;
    public TextField phoneBox;
    public ComboBox<String> countryCombo;
    public ComboBox<String> regionCombo;
    public BorderPane border;
    public ComboBox<String> userCombo;
    public RadioButton weeklyRadio;
    public RadioButton monthlyRadio;
    public RadioButton allRadio;
    public Button exitBtn;
    public Tab reportsTab;
    public Button cancelCustomerBtn;
    public Button newCustomerBtn;
    public Button cancelApptBtn;
    public Text cancelText;
    public Text deleteCustomerText;
    public AnchorPane apptAnchor;
    public AnchorPane customerAnchor;
    public Button saveApptBtn;
    @FXML
    private TableView<Appointment> apptTable;
    @FXML
    private TableColumn<Appointment, Integer> apptIDCol;
    @FXML
    private TableColumn<Appointment, String> descCol;
    @FXML
    private TableColumn<Appointment, String> endDateCol;
    @FXML
    private TableColumn<Appointment, String> endTimeCol;
    @FXML
    private TableColumn<Appointment, String> locCol;
    @FXML
    private TableColumn<Appointment, String> startDateCol;
    @FXML
    private TableColumn<Appointment, String> startTimeCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableColumn<Appointment, Integer> contactIDCol;
    @FXML
    private TableColumn<Appointment, Integer> userIDCol;
    @FXML
    private TableView<Customer> custTable;
    @FXML
    private TableColumn<Customer, Integer> custIDCol;
    @FXML
    private TableColumn<Customer, String> addressCol;
    @FXML
    private TableColumn<Customer, String> phoneCol;
    @FXML
    private TableColumn<Customer, String> postalCol;
    @FXML
    private TableColumn<Customer, String> custNameCol;
    @FXML
    public TableColumn<Customer, String> regionCol;
    @FXML
    public TableColumn<Customer, String> countryCol;
    @FXML
    private BorderPane reportsPane;

    //JDBC Connection to MYSQL
    private static final Connection conn = JDBC.connection;
    //Business Hours (8am-10pm EST) in UTC. bizCloseHH set at 9pm for getTimes() logic
    private static final int bizOpenHH = 13;
    private static final int bizCloseHH = 27;

    /**
     * Initialize main window
     *
     * [LAMBDA: Listener -
     *  Sets endDatePicker value when date is selected in startDatePicker]
     *
     * [LAMBDA: Listener -
     *  When Reports tab is selected, show
     *  reportsView.fxml in stage]
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     *
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //Populate tables with MYSQL data
            populateAppts(conn);
            populateCustomers(conn);
            startDatePicker.setDayCellFactory(Helper.dayCellFactory);
            endDatePicker.setDayCellFactory(Helper.dayCellFactory);

            //LAMBDA
            startDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> endDatePicker.setValue(newValue));

            //LAMBDA
            reportsTab.setOnSelectionChanged(event -> {
                if (reportsTab.isSelected()) {
                    try {
                        reports();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            //Check if appointment start within 15 minutes of login
            AppointmentController.upcomingAppt(apptTable);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Populate appointment table
     *
     * [LAMBDA: Convert timestamps to dates and times]
     *
     * @param conn JDBC Connection
     * @throws SQLException the sql exception
     */
    public void populateAppts(Connection conn) throws SQLException {
        apptTable.getItems().clear();
        apptTable.setItems(AppointmentController.getAppointments(conn));
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("desc"));
        locCol.setCellValueFactory(new PropertyValueFactory<>("loc"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        contactIDCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        //LAMBDAS
        startDateCol.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        startTimeCol.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        endDateCol.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        endTimeCol.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());

        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }

    /**
     * Populate customer table
     *
     * [LAMBDA: Convert timestamps to dates and times]
     *
     * @param conn JDBC Connection
     * @throws SQLException SQL exception
     */
    public void populateCustomers(Connection conn) throws SQLException {
        custTable.getItems().clear();
        custTable.setItems(CustomerController.getCustomers(conn));
        custIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        //LAMBDAS
        countryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        regionCol.setCellValueFactory(cellData -> cellData.getValue().regionProperty());

        postalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }


///////////////////////// Appointment Tab /////////////////////////

//_______________________________________________________________//

    /**
     * Get local zone offset
     *
     * @return intOffset Zone offset in hours
     */
    private int getOffset() {
        ZoneId userZone = ZoneId.systemDefault();
        ZoneOffset userOffset = userZone.getRules().getOffset(LocalDateTime.now(userZone));
        int intOffset = userOffset.getTotalSeconds()/60/60;
        return intOffset;
    }

    /**
     * Get appointment times during business hours
     * @param bizOpenHH
     * @param bizCloseHH
     * @return times List of times in 15 minute increments
     */
    private ObservableList<String> getTimes(int bizOpenHH,int bizCloseHH) {
        ObservableList<String> times = FXCollections.observableArrayList();
        //Convert EST business hours to local time
        int openOffset =  bizOpenHH + getOffset();
        int closeOffset = bizCloseHH + getOffset();
        //add times at 15 minute intervals
        int MMInc = 15;
        int HH = openOffset;
        while (HH < closeOffset) {
            if (HH > 24) {
                HH -= 12;
                closeOffset -= 12;
            }
            String MM = "00";
            while (Integer.parseInt(MM) <= 45) {
                String HHMM = HH + ":" + MM;
                times.add(HHMM);
                MM = String.valueOf(Integer.parseInt(MM) + MMInc);
            }
            HH += 1;
        }
        return times;
    }

    /**
     * Set times in Start Time combobox
     * @param startCombo
     */
    @FXML
    private void setStartTimes(ComboBox<String> startCombo) {
        //Reset combobox and fill with available start times
        startCombo.getItems().clear();
        for (String time:getTimes(bizOpenHH,bizCloseHH)) {
            startCombo.getItems().add(time);
        }
    }

    /**
     * Set times in End Time combobox
     * @param endCombo
     * @param bizCloseHH
     */
    @FXML
    private void setEndTimes(ComboBox<String> endCombo,int bizCloseHH) {
        //Reset combobox
        endCombo.getItems().clear();
        //Get dates from datepickers
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        //If start date equals end date, end times begin after start time
        if (start.equals(end)) {
            String[] startTime = this.startCombo.getValue().split(":");
            int startHH = Integer.parseInt(startTime[0]);
            int startMM = Integer.parseInt(startTime[1]);
            int closeOffset = bizCloseHH + getOffset();
            int MMInc = 15;
            int HH = startHH;
            while (startMM < 45) {
                startMM += MMInc;
                String HHMM = HH + ":" + startMM;
                endCombo.getItems().add(HHMM);
            }
            HH += 1;
            while (HH < closeOffset) {
                if (HH > 24) {
                    HH -= 12;
                    closeOffset -= 12;
                }
                String MM = "00";
                while (Integer.parseInt(MM) <= 45) {
                    String HHMM = HH + ":" + MM;
                    endCombo.getItems().add(HHMM);
                    MM = String.valueOf(Integer.parseInt(MM) + MMInc);
                }
                HH += 1;
            }
            endCombo.getItems().add(HH + ":" + "00");
        //If dates are different, all times available for appointment end time
        } else {
            for (String time:getTimes(bizOpenHH,bizCloseHH)) {
                endCombo.getItems().add(time);
            }
        }
    }

    /**
     * New Appointment handler to enable input fields
     *
     * @param mouseEvent
     */
    @FXML
    void addAppt(MouseEvent mouseEvent) {
        inputToggle(false);
        saveApptBtn.setDisable(false);
    }

    /**
     * Modify selected appointment
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void modifyAppt(MouseEvent mouseEvent) throws SQLException {
        saveApptBtn.setDisable(false);
        //Get info from selected appointment
        Appointment selected = apptTable.getSelectionModel().getSelectedItem();
        int id = selected.getApptID();
        String title = selected.getTitle();
        String desc = selected.getDesc();
        String loc = selected.getLoc();
        int contactID = selected.getContactID();
        String type = selected.getType();
        LocalDateTime start = selected.getStart();
        LocalDate startDate = selected.getStart().toLocalDate();
        LocalTime startTime = selected.getStart().toLocalTime();
        LocalDateTime end = selected.getEnd();
        LocalDate endDate = selected.getEnd().toLocalDate();
        LocalTime endTime = selected.getEnd().toLocalTime();

        int customerID = selected.getCustomerID();
        int userID = selected.getUserID();

        //Populate input fields with appointment data
        apptIDBox.setText(String.valueOf(id));
        titleBox.setText(title);
        descBox.setText(desc);
        locBox.setText(loc);
        contactCombo.setValue(AppointmentController.getContactName(contactID));
        typeBox.setText(type);
        startDatePicker.setValue(startDate);
        startCombo.setValue(String.valueOf(startTime));
        endDatePicker.setValue(endDate);
        endCombo.setValue(String.valueOf(endTime));
        customerCombo.setValue(AppointmentController.getCustomerName(customerID));
        userCombo.setValue(AppointmentController.getUserName(userID));

        //Enable input fields
        inputToggle(false);
    }

    /**
     * Save input data as new appointment
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void saveAppt(MouseEvent mouseEvent) throws SQLException {
        try {
            //Validate inputs. If empty -> throw warning
            if (validateInputs(apptAnchor)) {
                //If appointment id field is empty, save new appointment
                if (Objects.equals(apptIDBox.getText(), "")) {
                    //Convert time combobox values into hours and minutes
                    String[] startTime = startCombo.getValue().split(":");
                    String[] endTime = endCombo.getValue().split(":");

                    //Merge dates and times into LocalDateTimes
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDateTime startldt = LocalDateTime.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth(), Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));
                    LocalDate endDate = endDatePicker.getValue();
                    LocalDateTime endldt = LocalDateTime.of(endDate.getYear(), endDate.getMonthValue(), endDate.getDayOfMonth(), Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1]));
                    //Convert local start & end to UTC
                    LocalDateTime start = Helper.toUTC(startldt);
                    LocalDateTime end = Helper.toUTC(endldt);
                    //Get next available appointment id
                    int apptID = Helper.nextApptID();
                    String title = titleBox.getText();
                    String desc = descBox.getText();
                    String type = typeBox.getText();
                    String loc = locBox.getText();
                    //Get contact, customer, user IDs from names
                    int contactID = AppointmentController.getContactID(String.valueOf(contactCombo.getValue()));
                    int customerID = AppointmentController.getCustomerID(String.valueOf(customerCombo.getValue()));
                    int userID = AppointmentController.getUserID(String.valueOf(userCombo.getValue()));
                    //Check if appointment times overlap with existing appointments
                    if (Helper.isOverlap(apptID,startldt,endldt) != null) {
                        //Warning: shows conflicting appointment ids and times
                        Warning.overlapWarning(Helper.isOverlap(apptID,startldt,endldt));
                    } else {
                        String query = "INSERT INTO APPOINTMENTS (" +
                                "Title, " +
                                "Description, " +
                                "Location, " +
                                "Type, " +
                                "Start, " +
                                "End, " +
                                "Create_Date," +
                                "Created_By," +
                                "Last_Update," +
                                "Last_Updated_By," +
                                "Customer_ID," +
                                "User_ID," +
                                "Contact_ID" +
                                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        PreparedStatement pStmt = conn.prepareStatement(query);
                        pStmt.setString(1, title); //title
                        pStmt.setString(2, desc); //description
                        pStmt.setString(3, loc); //location
                        pStmt.setString(4, type); //type
                        pStmt.setObject(5, start); //start datetime
                        pStmt.setObject(6, end); // end datetime
                        pStmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); //created date
                        pStmt.setString(8, LoginController.getUsername()); //created by
                        pStmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now())); //last update
                        pStmt.setString(10, LoginController.getUsername()); //last updated by
                        pStmt.setInt(11, customerID); //customer id
                        pStmt.setInt(12, userID); //user id
                        pStmt.setInt(13, contactID); //contact id

                        pStmt.executeUpdate();
                        pStmt.close();

                        //Reset input fields
                        titleBox.setText(null);
                        descBox.setText(null);
                        locBox.setText(null);
                        contactCombo.setValue(null);
                        typeBox.setText(null);
                        startDatePicker.setValue(null);
                        startCombo.setValue(null);
                        endDatePicker.setValue(null);
                        endCombo.setValue(null);
                        customerCombo.setValue(null);

                        //Disable input fields
                        inputToggle(true);

                        //Reset appointment table with current data
                        populateAppts(conn);
                    }
                } else {//Save modified appointment
                    //Get input fields
                    int apptID = Integer.parseInt(apptIDBox.getText());
                    String titleUpdt = titleBox.getText();
                    String descUpdt = descBox.getText();
                    String typeUpdt = typeBox.getText();
                    String locUpdt = locBox.getText();
                    String[] startUpdt = startCombo.getValue().split(":");
                    String[] endUpdt = endCombo.getValue().split(":");
                    String userName = String.valueOf(userCombo.getValue());

                    LocalDate startDateUpdt = startDatePicker.getValue();
                    LocalDateTime startldt = LocalDateTime.of(startDateUpdt.getYear(), startDateUpdt.getMonthValue(), startDateUpdt.getDayOfMonth(), Integer.parseInt(startUpdt[0]), Integer.parseInt(startUpdt[1]));

                    LocalDate endDateUpdt = endDatePicker.getValue();
                    LocalDateTime endldt = LocalDateTime.of(endDateUpdt.getYear(), endDateUpdt.getMonthValue(), endDateUpdt.getDayOfMonth(), Integer.parseInt(endUpdt[0]), Integer.parseInt(endUpdt[1]));
                    LocalDateTime start = Helper.toUTC(startldt);
                    LocalDateTime end = Helper.toUTC(endldt);

                    int contactIDUpdt = AppointmentController.getContactID(String.valueOf(contactCombo.getValue()));
                    int customerIDUpdt = AppointmentController.getCustomerID(String.valueOf(customerCombo.getValue()));
                    int userIDUpdt = AppointmentController.getUserID(userName);

                    if (Helper.isOverlap(apptID,startldt,endldt) != null) {
                        Warning.overlapWarning(Helper.isOverlap(apptID,startldt,endldt));
                    } else {
                        String query = "UPDATE APPOINTMENTS SET " +
                                "Title = ?, " + //1
                                "Description = ?, " + //2
                                "Location = ?, " + //3
                                "Type = ?, " + //4
                                "Start = ?, " + //5
                                "End = ?, " + //6
                                "Last_Update = ?," + //7
                                "Last_Updated_By = ?," + //8
                                "Customer_ID = ?," + //9
                                "User_ID = ?," + //10
                                "Contact_ID = ? " + //11
                                "WHERE Appointment_ID = ?"; //12

                        PreparedStatement pStmt = conn.prepareStatement(query);
                        pStmt.setString(1, titleUpdt); //title
                        pStmt.setString(2, descUpdt); //description
                        pStmt.setString(3, locUpdt); //location
                        pStmt.setString(4, typeUpdt); //type
                        pStmt.setObject(5, start); //start datetime
                        pStmt.setObject(6, end); // end datetime
                        pStmt.setTimestamp(7, Timestamp.valueOf(Helper.toLDT(LocalDateTime.now()))); //last update
                        pStmt.setString(8, LoginController.getUsername()); //last updated by
                        pStmt.setInt(9, customerIDUpdt); //customer id
                        pStmt.setInt(10, userIDUpdt); //user id
                        pStmt.setInt(11, contactIDUpdt); //contact id
                        pStmt.setInt(12, apptID); //appointment id

                        pStmt.executeUpdate();
                        pStmt.close();

                        apptIDBox.setText("");
                        titleBox.setText("");
                        descBox.setText("");
                        locBox.setText("");
                        contactCombo.setValue(null);
                        typeBox.setText("");
                        startDatePicker.setValue(null);
                        startCombo.setValue(null);
                        endDatePicker.setValue(null);
                        endCombo.setValue(null);
                        customerCombo.setValue(null);
                        userCombo.setValue(null);

                        inputToggle(true);
                        populateAppts(conn);
                    }
                }
                saveApptBtn.setDisable(true);
            } else {
                Warning.saveWarning();
            }
        } catch (Exception e) {
            throw new SQLWarning(e);
        }
        //Monthly and Weekly appointment filters
        if (monthlyRadio.isSelected()) {
            viewMonthly(null);
            monthlyRadio.setSelected(true);
        }
        else if (weeklyRadio.isSelected()) {
            viewWeekly(null);
            weeklyRadio.setSelected(true);
        }
    }

    /**
     * Delete selected appointment
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void deleteAppt(MouseEvent mouseEvent) throws SQLException {
        try {
            //Warning: confirm appointment delete
            Warning.deleteAppointmentConfirm();
            //Get selected appintment data
            Appointment appointment = apptTable.getSelectionModel().getSelectedItem();
            int apptID = appointment.getApptID();
            String type = appointment.getType();
            //Delete appointment from database
            String query = "DELETE FROM APPOINTMENTS WHERE Appointment_ID = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, apptID);
            pStmt.executeUpdate();
            pStmt.close();
            //Reset appointment table
            populateAppts(conn);
            //Show message, appointment ID & Type deleted
            cancelText.setText("Appointment " + apptID + " cancelled.\nType: " + type);

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Start combobox click handler
     *
     * @param mouseEvent
     */
    public void startComboClick(MouseEvent mouseEvent) {
        //Show start times
        setStartTimes(startCombo);
    }

    /**
     * End combobox click handler
     *
     * @param mouseEvent
     */
    public void endComboClick(MouseEvent mouseEvent) {
        //Show end times
        setEndTimes(endCombo,bizCloseHH);
    }

    /**
     * Fill contacts combobox
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void fillContactsCombo(MouseEvent mouseEvent) throws SQLException {
        contactCombo.getItems().clear();
        try {
            String query = "SELECT Contact_Name FROM CONTACTS";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                String contact = res.getString(1);
                contactCombo.getItems().add(contact);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fill customers combobox
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void fillCustomersCombo(MouseEvent mouseEvent) throws SQLException {
        customerCombo.getItems().clear();
        try {
            String query = "SELECT Customer_Name FROM CUSTOMERS";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                String customer = res.getString(1);
                customerCombo.getItems().add(customer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fill user combobox
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void fillUserCombo(MouseEvent mouseEvent) throws SQLException {
        userCombo.getItems().clear();
        try {
            String query = "SELECT User_Name from USERS";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                String user = res.getString(1);
                userCombo.getItems().add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Weekly appointment filter handler
     *
     * @param actionEvent the action event
     * @throws SQLException the sql exception
     */
    public void viewWeekly(ActionEvent actionEvent) throws SQLException {
        //Unselect other radios
        allRadio.setSelected(false);
        monthlyRadio.setSelected(false);
        //Run filter
        weeklyAppts(conn);
    }

    /**
     * Filter appointments by current week
     *
     * [LAMBDA: Convert timestamps to dates and times]
     *
     * @param conn JDBC Connection
     * @throws SQLException SQL Exception
     */
    private void weeklyAppts (Connection conn) throws SQLException {

        ObservableList<Appointment> appts = FXCollections.observableArrayList();
        //Get now at local time
        LocalDate today = Helper.toLDT(LocalDateTime.now()).toLocalDate();
        //Set week filter to previous Saturday and following Sunday
        LocalDate sat = today.with(previous(SATURDAY));
        LocalDate sun = today.with(next(SUNDAY));

        try {
            String query = "SELECT * FROM APPOINTMENTS";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                //Convert appointment start and time datetimes to dates
                LocalDate start = res.getTimestamp("Start").toLocalDateTime().toLocalDate();
                LocalDate end = res.getTimestamp("End").toLocalDateTime().toLocalDate();
                //Get appointments between previous Sunday and following Saturday
                if (start.isAfter(sat) & start.isBefore(sun)) {
                    Appointment appointment = new Appointment(
                            res.getInt("Appointment_ID"),
                            res.getString("Title"),
                            res.getString("Description"),
                            res.getString("Location"),
                            res.getString("Type"),
                            res.getTimestamp("Start").toLocalDateTime(),
                            res.getTimestamp("End").toLocalDateTime(),
                            res.getInt("Customer_ID"),
                            res.getInt("User_ID"),
                            res.getInt("Contact_ID"));
                    appts.add(appointment);
                }
            }
            //Show filtered appointments
            apptTable.getItems().clear();
            apptTable.setItems(appts);
            apptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            descCol.setCellValueFactory(new PropertyValueFactory<>("desc"));
            locCol.setCellValueFactory(new PropertyValueFactory<>("loc"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            contactIDCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
            //LAMBDAS
            startDateCol.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
            startTimeCol.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
            endDateCol.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
            endTimeCol.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());

            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
            stmt.close();
            } catch (SQLException e) {
                throw new SQLException(e);
        }
    }

    /**
     * Monthly appointment filter handler
     *
     * @param actionEvent
     * @throws SQLException SQL exception
     */
    public void viewMonthly(ActionEvent actionEvent) throws SQLException {
        //Unselect other radios
        allRadio.setSelected(false);
        weeklyRadio.setSelected(false);
        //Filter appointments
        monthlyAppts(conn);
    }

    /**
     * Filter appointments by current month
     *
     * [LAMBDA: Convert timestamps to dates and times]
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    private ObservableList<Appointment> monthlyAppts (Connection conn) throws SQLException {
        ObservableList<Appointment> appts = FXCollections.observableArrayList();
        //Get current month
        Month month = LocalDate.now().getMonth();

        try {
            String query = "SELECT * FROM APPOINTMENTS";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                //Get appointment start month
                Month start = res.getTimestamp("Start").toLocalDateTime().toLocalDate().getMonth();
                //Get appointments in current month
                if (month.equals(start)) {
                    Appointment appointment = new Appointment(
                            res.getInt("Appointment_ID"),
                            res.getString("Title"),
                            res.getString("Description"),
                            res.getString("Location"),
                            res.getString("Type"),
                            res.getTimestamp("Start").toLocalDateTime(),
                            res.getTimestamp("End").toLocalDateTime(),
                            res.getInt("Customer_ID"),
                            res.getInt("User_ID"),
                            res.getInt("Contact_ID"));
                    appts.add(appointment);
                }
            }
            //Reset appointment table and show filtered appointments
            apptTable.getItems().clear();
            apptTable.setItems(appts);
            apptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            descCol.setCellValueFactory(new PropertyValueFactory<>("desc"));
            locCol.setCellValueFactory(new PropertyValueFactory<>("loc"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            contactIDCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
            //LAMBDAS
            startDateCol.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
            startTimeCol.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
            endDateCol.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
            endTimeCol.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());

            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));

            stmt.close();
            return appts;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * View all appointments
     *
     * @param actionEvent the action event
     * @throws SQLException the sql exception
     */
    public void viewAll(ActionEvent actionEvent) throws SQLException {
        //Unselect other radios
        weeklyRadio.setSelected(false);
        monthlyRadio.setSelected(false);
        try {
            populateAppts(conn);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Disable/Enable input fields
     * @param bool
     */
    private void inputToggle(Boolean bool) {
        titleBox.setDisable(bool);
        descBox.setDisable(bool);
        locBox.setDisable(bool);
        contactCombo.setDisable(bool);
        typeBox.setDisable(bool);
        customerCombo.setDisable(bool);
        startDatePicker.setDisable(bool);
        startCombo.setDisable(bool);
        endDatePicker.setDisable(bool);
        endCombo.setDisable(bool);
        userCombo.setDisable(bool);
    }

    /**
     * Cancel new/modify appointment
     *
     * @param actionEvent
     */
    public void cancelAppt(ActionEvent actionEvent) {
        //Empty input fields
        apptIDBox.setText(null);
        titleBox.setText(null);
        descBox.setText(null);
        locBox.setText(null);
        contactCombo.setValue(null);
        typeBox.setText(null);
        customerCombo.setValue(null);
        startDatePicker.setValue(null);
        startCombo.setValue(null);
        endDatePicker.setValue(null);
        endCombo.setValue(null);
        userCombo.setValue(null);
        inputToggle(true);
        saveApptBtn.setDisable(true);
    }

///////////////////////// Customer Tab /////////////////////////

//____________________________________________________________//

    /**
     * Modify selected customer
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void modifyCustomer(MouseEvent mouseEvent) throws SQLException {
        saveCustomerBtn.setDisable(false);
        //Get selected customer data
        Customer selected = custTable.getSelectionModel().getSelectedItem();
        int custID = selected.getCustomerID();
        String custName = selected.getCustomerName();
        String address = selected.getAddress();
        String postal = selected.getPostalCode();
        String phone = selected.getPhone();
        int divID = selected.getDivID();
        String country = null;
        String region = null;
        //Get country and region by division id
        try {
            String query = "SELECT Country, Division FROM COUNTRIES " +
                    "INNER JOIN FIRST_LEVEL_DIVISIONS USING (Country_ID) " +
                    "WHERE Division_ID = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1,divID);
            ResultSet res = pStmt.executeQuery();
            while (res.next()) {
                country = res.getString(1);
                region = res.getString(2);
            }
            //Enable input fields
            customerInputToggle(false);
            //Populate input fields with selected customer data
            custIDBox.setText(String.valueOf(custID));
            custNameBox.setText(custName);
            addressBox.setText(address);
            postalCodeBox.setText(postal);
            phoneBox.setText(phone);
            countryCombo.setValue(country);
            regionCombo.setValue(region);
        } catch (SQLException e) {
        throw new RuntimeException(e);
        }
    }

    /**
     * Delete selected customer
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void deleteCustomer(MouseEvent mouseEvent) throws SQLException {
        try {
            //Warning: confirm customer delete
            Warning.deleteCustomerConfirm();
            Customer customer = custTable.getSelectionModel().getSelectedItem();
            int customerID = customer.getCustomerID();
            //Delete customer from database
            String query = "DELETE FROM CUSTOMERS WHERE Customer_ID = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1,customerID);
            pStmt.executeUpdate();
            pStmt.close();

            populateCustomers(conn);
            //Show message, customer id deleted
            deleteCustomerText.setText("Customer " + customerID + " deleted");
        } catch (SQLException e) {
            //Warning: cannot delete with active appointments
            Warning.deleteCustomerWarning();
        }
    }

    /**
     * New Customer
     *
     * @param mouseEvent
     */
    public void newCustomer(MouseEvent mouseEvent) {
        //Enable input fields
        customerInputToggle(false);
        saveCustomerBtn.setDisable(false);
    }

    /**
     * Save customer
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void saveCustomer(MouseEvent mouseEvent) throws SQLException {
        try {
            //Validate inputs. If empty -> throw warning
            if (validateInputs(customerAnchor)) {
                //If customer id field is empty, save new customer
                if (Objects.equals(custIDBox.getText(), "")) {
                    //Get input data and save customer to database
                    String name = custNameBox.getText();
                    String address = addressBox.getText();
                    String postal = postalCodeBox.getText();
                    String phone = phoneBox.getText();
                    String region = String.valueOf(regionCombo.getValue());
                    String username = LoginController.getUsername();
                    String query = "INSERT INTO CUSTOMERS (" +
                            "Customer_Name, " +
                            "Address, " +
                            "Postal_Code, " +
                            "Phone, " +
                            "Create_Date, " +
                            "Created_By, " +
                            "Last_Update," +
                            "Last_Updated_By," +
                            "Division_ID" +
                            ") VALUES (?,?,?,?,?,?,?,?,?)";
                    PreparedStatement pStmt = conn.prepareStatement(query);

                    pStmt.setString(1, name); //customer name
                    pStmt.setString(2, address); //address
                    pStmt.setString(3, postal); //postal code
                    pStmt.setString(4, phone); //phone
                    pStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); //create date
                    pStmt.setString(6, username); //created by
                    pStmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); //update date
                    pStmt.setString(8, username); //updated by
                    pStmt.setInt(9,CustomerController.getDivisionID(region));

                    pStmt.executeUpdate();
                    pStmt.close();
                } else if (!custIDBox.getText().isEmpty()){ //Save modified customer
                    //Get input data and update customer
                    int id = Integer.parseInt(custIDBox.getText());
                    String name = custNameBox.getText();
                    String address = addressBox.getText();
                    String postal = postalCodeBox.getText();
                    String phone = phoneBox.getText();
                    String region = String.valueOf(regionCombo.getValue());
                    String updatedBy = LoginController.getUsername();
                    String query = "UPDATE customers SET " +
                            "Customer_Name = ?, " +
                            "Address = ?, " +
                            "Postal_Code = ?, " +
                            "Phone = ?, " +
                            "Division_ID = ?, " +
                            "Last_Update = ?, " +
                            "Last_Updated_By = ? " +
                            "WHERE Customer_ID = ?";
                    PreparedStatement pStmt = conn.prepareStatement(query);

                    pStmt.setString(1, name); //customer name
                    pStmt.setString(2, address); //address
                    pStmt.setString(3, postal); //postal code
                    pStmt.setString(4, phone); //phone
                    pStmt.setInt(5,CustomerController.getDivisionID(region));
                    pStmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now())); //update date
                    pStmt.setString(7, updatedBy); //updated by
                    pStmt.setInt(8, id); //customer id

                    pStmt.executeUpdate();
                    pStmt.close();
                }
                //Reset customer table
                populateCustomers(conn);
                //Empty input fields
                cancelCustomer(null);
                //Disable input fields
                customerInputToggle(true);
                saveCustomerBtn.setDisable(true);

            } else {
                Warning.saveWarning();
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Disable/Enable input fields
     *
     * @param bool
     */
    private void customerInputToggle(Boolean bool) {
        custNameBox.setDisable(bool);
        addressBox.setDisable(bool);
        postalCodeBox.setDisable(bool);
        phoneBox.setDisable(bool);
        countryCombo.setDisable(bool);
        regionCombo.setDisable(bool);
    }

    /**
     * Gets countries
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void getCountries(MouseEvent mouseEvent) throws SQLException {
        //Reset country combobox
        countryCombo.getItems().clear();
        //Add countries to combobox
        try {
            String query = "SELECT Country from COUNTRIES";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                String country = res.getString(1);
                countryCombo.getItems().add(country);
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Gets regions
     *
     * @param mouseEvent
     * @throws SQLException SQL exception
     */
    public void getRegions(MouseEvent mouseEvent) throws SQLException {
        //Reset region combobox
        regionCombo.getItems().clear();
        //Get country from country combobox
        String country = String.valueOf(countryCombo.getValue());
        try {
            //Get regions from selected country
            String query = "SELECT Division FROM FIRST_LEVEL_DIVISIONS AS FLD " +
                    "INNER JOIN COUNTRIES USING(Country_ID) " +
                    "WHERE Country = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, country);
            ResultSet res = pStmt.executeQuery();

            while (res.next()) {
                String region = res.getString(1);
                regionCombo.getItems().add(region);
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Cancel new/modify customer
     *
     * @param actionEvent
     */
    public void cancelCustomer(ActionEvent actionEvent) {
        //Empty input fields
        custIDBox.setText("");
        custNameBox.setText(null);
        addressBox.setText(null);
        postalCodeBox.setText(null);
        phoneBox.setText(null);
        countryCombo.setValue(null);
        regionCombo.setValue(null);
        customerInputToggle(true);
        saveCustomerBtn.setDisable(true);
    }

    /**
     * Validate inputs
     * Checks for empty/null/blank inputs
     * @param anchorPane
     * @return true if valid, false if invalid
     */
    private Boolean validateInputs(AnchorPane anchorPane) {
        for (Node node : anchorPane.getChildren()) {
            if ((node instanceof TextField && node != custIDBox && node != apptIDBox)) {
                if (((TextField) node).getText() == null || ((TextField) node).getText() == "") {
                    return false;
                }
            } else if (node instanceof DatePicker) {
                if (((DatePicker) node).getValue() == null) {
                    return false;
                }
            } else if (node instanceof ComboBox) {
                if (((ComboBox<String>) node).getValue() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Exit program
     *
     * @param actionEvent
     */
    @FXML
    private void exit(ActionEvent actionEvent) {
        //Get and close stage
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * Set ReportsView in Reports Tab
     * @throws IOException
     * @throws SQLException
     */
    private void reports() throws IOException, SQLException {
        //Load ReportsView
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Views/ReportsView.fxml"));
        //Set ReportsView to center of border pane (reportsPane)
        reportsPane.setCenter(root);
    }
}

