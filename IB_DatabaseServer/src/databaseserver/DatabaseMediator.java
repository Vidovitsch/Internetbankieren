/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseserver;

import Shared.Interfaces.IPersistencyMediator;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michiel van Eijkeren
 */
public class DatabaseMediator extends UnicastRemoteObject implements IPersistencyMediator
{
    private static Connection con;
    //private static Statement statement;
    private static ResultSet myRs;
    private static String connectionstring = "jdbc:mysql://localhost/internetbankieren";
    private static String user = "Internetbankieren";
    private static String pass = "GSO2016";
    
    public DatabaseMediator() throws RemoteException
    {
        try
        {
            con = DriverManager.getConnection(connectionstring, user, pass);
            System.out.println(con.toString());
        } catch (SQLException ex)
        {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        };
    }

    @Override
    public boolean Login(String username, String password) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean registerAccount(String Email, String Password) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean emailAvailable(String Email) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
