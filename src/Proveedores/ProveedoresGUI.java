package Proveedores;

import Conexion.ConexionBD;
import MenuPrincipal.MenuPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class ProveedoresGUI {
    private JPanel main;
    private JTable table1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JComboBox<String> comboBox1;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JButton volverButton;

    ProveedoresDAO proveedoresDAO = new ProveedoresDAO();
    ConexionBD conexionBD = new ConexionBD();

    public ProveedoresGUI() {
        obtenerDatos();

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = textField2.getText();
                String telefono = textField3.getText();
                String categoria = (String) comboBox1.getSelectedItem();
                proveedoresDAO.agregarProveedor(nombre, telefono, categoria);
                obtenerDatos();
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField1.getText().isEmpty()) return;

                int id = Integer.parseInt(textField1.getText());
                String nombre = textField2.getText();
                String telefono = textField3.getText();
                String categoria = (String) comboBox1.getSelectedItem();

                proveedoresDAO.actualizarProveedor(id, nombre, telefono, categoria);
                obtenerDatos();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField1.getText().isEmpty()) return;

                int id = Integer.parseInt(textField1.getText());
                proveedoresDAO.eliminarProveedor(id);
                obtenerDatos();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = table1.getSelectedRow();
                if (fila >= 0) {
                    textField1.setText(table1.getValueAt(fila, 0).toString());
                    textField2.setText(table1.getValueAt(fila, 1).toString());
                    textField3.setText(table1.getValueAt(fila, 2).toString());
                    comboBox1.setSelectedItem(table1.getValueAt(fila, 3).toString());
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

    public void obtenerDatos() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Proveedor");
        model.addColumn("Nombre");
        model.addColumn("Teléfono");
        model.addColumn("Categoría Producto");

        table1.setModel(model);
        Connection con = conexionBD.getconnection();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM proveedores");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("categoria_producto")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Proveedores");
        frame.setContentPane(new ProveedoresGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(880, 700);
        frame.setResizable(false);
    }
}
