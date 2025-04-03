package MenuPrincipal;

import javax.swing.*;
import java.awt.*;

public class JPanelBackground extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Aquí puedes dibujar un color de fondo o cualquier otro componente si lo deseas
        g.setColor(Color.MAGENTA); // Color de fondo
        g.fillRect(0, 0, getWidth(), getHeight()); // Rellenar el panel con el color

        // Crear un panel adicional para el título
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Cambia a BoxLayout

        JLabel titleLabel = new JLabel("FERETERIA", SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        // Espaciadores
        mainPanel.add(Box.createHorizontalGlue()); // Espaciador
        mainPanel.add(mainPanel);
        mainPanel.add(Box.createHorizontalGlue()); // Espaciador

        // Añadir mainPanel a tu JFrame
        this.add(mainPanel); // Asegúrate de añadir el mainPanel al JPanelBackground
    }
}