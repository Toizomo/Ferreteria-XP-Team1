package Reportes;

import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class ReportesDAO {
    private final Connection conexion;

    public ReportesDAO() {
        this.conexion = new ConexionBD().getConnection();
    }

    // Reporte 1: Ventas por período
    public List<Map<String, Object>> generarReporteVentas(Date fechaInicio, Date fechaFin) {
        List<Map<String, Object>> ventas = new ArrayList<>();
        String sql = "SELECT DATE(v.fecha) as fecha, COUNT(*) as cantidad, SUM(v.total) as total " +
                "FROM ventas v " +
                "WHERE v.fecha BETWEEN ? AND ? " +
                "GROUP BY DATE(v.fecha) " +
                "ORDER BY DATE(v.fecha)";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            pst.setDate(2, new java.sql.Date(fechaFin.getTime()));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("fecha", rs.getDate("fecha"));
                    fila.put("cantidad", rs.getInt("cantidad"));
                    fila.put("total", rs.getDouble("total"));
                    ventas.add(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte de ventas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return ventas;
    }

    // Reporte 2: Productos más vendidos
    public List<Map<String, Object>> generarReporteProductosMasVendidos(Date fechaInicio, Date fechaFin) {
        List<Map<String, Object>> productos = new ArrayList<>();
        String sql = "SELECT p.nombre_producto, SUM(dv.cantidad) as cantidad_vendida, " +
                "SUM(dv.sub_total) as total_ventas " +
                "FROM detalle_ventas dv " +
                "JOIN productos p ON dv.id_producto = p.id_producto " +
                "JOIN ventas v ON dv.id_venta = v.id_venta " +
                "WHERE v.fecha BETWEEN ? AND ? " +
                "GROUP BY p.nombre_producto " +
                "ORDER BY cantidad_vendida DESC " +
                "LIMIT 10";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            pst.setDate(2, new java.sql.Date(fechaFin.getTime()));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("producto", rs.getString("nombre_producto"));
                    fila.put("cantidad", rs.getInt("cantidad_vendida"));
                    fila.put("total", rs.getDouble("total_ventas"));
                    productos.add(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte de productos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return productos;
    }

    // Reporte 3: Stock bajo
    public List<Map<String, Object>> generarReporteStockBajo() {
        List<Map<String, Object>> productos = new ArrayList<>();
        String sql = "SELECT p.nombre_producto, p.cantidad_stock, p.stock_minimo, " +
                "pr.nombre as proveedor " +
                "FROM productos p " +
                "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                "WHERE p.cantidad_stock < p.stock_minimo AND p.estado = 'ACTIVO' " +
                "ORDER BY (p.stock_minimo - p.cantidad_stock) DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("producto", rs.getString("nombre_producto"));
                fila.put("stock_actual", rs.getInt("cantidad_stock"));
                fila.put("stock_minimo", rs.getInt("stock_minimo"));
                fila.put("proveedor", rs.getString("proveedor"));
                fila.put("diferencia", rs.getInt("stock_minimo") - rs.getInt("cantidad_stock"));
                productos.add(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte de stock: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return productos;
    }

    // Reporte 4: Clientes con más compras
    public List<Map<String, Object>> generarReporteClientesTop(Date fechaInicio, Date fechaFin) {
        List<Map<String, Object>> clientes = new ArrayList<>();
        String sql = "SELECT c.nombre, COUNT(v.id_venta) as compras, SUM(v.total) as total_gastado " +
                "FROM ventas v " +
                "JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "WHERE v.fecha BETWEEN ? AND ? " +
                "GROUP BY c.nombre " +
                "ORDER BY total_gastado DESC " +
                "LIMIT 10";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            pst.setDate(2, new java.sql.Date(fechaFin.getTime()));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("cliente", rs.getString("nombre"));
                    fila.put("compras", rs.getInt("compras"));
                    fila.put("total", rs.getDouble("total_gastado"));
                    clientes.add(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte de clientes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return clientes;
    }

    // Reporte 5: Ventas por empleado
    public List<Map<String, Object>> generarReporteVentasPorEmpleado(Date fechaInicio, Date fechaFin) {
        List<Map<String, Object>> empleados = new ArrayList<>();
        String sql = "SELECT e.nombre, COUNT(v.id_venta) as ventas, SUM(v.total) as total " +
                "FROM ventas v " +
                "JOIN empleados e ON v.id_empleado = e.id_empleado " +
                "WHERE v.fecha BETWEEN ? AND ? " +
                "GROUP BY e.nombre " +
                "ORDER BY total DESC";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            pst.setDate(2, new java.sql.Date(fechaFin.getTime()));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("empleado", rs.getString("nombre"));
                    fila.put("ventas", rs.getInt("ventas"));
                    fila.put("total", rs.getDouble("total"));
                    empleados.add(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte por empleado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return empleados;
    }

    // Reporte 6: Productos por agotarse (próximos a stock mínimo)
    public List<Map<String, Object>> generarReporteProductosPorAgotarse(int margen) {
        List<Map<String, Object>> productos = new ArrayList<>();
        String sql = "SELECT p.nombre_producto, p.cantidad_stock, p.stock_minimo, " +
                "ROUND((p.cantidad_stock/p.stock_minimo)*100) as porcentaje " +
                "FROM productos p " +
                "WHERE p.cantidad_stock BETWEEN p.stock_minimo AND (p.stock_minimo + ?) " +
                "AND p.estado = 'ACTIVO' " +
                "ORDER BY porcentaje ASC";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, margen);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("producto", rs.getString("nombre_producto"));
                    fila.put("stock_actual", rs.getInt("cantidad_stock"));
                    fila.put("stock_minimo", rs.getInt("stock_minimo"));
                    fila.put("porcentaje", rs.getInt("porcentaje") + "%");
                    productos.add(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte de productos por agotarse: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return productos;
    }
}