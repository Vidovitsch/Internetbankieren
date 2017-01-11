package ib_client;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

    public void initSuccessMessage(String message) {

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
        String result = euros + cents;
        return result.replace(".", ",");
    }

    public String checkIBAN(String IBAN, String activeIBAN) {
        if (activeIBAN.equals(IBAN)) {
            return "Me";
        } else {
            return IBAN;
        }
    }
    
    public Color setTransactionColor(String values) {
        int indexIBAN = values.indexOf("NL");
        int indexArrow = values.indexOf("→");
        if (indexIBAN < indexArrow) {
            return Color.GREEN;
        } else {
            return Color.RED;
        }
    }
    
    public String formatTransaction(String value, String activeIBAN) {
        String date = transactionToDate(value);
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

    @Override
    public void stop() {
        controller.logout();
        System.exit(0);
    }
}
