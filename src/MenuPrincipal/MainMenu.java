package MenuPrincipal;

import Clientes.ClientesGUI;
import Empleados.EmpleadosGUI;
//import Inventario.InventarioGUI;
import Proveedores.ProveedoresGUI;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {
    private JPanel main;
    private JButton clientesMenu;
    private JButton empleadosMenu;
    //    private JButton inventariosMenu;
    private JButton ordenesCompraMenu;
    private JButton proveedoresMenu;

    public MainMenu() {
        // Inicializar el panel principal
        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS)); // Usar un layout vertical

        // Crear botones y añadirlos al panel
        clientesMenu = new JButton("Clientes");
        empleadosMenu = new JButton("Empleados");
        // inventariosMenu = new JButton("Inventarios");
        proveedoresMenu = new JButton("Proveedores");

        main.add(clientesMenu);
        main.add(empleadosMenu);
        // main.add(inventariosMenu);
        main.add(proveedoresMenu);

        // Añadir acciones a los botones
        clientesMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(clientesMenu);
                jFrame.dispose();
                ClientesGUI.main(null);
            }
        });

//        inventariosMenu.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(inventariosMenu);
//                jFrame.dispose();
//                InventarioGUI.main(null);
//            }
//        });

        empleadosMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(empleadosMenu);
                jFrame.dispose();
                EmpleadosGUI.main(null);
            }
        });

        proveedoresMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(proveedoresMenu);
                jFrame.dispose();
                ProveedoresGUI.main(null);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Menú principal");
        frame.setContentPane(new MainMenu().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
    }
}