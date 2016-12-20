package Models;

import Exceptions.SessionExpiredException;
import Shared_Centrale.IBankTrans;
import Shared_Centrale.ICentrale;
import Shared_Client.IBank;
import Shared_Client.Klant;
import Shared_Data.IPersistencyMediator;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class Bank extends UnicastRemoteObject implements IBank, IBankTrans {

    private IPersistencyMediator pMediator;
    private ArrayList<Bankrekening> bankAccounts;
    private String name;
    private String shortName;
    private Administratie admin;
    private ICentrale centrale;
    
    /**
     * A bank registered at the administration.
     * @param name of the bank, if empty IllegalArgumentException
     * @param shortName
     * @param admin
     * @param centrale
     * @throws RemoteException, IllegalArgumentExcpetion
     */
    public Bank(String name, String shortName, Administratie admin, ICentrale centrale) throws RemoteException {
        bankAccounts = new ArrayList();
        this.centrale = centrale;
        this.admin = admin;
        this.name = name;
        this.shortName = shortName;
    }

    public void setPersistencyMediator(IPersistencyMediator pMediator) {
        this.pMediator = pMediator;
        try {
            setDatabaseData();
        } catch (RemoteException ex) {
            Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public String getShortName() throws RemoteException {
        return shortName;
    }
    
    @Override
    public ArrayList<String> getAccounts(Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        ArrayList<String> accounts = new ArrayList();
        if (klant == null) {
            throw new IllegalArgumentException("Client can't be null");
        }
        else if (!admin.checkSession(klant)) {
            throw new SessionExpiredException("Session has expired");
        }
        else {
            for (Bankrekening b : bankAccounts) {
                if (b.getKlant().equals(klant)) {
                    accounts.add(b.toString());
                }
            }
        }
        return accounts;
    }

    @Override
    public ArrayList<String> getTransactions(String IBAN, Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        ArrayList<String> transactions = new ArrayList();
        if (IBAN.isEmpty() || klant == null) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!admin.checkSession(klant)) {
            throw new SessionExpiredException("Session has expired");
        }
        else if (!checkIBANProperty(IBAN, klant)) {
            throw new IllegalArgumentException("IBAN is no property of this client");
        }
        else {
            transactions = centrale.getTransactions(IBAN);
        }
        return transactions;
    }

    @Override
    public void addBankAccount(Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        if (klant == null) {
            throw new IllegalArgumentException("Client can't be null");
        }
        else if (!admin.checkSession(klant)) {
            throw new SessionExpiredException("Session has expired");
        }
        else {
            bankAccounts.add(new Bankrekening(generateNewIBAN(), 0, 100, klant));
            //DB code
        }
    }

    @Override
    public boolean removeBankAccount(String IBAN, Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        boolean bool = false;
        if (IBAN.isEmpty() || klant == null) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!admin.checkSession(klant)) {
            throw new SessionExpiredException("Session has expired");
        }
        else if (!checkIBANProperty(IBAN, klant)) {
            throw new IllegalArgumentException("IBAN is no property of this client");
        }
        else {
            bankAccounts.remove(IBANToBankAccount(IBAN));
            bool = true;
        }
        return bool;
    }

    @Override
    public boolean startTransaction(Klant klant, String IBAN1, String IBAN2, double value, String description) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        boolean bool = false;
        if (IBAN1.isEmpty() || IBAN2.isEmpty() || value <= 0 || klant == null) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!admin.checkSession(klant)) {
            throw new SessionExpiredException("Session has expired");
        }
        else if (!checkIBANProperty(IBAN1, klant)) {
            throw new IllegalArgumentException("IBAN is no property of this client");
        }
        else if (!checkIBANExists(IBAN2)) {
            throw new IllegalArgumentException("IBAN doesn't exist");
        }
        else {
            centrale.startTransaction(IBAN1, IBAN2, this, value, description);
            bool = true;
        }
        return bool;
    }

    @Override
    public void addSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException {
        if (IBAN.isEmpty() || value <= 0) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!checkIBANExists(IBAN)) {
            throw new IllegalArgumentException("IBAN doesn't exists");
        }
        else {
            Bankrekening bankAccount = IBANToBankAccount(IBAN);
            bankAccount.addToBalance(value);
        }
    }

    @Override
    public void removeSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException {
        if (IBAN.isEmpty() || value <= 0) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!checkIBANExists(IBAN)) {
            throw new IllegalArgumentException("IBAN doesn't exists");
        }
        else {
            Bankrekening bankAccount = IBANToBankAccount(IBAN);
            bankAccount.removeFromBalance(value);
        }
    }
    
    /**
     * Checks if a IBAN is an existing one.
     * Gets calles when you want to check an IBAN but don't have a client.
     * @param IBAN
     * @return True if it exists, else false.
     */
    private boolean checkIBANExists(String IBAN) {
        //Deze methode staat in klasse bank, omdat we ervan uitgaan dat er maar 1 bank is.
        //Als we dit gaan uitbreiden met meer dan 1 bank, dan moet deze methode in de administatie staan.
        //Het ophalen van bankrekeningen wordt dan gedaan uit de database i.p.v. (in dit geval) de klasse Bank.
        for (Bankrekening b : bankAccounts) {
            if (b.toString().split(";")[0].equals(IBAN)) {
                return true;
            }
        }
        return false;
    }
    
     /**
     * Checks if a IBAN is an and existing one.
     * Gets called when you want to know if a IBAN is property of a client.
     * @param IBAN
     * @param klant
     * @return True if it exists, else false.
     */
    private boolean checkIBANProperty(String IBAN, Klant klant) {
        for (Bankrekening b : bankAccounts) {
            if (b.toString().split(";")[0].equals(IBAN)) {
                if (b.getKlant().equals(klant)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Generates a new IBAN at a specific bank (shortName)
     * @return IBAN as String
     */
    private String generateNewIBAN() {
        String part1 = "NL" + generateRandom(0, 99, 2);
        String part2 = shortName;
        String part3 = "0" + generateRandom(0, 999999999, 9);
        return part1 + part2 + part3;
    }
    
    /**
     * Converts a IBAN to a linked Bankrekening
     * @param IBAN as String
     * @return Bankrekening
     */
    private Bankrekening IBANToBankAccount(String IBAN) {
        Bankrekening bankAccount = null;
        for (Bankrekening b : bankAccounts) {
            if (b.toString().split(";")[0].equals(IBAN)) {
                bankAccount = b;
            }
        }
        return bankAccount;
    }
    
    /**
     * Generates a random value as String between two values.
     * @param startValue is lowest number
     * @param endValue is highest number
     * @param numberLength amount of charaters in the generated number
     * @return String
     */
    private String generateRandom(int startValue, int endValue, int numberLength) {
        Random r = new Random();
        String value = String.valueOf(r.nextInt((endValue - startValue) + 1) + startValue);
        while (value.length() != numberLength) {
            value = String.valueOf(new Random().nextInt(9) + startValue) + value;
        }
        return value;
    }
    
    /**
     * Set all database to lists in this class
     */
    private void setDatabaseData() throws RemoteException {
        //Set bankrekeningen
        for (String bankValues : pMediator.getAllBanks()) {
            bankAccounts.add(stringToBankrekening(bankValues));
        }
    }
    
    /**
     * Converts the String of klant-values to a Klant object.
     * Als de klant een geldige sessie heeft draaien wordt er ook
     * voor deze klant een sessie gestart.
     * @param values
     * @return Klant
     */
    private Bankrekening stringToBankrekening(String values) {
        return null;
    }
}
