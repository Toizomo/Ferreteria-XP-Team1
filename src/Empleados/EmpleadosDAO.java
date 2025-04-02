package Empleados;

import Conexion.ConexionBD;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadosDAO {
    private final ConexionBD conexionBD;

    public EmpleadosDAO() {
        conexionBD = new ConexionBD();
    }

    // Método mejorado para insertar empleado
    public boolean insertarEmpleado(Empleados empleado) {
        String sql = "INSERT INTO empleados (nombre, cargo, salario) VALUES (?, ?, ?)";
        try (Connection con = conexionBD.getconnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, empleado.getNombre());
            pst.setString(2, empleado.getCargo());
            pst.setDouble(3, empleado.getSalario());

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        empleado.setId_empleado(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar empleado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Método mejorado para actualizar empleado
    public boolean actualizarEmpleado(Empleados empleado) {
        String sql = "UPDATE empleados SET nombre = ?, cargo = ?, salario = ? WHERE id_empleado = ?";
        try (Connection con = conexionBD.getconnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, empleado.getNombre());
            pst.setString(2, empleado.getCargo());
            pst.setDouble(3, empleado.getSalario());
            pst.setInt(4, empleado.getId_empleado());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar empleado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Método mejorado para eliminar empleado
    public boolean eliminarEmpleado(int idEmpleado) {
        String sql = "DELETE FROM empleados WHERE id_empleado = ?";
        try (Connection con = conexionBD.getconnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idEmpleado);
            return pst.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null,
                    "No se puede eliminar el empleado porque tiene órdenes asociadas",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar empleado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Método para obtener todos los empleados
    public List<Empleados> obtenerTodosEmpleados() {
        List<Empleados> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleados";

        try (Connection con = conexionBD.getconnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Empleados emp = new Empleados(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("cargo"),
                        rs.getDouble("salario")
                );
                empleados.add(emp);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener empleados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return empleados;
    }

    // Método para buscar empleado por ID
    public Empleados obtenerEmpleadoPorId(int idEmpleado) {
        String sql = "SELECT * FROM empleados WHERE id_empleado = ?";

        try (Connection con = conexionBD.getconnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idEmpleado);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Empleados(
                            rs.getInt("id_empleado"),
                            rs.getString("nombre"),
                            rs.getString("cargo"),
                            rs.getDouble("salario")
                    );
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar empleado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}