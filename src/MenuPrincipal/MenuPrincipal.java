package MenuPrincipal;

import Clientes.ClientesGUI;
import Empleados.EmpleadosGUI;
import Inventario.InventarioGUI;
import Orden_Compras.OrdenesCompra.OrdenesCompraGUI;
import Proveedores.ProveedoresGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal {
    private JPanel main; // Panel principal
    private JButton clientesButton;
    private JButton inventarioButton;
    private JButton proveedoresButton;
    private JButton empleadosButton;
    private JButton ordenesCompraButton;
    private JTextPane FERETERIATextPane;

    public MenuPrincipal() {
        // Inicializar el panel principal
        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS)); // Usar un layout vertical
        main.setBackground(new Color(255, 198, 45)); // Establecer el color de fondo del panel principal

        // Crear un panel para el JTextPane y establecer un layout centrado
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Centrar el JTextPane
        textPanel.setBackground(new Color(239, 32, 32)); // Establecer el color de fondo del panel de texto

        // Inicializar el JTextPane
        FERETERIATextPane = new JTextPane();
        FERETERIATextPane.setText("FERETERIA"); // Texto de ejemplo
        FERETERIATextPane.setEditable(false); // Hacerlo no editable
        FERETERIATextPane.setPreferredSize(new Dimension(200, 30)); // Tamaño preferido
        FERETERIATextPane.setBackground(new Color(244, 66, 66)); // Fondo del JTextPane
        FERETERIATextPane.setForeground(Color.WHITE); // Texto en blanco

        // Añadir el JTextPane al panel de texto
        textPanel.add(FERETERIATextPane);

        // Crear un panel para los botones y establecer un layout centrado
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Centrar los botones
        buttonPanel.setBackground(new Color(255, 198, 45)); // Establecer el color de fondo del panel de botones

        // Inicializar botones (suponiendo que ya los has creado en el diseñador)
        clientesButton = new JButton("Clientes");
        inventarioButton = new JButton("Inventario");
        proveedoresButton = new JButton("Proveedores");
        empleadosButton = new JButton("Empleados");
        ordenesCompraButton = new JButton("Órdenes de Compra");

        // Personalización de los botones
        customizeButton(clientesButton, new Color(70, 130, 180)); // Naranja
        customizeButton(inventarioButton, new Color(70, 130, 180)); // Azul
        customizeButton(proveedoresButton, new Color(70, 130, 180)); // Verde
        customizeButton(empleadosButton, new Color(70, 130, 180)); // Rojo
        customizeButton(ordenesCompraButton, new Color(70, 130, 180)); // Púrpura

        // Añadir botones al panel de botones
        buttonPanel.add(clientesButton);
        buttonPanel.add(inventarioButton);
        buttonPanel.add(proveedoresButton);
        buttonPanel.add(empleadosButton);
        buttonPanel.add(ordenesCompraButton);

        // Añadir el panel de texto y el panel de botones al panel principal
        main.add(Box.createVerticalGlue()); // Espacio flexible antes del JTextPane
        main.add(textPanel);
        main.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio fijo entre el JTextPane y los botones
        main.add(buttonPanel);
        main.add(Box.createVerticalGlue()); // Espacio flexible después de los botones

        // Añadir acciones a los botones
        clientesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(clientesButton);
                jFrame.dispose();
                ClientesGUI.main(null);
            }
        });

        inventarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(inventarioButton);
                jFrame.dispose();
                InventarioGUI.main(null);
            }
        });

        proveedoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(proveedoresButton);
                jFrame.dispose();
                ProveedoresGUI.main(null);
            }
        });

        empleadosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(empleadosButton);
                jFrame.dispose();
                EmpleadosGUI.main(null);
            }
        });

        ordenesCompraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(ordenesCompraButton);
                jFrame.dispose();
                OrdenesCompraGUI.main(null);
            }
        });



        // Configurar el JFrame
        JFrame frame = new JFrame("Menú Principal");
        frame.setContentPane(main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
    }

    private void customizeButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Borde negro
    }

    public static void main(String[] args) {
        new MenuPrincipal(); // Crear una instancia de MenuPrincipal
    }
}