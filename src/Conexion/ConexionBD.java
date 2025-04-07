package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {
    public Connection getconnection(){
        Connection con = null;
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ferreteria","root","");
        } catch (Exception e){
            e.printStackTrace();
        }
        return  con;
    }
}