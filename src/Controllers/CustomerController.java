package Controllers;

import Model.Customer;
import Utils.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.ref.PhantomReference;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Customer controller.
 */
public class CustomerController {
    private static Connection conn = JDBC.connection;

    private static ObservableList<Customer> customers = FXCollections.observableArrayList();

    /**
     * Add new customer to database
     *
     * @param customer
     */
    public static void newCust(Customer customer) {
        customers.add(customer);
    }

    /**
     * Get all customers
     *
     * @param conn JDBC Connection
     * @return list of all customers
     * @throws SQLException SQL exception
     */
    public static ObservableList<Customer> getCustomers(Connection conn) throws SQLException {
        //Get customers from database
        String query = "SELECT * FROM CUSTOMERS";
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query);
        while (res.next()) {
            int customerID = res.getInt("Customer_ID");
            String customerName = res.getString("Customer_Name");
            String address = res.getString("Address");
            String postalCode = res.getString("Postal_Code");
            String phone = res.getString("Phone");
            LocalDateTime created = res.getObject("Create_Date", LocalDateTime.class);
            String createdBy = res.getString("Created_By");
            LocalDateTime updated = res.getObject("Last_Update", LocalDateTime.class);
            String updatedBy = res.getString("Last_Updated_By");
            int divID = res.getInt("Division_ID");

            Customer customer = new Customer(
                    customerID,
                    customerName,
                    address,
                    postalCode,
                    phone,
                    created,
                    createdBy,
                    updated,
                    updatedBy,
                    divID);
            newCust(customer);
        }
        return customers;
    }

    /**
     * Gets division id
     *
     * @param region
     * @return division id
     * @throws SQLException SQL exception
     */
    public static int getDivisionID(String region) throws SQLException {
        //Get division id from region/division
        String query = "SELECT Division_ID FROM FIRST_LEVEL_DIVISIONS\n" +
                "WHERE Division = ?";
        PreparedStatement pStmt = conn.prepareStatement(query);
        pStmt.setString(1,region);
        ResultSet res = pStmt.executeQuery();
        res.next();
        return res.getInt(1);
    }
}
