//Aanpassing t.o.v. architectuurdocument:
//IAdminCheck bestaat niet meer, wordt nu lokaal geregeld op de Server
//Klant heeft geen attribuut password
//IAdmin heeft een extra methode logout
//BankAccount heeft een extra parameter in constructor: double credit
//Bank heeft geen addTransactie meer (transacties worden opgeslagen op de overboekcentrale)
//IBank illegalargumentexceptions toegevoegd
//Bank heeft een extra parameter in constructor Administratie admin
//Transacties 2 IBAN's toegevoegd
//IBankTrans heeft geen addTransactie meer (transacties worden opgeslagen op de overboekcentrale)



package ib_client;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author David
 */
public class GUI extends Application
{
    private GUIController controller;
    
    @Override
    public void start(Stage stage) throws Exception {
        controller = new GUIController(this);
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    public void setAccountList(ArrayList<String> accounts) {
        
    }
    
    public void setTransactionList(ArrayList<String> transactions) {
        
    }
    
    public void initErrorMessage(String message) {
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}
