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
            System.out.println(con.toString());
        } catch (SQLException ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int Login(String username, String password) throws RemoteException
    {
        int userId = -1;
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT ID, (Naam + Woonplaats) AS Username from Klant WHERE Username = '" + username + "' AND Wachtwoord = '" + password + "'";
            myRs = statement.executeQuery(query);
            if (myRs.next())
            {
                userId = myRs.getInt("ID");
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userId;
    }

    @Override
    public boolean registerAccount(String name, String Residence, String Password) throws RemoteException
    {
        boolean registered = false;
        try
        {
            Statement statement = con.createStatement();
            String query = "INSERT INTO Klant(Naam,Woonplaats,Wachtwoord)VALUES("
                    + name + ",'"
                    + Residence
                    + "','"
                    + Password
                    + "')";
            statement.executeUpdate(query);
            registered = true;
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return registered;
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
    public boolean addBankrekening(int clientID, String iban, String bankShortName) throws RemoteException
    {
        boolean bankAdded = false;
        try
        {
            Statement statement = con.createStatement();
            String query = "INSERT INTO Bankrekening(IBAN,bank_Afkorting,Klant_ID)VALUES("
                    + iban + ",'"
                    + bankShortName
                    + "',"
                    + clientID
                    + ")";
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
    public boolean addTransaction(String ibanFrom, String ibanTo, double amount, Date date, String description) throws RemoteException
    {

        boolean transactionAdded = false;
        try
        {
            Date dt = new Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
            String currentTime = sdf.format(dt);
            Statement statement = con.createStatement();
            String query = "INSERT INTO Transaction(beschrijving,bedrag,datum,Bankrekening_IBAN_Naar, Bankrekening_IBAN_Van)VALUES("
                    + description + ","
                    + amount
                    + ",STR_TO_DATE('"
                    + currentTime + "', '%d-%m-%Y'),'"
                    + ibanTo
                    + "','"
                    + ibanFrom
                    + "')";
            statement.executeUpdate(query);
            transactionAdded = true;
        } catch (Exception ex)
        {
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
            if (myRs.next())
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
    public ArrayList<String> getAllKlanten() throws RemoteException
    {
        String fields;
        ArrayList<String> clients = new ArrayList<>();
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT CONCAT_WS(';', Naam, Woonplaats, GeldigeSessie) AS Fields FROM Klant";
            myRs = statement.executeQuery(query);
            if (myRs.next())
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
    public ArrayList<String> getAllBankrekeningen() throws RemoteException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<String> getAllBanks() throws RemoteException {
        String fields;
        ArrayList<String> transactions = new ArrayList<>();
        try
        {
            Statement statement = con.createStatement();
            String query = "SELECT CONCAT_WS(';', Naam, Afkorting) AS Fields FROM Bank";
            myRs = statement.executeQuery(query);
            if (myRs.next())
            {
                fields = myRs.getString("Fields");
                transactions.add(fields);
            }
        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transactions;
    }
}
