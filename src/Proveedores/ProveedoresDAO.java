package Proveedores;

import Clientes.Clientes;
import Conexion.ConexionBD;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProveedoresDAO {

    ConexionBD ConexionBD = new ConexionBD();

    public void agregar(Proveedores proveedores) {
        Connection con = ConexionBD.getconnection();

        String query = "INSERT INTO proveedores (nombre,contacto,productos_suministrados) VALUES (?,?,?)";

        try
        {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, proveedores.getNombre());
            pst.setString(2, proveedores.getContacto());
            pst.setString(3, proveedores.getProductos_suministrados());

            int resultado = pst.executeUpdate();

            if (resultado > 0)
            {
                JOptionPane.showMessageDialog(null, "Proveedor agregado exitosamente");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Error al agregar proveedor");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminar(int id_proveedor)
    {
        Connection con = ConexionBD.getconnection();

        String query = "DELETE FROM proveedores WHERE id_proveedor = ?";

        try
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id_proveedor);

            int resultado = pst.executeUpdate();

            if (resultado>0)
            {
                JOptionPane.showMessageDialog(null,"Proveedor eliminado exitosamnte");
            }
            else {
                JOptionPane.showMessageDialog(null, "Proveedor no eliminado");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void actualizar(Proveedores proveedores)
    {
        Connection con = ConexionBD.getconnection();
        String query = "UPDATE proveedores set nombre = ?, contacto = ?, productos_suministrados = ? WHERE id_proveedor = ?";

        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, proveedores.getNombre());
            pst.setString(2, proveedores.getContacto());
            pst.setString(3, proveedores.getProductos_suministrados());
            pst.setInt(4, proveedores.getId_proveedor());

            int resultado = pst.executeUpdate();

            if (resultado > 0)
            {
                JOptionPane.showMessageDialog(null,"Proveedor actualizado exitosamnte");
            }
            else {
                JOptionPane.showMessageDialog(null, "Proveedor no actualizado");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
