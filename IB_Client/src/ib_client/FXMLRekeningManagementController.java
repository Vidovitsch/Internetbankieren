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
import javafx.event.EventHandler;
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
import javafx.scene.input.MouseEvent;
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
public class FXMLRekeningManagementController implements Initializable {

    private GUI gui;
    private GUIController controller;
    private Stage stage;
    private String activeIBAN = "";

    private ArrayList<String[]> transactionCollection = new ArrayList();

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
    private TextField textFieldInternalTransactionAmountToTransfer2;
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
    private TextField textFieldExternalTransactionAmountToTransfer2;
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

    void setStage(Stage stage) {
        this.stage = stage;
        stage.setHeight(mainPane.getHeight());
    }

    void setGuiController(GUIController controller) {
        this.controller = controller;
        controller.getAccounts();
        setComboBoxBankData();
        
        buttonInternalTransactionTransferMoney.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String IBANFrom = activeIBAN;
                String IBANTo = (String) comboBoxInternalTransactionBankAccountTo.getSelectionModel().getSelectedItem();
                String euros = textFieldInternalTransactionAmountToTransfer.getText();
                String cents = textFieldInternalTransactionAmountToTransfer2.getText();
                String description = textAreaInternalTransactionDesctiption.getText();
                if (IBANFrom.isEmpty() || IBANTo.isEmpty()) {
                    gui.initErrorMessage("Fill all the fields first");
                } else {
                    startTransaction(IBANFrom, IBANTo, euros, cents, description);
                }
                emtpyFields();
            }
        });
        
        buttonExternalTransactionTransferMoney.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String IBANFrom = activeIBAN;
                
                String prefix = textFieldExternalTransactionBankAccountToPrefix.getText();
                String shortName = (String) comboBoxExternalTransactionBankAccountToBankShortName.getSelectionModel().getSelectedItem();
                String number = textFieldExternalTransactionBankAccountToNumber.getText();
                String IBANTo = prefix + shortName + number;
                
                String euros = textFieldExternalTransactionAmountToTransfer.getText();
                String cents = textFieldExternalTransactionAmountToTransfer2.getText();
                String description = textAreaExternalTransactionDesctiption.getText();
                if (IBANFrom.isEmpty() || IBANTo.isEmpty()) {
                    gui.initErrorMessage("Fill all the fields first");
                } else if (IBANFrom.equals(IBANTo)) {
                    gui.initErrorMessage("Can't transfer money to the same bank account");
                } else {
                    startTransaction(IBANFrom, IBANTo, euros, cents, description);
                }
                emtpyFields();
            }
        });
        
        listViewBankAccount.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int index = listViewBankAccount.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    activeIBAN = listViewBankAccount.getItems().get(index).split("\n")[0];
                    //Set transaction of activeIBAN
                    controller.getTransactions(activeIBAN);
                    //Select transaction tab
                    tabPaneBankAccountOptions.getSelectionModel().select(tabTransactionsList);
                    updateComboBoxIBAN();
                }
            }
        });

        listViewTransactions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int index = listViewTransactions.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    String formattedTransaction = (String) listViewTransactions.getItems().get(index);
                    for (String[] fields : transactionCollection) {
                        if (fields[1].equals(formattedTransaction)) {
                            //Setting all variables
                            String date = gui.formatDateTime(gui.transactionToDate(fields[0]));
                            String description = gui.transactionToDescription(fields[0]);
                            String IBANFrom = gui.checkIBAN(gui.transactionToIBANFrom(fields[0]), activeIBAN);
                            String IBANTo = gui.checkIBAN(gui.transactionToIBANTo(fields[0]), activeIBAN);
                            String IBAN;
                            if (IBANFrom.contains("NL")) {
                                IBAN = IBANFrom;
                            } else {
                                IBAN = IBANTo;
                            }
                            //Setting fields in the gui
                            labelTransactionDate.setText(date);
                            labelTransactionsBankaccount.setText(IBAN);
                            labelTransactionsDescription.setText(description);
                        }
                    }
                }
            }
        });

        comboBoxTransactionsBankAccount.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int index = comboBoxTransactionsBankAccount.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    activeIBAN = comboBoxTransactionsBankAccount.getItems().get(index).toString();
                    //Set transaction of activeIBAN
                    controller.getTransactions(activeIBAN);
                    //Select transaction tab
                    updateComboBoxIBAN();
                }
            }
        });

        comboBoxInternalTransactionBankAccountFrom.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int index = comboBoxInternalTransactionBankAccountFrom.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    activeIBAN = comboBoxInternalTransactionBankAccountFrom.getItems().get(index).toString();
                    //Set transaction of activeIBAN
                    controller.getTransactions(activeIBAN);
                    //Select transaction tab
                    updateComboBoxIBAN();
                }
            }
        });
        
        //Set textFields with the correct settings
        textFieldInternalTransactionAmountToTransfer.lengthProperty()
                .addListener(new textFieldLengthListener(textFieldInternalTransactionAmountToTransfer, 6));
        textFieldInternalTransactionAmountToTransfer.textProperty()
                .addListener(new textFieldNumberListener(textFieldInternalTransactionAmountToTransfer));
        textFieldInternalTransactionAmountToTransfer2.lengthProperty()
                .addListener(new textFieldLengthListener(textFieldInternalTransactionAmountToTransfer2, 2));
        textFieldInternalTransactionAmountToTransfer2.textProperty()
                .addListener(new textFieldNumberListener(textFieldInternalTransactionAmountToTransfer2));
        textFieldExternalTransactionAmountToTransfer.lengthProperty()
                .addListener(new textFieldLengthListener(textFieldExternalTransactionAmountToTransfer, 6));
        textFieldExternalTransactionAmountToTransfer.textProperty()
                .addListener(new textFieldNumberListener(textFieldExternalTransactionAmountToTransfer));
        textFieldExternalTransactionAmountToTransfer2.lengthProperty()
                .addListener(new textFieldLengthListener(textFieldExternalTransactionAmountToTransfer2, 2));
        textFieldExternalTransactionAmountToTransfer2.textProperty()
                .addListener(new textFieldNumberListener(textFieldExternalTransactionAmountToTransfer2));
        
        textFieldExternalTransactionBankAccountToPrefix.lengthProperty()
                .addListener(new textFieldLengthListener(textFieldExternalTransactionBankAccountToPrefix, 4));
        textFieldExternalTransactionBankAccountToNumber.lengthProperty()
                .addListener(new textFieldLengthListener(textFieldExternalTransactionBankAccountToNumber, 10));
        textFieldExternalTransactionBankAccountToNumber.textProperty()
                .addListener(new textFieldNumberListener(textFieldExternalTransactionBankAccountToNumber));
    }

    void setGui(GUI gui) {
        this.gui = gui;
        gui.setManagementController(this);
    }

    //controls
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void openLoginScreen() {
        Stage st = new Stage();
        st.setTitle("Login");

        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("FXMLLogin.fxml"));
        Pane myPane;
        try {
            myPane = (Pane) myLoader.load();
            FXMLLoginController loginController = (FXMLLoginController) myLoader.getController();
            loginController.setStage(stage);
            loginController.setGuiController(controller);
            loginController.setGui(gui);
            Scene scene = new Scene(myPane);
            st.setScene(scene);
            stage.close();
            st.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLRekeningManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void addBankAccount() {
        controller.addBankAccount();
    }

    @FXML
    public void logout() {
        controller.logout();
    }
    
    public void setBankAccounts(ArrayList<String> accounts) {
        if (!listViewBankAccount.getItems().isEmpty()) {
            listViewBankAccount.getItems().clear();
        }
        for (String value : accounts) {
            String IBAN = gui.accountToIBAN(value);
            String saldo = gui.formatSaldo(gui.accountToAmount(value));
            String bankAccount = IBAN + "\n" + saldo;
            listViewBankAccount.getItems().add(bankAccount);
        }
    }

    public void setTransactions(ArrayList<String> transactions) {
        if (!listViewTransactions.getItems().isEmpty()) {
            listViewTransactions.getItems().clear();
        }
        for (String value : transactions) {
            transactionCollection.add(new String[]{value, gui.formatTransaction(value, activeIBAN)});
            listViewTransactions.getItems().add(gui.formatTransaction(value, activeIBAN));
        }
        listViewTransactions.setCellFactory(t -> new transactionListCell());
    }

    private void setComboBoxBankData() {
        //Implemntation has to be changed if more banks a implemented
        String shortName = controller.getBankShortName();
        comboBoxExternalTransactionBankAccountToBankShortName.getItems().add(shortName);
        comboBoxExternalTransactionBankAccountToBankShortName.setValue(shortName);
    }
    
    public void setComboBoxData(ArrayList<String> accounts) {
        //Clear lists if filled
        if (!comboBoxExternalTransactionBankAccountFrom.getItems().isEmpty()) {
            comboBoxExternalTransactionBankAccountFrom.getItems().clear();
            comboBoxExtraBankAccount.getItems().clear();
            comboBoxInternalTransactionBankAccountFrom.getItems().clear();
            comboBoxTransactionsBankAccount.getItems().clear();
        }

        ArrayList<String> ibans = new ArrayList<>();
        for (String s : accounts) {
            ibans.add(gui.accountToIBAN(s));
        }
        comboBoxExternalTransactionBankAccountFrom.getItems().addAll(ibans);
        comboBoxExternalTransactionBankAccountFrom.setValue("Selecteer rekening");

        comboBoxExtraBankAccount.getItems().addAll(ibans);
        comboBoxExtraBankAccount.setValue("Selecteer rekening");

        comboBoxTransactionsBankAccount.getItems().addAll(ibans);
        comboBoxTransactionsBankAccount.setValue("Selecteer rekening");

        comboBoxInternalTransactionBankAccountFrom.getItems().addAll(ibans);
        comboBoxInternalTransactionBankAccountFrom.setValue("Selecteer rekening");

        //If IBAN is selected previously, this IBAN gets selected automaticly on update
        if (!activeIBAN.isEmpty()) {
            updateComboBoxIBAN();
        }
    }

    private void updateComboBoxIBAN() {
        comboBoxInternalTransactionBankAccountFrom.getSelectionModel().select(activeIBAN);
        comboBoxExternalTransactionBankAccountFrom.getSelectionModel().select(activeIBAN);
        comboBoxTransactionsBankAccount.getSelectionModel().select(activeIBAN);
        updateInternalComboBoxIBANTo();
    }

    private void updateInternalComboBoxIBANTo() {
        ArrayList<String> IBANs = new ArrayList();
        for (Object obj : comboBoxInternalTransactionBankAccountFrom.getItems()) {
            String IBAN = (String) obj;
            if (!IBAN.equals(activeIBAN)) {
                IBANs.add(IBAN);
            }
        }
        if (!comboBoxInternalTransactionBankAccountTo.getItems().isEmpty()) {
            comboBoxInternalTransactionBankAccountTo.getItems().clear();
        }
        comboBoxInternalTransactionBankAccountTo.getItems().addAll(IBANs);
        comboBoxInternalTransactionBankAccountTo.setValue("Selecteer rekening");
    }

    private void startTransaction(String IBANFrom, String IBANTo, String euros, String cents, String description) {
        double amount = gui.amountToDouble(euros, cents);
        String formattedAmount = gui.formatSaldo(String.valueOf(amount));
        if (gui.initAlertMessage("Are you sure you want to transfer " + formattedAmount + " to " + IBANTo + "?")) {
            controller.startTransaction(IBANFrom, IBANTo, amount, description);
        }
    }
    
    private void emtpyFields() {
        textFieldInternalTransactionAmountToTransfer.setText("");
        textFieldInternalTransactionAmountToTransfer2.setText("");
        textFieldExternalTransactionAmountToTransfer.setText("");
        textFieldExternalTransactionAmountToTransfer2.setText("");
    }
    
    private class transactionListCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
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
                middleText.setFill(gui.setTransactionColor(item));
                pane.getChildren().addAll(leftText, middleText);
            }
            setText("");
            setGraphic(pane);
        }
    }
    
    private class textFieldLengthListener implements ChangeListener<Number> {
        private int limit;
        private TextField textField;
        
        public textFieldLengthListener(TextField textField, int limit) {
            this.textField = textField;
            this.limit = limit;
        }
        
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if (newValue.intValue() > oldValue.intValue()) {
                   if (textField.getText().length() >= limit) {
                       textField.setText(textField.getText().substring(0, limit));
                    }
            }
        }
    }
    
    private class textFieldNumberListener implements ChangeListener<String> {
        private TextField textField;
        
        public textFieldNumberListener(TextField textField) {
            this.textField = textField;
        }
        
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        } 
    }
}
