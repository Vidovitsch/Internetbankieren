//Aanpassing t.o.v. architectuurdocument:
//
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
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
        //controller.register("David", "Eindhoven", "123456789");
        controller.login("David", "Eindhoven", "123456789");
    }

    public void setAccountList(ArrayList<String> accounts) {
        
    }
    
    public void setTransactionList(ArrayList<String> transactions) {
        
    }
    
    public void initErrorMessage(String message) {
        
    }
    
    /**
     * Convert Bankaccount.toString() to IBAN
     * @param account (String value)
     * @return IBAN (String value)
     */
    private String accountToIBAN(String account) {
        return account.split(";")[0];
    }
    
    /**
     * Convert Bankaccount.toString() to amount of money
     * @param account (String value)
     * @return amount of money (String value)
     */
    private String accountToAmount(String account) {
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
     * Convert Transaction.toString() to IBANTo
     * @param transaction (String value)
     * @return IBANTo (String value)
     */
    private String transactionToIBANTo(String transaction) {
        return transaction.split(";")[2];
    }
    
    /**
     * Convert Transaction.toString() to IBANFrom
     * @param transaction (String value)
     * @return IBANFrom (String value)
     */
    private String transactionToIBANFrom(String transaction) {
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}
