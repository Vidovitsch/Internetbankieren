/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    public int Login(String username,String password) throws RemoteException;
    
    public boolean usernameAvailable(String Username) throws RemoteException;
    
    public boolean registerAccount(String name, String Residence, String Password) throws RemoteException;
    
    public boolean addBank(String bankName, String bankShortName) throws RemoteException;
    
    public boolean addBankrekening(int clientID, String iban, String bankShortName) throws RemoteException;
    
    public ArrayList<String> getBankRekeningen(int klantID) throws RemoteException;
    
    public boolean ibanAvailable(String iban) throws RemoteException;
    
    public boolean addTransaction(String ibanFrom, String ibanTo, double amount, Date date, String description) throws RemoteException;
    
    public ArrayList<String> getTransactions(String iban) throws RemoteException;
    
}