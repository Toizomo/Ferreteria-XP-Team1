package Clientes.ClientesGUI;

import Clientes.Clientes;
import Clientes.ClientesDAO;
import Conexion.ConexionBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientesGUI {
    private JTable table1;
    private JPanel panel1;
    private JTextField textField1; // ID
    private JTextField textField2; // Nombre
    private JTextField textField3; // Teléfono
    private JTextField textField4; // Dirección
    private JTextField textField5; // Correo
    private JButton agregarButton;
    private JButton eliminarButton;
    private JButton actualizarButton;

    private ClientesDAO clientesDAO = new ClientesDAO();

    // Constructor de la clase
    public ClientesGUI() {
        textField1.setEnabled(false); // ID no editable
        obtenerDatos();

        agregarButton.addActionListener(e -> {
            String nombre = textField2.getText();
            String telefono = textField3.getText();
            String direccion = textField4.getText();
            String correo = textField5.getText();

            Clientes cliente = new Clientes(0, nombre, telefono, direccion, correo);
            clientesDAO.agregar(cliente);
            obtenerDatos();
            clear();
        });

        actualizarButton.addActionListener(e -> {
            String nombre = textField2.getText();
            String telefono = textField3.getText();
            String direccion = textField4.getText();
            String correo = textField5.getText();
            int id = Integer.parseInt(textField1.getText());

            Clientes cliente = new Clientes(id, nombre, telefono, direccion, correo);
            clientesDAO.actualizar(cliente);
            obtenerDatos();
            clear();
        });

        eliminarButton.addActionListener(e -> {
            int id = Integer.parseInt(textField1.getText());
            clientesDAO.eliminar(id);
            obtenerDatos();
            clear();
        });

        table1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectFila = table1.getSelectedRow();
                if (selectFila >= 0) {
                    textField1.setText(table1.getValueAt(selectFila, 0).toString());
                    textField2.setText(table1.getValueAt(selectFila, 1).toString());
                    textField3.setText(table1.getValueAt(selectFila, 2).toString());
                    textField4.setText(table1.getValueAt(selectFila, 3).toString());
                    textField5.setText(table1.getValueAt(selectFila, 4).toString());
                }
            }
        });
    }

    // Método para limpiar los campos de texto
    public void clear() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");
    }

    // Método para obtener los datos de la tabla
    public void obtenerDatos() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Teléfono");
        model.addColumn("Dirección");
        model.addColumn("Correo");

        table1.setModel(model);
        String[] dato = new String[5];

        Connection con = new ConexionBD().getconnection();
        try {
            Statement stmt = con.createStatement();
            String query = "SELECT * FROM clientes"; // Asegúrate de que la tabla se llame 'clientes'
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                dato[0] = rs.getString("id_clientes");
                dato[1] = rs.getString("nombre");
                dato[2] = rs.getString("telefono");
                dato[3] = rs.getString("direccion");
                dato[4] = rs.getString("correo");
                model.addRow(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Clientes");
        frame.setContentPane(new ClientesGUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(800, 600);
        frame.setResizable(false);
    }
}