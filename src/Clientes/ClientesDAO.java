package Clientes;

import Conexion.ConexionBD;

import javax.swing.*;
import java.sql.*;

public class ClientesDAO {

    ConexionBD ConexionBD = new ConexionBD();

    public void agregar(Clientes cliente) {
        Connection con = ConexionBD.getconnection();
        String query = "INSERT INTO clientes (nombre, telefono, direccion, correo) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, cliente.getNombre());
            pst.setString(2, cliente.getTelefono());
            pst.setString(3, cliente.getDireccion());
            pst.setString(4, cliente.getCorreo());

            int resultado = pst.executeUpdate();

            if (resultado > 0) {
                JOptionPane.showMessageDialog(null, "Cliente agregado exitosamente");
            } else {
                JOptionPane.showMessageDialog(null, "Error al agregar cliente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminar(int id_cliente) {
        Connection con = ConexionBD.getconnection();
        String query = "DELETE FROM clientes WHERE id_clientes = ?";

        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id_cliente);

            int resultado = pst.executeUpdate();

            if (resultado > 0) {
                JOptionPane.showMessageDialog(null, "Cliente eliminado exitosamente");
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar cliente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizar(Clientes cliente) {
        Connection con = ConexionBD.getconnection();
        String query = "UPDATE clientes SET nombre = ?, telefono = ?, direccion = ?, correo = ? WHERE id_clientes = ?";

        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, cliente.getNombre());
            pst.setString(2, cliente.getTelefono());
            pst.setString(3, cliente.getDireccion());
            pst.setString(4, cliente.getCorreo());
            pst.setInt(5, cliente.getId_clientes());

            int resultado = pst.executeUpdate();

            if (resultado > 0) {
                JOptionPane.showMessageDialog(null, "Cliente actualizado exitosamente");
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar cliente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet obtenerClientes() {
        Connection con = ConexionBD.getconnection();
        String query = "SELECT * FROM clientes";

        try {
            Statement stat = con.createStatement();
            return stat.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
