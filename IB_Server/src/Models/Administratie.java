package Models;

import Shared_Centrale.ICentrale;
import Shared_Client.IAdmin;
import Shared_Client.IBank;
import Shared_Client.Klant;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 *
 * @author David
 */
public class Administratie extends UnicastRemoteObject implements IAdmin {

    private ICentrale centrale;
    private Bank bank;
    
    public Administratie(ICentrale centrale, Bank bank) throws RemoteException {
        this.centrale = centrale;
        this.bank = bank;
    }

    @Override
    public Klant register(String name, String residence, String password) throws IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Klant login(String name, String residence, String password) throws IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IBank getBank(Klant klant) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeKlant(Klant klant) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean checkIBAN(int IBAN) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
