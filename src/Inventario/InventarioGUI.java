package Inventario;

import Conexion.ConexionBD;
import MenuPrincipal.MenuPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InventarioGUI {

    private JPanel main;

    private JTable table1;

    private JTextField id;

    private JTextField nombre;

    private JTextField categoria;

    private JTextField precio;

    private JTextField cantidad_stock;

    private JTextField id_proveedor;

    private JButton agregarButton;

    private JButton actualizarButton;

    private JButton eliminarButton;
    private JButton volverButton;

    InventarioDAO inventarioDAO = new InventarioDAO();

    public InventarioGUI() {
        obtener_datos();
        id.setEnabled(false);

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreProducto = nombre.getText();
                String categoriaProducto = categoria.getText();
                int precioProducto = Integer.parseInt(precio.getText());
                int cantidadStock = Integer.parseInt(cantidad_stock.getText());
                Integer idProveedor = id_proveedor.getText().isEmpty() ? null : Integer.parseInt(id_proveedor.getText());

                Inventario inventario = new Inventario(0, nombreProducto, categoriaProducto, cantidadStock, precioProducto, idProveedor);
                inventarioDAO.agregar(inventario);
                obtener_datos();
                clear();
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreProducto = nombre.getText();
                String categoriaProducto = categoria.getText();
                int precioProducto = Integer.parseInt(precio.getText());
                int cantidadStock = Integer.parseInt(cantidad_stock.getText());
                int idProducto = Integer.parseInt(id.getText());
                Integer idProveedor = id_proveedor.getText().isEmpty() ? null : Integer.parseInt(id_proveedor.getText());

                Inventario inventario = new Inventario(idProducto, nombreProducto, categoriaProducto, cantidadStock, precioProducto, idProveedor);
                inventarioDAO.actualizar(inventario);
                obtener_datos();
                clear();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idProducto = Integer.parseInt(id.getText());
                inventarioDAO.eliminar(idProducto);
                obtener_datos();
                clear();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selectFila = table1.getSelectedRow();

                if (selectFila >= 0) {
                    id.setText(table1.getValueAt(selectFila, 0).toString());
                    nombre.setText(table1.getValueAt(selectFila, 1).toString());
                    categoria.setText(table1.getValueAt(selectFila, 2).toString());
                    cantidad_stock.setText(table1.getValueAt(selectFila, 3).toString());
                    precio.setText(table1.getValueAt(selectFila, 4).toString());

                    // Handle potential null value for provider ID
                    Object proveedorValue = table1.getValueAt(selectFila, 5);
                    id_proveedor.setText(proveedorValue == null ? "" : proveedorValue.toString());
                }
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                jFrame.dispose();
                MenuPrincipal.main(null);
            }
        });
    }

    public void clear() {
        id.setText("");
        nombre.setText("");
        categoria.setText("");
        precio.setText("");
        cantidad_stock.setText("");
        id_proveedor.setText("");
    }

    ConexionBD conexionBD = new ConexionBD();

    public void obtener_datos() {
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("id_producto");
        model.addColumn("nombre_producto");
        model.addColumn("categoria");
        model.addColumn("cantidad_stock");
        model.addColumn("precio_producto");
        model.addColumn("id_proveedor_asociado");

        table1.setModel(model);
        Object[] dato = new Object[6];
        Connection con = conexionBD.getconnection();

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT * FROM inventario_productos";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                dato[0] = rs.getInt("id_producto");
                dato[1] = rs.getString("nombre_producto");
                dato[2] = rs.getString("categoria");
                dato[3] = rs.getInt("cantidad_stock");
                dato[4] = rs.getInt("precio_producto");
                dato[5] = rs.getObject("id_proveedor_asociado");
                model.addRow(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Inventario");
        frame.setContentPane(new InventarioGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
    }
}