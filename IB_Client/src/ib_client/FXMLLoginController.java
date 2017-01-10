package ib_client;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jdk.nashorn.internal.parser.TokenType;

/**
 *
 * @author David
 */
public class FXMLLoginController implements Initializable
{

    final double anchorHeightMoreOptions = 290;
    double anchorCurrentHeight = 0;
    double anchorWidth = 394;
    private Stage stage;
    private GUIController controller;
    private GUI gui;
    private String name;
    private String residence;
    private String currentRegisterName;
    private String currentRegisterResidence;
    private String currentRegisterPassword;
    private boolean isRegistering = false;
    int loginPoging = 0;

    public void setStage(Stage s)
    {
        this.stage = s;
    }

    void setGuiController(GUIController controller)
    {
        this.controller = controller;
        setLastLogged();
        setWelcomeText(name, residence);
    }

    void setGui(GUI gui)
    {
        this.gui = gui;
    }

    @FXML
    private Pane pane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button buttonLoginGivenAccount;

    @FXML
    private Button buttonLoginNew;

    @FXML
    private Button buttonRegisterNew;

    @FXML
    private Button buttonNotGivenAccount;

    @FXML
    private TextField textFieldPasswordGivenAccount;

    @FXML
    private TextField textFieldAccountName;

    @FXML
    private TextField textFieldAccountResidence;

    @FXML
    private TextField textFieldAccountPassword;

    @FXML
    private Label labelWelcomeText;

    @FXML
    private Label label;

    @FXML
    private void login()
    {
        if (isRegistering)
        {
            
        } else
        {
            name = textFieldAccountName.getText();
            residence = textFieldAccountResidence.getText();
            String password = textFieldAccountPassword.getText();
            loginPoging ++;
            String loginmessage = controller.login(name, residence, password);
            if (loginmessage == "")
            {
                OpenBankAccountManagement();
            }
            else{
                initErrorMessage(name);
            }
        }

        //controller.setBank();
    }
    
    public void initErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void loginGivenAccount()
    {
        String password = textFieldPasswordGivenAccount.getText();
        controller.login(name, residence, password);
        controller.setBank();
    }

    @FXML
    private void RegisterAccount()
    {
        try
        {
            //zet de huidig ingevoerde gegevens tijdelijk in onderstaande variabelen
            currentRegisterName = textFieldAccountName.getText();
            currentRegisterResidence = textFieldAccountResidence.getText();
            currentRegisterPassword = textFieldAccountPassword.getText();
            isRegistering = true;
            textFieldAccountName.setDisable(true);
            textFieldAccountPassword.setDisable(true);
            textFieldAccountPassword.setDisable(true);
            buttonNotGivenAccount.setDisable(true);
            labelWelcomeText.setText("Bevestig je wachtwoord om registratie te voltooien");
            buttonLoginGivenAccount.setText("Voltooi Registratie");

        } catch (Exception e)
        {
            //nog veranderen in logische errormessage
            System.out.println("voer geldige gegevens in");
        }
    }

    @FXML
    private void rolloutAnchorPane(ActionEvent event)
    {
        anchorCurrentHeight = stage.getHeight();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //anchorCurrentHeight = anchorPane.getHeight();
                while (anchorCurrentHeight < anchorHeightMoreOptions)
                {
                    try
                    {
                        anchorCurrentHeight++;
                        Platform.runLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                pane.setPrefHeight(anchorCurrentHeight);
                                stage.setHeight(anchorCurrentHeight);
                            }
                        });
                        Thread.currentThread().sleep(2);
                    } catch (InterruptedException ex)
                    {
                        Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
        disableWelcomeControls();
    }

    private void setLastLogged()
    {
        name = "";
        residence = "";
        String[] properties = controller.getLastLogged();
        if (properties != null)
        {
            name = properties[0];
            residence = properties[1];
        }
    }

    private void setWelcomeText(String name, String residence)
    {
        if (name.isEmpty() || residence.isEmpty())
        {
            labelWelcomeText.setText("Authenticate to login");
            buttonNotGivenAccount.setText("Press to login");
            disableWelcomeControls();
        } else
        {
            labelWelcomeText.setText("Welcome " + name + residence + "!");
        }
    }

    private void disableWelcomeControls()
    {
        buttonLoginGivenAccount.setDisable(true);
        textFieldPasswordGivenAccount.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }

    private void OpenBankAccountManagement()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
