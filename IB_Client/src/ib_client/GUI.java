package ib_client;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
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
     * Convert Transaction.toString() to date
     *
     * @param transaction (String value)
     * @return date (String value)
     */
    private String transactionToDate(String transaction) {
        return transaction.split(";")[0];
    }

    /**
     * Convert Transaction.toString() to amount of money
     *
     * @param transaction (String value)
     * @return amount of money transferred (String value)
     */
    private String transactionToAmount(String transaction) {
        return transaction.split(";")[1];
    }

    /**
     * Convert Transaction.toString() to IBANFrom
     *
     * @param transaction (String value)
     * @return IBANFrom (String value)
     */
    private String transactionToIBANFrom(String transaction) {
        return transaction.split(";")[2];
    }

    /**
     * Convert Transaction.toString() to IBANTo
     *
     * @param transaction (String value)
     * @return IBANTo (String value)
     */
    private String transactionToIBANTo(String transaction) {
        return transaction.split(";")[3];
    }

    /**
     * Convert Transaction.toString() to description
     *
     * @param transaction (String value)
     * @return "" if description is empty, else description (String value)
     */
    private String transactionToDescription(String transaction) {
        String[] fields = transaction.split(";");

        if (fields.length < 5) {
            return "";
        } else {
            return fields[4];
        }
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
