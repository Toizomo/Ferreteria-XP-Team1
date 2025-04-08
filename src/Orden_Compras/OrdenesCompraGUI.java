package Orden_Compras;

import MenuPrincipal.MenuPrincipal;
import Orden_Compras.OrdenesCompraDAO;
import Orden_Compras.ordenesCompra;

import javax.swing.*;
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
                    OrdenesCompraDAO .agregar(orden);

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
                    OrdenesCompraDAO .actualizar(orden);

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
}
