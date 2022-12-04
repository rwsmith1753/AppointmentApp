package Reports;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * TypeSum class
 */
public class TypeSum {

    SimpleStringProperty type;
    SimpleIntegerProperty sum;

    /**
     * Instantiates a new Type sum.
     *
     * @param type the type
     * @param sum  the sum
     */
    public TypeSum(String type, int sum) {
        this.type = new SimpleStringProperty(type);
        this.sum = new SimpleIntegerProperty(sum);
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type.get();
    }


    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type.set(type);
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
