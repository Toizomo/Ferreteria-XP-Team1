package MenuPrincipal;

import Chat.ChatServidorGUI;
import Clientes.ClientesGUI;
import Empleados.EmpleadosGUI;
import Img.img;
import Inventario.InventarioGUI;
import Orden_Compras.OrdenesCompraGUI;
import Proveedores.ProveedoresGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPrincipal {
    private JPanel main;
    private JButton clientesButton;
    private JButton inventarioButton;
    private JButton proveedoresButton;
    private JButton empleadosButton;
    private JButton ordenesCompraButton;
    private JButton chatAdministradorButton;
    private JLabel ferreteriaLabel;

    public MenuPrincipal() {
        main = new img(); // clase img que tiene el fondo
        main.setLayout(new BorderLayout());

        // Panel superior con los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false); // Fondo transparente

        // Crear botones
        clientesButton = new JButton("Clientes");
        inventarioButton = new JButton("Inventario");
        proveedoresButton = new JButton("Proveedores");
        empleadosButton = new JButton("Empleados");
        ordenesCompraButton = new JButton("Órdenes de Compra");
        chatAdministradorButton = new JButton("Chat Administrador");

        // Estilos
        Dimension buttonSize = new Dimension(180, 50);
        Font buttonFont = new Font("Times New Roman", Font.BOLD, 18);
        Color buttonColor = new Color(70, 130, 180); // Azul oscuro bonito

        // Personalizar botones
        customizeButton(clientesButton, buttonColor, buttonFont, buttonSize);
        customizeButton(inventarioButton, buttonColor, buttonFont, buttonSize);
        customizeButton(proveedoresButton, buttonColor, buttonFont, buttonSize);
        customizeButton(empleadosButton, buttonColor, buttonFont, buttonSize);
        customizeButton(ordenesCompraButton, buttonColor, buttonFont, buttonSize);
        customizeButton(chatAdministradorButton, buttonColor, buttonFont, buttonSize);

        // Añadir botones al panel
        buttonPanel.add(clientesButton);
        buttonPanel.add(inventarioButton);
        buttonPanel.add(proveedoresButton);
        buttonPanel.add(empleadosButton);
        buttonPanel.add(ordenesCompraButton);
        buttonPanel.add(chatAdministradorButton);

        main.add(buttonPanel, BorderLayout.NORTH); // Los botones van arriba

        // Acciones de los botones
        clientesButton.addActionListener(e -> switchTo(ClientesGUI.class));
        inventarioButton.addActionListener(e -> switchTo(InventarioGUI.class));
        proveedoresButton.addActionListener(e -> switchTo(ProveedoresGUI.class));
        empleadosButton.addActionListener(e -> switchTo(EmpleadosGUI.class));
        ordenesCompraButton.addActionListener(e -> switchTo(OrdenesCompraGUI.class));
        chatAdministradorButton.addActionListener( e-> switchTo(ChatServidorGUI.class));

        // Frame principal
        JFrame frame = new JFrame("Menú Principal");
        frame.setContentPane(main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private void customizeButton(JButton button, Color backgroundColor, Font font, Dimension size) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // Quita el fondo blanco
        button.setOpaque(false);            // Hace el fondo transparente
        button.setForeground(Color.WHITE);  // Texto blanco
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // Borde blanco

        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setForeground(new Color(255, 255, 255)); // Celeste claro
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 2));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
        });
    }

    private void switchTo(Class<?> targetClass) {
        JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(main);
        jFrame.dispose();
        try {
            targetClass.getMethod("main", String[].class).invoke(null, (Object) null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MenuPrincipal();
    }
}
