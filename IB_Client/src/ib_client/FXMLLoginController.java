/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ib_client;

import com.sun.scenario.effect.impl.Renderer;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
    
    public void setStage(Stage s)
    {
        this.stage = s;
    }

    void setGuiController(FXMLLoginController controller)
    {
        this.controller = this.controller;
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
    private void handleButtonAction(ActionEvent event)
    {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
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
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }


}
