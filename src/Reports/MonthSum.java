package Reports;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * The type Month sum.
 */
public class MonthSum {
    /**
     * The Month.
     */
    SimpleStringProperty month;
    /**
     * The Sum.
     */
    SimpleIntegerProperty sum;

    /**
     * Instantiates a new Month sum.
     *
     * @param month the month
     * @param sum   the sum
     */
    public MonthSum(SimpleStringProperty month, SimpleIntegerProperty sum) {
        this.month = month;
        this.sum = sum;
    }

    /**
     * Instantiates a new Month sum.
     *
     * @param monthNum the month num
     * @param sum      the sum
     */
    public MonthSum(int monthNum, SimpleIntegerProperty sum) {
        this.month = getMonthName(monthNum);
        this.sum = sum;
    }

    /**
     * Gets month.
     *
     * @return the month
     */
    public String getMonth() {
        return month.get();
    }

    /**
     * Month property simple string property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty monthProperty() {
        return month;
    }

    /**
     * Sets month.
     *
     * @param month the month
     */
    public void setMonth(String month) {
        this.month.set(month);
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
     * Sum property simple integer property.
     *
     * @return the simple integer property
     */
    public SimpleIntegerProperty sumProperty() {
        return sum;
    }

    /**
     * Sets sum.
     *
     * @param sum the sum
     */
    public void setSum(int sum) {
        this.sum.set(sum);
    }

    /** Get month name
     *  convert month index to name
     * @param monthNum month index (int)
     * @return month name
     */
    private SimpleStringProperty getMonthName(int monthNum) {
        SimpleStringProperty monthName = null;
        if (monthNum == 1) {
            monthName = new SimpleStringProperty("January");
        } else if (monthNum == 2) {
            monthName = new SimpleStringProperty("February");
        } else if (monthNum == 3) {
            monthName = new SimpleStringProperty("March");
        } else if (monthNum == 4) {
            monthName = new SimpleStringProperty("April");
        } else if (monthNum == 5) {
            monthName = new SimpleStringProperty("May");
        } else if (monthNum == 6) {
            monthName = new SimpleStringProperty("June");
        } else if (monthNum == 7) {
            monthName = new SimpleStringProperty("July");
        } else if (monthNum == 8) {
            monthName = new SimpleStringProperty("August");
        } else if (monthNum == 9) {
            monthName = new SimpleStringProperty("September");
        } else if (monthNum == 10) {
            monthName = new SimpleStringProperty("October");
        } else if (monthNum == 11) {
            monthName = new SimpleStringProperty("November");
        } else if (monthNum == 12) {
            monthName = new SimpleStringProperty("December");
        }
        return monthName;
    }

}
