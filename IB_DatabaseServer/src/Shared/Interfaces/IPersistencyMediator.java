/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shared.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Michiel van Eijkeren
 */
public interface IPersistencyMediator extends Remote
{
    public boolean Login(String username,String password) throws RemoteException;
    
    public boolean registerAccount(String Email, String Password) throws RemoteException;
    
    public boolean emailAvailable(String Email) throws RemoteException;
    
    
}
