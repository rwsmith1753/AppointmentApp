package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * The type Appointment.
 */
public class Appointment {
    private int apptID;
    private String title;
    private String desc;
    private String loc;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private int customerID;
    private int userID;
    private int contactID;

    private SimpleStringProperty startDate;
    private SimpleStringProperty startTime;
    private SimpleStringProperty endDate;
    private SimpleStringProperty endTime;

    private ObservableList<LocalDateTime> startDateList;

    /**
     * Instantiates a new Appointment.
     *
     * @param apptID     the appt id
     * @param title      the title
     * @param desc       the desc
     * @param loc        the loc
     * @param type       the type
     * @param start      the start
     * @param end        the end
     * @param customerID the customer id
     * @param userID     the user id
     * @param contactID  the contact id
     */
    public Appointment(
            int apptID,
            String title,
            String desc,
            String loc,
            String type,
            LocalDateTime start,
            LocalDateTime end,
            int customerID,
            int userID,
            int contactID
    )
    {
        this.apptID = apptID;
        this.title = title;
        this.desc = desc;
        this.loc = loc;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;

        this.startDate = new SimpleStringProperty(String.valueOf(this.start.toLocalDate()));
        this.startTime = new SimpleStringProperty(String.valueOf(this.start.toLocalTime()));
        this.endDate = new SimpleStringProperty(String.valueOf(this.end.toLocalDate()));
        this.endTime = new SimpleStringProperty(String.valueOf(this.end.toLocalTime()));
    }

    /**
     * Gets appt id.
     *
     * @return the appt id
     */
    public int getApptID() {return apptID;}

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {return title;}

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {return desc;}

    /**
     * Gets loc.
     *
     * @return the loc
     */
    public String getLoc() {return loc;}

    /**
     * Gets contact id.
     *
     * @return the contact id
     */
    public int getContactID() {return contactID;}

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {return type;}

    /**
     * Gets start.
     *
     * @return the start
     */
    public LocalDateTime getStart() {return start;}

    /**
     * Gets end.
     *
     * @return the end
     */
    public LocalDateTime getEnd() {return end;}

    /**
     * Gets customer id.
     *
     * @return the customer id
     */
    public int getCustomerID() {return customerID;}

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public int getUserID() {return userID;}

    /**
     * Sets appt id.
     *
     * @param apptID the appt id
     */
    public void setApptID(int apptID) {this.apptID = apptID;}

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {this.title = title;}

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {this.desc = desc;}

    /**
     * Sets loc.
     *
     * @param loc the loc
     */
    public void setLoc(String loc) {this.loc = loc;}

    /**
     * Sets contact.
     *
     * @param contactID the contact id
     */
    public void setContact(int contactID) {this.contactID = contactID;}

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {this.type = type;}

    /**
     * Sets start.
     *
     * @param start the start
     */
    public void setStart(LocalDateTime start) {this.start = start;}

    /**
     * Sets end.
     *
     * @param end the end
     */
    public void setEnd(LocalDateTime end) {this.end = end;}

    /**
     * Sets customer id.
     *
     * @param customerID the customer id
     */
    public void setCustomerID(int customerID) {this.customerID = customerID;}

    /**
     * Sets user id.
     *
     * @param userID the user id
     */
    public void setUserID(int userID) {this.userID = userID;}


    /**
     * Gets start date.
     *
     * @return the start date
     */
    public String getStartDate() {return startDate.get();}

    /**
     * Start date property simple string property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty startDateProperty() {return startDate;}

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public String getStartTime() {return startTime.get();}

    /**
     * Start time property simple string property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty startTimeProperty() {return startTime;}

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {return end.toLocalDate();}

    /**
     * End date property simple string property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty endDateProperty() {return endDate;}

    /**
     * Gets end time.
     *
     * @return the end time
     */
    public LocalTime getEndTime() {return end.toLocalTime();}

    /**
     * End time property simple string property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty endTimeProperty() {return endTime;}



}
