package Reportes;

import MenuPrincipal.MenuPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class ReportesGUI extends JPanel {
    private JPanel main;
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private JComboBox<String> comboBox1; // Filtro: Diario, Semanal, Mensual
    private JComboBox<String> comboBox2; // Cambiar estado: Pendiente, Enviado, Pagado
    private JButton volverButton;

    private ReportesDAO reportesDAO = new ReportesDAO();

    public ReportesGUI() {
        add(main);
        aplicarEstilos(); // Llamada al método para aplicar estilos

        if (comboBox1.getItemCount() == 0) {
            comboBox1.addItem("Diario");
            comboBox1.addItem("Semanal");
            comboBox1.addItem("Mensual");
        }

        if (comboBox2.getItemCount() == 0) {
            comboBox2.addItem("Pendiente");
            comboBox2.addItem("Enviado");
            comboBox2.addItem("Pagado");
        }

        cargarDatosFiltrados("Diario");

        comboBox1.addActionListener(e -> {
            String filtroSeleccionado = (String) comboBox1.getSelectedItem();
            cargarDatosFiltrados(filtroSeleccionado);
        });

        comboBox2.addActionListener(e -> {
            int filaSeleccionada = table1.getSelectedRow();
            if (filaSeleccionada != -1) {
                int idVenta = (int) table1.getValueAt(filaSeleccionada, 0);
                String nuevoEstado = (String) comboBox2.getSelectedItem();

                boolean actualizado = reportesDAO.actualizarEstadoVenta(idVenta, nuevoEstado);
                if (actualizado) {
                    JOptionPane.showMessageDialog(null, "Estado actualizado correctamente.");
                    table1.setValueAt(nuevoEstado, filaSeleccionada, 3);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al actualizar el estado.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecciona una venta para cambiar el estado.");
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

    private void aplicarEstilos() {
        Font fuenteCampos = new Font("Serif", Font.PLAIN, 15);
        Font fuenteBotones = new Font("Serif", Font.BOLD, 15);
        Color colorFondo = new Color(216, 196, 164); // beige claro
        Color colorTexto = new Color(59, 42, 27);    // marrón oscuro
        Color colorBotonFondo = colorFondo;
        Color colorBotonTexto = Color.BLACK;
        Color colorBordeBoton = Color.WHITE;
        Color fondoTabla = new Color(245, 245, 245);
        Color fondoAlternativo = new Color(230, 230, 230);
        Color colorHeader = new Color(60, 63, 65);
        Color textoHeader = Color.WHITE;

        main.setBackground(colorFondo);

        // Estilos para las tablas
        JTable[] tablas = {table1, table2, table3};
        for (JTable tabla : tablas) {
            tabla.setFont(fuenteCampos);
            tabla.setRowHeight(25);
            tabla.setFillsViewportHeight(true);

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 0; i < tabla.getColumnCount(); i++) {
                tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

            tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    c.setBackground(row % 2 == 0 ? fondoTabla : fondoAlternativo);
                    return c;
                }
            });

            JTableHeader header = tabla.getTableHeader();
            header.setBackground(colorHeader);
            header.setForeground(textoHeader);
            header.setFont(new Font("Serif", Font.BOLD, 15));
        }

        // Estilos para los combo boxes
        comboBox1.setFont(fuenteCampos);
        comboBox2.setFont(fuenteCampos);

        // Estilos para el botón volver
        volverButton.setFont(fuenteBotones);
        volverButton.setBackground(colorBotonFondo);
        volverButton.setForeground(colorBotonTexto);
        volverButton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
        volverButton.setFocusPainted(false);
    }

    public void cargarDatosFiltrados(String filtro) {
        ResultSet rsVentas = reportesDAO.obtenerVentasFiltradas(filtro);
        DefaultTableModel modeloVentas = new DefaultTableModel();
        modeloVentas.setColumnIdentifiers(new String[]{"ID Venta", "Total", "Fecha", "Estado"});

        try {
            while (rsVentas != null && rsVentas.next()) {
                modeloVentas.addRow(new Object[]{
                        rsVentas.getInt("id_venta"),
                        rsVentas.getDouble("total"),
                        rsVentas.getDate("fecha"),
                        rsVentas.getString("estado")
                });
            }
            table1.setModel(modeloVentas);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResultSet rsProductos = reportesDAO.obtenerProductosMasVendidos(filtro);
        DefaultTableModel modeloProductos = new DefaultTableModel();
        modeloProductos.setColumnIdentifiers(new String[]{"Producto", "Cantidad Vendida"});

        try {
            while (rsProductos != null && rsProductos.next()) {
                modeloProductos.addRow(new Object[]{
                        rsProductos.getString("nombre_producto"),
                        rsProductos.getInt("total_vendidos")
                });
            }
            table2.setModel(modeloProductos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResultSet rsClientes = reportesDAO.obtenerClientesTop(filtro);
        DefaultTableModel modeloClientes = new DefaultTableModel();
        modeloClientes.setColumnIdentifiers(new String[]{"Nombre Cliente", "Total Comprado"});

        try {
            while (rsClientes != null && rsClientes.next()) {
                modeloClientes.addRow(new Object[]{
                        rsClientes.getString("nombre_cliente"),
                        rsClientes.getDouble("total_compras")
                });
            }
            table3.setModel(modeloClientes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Reportes");
        frame.setContentPane(new ReportesGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}