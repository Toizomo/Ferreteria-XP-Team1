/**
 * Paquete que contiene las clases relacionadas con la generación y gestión de reportes.
 */
package Reportes;

import Conexion.ConexionBD;
import MenuPrincipal.MenuPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ReportesGUI extends JFrame {
    private JPanel main;
    private JComboBox<String> tipoReporteComboBox;
    private JTextField fechaTextField;
    private JComboBox<String> empleadoComboBox;
    private JTextArea descripcionTextArea;
    private JButton generarReporteButton;
    private JButton limpiarButton;
    private JTable reportesTable;
    private JButton regresarButton;
    private JButton salirButton;
    private JSpinner parametroSpinner;
    private JLabel parametroLabel;

    private DefaultTableModel tableModel;
    private Connection conexion;
    private ReportesDAO reportesImpl;

    private Map<Integer, String> empleadosMap = new HashMap<>();
    private String nombreEmpleadoSeleccionado = "";


    public JPanel getMainPanel() {
        return main;
    }

    public ReportesGUI() {
        this(ConexionBD.getconnection());

        if (this.conexion == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo establecer conexión con la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ReportesGUI(Connection conexion) {
        // Asignar correctamente el parámetro de conexión al campo de la clase
        this.conexion = conexion;
        salirButton = new JButton("Salir");


        setContentPane(main);
        setTitle("Reportes");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Configurar fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fechaTextField.setText(dateFormat.format(new Date()));

        // Inicializar componentes adicionales
        inicializarComponentes();

        // Cargar empleados desde la base de datos
        cargarEmpleados();

        // Configurar tabla de reportes
        configurarTabla();

        // Inicializar implementación de reportes
        reportesImpl = new ReportesDAO(conexion, reportesTable, tableModel);

        // Configurar botones
        configurarBotones();

        // Configurar la detección del cambio de selección en el combobox de empleados
        empleadoComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarNombreEmpleadoSeleccionado();
                actualizarDescripcionAutomatica();
            }
        });

        setVisible(true);
    }

    private void inicializarComponentes() {
        // Configurar tipos de reportes disponibles
        if (tipoReporteComboBox.getItemCount() == 0) {
            tipoReporteComboBox.addItem("Seleccione un tipo de reporte");
            tipoReporteComboBox.addItem("Ventas Diarias");
            tipoReporteComboBox.addItem("Ventas Semanales");
            tipoReporteComboBox.addItem("Ventas Mensuales");
            tipoReporteComboBox.addItem("Productos Más Vendidos");
            tipoReporteComboBox.addItem("Clientes con Más Compras");
            tipoReporteComboBox.addItem("Stock Bajo");
            tipoReporteComboBox.addItem("Reporte Personalizado");
        }

        // Configurar el spinner para parámetros
        parametroSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        parametroLabel = new JLabel("Límite de registros:");

        // Añadir estos componentes al panel, aunque no están definidos en el código original
        // Tendrías que añadirlos a tu diseño de GUI

        // Configurar listener para el cambio de tipo de reporte
        tipoReporteComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipoSeleccionado = (String) tipoReporteComboBox.getSelectedItem();
                actualizarParametrosPorTipoReporte(tipoSeleccionado);
                actualizarDescripcionAutomatica();
            }
        });
    }

    private void actualizarParametrosPorTipoReporte(String tipoReporte) {
        if (parametroLabel == null || parametroSpinner == null) {
            return; // Evitar NullPointerException si no están inicializados
        }

        if (tipoReporte == null || tipoReporte.equals("Seleccione un tipo de reporte")) {
            parametroLabel.setVisible(false);
            parametroSpinner.setVisible(false);
            return;
        }

        switch (tipoReporte) {
            case "Productos Más Vendidos":
            case "Clientes con Más Compras":
                parametroLabel.setText("Límite de registros:");
                parametroSpinner.setValue(5);
                parametroLabel.setVisible(true);
                parametroSpinner.setVisible(true);
                break;
            case "Stock Bajo":
                parametroLabel.setText("Umbral de stock:");
                parametroSpinner.setValue(10);
                parametroLabel.setVisible(true);
                parametroSpinner.setVisible(true);
                break;
            default:
                parametroLabel.setVisible(false);
                parametroSpinner.setVisible(false);
                break;
        }
    }

    private void cargarEmpleados() {
        try {
            // Comprobar si la conexión es nula antes de usarla
            if (conexion == null) {
                System.out.println("La conexión es nula en cargarEmpleados()");
                conexion = ConexionBD.getconnection();
                if (conexion == null) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo establecer conexión con la base de datos.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            PreparedStatement stmt = conexion.prepareStatement("SELECT id_empleado, nombre FROM empleados ORDER BY nombre");
            ResultSet rs = stmt.executeQuery();

            empleadoComboBox.removeAllItems();
            empleadoComboBox.addItem("Seleccione un empleado");
            empleadosMap.clear();

            while (rs.next()) {
                int idEmpleado = rs.getInt("id_empleado");
                String nombreEmpleado = rs.getString("nombre");
                empleadoComboBox.addItem(idEmpleado + " - " + nombreEmpleado);
                empleadosMap.put(idEmpleado, nombreEmpleado);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarNombreEmpleadoSeleccionado() {
        String seleccion = (String) empleadoComboBox.getSelectedItem();
        if (seleccion != null && !seleccion.equals("Seleccione un empleado")) {
            try {
                int idEmpleado = Integer.parseInt(seleccion.split(" - ")[0]);
                nombreEmpleadoSeleccionado = empleadosMap.get(idEmpleado);
                if (nombreEmpleadoSeleccionado == null) {
                    nombreEmpleadoSeleccionado = seleccion.split(" - ")[1]; // Fallback
                }
            } catch (Exception e) {
                nombreEmpleadoSeleccionado = "";
                System.out.println("Error al obtener nombre de empleado: " + e.getMessage());
            }
        } else {
            nombreEmpleadoSeleccionado = "";
        }
    }

    private void actualizarDescripcionAutomatica() {
        String tipoReporte = (String) tipoReporteComboBox.getSelectedItem();
        if (tipoReporte != null && !tipoReporte.equals("Seleccione un tipo de reporte")) {
            StringBuilder descripcion = new StringBuilder("Reporte ");
            descripcion.append(tipoReporte);

            if (!nombreEmpleadoSeleccionado.isEmpty()) {
                descripcion.append(" generado por ").append(nombreEmpleadoSeleccionado);
            }

            // Añadir parámetros específicos según el tipo de reporte
            if (parametroSpinner.isVisible()) {
                int valor = (Integer) parametroSpinner.getValue();
                if (tipoReporte.equals("Stock Bajo")) {
                    descripcion.append(". Umbral de stock: ").append(valor);
                } else {
                    descripcion.append(". Límite: ").append(valor).append(" registros");
                }
            }

            descripcionTextArea.setText(descripcion.toString());
        }
    }

    private void configurarTabla() {
        tableModel = new DefaultTableModel();
        // Columnas iniciales, se actualizarán según el tipo de reporte
        tableModel.addColumn("ID");
        tableModel.addColumn("Tipo");
        tableModel.addColumn("Fecha");
        tableModel.addColumn("Empleado ID");
        tableModel.addColumn("Descripción");

        reportesTable.setModel(tableModel);
    }

    private void cargarReportes() {
        tableModel.setRowCount(0);

        try {
            // Comprobar si la conexión es nula antes de usarla
            if (conexion == null) {
                System.out.println("La conexión es nula en cargarReportes()");
                conexion = ConexionBD.getconnection();
                if (conexion == null) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo establecer conexión con la base de datos.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            PreparedStatement stmt = conexion.prepareStatement(
                    "SELECT * FROM reportes_generados ORDER BY fecha_compra DESC");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getInt("id_reporte");
                row[1] = rs.getString("tipo_reporte");
                row[2] = rs.getString("fecha_compra");
                row[3] = rs.getInt("id_empleado");

                String descripcion = rs.getString("descripcion");
                if (descripcion != null && descripcion.length() > 30) {
                    descripcion = descripcion.substring(0, 30) + "...";
                }
                row[4] = descripcion;

                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar reportes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarBotones() {
        generarReporteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipoReporte = (String) tipoReporteComboBox.getSelectedItem();

                if (tipoReporte.equals("Seleccione un tipo de reporte")) {
                    JOptionPane.showMessageDialog(ReportesGUI.this,
                            "Por favor, seleccione un tipo de reporte",
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Asegurarse de que nombreEmpleadoSeleccionado esté actualizado
                actualizarNombreEmpleadoSeleccionado();

                switch (tipoReporte) {
                    case "Ventas Diarias":
                        reportesImpl.generarReporteVentasPorPeriodo("diario");
                        break;
                    case "Ventas Semanales":
                        reportesImpl.generarReporteVentasPorPeriodo("semanal");
                        break;
                    case "Ventas Mensuales":
                        reportesImpl.generarReporteVentasPorPeriodo("mensual");
                        break;
                    case "Productos Más Vendidos":
                        int limite = (Integer) parametroSpinner.getValue();
                        reportesImpl.generarReporteProductosMasVendidos(limite);
                        break;
                    case "Clientes con Más Compras":
                        limite = (Integer) parametroSpinner.getValue();
                        reportesImpl.generarReporteClientesConMasCompras(limite);
                        break;
                    case "Stock Bajo":
                        int umbral = (Integer) parametroSpinner.getValue();
                        reportesImpl.generarReporteStockBajo(umbral);
                        break;
                    case "Reporte Personalizado":
                        generarReporte(); // Usa el método original de generación de reportes
                        break;
                    default:
                        JOptionPane.showMessageDialog(ReportesGUI.this,
                                "Tipo de reporte no implementado",
                                "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Registrar que se generó un reporte
                guardarRegistroReporte(tipoReporte);
            }
        });

        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarFormulario();
            }
        });


        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Cierra la ventana de reportes
                MenuPrincipal.main(null);
            }
        });

    }

    private void guardarRegistroReporte(String tipoReporte) {
        try {
            if (conexion == null) {
                conexion = ConexionBD.getconnection();
                if (conexion == null) {
                    return;
                }
            }

            // Obtener ID del empleado
            int idEmpleado = 0;
            String empleadoSeleccionado = (String) empleadoComboBox.getSelectedItem();
            if (empleadoSeleccionado != null && !empleadoSeleccionado.equals("Seleccione un empleado")) {
                idEmpleado = Integer.parseInt(empleadoSeleccionado.split(" - ")[0]);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fecha = sdf.format(new Date());
            String descripcion = descripcionTextArea.getText();

            String sql = "INSERT INTO reportes_generados (tipo_reporte, fecha_compra, id_empleado, descripcion) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, tipoReporte);
            stmt.setString(2, fecha);
            stmt.setInt(3, idEmpleado);
            stmt.setString(4, descripcion);

            int result = stmt.executeUpdate();
            stmt.close();

            if (result > 0) {
                System.out.println("Registro de reporte guardado correctamente");
                // Opcionalmente refrescar la lista de reportes
                // cargarReportes();
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar registro de reporte: " + e.getMessage());
            // No mostrar mensaje al usuario para no interrumpir el flujo
        }
    }

    private void limpiarFormulario() {
        tipoReporteComboBox.setSelectedIndex(0);
        empleadoComboBox.setSelectedIndex(0);
        descripcionTextArea.setText("");
        parametroSpinner.setValue(5);
        parametroLabel.setVisible(false);
        parametroSpinner.setVisible(false);

        // Actualizar la fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fechaTextField.setText(dateFormat.format(new Date()));

        // Limpiar la tabla
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }

        nombreEmpleadoSeleccionado = "";
    }

    private void generarReporte() {
        // Este método implementaría la generación de reportes personalizados
        // Por ejemplo, podría abrir un diálogo adicional para configurar parámetros específicos
        JOptionPane.showMessageDialog(this,
                "La funcionalidad de reportes personalizados está en desarrollo.\n" +
                        "Por favor, seleccione otro tipo de reporte.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            // Aplicar Look and Feel del sistema para que la aplicación se vea más nativa
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ejecutar la aplicación
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ReportesGUI();
            }
        });
    }
}