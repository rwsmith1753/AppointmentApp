package Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class
 *  provides methods for use throughout application
 */
public class Helper {
    //static database connection
    private static final Connection conn = JDBC.connection;

    /**
     * dayCellFactory
     * Disables weekends and previous dates in datepickers
     *
     *[LAMBDA: sets disable and background color of all dates that meet criteria
     *  prevents user from scheduling past appointments]
     */
    public static final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
        public DateCell call(final DatePicker datePicker) {
            //LAMBDA applies conditions to each date
            return new DateCell() {
                @Override public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item.isBefore(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                    DayOfWeek day = DayOfWeek.from(item);
                    if(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                        this.setDisable(true);
                        this.setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            };
        }
    };

    /**
     * Convert local time to UTC
     *
     * @param local the local
     * @return the local date time
     */
    public static LocalDateTime toUTC(LocalDateTime local) {

        ZonedDateTime toZone = local.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        ZonedDateTime UTC = toZone.withZoneSameInstant(ZoneId.of("UTC"));

        return UTC.toLocalDateTime();
    }

    /**
     * Convert UTC LocalDateTime to ZonedDateTime at user's zone
     *
     * @param ldt localDateTime
     * @return localDateTime at user's zone
     */
    public static LocalDateTime toLDT(LocalDateTime ldt) {
        ZonedDateTime toZone = ldt.atZone(ZoneId.of("UTC"));
        ZonedDateTime toLocal = toZone.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));

        return toLocal.toLocalDateTime();
    }

    /**
     * List of conflicting appointments
     *
     * @param apptID  the appt id
     * @param startDT the start dt
     * @param endDT   the end dt
     * @return the observable list
     * @throws SQLException the sql exception
     */
    public static ObservableList<Integer> isOverlap(int apptID, LocalDateTime startDT, LocalDateTime endDT) throws SQLException {
        LocalDateTime startUTC = startDT;
        LocalDateTime endUTC = endDT;;
        ObservableList<Integer> conflicts = FXCollections.observableArrayList();
        //Get all appointments from database
        try {
            String query = "SELECT * FROM APPOINTMENTS";
            Statement stmt = conn.createStatement();

            ResultSet res = stmt.executeQuery(query);

            while (res.next()) {
                int conflictID = res.getInt("Appointment_ID");
                //Convert TimeStamps to LocalDateTimes
                LocalDateTime startDB = res.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endDB = res.getTimestamp("End").toLocalDateTime();
                //Add appointment id to conflicts if overlap
                if ((startUTC.isAfter(startDB) && startUTC.isBefore(endDB)
                        || endUTC.isAfter(startDB) && endUTC.isBefore(endDB)
                        || startDB.isAfter(startUTC) && startDB.isBefore(endUTC)
                        || endDB.isAfter(startUTC) && endDB.isBefore(endUTC)
                        || startUTC.isEqual(startDB))
                        && conflictID != apptID) {
                    conflicts.add(conflictID);
                }
            }
            //If there are conflicts
            if (!conflicts.isEmpty()) {
                return conflicts;
            }
            //If no conflicts
            else {
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Get next available appointment id
     *
     * @return appointment id
     */
    public static int nextApptID() {
        int id = 0;
        try {
            Connection conn = JDBC.connection;
            //Get highest appointment id
            String query = "SELECT MAX(Appointment_ID) AS last_id FROM APPOINTMENTS";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            if (res.next()) {
                id = res.getInt("last_id");
            }
            //Return highest appointment id plus 1
            return id + 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
