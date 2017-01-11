/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ib_client;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Michiel van Eijkeren
 */
public class FXMLRekeningManagementController implements Initializable
{

    private GUI gui;
    private GUIController controller;
    private Stage stage;
    private String activeIBAN;

    void setStage(Stage stage)
    {
        this.stage = stage;
        stage.setHeight(mainPane.getHeight());
    }

    void setGuiController(GUIController controller)
    {
        this.controller = controller;
        controller.getAccounts();
        listViewBankAccount.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int index = listViewBankAccount.getSelectionModel().getSelectedIndex();
                activeIBAN = listViewBankAccount.getItems().get(index).split("\n")[0];
                //Set transaction of activeIBAN
                controller.getTransactions(activeIBAN);
                //Select transaction tab
                tabPaneBankAccountOptions.getSelectionModel().select(tabTransactionsList);
            }
        });
    }

    void setGui(GUI gui)
    {
        this.gui = gui;
        gui.setManagementController(this);
    }

    @FXML
    private Pane mainPane;

    @FXML
    private ListView<String> listViewBankAccount;
    @FXML
    private ComboBox comboBoxBankAccountType;
    @FXML
    private Button buttonAddBankAccount;
    @FXML
    private TabPane tabPaneBankAccountOptions;

    @FXML
    private Tab tabInternalTransaction;

    //controls on tabInternalTransaction
    @FXML
    private ComboBox comboBoxInternalTransactionBankAccountFrom;
    @FXML
    private ComboBox comboBoxInternalTransactionBankAccountTo;
    @FXML
    private TextField textFieldInternalTransactionAmountToTransfer;
    @FXML
    private TextArea textAreaInternalTransactionDesctiption;
    @FXML
    private Button buttonInternalTransactionTransferMoney;

    @FXML
    private Tab tabExternalTransaction;

    //controls on tabExternalTransaction
    @FXML
    private ComboBox comboBoxExternalTransactionBankAccountFrom;
    @FXML
    private ComboBox comboBoxExternalTransactionBankAccountToBankShortName;
    @FXML
    private TextField textFieldExternalTransactionBankAccountToPrefix;
    @FXML
    private TextField textFieldExternalTransactionAmountToTransfer;
    @FXML
    private TextField textFieldExternalTransactionBankAccountToNumber;
    @FXML
    private TextArea textAreaExternalTransactionDesctiption;
    @FXML
    private Button buttonExternalTransactionTransferMoney;

    @FXML
    private Tab tabTransactionsList;

    //controls on tabTransactionsList
    @FXML
    private ListView listViewTransactions;
    @FXML
    private ComboBox comboBoxTransactionsBankAccount;
    @FXML
    private Label labelTransactionDate;
    @FXML
    private Label labelTransactionsBankaccount;
    @FXML
    private Label labelTransactionsDescription;

    @FXML
    private Tab tabExtraOptions;

    //controls on tabExtraOptions;
    @FXML
    private ComboBox comboBoxExtraBankAccount;
    @FXML
    private Label labelExtraCurrentCreditLimit;
    @FXML
    private ComboBox comboBoxExtraNewLimit;
    @FXML
    private Button buttonExtraSubmitNewLimit;
    @FXML
    private CheckBox checkBoxExtraCreditOn;

    public FXMLRekeningManagementController() {
    }

    //controls
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    { }

    @FXML
    public void addBankAccount() {
        controller.addBankAccount();
    }

    public void setBankAccounts(ArrayList<String> accounts) {
        if (!listViewBankAccount.getItems().isEmpty()) {
            listViewBankAccount.getItems().clear();
        }
        for (String value : accounts) {
            String IBAN = accountToIBAN(value);
            String saldo = formatSaldo(accountToAmount(value));
            String bankAccount = IBAN + "\n" + saldo;
            listViewBankAccount.getItems().add(bankAccount);
        }
    }

    public void setTransactions(ArrayList<String> transactions) {
        if (!listViewTransactions.getItems().isEmpty()) {
            listViewTransactions.getItems().clear();
        }
        for (String value : transactions) {
            listViewTransactions.getItems().add(formatTransaction(value));
        }
        listViewTransactions.setCellFactory(t -> new customListCell());
    }
    
    public void setComboBoxData(ArrayList<String> accounts)
    {
        ArrayList<String> ibans = new ArrayList<>();
        for (String s : accounts)
        {
            ibans.add(accountToIBAN(s));
        }
        comboBoxExternalTransactionBankAccountFrom.getItems().addAll(ibans);
        comboBoxExternalTransactionBankAccountFrom.setValue("Selecteer rekening");
        comboBoxExtraBankAccount.getItems().addAll(ibans);
        comboBoxExtraBankAccount.setValue("Selecteer rekening");
        comboBoxTransactionsBankAccount.getItems().addAll(ibans);
        comboBoxTransactionsBankAccount.setValue("Selecteer rekening");
        comboBoxInternalTransactionBankAccountFrom.getItems().addAll(ibans);
        comboBoxInternalTransactionBankAccountFrom.setValue("Selecteer rekening");
    }

    private String formatTransaction(String value) {
        String date = transactionToDate(value);
        String amount = formatSaldo(transactionToAmount(value));
        String IBANFrom = checkIBAN(transactionToIBANFrom(value));
        String IBANTo = checkIBAN(transactionToIBANTo(value));
        
        return date + "\n" + IBANFrom + " → " + IBANTo + ";" + amount;
    }
    
    private String formatSaldo(String saldo)
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

    private String checkIBAN(String IBAN) {
        if (activeIBAN.equals(IBAN)) {
            return "Me";
        } else {
            return IBAN;
        }
    }
    
    private Color setTransactionColor(String values) {
        int indexIBAN = values.indexOf("NL");
        int indexArrow = values.indexOf("→");
        if (indexIBAN < indexArrow) {
            return Color.GREEN;
        } else {
            return Color.RED;
        }
    }
    
    public void openLoginScreen()
    {
        Stage st = new Stage();
        st.setTitle("Login");

        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("FXMLLogin.fxml"));
        Pane myPane;
        try
        {
            myPane = (Pane) myLoader.load();
            FXMLLoginController loginController = (FXMLLoginController) myLoader.getController();
            loginController.setStage(stage);
            loginController.setGuiController(controller);
            loginController.setGui(gui);
            Scene scene = new Scene(myPane);
            st.setScene(scene);
            stage.close();
            st.show();
        } catch (IOException ex)
        {
            Logger.getLogger(FXMLRekeningManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Convert Bankaccount.toString() to IBAN
     *
     * @param account (String value)
     * @return IBAN (String value)
     */
    private String accountToIBAN(String account)
    {
        return account.split(";")[0];
    }

    /**
     * Convert Bankaccount.toString() to amount of money
     *
     * @param account (String value)
     * @return amount of money (String value)
     */
    private String accountToAmount(String account)
    {
        return account.split(";")[1];
    }
    
    /**
     * Convert Transaction.toString() to date
     * @param transaction (String value)
     * @return date (String value)
     */
    private String transactionToDate(String transaction) {
        return transaction.split(";")[0];
    }
    
    /**
     * Convert Transaction.toString() to amount of money
     * @param transaction (String value)
     * @return amount of money transferred (String value)
     */
    private String transactionToAmount(String transaction) {
        return transaction.split(";")[1];
    }
    
    /**
     * Convert Transaction.toString() to IBANFrom
     * @param transaction (String value)
     * @return IBANFrom (String value)
     */
    private String transactionToIBANFrom(String transaction) {
        return transaction.split(";")[2];
    }
    
    /**
     * Convert Transaction.toString() to IBANTo
     * @param transaction (String value)
     * @return IBANTo (String value)
     */
    private String transactionToIBANTo(String transaction) {
        return transaction.split(";")[3];
    }
    
    /**
     * Convert Transaction.toString() to description
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
    
    private class customListCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            Font font = new Font("Arial", 5);
            Pane pane = null;
            if (!empty) {
                pane = new Pane();
                // left-aligned text at position 0em
                final Text leftText = new Text(item.split(";")[0]);
                leftText.setTextOrigin(VPos.BOTTOM);
                leftText.relocate(0, 0);
                // left-aligned text at position 16em 
                final Text middleText = new Text(item.split(";")[1]);
                middleText.setTextOrigin(VPos.BOTTOM);
                final double em = middleText.getLayoutBounds().getHeight();
                middleText.relocate(16 * em, 0);
                middleText.setFill(setTransactionColor(item));
                pane.getChildren().addAll(leftText, middleText);
            }
            setText("");
            setGraphic(pane);
        }
    }
}
