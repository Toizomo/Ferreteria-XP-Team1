package Empleados;

import Conexion.ConexionBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class EmpleadosGUI {
    private JPanel main;
    private JTable table1;
    private JTextField textField1; // ID
    private JTextField textField2; // Nombre
    private JComboBox<String> comboBox1; // Cargo
    private JTextField textField3; // Salario
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    EmpleadosDAO empleadosDAO = new EmpleadosDAO();
    ConexionBD conexionBD = new ConexionBD();
    int filas = 0;

    public EmpleadosGUI() {
        // Puedes agregar más opciones si lo deseas
        comboBox1.addItem("Gerente");
        comboBox1.addItem("Desarrollador");
        comboBox1.addItem("Analista");
        comboBox1.addItem("Soporte");

        mostrar();

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = textField2.getText();
                String cargo = comboBox1.getSelectedItem().toString();
                double salario = Double.parseDouble(textField3.getText());

                Empleados empleados = new Empleados(
                        0,
                        nombre,
                        cargo,
                        "0000000000",         // teléfono por defecto
                        "email@ejemplo.com",  // email por defecto
                        cargo,
                        salario,
                        LocalDate.now(),      // fecha actual
                        "Activo",             // estado por defecto
                        "usuario",            // usuario por defecto
                        "contrasena"          // contraseña por defecto
                );

                empleadosDAO.insertarEmpleado(empleados);
                mostrar();
                limpiarCampos();
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id_empleado = Integer.parseInt(textField1.getText());
                String nombre = textField2.getText();
                String cargo = comboBox1.getSelectedItem().toString();
                double salario = Double.parseDouble(textField3.getText());

                Empleados empleados = new Empleados(
                        id_empleado,
                        nombre,
                        cargo,
                        "0000000000",
                        "email@ejemplo.com",
                        cargo,
                        salario,
                        LocalDate.now(),
                        "Activo",
                        "usuario",
                        "contrasena"
                );

                empleadosDAO.actualizarEmpleado(empleados);
                mostrar();
                limpiarCampos();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id_empleado = Integer.parseInt(textField1.getText());
                empleadosDAO.eliminarEmpleado(id_empleado);
                mostrar();
                limpiarCampos();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int filaSeleccionada = table1.getSelectedRow();

                if (filaSeleccionada >= 0) {
                    textField1.setText(String.valueOf(table1.getValueAt(filaSeleccionada, 0)));
                    textField2.setText((String) table1.getValueAt(filaSeleccionada, 1));
                    comboBox1.setSelectedItem((String) table1.getValueAt(filaSeleccionada, 2));
                    textField3.setText(String.valueOf(table1.getValueAt(filaSeleccionada, 3)));
                    filas = filaSeleccionada;
                }
            }
        });
    }

    public void mostrar() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Empleado");
        model.addColumn("Nombre");
        model.addColumn("Cargo");
        model.addColumn("Salario");

        table1.setModel(model);
        String[] dato = new String[4];
        Connection con = conexionBD.getconnection();

        try {
            Statement stat = con.createStatement();
            String query = "SELECT id_empleado, nombre, cargo, salario FROM empleados";
            ResultSet rs = stat.executeQuery(query);

            while (rs.next()) {
                dato[0] = rs.getString("id_empleado");
                dato[1] = rs.getString("nombre");
                dato[2] = rs.getString("cargo");
                dato[3] = rs.getString("salario");

                model.addRow(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        comboBox1.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Empleados");
        frame.setContentPane(new EmpleadosGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(880, 700);
        frame.setResizable(false);
    }
}
