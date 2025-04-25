package Reportes;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportesDAO {
    private Connection conexion;
    private JTable reportesTable;
    private DefaultTableModel tableModel;

    public ReportesDAO(Connection conexion, JTable reportesTable, DefaultTableModel tableModel) {
        this.conexion = conexion;
        this.reportesTable = reportesTable;
        this.tableModel = tableModel;
    }

    public void generarReporteVentasPorPeriodo(String periodo) {
        tableModel.setRowCount(0);

        // Configurar columnas específicas para este reporte
        configurarColumnas(new String[]{"Fecha", "Total Ventas", "Cantidad de Órdenes"});

        try {
            String sql = "";

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

    public void generarReporteProductosMasVendidos(int limite) {
        tableModel.setRowCount(0);

        // Configurar columnas específicas para este reporte
        configurarColumnas(new String[]{"ID Producto", "Nombre Producto", "Categoría", "Cantidad Vendida", "Total Generado"});

        try {
            String sql = "SELECT p.id_producto, p.nombre_producto, p.categoria, " +
                    "SUM(rv.cantidad) as cantidad_vendida, " +
                    "SUM(rv.sub_total) as total_generado " +
                    "FROM inventario_productos p " +
                    "JOIN registro_ventas rv ON p.id_producto = rv.id_producto " +
                    "GROUP BY p.id_producto, p.nombre_producto, p.categoria " +
                    "ORDER BY cantidad_vendida DESC, total_generado DESC " +
                    "LIMIT ?";

            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad_vendida"),
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

    public void generarReporteClientesConMasCompras(int limite) {
        tableModel.setRowCount(0);

        // Configurar columnas específicas para este reporte
        configurarColumnas(new String[]{"ID Cliente", "Nombre Cliente", "Compras Realizadas", "Total Gastado", "Última Compra"});

        try {
            String sql = "SELECT c.id_cliente, c.nombre as nombre_cliente, " +
                    "COUNT(DISTINCT o.id_orden_compra) as num_compras, " +
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

    private void configurarColumnas(String[] columnas) {
        tableModel.setColumnCount(0);
        for (String columna : columnas) {
            tableModel.addColumn(columna);
        }
    }

    public void generarFacturaPDF(String tipoReporte, String nombreEmpleado) {
        try {
            // Crear el diálogo para seleccionar dónde guardar el archivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar factura PDF");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setSelectedFile(new File("Factura_" + tipoReporte.replace(" ", "_") + ".pdf"));

            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File archivoSeleccionado = fileChooser.getSelectedFile();
            String rutaArchivo = archivoSeleccionado.getAbsolutePath();
            if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }

            // Configurar el documento
            Document documento = new Document(PageSize.A4);
            PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));
            documento.open();

            // Definir fuentes
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
            Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            Font fontNegrita = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
            Font fontPequeña = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);


            // Título y datos de la empresa
            Paragraph titulo = new Paragraph();
            addEmptyLine(titulo, 5); // Espacio para el logo
            titulo.add(new Paragraph("FERRETERÍA Future soft", fontTitulo));
            titulo.add(new Paragraph("NIT: 900.123.456-7", fontNormal));
            titulo.add(new Paragraph("Sede Sagrado", fontNormal));
            titulo.add(new Paragraph("Teléfono: (57) 123-4567", fontNormal));
            titulo.add(new Paragraph("Email: contacto@ferreteriafuturesotf.com", fontNormal));
            titulo.setAlignment(Element.ALIGN_RIGHT);
            documento.add(titulo);

            // Datos de la factura
            Paragraph datosFactura = new Paragraph();
            addEmptyLine(datosFactura, 2);
            datosFactura.add(new Paragraph("FACTURA DE " + tipoReporte.toUpperCase(), fontSubtitulo));

            // Obtener un ID único para la factura (puede ser con timestamp o un contador)
            SimpleDateFormat sdfId = new SimpleDateFormat("yyyyMMddHHmmss");
            String idFactura = sdfId.format(new Date());
            datosFactura.add(new Paragraph("No. " + idFactura, fontSubtitulo));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            datosFactura.add(new Paragraph("Fecha: " + sdf.format(new Date()), fontNormal));
            datosFactura.setAlignment(Element.ALIGN_CENTER);
            documento.add(datosFactura);

            // Datos del cliente/usuario que genera el reporte
            Paragraph datosUsuario = new Paragraph();
            addEmptyLine(datosUsuario, 1);
            datosUsuario.add(new Paragraph("DATOS DEL SOLICITANTE", fontNegrita));
            if (nombreEmpleado != null && !nombreEmpleado.isEmpty() && !nombreEmpleado.startsWith("Seleccione")) {
                datosUsuario.add(new Paragraph("Generado por: " + nombreEmpleado, fontNormal));
            } else {
                datosUsuario.add(new Paragraph("Generado por: Usuario del sistema", fontNormal));
            }
            datosUsuario.add(new Paragraph("Tipo de reporte: " + tipoReporte, fontNormal));
            documento.add(datosUsuario);

            // Tabla de contenido del reporte
            addEmptyLine(new Paragraph(), 1);
            documento.add(new Paragraph("DETALLE DEL REPORTE", fontNegrita));

            PdfPTable tabla = new PdfPTable(reportesTable.getColumnCount());
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(10f);
            tabla.setSpacingAfter(10f);

            // Ajustar anchos de columnas si es necesario
            float[] columnWidths = new float[reportesTable.getColumnCount()];
            for (int i = 0; i < reportesTable.getColumnCount(); i++) {
                columnWidths[i] = 1f; // Todas las columnas con el mismo ancho por defecto
            }
            tabla.setWidths(columnWidths);

            // Encabezados de la tabla
            for (int i = 0; i < reportesTable.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(reportesTable.getColumnName(i), fontNegrita));
                cell.setBackgroundColor(new BaseColor(220, 220, 220));
                cell.setPadding(5);
                tabla.addCell(cell);
            }

            // Agregar datos a la tabla
            for (int row = 0; row < reportesTable.getRowCount(); row++) {
                for (int col = 0; col < reportesTable.getColumnCount(); col++) {
                    Object value = reportesTable.getValueAt(row, col);
                    String texto = (value != null) ? value.toString() : "";
                    tabla.addCell(new Phrase(texto, fontNormal));
                }
            }

            documento.add(tabla);

            // Si es posible, agregar resumen o estadísticas
            if (reportesTable.getRowCount() > 0) {
                Paragraph resumen = new Paragraph();
                addEmptyLine(resumen, 1);

                PdfPTable tablaTotales = new PdfPTable(2);
                tablaTotales.setWidthPercentage(40);
                tablaTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaTotales.setSpacingBefore(10f);

                tablaTotales.addCell(new Phrase("Total de registros:", fontNegrita));
                tablaTotales.addCell(new Phrase(String.valueOf(reportesTable.getRowCount()), fontNormal));

                // Agregar más estadísticas según el tipo de reporte
                if (tipoReporte.contains("Ventas")) {
                    double totalVentas = 0;
                    int totalOrdenes = 0;
                    for (int row = 0; row < reportesTable.getRowCount(); row++) {
                        String ventasStr = ((String) reportesTable.getValueAt(row, 1)).replace("$", "").replace(",", "");
                        totalVentas += Double.parseDouble(ventasStr);
                        totalOrdenes += (Integer) reportesTable.getValueAt(row, 2);
                    }

                    tablaTotales.addCell(new Phrase("Total ventas:", fontNegrita));
                    tablaTotales.addCell(new Phrase(String.format("$%.2f", totalVentas), fontNormal));

                    tablaTotales.addCell(new Phrase("Total órdenes:", fontNegrita));
                    tablaTotales.addCell(new Phrase(String.valueOf(totalOrdenes), fontNormal));
                }

                documento.add(tablaTotales);
            }

            // Información adicional
            Paragraph infoAdicional = new Paragraph();
            addEmptyLine(infoAdicional, 2);
            infoAdicional.add(new Paragraph("INFORMACIÓN ADICIONAL", fontNegrita));
            infoAdicional.add(new Paragraph("Reporte generado el: " + sdf.format(new Date()), fontNormal));
            documento.add(infoAdicional);

            // Notas y condiciones
            Paragraph notas = new Paragraph();
            addEmptyLine(notas, 2);
            notas.add(new Paragraph("NOTAS Y CONDICIONES", fontNegrita));
            notas.add(new Paragraph("- Este documento es para uso interno de la empresa.", fontPequeña));
            notas.add(new Paragraph("- La información contenida es confidencial.", fontPequeña));
            notas.add(new Paragraph("- Prohibida su reproducción sin autorización.", fontPequeña));
            documento.add(notas);

            // Pie de página
            Paragraph footer = new Paragraph();
            addEmptyLine(footer, 2);
            footer.add(new Paragraph("Sistema de Reportes - Ferretería Future Soft", fontNormal));
            footer.setAlignment(Element.ALIGN_CENTER);
            documento.add(footer);

            // Cerrar el documento
            documento.close();

            JOptionPane.showMessageDialog(null,
                    "Factura de reporte generada correctamente.\nGuardada en: " + rutaArchivo,
                    "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            // Abrir el archivo automáticamente
            try {
                File pdfFile = new File(rutaArchivo);
                if (pdfFile.exists()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(pdfFile);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "No se puede abrir automáticamente. El archivo está en: " + rutaArchivo);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al abrir el archivo: " + ex.getMessage());
            }
        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al generar la factura: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void exportarReporteActualAPDF(String tipoReporte, String nombreEmpleado) {
        generarFacturaPDF(tipoReporte, nombreEmpleado);
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}