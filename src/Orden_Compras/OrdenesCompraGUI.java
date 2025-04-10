package Orden_Compras;

import MenuPrincipal.MenuPrincipal;
import Orden_Compras.OrdenesCompraDAO;
import Orden_Compras.ordenesCompra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrdenesCompraGUI {
    private JTable table1;
    private JTextField idOrdenCompra;
    private JTextField idCliente;
    private JTextField idEmpleado;
    private JTextField idProducto;
    private JTextField total;
    private JTextField fechaCompra;
    private JButton agregarCompraButton;
    private JButton actualizarCompraButton;
    private JButton volverButton;
    private JPanel main;

    public OrdenesCompraGUI() {

        // Aplicar estilos
        aplicarEstilos();

        agregarCompraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int cliente = Integer.parseInt(idCliente.getText());
                    int empleado = Integer.parseInt(idEmpleado.getText());
                    int producto = Integer.parseInt(idProducto.getText());
                    double totalCompra = Double.parseDouble(total.getText());
                    String fecha = fechaCompra.getText();

                    ordenesCompra orden = new ordenesCompra(0, cliente, empleado, producto, totalCompra, fecha);
                    OrdenesCompraDAO.agregar(orden);

                    JOptionPane.showMessageDialog(null, "Compra agregada correctamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al agregar la compra: " + ex.getMessage());
                }
            }
        });

        actualizarCompraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(idOrdenCompra.getText());
                    int cliente = Integer.parseInt(idCliente.getText());
                    int empleado = Integer.parseInt(idEmpleado.getText());
                    int producto = Integer.parseInt(idProducto.getText());
                    double totalCompra = Double.parseDouble(total.getText());
                    String fecha = fechaCompra.getText();

                    ordenesCompra orden = new ordenesCompra(0, cliente, empleado, producto, totalCompra, fecha);
                    OrdenesCompraDAO.actualizar(orden);

                    JOptionPane.showMessageDialog(null, "Compra actualizada correctamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al actualizar la compra: " + ex.getMessage());
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ordenes de compra");
        frame.setContentPane(new OrdenesCompraGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
    }

    private void aplicarEstilos() {
        Font fuenteCampos = new Font("Serif", Font.PLAIN, 15);
        Font fuenteBotones = new Font("Serif", Font.BOLD, 15);
        Color colorFondo = new Color(216, 196, 164); // beige claro
        Color colorTexto = new Color(59, 42, 27);    // marrón oscuro
        Color colorBotonFondo = colorFondo;
        Color colorBotonTexto = Color.WHITE;
        Color colorBordeBoton = Color.WHITE;

        main.setBackground(colorFondo);

        // Estilos para campos de texto
        JTextField[] campos = { idOrdenCompra, idCliente, idEmpleado, idProducto, total, fechaCompra };
        for (JTextField campo : campos) {
            campo.setFont(fuenteCampos);
            campo.setBackground(Color.WHITE);
            campo.setForeground(colorTexto);
            campo.setBorder(BorderFactory.createLineBorder(colorTexto));
        }

        // Estilos para botones
        JButton[] botones = { agregarCompraButton, actualizarCompraButton, volverButton };
        for (JButton boton : botones) {
            boton.setFont(fuenteBotones);
            boton.setBackground(colorBotonFondo);
            boton.setForeground(colorBotonTexto);
            boton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
            boton.setFocusPainted(false);
        }

        // Estilos para la tabla (si se agrega)
        if (table1 != null) {
            table1.setFont(new Font("Serif", Font.PLAIN, 14));
            table1.setForeground(colorTexto);
            table1.setBackground(Color.WHITE);
            table1.setRowHeight(25);
            table1.getTableHeader().setFont(new Font("Serif", Font.BOLD, 15));
            table1.getTableHeader().setBackground(colorFondo);
            table1.getTableHeader().setForeground(colorTexto);
        }
    }
}
