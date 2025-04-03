package MenuPrincipal;

import javax.swing.*;
import java.awt.*;

public class JPanelBackground extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Aquí puedes dibujar un color de fondo o cualquier otro componente si lo deseas
        g.setColor(Color.LIGHT_GRAY); // Color de fondo
        g.fillRect(0, 0, getWidth(), getHeight()); // Rellenar el panel con el color
    }
}