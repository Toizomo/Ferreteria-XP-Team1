package Orden_Compras;

import MenuPrincipal.MenuPrincipal;
import com.itextpdf.text.Font;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;


public class OrdenesCompraGUI {
    private JTable table1;
    private JTextField idOrdenCompra;
    private JTextField cantidadTextField;
    private JTextField total;
    private JTextField fechaCompra;
    private JPanel mainPanel;
    private JTextField ClienteTextField;
    private JTextField EmpleadoTextField;
    private JTextField ProductoTextField;
    private JTextField estadoTextField;
    private JButton actualizarEstadoButton;
    private JButton generarFacturaButton;
    private JPanel buttonPanel;
    private JPanel filtroPanel;
    private JTextField filtroTextField;
    private JButton buscarButton;
    private JButton limpiarFiltrosButton;
    private JButton volverButton;
    private static final double IVA_RATE = 0.19;

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private class OrdenDetalle {
        private int idOrden;
        private String cliente;
        private String direccion;
        private String telefono;
        private String empleado;
        private String estado;
        private String fecha;


        public java.util.List<ProductoOrden> getProductos()
        { return productos; }

        public int getIdOrden() { return idOrden; }
        public void setIdOrden(int idOrden) { this.idOrden = idOrden; }

        public String getCliente() { return cliente; }
        public void setCliente(String cliente) { this.cliente = cliente; }

        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }

        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }

        public String getEmpleado() { return empleado; }
        public void setEmpleado(String empleado) { this.empleado = empleado; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }

        private java.util.List productos = new ArrayList<>();    }

    private class ProductoOrden {
        private String nombre;
        private double precioUnitario;
        private int cantidad;

        public ProductoOrden(String nombre, double precioUnitario, int cantidad) {
            this.nombre = nombre;
            this.precioUnitario = precioUnitario;
            this.cantidad = cantidad;
        }

        public String getNombre() { return nombre; }
        public double getPrecioUnitario() { return precioUnitario; }
        public int getCantidad() { return cantidad; }

        // Calcular el IVA para este producto
        public double getIva() {
            return precioUnitario * cantidad * IVA_RATE;
        }

        // Calcular el total para este producto (precio * cantidad + IVA)
        public double getTotal() {
            return precioUnitario * cantidad + getIva();
        }
    }

    private OrdenDetalle obtenerDetalleOrden(int idOrden) {
        OrdenDetalle detalle = new OrdenDetalle();

        try {
            // Primera consulta: obtener datos de la orden y cliente
            String sqlOrden = "SELECT oc.id_orden_compra, c.nombre as cliente, c.direccion, c.telefono, " +
                    "e.nombre as empleado, oc.estado_orden, oc.fecha_compra " +
                    "FROM ordenes_compra oc " +
                    "JOIN clientes c ON oc.id_cliente = c.id_cliente " +
                    "JOIN empleados e ON oc.id_empleado = e.id_empleado " +
                    "WHERE oc.id_orden_compra = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrden)) {
                pstmt.setInt(1, idOrden);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    detalle.setIdOrden(rs.getInt("id_orden_compra"));
                    detalle.setCliente(rs.getString("cliente"));
                    detalle.setDireccion(rs.getString("direccion"));
                    detalle.setTelefono(rs.getString("telefono"));
                    detalle.setEmpleado(rs.getString("empleado"));
                    detalle.setEstado(rs.getString("estado_orden"));
                    detalle.setFecha(rs.getString("fecha_compra"));

                    // Segunda consulta: obtener todos los productos relacionados con la orden
                    String sqlProductos = "SELECT p.nombre_producto, p.precio_producto, rv.cantidad " +
                            "FROM ordenes_compra oc " +
                            "JOIN inventario_productos p ON oc.id_producto = p.id_producto " +
                            "LEFT JOIN registro_ventas rv ON oc.id_orden_compra = rv.id_orden_compra " +
                            "WHERE oc.id_orden_compra = ?";

                    try (PreparedStatement pstmtProductos = conn.prepareStatement(sqlProductos)) {
                        pstmtProductos.setInt(1, idOrden);
                        ResultSet rsProductos = pstmtProductos.executeQuery();

                        // Verificar si hay al menos un producto
                        boolean hayProductos = false;

                        while (rsProductos.next()) {
                            hayProductos = true;
                            String nombreProducto = rsProductos.getString("nombre_producto");
                            double precioUnitario = rsProductos.getDouble("precio_producto");
                            int cantidad = rsProductos.getInt("cantidad");
                            if (cantidad == 0) cantidad = 1; // Si no hay registro en ventas

                            detalle.getProductos().add(new ProductoOrden(nombreProducto, precioUnitario, cantidad));
                        }

                        if (!hayProductos) {
                            JOptionPane.showMessageDialog(null, "No se encontraron productos para esta orden");
                            return null;
                        }

                        return detalle;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener detalles de la orden: " + e.getMessage());
        }

        return null;
    }

    private Map<String, Integer> clientesMap = new HashMap<>();
    private Map<String, Integer> empleadosMap = new HashMap<>();
    private Map<String, Integer> productosMap = new HashMap<>();
    private Map<Integer, Double> preciosProductos = new HashMap<>();

    private Connection conn;
    private DefaultTableModel model;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public OrdenesCompraGUI() {
        // Configurar la tabla
        configurarTabla();

        // Configurar botones de acción
        configurarBotones();

        // Conexión a la base de datos
        establecerConexion();

        // Cargar datos iniciales
        cargarClientes();
        cargarEmpleados();
        cargarProductos();
        cargarOrdenes();

        // Configurar fecha actual como no editable
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fechaCompra.setText(dateFormat.format(new Date()));
        fechaCompra.setEditable(false);

        // Listener para cargar datos en los campos cuando se selecciona una fila
        configurarListenerTabla();

        // Listener para el campo de ID de orden
        configurarListenerIdOrden();

        //limpiar campos
        limpiarCampos();

        // Aplicar los estilos
        aplicarEstilos();

    }

    private void configurarListenerTabla() {
        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table1.getSelectedRow() != -1) {
                cargarDatosEnCampos(table1.getSelectedRow());
            }
        });
    }

    private void cargarEmpleados() {
        try {
            String sql = "SELECT id_empleado, nombre FROM empleados";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    int idEmpleado = rs.getInt("id_empleado");
                    String nombreEmpleado = rs.getString("nombre");
                    empleadosMap.put(nombreEmpleado, idEmpleado);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar empleados: " + e.getMessage());
        }
    }

    private void establecerConexion() {
        try {
            String url = "jdbc:mysql://localhost:3306/ferreteria";
            String user = "root";
            String password = "";

            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private void cargarClientes() {
        try {
            String sql = "SELECT id_cliente, nombre FROM clientes";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    int idCliente = rs.getInt("id_cliente");
                    String nombreCliente = rs.getString("nombre");
                    clientesMap.put(nombreCliente, idCliente);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar clientes: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        model = new DefaultTableModel(new String[]{
                "ID Orden", "Cliente", "Empleado", "Producto", "Cantidad", "Total", "Estado", "Fecha"
        }, 0);
        table1.setModel(model);
    }

    private void cargarProductos() {
        try {
            String sql = "SELECT id_producto, nombre_producto, precio_producto FROM inventario_productos";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    int idProducto = rs.getInt("id_producto");
                    String nombreProducto = rs.getString("nombre_producto");
                    double precioProducto = rs.getDouble("precio_producto");

                    productosMap.put(nombreProducto, idProducto);
                    preciosProductos.put(idProducto, precioProducto);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void configurarListenerIdOrden() {
        idOrdenCompra.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarOrdenPorId();
                }
            }
        });
    }

    private void buscarOrdenPorId() {
        if (idOrdenCompra.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un ID de orden válido");
            return;
        }

        try {
            int idOrden = Integer.parseInt(idOrdenCompra.getText().trim());
            model.setRowCount(0); // Limpiar la tabla

            String sql = "SELECT oc.id_orden_compra, c.nombre as cliente, e.nombre as empleado, " +
                    "p.nombre_producto, rv.cantidad, oc.total, oc.estado_orden, oc.fecha_compra " +
                    "FROM ordenes_compra oc " +
                    "JOIN clientes c ON oc.id_cliente = c.id_cliente " +
                    "JOIN empleados e ON oc.id_empleado = e.id_empleado " +
                    "JOIN inventario_productos p ON oc.id_producto = p.id_producto " +
                    "LEFT JOIN registro_ventas rv ON oc.id_orden_compra = rv.id_orden_compra " +
                    "WHERE oc.id_orden_compra = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idOrden);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String cliente = rs.getString("cliente");
                    String empleado = rs.getString("empleado");
                    String producto = rs.getString("nombre_producto");
                    int cantidad = rs.getInt("cantidad");
                    if (cantidad == 0) cantidad = 1; // Si no hay registro en ventas
                    double totalOrden = rs.getDouble("total");
                    String estado = rs.getString("estado_orden");
                    String fecha = rs.getString("fecha_compra");

                    model.addRow(new Object[]{idOrden, cliente, empleado, producto, cantidad, totalOrden, estado, fecha});
                    table1.setRowSelectionInterval(0, 0); // Seleccionar la primera fila
                    cargarDatosEnCampos(0);
                } else {
                    limpiarCampos();
                    JOptionPane.showMessageDialog(null, "No se encontró ninguna orden con el ID: " + idOrden);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido para el ID de la orden");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar la orden: " + e.getMessage());
        }
    }

    private void configurarBotones() {
        actualizarEstadoButton.addActionListener(e -> mostrarDialogoActualizarEstado());

        generarFacturaButton.addActionListener(e -> {
            if (idOrdenCompra.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione una orden para generar factura");
                return;
            }
            generarFacturaPDF();
        });

        buscarButton.addActionListener(e -> aplicarFiltro());

        limpiarFiltrosButton.addActionListener(e -> {
            filtroTextField.setText("");
            cargarOrdenes();
            limpiarCampos();
        });

        // Agregar este nuevo listener para detectar cuando se presiona Enter
        filtroTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    aplicarFiltro();
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

    private void cargarOrdenes() {
        model.setRowCount(0); // Limpiar la tabla

        try {
            String sql = "SELECT oc.id_orden_compra, c.nombre as cliente, e.nombre as empleado, " +
                    "p.nombre_producto, rv.cantidad, oc.total, oc.estado_orden, oc.fecha_compra " +
                    "FROM ordenes_compra oc " +
                    "JOIN clientes c ON oc.id_cliente = c.id_cliente " +
                    "JOIN empleados e ON oc.id_empleado = e.id_empleado " +
                    "JOIN inventario_productos p ON oc.id_producto = p.id_producto " +
                    "LEFT JOIN registro_ventas rv ON oc.id_orden_compra = rv.id_orden_compra " +
                    "ORDER BY oc.id_orden_compra DESC";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    int idOrden = rs.getInt("id_orden_compra");
                    String cliente = rs.getString("cliente");
                    String empleado = rs.getString("empleado");
                    String producto = rs.getString("nombre_producto");
                    int cantidad = rs.getInt("cantidad");
                    if (cantidad == 0) cantidad = 1; // Si no hay registro en ventas
                    double totalOrden = rs.getDouble("total");
                    String estado = rs.getString("estado_orden");
                    String fecha = rs.getString("fecha_compra");

                    model.addRow(new Object[]{idOrden, cliente, empleado, producto, cantidad, totalOrden, estado, fecha});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar órdenes: " + e.getMessage());
        }
    }

    private void aplicarFiltro() {
        String textoBusqueda = filtroTextField.getText().trim();

        if (textoBusqueda.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un texto para filtrar");
            return;
        }

        try {
            int idOrden = Integer.parseInt(textoBusqueda);
            // Si se puede convertir a entero, buscamos por ID
            idOrdenCompra.setText(textoBusqueda);
            buscarOrdenPorId();
        } catch (NumberFormatException e) {
            // Si no es un número, mostrar mensaje
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido para el ID de la orden.",
                    "Formato incorrecto", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarDatosEnCampos(int row) {
        idOrdenCompra.setText(table1.getValueAt(row, 0).toString());
        ClienteTextField.setText(table1.getValueAt(row, 1).toString());
        EmpleadoTextField.setText(table1.getValueAt(row, 2).toString());
        ProductoTextField.setText(table1.getValueAt(row, 3).toString());
        cantidadTextField.setText(table1.getValueAt(row, 4).toString());
        total.setText(table1.getValueAt(row, 5).toString());
        estadoTextField.setText(table1.getValueAt(row, 6).toString());
        fechaCompra.setText(table1.getValueAt(row, 7).toString());
    }

    private void limpiarCampos() {
        idOrdenCompra.setText("");
        ClienteTextField.setText("");
        EmpleadoTextField.setText("");
        ProductoTextField.setText("");
        cantidadTextField.setText("");
        total.setText("");
        estadoTextField.setText("");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fechaCompra.setText(dateFormat.format(new Date()));
        table1.clearSelection();
    }

    private void actualizarEstadoOrden(int idOrden, String nuevoEstado) {
        try {
            String sql = "UPDATE ordenes_compra SET estado_orden = ? WHERE id_orden_compra = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nuevoEstado);
                pstmt.setInt(2, idOrden);

                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    // La actualización fue exitosa
                    cargarOrdenes(); // Refrescar la tabla
                    // Actualizar el campo de estado en la interfaz
                    estadoTextField.setText(nuevoEstado);
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo actualizar el estado de la orden.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar estado: " + e.getMessage());
        }
    }

    private void mostrarDialogoActualizarEstado() {
        // Verifica que haya una orden seleccionada
        if (idOrdenCompra.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una orden para actualizar su estado");
            return;
        }

        // Crear el diálogo modal
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mainPanel), "Actualizar Estado de Orden", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(mainPanel);

        // Panel de contenido
        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campo de ID de orden
        contentPanel.add(new JLabel("Orden ID:"));
        JTextField idOrdenField = new JTextField(idOrdenCompra.getText());
        idOrdenField.setEditable(false);
        contentPanel.add(idOrdenField);

        // Campo de estado actual
        contentPanel.add(new JLabel("Estado Actual:"));
        JTextField estadoActualField = new JTextField(estadoTextField.getText());
        estadoActualField.setEditable(false);
        contentPanel.add(estadoActualField);

        // ComboBox para seleccionar el nuevo estado
        contentPanel.add(new JLabel("Nuevo Estado:"));
        JComboBox<String> nuevoEstadoComboBox = new JComboBox<>();
        nuevoEstadoComboBox.addItem("pendiente");
        nuevoEstadoComboBox.addItem("pagada");
        nuevoEstadoComboBox.addItem("enviada");

        // Seleccionar el estado actual por defecto
        nuevoEstadoComboBox.setSelectedItem(estadoTextField.getText());
        contentPanel.add(nuevoEstadoComboBox);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton guardarButton = new JButton("Guardar");
        JButton cancelarButton = new JButton("Cancelar");

        // Listener para el botón Guardar
        guardarButton.addActionListener(e -> {
            String nuevoEstado = nuevoEstadoComboBox.getSelectedItem().toString();
            int idOrden = Integer.parseInt(idOrdenField.getText());

            // Actualizar el estado en la base de datos
            actualizarEstadoOrden(idOrden, nuevoEstado);

            // Cerrar el diálogo
            dialog.dispose();
        });

        // Listener para el botón Cancelar
        cancelarButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(guardarButton);
        buttonPanel.add(cancelarButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Mostrar el diálogo
        dialog.setVisible(true);
    }

    private void generarFacturaPDF() {
        try {
            int idOrden = Integer.parseInt(idOrdenCompra.getText().trim());
            OrdenDetalle ordenDetalle = obtenerDetalleOrden(idOrden);

            if (ordenDetalle == null) {
                JOptionPane.showMessageDialog(null, "No se encontraron los detalles de la orden");
                return;
            }

            // Verificar si el estado permite generar factura
            if ("pendiente".equals(ordenDetalle.getEstado())) {
                int respuesta = JOptionPane.showConfirmDialog(null,
                        "La orden está en estado Pendiente. ¿Desea generar la factura de todos modos?",
                        "Confirmación", JOptionPane.YES_NO_OPTION);
                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Configurar el documento
            Document documento = new Document(PageSize.A4);
            String rutaDescargas = System.getProperty("user.home") + File.separator + "Downloads";
            String nombreArchivo = "Factura_Compra" + idOrden + ".pdf";
            String rutaArchivo = rutaDescargas + File.separator + nombreArchivo;

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
            titulo.add(new Paragraph("FERRETERÍA El Santuario Del Fierro", fontTitulo));
            titulo.add(new Paragraph("NIT: 900.123.456-7", fontNormal));
            titulo.add(new Paragraph("Sede Sagrado", fontNormal));
            titulo.add(new Paragraph("Teléfono: (57) 123-4567", fontNormal));
            titulo.add(new Paragraph("Email: contacto@ferreteriaDelFierro.com", fontNormal));
            titulo.setAlignment(Element.ALIGN_RIGHT);
            documento.add(titulo);

            // Datos de la factura
            Paragraph datosFactura = new Paragraph();
            addEmptyLine(datosFactura, 2);
            datosFactura.add(new Paragraph("FACTURA DE VENTA", fontSubtitulo));
            datosFactura.add(new Paragraph("No. " + idOrden, fontSubtitulo));
            datosFactura.add(new Paragraph("Fecha: " + ordenDetalle.getFecha(), fontNormal));
            datosFactura.setAlignment(Element.ALIGN_CENTER);
            documento.add(datosFactura);

            // Datos del cliente
            Paragraph datosCliente = new Paragraph();
            addEmptyLine(datosCliente, 1);
            datosCliente.add(new Paragraph("DATOS DEL CLIENTE", fontNegrita));
            datosCliente.add(new Paragraph("Cliente: " + ordenDetalle.getCliente(), fontNormal));
            datosCliente.add(new Paragraph("Dirección: " + ordenDetalle.getDireccion(), fontNormal));
            datosCliente.add(new Paragraph("Teléfono: " + ordenDetalle.getTelefono(), fontNormal));
            documento.add(datosCliente);

            // Tabla de productos
            addEmptyLine(new Paragraph(), 1);
            documento.add(new Paragraph("DETALLE DE PRODUCTOS", fontNegrita));

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(10f);
            tabla.setSpacingAfter(10f);

            float[] columnWidths = {1.5f, 1f, 1f, 1f, 1f};
            tabla.setWidths(columnWidths);

            // Encabezados de la tabla
            PdfPCell cell = new PdfPCell(new Phrase("Descripción", fontNegrita));
            cell.setBackgroundColor(new BaseColor(220, 220, 220));
            cell.setPadding(5);
            tabla.addCell(cell);

            cell = new PdfPCell(new Phrase("Cantidad", fontNegrita));
            cell.setBackgroundColor(new BaseColor(220, 220, 220));
            cell.setPadding(5);
            tabla.addCell(cell);

            cell = new PdfPCell(new Phrase("Precio Unit.", fontNegrita));
            cell.setBackgroundColor(new BaseColor(220, 220, 220));
            cell.setPadding(5);
            tabla.addCell(cell);

            cell = new PdfPCell(new Phrase("IVA (19%)", fontNegrita));
            cell.setBackgroundColor(new BaseColor(220, 220, 220));
            cell.setPadding(5);
            tabla.addCell(cell);

            cell = new PdfPCell(new Phrase("Total", fontNegrita));
            cell.setBackgroundColor(new BaseColor(220, 220, 220));
            cell.setPadding(5);
            tabla.addCell(cell);

            // Agregar todos los productos a la tabla
            double subtotal = 0;
            double ivaTotal = 0;
            double granTotal = 0;

            for (ProductoOrden producto : ordenDetalle.getProductos()) {
                // Datos del producto
                tabla.addCell(new Phrase(producto.getNombre(), fontNormal));
                tabla.addCell(new Phrase(String.valueOf(producto.getCantidad()), fontNormal));
                tabla.addCell(new Phrase(String.format("%.2f", producto.getPrecioUnitario()), fontNormal));

                // Cálculo del IVA por producto
                double ivaProducto = producto.getIva();
                tabla.addCell(new Phrase(String.format("%.2f", ivaProducto), fontNormal));

                // Total por producto
                double totalProducto = producto.getTotal();
                tabla.addCell(new Phrase(String.format("%.2f", totalProducto), fontNormal));

                // Actualizar totales
                subtotal += producto.getPrecioUnitario() * producto.getCantidad();
                ivaTotal += ivaProducto;
                granTotal += totalProducto;
            }

            documento.add(tabla);

            // Resumen de totales
            Paragraph resumen = new Paragraph();
            addEmptyLine(resumen, 1);

            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidthPercentage(40);
            tablaTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.setSpacingBefore(10f);

            tablaTotales.addCell(new Phrase("Subtotal:", fontNegrita));
            tablaTotales.addCell(new Phrase(String.format("$%.2f", subtotal), fontNormal));

            tablaTotales.addCell(new Phrase("IVA (19%):", fontNegrita));
            tablaTotales.addCell(new Phrase(String.format("$%.2f", ivaTotal), fontNormal));

            tablaTotales.addCell(new Phrase("TOTAL:", fontNegrita));
            cell = new PdfPCell(new Phrase(String.format("$%.2f", granTotal), fontNegrita));
            cell.setBackgroundColor(new BaseColor(220, 220, 220));
            tablaTotales.addCell(cell);

            documento.add(tablaTotales);

            // Información adicional
            Paragraph infoAdicional = new Paragraph();
            addEmptyLine(infoAdicional, 2);
            infoAdicional.add(new Paragraph("INFORMACIÓN ADICIONAL", fontNegrita));
            infoAdicional.add(new Paragraph("Estado de la orden: " + ordenDetalle.getEstado(), fontNormal));
            infoAdicional.add(new Paragraph("Atendido por: " + ordenDetalle.getEmpleado(), fontNormal));
            documento.add(infoAdicional);

            // Notas y condiciones
            Paragraph notas = new Paragraph();
            addEmptyLine(notas, 2);
            notas.add(new Paragraph("NOTAS Y CONDICIONES", fontNegrita));
            notas.add(new Paragraph("- Esta factura es un título valor según la ley.", fontPequeña));
            notas.add(new Paragraph("- La garantía de los productos es de 30 días.", fontPequeña));
            notas.add(new Paragraph("- No se aceptan devoluciones después de 15 días.", fontPequeña));
            documento.add(notas);

            // Pie de página
            Paragraph footer = new Paragraph();
            addEmptyLine(footer, 2);
            footer.add(new Paragraph("¡Gracias por su compra!", fontNormal));
            footer.setAlignment(Element.ALIGN_CENTER);
            documento.add(footer);

            // Cerrar el documento
            documento.close();

            JOptionPane.showMessageDialog(null, "Factura generada correctamente.\nGuardada en: " + rutaArchivo);

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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar la factura: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void aplicarEstilos() {
        java.awt.Font fuenteCampos = new java.awt.Font("Serif", java.awt.Font.PLAIN, 15);
        java.awt.Font fuenteBotones = new java.awt.Font("Serif", java.awt.Font.BOLD, 15);
        Color colorFondo = new Color(216, 196, 164); // beige claro
        Color colorTexto = new Color(59, 42, 27);    // marrón oscuro
        Color colorBotonFondo = colorFondo;
        Color colorBotonTexto = Color.BLACK;
        Color colorBordeBoton = Color.WHITE;

        mainPanel.setBackground(colorFondo);

        JButton[] botones = { buscarButton, limpiarFiltrosButton, actualizarEstadoButton, generarFacturaButton };
        for (JButton boton : botones) {
            boton.setFont(fuenteBotones);
            boton.setBackground(colorBotonFondo);
            boton.setForeground(colorBotonTexto);
            boton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
            boton.setFocusPainted(false);
        }

        table1.setFont(new java.awt.Font("Serif", java.awt.Font.PLAIN, 14));
        table1.setForeground(colorTexto);
        table1.setBackground(Color.WHITE);
        table1.setRowHeight(25);
        table1.getTableHeader().setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 15));
        table1.getTableHeader().setBackground(colorFondo);
        table1.getTableHeader().setForeground(colorTexto);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Establecer el look and feel del sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Gestión de Órdenes de Compra");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new OrdenesCompraGUI().getMainPanel());
            frame.setSize(1024, 768);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

