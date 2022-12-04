Title: Appointment Scheduler
Purpose: This is a CRUD application for scheduling customer appointments. Data is stored in a MYSQL database, and an intuitive GUI provides simple user interaction.

Author: Ryan Smith
Email: rsm1479@wgu.edu
Version: 1.1.0
Date: 11/28/2022

IDE/Version: IntelliJ IDEA 2021.1.3 (Community Edition)
Java: 17.0.1
JavaFX: 17.0.1

Directions (IntelliJ, UNIX/LINUX & Windows):
1. Start IntelliJ
2. Open project folder
3. Go to Project Structure
4. Set Project SDK to Java 17.0.1
5. Add JavaFX SDK to libraries
6. Create new run configuration
7. Set Build and Run to "Main" class
8. Add VM Option: "--module-path ${PATH_TO_FX} --add-modules javafx.fxml,javafx.controls,javafx.graphics"
9. Compile/Build project and run

Username/Password for testing: "test"/"test" or "admin"/"admin"
After successful login, an alert will show if any appointments are scheduled to start within 15 minutes.
Login attempts are added to Login_Activity.txt with successful/failed attempt, date & time, and username.

Main window has tabs for viewing appointments, customers, and reports.
Appointments:
    TableView defaults to showing all appointments. "All", "Monthly", and "Weekly" radio buttons filter list to show respective appointments.
    Input fields default to disabled. Click "New Appointment" to enable fields and enter new appointment info.
    Click "Save" to save new appointment.
    Alert will show Appointment ID and Type of appointments with conflicting start/end times.
    "Cancel" will empty and disable input fields.
    To modify an appointment, select the appointment in the table and click "Modify Appointment". Input fields will be enabled and populated with appointment info.
    Click "Save" to update appointment.
    To delete an appointment, select the appointment in the table and click "Delete Appointment". Confirm appointment delete. Alert will warn if customer has active appointments scheduled.

    Start/End datepickers have weekends and previous dates disabled.
    Start Time/End Time comboboxes show available times within business hours.
        Times are automatically converted to user's local time zone to reflect 8am-10pm EST business hours. Appointments cannot be scheduled outside of business hours.

    "Exit" button closes application
Customers:
    "New Customer", "Modify Customer", "Delete Customer", and "Cancel" perform their respective functions in Appointments tab.
     Confirm appointment delete. Alert will warn if customer has active appointments scheduled.

Reports:
    Appointments Summary:
        List of how many appointments for each month
        List of how many appointments for each type
    Contact Schedules:
        List of all contact names.
        Schedule is empty at tab load.
        Click on a name in the Contacts table.
        The schedule will populate with all appointments for that contact.

    Activity Summary:
        Summary of each contact's appointments, grouped by customer.
            The purpose of this report is to provide insight on resource capacity and availability.
            This data is helpful when deciding if more resources are needed, or if it'd be beneficial for a one:one relationship between a customer and a contact.
        List of all contact names.
        Summary is empty at tab load.
        Click on a name in the Contacts table.
        The summary will populate with sum of appointments by customer for that contact.

MYSQL Connector: mysql-connector-j-8.0.312