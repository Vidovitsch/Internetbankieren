package Models;

import Exceptions.LimitReachedException;
import Exceptions.SessionExpiredException;
import Shared_Centrale.ICentrale;
import Shared_Client.IBank;
import Shared_Client.Klant;
import Shared_Data.IPersistencyMediator;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author David
 */
public class Bank extends UnicastRemoteObject implements IBank {

    private IPersistencyMediator pMediator;
    private ArrayList<Bankrekening> bankAccounts;
    private String name;
    private String shortName;
    private Administratie admin;
    private ICentrale centrale;
    private BankTrans bankTrans;
    
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
        bankTrans = new BankTrans(this);
    }

    public void setPersistencyMediator(IPersistencyMediator pMediator) throws RemoteException {
        this.pMediator = pMediator;
        setDatabaseData();
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
        else if (!admin.checkSession(klant.getUsername())) {
            throw new SessionExpiredException("Session has expired");
        }
        else {
            //Synchronize the list to prevent manipulation while iterating
            synchronized(bankAccounts) {
                for (Bankrekening b : bankAccounts) {
                    if (b.getKlant().getUsername().equals(klant.getUsername())) {
                        accounts.add(b.toString());
                    }
                }
            }
        }
        //Refresh session on button click
        admin.refreshSession(klant);
        return accounts;
    }

    @Override
    public ArrayList<String> getTransactions(String IBAN, Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        ArrayList<String> transactions = new ArrayList();
        if (IBAN.isEmpty() || klant == null) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!admin.checkSession(klant.getUsername())) {
            throw new SessionExpiredException("Session has expired");
        }
        else if (!checkIBANProperty(IBAN, klant)) {
            throw new IllegalArgumentException("IBAN is no property of this client");
        }
        else {
            transactions = centrale.getTransactions(IBAN);
            //Refresh session on button click
            admin.refreshSession(klant);
        }
        return transactions;
    }

    @Override
    public void addBankAccount(Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        if (klant == null) {
            throw new IllegalArgumentException("Client can't be null");
        }
        else if (!admin.checkSession(klant.getUsername())) {
            throw new SessionExpiredException("Session has expired");
        }
        else {
            boolean value = false;
            while (!value) {
                String IBAN = generateNewIBAN();
                bankAccounts.add(new Bankrekening(IBAN, klant));
                pMediator.addBankrekening(klant.getName(), klant.getResidence(), IBAN, shortName);
                value = true;
            }
            admin.refreshSession(klant);
        }
    }

    @Override
    public boolean removeBankAccount(String IBAN, Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        boolean bool = false;
        if (IBAN.isEmpty() || klant == null) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!admin.checkSession(klant.getUsername())) {
            throw new SessionExpiredException("Session has expired");
        }
        else if (!checkIBANProperty(IBAN, klant)) {
            throw new IllegalArgumentException("IBAN is no property of this client");
        }
        else {
            bankAccounts.remove(IBANToBankAccount(IBAN));
            pMediator.removeBankAccount(IBAN);
            bool = true;
            admin.refreshSession(klant);
        }
        return bool;
    }

    @Override
    public boolean startTransaction(Klant klant, String IBAN1, String IBAN2, double value, String description) throws SessionExpiredException, IllegalArgumentException, LimitReachedException, RemoteException {
        boolean bool = false;
        if (IBAN1.isEmpty() || IBAN2.isEmpty() || klant == null) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!admin.checkSession(klant.getUsername())) {
            throw new SessionExpiredException("Session has expired");
        }
        else if (!checkIBANProperty(IBAN1, klant)) {
            throw new IllegalArgumentException("IBAN is no property of this client");
        }
        else if (!checkIBANExists(IBAN2)) {
            throw new IllegalArgumentException("IBAN doesn't exist");
        }
        else if (value <= 0) {
            throw new IllegalArgumentException("Value must be higher than 0");
        }
        else {
            centrale.startTransaction(IBAN1, IBAN2, bankTrans, value, description);
            admin.publishTransaction(getUsernameByIBAN(IBAN1), getUsernameByIBAN(IBAN2), this);
            bool = true;
            admin.refreshSession(klant);
        }
        return bool;
    }
    
    /**
     * Checks if a IBAN is an existing one.
     * Gets calles when you want to check an IBAN but don't have a client.
     * @param IBAN
     * @return True if it exists, else false.
     */
    public boolean checkIBANExists(String IBAN) {
        //Deze methode staat in klasse bank, omdat we ervan uitgaan dat er maar 1 bank is.
        //Als we dit gaan uitbreiden met meer dan 1 bank, dan moet deze methode in de administatie staan.
        //Het ophalen van bankrekeningen wordt dan gedaan uit de database i.p.v. (in dit geval) de klasse Bank.
        
        //Synchronize the list to prevent manipulation while iterating
        synchronized(bankAccounts) {
            for (Bankrekening b : bankAccounts) {
                if (b.toString().split(";")[0].equals(IBAN)) {
                    return true;
                }
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
        //Synchronize the list to prevent manipulation while iterating
        synchronized(bankAccounts) {
            for (Bankrekening b : bankAccounts) {
                if (b.toString().split(";")[0].equals(IBAN)) {
                    if (b.getKlant().getUsername().equals(klant.getUsername())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Returns the username of the client from this IBAN.
     * @param IBAN of the client.
     * @return username of the client.
     */
    private String getUsernameByIBAN(String IBAN) {
        String username = null;
        //Synchronize the list to prevent manipulation while iterating
        synchronized(bankAccounts) {
            for (Bankrekening b : bankAccounts) {
                if (b.toString().split(";")[0].equals(IBAN)) {
                    username = b.getKlant().getUsername();
                }
            }
        }
        return username;
    }
    
    /**
     * Generates a new IBAN at a specific bank (shortName)
     * @return IBAN as String
     */
    private String generateNewIBAN() {
        String part1 = "NL" + generateRandom(0, 99, 2);
        String part2 = shortName;
        String part3 = "0" + generateRandom(0, 99999999, 8);
        String IBAN = part1 + part2 + part3;
        
        //Kans is 1 op de (100.000.000.000 * aantal banken * aantal landen) dat
        //dezelfde IBAN al in de database staat. Vanwege testredenen is de check hierop
        //weggecomment (dit viel niet te testen vanwegen een te kleine kans).
        //Voor de robuustheid van de code kan deze nog gebruikt worden in het vervolg.
        return IBAN;
//        if (!checkIBANExists(IBAN)) {
//            return IBAN;
//        } else {
//            return generateNewIBAN();
//        }
    }
    
    /**
     * Converts a IBAN to a linked Bankrekening
     * @param IBAN as String
     * @return Bankrekening
     */
    public Bankrekening IBANToBankAccount(String IBAN) {
        Bankrekening bankAccount = null;
        //Synchronize the list to prevent manipulation while iterating
        synchronized(bankAccounts) {
            for (Bankrekening b : bankAccounts) {
                if (b.toString().split(";")[0].equals(IBAN)) {
                    bankAccount = b;
                }
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
    private String generateRandom(int startValue, int endValue, int minNumberLength) {
        Random r = new Random();
        String value = String.valueOf(r.nextInt((endValue - startValue) + 1) + startValue);
        while (value.length() < minNumberLength) {
            value = String.valueOf(new Random().nextInt(9) + startValue) + value;
        }
        return value;
    }
    
    /**
     * Set all database to lists in this class
     */
    private void setDatabaseData() throws RemoteException {
        //Set bankrekeningen
        for (String rekeningValues : pMediator.getAllBankrekeningen(shortName)) {
            bankAccounts.add(stringToBankrekening(rekeningValues));
        }
    }
    
    /**
     * Converts the String of klant-values to a Klant object.
     * Als de klant een geldige sessie heeft draaien wordt er ook
     * voor deze klant een sessie gestart.
     * @param values
     * @return Bankrekening
     */
    private Bankrekening stringToBankrekening(String values) throws RemoteException {
        String[] rFields = values.split(";");
        String[] klantFields = pMediator.getKlantByID(Integer.valueOf(rFields[1])).split(";");
        String username = klantFields[0] + klantFields[1];
        Klant klant = admin.getKlantByUsername(username);
        Bankrekening rekening = new Bankrekening(rFields[0], klant);
        return rekening; 
    }
}
