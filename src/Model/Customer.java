package Model;

import Utils.JDBC;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Customer
 */
public class Customer {
    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private final LocalDateTime created;
    private final String createdBy;
    private LocalDateTime updated;
    private String updatedBy;
    private int divID;

    private SimpleStringProperty country;
    private SimpleStringProperty region;

    /**
     * Instantiates a new Customer
     *
     * @param customerID   customer id
     * @param customerName customer name
     * @param address      address
     * @param postalCode   postal code
     * @param phone        phone
     * @param created      created
     * @param createdBy    created by
     * @param updated      updated
     * @param updatedBy    updated by
     * @param divID        div id
     * @throws SQLException SQL exception
     */
    public Customer(
            int customerID,
            String customerName,
            String address,
            String postalCode,
            String phone,
            LocalDateTime created,
            String createdBy,
            LocalDateTime updated,
            String updatedBy,
            int divID
    ) throws SQLException {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.created = LocalDateTime.now();
        this.createdBy = createdBy;
        this.updated = LocalDateTime.now();
        this.updatedBy = updatedBy;
        this.divID = divID;
        //Set country and region from division id
        this.country = new SimpleStringProperty(getCountry(this.divID));
        this.region = new SimpleStringProperty(getRegion(this.divID));
    }

    /**
     * Get customer id
     *
     * @return customer id
     */
    public int getCustomerID(){return customerID;}

    /**
     * Gets customer name
     *
     * @return customer name
     */
    public String getCustomerName() {return customerName;}

    /**
     * Gets address
     *
     * @return address
     */
    public String getAddress() {return address;}

    /**
     * Gets postal code
     *
     * @return postal code
     */
    public String getPostalCode() {return postalCode;}

    /**
     * Gets phone
     *
     * @return phone
     */
    public String getPhone() {return phone;}

    /**
     * Gets created
     *
     * @return created
     */
    public LocalDateTime getCreated() {return created;}

    /**
     * Gets created by
     *
     * @return created by
     */
    public String getCreatedBy() {return createdBy;}

    /**
     * Gets updated
     *
     * @return updated
     */
    public LocalDateTime getUpdated() {return updated;}

    /**
     * Gets updated by
     *
     * @return updated by
     */
    public String getUpdatedBy() {return updatedBy;}

    /**
     * Gets div id
     *
     * @return div id
     */
    public int getDivID() {return divID;}

    /**
     * Sets customer id
     *
     * @param customerID customer id
     */
    public void setCustomerID(int customerID) {this.customerID = customerID;}

    /**
     * Sets customer name
     *
     * @param customerName customer name
     */
    public void setCustomerName(String customerName) {this.customerName = customerName;}

    /**
     * Sets address
     *
     * @param address address
     */
    public void setAddress(String address) {this.address = address;}

    /**
     * Sets postal code
     *
     * @param postalCode postal code
     */
    public void setPostalCode(String postalCode) {this.postalCode = postalCode;}

    /**
     * Sets phone number
     *
     * @param phone phone
     */
    public void setPhone(String phone) {this.phone = phone;}

    /**
     * Get region
     *
     * @param divID
     * @return region
     * @throws SQLException SQL exception
     */
    public static String getRegion(int divID) throws SQLException {
        try {
            //Connect to database and get region from division id
            Connection conn = JDBC.connection;
            String query = "SELECT Division FROM FIRST_LEVEL_DIVISIONS WHERE Division_ID = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, divID);

            ResultSet res = pStmt.executeQuery();
            String div = null;
            while (res.next()) {
                div = res.getString(1);

            }
            return div;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets country
     *
     * @param divID
     * @return country
     * @throws SQLException SQL exception
     */
    public static String getCountry(int divID) throws SQLException {
        try {
            //Connect to database and get country from division id
            Connection conn = JDBC.connection;
            String query = "SELECT Country FROM COUNTRIES " +
                    "INNER JOIN FIRST_LEVEL_DIVISIONS USING(Country_ID) " +
                    "WHERE Division_ID = ?";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, divID);

            ResultSet res = pStmt.executeQuery();
            String country = null;
            while (res.next()) {
                country = res.getString(1);

            }
            return country;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets country property
     *
     * @return country property
     */
    public String getCountryProperty() {return country.get();}

    /**
     * Country property simple string property
     *
     * @return simple string property
     */
    public SimpleStringProperty countryProperty() {return country;}

    /**
     * Gets region property
     *
     * @return region property
     */
    public String getRegionProperty() {return region.get();}

    /**
     * Region property simple string property
     *
     * @return simple string property
     */
    public SimpleStringProperty regionProperty() {return region;}

}
