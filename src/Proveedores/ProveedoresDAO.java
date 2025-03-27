package Proveedores;

import Conexion.ConexionBD;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProveedoresDAO {

    ConexionBD ConexionBD = new ConexionBD();

    public void agregar(Proveedores proveedor)
    {

        Connection con = ConexionBD.getconnection();
        System.out.println("Nombre: " + proveedor.getNombre() + " | Contacto: " + proveedor.getContacto());

        String query = "INSERT INTO proveedores (nombre, contacto) VALUES (?, ?)";

        try{

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, proveedor.getNombre());
            pst.setString(2, proveedor.getContacto());


            int resultado = pst.executeUpdate();

            if (resultado > 0)
            {
                JOptionPane.showMessageDialog(null, "Proveedor agregado exitosamente");
                JOptionPane.showMessageDialog(null, "Depuración: Nombre=" + proveedor.getNombre() + ", Contacto=" + proveedor.getContacto());

            }
            else
            {
                JOptionPane.showMessageDialog(null, "Error al agregar Proveedor");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void eliminar (int id_proveedor)
    {
        Connection con = ConexionBD.getconnection();

        String query = "DELETE FROM proveedores WHERE id_proveedor = ?";

        try
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id_proveedor);

            int resultado = pst.executeUpdate();

            if (resultado > 0)
            {
                JOptionPane.showMessageDialog(null, "Proveedor eliminado exitosamente");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Error al eliminar proveedor");
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void actualizar(Proveedores proveedor)
    {

        Connection con = ConexionBD.getconnection();

        String query = "UPDATE proveedores SET nombre = ?, contacto = ? WHERE id_proveedor = ?";

        try{

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, proveedor.getNombre());
            pst.setString(2, proveedor.getContacto());
            pst.setInt(3, proveedor.getId_proveedor());

            int resultado = pst.executeUpdate();

            if (resultado > 0)
            {
                JOptionPane.showMessageDialog(null, "Proveedor actualizado exitosamente");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Error al actualizar proveedor");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
