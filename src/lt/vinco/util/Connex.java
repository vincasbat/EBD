/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lt.vinco.ebdimport.EBDimport;

/**
 *
 * @author Vincas Batulevičius
 */
public class Connex {
   static   Connection con = null;
    public static Connection getConnection() throws  SQLException, ClassNotFoundException {
    Class.forName("com.informix.jdbc.IfxDriver");
  
    return con;
    }
    
}
