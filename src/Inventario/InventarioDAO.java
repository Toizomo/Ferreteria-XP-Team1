package Inventario;

import util.ConexionBD;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {
    private final Connection conexion;

    public InventarioDAO() {
        this.conexion = new ConexionBD().getConnection();
    }

    // CREATE - Agregar nuevo producto al inventario
    public boolean agregarProducto(Inventario producto) {
        String sql = "INSERT INTO productos (nombre_producto, categoria, cantidad_stock, " +
                "precio_venta, id_proveedor, stock_minimo, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, producto.getNombre_producto());
            pst.setString(2, producto.getCategoria());
            pst.setInt(3, producto.getCantidad_stock());
            pst.setDouble(4, producto.getPrecio_venta());
            pst.setInt(5, producto.getId_proveedor());
            pst.setInt(6, producto.getStock_minimo());
            pst.setString(7, producto.getEstado());

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        producto.setId_producto(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            mostrarError("Error al agregar producto", e);
        }
        return false;
    }

    // READ - Obtener todos los productos con información de proveedor
    public List<Inventario> obtenerTodosProductos() {
        List<Inventario> productos = new ArrayList<>();
        String sql = "SELECT p.*, pr.nombre as nombre_proveedor " +
                "FROM productos p " +
                "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Inventario prod = new Inventario(
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad_stock"),
                        rs.getDouble("precio_venta"),
                        rs.getInt("id_proveedor"),
                        rs.getInt("stock_minimo"),
                        rs.getString("estado")
                );
                prod.setNombre_proveedor(rs.getString("nombre_proveedor"));
                productos.add(prod);
            }
        } catch (SQLException e) {
            mostrarError("Error al obtener productos", e);
        }
        return productos;
    }

    // UPDATE - Actualizar producto
    public boolean actualizarProducto(Inventario producto) {
        String sql = "UPDATE productos SET " +
                "nombre_producto = ?, categoria = ?, cantidad_stock = ?, " +
                "precio_venta = ?, id_proveedor = ?, stock_minimo = ?, estado = ? " +
                "WHERE id_producto = ?";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, producto.getNombre_producto());
            pst.setString(2, producto.getCategoria());
            pst.setInt(3, producto.getCantidad_stock());
            pst.setDouble(4, producto.getPrecio_venta());
            pst.setInt(5, producto.getId_proveedor());
            pst.setInt(6, producto.getStock_minimo());
            pst.setString(7, producto.getEstado());
            pst.setInt(8, producto.getId_producto());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            mostrarError("Error al actualizar producto", e);
        }
        return false;
    }

    // DELETE - Desactivar producto (no lo eliminamos físicamente)
    public boolean desactivarProducto(int id) {
        String sql = "UPDATE productos SET estado = 'INACTIVO' WHERE id_producto = ?";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, id);

            int confirmacion = JOptionPane.showConfirmDialog(null,
                    "¿Está seguro de desactivar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            mostrarError("Error al desactivar producto", e);
        }
        return false;
    }

    // Método para obtener productos con bajo stock
    public List<Inventario> obtenerProductosBajoStock() {
        List<Inventario> productos = new ArrayList<>();
        String sql = "SELECT p.*, pr.nombre as nombre_proveedor " +
                "FROM productos p " +
                "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                "WHERE p.cantidad_stock < p.stock_minimo AND p.estado = 'ACTIVO'";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Inventario prod = new Inventario(
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad_stock"),
                        rs.getDouble("precio_venta"),
                        rs.getInt("id_proveedor"),
                        rs.getInt("stock_minimo"),
                        rs.getString("estado")
                );
                prod.setNombre_proveedor(rs.getString("nombre_proveedor"));
                productos.add(prod);
            }
        } catch (SQLException e) {
            mostrarError("Error al obtener productos con bajo stock", e);
        }
        return productos;
    }

    // Método para buscar productos por nombre o categoría
    public List<Inventario> buscarProductos(String criterio) {
        List<Inventario> productos = new ArrayList<>();
        String sql = "SELECT p.*, pr.nombre as nombre_proveedor " +
                "FROM productos p " +
                "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor " +
                "WHERE (p.nombre_producto LIKE ? OR p.categoria LIKE ?) " +
                "AND p.estado = 'ACTIVO'";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, "%" + criterio + "%");
            pst.setString(2, "%" + criterio + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Inventario prod = new Inventario(
                            rs.getInt("id_producto"),
                            rs.getString("nombre_producto"),
                            rs.getString("categoria"),
                            rs.getInt("cantidad_stock"),
                            rs.getDouble("precio_venta"),
                            rs.getInt("id_proveedor"),
                            rs.getInt("stock_minimo"),
                            rs.getString("estado")
                    );
                    prod.setNombre_proveedor(rs.getString("nombre_proveedor"));
                    productos.add(prod);
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al buscar productos", e);
        }
        return productos;
    }

    private void mostrarError(String mensaje, SQLException e) {
        JOptionPane.showMessageDialog(null,
                mensaje + ": " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}