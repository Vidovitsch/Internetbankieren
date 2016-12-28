package ib_client;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    
    public void setStage(Stage s) {
        this.stage = s;
    }

    void setGuiController(GUIController controller) {
        this.controller = controller;
        setLastLogged();
        setWelcomeText(name, residence);
    }
    
    void setGui(GUI gui) {
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
    private void login() {
        name = textFieldAccountName.getText();
        residence = textFieldAccountResidence.getText();
        String password = textFieldAccountPassword.getText();
        controller.login(name, residence, password);
        //controller.setBank();
    }
    
    @FXML
    private void loginGivenAccount() {
        String password = textFieldPasswordGivenAccount.getText();
        controller.login(name, residence, password);
        controller.setBank();
    }
    
    @FXML
    private void openRegisterScreen() {
        
    }

    @FXML
    private void rolloutAnchorPane(ActionEvent event) {
        anchorCurrentHeight = stage.getHeight();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //anchorCurrentHeight = anchorPane.getHeight();
                while (anchorCurrentHeight < anchorHeightMoreOptions) {
                    try {
                        anchorCurrentHeight++;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pane.setPrefHeight(anchorCurrentHeight);
                                stage.setHeight(anchorCurrentHeight);
                            }
                        });
                        Thread.currentThread().sleep(2);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
        disableWelcomeControls();
    }
    
    private void setLastLogged() {
        name = "";
        residence = "";
        String[] properties = controller.getLastLogged();
        if (properties != null) {
            name = properties[0];
            residence = properties[1];
        }
    }
    
    private void setWelcomeText(String name, String residence) {
        if (name.isEmpty() || residence.isEmpty()) {
            labelWelcomeText.setText("Authenticate to login");
            buttonNotGivenAccount.setText("Press to login");
            disableWelcomeControls();
        } else {
            labelWelcomeText.setText("Welcome " + name + residence + "!");
        }
    }
    
    private void disableWelcomeControls() {
        buttonLoginGivenAccount.setDisable(true);
        textFieldPasswordGivenAccount.setDisable(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }
}
