package Shared_Data;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Michiel van Eijkeren
 */
public interface IPersistencyMediator extends Remote
{
    /**
     * Logt de klant in, Geeft -1 terug wanneer dit niet gelukt is, anders geeft deze methode
     * het id van de zojuist ingelogde klant terug
     * @param naam
     * @param woonplaats
     * @param password password/hash
     * @return
     * @throws RemoteException
     */
    public int login(String naam, String woonplaats, String password) throws RemoteException;

    /**
     * Geeft true terug als de username (naam+woonplaats) beschikbaar is, en false als dit niet zo is
     * @param Username Name + Residence
     * @return
     * @throws RemoteException
     */
    public boolean usernameAvailable(String Username) throws RemoteException;

    /**
     * probeert de account te registreren en geeft true terug als dit lukt, wanneer hiet iets mis gaat geeft deze false terug
     * @param name
     * @param Residence
     * @param Password
     * @throws RemoteException
     */
    public void registerAccount(String name, String Residence, String Password) throws RemoteException;
    
    /**
     * Voegt een bank toe aan de database, geeft true terug wanneer dit lukt, anders false
     * @param bankName          Volledige naam van de bank
     * @param bankShortName     afkorting die aan het begin van het IBAN nummer te vinden is
     * @return
     * @throws RemoteException
     */
    public boolean addBank(String bankName, String bankShortName) throws RemoteException;
    
    /**
     * voegt een bankrekening toe bij de betreffende bank, voor de betreffende klant
     * @param name
     * @param residence
     * @param iban          uniek nummer voor de nieuwe rekening
     * @param bankShortName afkorting voor iban
     * @return
     * @throws RemoteException
     */
    public boolean addBankrekening(String name, String residence, String iban, String bankShortName) throws RemoteException;
    
    /**
     * Geeft alle bankrekeningen van de betreffende klant terug in String-vorm
     * @param klantID   identifier voor de klant
     * @return
     * @throws RemoteException
     */
    public ArrayList<String> getBankRekeningenKlant(int klantID) throws RemoteException;
    
    /**
     * kijkt of het gegeven iban bezet is, als deze bezet is geeft de methode false terug, anders true
     * @param iban
     * @return
     * @throws RemoteException
     */
    public boolean ibanAvailable(String iban) throws RemoteException;
    
    /**
     * voegt een transactie toe aan de database en geeft true terug als dit lukt, anders false
     * @param ibanFrom
     * @param ibanTo
     * @param amount
     * @param date
     * @param description
     * @return
     * @throws RemoteException
     */
    public boolean addTransaction(String ibanFrom, String ibanTo, double amount, String date, String description) throws RemoteException;
    
    /**
     * vraagt een lijst op van alle transacties die bij het meegegeven iban horen in String-vorm
     * @param iban
     * @return
     * @throws RemoteException
     */
    public ArrayList<String> getTransactions(String iban) throws RemoteException;
    
    public String getKlantByID(int userID) throws RemoteException;
    
    /**
     * geeft een lijst van alle klanten terug in String-vorm --> Name;Residence;id
     * @return
     * @throws RemoteException
     */
    public ArrayList<String> getAllKlanten() throws RemoteException;
    
    /**
     * geeft een lijst van alle bankrekeningen terug in Stringvorm
     * @param shortName
     * @return
     * @throws RemoteException
     */
    public ArrayList<String> getAllBankrekeningen(String shortName) throws RemoteException;
    
    public ArrayList<String> getAllBanks() throws RemoteException;
    
    public ArrayList<String> getAllTransacties() throws RemoteException;
    
    public void removeKlant(String username, String residence, String password) throws RemoteException;
    
    public void endSession(String name, String residence) throws RemoteException;
    
    public void transferMoney(String IBANFrom, String IBANTo, double value) throws RemoteException;
    
    public void removeBankAccount(String IBAN) throws RemoteException;
}
