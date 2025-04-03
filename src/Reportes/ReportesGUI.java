package Reportes;

import dao.ReportesDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ReportesGUI extends JFrame {
    private final ReportesDAO reportesDAO;
    private JTable tablaReporte;
    private JComboBox<String> cbTipoReporte;
    private JButton btnGenerar, btnExportar;
    private JDateChooser dcFechaInicio, dcFechaFin;
    private JSpinner spnMargen;

    public ReportesGUI() {
        reportesDAO = new ReportesDAO();
        configurarInterfaz();
    }

    private void configurarInterfaz() {
        setTitle("Reportes - Ferretería XP");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de controles
        JPanel panelControles = new JPanel(new GridLayout(0, 2, 10, 10));
        panelControles.setBorder(BorderFactory.createTitledBorder("Parámetros del Reporte"));

        // Componentes de controles
        panelControles.add(new JLabel("Tipo de Reporte:"));
        cbTipoReporte = new JComboBox<>(new String[]{
                "Ventas por período",
                "Productos más vendidos",
                "Stock bajo",
                "Clientes con más compras",
                "Ventas por empleado",
                "Productos por agotarse"
        });
        panelControles.add(cbTipoReporte);

        panelControles.add(new JLabel("Fecha Inicio:"));
        dcFechaInicio = new JDateChooser();
        dcFechaInicio.setDateFormatString("yyyy-MM-dd");
        panelControles.add(dcFechaInicio);

        panelControles.add(new JLabel("Fecha Fin:"));
        dcFechaFin = new JDateChooser();
        dcFechaFin.setDateFormatString("yyyy-MM-dd");
        panelControles.add(dcFechaFin);

        panelControles.add(new JLabel("Margen para stock (unidades):"));
        spnMargen = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        panelControles.add(spnMargen);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnGenerar = new JButton("Generar Reporte");
        btnExportar = new JButton("Exportar a Excel");

        btnGenerar.addActionListener(this::generarReporte);
        btnExportar.addActionListener(this::exportarReporte);

        panelBotones.add(btnGenerar);
        panelBotones.add(btnExportar);

        // Configurar tabla
        tablaReporte = new JTable();
        JScrollPane scrollPane = new JScrollPane(tablaReporte);

        // Agregar componentes al panel principal
        panelPrincipal.add(panelControles, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void generarReporte(ActionEvent e) {
        Date fechaInicio = dcFechaInicio.getDate();
        Date fechaFin = dcFechaFin.getDate();
        int margen = (int) spnMargen.getValue();

        // Validación básica de fechas para reportes que las requieren
        int reporteSeleccionado = cbTipoReporte.getSelectedIndex();
        if (reporteSeleccionado <= 4 && (fechaInicio == null || fechaFin == null)) {
            JOptionPane.showMessageDialog(this, "Seleccione un rango de fechas válido",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Map<String, Object>> datos = null;
        String titulo = "";

        switch (reporteSeleccionado) {
            case 0:
                datos = reportesDAO.generarReporteVentas(fechaInicio, fechaFin);
                titulo = "Reporte de Ventas del " + formatDate(fechaInicio) + " al " + formatDate(fechaFin);
                mostrarReporteTabla(datos, new String[]{"Fecha", "Cantidad de Ventas", "Total Vendido"}, titulo);
                mostrarGraficoVentas(datos);
                break;
            case 1:
                datos = reportesDAO.generarReporteProductosMasVendidos(fechaInicio, fechaFin);
                titulo = "Productos Más Vendidos del " + formatDate(fechaInicio) + " al " + formatDate(fechaFin);
                mostrarReporteTabla(datos, new String[]{"Producto", "Cantidad Vendida", "Total Ventas"}, titulo);
                mostrarGraficoBarras(datos, "Productos Más Vendidos", "Producto", "Cantidad Vendida");
                break;
            case 2:
                datos = reportesDAO.generarReporteStockBajo();
                titulo = "Productos con Stock Bajo";
                mostrarReporteTabla(datos, new String[]{"Producto", "Stock Actual", "Stock Mínimo", "Proveedor", "Faltante"}, titulo);
                break;
            case 3:
                datos = reportesDAO.generarReporteClientesTop(fechaInicio, fechaFin);
                titulo = "Clientes con Más Compras del " + formatDate(fechaInicio) + " al " + formatDate(fechaFin);
                mostrarReporteTabla(datos, new String[]{"Cliente", "Compras Realizadas", "Total Gastado"}, titulo);
                mostrarGraficoBarras(datos, "Clientes Top", "Cliente", "Total Gastado");
                break;
            case 4:
                datos = reportesDAO.generarReporteVentasPorEmpleado(fechaInicio, fechaFin);
                titulo = "Ventas por Empleado del " + formatDate(fechaInicio) + " al " + formatDate(fechaFin);
                mostrarReporteTabla(datos, new String[]{"Empleado", "Ventas Realizadas", "Total Vendido"}, titulo);
                mostrarGraficoBarras(datos, "Ventas por Empleado", "Empleado", "Total Vendido");
                break;
            case 5:
                datos = reportesDAO.generarReporteProductosPorAgotarse(margen);
                titulo = "Productos por Agotarse (Margen: " + margen + " unidades)";
                mostrarReporteTabla(datos, new String[]{"Producto", "Stock Actual", "Stock Mínimo", "% Restante"}, titulo);
                break;
        }
    }

    private void mostrarReporteTabla(List<Map<String, Object>> datos, String[] columnas, String titulo) {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (Map<String, Object> fila : datos) {
            Object[] rowData = new Object[columnas.length];
            for (int i = 0; i < columnas.length; i++) {
                rowData[i] = fila.get(columnas[i].toLowerCase().replace(" ", "_"));
            }
            modelo.addRow(rowData);
        }

        tablaReporte.setModel(modelo);
        setTitle(titulo + " - Ferretería XP");
    }

    private void mostrarGraficoVentas(List<Map<String, Object>> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map<String, Object> fila : datos) {
            String fecha = new SimpleDateFormat("dd/MM").format((Date)fila.get("fecha"));
            dataset.addValue((Number)fila.get("total"), "Ventas", fecha);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Tendencia de Ventas",
                "Fecha",
                "Total Vendido (S/)",
                dataset
        );

        mostrarGrafico(chart);
    }

    private void mostrarGraficoBarras(List<Map<String, Object>> datos, String titulo, String categoria, String valor) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map<String, Object> fila : datos) {
            dataset.addValue(
                    ((Number)fila.get(valor.toLowerCase().replace(" ", "_"))).doubleValue(),
                    valor,
                    fila.get(categoria.toLowerCase()).toString()
            );
        }

        JFreeChart chart = ChartFactory.createBarChart(
                titulo,
                categoria,
                valor,
                dataset
        );

        mostrarGrafico(chart);
    }

    private void mostrarGrafico(JFreeChart chart) {
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                new ChartPanel(chart),
                "Gráfico del Reporte",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void exportarReporte(ActionEvent e) {
        // Implementación para exportar a Excel
        JOptionPane.showMessageDialog(this, "Función de exportación a Excel en desarrollo",
                "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReportesGUI gui = new ReportesGUI();
            gui.setVisible(true);
        });
    }
}