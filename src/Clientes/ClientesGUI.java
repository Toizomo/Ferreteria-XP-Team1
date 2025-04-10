package Clientes;

import Conexion.ConexionBD;
import MenuPrincipal.MenuPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientesGUI {
    private JPanel main;
    private JTable table1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JTextField textField5;
    private JButton volverButton;

    ClientesDAO ClientesDAO = new ClientesDAO();
    ConexionBD ConexionBD = new ConexionBD();
    int filas = 0;

    public ClientesGUI() {
        aplicarEstilos();
        mostrar();

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = textField2.getText();
                String telefono = textField3.getText();
                String direccion = textField4.getText();
                String correo = textField5.getText();
                Clientes Clientes = new Clientes(0, nombre, telefono, direccion, correo);
                ClientesDAO.agregar(Clientes);
                mostrar();
                clear();
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField1.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Selecciona un cliente para actualizar");
                    return;
                }

                try {
                    int id_clientes = Integer.parseInt(textField1.getText());
                    String nombre = textField2.getText();
                    String telefono = textField3.getText();
                    String direccion = textField4.getText();
                    String correo = textField5.getText();
                    Clientes cliente = new Clientes(id_clientes, nombre, telefono, direccion, correo);
                    ClientesDAO.actualizar(cliente);
                    mostrar();
                    clear();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "ID inválido");
                }
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField1.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Selecciona un cliente para eliminar");
                    return;
                }

                try {
                    int id_clientes = Integer.parseInt(textField1.getText());
                    int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro que querés eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);

                    if (confirmacion == JOptionPane.YES_OPTION) {
                        ClientesDAO.eliminar(id_clientes);
                        mostrar();
                        clear();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "ID inválido");
                }
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selectFila = table1.getSelectedRow();
                if (selectFila >= 0) {
                    textField1.setText(table1.getValueAt(selectFila, 0).toString());
                    textField2.setText(table1.getValueAt(selectFila, 1).toString());
                    textField3.setText(table1.getValueAt(selectFila, 2).toString());
                    textField4.setText(table1.getValueAt(selectFila, 3).toString());
                    textField5.setText(table1.getValueAt(selectFila, 4).toString());
                    filas = selectFila;
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

    private void clear() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");
    }

    public void mostrar() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID cliente");
        model.addColumn("Nombre");
        model.addColumn("Telefono");
        model.addColumn("Direccion");
        model.addColumn("Correo");

        table1.setModel(model);
        String[] dato = new String[5];
        Connection con = ConexionBD.getconnection();

        try {
            Statement stat = con.createStatement();
            String query = "SELECT * FROM clientes";
            ResultSet fb = stat.executeQuery(query);

            while (fb.next()) {
                dato[0] = fb.getString(1);
                dato[1] = fb.getString(2);
                dato[2] = fb.getString(3);
                dato[3] = fb.getString(4);
                dato[4] = fb.getString(5);
                model.addRow(dato.clone()); // para evitar referencias compartidas
            }

            fb.close();
            stat.close();
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

        JTextField[] campos = { textField1, textField2, textField3, textField4, textField5 };
        for (JTextField campo : campos) {
            campo.setFont(fuenteCampos);
            campo.setBackground(Color.WHITE);
            campo.setForeground(colorTexto);
            campo.setBorder(BorderFactory.createLineBorder(colorTexto));
        }

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
        JFrame frame = new JFrame("Clientes");
        frame.setContentPane(new ClientesGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(880, 700);
        frame.setResizable(false);
    }
}
