package Orden_Compras;

import Conexion.ConexionBD;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrdenesCompraDAO {
    ConexionBD conexionDB = new ConexionBD();

    public static void agregar(ordenesCompra ordenesCompra){
        Connection con = ConexionBD.getconnection();
        String query = "INSERT INTO ordenes_compra (id_cliente, id_empleado, id_producto, total, estado_orden, fecha_compra) VALUES (?, ?, ?, ?, ?, ?)";

        try{
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, ordenesCompra.getId_cliente());
            pst.setInt(2, ordenesCompra.getId_empleado());
            pst.setInt(3, ordenesCompra.getId_producto());
            pst.setInt(4, ordenesCompra.getTotal());
            pst.setString(4, ordenesCompra.getEstado_orden());
            pst.setString(4, ordenesCompra.getFecha_compra());

            int resultado = pst.executeUpdate();
            String mensaje = resultado > 0 ? "Orden de compra registrada con éxito!" : "Error al ingresar la orden de compra...";
            int tipo_mensaje = resultado > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(null, mensaje, "Ordenes compra:", tipo_mensaje);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void actualizar(ordenesCompra ordenesCompra){
        Connection con = ConexionBD.getconnection();
        String query = "UPDATE ordenes_compra SET id_cliente = ?, id_empleado = ?, id_producto = ?, total = ?, estado_compra = ?, fecha_compra = ?";

        try{
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, ordenesCompra.getId_cliente());
            pst.setInt(2, ordenesCompra.getId_empleado());
            pst.setInt(3, ordenesCompra.getId_producto());
            pst.setInt(4, ordenesCompra.getTotal());
            pst.setString(4, ordenesCompra.getEstado_orden());
            pst.setString(4, ordenesCompra.getFecha_compra());

            int resultado = pst.executeUpdate();
            String mensaje = resultado > 0 ? "Orden de compra actualizada con éxito!" : "Error al actualizar la orden de compra...";
            int tipo_mensaje = resultado > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(null, mensaje, "Ordenes compra:", tipo_mensaje);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
