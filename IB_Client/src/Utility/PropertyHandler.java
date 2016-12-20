package Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class PropertyHandler {

    private final String loginKeyName = "lastLoggedName";
    private final String loginKeyResidence = "lastLoggedResidence";
    private final String loginPropsFile = "login.properties";
    
    private Properties loginProps = new Properties();
    
    public PropertyHandler() {
        setUpLoginPropertiesFile();
    }
    
    public void setLoginProperties(String name, String residence) {
        OutputStream output = null;
        try {
            output = new FileOutputStream(loginPropsFile);
            loginProps.replace(loginKeyName, name);
            loginProps.replace(loginKeyResidence, residence);
            loginProps.store(output, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PropertyHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PropertyHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (output != null) {
                    output.close();                    
                }
            } catch (IOException ex) {
                Logger.getLogger(PropertyHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String[] getLoginProperties() {
        String name = loginProps.getProperty(loginKeyName);
        String residence = loginProps.getProperty(loginKeyResidence);
                System.out.println("Got properties: " + name + " and " + residence);
        if (name.equals(".") || residence.equals(".")) {
            return null;
        } else {
            return new String[]{name, residence};
        }
    }
    
    private void setUpLoginPropertiesFile() {
        OutputStream output = null;
        InputStream input = null;
        try {
            File file = new File(loginPropsFile);
            if (!file.exists()) {
                file.createNewFile();
                
                output = new FileOutputStream(file);
                loginProps.setProperty(loginKeyName, ".");
                loginProps.setProperty(loginKeyResidence, ".");
                loginProps.store(output, null);
            }
            input = new FileInputStream(file);
            loginProps.load(input);
        } catch (IOException ex) {
            Logger.getLogger(PropertyHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PropertyHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
