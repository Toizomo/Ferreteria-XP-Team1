package Empleados;

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
import java.util.Objects;

public class EmpleadosGUI {
    private JPanel main;
    private JTable table1;
    private JTextField textField1;
    private JTextField textField2;
    private JComboBox<String> comboBox1;
    private JTextField textField3;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JButton volverButton;
    EmpleadosDAO EmpleadosDAO = new EmpleadosDAO();
    ConexionBD ConexionBD = new ConexionBD();
    int filas = 0;

    public EmpleadosGUI() {
        mostrar();

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = textField2.getText();
                String cargo = Objects.requireNonNull(comboBox1.getSelectedItem()).toString();
                double salario = Double.parseDouble(textField3.getText());
                Empleados Empleados = new Empleados(0, nombre, cargo, salario);
                EmpleadosDAO.insertarEmpleado(Empleados);
                mostrar();
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = textField2.getText();
                String cargo = Objects.requireNonNull(comboBox1.getSelectedItem()).toString();
                double salario = Double.parseDouble(textField3.getText());
                int id_empleado = Integer.parseInt(textField1.getText());
                Empleados Empleados = new Empleados(id_empleado, nombre, cargo, salario);
                EmpleadosDAO.actualizarEmpleado(Empleados);
                mostrar();

            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id_empleado = Integer.parseInt(textField1.getText());
                EmpleadosDAO.eliminarEmpleado(id_empleado);
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
                    textField1.setText(String.valueOf(table1.getValueAt(selectFila, 0))); // ID Empleado
                    textField2.setText((String) table1.getValueAt(selectFila, 1)); // Nombre
                    comboBox1.setSelectedItem((String) table1.getValueAt(selectFila, 2)); // Cargo
                    textField3.setText(String.valueOf(table1.getValueAt(selectFila, 3)));

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

    public void clear() {
        textField1.setText("");
        textField2.setText("");
        comboBox1.setSelectedIndex(0);
        textField3.setText("");
    }

    public void mostrar()
    {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Empleado");
        model.addColumn("Nombre");
        model.addColumn("Cargo");
        model.addColumn("Salario");

        table1.setModel(model);
        String[] dato = new String[4];
        Connection con = ConexionBD.getconnection();

        try {
            Statement stat = con.createStatement();
            String query = "SELECT * FROM empleados";
            ResultSet fb = stat.executeQuery(query);

            while (fb.next())
            {
                dato[0] = fb.getString(1);
                dato[1] = fb.getString(2);
                dato[2] = fb.getString(3);
                dato[3] = fb.getString(4);

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
        JFrame frame = new JFrame("Empleados");
        frame.setContentPane(new EmpleadosGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(880,700);
        frame.setResizable(false);
    }
}
