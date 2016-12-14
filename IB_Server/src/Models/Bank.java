package Models;

import Exceptions.SessionExpiredException;
import Shared_Centrale.IBankTrans;
import Shared_Centrale.ITransactie;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<String> getAccounts(Klant klant) throws IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<String> getTransactions(String IBAN, Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String addAccount(Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAccount(String IBAN, Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean startTransaction(Klant klant, String IBAN1, String IBAN2, double value) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTransactie(String IBAN, ITransactie transactie) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

    
}
