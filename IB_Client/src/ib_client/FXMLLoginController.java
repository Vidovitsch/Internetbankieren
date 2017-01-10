package ib_client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
    boolean waitingForValidPassword = false;
    boolean loginLast = true;

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
    private void handleLoginButton()
    {
        if (isRegistering)
        {
            completeSignUp();
        } else
        {
            Login();
        }

        //controller.setBank();
    }

    public void completeSignUp()
    {
        if (textFieldPasswordGivenAccount.getText().equals(currentRegisterPassword))
        {
            if (controller.register(currentRegisterName, currentRegisterResidence, currentRegisterPassword))
            {
                OpenBankAccountManagement();
                labelWelcomeText.setText("succesvol ingelogd!");
            } else
            {
                waitingForValidPassword = true;
                textFieldAccountPassword.setDisable(false);
                textFieldPasswordGivenAccount.setDisable(true);
                buttonLoginGivenAccount.setDisable(true);
                labelWelcomeText.setText("Voer een geldig wachtwoord in");
            }
        } else
        {
            labelWelcomeText.setText("de wachtwoorden zijn niet gelijk");
        }
    }

    public void Login()
    {
        String password;
        if (loginLast)
        {
            password = textFieldPasswordGivenAccount.getText();
        } else
        {
            name = textFieldAccountName.getText();
            residence = textFieldAccountResidence.getText();
            password = textFieldAccountPassword.getText();
        }

        loginPoging++;
        if (controller.login(name, residence, password))
        {
            controller.setBank();
            labelWelcomeText.setText("succesvol ingelogd!");
            OpenBankAccountManagement();
        } else
        {
            labelWelcomeText.setText("Wachtwoord onjuist!");
        }
    }

    @FXML
    private void RegisterAccount()
    {
        if (waitingForValidPassword)
        {
            currentRegisterPassword = textFieldAccountPassword.getText();
            textFieldPasswordGivenAccount.setDisable(false);
            buttonLoginGivenAccount.setDisable(false);
        } else
        {
            try
            {
                //zet de huidig ingevoerde gegevens in onderstaande variabelen
                currentRegisterName = textFieldAccountName.getText();
                currentRegisterResidence = textFieldAccountResidence.getText();
                currentRegisterPassword = textFieldAccountPassword.getText();
                isRegistering = true;
                textFieldAccountName.setDisable(true);
                textFieldAccountPassword.setDisable(true);
                textFieldAccountResidence.setDisable(true);
                labelWelcomeText.setText("Bevestig je wachtwoord om registratie te voltooien");
                buttonLoginGivenAccount.setText("Voltooi");
                buttonLoginGivenAccount.setDisable(false);
                textFieldPasswordGivenAccount.setDisable(false);
            } catch (Exception e)
            {
                //nog veranderen in logische errormessage
                System.out.println("voer geldige gegevens in");
            }
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
        loginLast = false;
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
        buttonNotGivenAccount.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){

    }

    private void OpenBankAccountManagement()
    {
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("FXMLRekeningManagement.fxml"));
        Pane myPane;
        try
        {
            myPane = (Pane) myLoader.load();
            FXMLRekeningManagementController rekeningManagementController = (FXMLRekeningManagementController) myLoader.getController();
            rekeningManagementController.setStage(stage);
            rekeningManagementController.setGui(gui);
            rekeningManagementController.setGuiController(controller);
            Scene scene = new Scene(myPane);
            stage.setScene(scene);
            stage.setTitle("Rekening Management");
            stage.show();
        } catch (IOException ex)
        {
            Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
