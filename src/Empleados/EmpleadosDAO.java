package Empleados;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadosDAO {
    private Connection conexion;

    // Método para establecer conexión (debes configurar los detalles de conexión)
    private void conectar() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/ferreteria";
        String usuario = "tu_usuario";
        String contrasena = "tu_contraseña";
        conexion = DriverManager.getConnection(url, usuario, contrasena);
    }

    // Método para cerrar conexión
    private void desconectar() throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    // Crear nuevo empleado
    public boolean insertarEmpleado(Empleados empleado) {
        try {
            conectar();
            String sql = "INSERT INTO empleados (nombre, cargo, salario) VALUES (?, ?, ?)";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, empleado.getNombre());
            statement.setString(2, empleado.getCargo());
            statement.setDouble(3, empleado.getSalario());

            int filasInsertadas = statement.executeUpdate();
            desconectar();
            return filasInsertadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Listar todos los empleados
    public List<Empleados> listarEmpleados() {
        List<Empleados> listaEmpleados = new ArrayList<>();
        try {
            conectar();
            String sql = "SELECT * FROM empleados";
            Statement statement = conexion.createStatement();
            ResultSet resultado = statement.executeQuery(sql);

            while (resultado.next()) {
                Empleados empleado = new Empleados(
                        resultado.getInt("id_empleado"),
                        resultado.getString("nombre"),
                        resultado.getString("cargo"),
                        resultado.getDouble("salario")
                );
                listaEmpleados.add(empleado);
            }
            desconectar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaEmpleados;
    }

    // Actualizar empleado
    public boolean actualizarEmpleado(Empleados empleado) {
        try {
            conectar();
            String sql = "UPDATE empleados SET nombre = ?, cargo = ?, salario = ? WHERE id_empleado = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, empleado.getNombre());
            statement.setString(2, empleado.getCargo());
            statement.setDouble(3, empleado.getSalario());
            statement.setInt(4, empleado.getId_empleado());

            int filasActualizadas = statement.executeUpdate();
            desconectar();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar empleado
    public boolean eliminarEmpleado(int id_empleado) {
        try {
            conectar();
            String sql = "DELETE FROM empleados WHERE id_empleado = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, id_empleado);

            int filasEliminadas = statement.executeUpdate();
            desconectar();
            return filasEliminadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Buscar empleado por ID
    public Empleados obtenerEmpleadoPorId(int id_empleado) {
        Empleados empleado = null;
        try {
            conectar();
            String sql = "SELECT * FROM empleados WHERE id_empleado = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, id_empleado);
            ResultSet resultado = statement.executeQuery();

            if (resultado.next()) {
                empleado = new Empleados(
                        resultado.getInt("id_empleado"),
                        resultado.getString("nombre"),
                        resultado.getString("cargo"),
                        resultado.getDouble("salario")
                );
            }
            desconectar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empleado;
    }
}
