package Inventario;

import Conexion.ConexionBD;
import MenuPrincipal.MenuPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventarioGUI {
    private JPanel main;
    private JTable table1;
    private JTextField id;
    private JTextField nombre;
    private JTextField categoria;
    private JTextField precio;
    private JTextField cantidad_stock;
    private JComboBox<Integer> id_proveedor;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JButton volverButton;

    InventarioDAO inventarioDAO = new InventarioDAO();
    ConexionBD conexionBD = new ConexionBD();

    public InventarioGUI() {
        obtener_datos();
        cargarIdsProveedores();
        id.setEnabled(false);
        categoria.setEnabled(false); // No editable

        // Aplicar los estilos
        aplicarEstilos();

        // Evento al seleccionar proveedor
        id_proveedor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer selectedId = (Integer) id_proveedor.getSelectedItem();
                if (selectedId != null) {
                    try (Connection con = conexionBD.getconnection()) {
                        String sql = "SELECT categoria_producto FROM proveedores WHERE id_proveedor = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setInt(1, selectedId);
                        ResultSet rs = ps.executeQuery();

                        if (rs.next()) {
                            String nombreCategoria = rs.getString("categoria_producto");
                            categoria.setText(nombreCategoria);
                        } else {
                            categoria.setText("");
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al obtener la categoría del proveedor.");
                    }
                }
            }
        });

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreProducto = nombre.getText();
                String categoriaProducto = categoria.getText();
                int precioProducto = Integer.parseInt(precio.getText());
                int cantidadStock = Integer.parseInt(cantidad_stock.getText());
                Integer idProveedor = (Integer) id_proveedor.getSelectedItem();

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
                Integer idProveedor = (Integer) id_proveedor.getSelectedItem();

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
                int selectFila = table1.getSelectedRow();
                if (selectFila >= 0) {
                    id.setText(table1.getValueAt(selectFila, 0).toString());
                    nombre.setText(table1.getValueAt(selectFila, 1).toString());
                    categoria.setText(table1.getValueAt(selectFila, 2).toString());
                    cantidad_stock.setText(table1.getValueAt(selectFila, 3).toString());
                    precio.setText(table1.getValueAt(selectFila, 4).toString());

                    Object proveedorValue = table1.getValueAt(selectFila, 5);
                    id_proveedor.setSelectedItem(proveedorValue);
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

    public void cargarIdsProveedores() {
        try (Connection con = conexionBD.getconnection()) {
            String sql = "SELECT id_proveedor FROM proveedores";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            id_proveedor.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("id_proveedor");
                id_proveedor.addItem(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar proveedores.");
        }
    }

    public void clear() {
        id.setText("");
        nombre.setText("");
        categoria.setText("");
        precio.setText("");
        cantidad_stock.setText("");
        id_proveedor.setSelectedIndex(-1);
    }

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

    private void aplicarEstilos() {
        Font fuenteCampos = new Font("Serif", Font.PLAIN, 15);
        Font fuenteBotones = new Font("Serif", Font.BOLD, 15);
        Color colorFondo = new Color(216, 196, 164); // beige claro
        Color colorTexto = new Color(59, 42, 27);    // marrón oscuro
        Color colorBotonFondo = colorFondo;
        Color colorBotonTexto = Color.WHITE;
        Color colorBordeBoton = Color.WHITE;

        main.setBackground(colorFondo);

        JTextField[] campos = { id, nombre, categoria, precio, cantidad_stock };
        for (JTextField campo : campos) {
            campo.setFont(fuenteCampos);
            campo.setBackground(Color.WHITE);
            campo.setForeground(colorTexto);
            campo.setBorder(BorderFactory.createLineBorder(colorTexto));
        }

        id_proveedor.setFont(fuenteCampos);
        id_proveedor.setBackground(Color.WHITE);
        id_proveedor.setForeground(colorTexto);
        id_proveedor.setBorder(BorderFactory.createLineBorder(colorTexto));

        JButton[] botones = { agregarButton, actualizarButton, eliminarButton, volverButton };
        for (JButton boton : botones) {
            boton.setFont(fuenteBotones);
            boton.setBackground(colorBotonFondo);
            boton.setForeground(colorBotonTexto);
            boton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
            boton.setFocusPainted(false);
        }

        table1.setFont(new Font("Serif", Font.PLAIN, 14));
        table1.setForeground(colorTexto);
        table1.setBackground(Color.WHITE);
        table1.setRowHeight(25);
        table1.getTableHeader().setFont(new Font("Serif", Font.BOLD, 15));
        table1.getTableHeader().setBackground(colorFondo);
        table1.getTableHeader().setForeground(colorTexto);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Inventario");
        frame.setContentPane(new InventarioGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
