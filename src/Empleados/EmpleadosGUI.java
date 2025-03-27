package Empleados;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EmpleadosGUI extends JFrame {
    private EmpleadosDAO empleadosDAO;

    // Componentes de la interfaz
    private JTextField txtId;
    private JTextField txtNombre;
    private JComboBox<String> cboCargo;
    private JTextField txtSalario;
    private JTable tblEmpleados;
    private JButton btnAgregar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private DefaultTableModel modeloTabla;

    public EmpleadosGUI() {
        empleadosDAO = new EmpleadosDAO();

        // Configuración básica del JFrame
        setTitle("Gestión de Empleados - Ferretería");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new BorderLayout());

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Empleado"));

        // Componentes del formulario
        panelFormulario.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelFormulario.add(txtId);

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Cargo:"));
        cboCargo = new JComboBox<>(new String[]{"administrador", "vendedor"});
        panelFormulario.add(cboCargo);

        panelFormulario.add(new JLabel("Salario:"));
        txtSalario = new JTextField();
        panelFormulario.add(txtSalario);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        // Configurar tabla
        String[] columnas = {"ID", "Nombre", "Cargo", "Salario"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tblEmpleados = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tblEmpleados);

        // Agregar componentes al frame
        add(panelFormulario, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // Configurar listeners de botones
        configurarListeners();

        // Cargar empleados iniciales
        cargarEmpleados();
    }

    private void configurarListeners() {
        // Listener para agregar empleado
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarEmpleado();
            }
        });

        // Listener para actualizar empleado
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarEmpleado();
            }
        });

        // Listener para eliminar empleado
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarEmpleado();
            }
        });

        // Listener para limpiar formulario
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarFormulario();
            }
        });

        // Listener para selección de fila en la tabla
        tblEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tblEmpleados.getSelectedRow();
                if (filaSeleccionada != -1) {
                    mostrarDetallesEmpleado(filaSeleccionada);
                }
            }
        });
    }

    private void agregarEmpleado() {
        try {
            String nombre = txtNombre.getText();
            String cargo = (String) cboCargo.getSelectedItem();
            double salario = Double.parseDouble(txtSalario.getText());

            Empleados nuevoEmpleado = new Empleados();
            nuevoEmpleado.setNombre(nombre);
            nuevoEmpleado.setCargo(cargo);
            nuevoEmpleado.setSalario(salario);

            if (empleadosDAO.insertarEmpleado(nuevoEmpleado)) {
                JOptionPane.showMessageDialog(this, "Empleado agregado exitosamente");
                cargarEmpleados();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar empleado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un salario válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEmpleado() {
        try {
            int id = Integer.parseInt(txtId.getText());
            String nombre = txtNombre.getText();
            String cargo = (String) cboCargo.getSelectedItem();
            double salario = Double.parseDouble(txtSalario.getText());

            Empleados empleadoActualizado = new Empleados();
            empleadoActualizado.setId_empleado(id);
            empleadoActualizado.setNombre(nombre);
            empleadoActualizado.setCargo(cargo);
            empleadoActualizado.setSalario(salario);

            if (empleadosDAO.actualizarEmpleado(empleadoActualizado)) {
                JOptionPane.showMessageDialog(this, "Empleado actualizado exitosamente");
                cargarEmpleados();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar empleado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese datos válidos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEmpleado() {
        try {
            int id = Integer.parseInt(txtId.getText());

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar este empleado?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                if (empleadosDAO.eliminarEmpleado(id)) {
                    JOptionPane.showMessageDialog(this, "Empleado eliminado exitosamente");
                    cargarEmpleados();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar empleado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEmpleados() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Obtener lista de empleados
        List<Empleados> listaEmpleados = empleadosDAO.listarEmpleados();

        // Llenar tabla
        for (Empleados empleado : listaEmpleados) {
            modeloTabla.addRow(new Object[]{
                    empleado.getId_empleado(),
                    empleado.getNombre(),
                    empleado.getCargo(),
                    empleado.getSalario()
            });
        }
    }

    private void mostrarDetallesEmpleado(int fila) {
        txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        cboCargo.setSelectedItem(modeloTabla.getValueAt(fila, 2).toString());
        txtSalario.setText(modeloTabla.getValueAt(fila, 3).toString());
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        cboCargo.setSelectedIndex(0);
        txtSalario.setText("");
        tblEmpleados.clearSelection();
    }

    // Método main para probar la interfaz
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EmpleadosGUI().setVisible(true);
        });
    }
}