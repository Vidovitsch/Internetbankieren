package Models;

import Exceptions.SessionExpiredException;
import Shared_Centrale.IBankTrans;
import Shared_Client.IBank;
import Shared_Client.Klant;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


/**
 *
 * @author David
 */
public class Bank extends UnicastRemoteObject implements IBank, IBankTrans{

    private ArrayList<Bankrekening> bankAccounts;
    private String name;
    private Administratie admin;
    
    /**
     * A bank registered at the administration.
     * @param name of the bank, if empty IllegalArgumentException
     * @param admin
     * @throws RemoteException, IllegalArgumentExcpetion
     */
    public Bank(String name, Administratie admin) throws RemoteException {
        bankAccounts = new ArrayList();
        this.name = name;
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
            //Code here
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
            //Code here
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
            //Code here
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
            //Code here
        }
        return bool;
    }

    @Override
    public boolean startTransaction(Klant klant, String IBAN1, String IBAN2, double value) throws SessionExpiredException, IllegalArgumentException, RemoteException {
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
            //Code here
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
            //code here
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
            //code here
        }
    }
    
}
