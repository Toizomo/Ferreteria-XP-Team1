package MenuPrincipal;

import Chat.ChatServidorGUI;
import Clientes.ClientesGUI;
import Empleados.EmpleadosGUI;
import Img.img;
import Inventario.InventarioGUI;
import Orden_Compras.OrdenesCompraGUI;
import Proveedores.ProveedoresGUI;
import Reportes.ReportesGUI;
import VentasGUI.VentasGUI;

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
    private JButton reportesButton;
    private JButton ventasButton;
    private JPanel mainPanel;

    public MenuPrincipal() {
        main = new img();
        main.setLayout(new BorderLayout());

        Color botonColor = new Color(179, 133, 107);
        Color hoverColor = new Color(0, 0, 0);
        Color bordeColor = new Color(170, 130, 100);
        Font fuente = new Font("Georgia", Font.BOLD, 18);
        Dimension tamano = new Dimension(180, 50);

        clientesButton = new JButton("Clientes");
        inventarioButton = new JButton("Inventario");
        proveedoresButton = new JButton("Proveedores");
        empleadosButton = new JButton("Empleados");
        ordenesCompraButton = new JButton("Órdenes de Compra");
        chatAdministradorButton = new JButton("Chat Admin");
        reportesButton = new JButton("Reportes");
        ventasButton = new JButton("Ventas");

        JButton[] botones = {
                clientesButton, inventarioButton, proveedoresButton, empleadosButton,
                ordenesCompraButton, chatAdministradorButton, reportesButton, ventasButton
        };

        JPanel gridPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        for (JButton btn : botones) {
            personalizarBoton(btn, botonColor, hoverColor, bordeColor, fuente, tamano);
            gridPanel.add(btn);
        }

        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setOpaque(false);
        contenedor.add(gridPanel);

        main.add(contenedor, BorderLayout.CENTER);

        clientesButton.addActionListener(e -> switchTo(ClientesGUI.class));
        inventarioButton.addActionListener(e -> switchTo(InventarioGUI.class));
        proveedoresButton.addActionListener(e -> switchTo(ProveedoresGUI.class));
        empleadosButton.addActionListener(e -> switchTo(EmpleadosGUI.class));
        ordenesCompraButton.addActionListener(e -> switchTo(OrdenesCompraGUI.class));
        chatAdministradorButton.addActionListener(e -> switchTo(ChatServidorGUI.class));
        reportesButton.addActionListener(e -> switchTo(ReportesGUI.class));
        ventasButton.addActionListener(e -> switchTo(VentasGUI.class));

        JFrame frame = new JFrame("Menú Principal");
        frame.setContentPane(main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private void personalizarBoton(JButton button, Color fondo, Color textoHover, Color borde, Font fuente, Dimension size) {
        button.setFont(fuente);
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        button.setForeground(Color.BLACK);
        button.setBackground(fondo);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(borde, 2));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(textoHover);
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.BLACK);
                button.setBorder(BorderFactory.createLineBorder(borde, 2));
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

    public Container getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(main, BorderLayout.CENTER);
        }
        return mainPanel;
    }

    public void setMainPanel(Container mainPanel) {
        this.mainPanel = (JPanel) mainPanel;
    }
}
