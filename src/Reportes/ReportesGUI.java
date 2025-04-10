/**
 * Paquete que contiene las clases relacionadas con la generación y gestión de reportes.
 */
package Reportes;

import Conexion.ConexionBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.Option;
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

/**
 * Interfaz gráfica para la gestión de reportes del sistema de ferretería.
 * Permite generar diferentes tipos de reportes, visualizarlos en una tabla
 * y exportarlos a formato PDF.
 *
 * Esta clase utiliza la biblioteca Swing para la interfaz gráfica y JDBC para
 * la conexión a la base de datos.
 *
 * @author Cristian Restrepo
 * @version 1.0
 */

public class ReportesGUI extends JFrame {
    /** Panel principal de la interfaz */
    private JPanel main;
    /** Selector de tipo de reporte */
    private JComboBox<String> tipoReporteComboBox;
    /** Campo de texto para mostrar la fecha actual */
    private JTextField fechaTextField;
    /** Selector de empleado que genera el reporte */
    private JComboBox<String> empleadoComboBox;
    /** Área de texto para la descripción del reporte */
    private JTextArea descripcionTextArea;
    /** Botón para generar el reporte seleccionado */
    private JButton generarReporteButton;
    /** Botón para limpiar el formulario */
    private JButton limpiarButton;
    /** Tabla para mostrar los reportes generados */
    private JTable reportesTable;
    /** Botón para volver a la pantalla anterior */
    private JButton regresarButton;
    /** Botón para salir de la aplicación */
    private JButton salirButton;
    /** Botón para exportar el reporte actual a PDF */
    private JButton exportarPDFButton;
    /** Selector numérico para parámetros específicos de reportes */
    private JSpinner parametroSpinner;
    /** Etiqueta descriptiva para el spinner de parámetros */
    private JLabel parametroLabel;

    /** Modelo de tabla para los reportes */
    private DefaultTableModel tableModel;
    /** Conexión a la base de datos */
    private Connection conexion;
    /** Implementación de la lógica de reportes */
    private ReportesDAO reportesImpl;

    /** Mapa para almacenar ID y nombre completo de empleados */
    private Map<Integer, String> empleadosMap = new HashMap<>();
    /** Nombre del empleado actualmente seleccionado */
    private String nombreEmpleadoSeleccionado = "";

    /**
     * Obtiene el panel principal de la interfaz.
     * @return Panel principal de la interfaz gráfica
     */
    public JPanel getMainPanel() {
        return main;
    }

    /**
     * Constructor sin argumentos que obtiene la conexión internamente.
     * Utiliza la clase ConexionDB para establecer la conexión con la base de datos.
     */
    public ReportesGUI() {
        this(ConexionBD.getconnection());

        if (this.conexion == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo establecer conexión con la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Constructor que recibe una conexión a la base de datos.
     * @param conexion Conexión a la base de datos
     */
    public ReportesGUI(Connection conexion) {
        // Asignar correctamente el parámetro de conexión al campo de la clase
        this.conexion = conexion;

        setContentPane(main);
        setTitle("Sistema de Reportes - Ferretería");
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

    /**
     * Inicializa los componentes de la interfaz gráfica.
     * Configura los tipos de reportes disponibles y los parámetros específicos.
     */
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

    /**
     * Actualiza la visibilidad y configuración de los parámetros según el tipo de reporte seleccionado.
     * @param tipoReporte Tipo de reporte seleccionado
     */
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

    /**
     * Carga la lista de empleados desde la base de datos al ComboBox de empleados.
     */
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

    /**
     * Actualiza el nombre del empleado seleccionado basado en la selección del ComboBox.
     */
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

    /**
     * Actualiza automáticamente la descripción del reporte basado en las selecciones actuales.
     */
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

    /**
     * Configura el modelo de la tabla de reportes con sus columnas iniciales.
     */
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

    /**
     * Carga los reportes generados desde la base de datos a la tabla.
     */
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

    /**
     * Configura los listeners de los botones de la interfaz.
     */
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

        exportarPDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipoReporte = (String) tipoReporteComboBox.getSelectedItem();
                if (tipoReporte.equals("Seleccione un tipo de reporte")) {
                    JOptionPane.showMessageDialog(ReportesGUI.this,
                            "Por favor, primero genere un reporte para exportar",
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Asegurarse de que nombreEmpleadoSeleccionado esté actualizado
                actualizarNombreEmpleadoSeleccionado();

                // Llamar al método exportar con el nombre del empleado
                reportesImpl.exportarReporteActualAPDF(tipoReporte, nombreEmpleadoSeleccionado);
            }
        });

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                // Aquí puedes abrir la ventana anterior si es necesario
            }
        });

        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opcion = JOptionPane.showConfirmDialog(ReportesGUI.this,
                        "¿Está seguro de que desea salir?",
                        "Confirmar salida", JOptionPane.YES_NO_OPTION);
                if (opcion == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Guarda un registro del reporte generado en la base de datos.
     * @param tipoReporte Tipo de reporte que se generó
     */
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

    /**
     * Limpia el formulario y restablece los valores predeterminados.
     */
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

    /**
     * Genera un reporte personalizado (funcionalidad en desarrollo).
     */
    private void generarReporte() {
        // Este método implementaría la generación de reportes personalizados
        // Por ejemplo, podría abrir un diálogo adicional para configurar parámetros específicos
        JOptionPane.showMessageDialog(this,
                "La funcionalidad de reportes personalizados está en desarrollo.\n" +
                        "Por favor, seleccione otro tipo de reporte.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Método principal que inicia la aplicación.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
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