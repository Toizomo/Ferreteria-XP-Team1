package Reportes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;

/**
 * Clase que maneja la generación y exportación de reportes del sistema de ferretería.
 * Permite generar diferentes tipos de reportes y exportarlos a PDF.
 *
 * @author Cristian Restrepo
 * @version 1.0
 */
public class ReportesDAO {
    private Connection conexion;
    private JTable reportesTable;
    private DefaultTableModel tableModel;

    /**
     * Constructor de la clase ReportesDAO.
     *
     * @param conexion Conexión a la base de datos
     * @param reportesTable Tabla donde se mostrarán los reportes
     * @param tableModel Modelo de tabla para los datos
     */
    public ReportesDAO(Connection conexion, JTable reportesTable, DefaultTableModel tableModel) {
        this.conexion = conexion;
        this.reportesTable = reportesTable;
        this.tableModel = tableModel;
    }

    /**
     * Genera un reporte de ventas por periodo (diario, semanal o mensual).
     *
     * @param periodo Periodo para el cual se generará el reporte (diario, semanal, mensual)
     * @throws IllegalArgumentException Si el periodo no es válido
     */
    public void generarReporteVentasPorPeriodo(String periodo) {
        tableModel.setRowCount(0);

        // Configurar columnas específicas para este reporte
        configurarColumnas(new String[]{"Fecha", "Total Ventas", "Cantidad de Órdenes"});

        try {
            String sql = "";
            String groupBy = "";
            String dateFormat = "";

            switch (periodo.toLowerCase()) {
                case "diario":
                    sql = "SELECT DATE(fecha_compra) as fecha, SUM(total) as total_ventas, COUNT(*) as num_ordenes " +
                            "FROM ordenes_compra " +
                            "WHERE fecha_compra >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) " +
                            "GROUP BY DATE(fecha_compra) " +
                            "ORDER BY fecha DESC";
                    break;
                case "semanal":
                    sql = "SELECT YEARWEEK(fecha_compra, 1) as semana, " +
                            "CONCAT('Semana ', WEEK(fecha_compra, 1), ' - ', YEAR(fecha_compra)) as periodo, " +
                            "SUM(total) as total_ventas, COUNT(*) as num_ordenes " +
                            "FROM ordenes_compra " +
                            "WHERE fecha_compra >= DATE_SUB(CURRENT_DATE(), INTERVAL 12 WEEK) " +
                            "GROUP BY YEARWEEK(fecha_compra, 1) " +
                            "ORDER BY semana DESC";
                    break;
                case "mensual":
                    sql = "SELECT CONCAT(YEAR(fecha_compra), '-', MONTH(fecha_compra)) as mes, " +
                            "CONCAT(MONTHNAME(fecha_compra), ' ', YEAR(fecha_compra)) as periodo, " +
                            "SUM(total) as total_ventas, COUNT(*) as num_ordenes " +
                            "FROM ordenes_compra " +
                            "WHERE fecha_compra >= DATE_SUB(CURRENT_DATE(), INTERVAL 12 MONTH) " +
                            "GROUP BY YEAR(fecha_compra), MONTH(fecha_compra) " +
                            "ORDER BY YEAR(fecha_compra) DESC, MONTH(fecha_compra) DESC";
                    break;
                default:
                    throw new IllegalArgumentException("Periodo no válido: " + periodo);
            }

            PreparedStatement stmt = conexion.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row;
                if (periodo.equalsIgnoreCase("diario")) {
                    row = new Object[]{
                            rs.getString("fecha"),
                            String.format("$%.2f", rs.getDouble("total_ventas")),
                            rs.getInt("num_ordenes")
                    };
                } else {
                    row = new Object[]{
                            rs.getString("periodo"),
                            String.format("$%.2f", rs.getDouble("total_ventas")),
                            rs.getInt("num_ordenes")
                    };
                }
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte de ventas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Genera un reporte de los productos más vendidos.
     *
     * @param limite Cantidad máxima de productos a mostrar en el reporte
     */
    public void generarReporteProductosMasVendidos(int limite) {
        tableModel.setRowCount(0);

        // Configurar columnas específicas para este reporte
        configurarColumnas(new String[]{"ID Producto", "Nombre Producto", "Categoría", "Cantidad Vendida", "Total Generado"});

        try {
            String sql = "SELECT p.id_producto, p.nombre_producto, p.categoria, " +
                    "COUNT(o.id_orden_compra) as num_ventas, " +
                    "SUM(o.total) as total_generado " +
                    "FROM inventario_productos p " +
                    "JOIN ordenes_compra o ON p.id_producto = o.id_producto " +
                    "GROUP BY p.id_producto, p.nombre_producto, p.categoria " +
                    "ORDER BY num_ventas DESC, total_generado DESC " +
                    "LIMIT ?";

            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getString("categoria"),
                        rs.getInt("num_ventas"),
                        String.format("$%.2f", rs.getDouble("total_generado"))
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte de productos más vendidos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Genera un reporte de los clientes con mayor número de compras.
     *
     * @param limite Cantidad máxima de clientes a mostrar en el reporte
     */
    public void generarReporteClientesConMasCompras(int limite) {
        tableModel.setRowCount(0);

        // Configurar columnas específicas para este reporte
        configurarColumnas(new String[]{"ID Cliente", "Nombre Cliente", "Compras Realizadas", "Total Gastado", "Última Compra"});

        try {

            //  La tabla clientes con id_cliente y nombre
            String sql = "SELECT c.id_cliente, c.nombre as nombre_cliente, " +
                    "COUNT(o.id_orden_compra) as num_compras, " +
                    "SUM(o.total) as total_gastado, " +
                    "MAX(o.fecha_compra) as ultima_compra " +
                    "FROM clientes c " +
                    "JOIN ordenes_compra o ON c.id_cliente = o.id_cliente " +
                    "GROUP BY c.id_cliente, c.nombre " +
                    "ORDER BY num_compras DESC, total_gastado DESC " +
                    "LIMIT ?";

            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("nombre_cliente"),
                        rs.getInt("num_compras"),
                        String.format("$%.2f", rs.getDouble("total_gastado")),
                        rs.getTimestamp("ultima_compra")
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte de clientes con más compras: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Genera un reporte de productos con stock bajo (por debajo del umbral especificado).
     *
     * @param umbralStock Valor umbral para considerar el stock como bajo
     */
    public void generarReporteStockBajo(int umbralStock) {
        tableModel.setRowCount(0);

        // Configurar columnas específicas para este reporte
        configurarColumnas(new String[]{"ID Producto", "Nombre Producto", "Categoría", "Stock Actual", "Precio", "Proveedor ID"});

        try {
            String sql = "SELECT p.id_producto, p.nombre_producto, p.categoria, " +
                    "p.cantidad_stock, p.precio_producto, p.id_proveedor_asociado " +
                    "FROM inventario_productos p " +
                    "WHERE p.cantidad_stock <= ? " +
                    "ORDER BY p.cantidad_stock ASC, p.nombre_producto ASC";

            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, umbralStock);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad_stock"),
                        String.format("$%.2f", rs.getDouble("precio_producto")),
                        rs.getInt("id_proveedor_asociado")
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte de stock bajo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método auxiliar para configurar las columnas de la tabla según el tipo de reporte.
     *
     * @param columnas Array con los nombres de las columnas a configurar
     */
    private void configurarColumnas(String[] columnas) {
        tableModel.setColumnCount(0);
        for (String columna : columnas) {
            tableModel.addColumn(columna);
        }
    }

    /**
     * Exporta el reporte actual a un archivo PDF usando la biblioteca iText.
     * Permite al usuario seleccionar la ubicación donde guardar el archivo.
     *
     * @param tipoReporte Tipo de reporte que se está exportando
     * @param nombreEmpleado Nombre del empleado que genera el reporte
     */
    public void exportarReporteActualAPDF(String tipoReporte, String nombreEmpleado) {
        try {
            // Crear el diálogo para seleccionar dónde guardar el archivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar reporte PDF");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setSelectedFile(new File(tipoReporte.replace(" ", "_") + ".pdf"));

            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File archivoSeleccionado = fileChooser.getSelectedFile();
            String rutaArchivo = archivoSeleccionado.getAbsolutePath();
            if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }

            // Inicializar documento PDF
            Document documento = new Document(PageSize.A4);
            PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));
            documento.open();

            // Agregar título del reporte
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte: " + tipoReporte, fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            // Agregar información del reporte
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 12);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            Paragraph fechaParrafo = new Paragraph("Fecha de generación: " + sdf.format(new Date()), fontNormal);
            fechaParrafo.setSpacingBefore(10);
            documento.add(fechaParrafo);

            if (nombreEmpleado != null && !nombreEmpleado.isEmpty() && !nombreEmpleado.startsWith("Seleccione")) {
                Paragraph empleadoParrafo = new Paragraph("Generado por: " + nombreEmpleado, fontNormal);
                documento.add(empleadoParrafo);
            }

            documento.add(new Paragraph(" ")); // Espacio

            // Crear tabla para el reporte
            PdfPTable pdfTable = new PdfPTable(reportesTable.getColumnCount());
            pdfTable.setWidthPercentage(100);

            // Agregar encabezados
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            for (int i = 0; i < reportesTable.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(reportesTable.getColumnName(i), fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            // Agregar datos
            for (int row = 0; row < reportesTable.getRowCount(); row++) {
                for (int col = 0; col < reportesTable.getColumnCount(); col++) {
                    Object value = reportesTable.getValueAt(row, col);
                    String texto = (value != null) ? value.toString() : "";
                    PdfPCell cell = new PdfPCell(new Phrase(texto, fontNormal));
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }
            }

            documento.add(pdfTable);

            // Agregar información adicional
            Paragraph infoAdicional = new Paragraph("\nEste reporte fue generado automáticamente por el Sistema de Reportes de Ferretería.",
                    new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
            infoAdicional.setSpacingBefore(10);
            documento.add(infoAdicional);

            // Cerrar el documento
            documento.close();

            JOptionPane.showMessageDialog(null,
                    "Reporte exportado exitosamente a:\n" + rutaArchivo,
                    "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);

        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al exportar el reporte a PDF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}