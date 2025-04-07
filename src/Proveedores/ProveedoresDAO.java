package Proveedores;

import Conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProveedoresDAO {

    public void agregarProveedor(String nombre, String telefono, String categoria) {
        String sql = "INSERT INTO proveedores (nombre, telefono, categoria_producto) VALUES (?, ?, ?)";
        try (Connection con = new ConexionBD().getconnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, categoria);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarProveedor(int id, String nombre, String telefono, String categoria) {
        String sql = "UPDATE proveedores SET nombre = ?, telefono = ?, categoria_producto = ? WHERE id_proveedor = ?";
        try (Connection con = new ConexionBD().getconnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, categoria);
            ps.setInt(4, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarProveedor(int id) {
        String sql = "DELETE FROM proveedores WHERE id_proveedor = ?";
        try (Connection con = new ConexionBD().getconnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}