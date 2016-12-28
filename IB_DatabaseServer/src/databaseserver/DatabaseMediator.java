package databaseserver;

import Shared_Data.IPersistencyMediator;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michiel van Eijkeren
 */
public class DatabaseMediator extends UnicastRemoteObject implements IPersistencyMediator
{
    private static Connection con;
    private static ResultSet myRs;
    private static String connectionstring = "jdbc:mysql://localhost/internetbankieren";
    private static String user = "Internetbankieren";
    private static String pass = "GSO2016";

    public DatabaseMediator() throws RemoteException
    {
        try
        {
            con = DriverManager.getConnection(connectionstring, user, pass);
        } catch (SQLException ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int login(String naam, String woonplaats, String password) throws RemoteException
    {
        //Geldige sessie wordt ook toegevoegd aan de database als userID niet -1 is
        int userId = -1;
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT ID FROM Klant WHERE Naam = '" + naam + "' AND Woonplaats = '" + woonplaats
                    + "' AND Wachtwoord = '" + password + "'";
            myRs = statement.executeQuery(query);
            if (myRs.next())
            {
                userId = myRs.getInt("ID");
            }
            if (userId != -1) {
                statement.executeUpdate("UPDATE Klant SET GeldigeSessie = 1 WHERE ID = " + userId);
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userId;
    }

    @Override
    public void registerAccount(String name, String residence, String password) throws RemoteException
    {
        //Geldige sessie wordt ook toegevoegd aan de database
        try
        {
            Statement statement = con.createStatement();
            String query = "INSERT INTO Klant(Naam,Woonplaats,Wachtwoord,GeldigeSessie)VALUES('"
                    + name
                    + "','"
                    + residence
                    + "','"
                    + password
                    + "',1)";
            statement.executeUpdate(query);
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean addBank(String bankName, String bankShortName) throws RemoteException
    {
        boolean bankAdded = false;
        try
        {
            Statement statement = con.createStatement();
            String query = "INSERT INTO Bank(Afkorting,naam)VALUES("
                    + bankShortName + ",'"
                    + bankName
                    + "')";
            statement.executeUpdate(query);
            bankAdded = true;
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return bankAdded;
    }

    @Override
    public boolean addBankrekening(String name, String residence, String IBAN, String bankShortName) throws RemoteException {
        boolean bankAdded = false;
        try {
            Statement statement = con.createStatement();
            String query = "INSERT INTO Bankrekening(IBAN,bank_Afkorting,Klant_ID)VALUES('"
                    + IBAN + "','"
                    + bankShortName
                    + "',"
                    + getIDByUsername(name, residence)
                    + ")";
            statement.executeUpdate(query);
            bankAdded = true;
        } catch (Exception ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return bankAdded;
    }

    @Override
    public ArrayList<String> getBankRekeningenKlant(int klantID) throws RemoteException
    {
        String rekeningString = "";
        ArrayList<String> bankRekeningen = new ArrayList<>();
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT (IBAN + ';' + Saldo + ';' + Kredietlimiet) AS Bankstring from Bankrekening WHERE Klant_ID  = " + klantID;
            myRs = statement.executeQuery(query);
            if (myRs.next())
            {
                rekeningString = myRs.getString("Bankstring");
                bankRekeningen.add(rekeningString);
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bankRekeningen;
    }

    @Override
    public boolean ibanAvailable(String iban) throws RemoteException
    {
        boolean available = true;
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT IBAN from bankrekening WHERE IBAN = '" + iban + "'";
            myRs = statement.executeQuery(query);
            while (myRs.next())
            {
                available = false;
                break;
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return available;
    }

    @Override
    public boolean addTransaction(String ibanFrom, String ibanTo, double amount, String date, String description) throws RemoteException {
        boolean transactionAdded = false;
        try {
            Date dt = new Date();
//            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
//            String currentTime = sdf.format(dt);
            Statement statement = con.createStatement();
            String query = "INSERT INTO Transactie(beschrijving,bedrag,datum,Bankrekening_IBAN_Naar, Bankrekening_IBAN_Van)VALUES('"
                    + description + "',"
                    + amount + ",'"
                    + date + "','"
                    + ibanTo
                    + "','"
                    + ibanFrom
                    + "')";
            statement.executeUpdate(query);
            transactionAdded = true;
        } catch (Exception ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return transactionAdded;
    }

    @Override
    public ArrayList<String> getTransactions(String iban) throws RemoteException
    {
        String transactionString = "";
        ArrayList<String> transactions = new ArrayList<>();
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT (Datum + ';' + Bankrekening_IBAN_Van + ';' + Bankrekening_IBAN_Naar + ';' + bedrag + ';' + Beschrijving) AS TransactieString from transactie WHERE Bankrekening_IBAN_Van = '" + iban + "' OR Bankrekening_IBAN_Naar = '" + iban + "'";
            myRs = statement.executeQuery(query);
            while (myRs.next())
            {
                transactionString = myRs.getString("TransactieString");
                transactions.add(transactionString);
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transactions;
    }

    @Override
    public boolean usernameAvailable(String Username) throws RemoteException
    {
        boolean available = true;
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT (Naam + Woonplaats) AS Username from Klant WHERE Username = '" + Username + "'";
            myRs = statement.executeQuery(query);
            while (myRs.next())
            {
                available = false;
                break;
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return available;
    }

    @Override
    public String getKlantByID(int userID) throws RemoteException {
        String fields = "";
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT CONCAT_WS(';', Naam, Woonplaats) AS Fields FROM Klant WHERE ID = " + userID;
            myRs = statement.executeQuery(query);
            if (myRs.next())
            {
                fields = myRs.getString("Fields");
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fields;
    }
    
    @Override
    public ArrayList<String> getAllKlanten() throws RemoteException
    {
        String fields;
        ArrayList<String> clients = new ArrayList<>();
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT CONCAT_WS(';', Naam, Woonplaats, GeldigeSessie) AS Fields FROM Klant";
            myRs = statement.executeQuery(query);
            while (myRs.next())
            {
                fields = myRs.getString("Fields");
                clients.add(fields);
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clients;
    }

    @Override
    public ArrayList<String> getAllBankrekeningen(String shortName) throws RemoteException
    {
        String fields;
        ArrayList<String> rekeningen = new ArrayList<>();
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT CONCAT_WS(';', IBAN, Klant_ID, Saldo, Kredietlimiet) AS Fields FROM Bankrekening";
            myRs = statement.executeQuery(query);
            while (myRs.next())
            {
                fields = myRs.getString("Fields");
                rekeningen.add(fields);
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rekeningen;
    }

    @Override
    public ArrayList<String> getAllBanks() throws RemoteException {
        String fields;
        ArrayList<String> banks = new ArrayList<>();
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT CONCAT_WS(';', Naam, Afkorting) AS Fields FROM Bank";
            myRs = statement.executeQuery(query);
            while (myRs.next())
            {
                fields = myRs.getString("Fields");
                banks.add(fields);
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return banks;
    }
    
    @Override
    public ArrayList<String> getAllTransacties() throws RemoteException {
        String fields;
        ArrayList<String> transactions = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            String query = "SELECT CONCAT_WS(';', Beschrijving, Bedrag, Datum, Bankrekening_IBAN_Naar, Bankrekening_IBAN_Van) AS Fields FROM Transactie";
            myRs = statement.executeQuery(query);
            while (myRs.next()) {
                fields = myRs.getString("Fields");
                transactions.add(fields);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transactions;
    }

    @Override
    public void removeKlant(String name, String residence, String password) throws RemoteException {
        //Kan met behlulp van wachtwoord of zonder wachtwoord?
        try {
            Statement statement = con.createStatement();
            String query = "DELETE FROM Klant WHERE Naam = '" + name + "' AND Woonplaats = '" + residence
                    + "' AND Wachtwoord = '" + password + "'";
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void endSession(String name, String residence) throws RemoteException {
        try {
            Statement statement = con.createStatement();
            String query = "UPDATE Klant SET GeldigeSessie = 0 WHERE Naam = '" + name + "' AND Woonplaats = '" + residence + "'";
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void transferMoney(String IBANFrom, String IBANTo, double value) throws RemoteException {
        try {
            Statement statement = con.createStatement();
            String query = "UPDATE Bankrekening SET Saldo = Saldo - " + value + " WHERE IBAN = '" + IBANFrom + "'";
            statement.executeUpdate(query);
            query = "UPDATE Bankrekening SET Saldo = Saldo + " + value + " WHERE IBAN = '" + IBANTo + "'";
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void removeBankAccount(String IBAN) {
        try {
            Statement statement = con.createStatement();
            String query = "DELETE FROM Bankrekening WHERE IBAN = '" + IBAN + "'";
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Gets ID by username (= name + client)
     * @param name of the client
     * @param residence of the client
     * @return id of the client
     */
    private int getIDByUsername(String name, String residence) {
        int userID = 0;
        try {
            Statement statement = con.createStatement();
            String query = "SELECT ID FROM Klant WHERE Naam = '" + name + "' AND Woonplaats = '" + residence + "'";
            myRs = statement.executeQuery(query);
            if (myRs.next()) {
                userID = myRs.getInt("ID");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userID;
    }
}
