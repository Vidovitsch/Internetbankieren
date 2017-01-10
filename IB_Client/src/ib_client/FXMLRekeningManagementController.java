/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ib_client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
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

    void setStage(Stage stage)
    {
        this.stage = stage;
        stage.setHeight(mainPane.getHeight());
    }

    void setGuiController(GUIController controller)
    {
        this.controller = controller;
    }

    void setGui(GUI gui)
    {
        this.gui = gui;
    }

    @FXML
    private Pane mainPane;
    
    @FXML
    private ListView listViewBankAccount;
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

    public void setStage() {
        
    }
    
    //controls
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

    }
}
