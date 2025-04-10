package Reportes;

import Conexion.ConexionBD;
import Inventario.Inventario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class ReportesDAO {

        // Agrega un producto a la tablaFactura o aumenta su cantidad si ya existe
        public void agregarProducto(JTable tablaFactura, Inventario producto) {
            DefaultTableModel model = (DefaultTableModel) tablaFactura.getModel();

            boolean encontrado = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                int idExistente = (int) model.getValueAt(i, 0);

                if (idExistente == producto.getId_producto()) {
                    int cantidadActual = (int) model.getValueAt(i, 5);
                    model.setValueAt(cantidadActual + 1, i, 5);

                    int precioVenta = (int) model.getValueAt(i, 4);
                    model.setValueAt((cantidadActual + 1) * precioVenta, i, 6);

                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                Vector<Object> fila = new Vector<>();
                fila.add(producto.getId_producto());
                fila.add(producto.getCategoria());
                fila.add(producto.getNombre_producto());
                fila.add(producto.getId_proveedor_asociado());
                //fila.add(producto.getPrecio_venta());
                fila.add(1); // Cantidad
                //fila.add(producto.getPrecio_venta()); // Subtotal
                model.addRow(fila);
            }
        }

        // Guarda la venta principal en la tabla registro_ventas, incluyendo el estado
        public int guardarVenta(int idCliente, int idEmpleado, int total, String estado) {
            int idVenta = -1;
            String sql = "INSERT INTO registro_ventas (id_cliente, id_empleado, total, estado, fecha) VALUES (?, ?, ?, ?, NOW())";

            ConexionBD conBD = new ConexionBD();
            try (Connection conn = conBD.getconnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, idCliente);
                ps.setInt(2, idEmpleado);
                ps.setInt(3, total);
                ps.setString(4, estado);  // Aquí se agrega el estado

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idVenta = rs.getInt(1);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return idVenta;
        }


        // Guarda el detalle de cada producto vendido
        public void guardarDetalleVenta(int idVenta, int idProducto, int cantidad, int precioUnitario, int subtotal) {
            String sql = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

            ConexionBD conBD = new ConexionBD();
            try (Connection conn = conBD.getconnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idVenta);
                ps.setInt(2, idProducto);
                ps.setInt(3, cantidad);
                ps.setInt(4, precioUnitario);
                ps.setInt(5, subtotal);
                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }




        // Resta la cantidad vendida al inventario
        public void actualizarStock(int idProducto, int cantidadVendida) {
            String sql = "UPDATE inventario_productos SET cantidad_stock = cantidad_stock - ? WHERE id_producto = ?";

            ConexionBD conBD = new ConexionBD();
            try (Connection conn = conBD.getconnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, cantidadVendida);
                ps.setInt(2, idProducto);
                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public ResultSet obtenerVentasFiltradas(String filtro) {
            String condicionFecha = "";

            switch (filtro) {
                case "Diario":
                    condicionFecha = "DATE(fecha) = CURDATE()";
                    break;
                case "Semanal":
                    condicionFecha = "YEARWEEK(fecha, 1) = YEARWEEK(CURDATE(), 1)";
                    break;
                case "Mensual":
                    condicionFecha = "MONTH(fecha) = MONTH(CURDATE()) AND YEAR(fecha) = YEAR(CURDATE())";
                    break;
            }

            String sql = "SELECT id_venta, total, fecha, estado FROM registro_ventas WHERE " + condicionFecha;
            ConexionBD conBD = new ConexionBD();

            try {
                Connection conn = conBD.getconnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                return ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }



        public ResultSet obtenerProductosMasVendidos(String filtro) {
            String condicionFecha = "";

            switch (filtro) {
                case "Diario":
                    condicionFecha = "DATE(v.fecha) = CURDATE()";
                    break;
                case "Semanal":
                    condicionFecha = "YEARWEEK(v.fecha, 1) = YEARWEEK(CURDATE(), 1)";
                    break;
                case "Mensual":
                    condicionFecha = "MONTH(v.fecha) = MONTH(CURDATE()) AND YEAR(v.fecha) = YEAR(CURDATE())";
                    break;
            }

            String sql = "SELECT p.nombre_producto, SUM(dv.cantidad) AS total_vendidos " +
                    "FROM detalle_venta dv " +
                    "INNER JOIN inventario_productos p ON dv.id_producto = p.id_producto " +
                    "INNER JOIN registro_ventas v ON dv.id_venta = v.id_venta " +
                    "WHERE " + condicionFecha + " " +
                    "GROUP BY p.nombre_producto " +
                    "ORDER BY total_vendidos DESC " +
                    "LIMIT 5";

            ConexionBD conBD = new ConexionBD();

            try {
                Connection conn = conBD.getconnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                return ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }



        public ResultSet obtenerClientesTop(String filtro) {
            String condicionFecha = "";

            switch (filtro) {
                case "Diario":
                    condicionFecha = "DATE(v.fecha) = CURDATE()";
                    break;
                case "Semanal":
                    condicionFecha = "YEARWEEK(v.fecha, 1) = YEARWEEK(CURDATE(), 1)";
                    break;
                case "Mensual":
                    condicionFecha = "MONTH(v.fecha) = MONTH(CURDATE()) AND YEAR(v.fecha) = YEAR(CURDATE())";
                    break;
            }

            String sql = "SELECT c.nombre AS nombre_cliente, SUM(v.total) AS total_compras " +
                    "FROM registro_ventas v " +
                    "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                    "WHERE " + condicionFecha + " " +
                    "GROUP BY c.nombre " +
                    "ORDER BY total_compras DESC " +
                    "LIMIT 5";

            ConexionBD conBD = new ConexionBD();

            try {
                Connection conn = conBD.getconnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                return ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }



        public java.sql.Date obtenerFechaFiltro(String filtro) {
            java.util.Calendar cal = java.util.Calendar.getInstance();

            switch (filtro) {
                case "Diario" -> cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
                case "Semanal" -> cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
                case "Mensual" -> cal.add(java.util.Calendar.MONTH, -1);
            }

            return new java.sql.Date(cal.getTimeInMillis());
        }

        public boolean actualizarEstadoVenta(int idVenta, String nuevoEstado) {
            String sql = "UPDATE registro_ventas SET estado = ? WHERE id_venta = ?";
            ConexionBD conBD = new ConexionBD();

            try (Connection conn = conBD.getconnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, nuevoEstado);
                ps.setInt(2, idVenta);

                return ps.executeUpdate() > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }




}
