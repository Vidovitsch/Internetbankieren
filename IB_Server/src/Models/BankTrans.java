package Models;

import Shared_Centrale.IBankTrans;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author David
 */
public class BankTrans extends UnicastRemoteObject implements IBankTrans {

    public Bank bank;
    
    public BankTrans(Bank bank) throws RemoteException {
        this.bank = bank;
    }
    
    @Override
    public void addSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException {
        if (IBAN.isEmpty() || value <= 0) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!bank.checkIBANExists(IBAN)) {
            throw new IllegalArgumentException("IBAN doesn't exists");
        }
        else {
            Bankrekening bankAccount = bank.IBANToBankAccount(IBAN);
            bankAccount.addToBalance(value);
        }
    }

    @Override
    public boolean removeSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException {
        if (IBAN.isEmpty() || value <= 0) {
            throw new IllegalArgumentException("Input can't be null");
        }
        else if (!bank.checkIBANExists(IBAN)) {
            throw new IllegalArgumentException("IBAN doesn't exists");
        }
        else {
            Bankrekening bankAccount = bank.IBANToBankAccount(IBAN);
            if (bankAccount.removeFromBalance(value)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
