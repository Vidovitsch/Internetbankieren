package Models;

import Exceptions.SessionExpiredException;
import Shared_Centrale.IBankTrans;
import Shared_Centrale.ICentrale;
import Shared_Centrale.ITransactie;
import Shared_Client.IBank;
import Shared_Client.Klant;
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
public class Bank extends UnicastRemoteObject implements IBank, IBankTrans{

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
        this.shortName = shortName;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public ArrayList<String> getAccounts(Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        ArrayList<String> accounts = new ArrayList();
        if (klant == null) {
            throw new IllegalArgumentException("Client can't be null");
        }
        else if (!admin.checkSession(klant)) {
            throw new SessionExpiredException("Session has expired for this client");
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
            throw new SessionExpiredException("Session has expired for this client");
        }
        else if (!admin.checkIBANProperty(IBAN, klant)) {
            throw new IllegalArgumentException("IBAN is no property of this client");
        }
        else {
            ArrayList<ITransactie> transList = centrale.getTransactions(IBAN);
            for (ITransactie trans : transList) {
                transactions.add(transactionToString(trans));
            }
        }
        return transactions;
    }

    @Override
    public void addAccount(Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        if (klant == null) {
            throw new IllegalArgumentException("Client can't be null");
        }
        else if (!admin.checkSession(klant)) {
            throw new SessionExpiredException("Session has expired for this client");
        }
        else {
            bankAccounts.add(new Bankrekening(generateNewIBAN(), 0, 100, klant));
        }
    }

    @Override
    public boolean removeAccount(String IBAN, Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        boolean bool = false;
        if (IBAN.isEmpty() || klant == null) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!admin.checkSession(klant)) {
            throw new SessionExpiredException("Session has expired for this client");
        }
        else if (!admin.checkIBANProperty(IBAN, klant)) {
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
            throw new SessionExpiredException("Session has expired for this client");
        }
        else if (!admin.checkIBANProperty(IBAN1, klant)) {
            throw new IllegalArgumentException("IBAN is no property of this client");
        }
        else if (!admin.checkIBANExists(IBAN2)) {
            throw new IllegalArgumentException("IBAN doesn't exists");
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
        else if (!admin.checkIBANExists(IBAN)) {
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
        else if (!admin.checkIBANExists(IBAN)) {
            throw new IllegalArgumentException("IBAN doesn't exists");
        }
        else {
            Bankrekening bankAccount = IBANToBankAccount(IBAN);
            bankAccount.removeFromBalance(value);
        }
    }
    
    private String generateNewIBAN() {
        String part1 = "NL" + generateRandom(0, 99, 2);
        String part2 = shortName;
        String part3 = "0" + generateRandom(0, 999999999, 9);
        return part1 + part2 + part3;
    }
    
    private Bankrekening IBANToBankAccount(String IBAN) {
        Bankrekening bankAccount = null;
        for (Bankrekening b : bankAccounts) {
            if (b.toString().split(";")[0].equals(IBAN)) {
                bankAccount = b;
            }
        }
        return bankAccount;
    }
    
    private String transactionToString(ITransactie transaction) {
        try {
            String description = transaction.getDescription();
            if (description.isEmpty()) {
                return transaction.getDate() + ";" + String.valueOf(transaction.getAmount()) + ";" +
                        transaction.getIBANFrom() + ";" + transaction.getIBANTo();
            } else {
                return transaction.getDate() + ";" + String.valueOf(transaction.getAmount()) + ";" +
                        transaction.getIBANFrom() + ";" + transaction.getIBANTo() + ";" + transaction.getDescription();
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private String generateRandom(int startValue, int endValue, int numberLength) {
        Random r = new Random();
        String value = String.valueOf(r.nextInt((endValue - startValue) + 1) + startValue);
        while (value.length() != numberLength) value = String.valueOf(new Random().nextInt(9) + startValue) + value;
        return value;
    }
}
