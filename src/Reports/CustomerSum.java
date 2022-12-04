package Reports;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * CustomerSum class
 */
public class CustomerSum {
    private SimpleStringProperty customer;
    private SimpleIntegerProperty sum;

    /**
     * Instantiates a new CustomerSum.
     *
     * @param customer the customer
     * @param sum      the sum
     */
    public CustomerSum(String customer, int sum) {
        this.customer = new SimpleStringProperty(customer);
        this.sum = new SimpleIntegerProperty(sum);
    }

    /**
     * Gets customer.
     *
     * @return the customer
     */
    public String getCustomer() {
        return customer.get();
    }

    /**
     * Sets customer.
     *
     * @param customer the customer
     */
    public void setCustomer(String customer) {
        this.customer.set(customer);
    }

    /**
     * Gets sum.
     *
     * @return the sum
     */
    public int getSum() {
        return sum.get();
    }

    /**
     * Sets sum.
     *
     * @param sum the sum
     */
    public void setSum(int sum) {
        this.sum.set(sum);
    }
}
