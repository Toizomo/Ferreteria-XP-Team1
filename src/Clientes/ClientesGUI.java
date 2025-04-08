package Clientes;

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
        mostrar();

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
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
            public void actionPerformed(ActionEvent e)
            {
                String nombre = textField2.getText();
                String telefono = textField3.getText();
                String direccion = textField4.getText();
                String correo = textField5.getText();
                int id_clientes = Integer.parseInt(textField1.getText());
                Clientes Clientes = new Clientes(id_clientes, nombre, telefono, direccion, correo);
                ClientesDAO.actualizar(Clientes);
                mostrar();
                clear();
            }
        });


        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int id_clientes = Integer.parseInt(textField1.getText());
                ClientesDAO.eliminar(id_clientes);
                mostrar();
                clear();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                int selectFila = table1.getSelectedRow();

                if (selectFila >= 0)
                {
                    textField1.setText((String) table1.getValueAt(selectFila, 0));
                    textField2.setText((String) table1.getValueAt(selectFila, 1));
                    textField3.setText((String) table1.getValueAt(selectFila, 2));
                    textField4.setText((String) table1.getValueAt(selectFila, 3));

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

    public void mostrar()
    {
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

            while (fb.next())
            {
                dato[0] = fb.getString(1);
                dato[1] = fb.getString(2);
                dato[2] = fb.getString(3);
                dato[3] = fb.getString(4);
                dato[4] = fb.getString(5);
                model.addRow(dato);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Clientes");
        frame.setContentPane(new ClientesGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(880,700);
        frame.setResizable(false);
    }
}
