package ib_client;

import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author David
 */
public class GUI extends Application {

    private FXMLRekeningManagementController RMController;
    private GUIController controller;
    private Pane myPane;

    @Override
    public void start(Stage stage) throws Exception {
        controller = new GUIController(this);
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("FXMLLogin.fxml"));
        myPane = (Pane) myLoader.load();
        FXMLLoginController loginController = (FXMLLoginController) myLoader.getController();
        Scene scene = new Scene(myPane);
        loginController.setStage(stage);
        loginController.setGuiController(controller);
        loginController.setGui(this);

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    /**
     * Sets the managementScreenController for this GUI class
     * @param RMController 
     */
    public void setManagementController(FXMLRekeningManagementController RMController) {
        this.RMController = RMController;
    }

    /**
     * Fills the list of this user's bank account. Only gets called by the
     * controller.
     *
     * @param accounts
     */
    public void setAccountList(ArrayList<String> accounts) {
        RMController.setBankAccounts(accounts);
        RMController.setComboBoxData(accounts);
    }

    /**
     * Fills the list of this user's transactions of a bank account. Only gets
     * called by the controller.
     *
     * @param transactions
     */
    public void setTransactionList(ArrayList<String> transactions) {
        RMController.setTransactions(transactions);
    }

    public boolean initAlertMessage(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    
    public void initSuccessMessage(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
    }

    public void initErrorMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void logoutScreen() {
        RMController.openLoginScreen();
    }

    /**
     * Convert Bankaccount.toString() to IBAN
     *
     * @param account (String value)
     * @return IBAN (String value)
     */
    public String accountToIBAN(String account)
    {
        return account.split(";")[0];
    }

    /**
     * Convert Bankaccount.toString() to amount of money
     *
     * @param account (String value)
     * @return amount of money (String value)
     */
    public String accountToAmount(String account)
    {
        return account.split(";")[1];
    }
    
    /**
     * Convert Transaction.toString() to date
     *
     * @param transaction (String value)
     * @return date (String value)
     */
    public String transactionToDate(String transaction) {
        return transaction.split(";")[0];
    }

    /**
     * Convert Transaction.toString() to amount of money
     *
     * @param transaction (String value)
     * @return amount of money transferred (String value)
     */
    public String transactionToAmount(String transaction) {
        return transaction.split(";")[1];
    }

    /**
     * Convert Transaction.toString() to IBANFrom
     *
     * @param transaction (String value)
     * @return IBANFrom (String value)
     */
    public String transactionToIBANFrom(String transaction) {
        return transaction.split(";")[2];
    }

    /**
     * Convert Transaction.toString() to IBANTo
     *
     * @param transaction (String value)
     * @return IBANTo (String value)
     */
    public String transactionToIBANTo(String transaction) {
        return transaction.split(";")[3];
    }

    /**
     * Convert Transaction.toString() to description
     *
     * @param transaction (String value)
     * @return "" if description is empty, else description (String value)
     */
    public String transactionToDescription(String transaction) {
        String[] fields = transaction.split(";");

        if (fields.length < 5) {
            return "";
        } else {
            return fields[4];
        }
    }

    /**
     * Converts a euro (String) and cents (String) to a double.
     * @param euros
     * @param cents
     * @return Converted amount (double)
     */
    public double amountToDouble(String euros, String cents) {
        if (euros.isEmpty()) {
            euros = "0";
        }
        if (cents.isEmpty()) {
            cents = "0";
        }
        return Double.parseDouble(euros + "." + cents);
    }
    
    /**
     * Formats a dateTime to a more readable date (without the time).
     * @param dateTime
     * @return Formatted date (without the time)
     */
    private String formatDate(String dateTime) {
        //input format: yyyy-mm-ddThh:mm:ss:mmm
        String d = dateTime.substring(0, dateTime.indexOf("T"));
        String[] fields = d.split("-");
        return fields[2] + "-" + fields[1] + "-" + fields[0];
    }
    
    /**
     * Fromats a dateTime to more a readable dateTime.
     * @param dateTime
     * @return Formatted dateTime.
     */
    public String formatDateTime(String dateTime) {
        String date = formatDate(dateTime);
        String t = dateTime.substring(dateTime.indexOf("T") + 1);
        String[] fields = t.split(":");
        String time = fields[0] + ":" + fields[1];
        return date + " (" + time + ")";
    }
    
    /**
     * Formats the saldo to a more readable format.
     * @param saldo
     * @return Formatted saldo.
     */
    public String formatSaldo(String saldo)
    {
        String euros = "€" + saldo.substring(0, saldo.indexOf(".") + 1);
        String cents = saldo.substring(saldo.indexOf(".") + 1);
        if (cents.length() == 1)
        {
            cents += "0";
            if ("00".equals(cents))
            {
                cents = cents.replace("00", "-");
            }
        }
        
        if (cents.length() > 2) {
            cents = cents.substring(0, 2);
        }
        
        String result = euros + cents;
        return result.replace(".", ",");
    }

    /**
     * Checks if the IBAN is your own IBAN or someone else's.
     * If the IBAN is yours, returns "Me".
     * If the IBAN is not yours, returns the IBAN set as the parameter.
     * @param IBAN you want to check
     * @param activeIBAN your own IBAN
     * @return IBAN or "Me"
     */
    public String checkIBAN(String IBAN, String activeIBAN) {
        if (activeIBAN.equals(IBAN)) {
            return "Me";
        } else {
            return IBAN;
        }
    }
    
    /**
     * If money has been removed from the bank account: color = red.
     * If money has been added to the bank account: color = green.
     * @param values (String of a formatted transaction).
     * @return Color
     */
    public Color setTransactionColor(String values) {
        int indexIBAN = values.indexOf("NL");
        int indexArrow = values.indexOf("→");
        if (indexIBAN < indexArrow) {
            return Color.GREEN;
        } else {
            return Color.RED;
        }
    }
    
    /**
     * Formats a transaction from values 'String value' and 'String IBAN'.
     * This format is is loaded in a listview.
     * @param value
     * @param activeIBAN
     * @return Formatted transaction
     */
    public String formatTransaction(String value, String activeIBAN) {
        String date = formatDate(transactionToDate(value));
        String amount = formatSaldo(transactionToAmount(value));
        String IBANFrom = checkIBAN(transactionToIBANFrom(value), activeIBAN);
        String IBANTo = checkIBAN(transactionToIBANTo(value), activeIBAN);
        
        return date + "\n" + IBANFrom + " → " + IBANTo + ";" + amount;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Gives a list of the current status of the IBAN's, before
     * a transaction.
     * @return 
     */
    public ArrayList<String> getCurrentIBANs() {
        return RMController.getCurrentIBANs();
    }
    
    @Override
    public void stop() {
        controller.logout();
        System.exit(0);
    }
}
