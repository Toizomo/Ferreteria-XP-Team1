package Empleados;

import Conexion.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class EmpleadosDAO {
    private final Connection conexion;

    public EmpleadosDAO() {
        this.conexion = new ConexionBD().getconnection();
    }

    // Método para insertar empleado
    public boolean insertarEmpleado(Empleados empleado) {
        String sql = "INSERT INTO empleados (nombre, dni, telefono, email, cargo, salario, fecha_contratacion, estado, usuario, contrasena) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SHA2(?, 256))";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, empleado.getNombre());
            pst.setString(2, empleado.getDni());
            pst.setString(3, empleado.getTelefono());
            pst.setString(4, empleado.getEmail());
            pst.setString(5, empleado.getCargo());
            pst.setDouble(6, empleado.getSalario());
            pst.setDate(7, java.sql.Date.valueOf(empleado.getFecha_contratacion()));
            pst.setString(8, empleado.getEstado());
            pst.setString(9, empleado.getUsuario());
            pst.setString(10, empleado.getContrasena());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar empleado: " + e.getMessage());
            return false;
        }
    }

    // Método para actualizar empleado
    public boolean actualizarEmpleado(Empleados empleado) {
        String sql = "UPDATE empleados SET nombre=?, dni=?, telefono=?, email=?, cargo=?, salario=?, fecha_contratacion=?, estado=?, usuario=?, contrasena=SHA2(?, 256) WHERE id_empleado=?";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, empleado.getNombre());
            pst.setString(2, empleado.getDni());
            pst.setString(3, empleado.getTelefono());
            pst.setString(4, empleado.getEmail());
            pst.setString(5, empleado.getCargo());
            pst.setDouble(6, empleado.getSalario());
            pst.setDate(7, java.sql.Date.valueOf(empleado.getFecha_contratacion()));
            pst.setString(8, empleado.getEstado());
            pst.setString(9, empleado.getUsuario());
            pst.setString(10, empleado.getContrasena());
            pst.setInt(11, empleado.getId_empleado());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar empleado: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar empleado
    public boolean eliminarEmpleado(int id_empleado) {
        String sql = "DELETE FROM empleados WHERE id_empleado = ?";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, id_empleado);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar empleado: " + e.getMessage());
            return false;
        }
    }

    // Método para autenticar empleado
    public Empleados autenticar(String usuario, String contrasena) {
        String sql = "SELECT * FROM empleados WHERE usuario = ? AND contrasena = SHA2(?, 256) AND estado = 'ACTIVO'";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, usuario);
            pst.setString(2, contrasena);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Empleados(
                            rs.getInt("id_empleado"),
                            rs.getString("nombre"),
                            rs.getString("dni"),
                            rs.getString("telefono"),
                            rs.getString("email"),
                            rs.getString("cargo"),
                            rs.getDouble("salario"),
                            rs.getDate("fecha_contratacion").toLocalDate(),
                            rs.getString("estado"),
                            rs.getString("usuario"),
                            rs.getString("contrasena")
                    );
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de autenticación: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    //Método para registrar un nuevo empleado (solo administradores)
    public boolean registrarEmpleado(Empleados empleado) {
        if (!empleadoExiste(empleado.getDni(), empleado.getUsuario())) {
            String sql = "INSERT INTO empleados (nombre, dni, telefono, email, cargo, salario, " +
                    "fecha_contratacion, estado, usuario, contrasena) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SHA2(?, 256))";

            try (PreparedStatement pst = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, empleado.getNombre());
                pst.setString(2, empleado.getDni());
                pst.setString(3, empleado.getTelefono());
                pst.setString(4, empleado.getEmail());
                pst.setString(5, empleado.getCargo());
                pst.setDouble(6, empleado.getSalario());
                pst.setDate(7, Date.valueOf(empleado.getFecha_contratacion()));
                pst.setString(8, empleado.getEstado());
                pst.setString(9, empleado.getUsuario());
                pst.setString(10, empleado.getContrasena());

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
                JOptionPane.showMessageDialog(null, "Error al registrar empleado: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "El DNI o usuario ya están registrados",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Método para verificar si el empleado ya existe
    private boolean empleadoExiste(String dni, String usuario) {
        String sql = "SELECT COUNT(*) FROM empleados WHERE dni = ? OR usuario = ?";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, dni);
            pst.setString(2, usuario);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ... (implementar los demás métodos CRUD: actualizar, eliminar, listar, buscar por ID, etc.)

    // Método para obtener empleados por cargo
    public List<Empleados> obtenerPorCargo(String cargo) {
        List<Empleados> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleados WHERE cargo = ? AND estado = 'ACTIVO'";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, cargo);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Empleados emp = new Empleados(
                            rs.getInt("id_empleado"),
                            rs.getString("nombre"),
                            rs.getString("dni"),
                            rs.getString("telefono"),
                            rs.getString("email"),
                            rs.getString("cargo"),
                            rs.getDouble("salario"),
                            rs.getDate("fecha_contratacion").toLocalDate(),
                            rs.getString("estado"),
                            rs.getString("usuario"),
                            rs.getString("contrasena")
                    );
                    empleados.add(emp);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener empleados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return empleados;
    }

}