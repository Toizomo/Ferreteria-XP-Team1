package Proveedores;

import Conexion.ConexionBD;

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

public class ProveedoresGUI {

    private JPanel main;
    private JTable table1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    ProveedoresDAO ProveedoresDAO = new ProveedoresDAO();
    ConexionBD ConexionBD = new ConexionBD();
    int filas = 0;


    public ProveedoresGUI()
    {
        agregarButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String nombre = textField2.getText();
                String contacto = textField3.getText();

                Proveedores Proveedores = new Proveedores(0, nombre, contacto);
                ProveedoresDAO.agregar(Proveedores);
                mostrar();
            }
        });

        actualizarButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String nombre = textField2.getText();
                String contacto = textField3.getText();
                int id_proveedor = Integer.parseInt(textField1.getText());
                Proveedores Proveedores = new Proveedores(0, nombre, contacto);
                ProveedoresDAO.actualizar(Proveedores);
                mostrar();
            }
        });

        eliminarButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int id_proveedor = Integer.parseInt(textField1.getText());
                ProveedoresDAO.eliminar(id_proveedor);
                mostrar();
            }
        });

        table1.addMouseListener(new MouseAdapter()
        {
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

                    filas = selectFila;
                }
            }
        });
    }

    public void mostrar()
    {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Proveedor");
        model.addColumn("Nombre");
        model.addColumn("Contacto");

        table1.setModel(model);
        String [] dato = new String[3];
        Connection con = ConexionBD.getconnection();

        try
        {
            Statement stat = con.createStatement();
            String query = "SELECT * FROM proveedores";
            ResultSet fb = stat.executeQuery(query);

            while (fb.next())
            {
                dato[0] = fb.getString(1);
                dato[1] = fb.getString(2);
                dato[2] = fb.getString(3);
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
        JFrame frame = new JFrame("Proveedores");
        frame.setContentPane(new ProveedoresGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(880,700);
        frame.setResizable(false);
    }
}
